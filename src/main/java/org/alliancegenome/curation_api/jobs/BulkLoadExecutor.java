package org.alliancegenome.curation_api.jobs;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.loads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.model.fms.DataFile;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.fms.DataFileService;
import org.alliancegenome.curation_api.util.FileTransferHelper;
import org.apache.commons.codec.digest.DigestUtils;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.Message;
import io.vertx.mutiny.core.eventbus.EventBus;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadExecutor {

    @Inject
    EventBus bus;
    
    @Inject
    BulkFMSLoadDAO bulkFMSLoadDAO;
    
    @Inject
    BulkLoadFileDAO bulkLoadFileDAO;
    
    @Inject DataFileService fmsDataFileService;
    
    @Transactional
    @ConsumeEvent(value = "bulkload", blocking = true)
    public void bulkLoad(Message<BulkFMSLoad> load) {
        BulkFMSLoad fmsLoad = bulkFMSLoadDAO.find(load.body().getId());
        
        fmsLoad.setStatus(BulkLoadStatus.RUNNING);
        log.info("Load: " + fmsLoad + " is running");

        List<DataFile> files = fmsDataFileService.getDataFiles(fmsLoad.getDataType(), fmsLoad.getDataSubType());

        if(files.size() == 1) {
            DataFile df = files.get(0);
            log.info(df.getS3Url());
            FileTransferHelper helper = new FileTransferHelper(df.getS3Url());
            
            String filePath = helper.getOutputFilePath();
            File inputFile = new File(filePath);
        
            try {
                InputStream is = new GZIPInputStream(new FileInputStream(inputFile));
                String md5Sum = DigestUtils.md5Hex(is);
                is.close();

                log.info("MD5 Sum: " + md5Sum);
                
                SearchResponse<BulkLoadFile> bulkLoadFiles = bulkLoadFileDAO.findByField("md5Sum", md5Sum);
                BulkLoadFile bulkLoadFile;
                
                if(bulkLoadFiles == null || bulkLoadFiles.getTotalResults() == 0) {
                    bulkLoadFile = new BulkLoadFile();
                    bulkLoadFile.setBulkLoad(fmsLoad);
                    bulkLoadFile.setMd5Sum(md5Sum);
                    bulkLoadFile.setFileSize(inputFile.length());
                    bulkLoadFileDAO.persist(bulkLoadFile);
                } else {
                    bulkLoadFile = bulkLoadFiles.getResults().get(0);
                }
                if(!fmsLoad.getLoadFiles().contains(bulkLoadFile)) {
                    fmsLoad.getLoadFiles().add(bulkLoadFile);
                }
                
                bus.send("bulkloadfile", bulkLoadFile);
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
        } else {
            log.warn("Issue pulling files from the FMS: " + fmsLoad);
        }
    }
    
    @Transactional
    @ConsumeEvent(value = "bulkloadfile", blocking = true)
    public void bulkLoadFile(Message<BulkLoadFile> file) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BulkLoadFile bulkLoadFile = bulkLoadFileDAO.find(file.body().getId());
        
        bulkLoadFile.getBulkLoad().setStatus(BulkLoadStatus.FINISHED);
        log.info("Load: " + bulkLoadFile + " is finished");
    }
}
