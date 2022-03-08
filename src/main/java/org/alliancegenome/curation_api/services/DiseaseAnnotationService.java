package org.alliancegenome.curation_api.services;

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
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.AnnotationType;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseQualifier;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseGeneticModifierRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.GeneticSex;
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
                if (conditionRelationType.equals("ameliorated_by") || 
                        conditionRelationType.equals("exacerbated_by") ||
                        conditionRelationType.equals("has_condition") ||
                        conditionRelationType.equals("induced_by") ||
                        conditionRelationType.equals("not_induced_by") ||
                        conditionRelationType.equals("not_ameliorated_by") ||
                        conditionRelationType.equals("not_exacerbated_by")
                        ) {
                    relation.setConditionRelationType(conditionRelationType);
                } else {
                    throw new ObjectUpdateException(annotationDTO, "Annotation " + annotation.getUniqueId() + " contains invalid conditionRelationType " + conditionRelationType + " - skipping annotation");
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
        
        if (dto.getObject() == null || dto.getDiseaseRelation() == null || dto.getDataProvider() == null || dto.getSingleReference() == null || CollectionUtils.isEmpty(dto.getEvidenceCodes())) {
            throw new ObjectValidationException(dto, "Annotation for " + dto.getObject() + " missing required fields - skipping");
        }
        annotation.setDataProvider(dto.getDataProvider());

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
            List<DiseaseQualifier> diseaseQualifiers = new ArrayList<>();
            for (String qualifier : dto.getDiseaseQualifiers()) {
                if (!qualifier.equals("susceptibility") &&
                        !qualifier.equals("disease_progression") &&
                        !qualifier.equals("severity") &&
                        !qualifier.equals("onset") &&
                        !qualifier.equals("sexual_dimorphism") &&
                        !qualifier.equals("resistance") &&
                        !qualifier.equals("penetrance")
                        ) {
                    throw new ObjectValidationException(dto, "Invalid disease qualifier (" + qualifier + ") for " + annotation.getUniqueId() + " - skipping annotation");
                }
                diseaseQualifiers.add(DiseaseQualifier.valueOf(qualifier));
            }
            annotation.setDiseaseQualifiers(diseaseQualifiers);
        }
        
        if (dto.getDiseaseGeneticModifier() != null || dto.getDiseaseGeneticModifierRelation() != null) {
            if (dto.getDiseaseGeneticModifier() == null || dto.getDiseaseGeneticModifierRelation() == null) {
                throw new ObjectValidationException(dto, "Genetic modifier specified without genetic modifier relation (or vice versa) for " + annotation.getUniqueId() + " - skipping annotation");
            }
            if (!dto.getDiseaseGeneticModifierRelation().equals("ameliorated_by") &&
                    !dto.getDiseaseGeneticModifierRelation().equals("not_ameliorated_by") &&
                    !dto.getDiseaseGeneticModifierRelation().equals("exacerbated_by") &&
                    !dto.getDiseaseGeneticModifierRelation().equals("not_exacerbated_by")
                    ) {
                throw new ObjectValidationException(dto, "Invalid disease genetic modifier relation (" + dto.getDiseaseGeneticModifierRelation() + ") for " + annotation.getUniqueId() + " - skipping annotation");
            }
            annotation.setDiseaseGeneticModifierRelation(DiseaseGeneticModifierRelation.valueOf(dto.getDiseaseGeneticModifierRelation()));
            BiologicalEntity diseaseGeneticModifier = biologicalEntityDAO.find(dto.getDiseaseGeneticModifier());
            if (diseaseGeneticModifier == null) {
                throw new ObjectValidationException(dto, "Invalid biological entity (" + dto.getDiseaseGeneticModifier() + ") in 'disease_genetic_modifier' field in " + annotation.getUniqueId() + " - skipping annotation");
            }
            annotation.setDiseaseGeneticModifier(diseaseGeneticModifier);
        }
        
        if (dto.getAnnotationType() != null) {
            if (!dto.getAnnotationType().equals("manually_curated") &&
                    !dto.getAnnotationType().equals("high-throughput") &&
                    !dto.getAnnotationType().equals("computational")
                    ) {
                throw new ObjectValidationException(dto, "Invalid annotation type (" + dto.getAnnotationType() + ") in " + annotation.getUniqueId() + " - skipping annotation");
            }   
            if (dto.getAnnotationType().equals("high-throughput")) {
                annotation.setAnnotationType(AnnotationType.high_throughput);
            }
            else {
                annotation.setAnnotationType(AnnotationType.valueOf(dto.getAnnotationType()));
            }
        }
        
        if (dto.getGeneticSex() != null) {
            if (!dto.getGeneticSex().equals("male") &&
                    !dto.getGeneticSex().equals("female") &&
                    !dto.getGeneticSex().equals("hermaphrodite")
                    ) {
                throw new ObjectValidationException(dto, "Invalid genetic sex (" + dto.getGeneticSex() + ") in " + annotation.getUniqueId() + " - skipping annotation");
            }
            annotation.setGeneticSex(GeneticSex.valueOf(dto.getGeneticSex()));
        }
        
        if (CollectionUtils.isNotEmpty(dto.getRelatedNotes())) {
            List<Note> notesToPersist = new ArrayList<>();
            for (NoteDTO noteDTO : dto.getRelatedNotes()) {
                // might want to catch the ObjectValidationException here
                // and rethrow it with the dto object and errors
                // as the full dto object will not be available for viewing in the UI
                Note relatedNote = noteService.validateNoteDTO(noteDTO, "Disease annotation note types");
                if (relatedNote == null) return null;
                notesToPersist.add(relatedNote);
            }
            notesToPersist.forEach(note -> noteDAO.persist(note));
            annotation.setRelatedNotes(notesToPersist);
        }
        
        

        return annotation;
    }

}
