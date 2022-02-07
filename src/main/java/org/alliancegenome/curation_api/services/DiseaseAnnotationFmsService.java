package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.ConditionRelationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.ExperimentalConditionFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseModelAnnotationFmsDTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JBossLog
@RequestScoped
public class DiseaseAnnotationFmsService extends BaseCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {

    @Inject
    GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    @Inject
    AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
    @Inject
    AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;

    @Inject
    DiseaseAnnotationDAO diseaseAnnotationDAO;
    @Inject
    ReferenceDAO referenceDAO;
    @Inject
    DoTermDAO doTermDAO;
    @Inject
    EcoTermDAO ecoTermDAO;
    @Inject
    ChemicalTermDAO chemicalTermDAO;
    @Inject
    ZecoTermDAO zecoTermDAO;
    @Inject
    AnatomicalTermDAO anatomicalTermDAO;
    @Inject
    NcbiTaxonTermDAO ncbiTaxonTermDAO;
    @Inject
    GoTermDAO goTermDAO;
    @Inject
    ExperimentalConditionOntologyTermDAO experimentalConditionOntologyTermDAO;
    @Inject
    ConditionRelationDAO conditionRelationDAO;
    @Inject
    ExperimentalConditionDAO experimentalConditionDAO;
    @Inject
    BiologicalEntityDAO biologicalEntityDAO;
    @Inject
    GeneDAO geneDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(diseaseAnnotationDAO);
    }

    // The following methods are for bulk validation


    @Transactional
    public DiseaseAnnotation upsert(DiseaseModelAnnotationFmsDTO annotationFmsDTO) {

        DiseaseAnnotation annotation = validateAnnotationFmsDTO(annotationFmsDTO);
        if (annotation == null) return null;

        List<ConditionRelation> conditionRelations = new ArrayList<>();
        List<ConditionRelation> conditionRelationsToPersist = new ArrayList<>();
        List<ExperimentalCondition> experimentalConditionsToPersist = new ArrayList<>();
        // create Experimental Conditions
        if (CollectionUtils.isNotEmpty(annotationFmsDTO.getConditionRelations())) {
            for (ConditionRelationFmsDTO conditionRelationFmsDTO : annotationFmsDTO.getConditionRelations()) {
                ConditionRelation relation = new ConditionRelation();
                
                String conditionRelationType = validateConditionRelationFmsDTO(conditionRelationFmsDTO);
                if (conditionRelationType == null) {
                    log("Annotation " + annotation.getUniqueId() + " contains invalid conditionRelation - skipping annotation");
                    return null;
                } else {
                    relation.setConditionRelationType(conditionRelationType);
                }
                
                if (conditionRelationFmsDTO.getConditions() == null) {
                    log("Annotation " + annotation.getUniqueId() + " missing conditions for " + conditionRelationType + " - skipping annotation");
                    return null;
                }
                for (ExperimentalConditionFmsDTO experimentalConditionFmsDTO : conditionRelationFmsDTO.getConditions()) {
                    ExperimentalCondition experimentalCondition = validateExperimentalConditionFmsDTO(experimentalConditionFmsDTO);
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


        diseaseAnnotationDAO.persist(annotation);
        experimentalConditionsToPersist.forEach(condition -> experimentalConditionDAO.persist(condition));
        conditionRelationsToPersist.forEach(relation -> conditionRelationDAO.persist(relation));
        
        return annotation;

    }

    public void runLoad(String taxonID, DiseaseAnnotationMetaDataFmsDTO annotationData) {
        List<String> annotationsIdsBefore = new ArrayList<String>();
        annotationsIdsBefore.addAll(geneDiseaseAnnotationDAO.findAllAnnotationIds(taxonID));
        annotationsIdsBefore.addAll(alleleDiseaseAnnotationDAO.findAllAnnotationIds(taxonID));
        annotationsIdsBefore.addAll(agmDiseaseAnnotationDAO.findAllAnnotationIds(taxonID));

        log.debug("runLoad: Before: " + taxonID + " " + annotationsIdsBefore.size());
        List<String> annotationsIdsAfter = new ArrayList<>();
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Disease Annotation Update " + taxonID, annotationData.getData().size());
        annotationData.getData().forEach(annotationDTO -> {
            DiseaseAnnotation annotation = upsert(annotationDTO);
            if (annotation != null) {
                annotationsIdsAfter.add(annotation.getUniqueId());
            }
            ph.progressProcess();
        });
        ph.finishProcess();

        log.debug("runLoad: After: " + taxonID + " " + annotationsIdsAfter.size());

        List<String> distinctAfter = annotationsIdsAfter.stream().distinct().collect(Collectors.toList());
        log.debug("runLoad: Distinct: " + taxonID + " " + distinctAfter.size());

        List<String> curiesToRemove = ListUtils.subtract(annotationsIdsBefore, distinctAfter);
        log.debug("runLoad: Remove: " + taxonID + " " + curiesToRemove.size());

        for (String curie : curiesToRemove) {
            SearchResponse<DiseaseAnnotation> da = diseaseAnnotationDAO.findByField("uniqueId", curie);
            if (da != null && da.getTotalResults() == 1) {
                delete(da.getResults().get(0).getId());
            } else {
                log.error("Failed getting annotation: " + curie);
            }
        }
    }

    private DiseaseAnnotation validateAnnotationFmsDTO(DiseaseModelAnnotationFmsDTO dto) {
        DiseaseAnnotation annotation;
        
        if (dto.getObjectId() == null ||
                dto.getDoId() == null ||
                dto.getDateAssigned() == null ||
                dto.getEvidence() == null ||
                dto.getEvidence().getEvidenceCodes() == null ||
                dto.getEvidence().getPublication() == null ||
                dto.getEvidence().getPublication().getPublicationId() == null ||
                dto.getObjectRelation() == null ||
                dto.getObjectRelation().getAssociationType() == null ||
                dto.getObjectRelation().getObjectType() == null
        ) {
            log("Annotation for " + dto.getObjectId() + " missing required fields - skipping");
            return null;
        }
        
        // Check if primary annotation                                                                                                                                                              
        if (CollectionUtils.isNotEmpty(dto.getPrimaryGeneticEntityIDs())) {
            log("Annotation for " + dto.getObjectId() + " is a secondary annotation - skipping");
            return null;
        }
        
        String entityId = dto.getObjectId();
        BiologicalEntity subjectEntity = biologicalEntityDAO.find(entityId);
        if (subjectEntity == null) {
            log("Subject Entity " + entityId + " not found in database - skipping annotation");
            return null;
        }
        
        DiseaseAnnotationCurie diseaseAnnotationCurie = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(subjectEntity.getTaxon().getCurie());
        String annotationID = diseaseAnnotationCurie.getCurieID(dto);
        
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
            log("Annotation " + annotationID + " has invalid subject type valid type - skipping annotation");
            return null;
        }

        DOTerm disease = doTermDAO.find(dto.getDoId());
        if (disease == null) {
            log("Annotation " + annotationID + " contains invalid DOTerm: " + dto.getDoId() + " required fields - skipping annotation");
            return null;
        }
        annotation.setObject(disease);

        String publicationId = dto.getEvidence().getPublication().getPublicationId();
        Reference reference = referenceDAO.find(publicationId);
        if (reference == null) {
            reference = new Reference();
            reference.setCurie(publicationId);
            // ToDo: need this until references are loaded separately
            // raise an error when reference cannot be found?
            referenceDAO.persist(reference);
        }
        annotation.setReference(reference);
        
        // Check valid disease relation type                                                                                                                                                        
        if (dto.getObjectRelation().getObjectType().equals("gene")) {
            if (!dto.getObjectRelation().getAssociationType().equals("is_implicated_in") &&
                    !dto.getObjectRelation().getAssociationType().equals("is_marker_for")
            ) {
                log("Invalid gene disease relation for " + dto.getObjectId() + " - skipping annotation");
                return null;
            }
        } else if (dto.getObjectRelation().getObjectType().equals("allele")) {
            if (!dto.getObjectRelation().getAssociationType().equals("is_implicated_in")) {
                log("Invalid allele disease relation for " + dto.getObjectId() + " - skipping annotation");
                return null;
            }
        } else if (dto.getObjectRelation().getObjectType().equals("genotype") ||
                dto.getObjectRelation().getObjectType().equals("strain") ||
                dto.getObjectRelation().getObjectType().equals("fish")
        ) {
            if (!dto.getObjectRelation().getAssociationType().equals("is_model_of")) {
                log("Invalid AGM disease relation for " + dto.getObjectId() + " - skipping annotation");
                return null;
            }
        } else {
            log("Invalid object type for " + dto.getObjectId() + " - skipping annotation");
            return null;
        }
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(dto.getObjectRelation().getAssociationType()));
        
        if (CollectionUtils.isNotEmpty(dto.getEvidence().getEvidenceCodes())) {
            List<EcoTerm> ecoTerms = new ArrayList<>();
            for (String ecoCurie : dto.getEvidence().getEvidenceCodes()) {
                EcoTerm ecoTerm = ecoTermDAO.find(ecoCurie);
                if (ecoTerm == null) {
                    log("Invalid evidence code in " + annotationID + " - skipping annotation");
                }
                ecoTerms.add(ecoTerm);
            }
            annotation.setEvidenceCodes(ecoTerms);
        }
        if (dto.getNegation() != null)
            annotation.setNegated(dto.getNegation() == DiseaseModelAnnotationFmsDTO.Negation.not);

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
    
    private String validateConditionRelationFmsDTO(ConditionRelationFmsDTO dto) {
        if (dto.getConditionRelationType() == null || dto.getConditions() == null) {
            return null;
        }
        String conditionRelationType = dto.getConditionRelationType();
        if (conditionRelationType.equals("ameliorates")) {
            return "ameliorated_by";
        }
        if (conditionRelationType.equals("exacerbates")) {
            return "exacerbated_by";
        }
        if (conditionRelationType.equals("has_condition")) {
            return conditionRelationType;
        }
        if (conditionRelationType.equals("induces")) {
            return "induced_by";
        }
        
        return null;
    }
    
    private ExperimentalCondition validateExperimentalConditionFmsDTO(ExperimentalConditionFmsDTO dto) {
        ExperimentalCondition experimentalCondition = new ExperimentalCondition();
        
        if (dto.getChemicalOntologyId() != null) {
            ChemicalTerm term = chemicalTermDAO.find(dto.getChemicalOntologyId());
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
        if (dto.getConditionClassId() != null) {
            ZecoTerm term = zecoTermDAO.find(dto.getConditionClassId());
            if (term == null) return null;
            experimentalCondition.setConditionClass(term);
        }
        else {
            log("ConditionClassId is a required field - skipping annotation");
            return null;
        }
        
        if (dto.getAnatomicalOntologyId() != null) {
            AnatomicalTerm term = anatomicalTermDAO.find(dto.getAnatomicalOntologyId());
            if (term == null) {
                log("Invalid AnatomicalOntologyId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionAnatomy(term);
        }
        if (dto.getNcbiTaxonId() != null) {
            NCBITaxonTerm term = ncbiTaxonTermDAO.find(dto.getNcbiTaxonId());
            if (term == null) {
                term = ncbiTaxonTermDAO.downloadAndSave(dto.getNcbiTaxonId());
            }
            if (term == null) {
                log("Invalid NCBITaxonId - skipping annotation");
                return null;
            }
            experimentalCondition.setConditionTaxon(term);
        }
        if (dto.getGeneOntologyId() != null) {
            GOTerm term = goTermDAO.find(dto.getGeneOntologyId());
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
        //log.info(message);
    }

}
