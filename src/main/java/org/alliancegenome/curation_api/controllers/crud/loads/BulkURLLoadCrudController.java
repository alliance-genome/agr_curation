package org.alliancegenome.curation_api.controllers.crud.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkURLLoadDAO;
import org.alliancegenome.curation_api.interfaces.crud.loads.BulkURLLoadCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.BulkURLLoadService;

@RequestScoped
public class BulkURLLoadCrudController extends BaseCrudController<BulkURLLoadService, BulkURLLoad, BulkURLLoadDAO> implements BulkURLLoadCrudInterface {

    @Inject BulkURLLoadService bulkURLLoadService;

    @Override
    @PostConstruct
    protected void init() {
        setService(bulkURLLoadService);
    }
    
    @Override
    public ObjectResponse<BulkURLLoad> restartLoad(Long id) {
        return bulkURLLoadService.restartLoad(id);
    }
}
