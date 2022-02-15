package org.alliancegenome.curation_api.response;

import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadHistory;

import lombok.Data;

@Data
public class LoadHistoryResponce extends APIResponse {

    private BulkLoadHistory history;
    
    public LoadHistoryResponce(BulkLoadHistory history) {
        this.history = history;
    }

}
