package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AgmFmsExecutor extends LoadFileExecutor {

    @Inject AffectedGenomicModelService affectedGenomicModelService;

    public void runLoad(BulkLoadFile bulkLoadFile) {
        try {
            AffectedGenomicModelMetaDataFmsDTO agmData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AffectedGenomicModelMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(agmData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            runLoad(agmData);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    // Gets called from the API directly
    public APIResponse runLoad(AffectedGenomicModelMetaDataFmsDTO agmData) {
        
        BulkLoadFileHistory history = new BulkLoadFileHistory(agmData.getData().size());
        
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("AGM FMS DTO Update", agmData.getData().size());
        for(AffectedGenomicModelFmsDTO agm: agmData.getData()) {
            try {
                affectedGenomicModelService.processUpdate(agm);
                history.incrementCompleted();
            } catch (ObjectUpdateException e) {
                history.getExceptions().add(e);
                history.incrementFailed();
            } catch (Exception e) {
                history.getExceptions().add(new ObjectUpdateException(agm, e.getMessage()));
                history.incrementFailed();
            }
            
            ph.progressProcess();
        }
        ph.finishProcess();
        
        return new LoadHistoryResponce(history);
    }
}
