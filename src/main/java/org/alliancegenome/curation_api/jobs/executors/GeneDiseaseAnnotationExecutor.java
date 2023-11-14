package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.services.GeneDiseaseAnnotationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneDiseaseAnnotationExecutor extends LoadFileExecutor {

	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	GeneDiseaseAnnotationService geneDiseaseAnnotationService;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;

	public void runLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile);
		if (ingestDto == null) return;
		
		List<GeneDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseGeneIngestSet();
		if (annotations == null) annotations = new ArrayList<>();

		List<Long> annotationIdsLoaded = new ArrayList<>();
		List<Long> annotationIdsBefore = new ArrayList<>();
		annotationIdsBefore.addAll(geneDiseaseAnnotationService.getAnnotationIdsByDataProvider(dataProvider));
		annotationIdsBefore.removeIf(Objects::isNull);

		bulkLoadFile.setRecordCount(annotations.size() + bulkLoadFile.getRecordCount());		
		bulkLoadFileDAO.merge(bulkLoadFile);
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
		
		runLoad(history, dataProvider, annotations, annotationIdsLoaded);
		
		if(cleanUp) runCleanup(diseaseAnnotationService, history, dataProvider.name(), annotationIdsBefore, annotationIdsLoaded, bulkLoadFile.getMd5Sum());

		history.finishLoad();
		
		trackHistory(history, bulkLoadFile);
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProviderName, List<GeneDiseaseAnnotationDTO> annotations) {

		List<Long> annotationIdsLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		runLoad(history, dataProvider, annotations, annotationIdsLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}
	
	public void runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<GeneDiseaseAnnotationDTO> annotations, List<Long> idsAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Gene Disease Annotation Update for: " + dataProvider.name(), annotations.size());
		annotations.forEach(annotationDTO -> {
			try {
				GeneDiseaseAnnotation annotation = geneDiseaseAnnotationService.upsert(annotationDTO, dataProvider);
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
