package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.ConstructService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConstructExecutor extends LoadFileExecutor {

	@Inject ConstructDAO constructDAO;

	@Inject ConstructService constructService;

	@Inject NcbiTaxonTermService ncbiTaxonTermService;

	public void execLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile, ConstructDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<ConstructDTO> constructs = ingestDto.getConstructIngestSet();
		if (constructs == null) {
			constructs = new ArrayList<>();
		}

		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<Long> constructIdsLoaded = new ArrayList<>();
		List<Long> constructIdsBefore = new ArrayList<>();
		if (cleanUp) {
			constructIdsBefore.addAll(constructService.getConstructIdsByDataProvider(dataProvider));
			Log.debug("runLoad: Before: total " + constructIdsBefore.size());
		}

		bulkLoadFile.setRecordCount(constructs.size() + bulkLoadFile.getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFile);

		BulkLoadFileHistory history = new BulkLoadFileHistory(constructs.size());
		createHistory(history, bulkLoadFile);
		boolean success = runLoad(constructService, history, dataProvider, constructs, constructIdsLoaded);
		if (success && cleanUp) {
			runCleanup(constructService, history, dataProvider.name(), constructIdsBefore, constructIdsLoaded, "construct", bulkLoadFile.getMd5Sum());
		}
		history.finishLoad();
		finalSaveHistory(history);
	}

}
