package org.alliancegenome.curation_api.crud.controllers;
import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.interfaces.rest.CrossReferenceRESTInterface;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.services.CrossReferenceService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CrossReferenceController extends BaseController<CrossReferenceService, CrossReference, CrossReferenceDAO> implements CrossReferenceRESTInterface {

    @Inject CrossReferenceService crossReferenceService;


    @Override
    @PostConstruct
    protected void init() {
        setService(crossReferenceService);
    }
}
