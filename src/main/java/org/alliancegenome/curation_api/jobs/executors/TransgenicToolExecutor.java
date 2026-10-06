package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.TransgenicToolService;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** SCRUM-6535: bulk load for transgenic tools. Mirrors ConstructExecutor. */
@ApplicationScoped
public class TransgenicToolExecutor extends LoadFileExecutor {

	@Inject TransgenicToolService transgenicToolService;

	@Override
	protected List<?> getIngestSet(IngestDTO ingestDto) {
		return ingestDto.getTransgenicToolIngestSet();
	}

	@Override
	protected Class<?> getIngestDtoClass() {
		return TransgenicToolDTO.class;
	}

	@Override
	protected void loadIngestSet(BulkLoadFileHistory bulkLoadFileHistory, IngestDTO ingestDto, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		List<TransgenicToolDTO> transgenicTools = ingestDto.getTransgenicToolIngestSet();

		BackendBulkDataProvider dataProvider = manual.getDataProvider();

		List<Long> idsLoaded = new ArrayList<>();
		List<Long> idsBefore = new ArrayList<>();
		if (cleanUp) {
			idsBefore.addAll(transgenicToolService.getTransgenicToolIdsByDataProvider(dataProvider));
			Log.debug("runLoad: Before: total " + idsBefore.size());
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(transgenicTools.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		updateHistory(bulkLoadFileHistory);

		Set<String> refList = transgenicTools.stream()
			.flatMap(obj -> Stream.ofNullable(obj.getReferenceCuries()).flatMap(List::stream))
			.collect(Collectors.toSet());

		transgenicToolService.preLoadReferences(refList);

		boolean success = runLoad(transgenicToolService, bulkLoadFileHistory, dataProvider, transgenicTools, idsLoaded, "Transgenic Tools");
		if (success && cleanUp) {
			runCleanup(transgenicToolService, bulkLoadFileHistory, dataProvider.name(), idsBefore, idsLoaded, "Transgenic Tools");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
