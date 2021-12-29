package org.alliancegenome.curation_api.controllers.crud.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.interfaces.crud.loads.BulkLoadFileCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.BulkLoadFileService;

@RequestScoped
public class BulkLoadFileCrudController extends BaseCrudController<BulkLoadFileService, BulkLoadFile, BulkLoadFileDAO> implements BulkLoadFileCrudInterface {

    @Inject BulkLoadFileService bulkLoadFileService;

    @Override
    @PostConstruct
    protected void init() {
        setService(bulkLoadFileService);
    }
    
    @Override
    public ObjectResponse<BulkLoadFile> restartLoadFile(Long id) {
        return bulkLoadFileService.restartLoad(id);
    }

}
