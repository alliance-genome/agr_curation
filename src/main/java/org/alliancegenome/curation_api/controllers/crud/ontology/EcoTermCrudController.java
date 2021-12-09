package org.alliancegenome.curation_api.controllers.crud.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.EcoTermCrudInterface;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;

@RequestScoped
public class EcoTermCrudController extends BaseOntologyTermController<EcoTermService, EcoTerm, EcoTermDAO> implements EcoTermCrudInterface {

    @Inject EcoTermService ecoTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(ecoTermService);
    }

}
