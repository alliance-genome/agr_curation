package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.CassetteService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CassetteExecutor extends LoadFileExecutor {

	@Inject
	CassetteService cassetteService;

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, CassetteDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<CassetteDTO> cassettes = ingestDto.getCassetteIngestSet();
		if (CollectionUtils.isEmpty(cassettes)) {
			return;
		}

		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<Long> cassetteIdsLoaded = new ArrayList<>();
		List<Long> cassetteIdsBefore = new ArrayList<>();
		if (cleanUp) {
			cassetteIdsBefore.addAll(cassetteService.getCassetteIdsByDataProvider(dataProvider));
			Log.debug("runLoad: Before: total " + cassetteIdsBefore.size());
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(cassettes.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount("Deleted", cassettes.size());

		updateHistory(bulkLoadFileHistory);

		Set<String> refList = cassettes.stream()
			.flatMap(obj -> Stream.ofNullable(obj.getReferenceCuries()).flatMap(List::stream))
			.collect(Collectors.toSet());

		cassetteService.preLoadReferences(refList);

		boolean success = runLoad(cassetteService, bulkLoadFileHistory, dataProvider, cassettes, cassetteIdsLoaded);
		if (success && cleanUp) {
			runCleanup(cassetteService, bulkLoadFileHistory, dataProvider.name(), cassetteIdsBefore, cassetteIdsLoaded, "cassette");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
