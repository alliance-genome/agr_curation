package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.AlleleDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleDiseaseAnnotationExecutor extends LoadFileExecutor {

	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AlleleDiseaseAnnotationService alleleDiseaseAnnotationService;
	@Inject DiseaseAnnotationService diseaseAnnotationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Species species = manual.getSpecies();
		log.info("Running with dataProvider: " + species.getDisplayName());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AlleleDiseaseAnnotationDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AlleleDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseAlleleIngestSet();
		if (CollectionUtils.isEmpty(annotations)) {
			return;
		}

		List<Long> annotationIdsLoaded = new ArrayList<>();
		List<Long> annotationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			annotationIdsBefore.addAll(alleleDiseaseAnnotationService.getAnnotationIdsByDataProvider(species));
			annotationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(annotations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());
		
		String countType = "Allele Disease Annotations";
		boolean success = runLoad(alleleDiseaseAnnotationService, bulkLoadFileHistory, species, annotations, annotationIdsLoaded, countType);
		if (success && cleanUp) {
			runCleanup(diseaseAnnotationService, bulkLoadFileHistory, species.getDisplayName(), annotationIdsBefore, annotationIdsLoaded, countType);
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
