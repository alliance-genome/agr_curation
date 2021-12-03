package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.GoTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.services.ontology.GoTermService;

@RequestScoped
public class GoTermController extends BaseOntologyTermController<GoTermService, GOTerm, GoTermDAO> implements GoTermRESTInterface {

    @Inject GoTermService goTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(goTermService);
    }

}
