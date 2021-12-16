package org.alliancegenome.curation_api.services;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseService<DiseaseAnnotation, DiseaseAnnotationDAO> {

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
    public DiseaseAnnotation upsert(DiseaseModelAnnotationDTO annotationDTO) {
        
        String entityId = annotationDTO.getObjectId();

        BiologicalEntity subjectEntity = biologicalEntityDAO.find(entityId);

        // do not create DA if no entity / subject is found.
        if (subjectEntity == null) {
            log.info("Subject Entity " + entityId + " not found in database - skipping annotation");
            return null;
        }

        if (!validateAnnotationDTO(annotationDTO)) {
            log.info("Annotation for " + entityId + " missing required fields - skipping annotation");
            return null;
        }
        
        String doTermId = annotationDTO.getDoId();
        DOTerm disease = doTermDAO.find(doTermId);
        if (disease == null) {
            log.info("Annotation for " + entityId + " missing DOTerm: " + doTermId + " required fields - skipping annotation");
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

        String annotationID = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(subjectEntity.getTaxon()).getCurieID(annotationDTO);
        
        DiseaseAnnotation annotation = null;
        
        if(subjectEntity instanceof Gene) {
            SearchResponse<GeneDiseaseAnnotation> annotationList = geneDiseaseAnnotationDAO.findByField("curie", annotationID);
            if (annotationList == null || annotationList.getResults().size() == 0) {
                GeneDiseaseAnnotation geneAnnotation = new GeneDiseaseAnnotation();
                geneAnnotation.setCurie(annotationID);
                geneAnnotation.setSubject((Gene)subjectEntity);
                annotation = geneAnnotation;
            } else {
                annotation = annotationList.getResults().get(0);
            }
        } else if(subjectEntity instanceof Allele) {
            SearchResponse<AlleleDiseaseAnnotation> annotationList = alleleDiseaseAnnotationDAO.findByField("curie", annotationID);
            if (annotationList == null || annotationList.getResults().size() == 0) {
                AlleleDiseaseAnnotation alleleAnnotation = new AlleleDiseaseAnnotation();
                alleleAnnotation.setCurie(annotationID);
                alleleAnnotation.setSubject((Allele)subjectEntity);
                annotation = alleleAnnotation;
            } else {
                annotation = annotationList.getResults().get(0);
            }
        } else if(subjectEntity instanceof AffectedGenomicModel) {
            SearchResponse<AGMDiseaseAnnotation> annotationList = agmDiseaseAnnotationDAO.findByField("curie", annotationID);
            if (annotationList == null || annotationList.getResults().size() == 0) {
                AGMDiseaseAnnotation agmAnnotation = new AGMDiseaseAnnotation();
                agmAnnotation.setCurie(annotationID);
                agmAnnotation.setSubject((AffectedGenomicModel)subjectEntity);
                annotation = agmAnnotation;
            } else {
                annotation = annotationList.getResults().get(0);
            }
        } else {
            log.info("Annotation for " + entityId + " missing Subject: " + subjectEntity + " not valid type - skipping annotation");
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
        annotation.setNegated(annotationDTO.getNegation() == DiseaseModelAnnotationDTO.Negation.not);

        if (CollectionUtils.isNotEmpty(annotationDTO.getWith())) {
            List <Gene> withGenes = new ArrayList<>();
            annotationDTO.getWith().forEach(with -> {
                if (with.startsWith("HGNC:")) {
                    Gene withGene = geneDAO.getByIdOrCurie(with);
                    withGenes.add(withGene);
                }
            });
            annotation.setWith(withGenes);
        }
        
        annotation.setCreated(annotationDTO.getDateAssigned().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(annotationDTO.getObjectRelation().getAssociationType()));

        diseaseAnnotationDAO.persist(annotation);
        return annotation;

    }


    public void runLoad(String taxonID, DiseaseAnnotationMetaDataDTO annotationData) {
        List<String> annotationsCuriesBefore = diseaseAnnotationDAO.findAllAnnotationCuries(taxonID);
        List<String> annotationsCuriesAfter = new ArrayList<>();
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Disease Annotation Update " + taxonID, annotationData.getData().size());
        annotationData.getData().forEach(annotationDTO -> {
            DiseaseAnnotation annotation = upsert(annotationDTO);
            if (annotation != null) {
                annotationsCuriesAfter.add(annotation.getCurie());
            }
            ph.progressProcess();
        });
        ph.finishProcess();
        
        List<String> distinctAfter = annotationsCuriesAfter.stream().distinct().collect(Collectors.toList());
        List<String> curiesToRemove = ListUtils.subtract(annotationsCuriesBefore, distinctAfter);
        List<Long> idsToRemove = new ArrayList<>();
        for (String curie : curiesToRemove) {
            idsToRemove.add(diseaseAnnotationDAO.getIdFromCurie(curie));
        }
        idsToRemove.forEach(this::delete);
    }
    
    private boolean validateAnnotationDTO(DiseaseModelAnnotationDTO dto) {
        // Check if primary annotation                                                                                                                                                              
        if (CollectionUtils.isNotEmpty(dto.getPrimaryGeneticEntityIDs())) {
            log.debug("Annotation for " + dto.getDoId() + " is a secondary annotation - skipping");
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
            log.debug("Annotation for " + dto.getObjectId() + " missing required fields - skipping");
            return false;
        }
        // Check valid disease relation type                                                                                                                                                        
        if (dto.getObjectRelation().getObjectType().equals("gene")) {
            if (!dto.getObjectRelation().getAssociationType().equals("is_implicated_in") &&
                    !dto.getObjectRelation().getAssociationType().equals("is_marker_for")
                    ) {
                log.debug("Invalid gene disease relation for " + dto.getObjectId() + " - skipping annotation");
                return false;
            }
        }
        else if (dto.getObjectRelation().getObjectType().equals("allele")) {
            if (!dto.getObjectRelation().getAssociationType().equals("is_implicated_in")) {
                log.debug("Invalid allele disease relation for " + dto.getObjectId() + " - skipping annotation");
                return false;
            }
        }
        else if (dto.getObjectRelation().getObjectType().equals("genotype") ||
                dto.getObjectRelation().getObjectType().equals("strain") ||
                dto.getObjectRelation().getObjectType().equals("fish")
                ) {
            if (!dto.getObjectRelation().getAssociationType().equals("is_model_of")) {
                log.debug("Invalid AGM disease relation for " + dto.getObjectId() + " - skipping annotation");
                return false;
            }
        }
        else {
            log.debug("Invalid object type for " + dto.getObjectId() + " - skipping annotation");
            return false;
        }
        
        return true;
    }

}
