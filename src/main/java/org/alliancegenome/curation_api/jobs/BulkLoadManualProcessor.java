package org.alliancegenome.curation_api.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.enums.BackendBulkDataType;
import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadManualProcessor extends BulkLoadProcessor {

	@ConsumeEvent(value = "BulkManualLoad", blocking = true) // Triggered by the Scheduler or API
	public void processBulkManualLoadFromAPI(Message<BulkManualLoad> load) {
		//BulkManualLoad bulkManualLoad = load.body();
		//bulkManualLoad = bulkManualLoadDAO.find(bulkManualLoad.getId());
		//startLoad(bulkManualLoad);
		//endLoad(bulkManualLoad, null, BulkLoadStatus.FINISHED);
	}
	
	public void processBulkManualLoadFromDQM(MultipartFormDataInput input, BackendBulkLoadType loadType, BackendBulkDataType dataType) {  // Triggered by the API
		Map<String, List<InputPart>> form = input.getFormDataMap();
		
		if(form.containsKey(loadType)) {
			log.warn("Key not found: " + loadType);
			return;
		}
		
		BulkManualLoad bulkManualLoad = null;
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("backendBulkLoadType", loadType);
		params.put("dataType", dataType);
		SearchResponse<BulkManualLoad> load = bulkManualLoadDAO.findByParams(null, params);
		if(load != null && load.getTotalResults() == 1) {
			bulkManualLoad = load.getResults().get(0);
			bulkManualLoad.setBulkloadStatus(JobStatus.MANUAL_STARTED);
			
			startLoad(bulkManualLoad);
			
			String filePath = fileHelper.saveIncomingFile(input, bulkManualLoad.getBackendBulkLoadType().toString() + "_" + bulkManualLoad.getDataType().toString());
			String localFilePath = fileHelper.compressInputFile(filePath);
			processFilePath(bulkManualLoad, localFilePath);

			endLoad(bulkManualLoad, null, JobStatus.FINISHED);
		} else {
			log.warn("BulkManualLoad not found: " + loadType);
			endLoad(bulkManualLoad, "BulkManualLoad not found: " + loadType, JobStatus.FAILED);
			return;
		}

	}
}
