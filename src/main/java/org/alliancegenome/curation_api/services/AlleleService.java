package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AlleleFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AlleleObjectRelationsFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.DtoConverterHelper;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleValidator;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@JBossLog
@RequestScoped
public class AlleleService extends BaseCrudService<Allele, AlleleDAO> {

    @Inject AlleleDAO alleleDAO;
    @Inject AlleleValidator alleleValidator;
    @Inject GeneDAO geneDAO;
    @Inject CrossReferenceService crossReferenceService;
    @Inject SynonymService synonymService;
    @Inject NcbiTaxonTermService ncbiTaxonTermService;
    @Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(alleleDAO);
    }

    @Override
    @Transactional
    public ObjectResponse<Allele> update(Allele uiEntity) {
        Allele dbEntity = alleleValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<Allele>(alleleDAO.persist(dbEntity));
    }

    @Transactional
    public void processUpdate(AlleleFmsDTO allele) {
        if (!validateAlleleDTO(allele)) {
            return;
        }

        Allele dbAllele = alleleDAO.find(allele.getPrimaryId());
        //log.info("Allele: " + allele + " : " + alleleDTO.getPrimaryId());
        if (dbAllele == null) {
            dbAllele = new Allele();
            dbAllele.setCurie(allele.getPrimaryId());

            handleNewSynonyms(allele, dbAllele);
        } else {
            handleUpdateSynonyms(allele, dbAllele);
        }

        dbAllele.setSymbol(allele.getSymbol());
        dbAllele.setDescription(allele.getDescription());
        dbAllele.setTaxon(ncbiTaxonTermDAO.find(allele.getTaxonId()));
        
        handleCrossReferences(allele, dbAllele);
        handleSecondaryIds(allele, dbAllele);

        alleleDAO.persist(dbAllele);

    }

    private void handleCrossReferences(AlleleFmsDTO allele, Allele dbAllele) {
        Map<String, CrossReference> currentIds;
        if (dbAllele.getCrossReferences() == null) {
            currentIds = new HashedMap<>();
            dbAllele.setCrossReferences(new ArrayList<>());
        } else {
            currentIds = dbAllele.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
        }

        Map<String, CrossReferenceFmsDTO> newIds;
        if (allele.getCrossReferences() == null) {
            newIds = new HashedMap<>();
        } else {
            newIds = allele.getCrossReferences().stream().collect(Collectors.toMap(CrossReferenceFmsDTO::getId, Function.identity(),
                    (cr1, cr2) -> {
                        HashSet<String> pageAreas = new HashSet<>();
                        if (cr1.getPages() != null) pageAreas.addAll(cr1.getPages());
                        if (cr2.getPages() != null) pageAreas.addAll(cr2.getPages());
                        CrossReferenceFmsDTO newCr = new CrossReferenceFmsDTO();
                        newCr.setId(cr2.getId());
                        newCr.setPages(new ArrayList<>(pageAreas));
                        return newCr;
                    }
            ));
        }

        newIds.forEach((k, v) -> {
            if (!currentIds.containsKey(k)) {
                dbAllele.getCrossReferences().add(crossReferenceService.processUpdate(v));
            }
        });

        currentIds.forEach((k, v) -> {
            if (!newIds.containsKey(k)) {
                dbAllele.getCrossReferences().remove(v);
            }
        });

    }

    private void handleNewSynonyms(AlleleFmsDTO allele, Allele dbAllele) {
        if (CollectionUtils.isNotEmpty(allele.getSynonyms())) {
            List<Synonym> synonyms = DtoConverterHelper.getSynonyms(allele);
            synonyms.forEach(synonym -> synonymService.create(synonym));
            dbAllele.setSynonyms(synonyms);
        }
    }

    private void handleUpdateSynonyms(AlleleFmsDTO allele, Allele dbAllele) {
        if (CollectionUtils.isNotEmpty(allele.getSynonyms())) {
            List<Synonym> newSynonyms = DtoConverterHelper.getSynonyms(allele);

            List<Synonym> existingSynonyms = dbAllele.getSynonyms();

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
                    synonym.setGenomicEntities(List.of(dbAllele));
                    synonymService.create(synonym);
                });
                existingSynonyms.addAll(newCollect);
            }
        } else {
            // remove all existing synonyms if there are no incoming synonyms
            dbAllele.getSynonyms().forEach(synonym -> synonymService.delete(synonym.getId()));
            dbAllele.setSynonyms(new ArrayList<>());
        }
    }

    private void handleSecondaryIds(AlleleFmsDTO allele, Allele dbAllele) {
        Set<String> currentIds;
        if (dbAllele.getSecondaryIdentifiers() == null) {
            currentIds = new HashSet<>();
            dbAllele.setSecondaryIdentifiers(new ArrayList<>());
        } else {
            currentIds = dbAllele.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
        }

        Set<String> newIds;
        if (allele.getSecondaryIds() == null) {
            newIds = new HashSet<>();
        } else {
            newIds = allele.getSecondaryIds().stream().collect(Collectors.toSet());
        }

        newIds.forEach(id -> {
            if (!currentIds.contains(id)) {
                dbAllele.getSecondaryIdentifiers().add(id);
            }
        });

        currentIds.forEach(id -> {
            if (!newIds.contains(id)) {
                dbAllele.getSecondaryIdentifiers().remove(id);
            }
        });

    }

    private boolean validateAlleleDTO(AlleleFmsDTO allele) {
        // TODO: replace regex method with DB lookup for taxon ID once taxons are loaded

        // Check for required fields
        if (allele.getPrimaryId() == null || allele.getSymbol() == null
                || allele.getSymbolText() == null || allele.getTaxonId() == null) {
            log.debug("Entry for allele " + allele.getPrimaryId() + " missing required fields - skipping");
            return false;
        }

        // Validate taxon ID
        ObjectResponse<NCBITaxonTerm> taxon = ncbiTaxonTermService.get(allele.getTaxonId());
        if (taxon.getEntity() == null) {
            log.debug("Invalid taxon ID for allele " + allele.getPrimaryId() + " - skipping");
            return false;
        }

        // Validate xrefs
        if (allele.getCrossReferences() != null) {
            for (CrossReferenceFmsDTO xrefDTO : allele.getCrossReferences()) {
                if (xrefDTO.getId() == null) {
                    log.debug("Missing xref ID for allele " + allele.getPrimaryId() + " - skipping");
                    return false;
                }
            }
        }
        
        // Validate allele object relations
        // TODO: validate construct relation once constructs are loaded
        if (CollectionUtils.isNotEmpty(allele.getAlleleObjectRelations())) {
            for (AlleleObjectRelationsFmsDTO objectRelations : allele.getAlleleObjectRelations()) {
                if (objectRelations.getObjectRelation().containsKey("gene")) {
                    Gene gene = geneDAO.find(objectRelations.getObjectRelation().get("gene"));
                    if (gene == null) {
                        log.debug("Related gene not found in database for " + allele.getPrimaryId() + " - skipping");
                        return false;
                    }
                    if (objectRelations.getObjectRelation().containsKey("associationType") &&
                            !objectRelations.getObjectRelation().get("associationType").equals("allele_of")) {
                        log.debug("Invalid association type for related gene of " + allele.getPrimaryId() + " - skipping");
                        return false;
                    }
                }
                if (objectRelations.getObjectRelation().containsKey("construct")) {
                    if (objectRelations.getObjectRelation().containsKey("associationType") &&
                            !objectRelations.getObjectRelation().get("associationType").equals("contains")) {
                        log.debug("Invalid association type for related construct of " + allele.getPrimaryId() + " - skipping");
                        return false;
                    }
                }
                if (!objectRelations.getObjectRelation().containsKey("gene") &&
                        !objectRelations.getObjectRelation().containsKey("construct")) {
                    log.debug("No valid related entity type found in " + allele.getPrimaryId() + " objectRelation - skipping");
                    return false;
                }
                for (String orKey : objectRelations.getObjectRelation().keySet()) {
                    if (!orKey.equals("gene") && !orKey.equals("construct") &&
                            !orKey.equals("associationType")) {
                        log.debug("Invalid key (" + orKey + ") found in " + allele.getPrimaryId() + " objectRelation - skipping");
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
