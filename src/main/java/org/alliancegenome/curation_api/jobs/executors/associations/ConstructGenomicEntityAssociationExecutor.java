package org.alliancegenome.curation_api.jobs.executors.associations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.services.associations.ConstructGenomicEntityAssociationService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class ConstructGenomicEntityAssociationExecutor extends LoadFileExecutor {

	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Species species = manual.getSpecies();
		log.info("Running with dataProvider: " + species.getDisplayName());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, ConstructGenomicEntityAssociationDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<ConstructGenomicEntityAssociationDTO> associations = ingestDto.getConstructGenomicEntityAssociationIngestSet();
		if (CollectionUtils.isEmpty(associations)) {
			return;
		}

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(constructGenomicEntityAssociationService.getAssociationsBySpecies(species));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(associations.size());
		updateHistory(bulkLoadFileHistory);

		boolean success = runLoad(constructGenomicEntityAssociationService, bulkLoadFileHistory, species, associations, associationIdsLoaded);
		if (cleanUp && success) {
			runCleanup(constructGenomicEntityAssociationService, bulkLoadFileHistory, species.getDisplayName(), associationIdsBefore, associationIdsLoaded, "construct genomic entity association");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
