package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.entities.ontology.*;
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
    XcoTermDAO xcoTermDAO;
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
    public DiseaseAnnotation upsert(DiseaseModelAnnotationFmsDTO annotationDTO) {

        String entityId = annotationDTO.getObjectId();

        BiologicalEntity subjectEntity = biologicalEntityDAO.find(entityId);

        // do not create DA if no entity / subject is found.
        if (subjectEntity == null) {
            log("Subject Entity " + entityId + " not found in database - skipping annotation");
            return null;
        }

        if (!validateAnnotationDTO(annotationDTO)) {
            log("Annotation for " + entityId + " validation failed - skipping annotation");
            return null;
        }

        String doTermId = annotationDTO.getDoId();
        DOTerm disease = doTermDAO.find(doTermId);
        if (disease == null) {
            log("Annotation for " + entityId + " missing DOTerm: " + doTermId + " required fields - skipping annotation");
            return null;
        }

        String publicationId = annotationDTO.getEvidence().getPublication().getPublicationId();
        Reference reference = referenceDAO.find(publicationId);
        if (reference == null) {
            reference = new Reference();
            reference.setCurie(publicationId);
            // ToDo: need this until references are loaded separately
            // raise an error when reference cannot be found?
            referenceDAO.persist(reference);
        }

        List<ConditionRelation> conditionRelations = new ArrayList<>();
        // create Experimental Conditions
        if (CollectionUtils.isNotEmpty(annotationDTO.getConditionRelations())) {
            annotationDTO.getConditionRelations().forEach(conditionRelationDTO -> {
                ConditionRelation relation = new ConditionRelation();
                relation.setConditionRelationType(conditionRelationDTO.getConditionRelationType());
                conditionRelationDTO.getConditions().forEach(experimentalConditionDTO -> {
                    ExperimentalCondition experimentalCondition = new ExperimentalCondition();
                    if (experimentalConditionDTO.getChemicalOntologyId() != null) {
                        ChemicalTerm term = chemicalTermDAO.find(experimentalConditionDTO.getChemicalOntologyId());
                        experimentalCondition.setConditionChemical(term);
                    }
                    if (experimentalConditionDTO.getConditionId() != null) {
                        XcoTerm term = xcoTermDAO.find(experimentalConditionDTO.getConditionId());
                        experimentalCondition.setConditionId(term);
                    }
                    if (experimentalConditionDTO.getConditionClassId() != null) {
                        ZecoTerm term = zecoTermDAO.find(experimentalConditionDTO.getConditionClassId());
                        experimentalCondition.setConditionClass(term);
                    }
                    if (experimentalConditionDTO.getConditionStatement() != null) {
                        experimentalCondition.setConditionStatement(experimentalConditionDTO.getConditionStatement());
                    }
                    String experimentalConditionUniqueId = DiseaseAnnotationCurie.getExperimentalConditionCurie(experimentalConditionDTO);
                    // reuse existing experimental condition
                    SearchResponse<ExperimentalCondition> searchResponse = experimentalConditionDAO.findByField("uniqueId", experimentalConditionUniqueId);
                    if (searchResponse == null || searchResponse.getSingleResult() == null) {
                        experimentalCondition.setUniqueId(experimentalConditionUniqueId);
                        experimentalConditionDAO.persist(experimentalCondition);
                    } else {
                        experimentalCondition = searchResponse.getSingleResult();
                    }
                    relation.addExperimentCondition(experimentalCondition);
                });
                String conditionRelationUniqueID = DiseaseAnnotationCurie.getConditionRelationUnique(relation);
                relation.setUniqueId(conditionRelationUniqueID);
                // reuse existing condition relation
                SearchResponse<ConditionRelation> searchResponseRel = conditionRelationDAO.findByField("uniqueId", conditionRelationUniqueID);
                if (searchResponseRel == null || searchResponseRel.getSingleResult() == null) {
                    relation.setUniqueId(conditionRelationUniqueID);
                    conditionRelationDAO.persist(relation);
                    conditionRelations.add(relation);
                } else {
                    conditionRelations.add(searchResponseRel.getSingleResult());
                }
            });
        }

        DiseaseAnnotationCurie diseaseAnnotationCurie = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(subjectEntity.getTaxon().getCurie());
        String annotationID = diseaseAnnotationCurie.getCurieID(annotationDTO);

        DiseaseAnnotation annotation;

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
            log("Annotation for " + entityId + " missing Subject: " + subjectEntity + " not valid type - skipping annotation");
            return null;
        }

        annotation.setObject(disease);
        annotation.setReference(reference);

        if (CollectionUtils.isNotEmpty(annotationDTO.getEvidence().getEvidenceCodes())) {
            List<EcoTerm> ecoTerms = new ArrayList<>();
            annotationDTO.getEvidence().getEvidenceCodes()
                    .forEach(evidence -> {
                        EcoTerm ecoTerm = ecoTermDAO.find(evidence);
                        ecoTerms.add(ecoTerm);
                    });
            annotation.setEvidenceCodes(ecoTerms);
        }
        annotation.setNegated(annotationDTO.getNegation() == DiseaseModelAnnotationFmsDTO.Negation.not);

        if (CollectionUtils.isNotEmpty(annotationDTO.getWith())) {
            List<Gene> withGenes = new ArrayList<>();
            annotationDTO.getWith().forEach(with -> {
                if (with.startsWith("HGNC:")) {
                    Gene withGene = geneDAO.getByIdOrCurie(with);
                    withGenes.add(withGene);
                }
            });
            annotation.setWith(withGenes);
        }
        annotation.setConditionRelations(conditionRelations);
        annotation.setCreated(annotationDTO.getDateAssigned().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(annotationDTO.getObjectRelation().getAssociationType()));

        diseaseAnnotationDAO.persist(annotation);
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

    private boolean validateAnnotationDTO(DiseaseModelAnnotationFmsDTO dto) {
        // Check if primary annotation                                                                                                                                                              
        if (CollectionUtils.isNotEmpty(dto.getPrimaryGeneticEntityIDs())) {
            log("Annotation for " + dto.getObjectId() + " is a secondary annotation - skipping");
            return false;
        }
        // Check required fields                                                                                                                                                                    
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
            return false;
        }
        // Check valid disease relation type                                                                                                                                                        
        if (dto.getObjectRelation().getObjectType().equals("gene")) {
            if (!dto.getObjectRelation().getAssociationType().equals("is_implicated_in") &&
                    !dto.getObjectRelation().getAssociationType().equals("is_marker_for")
            ) {
                log("Invalid gene disease relation for " + dto.getObjectId() + " - skipping annotation");
                return false;
            }
        } else if (dto.getObjectRelation().getObjectType().equals("allele")) {
            if (!dto.getObjectRelation().getAssociationType().equals("is_implicated_in")) {
                log("Invalid allele disease relation for " + dto.getObjectId() + " - skipping annotation");
                return false;
            }
        } else if (dto.getObjectRelation().getObjectType().equals("genotype") ||
                dto.getObjectRelation().getObjectType().equals("strain") ||
                dto.getObjectRelation().getObjectType().equals("fish")
        ) {
            if (!dto.getObjectRelation().getAssociationType().equals("is_model_of")) {
                log("Invalid AGM disease relation for " + dto.getObjectId() + " - skipping annotation");
                return false;
            }
        } else {
            log("Invalid object type for " + dto.getObjectId() + " - skipping annotation");
            return false;
        }

        return true;
    }

    private void log(String message) {
        log.debug(message);
        //log.info(message);
    }

}
