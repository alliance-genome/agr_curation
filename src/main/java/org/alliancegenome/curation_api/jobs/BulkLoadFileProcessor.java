package org.alliancegenome.curation_api.jobs;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
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
    @Inject DataFileService fmsDataFileService;
    @Inject BulkLoadFileDAO bulkLoadFileDAO;

    private FileTransferHelper fileHelper = new FileTransferHelper();

    public void process(BulkFMSLoad bulkFMSLoad) {
        String s3Url = processFMS(bulkFMSLoad.getDataType(), bulkFMSLoad.getDataSubType());
        String filePath = fileHelper.saveIncomingURLFile(s3Url);
        String localFilePath = fileHelper.compressInputFile(filePath);
        BulkLoadFile file = processFilePath(bulkFMSLoad, localFilePath);
        bus.send("bulkloadfile", file);
    }

    public void process(BulkURLLoad bulkURLLoad) {
        String filePath = fileHelper.saveIncomingURLFile(bulkURLLoad.getUrl());
        String localFilePath = fileHelper.compressInputFile(filePath);
        BulkLoadFile file = processFilePath(bulkURLLoad, localFilePath);
        bus.send("bulkloadfile", file);
    }
    
    public void process(BulkManualLoad bulkManualLoad) {
        // TODO FIx using multi form input 
        String localFilePath = fileHelper.compressInputFile(bulkManualLoad.getName());
        BulkLoadFile file = processFilePath(bulkManualLoad, localFilePath);
        bus.send("bulkloadfile", file);
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

    private BulkLoadFile processFilePath(BulkLoad load, String localFilePath) {

        String md5Sum = getMD5SumOfGzipFile(localFilePath);
        log.info("MD5 Sum: " + md5Sum);

        File inputFile = new File(localFilePath);

        SearchResponse<BulkLoadFile> bulkLoadFiles = bulkLoadFileDAO.findByField("md5Sum", md5Sum);
        BulkLoadFile bulkLoadFile;

        if(bulkLoadFiles == null || bulkLoadFiles.getTotalResults() == 0) {
            log.info("Bulk File does not exist creating it");
            bulkLoadFile = new BulkLoadFile();
            bulkLoadFile.setBulkLoad(load);
            bulkLoadFile.setMd5Sum(md5Sum);
            bulkLoadFile.setFileSize(inputFile.length());
            bulkLoadFile.setStatus(BulkLoadStatus.PENDING);
            bulkLoadFile.setLocalFilePath(localFilePath);
            bulkLoadFileDAO.persist(bulkLoadFile);
        } else {
            log.info("Bulk File already exists not creating it");
            bulkLoadFile = bulkLoadFiles.getResults().get(0);
            bulkLoadFile.setErrorMessage(null);
            bulkLoadFile.setLocalFilePath(localFilePath);
        }
        
        if(bulkLoadFile.getS3Path() == null) {
            String s3Path = fileHelper.uploadFileToS3(s3AccessKey, s3SecretKey, s3Bucket, s3PathPrefix, bulkLoadFile.generateS3MD5Path(), inputFile);
            bulkLoadFile.setS3Path(s3Path);
        }
        
        if(!load.getLoadFiles().contains(bulkLoadFile)) {
            load.getLoadFiles().add(bulkLoadFile);
        }
        bulkLoadFileDAO.merge(bulkLoadFile);
        bulkLoadDAO.merge(load);

        return bulkLoadFile;
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
