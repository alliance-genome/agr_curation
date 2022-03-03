package org.alliancegenome.curation_api.jobs;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.model.fms.DataFile;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.fms.DataFileService;
import org.alliancegenome.curation_api.util.FileTransferHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class BulkLoadProcessor {

    @ConfigProperty(name = "bulk.data.loads.s3Bucket")
    String s3Bucket = null;
    
    @ConfigProperty(name = "bulk.data.loads.s3PathPrefix")
    String s3PathPrefix = null;
    
    @ConfigProperty(name = "bulk.data.loads.s3AccessKey")
    String s3AccessKey = null;
    
    @ConfigProperty(name = "bulk.data.loads.s3SecretKey")
    String s3SecretKey = null;

    @Inject EventBus bus;
    @Inject DataFileService fmsDataFileService;
    
    @Inject BulkLoadDAO bulkLoadDAO;
    @Inject BulkManualLoadDAO bulkManualLoadDAO;
    @Inject BulkLoadFileDAO bulkLoadFileDAO;
    @Inject BulkFMSLoadDAO bulkFMSLoadDAO;
    @Inject BulkURLLoadDAO bulkURLLoadDAO;
    @Inject BulkLoadJobExecutor bulkLoadJobExecutor;

    protected FileTransferHelper fileHelper = new FileTransferHelper();

    @ConsumeEvent(value = "bulkloadfile", blocking = true) // Triggered by the Scheduler or Forced start
    public void bulkLoadFile(Message<BulkLoadFile> file) {
        BulkLoadFile bulkLoadFile = bulkLoadFileDAO.find(file.body().getId());
        if(!bulkLoadFile.getStatus().isStarted()) {
            log.warn("bulkLoadFile: Job is not started returning: " + bulkLoadFile.getStatus());
            //endLoad(bulkLoadFile, "Finished ended due to status: " + bulkLoadFile.getStatus(), bulkLoadFile.getStatus());
            return;
        } else {
            startLoadFile(bulkLoadFile);
        }

        try {
            if(bulkLoadFile.getLocalFilePath() == null || bulkLoadFile.getS3Path() == null) {
                syncWithS3(bulkLoadFile);
            }
            bulkLoadJobExecutor.process(bulkLoadFile);
            endLoadFile(bulkLoadFile, "", BulkLoadStatus.FINISHED);
            
        } catch (Exception e) {
            endLoadFile(bulkLoadFile, "Failed loading: " + bulkLoadFile.getBulkLoad().getName() + " please check the logs for more info. " + bulkLoadFile.getErrorMessage(), BulkLoadStatus.FAILED);
            log.error("Load File: " + bulkLoadFile.getBulkLoad().getName() + " is failed");
            e.printStackTrace();
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
    
    public void syncWithS3(BulkLoadFile bulkLoadFile) {
        log.info("Syncing with S3");
        log.info("Local: " + bulkLoadFile.getLocalFilePath());
        log.info("S3: " + bulkLoadFile.getS3Path());
        
        if((bulkLoadFile.getS3Path() != null || bulkLoadFile.generateS3MD5Path() != null) && bulkLoadFile.getLocalFilePath() == null) {
            File outfile = fileHelper.downloadFileFromS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, bulkLoadFile.generateS3MD5Path());
            if(outfile != null) {
                //log.info(outfile + " is of size: " + outfile.length());
                bulkLoadFile.setFileSize(outfile.length());
                bulkLoadFile.setLocalFilePath(outfile.getAbsolutePath());
            } else {
                //log.error("Failed to download file from S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path());
                bulkLoadFile.setErrorMessage("Failed to download file from S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path());
                bulkLoadFile.setStatus(BulkLoadStatus.FAILED);
            }
            //log.info("Saving File: " + bulkLoadFile);
            bulkLoadFileDAO.merge(bulkLoadFile);
        } else if(bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() != null) {
            if(s3AccessKey != null && s3AccessKey.length() > 0) {
                String s3Path = fileHelper.uploadFileToS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, bulkLoadFile.generateS3MD5Path(), new File(bulkLoadFile.getLocalFilePath()));
                bulkLoadFile.setS3Path(s3Path);
            }
            bulkLoadFileDAO.merge(bulkLoadFile);
        } else if(bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() == null) {
            bulkLoadFile.setErrorMessage("Failed to download or upload file with S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path() + " Local and remote file missing");
            bulkLoadFile.setStatus(BulkLoadStatus.FAILED);
        }
        log.info("Syncing with S3 Finished");
    }

    protected void processFilePath(BulkLoad bulkLoad, String localFilePath) {
        String md5Sum = getMD5SumOfGzipFile(localFilePath);
        log.info("processFilePath: MD5 Sum: " + md5Sum);

        File inputFile = new File(localFilePath);

        BulkLoad load = bulkLoadDAO.find(bulkLoad.getId());

        SearchResponse<BulkLoadFile> bulkLoadFiles = bulkLoadFileDAO.findByField("md5Sum", md5Sum);
        BulkLoadFile bulkLoadFile;

        if(bulkLoadFiles == null || bulkLoadFiles.getTotalResults() == 0) {
            log.info("Bulk File does not exist creating it");
            bulkLoadFile = new BulkLoadFile();
            bulkLoadFile.setBulkLoad(load);
            bulkLoadFile.setMd5Sum(md5Sum);
            bulkLoadFile.setFileSize(inputFile.length());
            if(load.getStatus() == BulkLoadStatus.FORCED_RUNNING) {
                bulkLoadFile.setStatus(BulkLoadStatus.FORCED_PENDING);
            }
            if(load.getStatus() == BulkLoadStatus.SCHEDULED_RUNNING) {
                bulkLoadFile.setStatus(BulkLoadStatus.SCHEDULED_PENDING);
            }
            
            bulkLoadFile.setLocalFilePath(localFilePath);
            bulkLoadFileDAO.persist(bulkLoadFile);
        } else if(load.getStatus().isForced()) {
            bulkLoadFile = bulkLoadFiles.getResults().get(0);
            if(bulkLoadFile.getStatus().isNotRunning()) {
                bulkLoadFile.setLocalFilePath(localFilePath);
                bulkLoadFile.setStatus(BulkLoadStatus.FORCED_PENDING);
            } else {
                log.warn("Bulk File is already running: " + bulkLoadFile.getMd5Sum());
                log.info("Cleaning up downloaded file: " + localFilePath);
                new File(localFilePath).delete();
            }
        } else {
            log.info("Bulk File already exists not creating it");
            bulkLoadFile = bulkLoadFiles.getResults().get(0);
            log.info("Cleaning up downloaded file: " + localFilePath);
            new File(localFilePath).delete();
            bulkLoadFile.setLocalFilePath(null);
        }

        if(!load.getLoadFiles().contains(bulkLoadFile)) {
            load.getLoadFiles().add(bulkLoadFile);
        }
        bulkLoadFileDAO.merge(bulkLoadFile);
        bulkLoadDAO.merge(load);
    }
    
    public String getMD5SumOfGzipFile(String fullFilePath) {
        try {
            InputStream is = new GZIPInputStream(new FileInputStream(new File(fullFilePath)));
            String md5Sum = DigestUtils.md5Hex(is);
            is.close();
            return md5Sum;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void startLoad(BulkLoad load) {
        log.info("Load: " + load.getName() + " is starting");

        BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
        if(!bulkLoad.getStatus().isStarted()) {
            log.warn("startLoad: Job is not started returning: " + bulkLoad.getStatus());
            return;
        }
        bulkLoad.setStatus(bulkLoad.getStatus().getNextStatus());
        bulkLoadDAO.merge(bulkLoad);
        log.info("Load: " + bulkLoad.getName() + " is running");
    }
    
    protected void endLoad(BulkLoad load, String message, BulkLoadStatus status) {
        BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
        bulkLoad.setErrorMessage(message);
        bulkLoad.setStatus(status);
        bulkLoadDAO.merge(bulkLoad);
        log.info("Load: " + bulkLoad.getName() + " is finished");
    }

    protected void startLoadFile(BulkLoadFile bulkLoadFile) {
        bulkLoadFile.setStatus(bulkLoadFile.getStatus().getNextStatus());
        bulkLoadFileDAO.merge(bulkLoadFile);
        log.info("Load File: " + bulkLoadFile.getMd5Sum() + " is running with file: " + bulkLoadFile.getLocalFilePath());
    }

    protected void endLoadFile(BulkLoadFile bulkLoadFile, String message, BulkLoadStatus status) {
        if(bulkLoadFile.getLocalFilePath() != null) {
            new File(bulkLoadFile.getLocalFilePath()).delete();
            bulkLoadFile.setLocalFilePath(null);
        }
        bulkLoadFile.setErrorMessage(message);
        bulkLoadFile.setStatus(status);
        bulkLoadFileDAO.merge(bulkLoadFile);
        log.info("Load File: " + bulkLoadFile.getMd5Sum() + " is finished");
    }

}
