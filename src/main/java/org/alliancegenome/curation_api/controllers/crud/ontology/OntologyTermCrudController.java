package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.OntologyTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.OntologyTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.ontology.OntologyTermService;

@RequestScoped
public class OntologyTermCrudController extends BaseOntologyTermController<OntologyTermService, OntologyTerm, OntologyTermDAO> implements OntologyTermCrudInterface {

    @Inject OntologyTermService ontologyTermService;

    @Override
    @PostConstruct
    public void init() {
        setService(ontologyTermService, OntologyTerm.class);
    }

}
