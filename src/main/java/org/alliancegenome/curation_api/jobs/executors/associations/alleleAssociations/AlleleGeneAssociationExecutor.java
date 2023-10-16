package org.alliancegenome.curation_api.jobs.executors.associations.alleleAssociations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.jobs.executors.LoadFileExecutor;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.associations.alleleAssociations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleGeneAssociationExecutor extends LoadFileExecutor {

	@Inject
	AlleleGeneAssociationService alleleGeneAssociationService;
	
	public void runLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider: " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile);
		if (ingestDto == null) return;
		
		List<AlleleGeneAssociationDTO> associations = ingestDto.getAlleleGeneAssociationIngestSet();
		if (associations == null) associations = new ArrayList<>();

		
		List<Long> associationIdsLoaded = new ArrayList<>();
		List<Long> associationIdsBefore = alleleGeneAssociationService.getAssociationsByDataProvider(dataProvider);
		associationIdsBefore.removeIf(Objects::isNull);
		
		bulkLoadFile.setRecordCount(associations.size() + bulkLoadFile.getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFile);

		BulkLoadFileHistory history = new BulkLoadFileHistory(associations.size());
		
		runLoad(history, dataProvider, associations, associationIdsLoaded);
		
		if(cleanUp) runCleanup(alleleGeneAssociationService, history, dataProvider.name(), associationIdsBefore, associationIdsLoaded, bulkLoadFile.getMd5Sum());

		history.finishLoad();
		
		trackHistory(history, bulkLoadFile);
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProviderName, List<AlleleGeneAssociationDTO> associations) {

		List<Long> associationIdsLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(associations.size());
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		runLoad(history, dataProvider, associations, associationIdsLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}
	
	public void runLoad(BulkLoadFileHistory history, BackendBulkDataProvider dataProvider, List<AlleleGeneAssociationDTO> associations, List<Long> idsAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Allele Gene Association Update for: " + dataProvider.name(), associations.size());
		associations.forEach(associationDTO -> {
			try {
				AlleleGeneAssociation association = alleleGeneAssociationService.upsert(associationDTO, dataProvider);
				history.incrementCompleted();
				if(idsAdded != null) {
					idsAdded.add(association.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(associationDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

	}
}