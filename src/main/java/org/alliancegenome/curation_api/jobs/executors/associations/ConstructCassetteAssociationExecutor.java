package org.alliancegenome.curation_api.jobs.executors.associations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructCassetteAssociationDTO;
import org.alliancegenome.curation_api.services.associations.ConstructCassetteAssociationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

/** SCRUM-6535: bulk load for construct cassette associations. Mirrors ConstructGenomicEntityAssociationExecutor. */
@JBossLog
@ApplicationScoped
public class ConstructCassetteAssociationExecutor extends LoadFileExecutor {

	private static final String INGEST_SET = "construct_cassette_association_ingest_set";

	@Inject ConstructCassetteAssociationService constructCassetteAssociationService;
	@Inject ConstructDAO constructDAO;

	@Override
	protected List<?> getIngestSet(IngestDTO ingestDto) {
		return ingestDto.getConstructCassetteAssociationIngestSet();
	}

	@Override
	protected Class<?> getIngestDtoClass() {
		return ConstructCassetteAssociationDTO.class;
	}

	@Override
	protected void loadIngestSet(BulkLoadFileHistory bulkLoadFileHistory, IngestDTO ingestDto, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		List<ConstructCassetteAssociationDTO> associations = ingestDto.getConstructCassetteAssociationIngestSet();

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(constructCassetteAssociationService.getAssociationsByDataProvider(dataProvider));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		updateHistory(bulkLoadFileHistory);

		boolean success = runLoad(constructCassetteAssociationService, bulkLoadFileHistory, dataProvider, associations, associationIdsLoaded, countLabel(INGEST_SET));
		if (cleanUp && success) {
			runCleanup(constructCassetteAssociationService, bulkLoadFileHistory, dataProvider.name(), associationIdsBefore, associationIdsLoaded, countLabel(INGEST_SET));
		}
		reindexSubjects(dataProvider);
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

	/**
	 * The service skips automatic indexing of constructs while associations are upserted (see its upsert),
	 * so index the provider's constructs once now, including those whose associations were cleaned up.
	 */
	public void reindexSubjects(BackendBulkDataProvider dataProvider) {
		constructDAO.reindexDataProvider(dataProvider.sourceOrganization);
	}

}
