package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
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
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

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
			log.info("Running with: " + manual.getDataProvider().name());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			
			if(!checkSchemaVersion(bulkLoadFile, GeneDTO.class)) return;

			List<GeneDTO> genes = ingestDto.getGeneIngestSet();
			if (genes == null) genes = new ArrayList<>();
			
			String dataProvider = manual.getDataProvider().name();

			List<String> geneCuriesLoaded = new ArrayList<>();
			List<String> geneCuriesBefore = geneService.getCuriesByDataProvider(dataProvider);
			log.debug("runLoad: Before: total " + geneCuriesBefore.size());

			bulkLoadFile.setRecordCount(genes.size() + bulkLoadFile.getRecordCount());
			bulkLoadFileDAO.merge(bulkLoadFile);

			BulkLoadFileHistory history = new BulkLoadFileHistory(genes.size());
			
			runLoad(history, genes, dataProvider, geneCuriesLoaded);

			runCleanup(geneService, history, dataProvider, geneCuriesBefore, geneCuriesLoaded, bulkLoadFile.getMd5Sum());
			
			history.finishLoad();
			
			trackHistory(history, bulkLoadFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(String dataProvider, List<GeneDTO> genes) {

		List<String> curiesLoaded = new ArrayList<>();
		
		BulkLoadFileHistory history = new BulkLoadFileHistory(genes.size());
		runLoad(history, genes, dataProvider, curiesLoaded);
		history.finishLoad();
		
		return new LoadHistoryResponce(history);
	}

	public void runLoad(BulkLoadFileHistory history, List<GeneDTO> genes, String dataProvider, List<String> curiesAdded) {

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("Gene Update for: " + dataProvider, genes.size());
		genes.forEach(geneDTO -> {
			try {
				Gene gene = geneService.upsert(geneDTO);
				history.incrementCompleted();
				if (curiesAdded != null) {
					curiesAdded.add(gene.getCurie());
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
