package org.alliancegenome.curation_api.services;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.*;
import org.alliancegenome.curation_api.exceptions.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.DtoConverterHelper;
import org.alliancegenome.curation_api.services.helpers.validators.GeneValidator;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneService extends BaseCrudService<Gene, GeneDAO> {

    @Inject
    GeneDAO geneDAO;
    @Inject
    CrossReferenceDAO crossReferenceDAO;
    @Inject
    CrossReferenceService crossReferenceService;
    @Inject
    SynonymService synonymService;
    @Inject
    SoTermDAO soTermDAO;
    @Inject
    GeneValidator geneValidator;
    @Inject
    NcbiTaxonTermService ncbiTaxonTermService;
    @Inject 
    NcbiTaxonTermDAO ncbiTaxonTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(geneDAO);
    }

    @Override
    @Transactional
    public ObjectResponse<Gene> update(Gene uiEntity) {
        Gene dbEntity = geneValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<Gene>(geneDAO.persist(dbEntity));
    }

    @Transactional
    public Gene getByIdOrCurie(String id) {
        Gene gene = geneDAO.getByIdOrCurie(id);
        if (gene != null) {
            gene.getSynonyms().size();
            gene.getSecondaryIdentifiers().size();
        }
        return gene;
    }

    @Transactional
    public void processUpdate(GeneFmsDTO gene) throws ObjectUpdateException {
        //log.info("processUpdate Gene: ");

        validateGeneDTO(gene);
        
        Gene g = geneDAO.find(gene.getBasicGeneticEntity().getPrimaryId());

        if (g == null) {
            g = new Gene();
            g.setCurie(gene.getBasicGeneticEntity().getPrimaryId());

            handleNewSynonyms(gene, g);
        } else {
            handleUpdateSynonyms(gene, g);
        }

        g.setGeneSynopsis(gene.getGeneSynopsis());
        g.setGeneSynopsisURL(gene.getGeneSynopsisUrl());

        g.setSymbol(gene.getSymbol());
        g.setName(gene.getName());
        
        g.setTaxon(ncbiTaxonTermDAO.find(gene.getBasicGeneticEntity().getTaxonId()));
        g.setGeneType(soTermDAO.find(gene.getSoTermId()));

        handleCrossReferences(gene, g);
        handleSecondaryIds(gene, g);

        geneDAO.persist(g);

    }

    private void handleSecondaryIds(GeneFmsDTO geneFmsDTO, Gene gene) {
        Set<String> currentIds;
        if (gene.getSecondaryIdentifiers() == null) {
            currentIds = new HashSet<>();
            gene.setSecondaryIdentifiers(new ArrayList<>());
        } else {
            currentIds = gene.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
        }

        Set<String> newIds;
        if (geneFmsDTO.getBasicGeneticEntity().getSecondaryIds() == null) {
            newIds = new HashSet<>();
        } else {
            newIds = geneFmsDTO.getBasicGeneticEntity().getSecondaryIds().stream().collect(Collectors.toSet());
        }

        newIds.forEach(id -> {
            if (!currentIds.contains(id)) {
                gene.getSecondaryIdentifiers().add(id);
            }
        });

        currentIds.forEach(id -> {
            if (!newIds.contains(id)) {
                gene.getSecondaryIdentifiers().remove(id);
            }
        });

    }

    private void handleCrossReferences(GeneFmsDTO geneFmsDTO, Gene gene) {
        Map<String, CrossReference> currentIds;
        if (gene.getCrossReferences() == null) {
            currentIds = new HashedMap<>();
            gene.setCrossReferences(new ArrayList<>());
        } else {
            currentIds = gene.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
        }
        Map<String, CrossReferenceFmsDTO> newIds;
        if (geneFmsDTO.getBasicGeneticEntity().getCrossReferences() == null) {
            newIds = new HashedMap<>();
        } else {
            newIds = geneFmsDTO.getBasicGeneticEntity().getCrossReferences().stream().collect(Collectors.toMap(CrossReferenceFmsDTO::getId, Function.identity(),
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
                gene.getCrossReferences().add(crossReferenceService.processUpdate(v));
            }
        });

        currentIds.forEach((k, v) -> {
            if (!newIds.containsKey(k)) {
                gene.getCrossReferences().remove(v);
            }
        });

    }

    private void handleNewSynonyms(GeneFmsDTO gene, Gene g) {
        if (CollectionUtils.isNotEmpty(gene.getBasicGeneticEntity().getSynonyms())) {
            List<Synonym> synonyms = DtoConverterHelper.getSynonyms(gene);
            synonyms.forEach(synonym -> synonymService.create(synonym));
            g.setSynonyms(synonyms);
        }
    }

    private void handleUpdateSynonyms(GeneFmsDTO geneFmsDTO, Gene gene) {
        if (CollectionUtils.isNotEmpty(geneFmsDTO.getBasicGeneticEntity().getSynonyms())) {
            List<Synonym> newSynonyms = DtoConverterHelper.getSynonyms(geneFmsDTO);

            List<Synonym> existingSynonyms = gene.getSynonyms();

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
            gene.getSynonyms().forEach(synonym -> synonymService.delete(synonym.getId()));
            gene.setSynonyms(new ArrayList<>());
        }
    }

    private void validateGeneDTO(GeneFmsDTO dto) throws ObjectValidationException {
        // TODO: replace regex method with DB lookup for taxon ID once taxons are loaded

        // Check for required fields
        if (dto.getBasicGeneticEntity().getPrimaryId() == null || dto.getBasicGeneticEntity().getTaxonId() == null) {
            throw new ObjectValidationException(dto, "Entry for gene " + dto.getBasicGeneticEntity().getPrimaryId() + " missing required fields - skipping");
        }

        String soTermId = dto.getSoTermId();
        if (soTermId != null) {
            SOTerm soTerm = soTermDAO.find(soTermId);
            if (soTerm == null) {
                throw new ObjectValidationException(dto, "Entry for gene " + dto.getBasicGeneticEntity().getPrimaryId() + " references an unknown SOTerm (" + soTermId + " - skipping");
            }
        }

        // Validate taxon ID
        ObjectResponse<NCBITaxonTerm> taxon = ncbiTaxonTermService.get(dto.getBasicGeneticEntity().getTaxonId());
        if (taxon.getEntity() == null) {
            throw new ObjectValidationException(dto, "Invalid taxon ID for gene " + dto.getBasicGeneticEntity().getPrimaryId() + " - skipping");
        }
        
        // Validate xrefs
        if (dto.getBasicGeneticEntity().getCrossReferences() != null) {
            for (CrossReferenceFmsDTO xrefDTO : dto.getBasicGeneticEntity().getCrossReferences()) {
                if (xrefDTO.getId() == null) {
                    throw new ObjectValidationException(dto, "Missing xref ID for gene " + dto.getBasicGeneticEntity().getPrimaryId() + " - skipping");
                }
            }
        }

        // Check any genome positions have valid start/end/strand
        if (CollectionUtils.isNotEmpty(dto.getBasicGeneticEntity().getGenomeLocations())) {
            for (GenomeLocationsFmsDTO location : dto.getBasicGeneticEntity().getGenomeLocations()) {
                if (location.getStartPosition() != null && location.getEndPosition() != null && location.getStartPosition().intValue() > location.getEndPosition().intValue()) {
                    throw new ObjectValidationException(dto, "Start position is downstream of end position for gene" + dto.getBasicGeneticEntity().getPrimaryId() + " - skipping");
                }
                if (location.getStrand() != null && !location.getStrand().equals("+") && !location.getStrand().equals("-") && !location.getStrand().equals(".")) {
                    throw new ObjectValidationException(dto, "Invalid strand specified for " + dto.getBasicGeneticEntity().getPrimaryId() + " - skipping");
                }
            }
        }

    }

}
