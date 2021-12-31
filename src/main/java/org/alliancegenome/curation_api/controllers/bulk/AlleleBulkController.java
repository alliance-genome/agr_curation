package org.alliancegenome.curation_api.controllers.bulk;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.interfaces.bulk.AlleleBulkInterface;
import org.alliancegenome.curation_api.jobs.BulkLoadFileProcessor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BackendBulkLoadType;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@RequestScoped
public class AlleleBulkController implements AlleleBulkInterface {

    @Inject BulkLoadFileProcessor bulkLoadFileProcessor;

    @Override
    public String updateAlleles(MultipartFormDataInput input) {
        bulkLoadFileProcessor.process(input, BackendBulkLoadType.ALLELE);   
        return "OK";
    }
    
}
