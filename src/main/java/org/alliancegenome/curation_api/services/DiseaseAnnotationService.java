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
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.*;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseService<DiseaseAnnotation, DiseaseAnnotationDAO> {

    
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

    private DiseaseAnnotation upsertAnnotation(DiseaseModelAnnotationDTO annotationDTO, BiologicalEntity entity, DOTerm disease, Reference reference) {

        String annotationID = getUniqueID(annotationDTO);
        
        SearchResponse<DiseaseAnnotation> annotationList = diseaseAnnotationDAO.findByField("curie", annotationID);

        DiseaseAnnotation annotation = null;
        if (annotationList == null || annotationList.getResults().size() == 0) {
            annotation = new DiseaseAnnotation();
            annotation.setCurie(annotationID);
        } else {
            annotation = annotationList.getResults().get(0);
        }

        annotation.setSubject(entity);
        annotation.setObject(disease);
        annotation.setReferenceList(List.of(reference));
        
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
                if (!with.startsWith("HGNC:")) return;
                Gene withGene = geneDAO.getByIdOrCurie(with);
                withGenes.add(withGene);
            });
            annotation.setWith(withGenes);
        }
        
        annotation.setCreated(
                annotationDTO.getDateAssigned()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );
        annotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.valueOf(
                annotationDTO
                        .getObjectRelation()
                        .getAssociationType())
        );

        diseaseAnnotationDAO.persist(annotation);
        return annotation;
    }

    @Transactional
    public DiseaseAnnotation upsert(DiseaseModelAnnotationDTO annotationDTO) {
        
        String entityId = annotationDTO.getObjectId();

        BiologicalEntity entity = biologicalEntityDAO.find(entityId);

        // do not create DA if no entity / subject is found.
        if (entity == null) {
            log.info("Entity " + entityId + " not found in database - skipping annotation");
            return null;
        }

        if (!validateAnnotationDTO(annotationDTO)) {
            log.info("Annotation for " + entityId + " missing required fields - skipping annotation");
            return null;
        }
        
        String doTermId = annotationDTO.getDoId();
        DOTerm disease = doTermDAO.find(doTermId);
        // TODo: Change logic when ontology loader is in place
        // do not create new DOTerm records here
        // but raise an error if disease cannot be found
        if (disease == null) {
            disease = new DOTerm();
            disease.setCurie(doTermId);
            doTermDAO.persist(disease);
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
        return upsertAnnotation(annotationDTO, entity, disease, reference);
    }

    private String getUniqueID(DiseaseModelAnnotationDTO annotationDTO) {

        BiologicalEntity entity = biologicalEntityDAO.find(annotationDTO.getObjectId());

        // do not create DA if no entity / subject is found.
        if (entity == null)
            return null;

        return DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(entity.getTaxon()).getCurieID(annotationDTO);
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

    @Override
    @Transactional
    public ObjectResponse<DiseaseAnnotation> create(DiseaseAnnotation entity) {
        validateAnnotation(entity, false);
        return super.create(entity);
    }
    
    @Override
    @Transactional
    public ObjectResponse<DiseaseAnnotation> update(DiseaseAnnotation entity) {
        validateAnnotation(entity, true);
        // assumes the incoming object is a complete object
        return super.update(entity);
    }

    public void validateAnnotation(DiseaseAnnotation entity, boolean isUpdate) {
        ObjectResponse<DiseaseAnnotation> response = new ObjectResponse<>(entity);
        String errorTitle = "Could not update Disease Annotation: [" + entity.getId() + "]";
        String originalDoId = null;
        List <String> originalEvidenceCodes = new ArrayList<String>();
        if (isUpdate) {
            Long id = entity.getId();
            if (id == null) {
                response.setErrorMessage("No Disease Annotation ID provided");
                throw new ApiErrorException(response);
            }
            DiseaseAnnotation diseaseAnnotation = diseaseAnnotationDAO.find(id);
            if (diseaseAnnotation == null) {
                response.setErrorMessage("Could not find Disease Annotation with ID: [" + id + "]");
                throw new ApiErrorException(response);
                // do not continue validation for update if Disease Annotation ID has not been found
            }       
            originalDoId = entity.getObject().getCurie();           
            for (EcoTerm ec : diseaseAnnotation.getEvidenceCodes()) {
                originalEvidenceCodes.add(ec.getCurie());
            }
        }
        else {
            errorTitle = "Could not create DiseaseAnnotation";
        }
        
        if (validateRequiredFields(entity, response)) {
            if (validateReferencedEntities(entity, response, originalDoId, originalEvidenceCodes)) {
                validateTypeAndSubject(entity, response);
                
            }
        }
        // check required fields
        // ToDo: implement mandatory / optional fields for each MOD
        //
        
        if (response.hasErrors()) {
            response.setErrorMessage(errorTitle);
            throw new ApiErrorException(response);
        }
    }

    private boolean validateRequiredFields(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response) {
        if (ObjectUtils.isEmpty(entity.getSubject()) || StringUtils.isEmpty(entity.getSubject().getCurie())) {
            addRequiredMessageToResponse("subject", response);
        }
        if (ObjectUtils.isEmpty(entity.getObject()) || StringUtils.isEmpty(entity.getObject().getCurie())) {
            addRequiredMessageToResponse("object", response);
        }
        if (StringUtils.isEmpty(entity.getDiseaseRelation().toString())) {
            addRequiredMessageToResponse("diseaseRelation", response);
        }
        if (CollectionUtils.isEmpty(entity.getEvidenceCodes())) {
            addRequiredMessageToResponse("evidence", response);
        }
        if (CollectionUtils.isEmpty(entity.getReferenceList())) {
            addRequiredMessageToResponse("referenceList", response);
        }
        
        if (response.hasErrors()) {
            return false;
        }
        return true;
    }
    
    private void validateTypeAndSubject(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response) {
        boolean correctTypeAndSubject = true;
        DiseaseAnnotation.DiseaseRelation relation = entity.getDiseaseRelation();
        if (entity.getSubject() instanceof Gene) {
            correctTypeAndSubject = ( relation == DiseaseAnnotation.DiseaseRelation.is_implicated_in ||
              relation == DiseaseAnnotation.DiseaseRelation.is_marker_for);
        }
        if (entity.getSubject() instanceof Allele) {
            correctTypeAndSubject = (relation == DiseaseAnnotation.DiseaseRelation.is_implicated_in);
        }
        if (entity.getSubject() instanceof AffectedGenomicModel) {
            correctTypeAndSubject = (relation == DiseaseAnnotation.DiseaseRelation.is_model_of);
        }
        if (!correctTypeAndSubject) {
            addInvalidMessagetoResponse("diseaseRelation", response);
        }
    }
    
    public boolean validateReferencedEntities(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response,
            String originalDoId, List<String> originalEvidenceCodes) {
        validateDiseaseAnnotationSubject(entity, response);
        validateDiseaseAnnotationObject(entity, response, originalDoId);
        validateDiseaseAnnotationEvidenceCodes(entity, response, originalEvidenceCodes);
        validateDiseaseAnnotationWith(entity, response);
        if (response.hasErrors()) {
            return false;
        }
        return true;
    }
    
    public void validateDiseaseAnnotationSubject(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response) {
        BiologicalEntity subjectEntity = biologicalEntityDAO.find(entity.getSubject().getCurie());
        if (subjectEntity == null) {
            addInvalidMessagetoResponse("subject", response);
        }
        entity.setSubject(subjectEntity);
    }
    
    public void validateDiseaseAnnotationObject(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response,
            String originalDoTerm) {
        DOTerm diseaseTerm = doTermDAO.find(entity.getObject().getCurie());
        if (diseaseTerm == null) {
            addInvalidMessagetoResponse("object", response);
        }
        else if (diseaseTerm.getObsolete() && diseaseTerm.getCurie() != originalDoTerm) {
            addObsoleteMessagetoResponse("object", response);
        }
        else {
            entity.setObject(diseaseTerm);
        }
    }
     
    private void validateDiseaseAnnotationEvidenceCodes(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response,
            List<String> originalEvidenceCodes ) {
        List<EcoTerm> validEvidenceCodes = new ArrayList<>(); 
        for (EcoTerm ec : entity.getEvidenceCodes()) {
            EcoTerm evidenceCode = ecoTermDAO.find(ec.getCurie());
            if (evidenceCode == null ) {
                addInvalidMessagetoResponse("evidence", response);
                break;
            }
            else if (evidenceCode.getObsolete() && !originalEvidenceCodes.contains(evidenceCode)) {
                addObsoleteMessagetoResponse("evidence", response);
                break;
            }
            else {
                validEvidenceCodes.add(evidenceCode);
            }
        }
        entity.setEvidenceCodes(validEvidenceCodes);
    }
    
    private void validateDiseaseAnnotationWith(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response) {
        if (CollectionUtils.isNotEmpty(entity.getWith())) {
            List<Gene> validWithGenes = new ArrayList<Gene>();
            for (Gene wg : entity.getWith()) {
                Gene withGene = geneDAO.find(wg.getCurie());
                if (withGene == null || !withGene.getCurie().startsWith("HGNC:")) {
                    addInvalidMessagetoResponse("with", response);
                    break;
                }
                else {
                    validWithGenes.add(withGene);
                }
            }
            entity.setWith(validWithGenes);
        }
    }

//  private boolean validateDisease(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response,
//      String originalDoTerm) {
//      String fieldName = "object";
//
//      if (validateRequiredObject(entity, response))
//          return validateDiseaseAnnotationDisease(entity, fieldName, response, originalDoTerm);
//      return false;
//  }

//  private boolean validateSubject(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response) {
//      String fieldName = "subject";
//      if (validateRequiredSubject(entity, response))
//          return validateDiseaseAnnotationSubject(entity, fieldName, response);
//      return false;
//  }

//  private boolean validateRequiredSubject(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response) {
//      if (entity.getSubject() == null || StringUtils.isEmpty(entity.getSubject().getCurie())) {
//          addRequiredMessageToResponse("subject", response);
//          return false;
//      }
//      return true;
//  }

//  private boolean validateRequiredObject(DiseaseAnnotation entity, ObjectResponse<DiseaseAnnotation> response) {
//      if (ObjectUtils.isEmpty(entity.getObject()) || StringUtils.isEmpty(entity.getObject().getCurie())) {
//          addRequiredMessageToResponse("object", response);
//          return false;
//      }
//      return true;
//  }

    private void addRequiredMessageToResponse(String fieldName, ObjectResponse<DiseaseAnnotation> response) {
        response.addErrorMessage(fieldName, "Required field is empty");
    }

    private void addInvalidMessagetoResponse(String fieldName, ObjectResponse<DiseaseAnnotation> response) {
        response.addErrorMessage(fieldName, "Not a valid entry");
    }
    
    private void addObsoleteMessagetoResponse(String fieldName, ObjectResponse<DiseaseAnnotation> response) {
        response.addErrorMessage(fieldName, "Obsolete term specified");
    }
    
    private boolean validateAnnotationDTO(DiseaseModelAnnotationDTO dto) {
        if (CollectionUtils.isNotEmpty(dto.getPrimaryGeneticEntityIDs()) ||
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
            return false;
        }
        return true;
    }

}
