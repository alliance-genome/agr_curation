package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.json.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.GeneDTO;
import org.alliancegenome.curation_api.services.helpers.DtoConverterHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;

@RequestScoped
public class AffectedGenomicModelService extends BaseCrudService<AffectedGenomicModel, AffectedGenomicModelDAO> {

    @Inject
    AffectedGenomicModelDAO affectedGenomicModelDAO;
    @Inject
    CrossReferenceService crossReferenceService;
    @Inject
    SynonymService synonymService;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(affectedGenomicModelDAO);
    }

    @Transactional
    public void processUpdate(AffectedGenomicModelDTO agm) {

        AffectedGenomicModel dbAgm = affectedGenomicModelDAO.find(agm.getPrimaryID());
        
        if(dbAgm == null) {
            dbAgm = new AffectedGenomicModel();
            dbAgm.setCurie(agm.getPrimaryID());
            
            handleNewSynonyms(agm, dbAgm);
            
        } else {
            handleUpdateSynonyms(agm, dbAgm);
        }
        
        dbAgm.setName(agm.getName().substring(0, Math.min(agm.getName().length(), 254)));
        dbAgm.setTaxon(agm.getTaxonId());
        dbAgm.setSubtype(agm.getSubtype());
        
        handleCrossReference(agm, dbAgm);
        handleSecondaryIds(agm, dbAgm);
        
        affectedGenomicModelDAO.persist(dbAgm);
    }
    
    private void handleCrossReference(AffectedGenomicModelDTO agm, AffectedGenomicModel dbAgm) {
        Map<String, CrossReference> currentIds;
        if(dbAgm.getCrossReferences() == null) {
            currentIds = new HashedMap<>();
            dbAgm.setCrossReferences(new ArrayList<>());
        } else {
            currentIds = dbAgm.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
        }
        
        Map<String, CrossReferenceDTO> newIds = new HashedMap<>();
        if(agm.getCrossReference() != null) {   
            CrossReferenceDTO newCr = new CrossReferenceDTO();
            HashSet<String> pageAreas = new HashSet<>();
            newCr.setId(agm.getCrossReference().getId());
            if (agm.getCrossReference().getPages() != null) pageAreas.addAll(agm.getCrossReference().getPages());
            newCr.setPages(new ArrayList<>(pageAreas));
            newIds.put(newCr.getId(), newCr);
        }
        
        newIds.forEach((k, v) -> {
            if(!currentIds.containsKey(k)) {
                dbAgm.getCrossReferences().add(crossReferenceService.processUpdate(v));
            }
        });
        
        currentIds.forEach((k, v) -> {
            if(!newIds.containsKey(k)) {
                dbAgm.getCrossReferences().remove(v);
            }
        });

    }
    
    private void handleNewSynonyms(AffectedGenomicModelDTO agm, AffectedGenomicModel dbAgm) {
        if (CollectionUtils.isNotEmpty(agm.getSynonyms())) {
            List<Synonym> synonyms = DtoConverterHelper.getSynonyms(agm);
            synonyms.forEach(synonym -> synonymService.create(synonym));
            dbAgm.setSynonyms(synonyms);
        }
    }

    private void handleUpdateSynonyms(AffectedGenomicModelDTO agm, AffectedGenomicModel dbAgm) {
        if (CollectionUtils.isNotEmpty(agm.getSynonyms())) {
            List<Synonym> newSynonyms = DtoConverterHelper.getSynonyms(agm);

            List<Synonym> existingSynonyms = dbAgm.getSynonyms();

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
                    synonym.setGenomicEntities(List.of(dbAgm));
                    synonymService.create(synonym);
                });
                existingSynonyms.addAll(newCollect);
            }
        } else {
            // remove all existing synonyms if there are no incoming synonyms
            dbAgm.getSynonyms().forEach(synonym -> synonymService.delete(synonym.getId()));
            dbAgm.setSynonyms(new ArrayList<>());
        }
    }
    
    private void handleSecondaryIds(AffectedGenomicModelDTO agm, AffectedGenomicModel dbAgm) {
        Set<String> currentIds;
        if(dbAgm.getSecondaryIdentifiers() == null) {
            currentIds = new HashSet<>();
            dbAgm.setSecondaryIdentifiers(new ArrayList<>());
        } else {
            currentIds = dbAgm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
        }
        
        Set<String> newIds;
        if(agm.getSecondaryIds() == null) {
            newIds = new HashSet<>();
        } else {
            newIds = agm.getSecondaryIds().stream().collect(Collectors.toSet());
        }
        
        newIds.forEach(id -> {
            if(!currentIds.contains(id)) {
                dbAgm.getSecondaryIdentifiers().add(id);
            }
        });
        
        currentIds.forEach(id -> {
            if(!newIds.contains(id)) {
                dbAgm.getSecondaryIdentifiers().remove(id);
            }
        });

    }
    
}
