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

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** SCRUM-6535: bulk load for cassettes. Mirrors ConstructExecutor. */
@ApplicationScoped
public class CassetteExecutor extends LoadFileExecutor {

	@Inject CassetteService cassetteService;

	@Override
	protected List<?> getIngestSet(IngestDTO ingestDto) {
		return ingestDto.getCassetteIngestSet();
	}

	@Override
	protected Class<?> getIngestDtoClass() {
		return CassetteDTO.class;
	}

	@Override
	protected void loadIngestSet(BulkLoadFileHistory bulkLoadFileHistory, IngestDTO ingestDto, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		List<CassetteDTO> cassettes = ingestDto.getCassetteIngestSet();

		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<Long> idsLoaded = new ArrayList<>();
		List<Long> idsBefore = new ArrayList<>();
		if (cleanUp) {
			idsBefore.addAll(cassetteService.getCassetteIdsByDataProvider(dataProvider));
			Log.debug("runLoad: Before: total " + idsBefore.size());
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(cassettes.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		updateHistory(bulkLoadFileHistory);

		Set<String> refList = cassettes.stream()
			.flatMap(obj -> Stream.ofNullable(obj.getReferenceCuries()).flatMap(List::stream))
			.collect(Collectors.toSet());

		cassetteService.preLoadReferences(refList);

		boolean success = runLoad(cassetteService, bulkLoadFileHistory, dataProvider, cassettes, idsLoaded, "Cassettes");
		if (success && cleanUp) {
			runCleanup(cassetteService, bulkLoadFileHistory, dataProvider.name(), idsBefore, idsLoaded, "Cassettes");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
