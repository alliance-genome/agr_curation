package org.alliancegenome.curation_api.services;

import java.util.*;

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
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.*;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationFmsService extends BaseCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {

    @Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    @Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
    @Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;

    @Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
    @Inject ReferenceDAO referenceDAO;
    @Inject DoTermDAO doTermDAO;
    @Inject EcoTermDAO ecoTermDAO;
    @Inject ChemicalTermDAO chemicalTermDAO;
    @Inject ZecoTermDAO zecoTermDAO;
    @Inject AnatomicalTermDAO anatomicalTermDAO;
    @Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;
    @Inject GoTermDAO goTermDAO;
    @Inject ExperimentalConditionOntologyTermDAO experimentalConditionOntologyTermDAO;
    @Inject ConditionRelationDAO conditionRelationDAO;
    @Inject ExperimentalConditionDAO experimentalConditionDAO;
    @Inject BiologicalEntityDAO biologicalEntityDAO;
    @Inject GeneDAO geneDAO;
    @Inject VocabularyTermDAO vocabularyTermDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(diseaseAnnotationDAO);
    }

    // The following methods are for bulk validation

    @Transactional
    public DiseaseAnnotation upsert(DiseaseModelAnnotationFmsDTO annotationFmsDTO) throws ObjectUpdateException {

        DiseaseAnnotation annotation = validateAnnotationFmsDTO(annotationFmsDTO);

        List<ConditionRelation> conditionRelations = new ArrayList<>();
        List<ConditionRelation> conditionRelationsToPersist = new ArrayList<>();
        List<ExperimentalCondition> experimentalConditionsToPersist = new ArrayList<>();
        // create Experimental Conditions
        if (CollectionUtils.isNotEmpty(annotationFmsDTO.getConditionRelations())) {
            for (ConditionRelationFmsDTO conditionRelationFmsDTO : annotationFmsDTO.getConditionRelations()) {
                ConditionRelation relation = new ConditionRelation();
                
                
                String conditionRelationType = conditionRelationFmsDTO.getConditionRelationType();
                if (conditionRelationType == null) {
                    throw new ObjectUpdateException(annotationFmsDTO, "Annotation " + annotation.getUniqueId() + " has condition without relation type - skipping");
                }
                conditionRelationType = convertConditionRelationTypeVocabulary(conditionRelationType);
                
                VocabularyTerm conditionRelationTypeTerm = vocabularyTermDAO.getTermInVocabulary(conditionRelationType, VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
                if (conditionRelationTypeTerm == null) {
                    throw new ObjectUpdateException(annotationFmsDTO, "Annotation " + annotation.getUniqueId() + " contains invalid conditionRelationType " + conditionRelationType + " - skipping annotation");
                } else {
                    relation.setConditionRelationType(conditionRelationTypeTerm);
                }
                
                if (conditionRelationFmsDTO.getConditions() == null) {
                    throw new ObjectUpdateException(annotationFmsDTO, "Annotation " + annotation.getUniqueId() + " missing conditions for " + conditionRelationType + " - skipping annotation");
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


        experimentalConditionsToPersist.forEach(condition -> experimentalConditionDAO.persist(condition));
        conditionRelationsToPersist.forEach(relation -> conditionRelationDAO.persist(relation));
        diseaseAnnotationDAO.persist(annotation);
        
        return annotation;

    }

    private DiseaseAnnotation validateAnnotationFmsDTO(DiseaseModelAnnotationFmsDTO dto) throws ObjectValidationException {
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
            throw new ObjectValidationException(dto, "Annotation for " + dto.getObjectId() + " missing required fields - skipping");
        }
        
        // Check if primary annotation                                                                                                                                                              
        if (CollectionUtils.isNotEmpty(dto.getPrimaryGeneticEntityIDs())) {
            throw new ObjectValidationException(dto, "Annotation for " + dto.getObjectId() + " is a secondary annotation - skipping");
        }
        
        String entityId = dto.getObjectId();
        BiologicalEntity subjectEntity = biologicalEntityDAO.find(entityId);
        if (subjectEntity == null) {
            throw new ObjectValidationException(dto, "Subject Entity " + entityId + " not found in database - skipping annotation");
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
            throw new ObjectValidationException(dto, "Annotation " + annotationID + " has invalid subject type valid type - skipping annotation");
        }

        DOTerm disease = doTermDAO.find(dto.getDoId());
        if (disease == null) {
            throw new ObjectValidationException(dto, "Annotation " + annotationID + " contains invalid DOTerm: " + dto.getDoId() + " required fields - skipping annotation");
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
        annotation.setSingleReference(reference);
        
        // Check valid disease relation type            
        VocabularyTerm diseaseRelation;
        if (dto.getObjectRelation().getObjectType().equals("gene")) {
            diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getObjectRelation().getAssociationType(), VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY);
            if (diseaseRelation == null) {
                throw new ObjectValidationException(dto, "Invalid gene disease relation for " + dto.getObjectId() + " - skipping annotation");
            }
        } else if (dto.getObjectRelation().getObjectType().equals("allele")) {
            diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getObjectRelation().getAssociationType(), VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY);
            if (diseaseRelation == null) {
                throw new ObjectValidationException(dto, "Invalid allele disease relation for " + dto.getObjectId() + " - skipping annotation");
            }
        } else if (dto.getObjectRelation().getObjectType().equals("genotype") ||
                dto.getObjectRelation().getObjectType().equals("strain") ||
                dto.getObjectRelation().getObjectType().equals("fish")
        ) {
            diseaseRelation = vocabularyTermDAO.getTermInVocabulary(dto.getObjectRelation().getAssociationType(), VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY);
            if (diseaseRelation == null) {
                throw new ObjectValidationException(dto, "Invalid AGM disease relation for " + dto.getObjectId() + " - skipping annotation");
            }
        } else {
            throw new ObjectValidationException(dto, "Invalid object type for " + dto.getObjectId() + " - skipping annotation");
        }
        annotation.setDiseaseRelation(diseaseRelation);
        
        if (CollectionUtils.isNotEmpty(dto.getEvidence().getEvidenceCodes())) {
            List<EcoTerm> ecoTerms = new ArrayList<>();
            for (String ecoCurie : dto.getEvidence().getEvidenceCodes()) {
                EcoTerm ecoTerm = ecoTermDAO.find(ecoCurie);
                if (ecoTerm == null) {
                    throw new ObjectValidationException(dto, "Invalid evidence code in " + annotationID + " - skipping annotation");
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
                    throw new ObjectValidationException(dto, "Non-HGNC gene (" + withCurie + ") found in 'with' field in " + annotationID + " - skipping annotation");
                }
                Gene withGene = geneDAO.getByIdOrCurie(withCurie);
                if (withGene == null) {
                    throw new ObjectValidationException(dto, "Invalid gene (" + withCurie + ") in 'with' field in " + annotationID + " - skipping annotation");
                }
                withGenes.add(withGene);
            }
            annotation.setWith(withGenes);
        }

        return annotation;
    }
    
    private String convertConditionRelationTypeVocabulary(String conditionRelationType) {
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
    
    private ExperimentalCondition validateExperimentalConditionFmsDTO(ExperimentalConditionFmsDTO dto) throws ObjectValidationException {
        ExperimentalCondition experimentalCondition = new ExperimentalCondition();
        
        if (dto.getChemicalOntologyId() != null) {
            ChemicalTerm term = chemicalTermDAO.find(dto.getChemicalOntologyId());
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid ChemicalOntologyId - skipping annotation");
            }
            experimentalCondition.setConditionChemical(term);
        }
        if (dto.getConditionId() != null) {
            ExperimentalConditionOntologyTerm term = experimentalConditionOntologyTermDAO.find(dto.getConditionId());
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid ConditionId - skipping annotation");
            }
            experimentalCondition.setConditionId(term);
        }
        if (dto.getConditionClassId() != null) {
            ZecoTerm term = zecoTermDAO.find(dto.getConditionClassId());
            if (term == null) return null;
            experimentalCondition.setConditionClass(term);
        }
        else {
            throw new ObjectValidationException(dto, "ConditionClassId is a required field - skipping annotation");
        }
        
        if (dto.getAnatomicalOntologyId() != null) {
            AnatomicalTerm term = anatomicalTermDAO.find(dto.getAnatomicalOntologyId());
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid AnatomicalOntologyId - skipping annotation");
            }
            experimentalCondition.setConditionAnatomy(term);
        }
        if (dto.getNcbiTaxonId() != null) {
            NCBITaxonTerm term = ncbiTaxonTermDAO.find(dto.getNcbiTaxonId());
            if (term == null) {
                term = ncbiTaxonTermDAO.downloadAndSave(dto.getNcbiTaxonId());
            }
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid NCBITaxonId - skipping annotation");
            }
            experimentalCondition.setConditionTaxon(term);
        }
        if (dto.getGeneOntologyId() != null) {
            GOTerm term = goTermDAO.find(dto.getGeneOntologyId());
            if (term == null) {
                throw new ObjectValidationException(dto, "Invalid GeneOntologyId - skipping annotation");
            }
            experimentalCondition.setConditionGeneOntology(term);
        }
        if (dto.getConditionQuantity() != null)
            experimentalCondition.setConditionQuantity(dto.getConditionQuantity());
        if (dto.getConditionStatement() == null) {
            throw new ObjectValidationException(dto, "ConditionStatement is a required field - skipping annotation");
        }
        experimentalCondition.setConditionStatement(dto.getConditionStatement());
        
        experimentalCondition.setUniqueId(DiseaseAnnotationCurie.getExperimentalConditionCurie(dto));
        
        return experimentalCondition;
    }

}
