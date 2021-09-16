package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;

@RequestScoped
public class EcoTermService extends BaseOntologyTermService<EcoTerm, EcoTermDAO> {
    
    @Inject EcoTermDAO ecoTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(ecoTermDAO);
    }
    
}
