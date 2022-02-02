package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.ChemicalTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class ChemicalTermService extends BaseOntologyTermService<ChemicalTerm, ChemicalTermDAO> {

    @Inject ChemicalTermDAO chemicalTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(chemicalTermDAO);
    }
    
}
