package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.services.VariantService;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VariantExecutor extends LoadFileExecutor {

	@Inject
	VariantDAO variantDAO;
	@Inject
	VariantService variantService;

	public void execLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile, VariantDTO.class);
		if (ingestDto == null) return;
		
		List<VariantDTO> variants = ingestDto.getVariantIngestSet();
		if (variants == null) variants = new ArrayList<>();
		
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		
		List<Long> variantIdsLoaded = new ArrayList<>();
		List<Long> variantIdsBefore = new ArrayList<>();
		if (cleanUp) {
			variantIdsBefore.addAll(variantService.getIdsByDataProvider(dataProvider.name()));
			Log.debug("runLoad: Before: total " + variantIdsBefore.size());
		}
		
		bulkLoadFile.setRecordCount(variants.size() + bulkLoadFile.getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFile);
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(variants.size());
		createHistory(history, bulkLoadFile);
		boolean success = runLoad(variantService, history, dataProvider, variants, variantIdsLoaded);
		if(success && cleanUp) runCleanup(variantService, history, bulkLoadFile, variantIdsBefore, variantIdsLoaded);
		history.finishLoad();
		finalSaveHistory(history);

	}


}
