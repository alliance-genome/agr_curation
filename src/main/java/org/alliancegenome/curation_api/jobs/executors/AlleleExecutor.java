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

import lombok.extern.jbosslog.JBossLog;

@JBossLog
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
			log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getTaxonId());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (!validateSchemaVersion(bulkLoadFile, AlleleDTO.class))
				return;
			List<AlleleDTO> alleles = ingestDto.getAlleleIngestSet();
			String speciesName = manual.getDataType().getSpeciesName();
			String dataType = manual.getDataType().name();

			if (alleles == null)
				alleles = new ArrayList<>();
				
			bulkLoadFile.setRecordCount(alleles.size() + bulkLoadFile.getRecordCount());
			bulkLoadFileDAO.merge(bulkLoadFile);
			trackHistory(runLoad(speciesName, alleles, dataType), bulkLoadFile);
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
		return runLoad(speciesNames, alleles, "API");
	}

	public APIResponse runLoad(String speciesName, List<AlleleDTO> alleles, String dataType) {
		Set<String> speciesNames = Collections.singleton(speciesName);
		return runLoad(speciesNames, alleles, dataType);
	}

	public APIResponse runLoad(Set<String> speciesNames, List<AlleleDTO> alleles, String dataType) {

		List<String> alleleCuriesBefore = new ArrayList<String>();
		for (String speciesName : speciesNames) {
			List<String> alleleCuries = alleleService.getCuriesBySpeciesName(speciesName);
			log.debug("runLoad: Before: " + speciesName + " " + alleleCuries.size());
			alleleCuriesBefore.addAll(alleleCuries);
		}
		if (speciesNames.size() > 1)
			log.debug("runLoad: Before: total " + alleleCuriesBefore.size());

		List<String> alleleCuriesAfter = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(alleles.size());
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("Allele Update " + speciesNames.toString(), alleles.size());
		alleles.forEach(alleleDTO -> {

			try {
				Allele allele = alleleService.upsert(alleleDTO);
				history.incrementCompleted();
				alleleCuriesAfter.add(allele.getCurie());
			} catch (ObjectUpdateException e) {
				addException(history, e.getData());
			} catch (Exception e) {
				addException(history, new ObjectUpdateExceptionData(alleleDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

		alleleService.removeOrDeprecateNonUpdatedAlleles(speciesNames.toString(), alleleCuriesBefore, alleleCuriesAfter, dataType);

		return new LoadHistoryResponce(history);
	}

}
