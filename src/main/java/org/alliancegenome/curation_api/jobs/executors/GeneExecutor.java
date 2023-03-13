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

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneExecutor extends LoadFileExecutor {

	@Inject
	GeneDAO geneDAO;

	@Inject
	GeneService geneService;

	@Inject
	NcbiTaxonTermService ncbiTaxonTermService;

	public void runLoad(BulkLoadFile bulkLoadFile) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getTaxonId());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (!validateSchemaVersion(bulkLoadFile, GeneDTO.class))
				return;
			List<GeneDTO> genes = ingestDto.getGeneIngestSet();
			String speciesName = manual.getDataType().getSpeciesName();
			String dataType = manual.getDataType().name();

			if (genes != null) {
				bulkLoadFile.setRecordCount(genes.size() + bulkLoadFile.getRecordCount());
				bulkLoadFileDAO.merge(bulkLoadFile);

				List<String> geneCuriesLoaded = new ArrayList<>();
				List<String> geneCuriesBefore = geneService.getCuriesBySpeciesName(speciesName);
				log.debug("runLoad: Before: total " + geneCuriesBefore.size());

				APIResponse resp = runLoad(speciesName, genes, dataType, geneCuriesLoaded);
				trackHistory(resp, bulkLoadFile);

				runCleanup(Collections.singleton(speciesName), dataType, geneCuriesBefore, geneCuriesLoaded);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runCleanup(Set<String> speciesNames, String dataType, List<String> geneCuriesBefore, List<String> geneCuriesLoaded) {
		log.debug("runLoad: After: " + speciesNames + " " + geneCuriesLoaded.size());

		List<String> distinctAfter = geneCuriesLoaded.stream().distinct().collect(Collectors.toList());
		log.debug("runLoad: Distinct: " + speciesNames + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(geneCuriesBefore, distinctAfter);
		log.debug("runLoad: Remove: " + speciesNames + " " + curiesToRemove.size());

		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of disease annotations linked to unloaded " + speciesNames + " genes", curiesToRemove.size());
		for (String curie : curiesToRemove) {
			geneService.removeOrDeprecateNonUpdatedGene(curie, dataType);
			ph.progressProcess();
		}
		ph.finishProcess();

	}

	// Gets called from the API directly
	public APIResponse runLoad(List<GeneDTO> genes) {
		List<String> taxonIds = genes.stream().map(geneDTO -> geneDTO.getTaxonCurie()).distinct().collect(Collectors.toList());
		Set<String> speciesNames = new HashSet<String>();
		for (String taxonId : taxonIds) {
			if (taxonId != null) {
				NCBITaxonTerm taxon = ncbiTaxonTermService.get(taxonId).getEntity();
				if (taxon != null)
					speciesNames.add(taxon.getGenusSpecies());
			}
		}
		return runLoad(speciesNames, genes, "API", null);
	}

	public APIResponse runLoad(String speciesName, List<GeneDTO> genes, String dataType, List<String> curiesAdded) {
		Set<String> speciesNames = Collections.singleton(speciesName);
		return runLoad(speciesNames, genes, dataType, curiesAdded);
	}

	public APIResponse runLoad(Set<String> speciesNames, List<GeneDTO> genes, String dataType, List<String> curiesAdded) {

		BulkLoadFileHistory history = new BulkLoadFileHistory(genes.size());
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);

		ph.startProcess("Gene Update " + speciesNames.toString(), genes.size());
		genes.forEach(geneDTO -> {

			try {
				Gene gene = geneService.upsert(geneDTO);
				history.incrementCompleted();
				if (curiesAdded != null) {
					curiesAdded.add(gene.getCurie());
				}
			} catch (ObjectUpdateException e) {
				addException(history, e.getData());
			} catch (Exception e) {
				addException(history, new ObjectUpdateExceptionData(geneDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

		return new LoadHistoryResponce(history);
	}
}
