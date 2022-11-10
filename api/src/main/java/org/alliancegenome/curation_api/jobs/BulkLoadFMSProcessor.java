package org.alliancegenome.curation_api.jobs;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.fms.DataFile;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadFMSProcessor extends BulkLoadProcessor {

	@ConsumeEvent(value = "BulkFMSLoad", blocking = true) // Triggered by the Scheduler
	public void processBulkFMSLoad(Message<BulkFMSLoad> load) {
		BulkFMSLoad bulkFMSLoad = load.body();
		startLoad(bulkFMSLoad);

		if(bulkFMSLoad.getFmsDataType() != null && bulkFMSLoad.getFmsDataSubType() != null) {
			List<DataFile> files = fmsDataFileService.getDataFiles(bulkFMSLoad.getFmsDataType(), bulkFMSLoad.getFmsDataSubType());

			if(files.size() == 1) {
				DataFile df = files.get(0);
				String s3Url = df.getS3Url();
				String filePath = fileHelper.saveIncomingURLFile(s3Url);
				String localFilePath = fileHelper.compressInputFile(filePath);
				processFilePath(bulkFMSLoad, localFilePath);
				endLoad(bulkFMSLoad, null, JobStatus.FINISHED);
			} else {
				log.warn("Files: " + files);
				log.warn("Issue pulling files from the FMS: " + bulkFMSLoad.getFmsDataType() + " " + bulkFMSLoad.getFmsDataSubType());
				endLoad(bulkFMSLoad, "Issue pulling files from the FMS: " + bulkFMSLoad.getFmsDataType() + " " + bulkFMSLoad.getFmsDataSubType(), JobStatus.FAILED);
			}
			
		} else {
			log.error("Load: " + bulkFMSLoad.getName() + " failed: FMS Params are missing");
			endLoad(bulkFMSLoad, "Load: " + bulkFMSLoad.getName() + " failed: FMS Params are missing", JobStatus.FAILED);
		}
	}

}
