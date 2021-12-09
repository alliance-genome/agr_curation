package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.*;

@RequestScoped
public class AGMDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

    @Inject
    AffectedGenomicModelDAO affectedGenomicModelDAO;
    
    @Inject
    AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;

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

        DOTerm term = validateObject(uiEntity, dbEntity);
        if(term != null) dbEntity.setObject(term);

        List<EcoTerm> terms = validateEvidenceCodes(uiEntity, dbEntity);
        if(terms != null) dbEntity.setEvidenceCodes(terms);

        DiseaseRelation relation = validateDiseaseRelation(uiEntity, dbEntity);
        if(relation != null) dbEntity.setDiseaseRelation(relation);

        List<Gene> genes = validateWith(uiEntity, dbEntity);
        if(genes != null) dbEntity.setWith(genes);

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
    
    private DiseaseRelation validateDiseaseRelation(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {
        String field = "diseaseRelation";
        if (StringUtils.isEmpty(uiEntity.getDiseaseRelation().toString())) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        
        DiseaseRelation relation = uiEntity.getDiseaseRelation();

        if(relation == DiseaseRelation.is_model_of) {
            return relation;
        } else {
            addMessageResponse(field, invalidMessage);
            return null;
        }
        
    }
}
