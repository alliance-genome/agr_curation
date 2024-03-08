package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.util.CsvSchemaBuilder;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneMolecularInteractionExecutor extends LoadFileExecutor {

	public void runLoad(BulkLoadFile bulkLoadFile) {
		try {
			
			BulkFMSLoad fmsLoad = (BulkFMSLoad) bulkLoadFile.getBulkLoad();
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(fmsLoad.getFmsDataSubType());
			
			CsvSchema psiMiTabSchema = CsvSchemaBuilder.psiMiTabSchema();
			MappingIterator<PsiMiTabDTO> it = csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS)
					.readerFor(PsiMiTabDTO.class).with(psiMiTabSchema)
					.readValues(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));
			List<PsiMiTabDTO> interactionData = it.readAll();
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(interactionData.size());
			
			List<Long> interactionIdsLoaded = new ArrayList<>();
			List<Long> interactionIdsBefore = null;// phenotypeAnnotationService.getAnnotationIdsByDataProvider(dataProvider);
			
			//runLoad(history, interactionData), annotationIdsLoaded, dataProvider);
			
			//runCleanup(phenotypeAnnotationService, history, interactionIdsBefore, interactionIdsLoaded, "phenotype annotation", bulkLoadFile.getMd5Sum());

			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Gets called from the API directly
	public APIResponse runLoad(List<PsiMiTabDTO> interactions) {
		List<Long> interactionIdsLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(interactions.size());
		runLoad(history, interactions, interactionIdsLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	
	private void runLoad(BulkLoadFileHistory history, List<PhenotypeFmsDTO> annotations, List<Long> idsAdded, BackendBulkDataProvider dataProvider) {
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Phenotype annotation DTO Update for " + dataProvider.name(), annotations.size());

		loadPrimaryAnnotations(history, annotations, idsAdded, dataProvider, ph);
		loadSecondaryAnnotations(history, annotations, idsAdded, dataProvider, ph);
		
		ph.finishProcess();

	}
	
	private void loadSecondaryAnnotations(BulkLoadFileHistory history, List<PhenotypeFmsDTO> annotations, List<Long> idsAdded, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		for (PhenotypeFmsDTO dto : annotations) {
			if (CollectionUtils.isEmpty(dto.getPrimaryGeneticEntityIds()))
				continue;

			try {
				//phenotypeAnnotationService.addInferredOrAssertedEntities(dto, idsAdded, dataProvider);
				history.incrementCompleted();
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		}
		
	}


	private void loadPrimaryAnnotations(BulkLoadFileHistory history, List<PhenotypeFmsDTO> annotations, List<Long> idsAdded, BackendBulkDataProvider dataProvider, ProcessDisplayHelper ph) {
		for (PhenotypeFmsDTO dto : annotations) {
			if (CollectionUtils.isNotEmpty(dto.getPrimaryGeneticEntityIds()))
				continue;

			try {
				Long primaryAnnotationId = null;//phenotypeAnnotationService.upsertPrimaryAnnotation(dto, dataProvider);
				if (primaryAnnotationId != null) {
					history.incrementCompleted();
					if (idsAdded != null)
						idsAdded.add(primaryAnnotationId);
				} 
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				e.printStackTrace();
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(dto, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		}
	}

}
