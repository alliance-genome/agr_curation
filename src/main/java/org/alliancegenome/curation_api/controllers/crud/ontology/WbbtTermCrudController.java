package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.WbbtTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.WbbtTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.WBbtTerm;
import org.alliancegenome.curation_api.services.ontology.WbbtTermService;

@RequestScoped
public class WbbtTermCrudController extends BaseOntologyTermController<WbbtTermService, WBbtTerm, WbbtTermDAO> implements WbbtTermCrudInterface {

    @Inject WbbtTermService wbbtTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(wbbtTermService, WBbtTerm.class);
    }

}
