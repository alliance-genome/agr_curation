package org.alliancegenome.curation_api.jobs.executors.associations.alleleAssociations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleConstructAssociationDTO;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleConstructAssociationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleConstructAssociationExecutor extends LoadFileExecutor {

	@Inject AlleleConstructAssociationService alleleConstructAssociationService;

	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFileHistory.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFileHistory, AlleleConstructAssociationDTO.class);
		if (ingestDto == null) {
			return;
		}

		List<AlleleConstructAssociationDTO> associations = ingestDto.getAlleleConstructAssociationIngestSet();
		if (associations == null) {
			associations = new ArrayList<>();
		}

		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(alleleConstructAssociationService.getAssociationsByDataProvider(dataProvider));
			associationIdsBefore.removeIf(Objects::isNull);
		}

		bulkLoadFileHistory.getBulkLoadFile().setRecordCount(associations.size() + bulkLoadFileHistory.getBulkLoadFile().getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFileHistory.getBulkLoadFile());

		bulkLoadFileHistory.setCount(associations.size());
		updateHistory(bulkLoadFileHistory);
		
		String countType = "Allele Construct Associations";
		boolean success = runLoad(alleleConstructAssociationService, bulkLoadFileHistory, dataProvider, associations, associationIdsLoaded, countType);
		if (success && cleanUp) {
			runCleanup(alleleConstructAssociationService, bulkLoadFileHistory, dataProvider.name(), associationIdsBefore, associationIdsLoaded, "allele construct association");
		}
		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);
	}

}
