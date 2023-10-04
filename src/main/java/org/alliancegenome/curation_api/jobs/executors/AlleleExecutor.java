package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;

@ApplicationScoped
public class AlleleExecutor extends LoadFileExecutor {

	@Inject
	AlleleDAO alleleDAO;

	@Inject
	AlleleService alleleService;
	
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;

	public void runLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			Log.info("Running with: " + manual.getDataProvider().name());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (StringUtils.isNotBlank(ingestDto.getAllianceMemberReleaseVersion()))
				bulkLoadFile.setAllianceMemberReleaseVersion(ingestDto.getAllianceMemberReleaseVersion());
			
			if(!checkSchemaVersion(bulkLoadFile, AlleleDTO.class)) return;
			
			List<AlleleDTO> alleles = ingestDto.getAlleleIngestSet();
			if (alleles == null) alleles = new ArrayList<>();
			
			BackendBulkDataProvider dataProvider = manual.getDataProvider();
			
			List<String> alleleCuriesLoaded = new ArrayList<>();
			List<String> alleleCuriesBefore = alleleService.getCuriesByDataProvider(dataProvider.name());
			Log.debug("runLoad: Before: total " + alleleCuriesBefore.size());
			
			bulkLoadFile.setRecordCount(alleles.size() + bulkLoadFile.getRecordCount());
			bulkLoadFileDAO.merge(bulkLoadFile);
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(alleles.size());

			runLoad(history, alleles, dataProvider, alleleCuriesLoaded);
			
			if(cleanUp) runCleanup(alleleService, history, dataProvider.name(), alleleCuriesBefore, alleleCuriesLoaded, bulkLoadFile.getMd5Sum());
			
			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProviderName, List<AlleleDTO> alleles) {

		List<String> curiesLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(alleles.size());
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		runLoad(history, alleles, dataProvider, curiesLoaded);
		
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	public void runLoad(BulkLoadFileHistory history, List<AlleleDTO> alleles, BackendBulkDataProvider dataProvider, List<String> curiesAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Allele Update for: " + dataProvider.name(), alleles.size());
		alleles.forEach(alleleDTO -> {
			try {
				Allele allele = alleleService.upsert(alleleDTO, dataProvider);
				history.incrementCompleted();
				if (curiesAdded != null) {
					curiesAdded.add(allele.getCurie());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(alleleDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

	}

}
