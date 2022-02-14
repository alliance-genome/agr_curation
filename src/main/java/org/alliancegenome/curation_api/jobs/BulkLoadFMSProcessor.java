package org.alliancegenome.curation_api.jobs;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
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

        if(bulkFMSLoad.getDataType() != null && bulkFMSLoad.getDataSubType() != null) {
            String s3Url = processFMS(bulkFMSLoad.getDataType(), bulkFMSLoad.getDataSubType());
            String filePath = fileHelper.saveIncomingURLFile(s3Url);
            String localFilePath = fileHelper.compressInputFile(filePath);
            processFilePath(bulkFMSLoad, localFilePath);
            endLoad(bulkFMSLoad, null, BulkLoadStatus.FINISHED);
        } else {
            log.error("Load: " + bulkFMSLoad.getName() + " failed: FMS Params are missing");
            endLoad(bulkFMSLoad, "Load: " + bulkFMSLoad.getName() + " failed: FMS Params are missing", BulkLoadStatus.FAILED);
        }
    }
    
    private String processFMS(String dataType, String dataSubType) {
        List<DataFile> files = fmsDataFileService.getDataFiles(dataType, dataSubType);

        if(files.size() == 1) {
            DataFile df = files.get(0);
            return df.getS3Url();
        } else {
            log.warn("Files: " + files);
            log.warn("Issue pulling files from the FMS: " + dataType + " " + dataSubType);
        }
        return null;
    }
}
