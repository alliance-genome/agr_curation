package org.alliancegenome.curation_api.jobs.executors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.response.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoadFileExecutor {

    @Inject ObjectMapper mapper;
    @Inject BulkLoadFileDAO bulkLoadFileDAO;
    @Inject BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
    
    protected void trackHistory(APIResponse runHistory, BulkLoadFile bulkLoadFile) {
        LoadHistoryResponce res = (LoadHistoryResponce)runHistory;
        BulkLoadFileHistory history = res.getHistory();
        history.setBulkLoadFile(bulkLoadFile);
        bulkLoadFileHistoryDAO.persist(history);
        
        bulkLoadFile.getHistory().add(history);
        bulkLoadFileDAO.merge(bulkLoadFile);
        
    }
}
