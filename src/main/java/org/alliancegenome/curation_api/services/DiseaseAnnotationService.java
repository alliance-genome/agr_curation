package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
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
import org.alliancegenome.curation_api.model.ingest.dto.*;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {

    @Inject
    GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    @Inject
    AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
    @Inject
    AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
    @Inject
    ExperimentalConditionDAO experimentalConditionDAO;
    @Inject
    ConditionRelationDAO conditionRelationDAO;
    @Inject
    DiseaseAnnotationDAO diseaseAnnotationDAO;
    @Inject
    ReferenceDAO referenceDAO;
    @Inject
    DoTermDAO doTermDAO;
    @Inject
    EcoTermDAO ecoTermDAO;
    @Inject
    AlleleDAO alleleDAO;
    @Inject
    GeneDAO geneDAO;
    @Inject
    NoteDAO noteDAO;
    @Inject
    ExperimentalConditionService experimentalConditionService;
    @Inject
    NoteService noteService;
    @Inject
    BiologicalEntityDAO biologicalEntityDAO;
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    
    private String ANNOTATION_TYPE_VOCABULARY = "Annotation types";
    private String DISEASE_QUALIFIER_VOCABULARY = "Disease qualifiers";
    private String DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY = "Disease genetic modifier relations";
    private String GENETIC_SEX_VOCABULARY = "Genetic sexes";
    private String CONDITION_RELATION_TYPE_VOCABULARY = "Condition relation types";

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(diseaseAnnotationDAO);
    }

    // The following methods are for bulk validation


    @Transactional
    public DiseaseAnnotation upsert(DiseaseAnnotation annotation, DiseaseAnnotationDTO annotationDTO) throws ObjectUpdateException {
        List<ConditionRelation> conditionRelations = new ArrayList<>();
        List<ConditionRelation> conditionRelationsToPersist = new ArrayList<>();
        List<ExperimentalCondition> experimentalConditionsToPersist = new ArrayList<>();
        
        // create Experimental Conditions
        if (CollectionUtils.isNotEmpty(annotationDTO.getConditionRelations())) {
            for (ConditionRelationDTO conditionRelationDTO : annotationDTO.getConditionRelations()) {
                ConditionRelation relation = new ConditionRelation();
                
                String conditionRelationType = conditionRelationDTO.getConditionRelationType();
                if (conditionRelationType == null) {
                    throw new ObjectUpdateException(annotationDTO, "Annotation " + annotation.getUniqueId() + " has condition without relation type - skipping");
                }
                VocabularyTerm conditionRelationTypeTerm = vocabularyTermDAO.getTermInVocabulary(conditionRelationType, CONDITION_RELATION_TYPE_VOCABULARY);
                if (conditionRelationTypeTerm == null) {
                    throw new ObjectUpdateException(annotationDTO, "Annotation " + annotation.getUniqueId() + " contains invalid conditionRelationType " + conditionRelationType + " - skipping annotation");
                } else {
                    relation.setConditionRelationType(conditionRelationTypeTerm);
                } 
                
                if (CollectionUtils.isEmpty(conditionRelationDTO.getConditions())) {
                    throw new ObjectUpdateException(annotationDTO, "Annotation " + annotation.getUniqueId() + " missing conditions for " + conditionRelationType + " - skipping annotation");
                }
                for (ExperimentalConditionDTO experimentalConditionDTO : conditionRelationDTO.getConditions()) {
                    ExperimentalCondition experimentalCondition = experimentalConditionService.validateExperimentalConditionDTO(experimentalConditionDTO);
                    if (experimentalCondition == null) return null;
                    
                    // reuse existing experimental condition
                    SearchResponse<ExperimentalCondition> searchResponse = experimentalConditionDAO.findByField("uniqueId", experimentalCondition.getUniqueId());
                    if (searchResponse == null || searchResponse.getSingleResult() == null) {
                        experimentalConditionsToPersist.add(experimentalCondition);
                    } else {
                        experimentalCondition = searchResponse.getSingleResult();
                    }
                    relation.addExperimentCondition(experimentalCondition);
                }
                
                relation.setUniqueId(DiseaseAnnotationCurie.getConditionRelationUnique(relation));
                // reuse existing condition relation
                SearchResponse<ConditionRelation> searchResponseRel = conditionRelationDAO.findByField("uniqueId", relation.getUniqueId());
                if (searchResponseRel == null || searchResponseRel.getSingleResult() == null) {
                    conditionRelationsToPersist.add(relation);
                } else {
                    relation = searchResponseRel.getSingleResult();
                }
                conditionRelations.add(relation);
            }
            annotation.setConditionRelations(conditionRelations);
        }


        experimentalConditionsToPersist.forEach(condition -> experimentalConditionDAO.persist(condition));
        conditionRelationsToPersist.forEach(relation -> conditionRelationDAO.persist(relation));
        diseaseAnnotationDAO.persist(annotation);
        return annotation;

    }

    public void removeNonUpdatedAnnotations(String taxonId, List<String> annotationIdsBefore, List<String> annotationIdsAfter) {
        log.debug("runLoad: After: " + taxonId + " " + annotationIdsAfter.size());
    
        List<String> distinctAfter = annotationIdsAfter.stream().distinct().collect(Collectors.toList());
        log.debug("runLoad: Distinct: " + taxonId + " " + distinctAfter.size());

        List<String> idsToRemove = ListUtils.subtract(annotationIdsBefore, distinctAfter);
        log.debug("runLoad: Remove: " + taxonId + " " + idsToRemove.size());
        
        for (String id : idsToRemove) {
            SearchResponse<DiseaseAnnotation> da = diseaseAnnotationDAO.findByField("uniqueId", id);
            if (da != null && da.getTotalResults() == 1) {
                delete(da.getResults().get(0).getId());
            } else {
                log.error("Failed getting annotation: " + id);
            }
        }
    }

    public DiseaseAnnotation validateAnnotationDTO(DiseaseAnnotation annotation, DiseaseAnnotationDTO dto) throws ObjectValidationException {
        
        if (dto.getObject() == null || dto.getDiseaseRelation() == null || dto.getDataProvider() == null || dto.getSingleReference() == null ||
                CollectionUtils.isEmpty(dto.getEvidenceCodes()) || dto.getCreatedBy() == null || dto.getModifiedBy() == null) {
            throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing required fields - skipping");
        }
        annotation.setDataProvider(dto.getDataProvider());
        annotation.setCreatedBy(dto.getCreatedBy());
        annotation.setModifiedBy(dto.getModifiedBy());

        if (dto.getDateLastModified() != null) {
            OffsetDateTime dateLastModified;
            try {
                dateLastModified = OffsetDateTime.parse(dto.getDateLastModified());
            } catch (DateTimeParseException e) {
                throw new ObjectValidationException(dto, "Could not parse date_last_modified in annotation " + annotation.getUniqueId() + " - skipping");
            }
            annotation.setDateLastModified(dateLastModified);
        }
        
        if (dto.getCreationDate() != null) {
            OffsetDateTime creationDate;
            try {
                creationDate = OffsetDateTime.parse(dto.getCreationDate());
            } catch (DateTimeParseException e) {
                throw new ObjectValidationException(dto, "Could not parse creation_date in annotation " + annotation.getUniqueId() + " - skipping");
            }
            annotation.setCreationDate(creationDate);
        }

        if (dto.getModEntityId() != null) {
            annotation.setModEntityId(dto.getModEntityId());
        }
        
        DOTerm disease = doTermDAO.find(dto.getObject());
        if (disease == null) {
            throw new ObjectValidationException(dto, "Annotation " + annotation.getUniqueId() + " has DOTerm " + disease + " not found in database - skipping");
        }
        annotation.setObject(disease);
        
        String publicationId = dto.getSingleReference();
        Reference reference = referenceDAO.find(publicationId);
        if (reference == null) {
            reference = new Reference();
            reference.setCurie(publicationId);
            //log("Reference: " + reference.toString());
            // ToDo: need this until references are loaded separately
            // raise an error when reference cannot be found?
            referenceDAO.persist(reference);
        }
        annotation.setSingleReference(reference);
        
                
        List<EcoTerm> ecoTerms = new ArrayList<>();
        for (String ecoCurie : dto.getEvidenceCodes()) {
            EcoTerm ecoTerm = ecoTermDAO.find(ecoCurie);
            if (ecoTerm == null) {
                throw new ObjectValidationException(dto, "Invalid evidence code in " + annotation.getUniqueId() + " - skipping annotation");
            }
            ecoTerms.add(ecoTerm);
        }
        annotation.setEvidenceCodes(ecoTerms);
        
        if (dto.getNegated() != null)
            annotation.setNegated(dto.getNegated());
        
        if (CollectionUtils.isNotEmpty(dto.getWith())) {
            List<Gene> withGenes = new ArrayList<>();
            for (String withCurie : dto.getWith()) {
                if (!withCurie.startsWith("HGNC:")) {
                    throw new ObjectValidationException(dto, "Non-HGNC gene (" + withCurie + ") found in 'with' field in " + annotation.getUniqueId() + " - skipping annotation");
                }
                Gene withGene = geneDAO.getByIdOrCurie(withCurie);
                if (withGene == null) {
                    throw new ObjectValidationException(dto, "Invalid gene (" + withCurie + ") in 'with' field in " + annotation.getUniqueId() + " - skipping annotation");
                }
                withGenes.add(withGene);
            }
            annotation.setWith(withGenes);
        }
        
        if (dto.getSecondaryDataProvider() != null)
            annotation.setSecondaryDataProvider(dto.getSecondaryDataProvider());
        
        if (CollectionUtils.isNotEmpty(dto.getDiseaseQualifiers())) {
            List<VocabularyTerm> diseaseQualifiers = new ArrayList<>();
            for (String qualifier : dto.getDiseaseQualifiers()) {
                VocabularyTerm diseaseQualifier = vocabularyTermDAO.getTermInVocabulary(qualifier, DISEASE_QUALIFIER_VOCABULARY);
                if (diseaseQualifier == null) {
                    throw new ObjectValidationException(dto, "Invalid disease qualifier (" + qualifier + ") for " + annotation.getUniqueId() + " - skipping annotation");
                }
                diseaseQualifiers.add(diseaseQualifier);
            }
            annotation.setDiseaseQualifiers(diseaseQualifiers);
        }
        
        if (dto.getDiseaseGeneticModifier() != null || dto.getDiseaseGeneticModifierRelation() != null) {
            if (dto.getDiseaseGeneticModifier() == null || dto.getDiseaseGeneticModifierRelation() == null) {
                throw new ObjectValidationException(dto, "Genetic modifier specified without genetic modifier relation (or vice versa) for " + annotation.getUniqueId() + " - skipping annotation");
            }
            VocabularyTerm diseaseGeneticModifierRelation = vocabularyTermDAO.getTermInVocabulary(dto.getDiseaseGeneticModifierRelation(), DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY);
            if (diseaseGeneticModifierRelation == null) {
                throw new ObjectValidationException(dto, "Invalid disease genetic modifier relation (" + dto.getDiseaseGeneticModifierRelation() + ") for " + annotation.getUniqueId() + " - skipping annotation");
            }
            annotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
            
            BiologicalEntity diseaseGeneticModifier = biologicalEntityDAO.find(dto.getDiseaseGeneticModifier());
            if (diseaseGeneticModifier == null) {
                throw new ObjectValidationException(dto, "Invalid biological entity (" + dto.getDiseaseGeneticModifier() + ") in 'disease_genetic_modifier' field in " + annotation.getUniqueId() + " - skipping annotation");
            }
            annotation.setDiseaseGeneticModifier(diseaseGeneticModifier);
        }
        
        if (dto.getAnnotationType() != null) {
            VocabularyTerm annotationType = vocabularyTermDAO.getTermInVocabulary(dto.getAnnotationType(), ANNOTATION_TYPE_VOCABULARY);
            if (annotationType == null) {
                throw new ObjectValidationException(dto, "Invalid annotation type (" + dto.getAnnotationType() + ") in " + annotation.getUniqueId() + " - skipping annotation");
            }   
            annotation.setAnnotationType(annotationType);
            
        }
        
        if (dto.getGeneticSex() != null) {
            VocabularyTerm geneticSex = vocabularyTermDAO.getTermInVocabulary(dto.getGeneticSex(), GENETIC_SEX_VOCABULARY);
            if (geneticSex == null) {
                throw new ObjectValidationException(dto, "Invalid genetic sex (" + dto.getGeneticSex() + ") in " + annotation.getUniqueId() + " - skipping annotation");
            }
            annotation.setGeneticSex(geneticSex);
        }
        
        if (CollectionUtils.isNotEmpty(dto.getRelatedNotes())) {
            List<Note> notesToPersist = new ArrayList<>();
            for (NoteDTO noteDTO : dto.getRelatedNotes()) {
                Note relatedNote = noteService.validateNoteDTO(noteDTO, "Disease annotation note types");
                if (relatedNote == null)
                    throw new ObjectValidationException(dto, "Invalid note attached to disease annotation " + annotation.getUniqueId() + " - skipping annotation");
                notesToPersist.add(relatedNote);
            }
            notesToPersist.forEach(note -> noteDAO.persist(note));
            annotation.setRelatedNotes(notesToPersist);
        }
        
        

        return annotation;
    }

}
