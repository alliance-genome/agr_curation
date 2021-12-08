package org.alliancegenome.curation_api.services.ontology;

import org.alliancegenome.curation_api.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.CHEBITermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CHEBITermService extends BaseOntologyTermService<CHEBITerm, CHEBITermDAO> {
    @Inject
    CHEBITermDAO chebiTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(chebiTermDAO);
    }
}
