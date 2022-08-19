package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneExecutor extends LoadFileExecutor {

	@Inject GeneDAO geneDAO;

	@Inject GeneService geneService;

	
	public void runLoad(BulkLoadFile bulkLoadFile) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getTaxonId());
			
			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			List<GeneDTO> genes = ingestDto.getGeneIngestSet();
			String taxonId = manual.getDataType().getTaxonId();
			
			if (genes != null) {
				bulkLoadFile.setRecordCount(genes.size() + bulkLoadFile.getRecordCount());
				bulkLoadFileDAO.merge(bulkLoadFile);
				
				trackHistory(runLoad(taxonId, genes), bulkLoadFile);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad (List <GeneDTO> genes) {
		List<String> taxonIds = genes.stream()
				 .map( geneDTO -> geneDTO.getTaxon() ).distinct().collect( Collectors.toList() );
		return runLoad(taxonIds, genes);
	}
	
	public APIResponse runLoad(String taxonId, List<GeneDTO> genes) {
		List<String> taxonIds = new ArrayList<String>();
		taxonIds.add(taxonId);
		return runLoad(taxonIds, genes);
	}
		
	public APIResponse runLoad(List<String> taxonIds, List<GeneDTO> genes) {
		
		List<String> geneCuriesBefore = new ArrayList<String>();
		for (String taxonId : taxonIds) {
			List<String> geneCuries = geneService.getCuriesByTaxonId(taxonId);
			log.debug("runLoad: Before: " + taxonId + " " + geneCuries.size());
			geneCuriesBefore.addAll(geneCuries);
		}
		if (taxonIds.size() > 1) log.debug("runLoad: Before: total " + geneCuriesBefore.size());
		
		List<String> geneCuriesAfter = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(genes.size());
		ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
		ph.startProcess("Gene Update " + taxonIds.toString(), genes.size());
		genes.forEach(geneDTO -> {
			
			try {
				Gene gene = geneService.upsert(geneDTO);
				history.incrementCompleted();
				geneCuriesAfter.add(gene.getCurie());
			} catch (ObjectUpdateException e) {
				addException(history, e.getData());
			} catch (Exception e) {
				addException(history, new ObjectUpdateExceptionData(geneDTO, e.getMessage()));
			}
			
			ph.progressProcess();
		});
		ph.finishProcess();
		
		geneService.removeNonUpdatedGenes(taxonIds.toString(), geneCuriesBefore, geneCuriesAfter);
		
		return new LoadHistoryResponce(history);	
	}
}
