package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.WbbtTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBbtTerm;

@RequestScoped
public class WbbtTermService extends BaseOntologyTermService<WBbtTerm, WbbtTermDAO> {

    @Inject WbbtTermDAO wbbtTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(wbbtTermDAO);
    }
    
}
