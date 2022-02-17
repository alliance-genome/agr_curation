package org.alliancegenome.curation_api.controllers;

import javax.inject.Inject;

import org.alliancegenome.curation_api.enums.BackendBulkDataType;
import org.alliancegenome.curation_api.interfaces.DQMSubmissionInterface;
import org.alliancegenome.curation_api.jobs.BulkLoadManualProcessor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BackendBulkLoadType;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

public class DQMSubmissionController implements DQMSubmissionInterface {

    @Inject BulkLoadManualProcessor bulkLoadManualProcessor;

    @Override
    public String update(MultipartFormDataInput input) {
        for(String key: input.getFormDataMap().keySet()) {
            String separator = "_";
            int sepPos = key.lastIndexOf(separator);
            BackendBulkLoadType loadType = BackendBulkLoadType.valueOf(key.substring(0,sepPos));
            BackendBulkDataType dataType = BackendBulkDataType.valueOf(key.substring(sepPos + 1));
            if(loadType == null || dataType == null) {
                return "FAIL";
            } else {
                bulkLoadManualProcessor.processBulkManualLoadFromDQM(input, loadType, dataType);
            }
        }

        return "OK";
    }
    
}
