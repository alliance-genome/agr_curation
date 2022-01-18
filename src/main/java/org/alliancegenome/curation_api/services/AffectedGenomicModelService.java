package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.json.dto.AffectedGenomicModelComponentDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.services.helpers.DtoConverterHelper;
import org.alliancegenome.curation_api.services.helpers.validators.AffectedGenomicModelValidator;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AffectedGenomicModelService extends BaseCrudService<AffectedGenomicModel, AffectedGenomicModelDAO> {

    @Inject
    AffectedGenomicModelDAO affectedGenomicModelDAO;
    @Inject
    CrossReferenceService crossReferenceService;
    @Inject
    SynonymService synonymService;
    @Inject
    AlleleDAO alleleDAO;
    @Inject
    AffectedGenomicModelValidator affectedGenomicModelValidator;
    @Inject
    NcbiTaxonTermDAO ncbiTaxonTermDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(affectedGenomicModelDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<AffectedGenomicModel> update(AffectedGenomicModel uiEntity) {
        AffectedGenomicModel dbEntity = affectedGenomicModelValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<AffectedGenomicModel>(affectedGenomicModelDAO.persist(dbEntity));
    }
    

    @Transactional
    public void processUpdate(AffectedGenomicModelDTO agm) {
        // TODO: add loading of components
        // TODO: add loading of sequenceTargetingReagents
        // TODO: add loading of parentalPopulations

        if (!validateAffectedGenomicModelDTO(agm)) {
            return;
        }
        
        AffectedGenomicModel dbAgm = affectedGenomicModelDAO.find(agm.getPrimaryID());
        
        if(dbAgm == null) {
            dbAgm = new AffectedGenomicModel();
            dbAgm.setCurie(agm.getPrimaryID());
            
            handleNewSynonyms(agm, dbAgm);
            
        } else {
            handleUpdateSynonyms(agm, dbAgm);
        }
        
        dbAgm.setName(agm.getName().substring(0, Math.min(agm.getName().length(), 254)));
        dbAgm.setTaxon(ncbiTaxonTermDAO.find(agm.getTaxonId()));
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
    
    private boolean validateAffectedGenomicModelDTO(AffectedGenomicModelDTO agm) {
        // TODO: replace regex method with DB lookup for taxon ID once taxons are loaded
        
        // Check for required fields
        if (agm.getPrimaryID() == null || agm.getName() == null || agm.getTaxonId() == null) {
            log.debug("Entry for AGM " + agm.getPrimaryID() + " missing required fields - skipping");
            return false;
        }
        
        // Validate taxon ID
        Pattern taxonIdPattern = Pattern.compile("^NCBITaxon:\\d+$");
        Matcher taxonIdMatcher = taxonIdPattern.matcher(agm.getTaxonId());
        if (!taxonIdMatcher.find()) {
            log.debug("Invalid taxon ID for AGM " + agm.getPrimaryID() + " - skipping");
            return false;
        }
        
        // Validate component fields
        if (CollectionUtils.isNotEmpty(agm.getAffectedGenomicModelComponents())) {
            for (AffectedGenomicModelComponentDTO component : agm.getAffectedGenomicModelComponents()) {
                if (component.getAlleleID() == null) {
                    log.debug("Entry for AGM " + agm.getPrimaryID() + " has component with missing allele - skipping");
                    return false;
                }
                Allele componentAllele = alleleDAO.find(component.getAlleleID());
                if (componentAllele == null) {
                    log.debug("Entry for AGM " + agm.getPrimaryID() + " has component allele (" + component.getAlleleID() + ") not found in database - skipping");
                    return false;
                }
                if (component.getZygosity() == null) {
                    log.debug("Entry for AGM " + agm.getPrimaryID() + " has component allele (" + component.getAlleleID() + ") with missing zygosity - skipping");
                    return false;
                }
                
                if (!validZygosityCodes.contains(component.getZygosity())) {
                    log.debug("Entry for AGM " + agm.getPrimaryID() + " has component allele (" + component.getAlleleID() + ") with invalid zygosity - skipping");
                    return false;
                }
            }
        }
        return true;
    }
    
    private static final Set<String> validZygosityCodes = Set.of(
            "GENO:0000602", "GENO:0000603", "GENO:0000604", "GENO:0000605", "GENO:0000606", "GENO:0000135",
            "GENO:0000136", "GENO:0000137", "GENO:0000134"
        );
}
