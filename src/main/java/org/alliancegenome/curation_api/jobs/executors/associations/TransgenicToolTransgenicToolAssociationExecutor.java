package org.alliancegenome.curation_api.jobs.executors.associations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.TransgenicToolTransgenicToolAssociationDTO;
import org.alliancegenome.curation_api.services.associations.TransgenicToolTransgenicToolAssociationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

/** SCRUM-6543: bulk load for associations between two compatible transgenic tools. Mirrors CassetteTransgenicToolAssociationExecutor. */
@JBossLog
@ApplicationScoped
public class TransgenicToolTransgenicToolAssociationExecutor extends LoadFileExecutor {

	private static final String INGEST_SET = "transgenic_tool_transgenic_tool_association_ingest_set";

	@Inject TransgenicToolTransgenicToolAssociationService transgenicToolTransgenicToolAssociationService;

	@Override
	protected List<?> getIngestSet(IngestDTO ingestDto) {
		return ingestDto.getTransgenicToolTransgenicToolAssociationIngestSet();
	}

	@Override
	protected Class<?> getIngestDtoClass() {
		return TransgenicToolTransgenicToolAssociationDTO.class;
	}

	@Override
	protected void loadIngestSet(BulkLoadFileHistory bulkLoadFileHistory, IngestDTO ingestDto, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		List<TransgenicToolTransgenicToolAssociationDTO> associations = ingestDto.getTransgenicToolTransgenicToolAssociationIngestSet();

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(transgenicToolTransgenicToolAssociationService.getAssociationsByDataProvider(dataProvider));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		updateHistory(bulkLoadFileHistory);

		boolean success = runLoad(transgenicToolTransgenicToolAssociationService, bulkLoadFileHistory, dataProvider, associations, associationIdsLoaded, countLabel(INGEST_SET));
		if (cleanUp && success) {
			runCleanup(transgenicToolTransgenicToolAssociationService, bulkLoadFileHistory, dataProvider.name(), associationIdsBefore, associationIdsLoaded, countLabel(INGEST_SET));
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
