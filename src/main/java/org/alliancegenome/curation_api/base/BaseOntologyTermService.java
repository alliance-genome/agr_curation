package org.alliancegenome.curation_api.base;

import java.util.*;
import java.util.stream.Collectors;

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
        
        handleSubsets(term, inTerm);
        handleDefinitionUrls(term, inTerm);
        handleSecondaryIds(term, inTerm);
        handleSynonyms(term, inTerm);

        dao.persist(term);

        return term;
    }
    
    
    
    
    protected void handleDefinitionUrls(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
        Set<String> currentDefinitionUrls;
        if(dbTerm.getDefinitionUrls() == null) {
            currentDefinitionUrls = new HashSet<>();
            dbTerm.setDefinitionUrls(new ArrayList<>());
        } else {
            currentDefinitionUrls = dbTerm.getDefinitionUrls().stream().collect(Collectors.toSet());
        }
        
        Set<String> newDefinitionUrls;
        if(incomingTerm.getDefinitionUrls() == null) {
            newDefinitionUrls = new HashSet<>();
        } else {
            newDefinitionUrls = incomingTerm.getDefinitionUrls().stream().collect(Collectors.toSet());
        }
        
        newDefinitionUrls.forEach(id -> {
            if(!currentDefinitionUrls.contains(id)) {
                dbTerm.getDefinitionUrls().add(id);
            }
        });
        
        currentDefinitionUrls.forEach(id -> {
            if(!newDefinitionUrls.contains(id)) {
                dbTerm.getDefinitionUrls().remove(id);
            }
        });

    }
    
    
    
    
    protected void handleSubsets(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
        Set<String> currentSubsets;
        if(dbTerm.getSubsets() == null) {
            currentSubsets = new HashSet<>();
            dbTerm.setSubsets(new ArrayList<>());
        } else {
            currentSubsets = dbTerm.getSubsets().stream().collect(Collectors.toSet());
        }
        
        Set<String> newSubsets;
        if(incomingTerm.getSubsets() == null) {
            newSubsets = new HashSet<>();
        } else {
            newSubsets = incomingTerm.getSubsets().stream().collect(Collectors.toSet());
        }
        
        newSubsets.forEach(id -> {
            if(!currentSubsets.contains(id)) {
                dbTerm.getSubsets().add(id);
            }
        });
        
        currentSubsets.forEach(id -> {
            if(!newSubsets.contains(id)) {
                dbTerm.getSubsets().remove(id);
            }
        });

    }
    
    protected void handleSynonyms(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
        Set<String> currentSynonyms;
        if(dbTerm.getSynonyms() == null) {
            currentSynonyms = new HashSet<>();
            dbTerm.setSynonyms(new ArrayList<>());
        } else {
            currentSynonyms = dbTerm.getSynonyms().stream().collect(Collectors.toSet());
        }
        
        Set<String> newSynonyms;
        if(incomingTerm.getSynonyms() == null) {
            newSynonyms = new HashSet<>();
        } else {
            newSynonyms = incomingTerm.getSynonyms().stream().collect(Collectors.toSet());
        }
        
        newSynonyms.forEach(id -> {
            if(!currentSynonyms.contains(id)) {
                dbTerm.getSynonyms().add(id);
            }
        });
        
        currentSynonyms.forEach(id -> {
            if(!newSynonyms.contains(id)) {
                dbTerm.getSynonyms().remove(id);
            }
        });

    }
    
    
    protected void handleSecondaryIds(OntologyTerm dbTerm, OntologyTerm incomingTerm) {
        Set<String> currentIds;
        if(dbTerm.getSecondaryIdentifiers() == null) {
            currentIds = new HashSet<>();
            dbTerm.setSecondaryIdentifiers(new ArrayList<>());
        } else {
            currentIds = dbTerm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
        }
        
        Set<String> newIds;
        if(incomingTerm.getSecondaryIdentifiers() == null) {
            newIds = new HashSet<>();
        } else {
            newIds = incomingTerm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
        }
        
        newIds.forEach(id -> {
            if(!currentIds.contains(id)) {
                dbTerm.getSecondaryIdentifiers().add(id);
            }
        });
        
        currentIds.forEach(id -> {
            if(!newIds.contains(id)) {
                dbTerm.getSecondaryIdentifiers().remove(id);
            }
        });

    }
}
