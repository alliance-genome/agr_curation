package org.alliancegenome.curation_api.controllers.crud.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.interfaces.crud.loads.BulkManualLoadCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.BulkManualLoadService;

@RequestScoped
public class BulkManualLoadCrudController extends BaseCrudController<BulkManualLoadService, BulkManualLoad, BulkManualLoadDAO> implements BulkManualLoadCrudInterface {

    @Inject BulkManualLoadService bulkManualLoadService;

    @Override
    @PostConstruct
    protected void init() {
        setService(bulkManualLoadService);
    }
    
    @Override
    public ObjectResponse<BulkManualLoad> restartLoad(Long id) {
        return bulkManualLoadService.restartLoad(id);
    }
}
