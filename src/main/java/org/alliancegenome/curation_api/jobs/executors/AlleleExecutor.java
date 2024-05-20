package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.AlleleService;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AlleleExecutor extends LoadFileExecutor {

	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AlleleService alleleService;

	public void execLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile, AlleleDTO.class);
		if (ingestDto == null) return;
		
		List<AlleleDTO> alleles = ingestDto.getAlleleIngestSet();
		if (alleles == null) alleles = new ArrayList<>();
		
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		
		List<Long> alleleIdsLoaded = new ArrayList<>();
		List<Long> alleleIdsBefore = new ArrayList<>();
		if (cleanUp) {
			alleleIdsBefore.addAll(alleleService.getIdsByDataProvider(dataProvider.name()));
			Log.debug("runLoad: Before: total " + alleleIdsBefore.size());
		}
		
		bulkLoadFile.setRecordCount(alleles.size() + bulkLoadFile.getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFile);
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(alleles.size());
		createHistory(history, bulkLoadFile);
		runLoad(alleleService, history, dataProvider, alleles, alleleIdsLoaded);
		if(cleanUp) runCleanup(alleleService, history, bulkLoadFile, alleleIdsBefore, alleleIdsLoaded);
		history.finishLoad();
		finalSaveHistory(history);
	}

}
