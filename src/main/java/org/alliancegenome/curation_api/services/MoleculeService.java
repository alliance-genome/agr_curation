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
import org.alliancegenome.curation_api.services.helpers.DtoConverterHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class MoleculeService extends BaseService<Molecule, MoleculeDAO> {

    @Inject
    MoleculeDAO moleculeDAO;
    @Inject
    CrossReferenceDAO crossReferenceDAO;
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
            molecule.getSecondaryIdentifiers().size();
        }
        return molecule;
    }

    
    @Transactional
    public void processUpdate(MoleculeDTO molecule) {
        //log.info("processUpdate Molecule: ");

        Molecule m = moleculeDAO.find(molecule.getBasicGeneticEntity().getPrimaryId());

        if (m == null) {
            m = new Molecule();
            m.setCurie(molecule.getPrimaryId());
            handleNewSynonyms(molecule, m);
        } else {
        	handleUpdateSynonyms(molecule,m);
        }

        m.setName(molecule.getName());
        m.setInchi(molecule.getInchi());
        m.setInchiKey(molecule.getInchiKey());
        m.setIupac(molecule.getIupac());
        m.setFormula(molecule.getFormula());
        m.setSmiles(molecule.getSmiles());
        
        handleCrossReferences(molecule, m);
    
        moleculeDAO.persist(m);

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
    
    private void handleNewSynonyms(MoleculeDTO molecule, Molecule m) {
        if (CollectionUtils.isNotEmpty(molecule.getBasicGeneticEntity().getSynonyms())) {
            List<Synonym> synonyms = DtoConverterHelper.getSynonyms(molecule);
            synonyms.forEach(synonym -> synonymService.create(synonym));
            m.setSynonyms(synonyms);
        }
    }

    private void handleUpdateSynonyms(MoleculeDTO moleculeDTO, Molecule molecule) {
        if (CollectionUtils.isNotEmpty(moleculeDTO.getSynonyms())) {
            List<Synonym> newSynonyms = DtoConverterHelper.getSynonyms(moleculeDTO);

            List<Synonym> existingSynonyms = molecule.getSynonyms();

            // remove synonyms that are not found in the new synonym list
            if (CollectionUtils.isNotEmpty(existingSynonyms)) {
                List<String> existingSynonymStrings = existingSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
                List<Long> removeSynIDs = existingSynonyms.stream()
                        .filter(synonym -> !existingSynonymStrings.contains(synonym.getName()))
                        .map(Synonym::getId)
                        .collect(Collectors.toList());
                removeSynIDs.forEach(id -> synonymService.delete(id));
                existingSynonyms.removeIf(synonym -> newSynonyms.stream().noneMatch(synonym1 -> synonym1.getName().equals(synonym.getName())));
            }
            // add new synonyms that are not found in the existing synonym list
            if (existingSynonyms != null) {
                List<String> existingSynonymStrings = existingSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
                final List<Synonym> newCollect = newSynonyms.stream().filter(synonym -> !existingSynonymStrings.contains(synonym.getName())).collect(Collectors.toList());
                newCollect.forEach(synonym -> {
                    synonym.setGenomicEntities(List.of(gene));
                    synonymService.create(synonym);
                });
                existingSynonyms.addAll(newCollect);
            }
        } else {
            // remove all existing synonyms if there are no incoming synonyms
            molecule.getSynonyms().forEach(synonym -> synonymService.delete(synonym.getId()));
            molecule.setSynonyms(new ArrayList<>());
        }
    }


}
