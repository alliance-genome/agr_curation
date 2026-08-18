package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.services.TransgenicToolService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TransgenicToolExecutor extends LoadFileExecutor {

	@Inject
	TransgenicToolService transgenicToolService;

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, TransgenicToolDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<TransgenicToolDTO> transgenicTools = ingestDto.getTransgenicToolIngestSet();
		if (CollectionUtils.isEmpty(transgenicTools)) {
			return;
		}

		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<Long> transgenicToolIdsLoaded = new ArrayList<>();
		List<Long> transgenicToolIdsBefore = new ArrayList<>();
		if (cleanUp) {
			transgenicToolIdsBefore.addAll(transgenicToolService.getTransgenicToolIdsByDataProvider(dataProvider));
			Log.debug("runLoad: Before: total " + transgenicToolIdsBefore.size());
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(transgenicTools.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount("Deleted", transgenicTools.size());

		updateHistory(bulkLoadFileHistory);

		Set<String> refList = transgenicTools.stream()
			.flatMap(obj -> Stream.ofNullable(obj.getReferenceCuries()).flatMap(List::stream))
			.collect(Collectors.toSet());

		transgenicToolService.preLoadReferences(refList);

		boolean success = runLoad(transgenicToolService, bulkLoadFileHistory, dataProvider, transgenicTools, transgenicToolIdsLoaded);
		if (success && cleanUp) {
			runCleanup(transgenicToolService, bulkLoadFileHistory, dataProvider.name(), transgenicToolIdsBefore, transgenicToolIdsLoaded, "transgenictool");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
