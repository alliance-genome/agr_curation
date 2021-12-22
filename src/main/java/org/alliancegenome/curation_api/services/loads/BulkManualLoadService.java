package org.alliancegenome.curation_api.services.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;

@RequestScoped
public class BulkManualLoadService extends BaseService<BulkManualLoad, BulkManualLoadDAO> {
    
    @Inject
    BulkManualLoadDAO bulkManualLoadDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(bulkManualLoadDAO);
    }
}