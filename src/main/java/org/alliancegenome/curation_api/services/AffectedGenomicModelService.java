package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AffectedGenomicModelComponentFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AffectedGenomicModelFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.DtoConverterHelper;
import org.alliancegenome.curation_api.services.helpers.validators.AffectedGenomicModelValidator;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
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
    CrossReferenceDAO crossReferenceDAO;
    @Inject
    SynonymService synonymService;
    @Inject
    AlleleDAO alleleDAO;
    @Inject
    AffectedGenomicModelValidator affectedGenomicModelValidator;
    @Inject
    NcbiTaxonTermDAO ncbiTaxonTermDAO;
    @Inject
    NcbiTaxonTermService ncbiTaxonTermService;
    @Inject
    PersonService personService;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(affectedGenomicModelDAO);
    }

    @Transactional
    @Override
    public ObjectResponse<AffectedGenomicModel> create(AffectedGenomicModel entity) {
        NCBITaxonTerm term = ncbiTaxonTermDAO.find(entity.getTaxon().getCurie());
        entity.setTaxon(term);
        if (CollectionUtils.isNotEmpty(entity.getCrossReferences())) {
            List<CrossReference> refs = new ArrayList<>();
            entity.getCrossReferences().forEach(crossReference -> {
                CrossReference reference = new CrossReference();
                reference.setCurie(crossReference.getCurie());
                crossReferenceDAO.persist(reference);
                refs.add(reference);
            });
            entity.setCrossReferences(refs);
        }
        AffectedGenomicModel object = dao.persist(entity);
        ObjectResponse<AffectedGenomicModel> ret = new ObjectResponse<>(object);
        return ret;
    }

    @Override
    @Transactional
    public ObjectResponse<AffectedGenomicModel> update(AffectedGenomicModel uiEntity) {
        log.info(authenticatedPerson);
        AffectedGenomicModel dbEntity = affectedGenomicModelValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<>(affectedGenomicModelDAO.persist(dbEntity));
    }


    @Transactional
    public AffectedGenomicModel upsert(AffectedGenomicModelDTO dto) throws ObjectUpdateException {
        AffectedGenomicModel agm = validateAffectedGenomicModelDTO(dto);
        
        if (agm == null) return null;
        
        return affectedGenomicModelDAO.persist(agm);
    }
    
    public void removeNonUpdatedAlleles(String taxonIds, List<String> agmCuriesBefore, List<String> agmCuriesAfter) {
        log.debug("runLoad: After: " + taxonIds + " " + agmCuriesAfter.size());

        List<String> distinctAfter = agmCuriesAfter.stream().distinct().collect(Collectors.toList());
        log.debug("runLoad: Distinct: " + taxonIds + " " + distinctAfter.size());

        List<String> curiesToRemove = ListUtils.subtract(agmCuriesBefore, distinctAfter);
        log.debug("runLoad: Remove: " + taxonIds + " " + curiesToRemove.size());

        for (String curie : curiesToRemove) {
            AffectedGenomicModel agm = affectedGenomicModelDAO.find(curie);
            if (agm != null) {
                delete(agm.getCurie());
            } else {
                log.error("Failed getting AGM: " + curie);
            }
        }
    }
    
    public List<String> getCuriesByTaxonId(String taxonId) {
        List<String> curies = affectedGenomicModelDAO.findAllCuriesByTaxon(taxonId);
        curies.removeIf(Objects::isNull);
        return curies;
    }
    
    @Transactional
    public AffectedGenomicModel processUpdate(AffectedGenomicModelFmsDTO agm) throws ObjectUpdateException {
        // TODO: add loading of components
        // TODO: add loading of sequenceTargetingReagents
        // TODO: add loading of parentalPopulations

        validateAffectedGenomicModelDTO(agm);

        AffectedGenomicModel dbAgm = affectedGenomicModelDAO.find(agm.getPrimaryID());

        if (dbAgm == null) {
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

        return dbAgm;
    }

    private void handleCrossReference(AffectedGenomicModelFmsDTO agm, AffectedGenomicModel dbAgm) {
        Map<String, CrossReference> currentIds;
        if (dbAgm.getCrossReferences() == null) {
            currentIds = new HashedMap<>();
            dbAgm.setCrossReferences(new ArrayList<>());
        } else {
            currentIds = dbAgm.getCrossReferences().stream().collect(Collectors.toMap(CrossReference::getCurie, Function.identity()));
        }

        Map<String, CrossReferenceFmsDTO> newIds = new HashedMap<>();
        if (agm.getCrossReference() != null) {
            CrossReferenceFmsDTO newCr = new CrossReferenceFmsDTO();
            HashSet<String> pageAreas = new HashSet<>();
            newCr.setId(agm.getCrossReference().getId());
            if (agm.getCrossReference().getPages() != null) pageAreas.addAll(agm.getCrossReference().getPages());
            newCr.setPages(new ArrayList<>(pageAreas));
            newIds.put(newCr.getId(), newCr);
        }

        newIds.forEach((k, v) -> {
            if (!currentIds.containsKey(k)) {
                dbAgm.getCrossReferences().add(crossReferenceService.processUpdate(v));
            }
        });

        currentIds.forEach((k, v) -> {
            if (!newIds.containsKey(k)) {
                dbAgm.getCrossReferences().remove(v);
            }
        });

    }

    private void handleNewSynonyms(AffectedGenomicModelFmsDTO agm, AffectedGenomicModel dbAgm) {
        if (CollectionUtils.isNotEmpty(agm.getSynonyms())) {
            List<Synonym> synonyms = DtoConverterHelper.getSynonyms(agm);
            synonyms.forEach(synonym -> synonymService.create(synonym));
            dbAgm.setSynonyms(synonyms);
        }
    }

    private void handleUpdateSynonyms(AffectedGenomicModelFmsDTO agm, AffectedGenomicModel dbAgm) {
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

    private void handleSecondaryIds(AffectedGenomicModelFmsDTO agm, AffectedGenomicModel dbAgm) {
        Set<String> currentIds;
        if (dbAgm.getSecondaryIdentifiers() == null) {
            currentIds = new HashSet<>();
            dbAgm.setSecondaryIdentifiers(new ArrayList<>());
        } else {
            currentIds = dbAgm.getSecondaryIdentifiers().stream().collect(Collectors.toSet());
        }

        Set<String> newIds;
        if (agm.getSecondaryIds() == null) {
            newIds = new HashSet<>();
        } else {
            newIds = agm.getSecondaryIds().stream().collect(Collectors.toSet());
        }

        newIds.forEach(id -> {
            if (!currentIds.contains(id)) {
                dbAgm.getSecondaryIdentifiers().add(id);
            }
        });

        currentIds.forEach(id -> {
            if (!newIds.contains(id)) {
                dbAgm.getSecondaryIdentifiers().remove(id);
            }
        });

    }

    private void validateAffectedGenomicModelDTO(AffectedGenomicModelFmsDTO agm) throws ObjectValidationException {
        // Check for required fields
        if (agm.getPrimaryID() == null || agm.getName() == null || agm.getTaxonId() == null) {
            throw new ObjectValidationException(agm, "Entry for AGM " + agm.getPrimaryID() + " missing required fields - skipping");
        }

        // Validate taxon ID
        ObjectResponse<NCBITaxonTerm> taxon = ncbiTaxonTermService.get(agm.getTaxonId());
        if (taxon.getEntity() == null) {
            throw new ObjectValidationException(agm, "Invalid taxon ID for AGM " + agm.getPrimaryID() + " - skipping");
        }

        // Validate xref
        if (agm.getCrossReference() != null && agm.getCrossReference().getId() == null) {
            throw new ObjectValidationException(agm, "Missing xref ID for AGM " + agm.getPrimaryID() + " - skipping");
        }

        // Validate component fields
        if (CollectionUtils.isNotEmpty(agm.getAffectedGenomicModelComponents())) {
            for (AffectedGenomicModelComponentFmsDTO component : agm.getAffectedGenomicModelComponents()) {
                if (component.getAlleleID() == null) {
                    throw new ObjectValidationException(agm, "Entry for AGM " + agm.getPrimaryID() + " has component with missing allele - skipping");
                }
                Allele componentAllele = alleleDAO.find(component.getAlleleID());
                if (componentAllele == null) {
                    throw new ObjectValidationException(agm, "Entry for AGM " + agm.getPrimaryID() + " has component allele (" + component.getAlleleID() + ") not found in database - skipping");
                }
                if (component.getZygosity() == null) {
                    throw new ObjectValidationException(agm, "Entry for AGM " + agm.getPrimaryID() + " has component allele (" + component.getAlleleID() + ") with missing zygosity - skipping");
                }

                if (!validZygosityCodes.contains(component.getZygosity())) {
                    throw new ObjectValidationException(agm, "Entry for AGM " + agm.getPrimaryID() + " has component allele (" + component.getAlleleID() + ") with invalid zygosity - skipping");
                }
            }
        }
    }

    private static final Set<String> validZygosityCodes = Set.of(
            "GENO:0000602", "GENO:0000603", "GENO:0000604", "GENO:0000605", "GENO:0000606", "GENO:0000135",
            "GENO:0000136", "GENO:0000137", "GENO:0000134"
    );
    
    private AffectedGenomicModel validateAffectedGenomicModelDTO(AffectedGenomicModelDTO dto) throws ObjectValidationException {
        // Check for required fields
        if (dto.getCurie() == null || dto.getTaxon() == null) {
            throw new ObjectValidationException(dto, "Entry for allele " + dto.getCurie() + " missing required fields - skipping");
        }

        AffectedGenomicModel agm = affectedGenomicModelDAO.find(dto.getCurie());
        if (agm == null) {
            agm = new AffectedGenomicModel();
            agm.setCurie(dto.getCurie());
        } 
        
        // Validate taxon ID
        ObjectResponse<NCBITaxonTerm> taxonResponse = ncbiTaxonTermService.get(dto.getTaxon());
        if (taxonResponse.getEntity() == null) {
            throw new ObjectValidationException(dto, "Invalid taxon ID for allele " + dto.getCurie() + " - skipping");
        }
        agm.setTaxon(taxonResponse.getEntity());
        
        if (dto.getName() != null) agm.setName(dto.getName());
        
        if (dto.getCreatedBy() != null) {
            Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
            agm.setCreatedBy(createdBy);
        }
        if (dto.getModifiedBy() != null) {
            Person modifiedBy = personService.fetchByUniqueIdOrCreate(dto.getModifiedBy());
            agm.setModifiedBy(modifiedBy);
        }
        
        agm.setInternal(dto.getInternal());

        if (dto.getDateUpdated() != null) {
            OffsetDateTime dateLastModified;
            try {
                dateLastModified = OffsetDateTime.parse(dto.getDateUpdated());
            } catch (DateTimeParseException e) {
                throw new ObjectValidationException(dto, "Could not parse date_updated in " + agm.getCurie() + " - skipping");
            }
            agm.setDateUpdated(dateLastModified);
        }

        if (dto.getDateCreated() != null) {
            OffsetDateTime creationDate;
            try {
                creationDate = OffsetDateTime.parse(dto.getDateCreated());
            } catch (DateTimeParseException e) {
                throw new ObjectValidationException(dto, "Could not parse date_created in " + agm.getCurie() + " - skipping");
            }
            agm.setDateCreated(creationDate);
        }
        
        return agm;
    }
}
