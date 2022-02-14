package org.alliancegenome.curation_api.jobs;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;

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
        
        if(bulkURLLoad.getUrl() != null && bulkURLLoad.getUrl().length() > 0) {
            String filePath = fileHelper.saveIncomingURLFile(bulkURLLoad.getUrl());
            String localFilePath = fileHelper.compressInputFile(filePath);
            processFilePath(bulkURLLoad, localFilePath);
            endLoad(bulkURLLoad, null, BulkLoadStatus.FINISHED);
        } else {
            log.info("Load: " + bulkURLLoad.getName() + " failed: URL is missing");
            endLoad(bulkURLLoad, "Load: " + bulkURLLoad.getName() + " failed: URL is missing", BulkLoadStatus.FAILED);
        }
    }
}
