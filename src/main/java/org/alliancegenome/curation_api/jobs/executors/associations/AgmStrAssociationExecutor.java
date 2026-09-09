package org.alliancegenome.curation_api.jobs.executors.associations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmSequenceTargetingReagentAssociationDTO;
import org.alliancegenome.curation_api.services.associations.AgmStrAssociationService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AgmStrAssociationExecutor extends LoadFileExecutor {

	@Inject AgmStrAssociationService agmStrAssociationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Species species = manual.getSpecies();
		log.info("Running with dataProvider: " + species.getDisplayName());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AgmSequenceTargetingReagentAssociationDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AgmSequenceTargetingReagentAssociationDTO> associations = ingestDto.getAgmStrAssociationIngestSet();
		if (CollectionUtils.isEmpty(associations)) {
			return;
		}

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(agmStrAssociationService.getAssociationsBySpecies(species));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		String countType = "AGM STR Associations";
		bulkLoadFileHistory.setCount(countType, associations.size());
		updateHistory(bulkLoadFileHistory);
		
		boolean success = runLoad(agmStrAssociationService, bulkLoadFileHistory, species, associations, associationIdsLoaded, countType);
		if (success && cleanUp) {
			runCleanup(agmStrAssociationService, bulkLoadFileHistory, species.getDisplayName(), associationIdsBefore, associationIdsLoaded, countType);
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
