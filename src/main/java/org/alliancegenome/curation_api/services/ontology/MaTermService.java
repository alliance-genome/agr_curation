package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.MaTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;

@RequestScoped
public class MaTermService extends BaseOntologyTermService<MATerm, MaTermDAO> {

    @Inject MaTermDAO maTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(maTermDAO);
    }
    
}
