package org.alliancegenome.curation_api.controllers;

import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.DQMSubmissionInterface;
import org.alliancegenome.curation_api.jobs.BulkLoadFileProcessor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BackendBulkLoadType;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

public class DQMSubmissionController implements DQMSubmissionInterface {

    @Inject BulkLoadFileProcessor bulkLoadFileProcessor;

    @Override
    public String update(MultipartFormDataInput input) {
        for(String key: input.getFormDataMap().keySet()) {
            BackendBulkLoadType type = BackendBulkLoadType.valueOf(key);
            bulkLoadFileProcessor.processBulkManualLoad(input, type); 
        }

        return "OK";
    }
    
}
