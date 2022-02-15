package org.alliancegenome.curation_api.model.entities.bulkloads;


import java.util.*;

import javax.persistence.Transient;

import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;

import lombok.Data;

@Data
public class BulkLoadHistory {

    
    private long totalRecords;
    private long failedRecords;
    private long completedRecords;
    private List<ObjectUpdateException> exceptions = new ArrayList<>();
    
    public BulkLoadHistory(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    @Transient
    public void incrementCompleted() {
        completedRecords++;
    }
    @Transient
    public void incrementFailed() {
        failedRecords++;
    }

}
