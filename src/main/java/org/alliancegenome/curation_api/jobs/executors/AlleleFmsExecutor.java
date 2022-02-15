package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleFmsExecutor extends LoadFileExecutor {

    @Inject AlleleService alleleService;
    
    public void runLoad(BulkLoadFile bulkLoadFile) {
        
        try {
            AlleleMetaDataFmsDTO alleleData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AlleleMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(alleleData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            
            runLoad(alleleData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Gets called from the API directly
    public APIResponse runLoad(AlleleMetaDataFmsDTO alleleData) {
        
        BulkLoadHistory history = new BulkLoadHistory(alleleData.getData().size());
        
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Allele FMS DTO Update", alleleData.getData().size());

        for(AlleleFmsDTO allele: alleleData.getData()) {
            try {
                alleleService.processUpdate(allele);
                history.incrementCompleted();
            } catch (ObjectUpdateException e) {
                history.getExceptions().add(e);
                history.incrementFailed();
            } catch (Exception e) {
                history.getExceptions().add(new ObjectUpdateException(allele, e.getMessage()));
                history.incrementFailed();
            }

            ph.progressProcess();
        }
        ph.finishProcess();
        
        return new LoadHistoryResponce(history);
    }


}
