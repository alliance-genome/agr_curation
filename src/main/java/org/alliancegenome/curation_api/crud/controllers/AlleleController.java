package org.alliancegenome.curation_api.crud.controllers;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseController;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.interfaces.rest.AlleleRESTInterface;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.services.AlleleService;

@RequestScoped
public class AlleleController extends BaseController<AlleleService, Allele, AlleleDAO> implements AlleleRESTInterface {

    @Inject AlleleService alleleService;

    @Override
    @PostConstruct
    protected void init() {
        setService(alleleService);
    }

}
