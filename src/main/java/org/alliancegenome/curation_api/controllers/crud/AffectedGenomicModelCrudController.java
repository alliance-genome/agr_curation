package org.alliancegenome.curation_api.controllers.crud;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.interfaces.crud.AffectedGenomicModelCrudInterface;
import org.alliancegenome.curation_api.jobs.executors.AgmExecutor;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;

@RequestScoped
public class AffectedGenomicModelCrudController extends BaseCrudController<AffectedGenomicModelService, AffectedGenomicModel, AffectedGenomicModelDAO> implements AffectedGenomicModelCrudInterface {

    @Inject AffectedGenomicModelService affectedGenomicModelService;

    @Inject AgmExecutor agmExecutor;
    
    @Override
    @PostConstruct
    protected void init() {
        setService(affectedGenomicModelService);
    }

    @Override
    public APIResponse updateAGMs(List<AffectedGenomicModelDTO> agmData) {
        return agmExecutor.runLoad(agmData);
    }

}
