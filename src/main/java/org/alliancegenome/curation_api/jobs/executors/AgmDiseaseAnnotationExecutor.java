package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
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
	AGMDiseaseAnnotationService agmDiseaseService;

	public void runLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			log.info("Running with: " + manual.getDataProvider().name());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			
			if(!checkSchemaVersion(bulkLoadFile, AGMDiseaseAnnotationDTO.class)) return;
			
			List<AGMDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseAgmIngestSet();
			if (annotations == null) annotations = new ArrayList<>();
			
			String dataProvider = manual.getDataProvider().name();

			List<Long> annotationIdsLoaded = new ArrayList<>();
			List<Long> annotationIdsBefore = new ArrayList<>();
			annotationIdsBefore.addAll(agmDiseaseAnnotationDAO.findAllAnnotationIdsByDataProvider(dataProvider));
			annotationIdsBefore.removeIf(Objects::isNull);
			
			bulkLoadFile.setRecordCount(annotations.size() + bulkLoadFile.getRecordCount());
			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
			
			runLoad(history, dataProvider, annotations, annotationIdsLoaded);
			
			if(cleanUp) runCleanup(diseaseAnnotationService, history, dataProvider, annotationIdsBefore, annotationIdsLoaded, bulkLoadFile.getMd5Sum());

			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProvider, List<AGMDiseaseAnnotationDTO> annotations) {

		List<Long> annotationIdsLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
		runLoad(history, dataProvider, annotations, annotationIdsLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}
	
	public void runLoad(BulkLoadFileHistory history, String dataProvider, List<AGMDiseaseAnnotationDTO> annotations, List<Long> curiesAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("AGM Disease Annotation Update for: " + dataProvider, annotations.size());
		annotations.forEach(annotationDTO -> {
			try {
				AGMDiseaseAnnotation annotation = agmDiseaseService.upsert(annotationDTO);
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
