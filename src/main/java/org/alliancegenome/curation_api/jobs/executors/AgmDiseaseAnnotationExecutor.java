package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AgmDiseaseAnnotationExecutor extends LoadFileExecutor {

	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	@Inject
	AGMDiseaseAnnotationService agmDiseaseAnnotationService;

	public void runLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile, AGMDiseaseAnnotationDTO.class);
		if (ingestDto == null) return;
		
		List<AGMDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseAgmIngestSet();
		if (annotations == null) annotations = new ArrayList<>();

		List<Long> annotationIdsLoaded = new ArrayList<>();
		List<Long> annotationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			annotationIdsBefore.addAll(agmDiseaseAnnotationService.getAnnotationIdsByDataProvider(dataProvider));
			annotationIdsBefore.removeIf(Objects::isNull);
		}
		
		bulkLoadFile.setRecordCount(annotations.size() + bulkLoadFile.getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFile);

		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
		
		runLoad(history, dataProvider, annotations, annotationIdsLoaded);
		
		if(cleanUp) runCleanup(diseaseAnnotationService, history, dataProvider.name(), annotationIdsBefore, annotationIdsLoaded, bulkLoadFile.getMd5Sum());

		history.finishLoad();
		
		trackHistory(history, bulkLoadFile);
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProviderName, List<AGMDiseaseAnnotationDTO> annotations) {

		List<Long> annotationIdsLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		runLoad(history, dataProvider, annotations, annotationIdsLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}
	
	private void runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<AGMDiseaseAnnotationDTO> annotations, List<Long> idsAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("AGM Disease Annotation Update for: " + dataProvider.name(), annotations.size());
		annotations.forEach(annotationDTO -> {
			try {
				AGMDiseaseAnnotation annotation = agmDiseaseAnnotationService.upsert(annotationDTO, dataProvider);
				history.incrementCompleted();
				if(idsAdded != null) {
					idsAdded.add(annotation.getId());
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
