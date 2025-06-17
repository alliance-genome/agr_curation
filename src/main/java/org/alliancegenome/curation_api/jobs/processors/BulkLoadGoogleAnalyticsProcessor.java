package org.alliancegenome.curation_api.jobs.processors;

import java.io.File;
import java.util.Map;

import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.StartedBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkGoogleAnalyticsLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import net.nilosplace.process_display.util.ObjectFileStorage;

@ApplicationScoped
public class BulkLoadGoogleAnalyticsProcessor extends BulkLoadProcessor {
	ObjectFileStorage<Map<String, Map<String, Double>>> objectFileStorage = new ObjectFileStorage<>();
	public void processCustomLoad(@ObservesAsync StartedBulkLoadJobEvent load) {
		BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
		if (bulkLoad instanceof BulkGoogleAnalyticsLoad googleAnalyticsBulkLoad) {

			Log.debug("processGoogleAnalyticsBulkLoad: " + load.getId());
			startLoad(googleAnalyticsBulkLoad);
			Map<String, Map<String, Double>> analyticsMap = googleAnalyticsService.getDataMap();
			try {
				File filePath = new File(fileHelper.generateUniqueFileName());
				objectFileStorage.writeObjectToFile(analyticsMap, filePath);
				String localFilePath = fileHelper.compressInputFile(filePath.getAbsolutePath());
				processFilePath(googleAnalyticsBulkLoad, localFilePath);
				endLoad(googleAnalyticsBulkLoad, null, JobStatus.FINISHED);
			} catch (Exception e) {
				Log.error("Error writing map to file: " + e.getMessage());
			}
		}
	}
}
