package org.alliancegenome.curation_api.controllers.crud.loads;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.interfaces.crud.bulkloads.BulkLoadFileHistoryCrudInterface;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.loads.*;

@RequestScoped
public class BulkLoadFileHistoryCrudController extends BaseCrudController<BulkLoadFileHistoryService, BulkLoadFileHistory, BulkLoadFileHistoryDAO> implements BulkLoadFileHistoryCrudInterface {

    @Inject BulkLoadFileHistoryService bulkLoadFileHistoryService;

    @Override
    @PostConstruct
    protected void init() {
        setService(bulkLoadFileHistoryService);
    }

}
