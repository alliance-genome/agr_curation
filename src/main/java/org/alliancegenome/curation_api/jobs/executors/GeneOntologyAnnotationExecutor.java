package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.GeneOntologyAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.GeneOntologyAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.services.GeneOntologyAnnotationService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.map.HashedMap;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GeneOntologyAnnotationExecutor extends LoadFileExecutor {

	@Inject
	GeneOntologyAnnotationService geneOntologyAnnotationService;
	@Inject
	OrganizationService organizationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws IOException {

		BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());
		
		CsvSchema csvSchema = CsvSchemaBuilder.gafSchema();
		CsvMapper csvMapper = new CsvMapper();
		MappingIterator<GeneOntologyAnnotationDTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS).readerFor(GeneOntologyAnnotationDTO.class).with(csvSchema).readValues(new GZIPInputStream(new FileInputStream(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath())));
		List<GeneOntologyAnnotationDTO> gafData = it.readAll();

		List<String> gafHeaderData = new ArrayList<>();
		for (GeneOntologyAnnotationDTO gafLine : gafData) {
			if (gafLine.getDb().startsWith("!")) {
				gafHeaderData.add(gafLine.getDb());
			} else {
				break;
			}
		}
		gafData.subList(0, gafHeaderData.size()).clear();

		String name = bulkLoadFileHistory.getBulkLoad().getName();
				
		List<Long> gafIdsBefore = geneOntologyAnnotationService.getAllGafIdsPerProvider(dataProvider.name());
		List<Long> gafIdsLoaded = new ArrayList<>();
		
		Map<String, Set<String>> annotationMap = new HashedMap<>();

		
		for(GeneOntologyAnnotationDTO annotation: gafData) {
			if(annotation.getDb().equals(dataProvider.resourceDescriptor)) {
				String curie = annotation.getDbObjectId();
				String prefix = dataProvider.curiePrefix;
				if(dataProvider == BackendBulkDataProvider.HUMAN || dataProvider == BackendBulkDataProvider.MGI) {
					prefix = "";
				}
				annotation.setDbObjectId(prefix + annotation.getDbObjectId());

				if(!annotationMap.containsKey(annotation.getDbObjectId())) {
					annotationMap.put(annotation.getDbObjectId(), new HashSet<>());
				}
				
				Set<String> goList = annotationMap.get(annotation.getDbObjectId());
				goList.add(annotation.getGoId());
				
			} else {
				//System.out.println("DB not found: " + annotation.getDb() + " " + dataProvider.resourceDescriptor);
			}
		}
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper();
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess(name, annotationMap.size());
		bulkLoadFileHistory.setTotalCount(annotationMap.size());

		for(Entry<String, Set<String>> annotationMapEntry: annotationMap.entrySet()) {

			ObjectListResponse<GeneOntologyAnnotation> gafInsert = geneOntologyAnnotationService.insert(annotationMapEntry.getKey(), annotationMapEntry.getValue());
			for(GeneOntologyAnnotation annotation: gafInsert.getEntities()) {
				gafIdsLoaded.add(annotation.getId());
			}
			
			if(gafInsert.getErrorMessages() != null && gafInsert.getErrorMessages().size() > 0) {
				//System.out.println(gafInsert.getErrorMessages());
				bulkLoadFileHistory.incrementFailed();
				List<String> errorList = gafInsert.getErrorMessages().entrySet().stream().map(t -> t.getValue()).toList();
				addException(bulkLoadFileHistory, new ObjectUpdateExceptionData(annotationMapEntry, errorList, null));
			} else {
				bulkLoadFileHistory.incrementCompleted();
			}

			ph.progressProcess();
			
			if (Thread.currentThread().isInterrupted()) {
				bulkLoadFileHistory.setErrorMessage("Thread isInterrupted");
				throw new RuntimeException("Thread isInterrupted");
			}
		}
		ph.finishProcess();

		
		runCleanup(geneOntologyAnnotationService, bulkLoadFileHistory, dataProvider.name(), gafIdsBefore, gafIdsLoaded, "GAF Load");
		updateHistory(bulkLoadFileHistory);
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
