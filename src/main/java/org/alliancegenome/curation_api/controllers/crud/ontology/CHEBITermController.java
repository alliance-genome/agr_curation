package org.alliancegenome.curation_api.controllers.crud.ontology;

import org.alliancegenome.curation_api.base.BaseOntologyTermController;
import org.alliancegenome.curation_api.dao.ontology.CHEBITermDAO;
import org.alliancegenome.curation_api.interfaces.crud.ontology.CHEBITermRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.services.ontology.CHEBITermService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CHEBITermController extends BaseOntologyTermController<CHEBITermService, CHEBITerm, CHEBITermDAO> implements CHEBITermRESTInterface {
    @Inject
    CHEBITermService chebiTermService;

    @Override
    @PostConstruct
    protected void init() {
        setService(chebiTermService);
    }
}
