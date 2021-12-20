package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseCrudController;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.interfaces.crud.AffectedGenomicModelCrudInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;

@RequestScoped
public class AffectedGenomicModelCrudController extends BaseCrudController<AffectedGenomicModelService, AffectedGenomicModel, AffectedGenomicModelDAO> implements AffectedGenomicModelCrudInterface {

    @Inject AffectedGenomicModelService affectedGenomicModelService;

    @Override
    @PostConstruct
    protected void init() {
        setService(affectedGenomicModelService);
    }

}
