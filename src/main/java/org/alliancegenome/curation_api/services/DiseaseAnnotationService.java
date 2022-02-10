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
    ZecoTermDAO zecoTermDAO;
    @Inject
    ChemicalTermDAO chemicalTermDAO;
    @Inject
    AnatomicalTermDAO anatomicalTermDAO;
    @Inject
    NcbiTaxonTermDAO ncbiTaxonTermDAO;
    @Inject
    GoTermDAO goTermDAO;
    @Inject
    ExperimentalConditionOntologyTermDAO experimentalConditionOntologyTermDAO;
    @Inject
    AffectedGenomicModelDAO agmDAO;
    @Inject
    AlleleDAO alleleDAO;
    @Inject
    GeneDAO geneDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(diseaseAnnotationDAO);
    }

    // The following methods are for bulk validation


    @Transactional
    public DiseaseAnnotation upsert(DiseaseAnnotationDTO annotationDTO, String entityType) {

        DiseaseAnnotation annotation = validateAnnotationDTO(annotationDTO, entityType);
        if (annotation == null) return null;

        
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
                    ExperimentalCondition experimentalCondition = validateExperimentalConditionDTO(experimentalConditionDTO);
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

    public void runLoad(String taxonID, List<DiseaseAnnotationDTO> annotations, String entityType) {
        List<String> annotationsIdsBefore = new ArrayList<>();
        if (entityType.equals("agm")) {
            annotationsIdsBefore.addAll(geneDiseaseAnnotationDAO.findAllAnnotationIds(taxonID));
        } else if (entityType.equals("allele")) {
            annotationsIdsBefore.addAll(alleleDiseaseAnnotationDAO.findAllAnnotationIds(taxonID));
        } else if (entityType.equals("gene")) {
            annotationsIdsBefore.addAll(agmDiseaseAnnotationDAO.findAllAnnotationIds(taxonID));
        } else {
            log.warn("Unrecognised type " + entityType);
        }
        annotationsIdsBefore.removeIf(Objects::isNull);

        log.debug("runLoad: Before: " + taxonID + " " + annotationsIdsBefore.size());
        List<String> annotationsIdsAfter = new ArrayList<>();
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Disease Annotation Update " + taxonID, annotations.size());
        annotations.forEach(annotationDTO -> {
            DiseaseAnnotation annotation = upsert(annotationDTO, entityType);
            if (annotation != null) {
                annotationsIdsAfter.add(annotation.getUniqueId());
            }
            ph.progressProcess();
        });
        ph.finishProcess();

        log.debug("runLoad: After: " + taxonID + " " + annotationsIdsAfter.size());

        List<String> distinctAfter = annotationsIdsAfter.stream().distinct().collect(Collectors.toList());
        log.debug("runLoad: Distinct: " + taxonID + " " + distinctAfter.size());

        List<String> idsToRemove = ListUtils.subtract(annotationsIdsBefore, distinctAfter);
        log.debug("runLoad: Remove: " + taxonID + " " + idsToRemove.size());

        for (String id : idsToRemove) {
            SearchResponse<DiseaseAnnotation> da = diseaseAnnotationDAO.findByField("uniqueId", id);
            if (da != null && da.getTotalResults() == 1) {
                delete(da.getResults().get(0).getId());
            } else {
                log.error("Failed getting annotation: " + id);
            }
        }
    }

    private DiseaseAnnotation validateAnnotationDTO(DiseaseAnnotationDTO dto, String entityType) {
        DiseaseAnnotation annotation;
        
        if (dto.getObject() == null ||
            dto.getSubject() == null ||
            dto.getDiseaseRelation() == null ||
            dto.getDataProvider() == null ||
            CollectionUtils.isEmpty(dto.getEvidenceCodes())
                ) {
            log("Annotation for " + dto.getObject() + " missing required fields - skipping");
            return null;
        }
        
        BiologicalEntity subjectEntity;
        if (entityType.equals("agm")) {
            subjectEntity = agmDAO.find(dto.getSubject());
        } else if (entityType.equals("allele")) {
            subjectEntity = alleleDAO.find(dto.getSubject());
        }
        else {
            subjectEntity = geneDAO.find(dto.getSubject());
        }
        if (subjectEntity == null) {
            log("Subject " + entityType + " " + dto.getSubject() + " not found in database - skipping annotation");
            return null;
        }
        
        String annotationID = dto.getModId();
        if (annotationID == null) {
            DiseaseAnnotationCurie diseaseAnnotationCurie = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(subjectEntity.getTaxon().getCurie());
            annotationID = diseaseAnnotationCurie.getCurieID(dto);
        }
        
        if (subjectEntity instanceof Gene) {
            SearchResponse<GeneDiseaseAnnotation> annotationList = geneDiseaseAnnotationDAO.findByField("uniqueId", annotationID);
            if (annotationList == null || annotationList.getResults().size() == 0) {
                GeneDiseaseAnnotation geneAnnotation = new GeneDiseaseAnnotation();
                geneAnnotation.setUniqueId(annotationID);
                geneAnnotation.setSubject((Gene) subjectEntity);
                annotation = geneAnnotation;
            } else {
                annotation = annotationList.getResults().get(0);
            }
        } else if (subjectEntity instanceof Allele) {
            SearchResponse<AlleleDiseaseAnnotation> annotationList = alleleDiseaseAnnotationDAO.findByField("uniqueId", annotationID);
            if (annotationList == null || annotationList.getResults().size() == 0) {
                AlleleDiseaseAnnotation alleleAnnotation = new AlleleDiseaseAnnotation();
                alleleAnnotation.setUniqueId(annotationID);
                alleleAnnotation.setSubject((Allele) subjectEntity);
                annotation = alleleAnnotation;
            } else {
                annotation = annotationList.getResults().get(0);
            }
        } else if (subjectEntity instanceof AffectedGenomicModel) {
            SearchResponse<AGMDiseaseAnnotation> annotationList = agmDiseaseAnnotationDAO.findByField("uniqueId", annotationID);
            if (annotationList == null || annotationList.getResults().size() == 0) {
                AGMDiseaseAnnotation agmAnnotation = new AGMDiseaseAnnotation();
                agmAnnotation.setUniqueId(annotationID);
                agmAnnotation.setSubject((AffectedGenomicModel) subjectEntity);
                annotation = agmAnnotation;
            } else {
                annotation = annotationList.getResults().get(0);
            }
        } else {
            log("Annotation " + annotationID + " has invalid subject type - skipping annotation");
            return null;
        }
        
        DOTerm disease = doTermDAO.find(dto.getObject());
        if (disease == null) {
            log("Annotation " + annotationID + " has DOTerm " + disease + " not found in database - skipping");
            return null;
        }
        annotation.setObject(disease);
        
        String publicationId = dto.getSingleReference();
        Reference reference = referenceDAO.find(publicationId);
        if (reference == null) {
            reference = new Reference();
            reference.setCurie(publicationId);
            // ToDo: need this until references are loaded separately
            // raise an error when reference cannot be found?
            referenceDAO.persist(reference);
        }
        annotation.setSingleReference(reference);
        
        // Check valid disease relation type                                                                                                                                                        
        if (entityType.equals("gene")) {
            if (!dto.getDiseaseRelation().equals("is_implicated_in") &&
                    !dto.getDiseaseRelation().equals("is_marker_for")
            ) {
                log("Invalid gene disease relation for " + dto.getSubject() + " - skipping annotation");
                return null;
            }
        } else if (entityType.equals("allele")) {
            if (!dto.getDiseaseRelation().equals("is_implicated_in")) {
                log("Invalid allele disease relation for " + dto.getSubject() + " - skipping annotation");
                return null;
            }
        } else if (entityType.equals("agm") ||
                dto.getDiseaseRelation().equals("strain") ||
                dto.getDiseaseRelation().equals("fish")
        ) {
            if (!dto.getDiseaseRelation().equals("is_model_of")) {
                log("Invalid AGM disease relation for " + dto.getSubject() + " - skipping annotation");
                return null;
            }
        } else {
            log("Invalid object type " + entityType + " for " + dto.getSubject() + " - skipping annotation");
            return null;
        }
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(dto.getDiseaseRelation()));
                
        List<EcoTerm> ecoTerms = new ArrayList<>();
        for (String ecoCurie : dto.getEvidenceCodes()) {
            EcoTerm ecoTerm = ecoTermDAO.find(ecoCurie);
            if (ecoTerm == null) {
                log("Invalid evidence code in " + annotationID + " - skipping annotation");
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
                    log("Non-HGNC gene (" + withCurie + ") found in 'with' field in " + annotationID + " - skipping annotation");
                    return null;
                }
                Gene withGene = geneDAO.getByIdOrCurie(withCurie);
                if (withGene == null) {
                    log("Invalid gene (" + withCurie + ") in 'with' field in " + annotationID + " - skipping annotation");
                    return null;
                }
                withGenes.add(withGene);
            }
            annotation.setWith(withGenes);
        }

        return annotation;
    }
    
    private ExperimentalCondition validateExperimentalConditionDTO(ExperimentalConditionDTO dto) {
        ExperimentalCondition experimentalCondition = new ExperimentalCondition();
        
        if (dto.getConditionChemical() != null) {
            ChemicalTerm term = chemicalTermDAO.find(dto.getConditionChemical());
            if (term == null) {
                log("Invalid ChemicalOntologyId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionChemical(term);
        }
        if (dto.getConditionId() != null) {
            ExperimentalConditionOntologyTerm term = experimentalConditionOntologyTermDAO.find(dto.getConditionId());
            if (term == null) {
                log("Invalid ConditionId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionId(term);
        }
        if (dto.getConditionClass() != null) {
            ZecoTerm term = zecoTermDAO.find(dto.getConditionClass());
            if (term == null) return null;
            experimentalCondition.setConditionClass(term);
        }
        else {
            log("ConditionClassId is a required field - skipping annotation");
            return null;
        }
        
        if (dto.getConditionAnatomy() != null) {
            AnatomicalTerm term = anatomicalTermDAO.find(dto.getConditionAnatomy());
            if (term == null) {
                log("Invalid AnatomicalOntologyId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionAnatomy(term);
        }
        if (dto.getConditionTaxon() != null) {
            NCBITaxonTerm term = ncbiTaxonTermDAO.find(dto.getConditionTaxon());
            if (term == null) {
                term = ncbiTaxonTermDAO.downloadAndSave(dto.getConditionTaxon());
            }
            if (term == null) {
                log("Invalid NCBITaxonId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionTaxon(term);
        }
        if (dto.getConditionTaxon() != null) {
            GOTerm term = goTermDAO.find(dto.getConditionTaxon());
            if (term == null) {
                log("Invalid GeneOntologyId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionGeneOntology(term);
        }
        if (dto.getConditionQuantity() != null)
            experimentalCondition.setConditionQuantity(dto.getConditionQuantity());
        if (dto.getConditionStatement() == null) {
            log("ConditionStatement is a required field - skipping annotation");
            return null;
        }
        experimentalCondition.setConditionStatement(dto.getConditionStatement());
        
        experimentalCondition.setUniqueId(DiseaseAnnotationCurie.getExperimentalConditionCurie(dto));
        
        return experimentalCondition;
    }

    private void log(String message) {
        log.debug(message);
        // log.info(message);
    }

}
