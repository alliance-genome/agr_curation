package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.MmusdvTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MmusDvTerm;

@RequestScoped
public class MmusdvTermService extends BaseOntologyTermService<MmusDvTerm, MmusdvTermDAO> {

    @Inject MmusdvTermDAO mmusdvTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(mmusdvTermDAO);
    }
    
}
