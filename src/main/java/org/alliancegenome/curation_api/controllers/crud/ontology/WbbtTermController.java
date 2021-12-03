package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.WbbtTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.WbbtTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.WBbtTerm;
import org.alliancegenome.curation_api.services.ontology.WbbtTermService;

@RequestScoped
public class WbbtTermController extends BaseOntologyTermController<WbbtTermService, WBbtTerm, WbbtTermDAO> implements WbbtTermRESTInterface {

    @Inject WbbtTermService wbbtTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(wbbtTermService);
    }

}
