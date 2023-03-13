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
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
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
			log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getSpeciesName());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (!validateSchemaVersion(bulkLoadFile, AffectedGenomicModelDTO.class))
				return;
			List<AffectedGenomicModelDTO> agms = ingestDto.getAgmIngestSet();
			String speciesName = manual.getDataType().getSpeciesName();
			String dataType = manual.getDataType().name();

			if(agms != null) {
				bulkLoadFile.setRecordCount(agms.size() + bulkLoadFile.getRecordCount());
				bulkLoadFileDAO.merge(bulkLoadFile);
				
				List<String> amgCuriesLoaded = new ArrayList<>();
				List<String> agmCuriesBefore = affectedGenomicModelService.getCuriesBySpeciesName(speciesName);
				log.debug("runLoad: Before: total " + agmCuriesBefore.size());

				APIResponse resp = runLoad(speciesName, agms, dataType, amgCuriesLoaded);
				
				trackHistory(resp, bulkLoadFile);

				runCleanup(Collections.singleton(speciesName), dataType, agmCuriesBefore, amgCuriesLoaded);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void runCleanup(Set<String> speciesNames, String dataType, List<String> agmCuriesBefore, List<String> agmCuriesAfter) {
		log.debug("runLoad: After: " + speciesNames + " " + agmCuriesAfter.size());

		List<String> distinctAfter = agmCuriesAfter.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + speciesNames + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(agmCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + speciesNames + " " + curiesToRemove.size());

		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of disease annotations linked to unloaded " + speciesNames + " AGMs", curiesToRemove.size());
		for (String curie : curiesToRemove) {
			affectedGenomicModelService.removeOrDeprecateNonUpdatedAgm(curie, dataType);
			ph.progressProcess();
		}
		ph.finishProcess();
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
		return runLoad(speciesNames, agms, "API", null);
	}

	public APIResponse runLoad(String speciesName, List<AffectedGenomicModelDTO> agms, String dataType, List<String> curiesAdded) {
		Set<String> speciesNames = Collections.singleton(speciesName);
		return runLoad(speciesNames, agms, dataType, curiesAdded);
	}

	public APIResponse runLoad(Set<String> speciesNames, List<AffectedGenomicModelDTO> agms, String dataType, List<String> curiesAdded) {

		List<String> agmCuriesBefore = new ArrayList<String>();
		for (String speciesName : speciesNames) {
			List<String> agmCuries = affectedGenomicModelService.getCuriesBySpeciesName(speciesName);
			log.debug("runLoad: Before: " + speciesName + " " + agmCuries.size());
			agmCuriesBefore.addAll(agmCuries);
		}
		if (speciesNames.size() > 1)
			log.debug("runLoad: Before: total " + agmCuriesBefore.size());

		List<String> agmCuriesAfter = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(agms.size());
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("AGM Update " + speciesNames.toString(), agms.size());
		agms.forEach(agmDTO -> {

			try {
				AffectedGenomicModel agm = affectedGenomicModelService.upsert(agmDTO);
				history.incrementCompleted();
				if(curiesAdded != null) {
					agmCuriesAfter.add(agm.getCurie());
				}
			} catch (ObjectUpdateException e) {
				addException(history, e.getData());
			} catch (Exception e) {
				addException(history, new ObjectUpdateExceptionData(agmDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

		return new LoadHistoryResponce(history);
	}
}
