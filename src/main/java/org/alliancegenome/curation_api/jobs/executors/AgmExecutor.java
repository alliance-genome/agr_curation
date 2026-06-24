package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AgmExecutor extends LoadFileExecutor {

	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

	@Inject AffectedGenomicModelService affectedGenomicModelService;

	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Log.info("Running with: " + manual.getSpecies().getDisplayName());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AffectedGenomicModelDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AffectedGenomicModelDTO> agms = ingestDto.getAgmIngestSet();
		if (CollectionUtils.isEmpty(agms)) {
			return;
		}

		Species species = manual.getSpecies();

		List<Long> agmIdsLoaded = new ArrayList<>();
		List<Long> agmIdsBefore = new ArrayList<>();
		if (cleanUp) {
			agmIdsBefore.addAll(affectedGenomicModelService.getIdsByDataProvider(species.getDisplayName()));
			Log.debug("runLoad: Before: total " + agmIdsBefore.size());
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(agms.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(agms.size());
		updateHistory(bulkLoadFileHistory);

		boolean success = runLoad(affectedGenomicModelService, bulkLoadFileHistory, species, agms, agmIdsLoaded);
		if (success && cleanUp) {
			runCleanup(affectedGenomicModelService, bulkLoadFileHistory, species.getDisplayName(), agmIdsBefore, agmIdsLoaded, "AGM");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);

	}

}
