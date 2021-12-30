package org.alliancegenome.curation_api.jobs;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.*;
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
    @Inject BulkLoadDAO bulkLoadDAO;
    @Inject BulkManualLoadDAO bulkManualLoadDAO;
    @Inject DataFileService fmsDataFileService;
    @Inject BulkLoadFileDAO bulkLoadFileDAO;

    private FileTransferHelper fileHelper = new FileTransferHelper();

    public void process(BulkFMSLoad bulkFMSLoad) {
        if(bulkFMSLoad.getDataType() != null && bulkFMSLoad.getDataSubType() != null) {
            String s3Url = processFMS(bulkFMSLoad.getDataType(), bulkFMSLoad.getDataSubType());
            String filePath = fileHelper.saveIncomingURLFile(s3Url);
            String localFilePath = fileHelper.compressInputFile(filePath);
            processFilePath(bulkFMSLoad, localFilePath);
        } else {
            BulkLoad bulkLoad = bulkLoadDAO.find(bulkFMSLoad.getId());
            log.info("Load: " + bulkLoad.getName() + " failed: FMS Params are missing");
            bulkLoad.setErrorMessage("FMS Params are missing load Failed");
            bulkLoad.setStatus(BulkLoadStatus.FAILED);
            bulkLoadDAO.merge(bulkLoad);
        }
    }

    public void process(BulkURLLoad bulkURLLoad) {
        if(bulkURLLoad.getUrl() != null && bulkURLLoad.getUrl().length() > 0) {
            String filePath = fileHelper.saveIncomingURLFile(bulkURLLoad.getUrl());
            String localFilePath = fileHelper.compressInputFile(filePath);
            processFilePath(bulkURLLoad, localFilePath);
        } else {
            BulkLoad bulkLoad = bulkLoadDAO.find(bulkURLLoad.getId());
            log.info("Load: " + bulkLoad.getName() + " failed: URL is missing");
            bulkLoad.setErrorMessage("URL is missing load Failed");
            bulkLoad.setStatus(BulkLoadStatus.FAILED);
            bulkLoadDAO.merge(bulkLoad);
        }
    }
    
    public void process(MultipartFormDataInput input, BackendBulkLoadType type) {
        Map<String, List<InputPart>> form = input.getFormDataMap();
        log.info(form);
        if(form.containsKey(type)) {
            log.warn("Key not found: " + type);
            return;
        }
        
        BulkManualLoad bml = null;
        SearchResponse<BulkManualLoad> load = bulkManualLoadDAO.findByField("backendBulkLoadType", type);
        if(load != null && load.getTotalResults() == 1) {
            bml = load.getResults().get(0);
        } else {
            log.warn("BulkManualLoad not found: " + type);
            return;
        }
        
        String filePath = fileHelper.saveIncomingFile(input, bml.getBackendBulkLoadType().toString());
        String localFilePath = fileHelper.compressInputFile(filePath);
        processFilePath(bml, localFilePath);
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
        
        if(bulkLoadFile.getS3Path() != null && bulkLoadFile.getLocalFilePath() == null) {
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
            bulkLoadFile.setLocalFilePath(localFilePath);
            bulkLoadFileDAO.persist(bulkLoadFile);
        } else {
            log.info("Bulk File already exists not creating it");
            bulkLoadFile = bulkLoadFiles.getResults().get(0);
            bulkLoadFile.setLocalFilePath(localFilePath);
        }
        
        bulkLoadFile.setErrorMessage(null);
        bulkLoadFile.setStatus(BulkLoadStatus.PENDING);
        
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

}
