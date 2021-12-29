package org.alliancegenome.curation_api.controllers.crud.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkFMSLoadDAO;
import org.alliancegenome.curation_api.interfaces.crud.loads.BulkFMSLoadCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.BulkFMSLoadService;

@RequestScoped
public class BulkFMSLoadCrudController extends BaseCrudController<BulkFMSLoadService, BulkFMSLoad, BulkFMSLoadDAO> implements BulkFMSLoadCrudInterface {

    @Inject BulkFMSLoadService bulkFMSLoadService;

    @Override
    @PostConstruct
    protected void init() {
        setService(bulkFMSLoadService);
    }

    @Override
    public ObjectResponse<BulkFMSLoad> restartLoad(Long id) {
        return bulkFMSLoadService.restartLoad(id);
    }

}
