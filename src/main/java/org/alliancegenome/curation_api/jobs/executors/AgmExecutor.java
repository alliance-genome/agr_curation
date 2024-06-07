package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AgmExecutor extends LoadFileExecutor {

	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

	@Inject AffectedGenomicModelService affectedGenomicModelService;

	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	public void execLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile, AffectedGenomicModelDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AffectedGenomicModelDTO> agms = ingestDto.getAgmIngestSet();
		if (agms == null) {
			agms = new ArrayList<>();
		}

		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<Long> agmIdsLoaded = new ArrayList<>();
		List<Long> agmIdsBefore = new ArrayList<>();
		if (cleanUp) {
			agmIdsBefore.addAll(affectedGenomicModelService.getIdsByDataProvider(dataProvider.name()));
			Log.debug("runLoad: Before: total " + agmIdsBefore.size());
		}

		bulkLoadFile.setRecordCount(agms.size() + bulkLoadFile.getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFile);

		BulkLoadFileHistory history = new BulkLoadFileHistory(agms.size());
		createHistory(history, bulkLoadFile);
		boolean success = runLoad(affectedGenomicModelService, history, dataProvider, agms, agmIdsLoaded);
		if (success && cleanUp) {
			runCleanup(affectedGenomicModelService, history, dataProvider.name(), agmIdsBefore, agmIdsLoaded, "AGM", bulkLoadFile.getMd5Sum());
		}
		history.finishLoad();
		finalSaveHistory(history);

	}

}
