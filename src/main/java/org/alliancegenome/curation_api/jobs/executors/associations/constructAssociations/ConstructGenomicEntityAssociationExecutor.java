package org.alliancegenome.curation_api.jobs.executors.associations.constructAssociations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.associations.constructAssociations.ConstructGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.constructAssociations.ConstructGenomicEntityAssociationDTO;
import org.alliancegenome.curation_api.services.associations.constructAssociations.ConstructGenomicEntityAssociationService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class ConstructGenomicEntityAssociationExecutor extends LoadFileExecutor {

	@Inject
	ConstructGenomicEntityAssociationDAO constructGenomicEntityAssociationDAO;
	@Inject
	ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;

	public void execLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile, ConstructGenomicEntityAssociationDTO.class);
		if (ingestDto == null) return;
		
		List<ConstructGenomicEntityAssociationDTO> associations = ingestDto.getConstructGenomicEntityAssociationIngestSet();
		if (associations == null) associations = new ArrayList<>();

		
		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = new ArrayList<>();
		if (cleanUp) {
			associationIdsBefore.addAll(constructGenomicEntityAssociationService.getAssociationsByDataProvider(dataProvider));
			associationIdsBefore.removeIf(Objects::isNull);
		}
		
		bulkLoadFile.setRecordCount(associations.size() + bulkLoadFile.getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFile);

		BulkLoadFileHistory history = new BulkLoadFileHistory(associations.size());
		createHistory(history, bulkLoadFile);
		runLoad(constructGenomicEntityAssociationService, history, dataProvider, associations, associationIdsLoaded);
		
		if(cleanUp) runCleanup(constructGenomicEntityAssociationService, history, dataProvider.name(), associationIdsBefore, associationIdsLoaded, bulkLoadFile.getMd5Sum());

		history.finishLoad();
		
		finalSaveHistory(history);
	}

}
