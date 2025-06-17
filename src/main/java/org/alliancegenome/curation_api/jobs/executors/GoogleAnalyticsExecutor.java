package org.alliancegenome.curation_api.jobs.executors;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.interfaces.base.BasePopularityInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ontology.DoTermService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.nilosplace.process_display.ProcessDisplayHelper;
import net.nilosplace.process_display.util.ObjectFileStorage;

@ApplicationScoped
public class GoogleAnalyticsExecutor extends LoadFileExecutor {

	@Inject
	BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;

	@Inject DoTermService doTermService;
	@Inject GoTermService goTermService;
	@Inject GeneService geneService;
	@Inject AlleleService alleleService;
	
	public void execLoad(BulkLoadFileHistory bulkLoadFileHistory) throws Exception {

		Log.info("Loading Google Analytics File");

		File inputFile = new File(bulkLoadFileHistory.getBulkLoadFile().getLocalFilePath());
		ObjectFileStorage<Map<String, Map<String, Double>>> objectFileStorage = new ObjectFileStorage<>();
		try {
			Map<String, Map<String, Double>> analyticsMap = objectFileStorage.readObjectFromFile(inputFile);
			Map<String, BasePopularityInterface> dataTypes = Map.of("disease_ontology",doTermService,"allele",alleleService,"gene",geneService,"gene_ontology",goTermService);
			for(Entry<String, BasePopularityInterface> dataTypeEntry : dataTypes.entrySet()) {
				Map<String, Double> dataTypeMap = analyticsMap.get(dataTypeEntry.getKey());
				ProcessDisplayHelper ph = new ProcessDisplayHelper();
				ph.startProcess("Google Analytics for " + dataTypeEntry.getKey(), dataTypeMap.size());
				for (Map.Entry<String, Double> entry : dataTypeMap.entrySet()) {
					dataTypeEntry.getValue().updatePopularity(entry.getKey(), entry.getValue());
					ph.progressProcess();
				}
				ph.finishProcess();
			}
		} catch (Exception e) {
			Log.error("Error reading Google Analytics file: " + e.getMessage());
			throw e;
		}
		updateHistory(bulkLoadFileHistory);

		bulkLoadFileHistory.finishLoad();
		updateHistory(bulkLoadFileHistory);
		updateExceptions(bulkLoadFileHistory);

		Log.info("Loading Google Analytics File Finished");
	}

}
