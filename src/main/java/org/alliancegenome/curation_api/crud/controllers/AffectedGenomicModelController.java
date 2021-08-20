package org.alliancegenome.curation_api.crud.controllers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.interfaces.rest.AffectedGenomicModelRESTInterface;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;

@RequestScoped
public class AffectedGenomicModelController extends BaseController<AffectedGenomicModelService, AffectedGenomicModel, AffectedGenomicModelDAO> implements AffectedGenomicModelRESTInterface {

    @Inject AffectedGenomicModelService affectedGenomicModelService;

    @Override
    @PostConstruct
    protected void init() {
        setService(affectedGenomicModelService);
    }

}
