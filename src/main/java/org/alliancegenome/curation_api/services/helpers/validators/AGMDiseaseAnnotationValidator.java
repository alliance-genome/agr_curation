package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.*;

@RequestScoped
public class AGMDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

    @Inject
    AffectedGenomicModelDAO affectedGenomicModelDAO;
    
    @Inject
    AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
    
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    
    public AGMDiseaseAnnotation validateAnnotation(AGMDiseaseAnnotation uiEntity) {
        response = new ObjectResponse<>(uiEntity);
        String errorTitle = "Could not update AGM Disease Annotation: [" + uiEntity.getId() + "]";

        Long id = uiEntity.getId();
        if (id == null) {
            addMessageResponse("No AGM Disease Annotation ID provided");
            throw new ApiErrorException(response);
        }
        AGMDiseaseAnnotation dbEntity = agmDiseaseAnnotationDAO.find(id);
        if (dbEntity == null) {
            addMessageResponse("Could not find AGM Disease Annotation with ID: [" + id + "]");
            throw new ApiErrorException(response);
            // do not continue validation for update if Disease Annotation ID has not been found
        }       

        AffectedGenomicModel subject = validateSubject(uiEntity, dbEntity);
        if(subject != null) dbEntity.setSubject(subject);

        VocabularyTerm relation = validateDiseaseRelation(uiEntity);
        if(relation != null) dbEntity.setDiseaseRelation(relation);

        dbEntity = (AGMDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);

        if (response.hasErrors()) {
            response.setErrorMessage(errorTitle);
            throw new ApiErrorException(response);
        }

        return dbEntity;
    }

    private AffectedGenomicModel validateSubject(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {
        if (ObjectUtils.isEmpty(uiEntity.getSubject()) || StringUtils.isEmpty(uiEntity.getSubject().getCurie())) {
            addMessageResponse("subject", requiredMessage);
            return null;
        }
        AffectedGenomicModel subjectEntity = affectedGenomicModelDAO.find(uiEntity.getSubject().getCurie());
        if (subjectEntity == null) {
            addMessageResponse("subject", invalidMessage);
            return null;
        }
        return subjectEntity;

    }
    
    private VocabularyTerm validateDiseaseRelation(AGMDiseaseAnnotation uiEntity) {
        String field = "diseaseRelation";
        if (uiEntity.getDiseaseRelation() == null) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        
        VocabularyTerm relation = vocabularyTermDAO.getTermInVocabulary(uiEntity.getDiseaseRelation().getName(), VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY);

        if(relation == null) {
            addMessageResponse(field, invalidMessage);
            return null;
        }
        
        return relation;
    }
}
