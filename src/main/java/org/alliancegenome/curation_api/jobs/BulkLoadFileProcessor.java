package org.alliancegenome.curation_api.jobs;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.*;
import org.alliancegenome.curation_api.enums.BackendBulkDataType;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.*;
import org.alliancegenome.curation_api.model.fms.DataFile;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.fms.DataFileService;
import org.alliancegenome.curation_api.util.FileTransferHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.plugins.providers.multipart.*;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadFileProcessor {

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

    private FileTransferHelper fileHelper = new FileTransferHelper();

    @ConsumeEvent(value = "BulkFMSLoad", blocking = true) // Triggered by the Scheduler
    public void processBulkFMSLoad(Message<BulkFMSLoad> load) {
        BulkFMSLoad bulkFMSLoad = load.body();
        startLoad(bulkFMSLoad);

        if(bulkFMSLoad.getDataType() != null && bulkFMSLoad.getDataSubType() != null) {
            String s3Url = processFMS(bulkFMSLoad.getDataType(), bulkFMSLoad.getDataSubType());
            String filePath = fileHelper.saveIncomingURLFile(s3Url);
            String localFilePath = fileHelper.compressInputFile(filePath);
            processFilePath(bulkFMSLoad, localFilePath);
            bulkFMSLoad.setErrorMessage(null);
            bulkFMSLoad = bulkFMSLoadDAO.find(bulkFMSLoad.getId());
            bulkFMSLoad.setStatus(BulkLoadStatus.FINISHED);
            bulkLoadDAO.merge(bulkFMSLoad);
        } else {
            BulkLoad bulkLoad = bulkLoadDAO.find(bulkFMSLoad.getId());
            log.info("Load: " + bulkLoad.getName() + " failed: FMS Params are missing");
            bulkLoad.setErrorMessage("FMS Params are missing load Failed");
            bulkLoad.setStatus(BulkLoadStatus.FAILED);
            bulkLoadDAO.merge(bulkLoad);
        }
    }

    @ConsumeEvent(value = "BulkURLLoad", blocking = true) // Triggered by the Scheduler
    public void processBulkURLLoad(Message<BulkURLLoad> load) {
        BulkURLLoad bulkURLLoad = load.body();
        startLoad(bulkURLLoad);
        
        if(bulkURLLoad.getUrl() != null && bulkURLLoad.getUrl().length() > 0) {
            String filePath = fileHelper.saveIncomingURLFile(bulkURLLoad.getUrl());
            String localFilePath = fileHelper.compressInputFile(filePath);
            processFilePath(bulkURLLoad, localFilePath);
            bulkURLLoad.setErrorMessage(null);
            bulkURLLoad = bulkURLLoadDAO.find(bulkURLLoad.getId());
            bulkURLLoad.setStatus(BulkLoadStatus.FINISHED);
            bulkLoadDAO.merge(bulkURLLoad);
        } else {
            BulkLoad bulkLoad = bulkLoadDAO.find(bulkURLLoad.getId());
            log.info("Load: " + bulkLoad.getName() + " failed: URL is missing");
            bulkLoad.setErrorMessage("URL is missing load Failed");
            bulkLoad.setStatus(BulkLoadStatus.FAILED);
            bulkLoadDAO.merge(bulkLoad);
        }
    }
    
    @ConsumeEvent(value = "BulkManualLoad", blocking = true) // Triggered by the Scheduler
    public void processBulkManualLoadFromAPI(Message<BulkManualLoad> load) {
        BulkManualLoad bulkManualLoad = load.body();
        startLoad(bulkManualLoad);
        
        bulkManualLoad.setErrorMessage(null);
        bulkManualLoad = bulkManualLoadDAO.find(bulkManualLoad.getId());
        bulkManualLoad.setStatus(BulkLoadStatus.FINISHED);
        bulkLoadDAO.merge(bulkManualLoad);
    }

    public void processBulkManualLoadFromDQM(MultipartFormDataInput input, BackendBulkLoadType loadType, BackendBulkDataType dataType) {  // Triggered by the API
        Map<String, List<InputPart>> form = input.getFormDataMap();
        
        log.info(form);
        
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
            startLoad(bulkManualLoad);
        } else {
            log.warn("BulkManualLoad not found: " + loadType);
            return;
        }
        
        String filePath = fileHelper.saveIncomingFile(input, bulkManualLoad.getBackendBulkLoadType().toString() + "_" + bulkManualLoad.getDataType().toString());
        String localFilePath = fileHelper.compressInputFile(filePath);
        processFilePath(bulkManualLoad, localFilePath);
        
        bulkManualLoad.setErrorMessage(null);
        bulkManualLoad = bulkManualLoadDAO.find(bulkManualLoad.getId());
        bulkManualLoad.setStatus(BulkLoadStatus.FINISHED);
        bulkLoadDAO.merge(bulkManualLoad);
    }
    

    @ConsumeEvent(value = "bulkloadfile", blocking = true) // Triggered by the Scheduler or Forced start
    public void bulkLoadFile(Message<BulkLoadFile> file) {
        BulkLoadFile bulkLoadFile = bulkLoadFileDAO.find(file.body().getId());
        if(bulkLoadFile.getStatus() != BulkLoadStatus.STARTED && bulkLoadFile.getStatus() != BulkLoadStatus.FORCED_STARTED) {
            log.warn("bulkLoadFile: Job is not started returning: " + bulkLoadFile.getStatus());
            endLoad(bulkLoadFile, bulkLoadFile.getStatus(), "Finished ended due to status: " + bulkLoadFile.getStatus());
            return;
        }

        bulkLoadFile.getBulkLoad().setStatus(BulkLoadStatus.RUNNING);
        bulkLoadDAO.merge(bulkLoadFile.getBulkLoad());
        bulkLoadFile.setStatus(BulkLoadStatus.RUNNING);
        bulkLoadFileDAO.merge(bulkLoadFile);

        log.info("Load: " + bulkLoadFile.getBulkLoad().getName() + " is running with file: " + bulkLoadFile.getLocalFilePath());

        try {
            if(bulkLoadFile.getLocalFilePath() == null || bulkLoadFile.getS3Path() == null) {
                syncWithS3(bulkLoadFile);
            }
            bulkLoadJobExecutor.process(bulkLoadFile);
            endLoad(bulkLoadFile, BulkLoadStatus.FINISHED, "");
            log.info("Load: " + bulkLoadFile + " is finished");
            
        } catch (Exception e) {
            endLoad(bulkLoadFile, BulkLoadStatus.FAILED, "Failed loading: " + bulkLoadFile.getBulkLoad().getName() + " please check the logs for more info. " + bulkLoadFile.getErrorMessage());
            log.error("Load: " + bulkLoadFile.getBulkLoad().getName() + " is failed");
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
        }
        if(bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() != null) {
            if(s3AccessKey != null && s3AccessKey.length() > 0) {
                String s3Path = fileHelper.uploadFileToS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, bulkLoadFile.generateS3MD5Path(), new File(bulkLoadFile.getLocalFilePath()));
                bulkLoadFile.setS3Path(s3Path);
            }
            bulkLoadFileDAO.merge(bulkLoadFile);
        }
        
        if(bulkLoadFile.getS3Path() == null && bulkLoadFile.getLocalFilePath() == null) {
            bulkLoadFile.setErrorMessage("Failed to download or upload file with S3 Path: " + s3PathPrefix + "/" + bulkLoadFile.generateS3MD5Path() + " Local and remote file missing");
            bulkLoadFile.setStatus(BulkLoadStatus.FAILED);
        }
        log.info("Syncing with S3 Finished");
    }

    private void processFilePath(BulkLoad bulkLoad, String localFilePath) {
        String md5Sum = getMD5SumOfGzipFile(localFilePath);
        log.info("MD5 Sum: " + md5Sum);

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
            bulkLoadFile.setStatus(BulkLoadStatus.PENDING_START);
            bulkLoadFile.setLocalFilePath(localFilePath);
            bulkLoadFileDAO.persist(bulkLoadFile);
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

    private void startLoad(BulkLoad load) {
        log.info("Load: " + load.getName() + " is starting");

        BulkLoad bulkLoad = bulkLoadDAO.find(load.getId());
        if(bulkLoad.getStatus() != BulkLoadStatus.STARTED) {
            log.warn("startLoad: Job is not started returning: " + bulkLoad.getStatus());
            return;
        }

        bulkLoad.setStatus(BulkLoadStatus.RUNNING);
        bulkLoadDAO.merge(bulkLoad);
        log.info("Load: " + bulkLoad.getName() + " is running");
    }
    
    private void endLoad(BulkLoadFile bulkLoadFile, BulkLoadStatus status, String message) {
        bulkLoadFile.getBulkLoad().setStatus(status);
        bulkLoadDAO.merge(bulkLoadFile.getBulkLoad());
        
        if(bulkLoadFile.getLocalFilePath() != null) {
            new File(bulkLoadFile.getLocalFilePath()).delete();
            bulkLoadFile.setLocalFilePath(null);
        }
        
        bulkLoadFile.setErrorMessage(message);
        bulkLoadFile.setStatus(status);
        bulkLoadFileDAO.merge(bulkLoadFile);
    }

}
