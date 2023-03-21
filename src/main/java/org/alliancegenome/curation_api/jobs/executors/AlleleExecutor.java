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

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import io.quarkus.logging.Log;

@ApplicationScoped
public class AlleleExecutor extends LoadFileExecutor {

	@Inject
	AlleleDAO alleleDAO;

	@Inject
	AlleleService alleleService;
	
	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;

	public void runLoad(BulkLoadFile bulkLoadFile) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			Log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getTaxonId());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			
			if(!checkSchemaVersion(bulkLoadFile, AlleleDTO.class)) return;
			
			List<AlleleDTO> alleles = ingestDto.getAlleleIngestSet();
			if (alleles == null) alleles = new ArrayList<>();
			
			String speciesName = manual.getDataType().getSpeciesName();
			String dataType = manual.getDataType().name();
			
			List<String> alleleCuriesLoaded = new ArrayList<>();
			List<String> alleleCuriesBefore = alleleService.getCuriesBySpeciesName(speciesName);
			Log.debug("runLoad: Before: total " + alleleCuriesBefore.size());
			
			bulkLoadFile.setRecordCount(alleles.size() + bulkLoadFile.getRecordCount());
			bulkLoadFileDAO.merge(bulkLoadFile);
			
			BulkLoadFileHistory history = new BulkLoadFileHistory(alleles.size());

			runLoad(history, Collections.singleton(speciesName), alleles, dataType, alleleCuriesLoaded);
			
			runCleanup(alleleService, history, Collections.singleton(speciesName), dataType, alleleCuriesBefore, alleleCuriesLoaded);
			
			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(List<AlleleDTO> alleles) {
		List<String> taxonIds = alleles.stream().map(alleleDTO -> alleleDTO.getTaxonCurie()).distinct().collect(Collectors.toList());
		Set<String> speciesNames = new HashSet<String>();
		for (String taxonId : taxonIds) {
			if (taxonId != null) {
				NCBITaxonTerm taxon = ncbiTaxonTermService.get(taxonId).getEntity();
				if (taxon != null)
					speciesNames.add(taxon.getGenusSpecies());
			}
		}
		BulkLoadFileHistory history = new BulkLoadFileHistory(alleles.size());
		
		runLoad(history, speciesNames, alleles, null, null);
		
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	public void runLoad(BulkLoadFileHistory history, Set<String> speciesNames, List<AlleleDTO> alleles, String dataType, List<String> curiesAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("Allele Update " + speciesNames.toString(), alleles.size());
		alleles.forEach(alleleDTO -> {
			try {
				Allele allele = alleleService.upsert(alleleDTO);
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
