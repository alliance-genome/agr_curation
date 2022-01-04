package org.alliancegenome.curation_api.controllers;

import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.DQMSubmissionInterface;
import org.alliancegenome.curation_api.jobs.BulkLoadFileProcessor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.*;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

public class DQMSubmissionController implements DQMSubmissionInterface {

    @Inject BulkLoadFileProcessor bulkLoadFileProcessor;

    @Override
    public String update(MultipartFormDataInput input) {
        for(String key: input.getFormDataMap().keySet()) {
            String[] array = key.split("_");
            BackendBulkLoadType loadType = BackendBulkLoadType.valueOf(array[0]);
            BackendBulkDataType dataType = BackendBulkDataType.valueOf(array[1]);
            if(loadType == null || dataType == null) {
                return "FAIL";
            } else {
                bulkLoadFileProcessor.processBulkManualLoadFromDQM(input, loadType, dataType);
            }
        }

        return "OK";
    }
    
}
