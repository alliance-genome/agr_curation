package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AgmExecutor extends LoadFileExecutor {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;

	@Inject
	AffectedGenomicModelService affectedGenomicModelService;
	
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;

	public void runLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			Log.info("Running with: " + manual.getDataProvider().name());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			
			if(!checkSchemaVersion(bulkLoadFile, AffectedGenomicModelDTO.class)) return;
			
			List<AffectedGenomicModelDTO> agms = ingestDto.getAgmIngestSet();
			if (agms == null) agms = new ArrayList<>();
			
			String dataProvider = manual.getDataProvider().name();

			List<String> amgCuriesLoaded = new ArrayList<>();
			List<String> agmCuriesBefore = affectedGenomicModelService.getCuriesByDataProvider(dataProvider);
			Log.debug("runLoad: Before: total " + agmCuriesBefore.size());

			bulkLoadFile.setRecordCount(agms.size() + bulkLoadFile.getRecordCount());
			bulkLoadFileDAO.merge(bulkLoadFile);
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(agms.size());

			runLoad(history, agms, dataProvider, amgCuriesLoaded);
			
			if(cleanUp) runCleanup(affectedGenomicModelService, history, dataProvider, agmCuriesBefore, amgCuriesLoaded, bulkLoadFile.getMd5Sum());
			
			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProvider, List<AffectedGenomicModelDTO> agms) {

		List<String> curiesLoaded = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(agms.size());
		runLoad(history, agms, dataProvider, curiesLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	public void runLoad(BulkLoadFileHistory history, List<AffectedGenomicModelDTO> agms, String dataProvider, List<String> curiesAdded) {
	
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("AGM Update for: " + dataProvider, agms.size());
		agms.forEach(agmDTO -> {
			try {
				AffectedGenomicModel agm = affectedGenomicModelService.upsert(agmDTO);
				history.incrementCompleted();
				if(curiesAdded != null) {
					curiesAdded.add(agm.getCurie());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(agmDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

	}
}
