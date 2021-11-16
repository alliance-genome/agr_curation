package org.alliancegenome.curation_api.services;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.apache.commons.collections4.map.HashedMap;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class MoleculeService extends BaseService<Molecule, MoleculeDAO> {

    @Inject
    MoleculeDAO moleculeDAO;
    
    @Inject
    CrossReferenceService crossReferenceService;
    @Inject
    SynonymService synonymService;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(moleculeDAO);
    }
    
    @Transactional
    public Molecule getByIdOrCurie(String id) {
        Molecule molecule = moleculeDAO.getByIdOrCurie(id);
        if (molecule != null) {
            molecule.getSynonyms().size();
        }
        return molecule;
    }
    
    
    @Transactional
    public void processUpdate(MoleculeDTO molecule) {
        log.info("processUpdate Molecule: ");
	
        Molecule m = moleculeDAO.find(molecule.getId());
	
        if (molecule.getId().startsWith("CHEBI:")) {
	    log.info("Skipping processing of " + molecule.getId());
	    return;
        }
        if (m == null) {
            m = new Molecule();
            m.setCurie(molecule.getId());
        }
        
        m.setName(molecule.getName());
        m.setInchi(molecule.getInchi());
        m.setInchiKey(molecule.getInchikey());
        m.setIupac(molecule.getIupac());
        m.setFormula(molecule.getFormula());
        m.setSmiles(molecule.getSmiles());
        m.setSynonyms(molecule.getSynonyms());
	
        moleculeDAO.persist(m);
	
        handleCrossReferences(molecule, m);    
	
    }
    
    
    private void handleCrossReferences(MoleculeDTO moleculeDTO, Molecule molecule) {
        Map<String, CrossReference> currentIds;
        if(molecule.getCrossReferences() == null) {
            currentIds = new HashedMap<>();
            molecule.setCrossReferences(new ArrayList<>());
        } else {
            currentIds = molecule.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
        }
        Map<String, CrossReferenceDTO> newIds;
        if(moleculeDTO.getCrossReferences() == null) {
            newIds = new HashedMap<>();
        } else {
            newIds = moleculeDTO.getCrossReferences().stream().collect(Collectors.toMap(CrossReferenceDTO::getId, Function.identity(),
                    (cr1, cr2) -> {
                        HashSet<String> pageAreas = new HashSet<>();
                        if(cr1.getPages() != null) pageAreas.addAll(cr1.getPages());
                        if(cr2.getPages() != null) pageAreas.addAll(cr2.getPages());
                        CrossReferenceDTO newCr = new CrossReferenceDTO();
                        newCr.setId(cr2.getId());
                        newCr.setPages(new ArrayList<>(pageAreas));
                        return newCr;
                    }
            ));
        }
        
        newIds.forEach((k, v) -> {
            if(!currentIds.containsKey(k)) {
                molecule.getCrossReferences().add(crossReferenceService.processUpdate(v));
            }
        });
        
        currentIds.forEach((k, v) -> {
            if(!newIds.containsKey(k)) {
                molecule.getCrossReferences().remove(v);
            }
        });

    }

}
