package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.*;

@RequestScoped
public class AlleleDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

    @Inject
    AlleleDAO alleleDAO;
    
    @Inject
    AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
    
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    
    private String ALLELE_DISEASE_RELATION_VOCABULARY = "Allele disease relations";

    public AlleleDiseaseAnnotation validateAnnotation(AlleleDiseaseAnnotation uiEntity) {
        response = new ObjectResponse<>(uiEntity);
        String errorTitle = "Could not update Gene Disease Annotation: [" + uiEntity.getId() + "]";

        Long id = uiEntity.getId();
        if (id == null) {
            addMessageResponse("No Gene Disease Annotation ID provided");
            throw new ApiErrorException(response);
        }
        AlleleDiseaseAnnotation dbEntity = alleleDiseaseAnnotationDAO.find(id);
        if (dbEntity == null) {
            addMessageResponse("Could not find Gene Disease Annotation with ID: [" + id + "]");
            throw new ApiErrorException(response);
            // do not continue validation for update if Disease Annotation ID has not been found
        }       
        
        Allele subject = validateSubject(uiEntity, dbEntity);
        if(subject != null) dbEntity.setSubject(subject);

        VocabularyTerm relation = validateDiseaseRelation(uiEntity);
        if(relation != null) dbEntity.setDiseaseRelation(relation);

        dbEntity = (AlleleDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);
        
        if (response.hasErrors()) {
            response.setErrorMessage(errorTitle);
            throw new ApiErrorException(response);
        }

        return dbEntity;
    }

    private Allele validateSubject(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {
        if (ObjectUtils.isEmpty(uiEntity.getSubject()) || StringUtils.isEmpty(uiEntity.getSubject().getCurie())) {
            addMessageResponse("subject", requiredMessage);
            return null;
        }
        Allele subjectEntity = alleleDAO.find(uiEntity.getSubject().getCurie());
        if (subjectEntity == null) {
            addMessageResponse("subject", invalidMessage);
            return null;
        }
        return subjectEntity;

    }
    
    private VocabularyTerm validateDiseaseRelation(AlleleDiseaseAnnotation uiEntity) {
        String field = "diseaseRelation";
        if (uiEntity.getDiseaseRelation() == null) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        
        VocabularyTerm relation = vocabularyTermDAO.getTermInVocabulary(uiEntity.getDiseaseRelation().getName(), ALLELE_DISEASE_RELATION_VOCABULARY);

        if(relation == null) {
            addMessageResponse(field, invalidMessage);
            return null;
        }
        
        return relation;
    }
}
