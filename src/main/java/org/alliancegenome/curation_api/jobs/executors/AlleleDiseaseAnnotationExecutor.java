package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataType;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.AlleleDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleDiseaseAnnotationExecutor extends LoadFileExecutor {

	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AlleleDiseaseAnnotationService alleleDiseaseService;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;

	public void runLoad(BulkLoadFile bulkLoadFile) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getSpeciesName());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			
			if(!checkSchemaVersion(bulkLoadFile, AlleleDiseaseAnnotationDTO.class)) return;
			
			List<AlleleDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseAlleleIngestSet();
			if (annotations == null) annotations = new ArrayList<>();
			
			String dataType = manual.getDataType().name();
			String dataProvider = manual.getDataType().getDataProviderAbbreviation();

			List<Long> annotationIdsLoaded = new ArrayList<>();
			List<Long> annotationIdsBefore = new ArrayList<>();
			annotationIdsBefore.addAll(alleleDiseaseAnnotationDAO.findAllAnnotationIdsByDataProvider(dataProvider));
			annotationIdsBefore.removeIf(Objects::isNull);

			bulkLoadFile.setRecordCount(annotations.size() + bulkLoadFile.getRecordCount());
			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
			
			runLoad(history, dataType, annotations, annotationIdsLoaded);
			
			runCleanup(diseaseAnnotationService, history, dataType, annotationIdsBefore, annotationIdsLoaded);

			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataType, List<AlleleDiseaseAnnotationDTO> annotations) {
		String dataProvider = BackendBulkDataType.getDataProviderAbbreviationFromDataType(dataType);
		
		List<Long> annotationIdsLoaded = new ArrayList<>();
		List<Long> annotationIdsBefore = new ArrayList<>();
		annotationIdsBefore.addAll(alleleDiseaseAnnotationDAO.findAllAnnotationIdsByDataProvider(dataProvider));
		annotationIdsBefore.removeIf(Objects::isNull);
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
		runLoad(history, dataType, annotations, annotationIdsLoaded);
		runCleanup(diseaseAnnotationService, history, dataProvider, annotationIdsBefore, annotationIdsLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}
	
	public void runLoad(BulkLoadFileHistory history, String dataType, List<AlleleDiseaseAnnotationDTO> annotations, List<Long> curiesAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("Allele Disease Annotation Update " + dataType, annotations.size());
		annotations.forEach(annotationDTO -> {
			try {
				AlleleDiseaseAnnotation annotation = alleleDiseaseService.upsert(annotationDTO);
				history.incrementCompleted();
				if(curiesAdded != null) {
					curiesAdded.add(annotation.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(annotationDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

	}
}
