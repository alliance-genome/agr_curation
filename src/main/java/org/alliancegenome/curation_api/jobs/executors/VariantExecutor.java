package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.VariantDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.VariantService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;

@ApplicationScoped
public class VariantExecutor extends LoadFileExecutor {

	@Inject
	VariantDAO variantDAO;
	@Inject
	VariantService variantService;

	public void runLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			Log.info("Running with: " + manual.getDataProvider().name());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (StringUtils.isNotBlank(ingestDto.getAllianceMemberReleaseVersion()))
				bulkLoadFile.setAllianceMemberReleaseVersion(ingestDto.getAllianceMemberReleaseVersion());
			
			if(!checkSchemaVersion(bulkLoadFile, VariantDTO.class)) return;
			
			List<VariantDTO> variants = ingestDto.getVariantIngestSet();
			if (variants == null) variants = new ArrayList<>();
			
			BackendBulkDataProvider dataProvider = manual.getDataProvider();
			
			List<String> variantCuriesLoaded = new ArrayList<>();
			List<String> variantCuriesBefore = variantService.getCuriesByDataProvider(dataProvider.name());
			Log.debug("runLoad: Before: total " + variantCuriesBefore.size());
			
			bulkLoadFile.setRecordCount(variants.size() + bulkLoadFile.getRecordCount());
			bulkLoadFileDAO.merge(bulkLoadFile);
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(variants.size());

			runLoad(history, variants, dataProvider, variantCuriesLoaded);
			
			if(cleanUp) runCleanup(variantService, history, bulkLoadFile, variantCuriesBefore, variantCuriesLoaded);
			
			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProviderName, List<VariantDTO> variants) {

		List<String> curiesLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(variants.size());
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		runLoad(history, variants, dataProvider, curiesLoaded);
		
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	public void runLoad(BulkLoadFileHistory history, List<VariantDTO> variants, BackendBulkDataProvider dataProvider, List<String> curiesAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Variant Update for: " + dataProvider.name(), variants.size());
		variants.forEach(variantDTO -> {
			try {
				Variant variant = variantService.upsert(variantDTO, dataProvider);
				history.incrementCompleted();
				if (curiesAdded != null) {
					curiesAdded.add(variant.getCurie());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(variantDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

	}

}
