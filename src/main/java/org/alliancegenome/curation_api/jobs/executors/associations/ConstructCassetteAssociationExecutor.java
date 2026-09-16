package org.alliancegenome.curation_api.jobs.executors.associations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.ConstructCassetteAssociationDTO;
import org.alliancegenome.curation_api.services.associations.ConstructCassetteAssociationService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

/** SCRUM-6535: bulk load for construct cassette associations. Mirrors ConstructGenomicEntityAssociationExecutor. */
@JBossLog
@ApplicationScoped
public class ConstructCassetteAssociationExecutor extends LoadFileExecutor {

	@Inject ConstructCassetteAssociationService lConstructCassetteAssociationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, ConstructCassetteAssociationDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<ConstructCassetteAssociationDTO> associations = ingestDto.getConstructCassetteAssociationIngestSet();
		if (CollectionUtils.isEmpty(associations)) {
			return;
		}

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(lConstructCassetteAssociationService.getAssociationsByDataProvider(dataProvider));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(associations.size());
		updateHistory(bulkLoadFileHistory);

		boolean success = runLoad(lConstructCassetteAssociationService, bulkLoadFileHistory, dataProvider, associations, associationIdsLoaded);
		if (cleanUp && success) {
			runCleanup(lConstructCassetteAssociationService, bulkLoadFileHistory, dataProvider.name(), associationIdsBefore, associationIdsLoaded, "construct cassette association");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
