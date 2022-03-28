package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.fasterxml.jackson.annotation.JsonView;
import org.alliancegenome.curation_api.auth.Secured;
import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.interfaces.crud.AffectedGenomicModelCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AgmFmsExecutor;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AffectedGenomicModelMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.view.View;

@RequestScoped
public class AffectedGenomicModelCrudController extends BaseCrudController<AffectedGenomicModelService, AffectedGenomicModel, AffectedGenomicModelDAO> implements AffectedGenomicModelCrudInterface {

    @Inject AffectedGenomicModelService affectedGenomicModelService;

    @Inject AgmFmsExecutor agmFmsExecutor;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(affectedGenomicModelService);
    }

    @Override
    public APIResponse updateAGMs(AffectedGenomicModelMetaDataFmsDTO agmData) {
        return agmFmsExecutor.runLoad(agmData);
    }

}
