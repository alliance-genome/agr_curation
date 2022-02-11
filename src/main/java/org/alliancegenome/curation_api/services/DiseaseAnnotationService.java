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
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ConditionRelationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
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
    AffectedGenomicModelDAO agmDAO;
    @Inject
    AlleleDAO alleleDAO;
    @Inject
    GeneDAO geneDAO;
    @Inject
    ExperimentalConditionService experimentalConditionService;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(diseaseAnnotationDAO);
    }

    // The following methods are for bulk validation


    @Transactional
    public DiseaseAnnotation upsert(DiseaseAnnotation annotation, DiseaseAnnotationDTO annotationDTO) {
        List<ConditionRelation> conditionRelations = new ArrayList<>();
        List<ConditionRelation> conditionRelationsToPersist = new ArrayList<>();
        List<ExperimentalCondition> experimentalConditionsToPersist = new ArrayList<>();
        // create Experimental Conditions
        if (CollectionUtils.isNotEmpty(annotationDTO.getConditionRelations())) {
            for (ConditionRelationDTO conditionRelationDTO : annotationDTO.getConditionRelations()) {
                ConditionRelation relation = new ConditionRelation();
                
                String conditionRelationType = conditionRelationDTO.getConditionRelationType();
                if (conditionRelationType.equals("ameliorated_by") || 
                        conditionRelationType.equals("exacerbated_by") ||
                        conditionRelationType.equals("has_condition") ||
                        conditionRelationType.equals("induced_by") ||
                        conditionRelationType.equals("not_induced_by") ||
                        conditionRelationType.equals("not_ameliorated_by") ||
                        conditionRelationType.equals("not_exacterbated_by")
                        ) {
                    relation.setConditionRelationType(conditionRelationType);
                } else {
                    log("Annotation " + annotation.getUniqueId() + " contains invalid conditionRelationType " + conditionRelationType + " - skipping annotation");
                    return null;
                } 
                
                if (CollectionUtils.isEmpty(conditionRelationDTO.getConditions())) {
                    log("Annotation " + annotation.getUniqueId() + " missing conditions for " + conditionRelationType + " - skipping annotation");
                    return null;
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

    public DiseaseAnnotation validateAnnotationDTO(DiseaseAnnotation annotation, DiseaseAnnotationDTO dto) {
        
        if (dto.getObject() == null ||
            dto.getDiseaseRelation() == null ||
            dto.getDataProvider() == null ||
            CollectionUtils.isEmpty(dto.getEvidenceCodes())
                ) {
            log("Annotation for " + dto.getObject() + " missing required fields - skipping");
            return null;
        }
        
        DOTerm disease = doTermDAO.find(dto.getObject());
        if (disease == null) {
            log("Annotation " + annotation.getUniqueId() + " has DOTerm " + disease + " not found in database - skipping");
            return null;
        }
        annotation.setObject(disease);
        
        String publicationId = dto.getSingleReference();
        Reference reference = referenceDAO.find(publicationId);
        if (reference == null) {
            reference = new Reference();
            reference.setCurie(publicationId);
            log("Reference: " + reference.toString());
            // ToDo: need this until references are loaded separately
            // raise an error when reference cannot be found?
            referenceDAO.persist(reference);
        }
        annotation.setSingleReference(reference);
        
                
        List<EcoTerm> ecoTerms = new ArrayList<>();
        for (String ecoCurie : dto.getEvidenceCodes()) {
            EcoTerm ecoTerm = ecoTermDAO.find(ecoCurie);
            if (ecoTerm == null) {
                log("Invalid evidence code in " + annotation.getUniqueId() + " - skipping annotation");
                return null;
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
                    log("Non-HGNC gene (" + withCurie + ") found in 'with' field in " + annotation.getUniqueId() + " - skipping annotation");
                    return null;
                }
                Gene withGene = geneDAO.getByIdOrCurie(withCurie);
                if (withGene == null) {
                    log("Invalid gene (" + withCurie + ") in 'with' field in " + annotation.getUniqueId() + " - skipping annotation");
                    return null;
                }
                withGenes.add(withGene);
            }
            annotation.setWith(withGenes);
        }

        return annotation;
    }

    private void log(String message) {
        log.debug(message);
        // log.info(message);
    }

}
