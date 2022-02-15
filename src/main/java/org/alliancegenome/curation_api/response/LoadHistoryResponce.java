package org.alliancegenome.curation_api.response;

import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;

import lombok.Data;

@Data
public class LoadHistoryResponce extends APIResponse {

    private BulkLoadFileHistory history;
    
    public LoadHistoryResponce(BulkLoadFileHistory history) {
        this.history = history;
    }

}
