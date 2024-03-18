package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

	public void runLoad(BulkLoadFile bulkLoadFile, Boolean cleanUp) {

		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		BackendBulkDataProvider dataProvider = manual.getDataProvider();
		log.info("Running with dataProvider : " + dataProvider.name());

		IngestDTO ingestDto = readIngestFile(bulkLoadFile, GeneDTO.class);
		if (ingestDto == null) return;
		
		List<GeneDTO> genes = ingestDto.getGeneIngestSet();
		if (genes == null) genes = new ArrayList<>();

		List<Long> geneIdsLoaded = new ArrayList<>();
		List<Long> geneIdsBefore = new ArrayList<>();
		if (cleanUp) {
			geneIdsBefore.addAll(geneService.getIdsByDataProvider(dataProvider));
			log.debug("runLoad: Before: total " + geneIdsBefore.size());
		}
		
		bulkLoadFile.setRecordCount(genes.size() + bulkLoadFile.getRecordCount());
		bulkLoadFileDAO.merge(bulkLoadFile);

		BulkLoadFileHistory history = new BulkLoadFileHistory(genes.size());
			
		runLoad(history, genes, dataProvider, geneIdsLoaded);

		if(cleanUp) runCleanup(geneService, history, bulkLoadFile, geneIdsBefore, geneIdsLoaded);
			
		history.finishLoad();
			
		trackHistory(history, bulkLoadFile);

	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProviderName, List<GeneDTO> genes) {

		List<Long> idsLoaded = new ArrayList<>();
		
		BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(dataProviderName);
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(genes.size());
		runLoad(history, genes, dataProvider, idsLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	public void runLoad(BulkLoadFileHistory history, List<GeneDTO> genes, BackendBulkDataProvider dataProvider, List<Long> idsAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(loadProcessDisplayService);
		ph.startProcess("Gene Update for: " + dataProvider.name(), genes.size());
		genes.forEach(geneDTO -> {
			try {
				Gene gene = geneService.upsert(geneDTO, dataProvider);
				history.incrementCompleted();
				if (idsAdded != null) {
					idsAdded.add(gene.getId());
				}
			} catch (ObjectUpdateException e) {
				history.incrementFailed();
				addException(history, e.getData());
			} catch (Exception e) {
				history.incrementFailed();
				addException(history, new ObjectUpdateExceptionData(geneDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

	}
}
