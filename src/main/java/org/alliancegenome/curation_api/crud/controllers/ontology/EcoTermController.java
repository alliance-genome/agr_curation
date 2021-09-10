package org.alliancegenome.curation_api.crud.controllers.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.interfaces.rest.EcoTermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.services.ontology.EcoTermService;

@RequestScoped
public class EcoTermController extends BaseOntologyTermController<EcoTermService, ECOTerm, EcoTermDAO> implements EcoTermRESTInterface {

    @Inject EcoTermService ecoTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(ecoTermService);
    }

}
