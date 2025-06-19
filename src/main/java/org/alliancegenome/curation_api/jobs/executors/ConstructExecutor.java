package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.ConstructService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConstructExecutor extends LoadFileExecutor {

	@Inject
	ConstructService constructService;

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, ConstructDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<ConstructDTO> constructs = ingestDto.getConstructIngestSet();
		if (CollectionUtils.isEmpty(constructs)) {
			return;
		}

		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<Long> constructIdsLoaded = new ArrayList<>();
		List<Long> constructIdsBefore = new ArrayList<>();
		if (cleanUp) {
			constructIdsBefore.addAll(constructService.getConstructIdsByDataProvider(dataProvider));
			Log.debug("runLoad: Before: total " + constructIdsBefore.size());
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(constructs.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount("Deleted", constructs.size());

		updateHistory(bulkLoadFileHistory);
		
		Set<String> refList = constructs.stream()
			.flatMap(obj -> Stream.ofNullable(obj.getReferenceCuries()).flatMap(List::stream))
			.collect(Collectors.toSet());

		constructService.preLoadReferences(refList);

		boolean success = runLoad(constructService, bulkLoadFileHistory, dataProvider, constructs, constructIdsLoaded);
		if (success && cleanUp) {
			runCleanup(constructService, bulkLoadFileHistory, dataProvider.name(), constructIdsBefore, constructIdsLoaded, "construct");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
