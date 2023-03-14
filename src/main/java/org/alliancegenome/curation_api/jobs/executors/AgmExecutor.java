package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import io.quarkus.logging.Log;

@ApplicationScoped
public class AgmExecutor extends LoadFileExecutor {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;

	@Inject
	AffectedGenomicModelService affectedGenomicModelService;
	
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;

	public void runLoad(BulkLoadFile bulkLoadFile) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			Log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getSpeciesName());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (!validateSchemaVersion(bulkLoadFile, AffectedGenomicModelDTO.class))
				return;
			List<AffectedGenomicModelDTO> agms = ingestDto.getAgmIngestSet();
			if (agms == null) agms = new ArrayList<>();
			
			String speciesName = manual.getDataType().getSpeciesName();
			String dataType = manual.getDataType().name();

			List<String> amgCuriesLoaded = new ArrayList<>();
			List<String> agmCuriesBefore = affectedGenomicModelService.getCuriesBySpeciesName(speciesName);
			Log.debug("runLoad: Before: total " + agmCuriesBefore.size());

			bulkLoadFile.setRecordCount(agms.size() + bulkLoadFile.getRecordCount());
			bulkLoadFileDAO.merge(bulkLoadFile);
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(agms.size());

			runLoad(history, Collections.singleton(speciesName), agms, dataType, amgCuriesLoaded);
			
			runCleanup(affectedGenomicModelService, history, Collections.singleton(speciesName), dataType, agmCuriesBefore, amgCuriesLoaded);
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(List<AffectedGenomicModelDTO> agms) {
		List<String> taxonIds = agms.stream().map(agmDTO -> agmDTO.getTaxonCurie()).distinct().collect(Collectors.toList());
		Set<String> speciesNames = new HashSet<String>();
		for (String taxonId : taxonIds) {
			if (taxonId != null) {
				NCBITaxonTerm taxon = ncbiTaxonTermService.get(taxonId).getEntity();
				if (taxon != null)
					speciesNames.add(taxon.getGenusSpecies());
			}
		}
		BulkLoadFileHistory history = new BulkLoadFileHistory(agms.size());
		
		runLoad(history, speciesNames, agms, "API", null);
		
		return new LoadHistoryResponce(history);
	}

	public void runLoad(BulkLoadFileHistory history, Set<String> speciesNames, List<AffectedGenomicModelDTO> agms, String dataType, List<String> curiesAdded) {
	
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("AGM Update " + speciesNames.toString(), agms.size());
		agms.forEach(agmDTO -> {
			try {
				AffectedGenomicModel agm = affectedGenomicModelService.upsert(agmDTO, dataType);
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
