package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AgmExecutor extends LoadFileExecutor {

	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

	@Inject AffectedGenomicModelService affectedGenomicModelService;

	public void runLoad(BulkLoadFile bulkLoadFile) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getTaxonId());
			
			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			List<AffectedGenomicModelDTO> agms = ingestDto.getAgmIngestSet();
			String taxonId = manual.getDataType().getTaxonId();
			String dataType = manual.getDataType().name();
			
			if (agms != null) {
				bulkLoadFile.setRecordCount(agms.size() + bulkLoadFile.getRecordCount());
				bulkLoadFileDAO.merge(bulkLoadFile);
				
				trackHistory(runLoad(taxonId, agms, dataType), bulkLoadFile);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Gets called from the API directly
	public APIResponse runLoad (List <AffectedGenomicModelDTO> agms) {
		List<String> taxonIds = agms.stream()
				.map( agmDTO -> agmDTO.getTaxonCurie() ).distinct().collect( Collectors.toList() );
		return runLoad(taxonIds, agms, "API");
	}
		
	public APIResponse runLoad(String taxonId, List<AffectedGenomicModelDTO> agms, String dataType) {
		List<String> taxonIds = new ArrayList<String>();
		taxonIds.add(taxonId);
		return runLoad(taxonIds, agms, dataType);
	}
			
	public APIResponse runLoad(List<String> taxonIds, List<AffectedGenomicModelDTO> agms, String dataType) {
			
		List<String> agmCuriesBefore = new ArrayList<String>();
		for (String taxonId : taxonIds) {
			List<String> agmCuries = affectedGenomicModelService.getCuriesByTaxonId(taxonId);
			log.debug("runLoad: Before: " + taxonId + " " + agmCuries.size());
			agmCuriesBefore.addAll(agmCuries);
		}
		if (taxonIds.size() > 1) log.debug("runLoad: Before: total " + agmCuriesBefore.size());
			
		List<String> agmCuriesAfter = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(agms.size());
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("AGM Update " + taxonIds.toString(), agms.size());
		agms.forEach(agmDTO -> {
				
			try {
				AffectedGenomicModel agm = affectedGenomicModelService.upsert(agmDTO);
				history.incrementCompleted();
				agmCuriesAfter.add(agm.getCurie());
			} catch (ObjectUpdateException e) {
				addException(history, e.getData());
			} catch (Exception e) {
				addException(history, new ObjectUpdateExceptionData(agmDTO, e.getMessage(), e.getStackTrace()));
			}
				
			ph.progressProcess();
		});
		ph.finishProcess();
			
		affectedGenomicModelService.removeNonUpdatedAgms(taxonIds.toString(), agmCuriesBefore, agmCuriesAfter, dataType);
			
		return new LoadHistoryResponce(history);	
	}
}
