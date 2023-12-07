package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AlleleExecutor extends LoadFileExecutor {

	@Inject
	AlleleDAO alleleDAO;
	@Inject
	AlleleService alleleService;

	public void runLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		Log.info("Running with: " + manual.getDataProvider().name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile, AlleleDTO.class);
		if (ingestDto == null) return;
		
		List<AlleleDTO> alleles = ingestDto.getAlleleIngestSet();
		if (alleles == null) alleles = new ArrayList<>();
		
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		
		List<Long> alleleIdsLoaded = new ArrayList<>();
		List<Long> alleleIdsBefore = alleleService.getIdsByDataProvider(dataProvider.name());
		Log.debug("runLoad: Before: total " + alleleIdsBefore.size());
		
		bulkLoadFile.setRecordCount(alleles.size() + bulkLoadFile.getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFile);
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(alleles.size());
		
		runLoad(history, alleles, dataProvider, alleleIdsLoaded);
			
		if(cleanUp) runCleanup(alleleService, history, bulkLoadFile, alleleIdsBefore, alleleIdsLoaded);
			
		history.finishLoad();
			
		trackHistory(history, bulkLoadFile);
		
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProviderName, List<AlleleDTO> alleles) {

		List<Long> idsLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(alleles.size());

		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		runLoad(history, alleles, dataProvider, idsLoaded);
		
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	public void runLoad(BulkLoadFileHistory history, List<AlleleDTO> alleles, BackendBulkDataProvider dataProvider, List<Long> idsAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Allele Update for: " + dataProvider.name(), alleles.size());
		for(AlleleDTO alleleDTO: alleles) {
			try {
				Allele allele = alleleService.upsert(alleleDTO, dataProvider);
				history.incrementCompleted();
				if (idsAdded != null) {
					idsAdded.add(allele.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(alleleDTO, e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		ph.finishProcess();

	}

}
