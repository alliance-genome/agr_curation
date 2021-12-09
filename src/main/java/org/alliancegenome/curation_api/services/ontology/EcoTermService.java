package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

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
    
    @Override
    @Transactional
    public EcoTerm processUpdate (EcoTerm inTerm) {
        EcoTerm term = ecoTermDAO.find(inTerm.getCurie());
        
        if(term == null) {
            term = ecoTermDAO.getNewInstance();
            term.setCurie(inTerm.getCurie());
        }
        
        term.setName(inTerm.getName());
        term.setType(inTerm.getType());
        term.setObsolete(inTerm.getObsolete());
        term.setNamespace(inTerm.getNamespace());
        term.setDefinition(inTerm.getDefinition());
        term.setAbbreviation(inTerm.getAbbreviation());
        
        handleSubsets(term, inTerm);
        handleDefinitionUrls(term, inTerm);
        handleSecondaryIds(term, inTerm);
        handleSynonyms(term, inTerm);

        dao.persist(term);
        
        return term;
    }
    
}
