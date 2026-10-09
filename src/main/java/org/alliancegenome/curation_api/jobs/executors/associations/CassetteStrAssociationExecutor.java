package org.alliancegenome.curation_api.jobs.executors.associations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.CassetteStrAssociationDTO;
import org.alliancegenome.curation_api.services.associations.CassetteStrAssociationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

/** SCRUM-6535: bulk load for cassette STR associations. Mirrors ConstructGenomicEntityAssociationExecutor. */
@JBossLog
@ApplicationScoped
public class CassetteStrAssociationExecutor extends LoadFileExecutor {

	// Named by hand rather than countLabel(), which would capitalise the abbreviation STR as "Str".
	private static final String COUNT_LABEL = "Cassette STR Association";

	@Inject CassetteStrAssociationService lCassetteStrAssociationService;

	@Override
	protected List<?> getIngestSet(IngestDTO ingestDto) {
		return ingestDto.getCassetteStrAssociationIngestSet();
	}

	@Override
	protected Class<?> getIngestDtoClass() {
		return CassetteStrAssociationDTO.class;
	}

	@Override
	protected void loadIngestSet(BulkLoadFileHistory bulkLoadFileHistory, IngestDTO ingestDto, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		List<CassetteStrAssociationDTO> associations = ingestDto.getCassetteStrAssociationIngestSet();

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(lCassetteStrAssociationService.getAssociationsByDataProvider(dataProvider));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		updateHistory(bulkLoadFileHistory);

		boolean success = runLoad(lCassetteStrAssociationService, bulkLoadFileHistory, dataProvider, associations, associationIdsLoaded, COUNT_LABEL);
		if (cleanUp && success) {
			runCleanup(lCassetteStrAssociationService, bulkLoadFileHistory, dataProvider.name(), associationIdsBefore, associationIdsLoaded, COUNT_LABEL);
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
