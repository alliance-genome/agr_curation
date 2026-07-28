package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AntibodyDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.AntibodyService;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AntibodyExecutor extends LoadFileExecutor {

	@Inject
	AntibodyService antibodyService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AntibodyDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AntibodyDTO> antibodies = ingestDto.getAntibodyIngestSet();
		if (CollectionUtils.isEmpty(antibodies)) {
			return;
		}

		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<Long> antibodyIdsLoaded = new ArrayList<>();
		List<Long> antibodyIdsBefore = new ArrayList<>();
		if (cleanUp) {
			antibodyIdsBefore.addAll(antibodyService.getAntibodyIdsByDataProvider(dataProvider));
			Log.debug("runLoad: Before: total " + antibodyIdsBefore.size());
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(antibodies.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount("Deleted", antibodies.size());

		updateHistory(bulkLoadFileHistory);

		Set<String> refList = antibodies.stream()
			.flatMap(obj -> Stream.concat(
				Stream.ofNullable(obj.getReferenceCuries()).flatMap(List::stream),
				Stream.ofNullable(obj.getOriginalReferenceCurie())))
			.collect(Collectors.toSet());

		antibodyService.preLoadReferences(refList);

		boolean success = runLoad(antibodyService, bulkLoadFileHistory, dataProvider, antibodies, antibodyIdsLoaded);
		if (success && cleanUp) {
			runCleanup(antibodyService, bulkLoadFileHistory, dataProvider.name(), antibodyIdsBefore, antibodyIdsLoaded, "antibody");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
