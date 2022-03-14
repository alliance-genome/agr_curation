package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneFmsExecutor extends LoadFileExecutor {

    @Inject GeneService geneService;

    
    public void runLoad(BulkLoadFile bulkLoadFile) {
        
        try {
            GeneMetaDataFmsDTO geneData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), GeneMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(geneData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            
            trackHistory(runLoad(geneData), bulkLoadFile);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gets called from the API directly
    public APIResponse runLoad(GeneMetaDataFmsDTO geneData) {
        
        BulkLoadFileHistory history = new BulkLoadFileHistory(geneData.getData().size());
        
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);

        ph.startProcess("Gene FMS DTO Update", geneData.getData().size());

        for(GeneFmsDTO gene: geneData.getData()) {
            
            try {
                geneService.processUpdate(gene);
                history.incrementCompleted();
            } catch (ObjectUpdateException e) {
                addException(history, e.getData());
            } catch (Exception e) {
                addException(history, new ObjectUpdateExceptionData(gene, e.getMessage()));
            }

            ph.progressProcess();
        }
        ph.finishProcess();
        
        return new LoadHistoryResponce(history);
    }
}
