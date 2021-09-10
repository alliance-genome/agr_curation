package org.alliancegenome.curation_api.base;

import javax.transaction.Transactional;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public abstract class BaseOntologyTermService<E extends OntologyTerm, D extends BaseDAO<E>> extends BaseService<E, BaseDAO<E>> {

    @Transactional
    public E processUpdate(E inTerm) {

        E term = dao.find(inTerm.getCurie());

        if(term == null) {
            term = dao.getNewInstance();
            term.setCurie(inTerm.getCurie());
        }
        
        term.setName(inTerm.getName());
        term.setType(inTerm.getType());
        term.setObsolete(inTerm.getObsolete());
        term.setNamespace(inTerm.getNamespace());
        term.setDefinition(inTerm.getDefinition());
        
        term.setSubsets(inTerm.getSubsets());
        term.setDefinitionUrls(inTerm.getDefinitionUrls());
        term.setSecondaryIdentifiers(inTerm.getSecondaryIdentifiers());
        term.setSynonyms(inTerm.getSynonyms());

        dao.persist(term);

        return term;
    }
}
