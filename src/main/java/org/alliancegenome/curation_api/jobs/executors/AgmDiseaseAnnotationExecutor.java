package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.AGMDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

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

	public void execLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

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
		
		createHistory(history, bulkLoadFile);
		
		runLoad(agmDiseaseAnnotationService, history, dataProvider, annotations, annotationIdsLoaded);
		
		if(cleanUp) runCleanup(diseaseAnnotationService, history, dataProvider.name(), annotationIdsBefore, annotationIdsLoaded, "AGM disease annotation", bulkLoadFile.getMd5Sum());
		history.finishLoad();
		finalSaveHistory(history);
	}


}
