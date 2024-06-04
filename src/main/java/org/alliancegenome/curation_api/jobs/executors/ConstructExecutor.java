package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.ConstructService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;

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
			runCleanup(constructService, history, dataProvider.name(), constructIdsBefore, constructIdsLoaded, bulkLoadFile.getMd5Sum());
		}
		history.finishLoad();
		finalSaveHistory(history);
	}

	// TODO Remove this method
	private void runCleanup(ConstructService service, BulkLoadFileHistory history, String dataProviderName, List<Long> constructIdsBefore, List<Long> constructIdsAfter, String md5sum) {
		Log.debug("runLoad: After: " + dataProviderName + " " + constructIdsAfter.size());

		List<Long> distinctAfter = constructIdsAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProviderName + " " + distinctAfter.size());

		List<Long> idsToRemove = ListUtils.subtract(constructIdsBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + dataProviderName + " " + idsToRemove.size());

		history.setTotalDeleteRecords((long) idsToRemove.size());

		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of constructs linked to unloaded " + dataProviderName, idsToRemove.size());
		for (Long id : idsToRemove) {
			try {
				String loadDescription = dataProviderName + " construct bulk load (" + md5sum + ")";
				service.removeOrDeprecateNonUpdated(id, false, loadDescription);
				history.incrementDeleted();
				updateHistory(history);
			} catch (Exception e) {
				history.incrementDeleteFailed();
				addException(history, new ObjectUpdateExceptionData("{ \"id\": " + id + "}", e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		ph.finishProcess();

	}

}
