package org.alliancegenome.curation_api.jobs;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadURLProcessor extends BulkLoadProcessor {

	@ConsumeEvent(value = "BulkURLLoad", blocking = true) // Triggered by the Scheduler
	public void processBulkURLLoad(Message<BulkURLLoad> load) {
		BulkURLLoad bulkURLLoad = load.body();
		startLoad(bulkURLLoad);

		if (bulkURLLoad.getBulkloadUrl() != null && bulkURLLoad.getBulkloadUrl().length() > 0) {
			String filePath = fileHelper.saveIncomingURLFile(bulkURLLoad.getBulkloadUrl());
			String localFilePath = fileHelper.compressInputFile(filePath);
			processFilePath(bulkURLLoad, localFilePath, false);
			endLoad(bulkURLLoad, null, JobStatus.FINISHED);
		} else {
			log.info("Load: " + bulkURLLoad.getName() + " failed: URL is missing");
			endLoad(bulkURLLoad, "Load: " + bulkURLLoad.getName() + " failed: URL is missing", JobStatus.FAILED);
		}
	}
}
