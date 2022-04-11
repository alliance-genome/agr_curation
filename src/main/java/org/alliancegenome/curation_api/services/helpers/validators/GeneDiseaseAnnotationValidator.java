package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.*;

@RequestScoped
public class GeneDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

    @Inject
    GeneDAO geneDAO;
    @Inject
    AffectedGenomicModelDAO agmDAO;
    @Inject
    GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    
    private String GENE_DISEASE_RELATION_VOCABULARY = "Gene disease relations";

    public GeneDiseaseAnnotation validateAnnotation(GeneDiseaseAnnotation uiEntity) {
        response = new ObjectResponse<>(uiEntity);
        String errorTitle = "Could not update Gene Disease Annotation: [" + uiEntity.getId() + "]";

        Long id = uiEntity.getId();
        if (id == null) {
            addMessageResponse("No Gene Disease Annotation ID provided");
            throw new ApiErrorException(response);
        }
        GeneDiseaseAnnotation dbEntity = geneDiseaseAnnotationDAO.find(id);
        if (dbEntity == null) {
            addMessageResponse("Could not find Gene Disease Annotation with ID: [" + id + "]");
            throw new ApiErrorException(response);
            // do not continue validation for update if Disease Annotation ID has not been found
        }       

        Gene subject = validateSubject(uiEntity, dbEntity);
        if(subject != null) dbEntity.setSubject(subject);

        VocabularyTerm relation = validateDiseaseRelation(uiEntity);
        if(relation != null) dbEntity.setDiseaseRelation(relation);

        AffectedGenomicModel sgdStrainBackground = validateSgdStrainBackground(uiEntity);
        if (sgdStrainBackground != null) dbEntity.setSgdStrainBackground(uiEntity.getSgdStrainBackground());
        
        dbEntity = (GeneDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);
        
        if (response.hasErrors()) {
            response.setErrorMessage(errorTitle);
            throw new ApiErrorException(response);
        }

        return dbEntity;
    }

    private Gene validateSubject(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {
        if (ObjectUtils.isEmpty(uiEntity.getSubject()) || StringUtils.isEmpty(uiEntity.getSubject().getCurie())) {
            addMessageResponse("subject", requiredMessage);
            return null;
        }
        Gene subjectEntity = geneDAO.find(uiEntity.getSubject().getCurie());
        if (subjectEntity == null) {
            addMessageResponse("subject", invalidMessage);
            return null;
        }
        return subjectEntity;

    }
    
    private VocabularyTerm validateDiseaseRelation(GeneDiseaseAnnotation uiEntity) {
        String field = "diseaseRelation";
        if (uiEntity.getDiseaseRelation() == null) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        
        VocabularyTerm relation = vocabularyTermDAO.getTermInVocabulary(uiEntity.getDiseaseRelation().getName(), GENE_DISEASE_RELATION_VOCABULARY);

        if(relation == null) {
            addMessageResponse(field, invalidMessage);
            return null;
        }
        
        return relation;
    }
    
    private AffectedGenomicModel validateSgdStrainBackground(GeneDiseaseAnnotation uiEntity) {
        if (uiEntity.getSgdStrainBackground() == null) {
            return null;
        }
        
        AffectedGenomicModel sgdStrainBackground = agmDAO.find(uiEntity.getSgdStrainBackground().getCurie());
        if (sgdStrainBackground == null || !sgdStrainBackground.getCurie().startsWith("SGD:")) {
            addMessageResponse("sgdStrainBackground", invalidMessage);
            return null;
        }
        
        return sgdStrainBackground;
    }
}
