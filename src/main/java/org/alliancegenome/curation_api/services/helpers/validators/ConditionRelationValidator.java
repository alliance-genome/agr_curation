package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.*;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class ConditionRelationValidator extends AuditedObjectValidator<ConditionRelation> {

    @Inject
    ConditionRelationDAO conditionRelationDAO;
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    @Inject
    ExperimentalConditionDAO experimentalConditionDAO;
    
    public ObjectResponse<ConditionRelation> validateConditionRelation(ConditionRelation uiEntity) {
        ConditionRelation conditionRelation = validateConditionRelation(uiEntity, false);
        response.setEntity(conditionRelation);
        return response;
    }
    
    public ConditionRelation validateConditionRelation(ConditionRelation uiEntity, Boolean throwError) {
        response = new ObjectResponse<>(uiEntity);
        String errorTitle = "Could not update ConditionRelation: [" + uiEntity.getId() + "]";
        
        Long id = uiEntity.getId();
        if (id == null) {
            addMessageResponse("No ConditionRelation ID provided");
            throw new ApiErrorException(response);
        }
        ConditionRelation dbEntity = conditionRelationDAO.find(id);
        if (dbEntity == null) {
            addMessageResponse("Could not find ConditionRelation with ID: [" + id + "]");
            throw new ApiErrorException(response);
        }
        
        dbEntity = validateAuditedObjectFields(uiEntity, dbEntity);
        
        VocabularyTerm conditionRelationType = validateConditionRelationType(uiEntity, dbEntity);
        dbEntity.setConditionRelationType(conditionRelationType);
        
        List<ExperimentalCondition> conditions = validateConditions(uiEntity);
        dbEntity.setConditions(conditions);
        
        // TODO: add validation for reference
        if (uiEntity.getSingleReference() != null)
            dbEntity.setSingleReference(uiEntity.getSingleReference());
        
        if (uiEntity.getHandle() != null)
            dbEntity.setHandle(uiEntity.getHandle());
        
        if (response.hasErrors()) {
            if (throwError) {
                response.setErrorMessage(errorTitle);
                throw new ApiErrorException(response);
            } else {
                return null;
            }
        }
        
        return dbEntity;
    }
    
    public VocabularyTerm validateConditionRelationType(ConditionRelation uiEntity, ConditionRelation dbEntity) {
        String field = "conditionRelationType";
        if (uiEntity.getConditionRelationType() == null ) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        
        VocabularyTerm conditionRelationType =
                vocabularyTermDAO.getTermInVocabulary(uiEntity.getConditionRelationType().getName(), 
                        VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
        if (conditionRelationType == null) {
            addMessageResponse(field, invalidMessage);
            return null;
        }
        
        if (conditionRelationType.getObsolete() && !conditionRelationType.getName().equals(dbEntity.getConditionRelationType().getName())) {
            addMessageResponse(field, obsoleteMessage);
            return null;
        }
        
        return conditionRelationType;
    }
    
    public List<ExperimentalCondition> validateConditions(ConditionRelation uiEntity) {
        String field = "conditions";
        if (CollectionUtils.isEmpty(uiEntity.getConditions())) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        
        List<ExperimentalCondition> conditions = new ArrayList<ExperimentalCondition>();
        for (ExperimentalCondition condition : uiEntity.getConditions()) {
            SearchResponse<ExperimentalCondition> conditionResponse =
                    experimentalConditionDAO.findByField("uniqueId", condition.getUniqueId());
            if (conditionResponse == null || conditionResponse.getSingleResult() == null) {
                addMessageResponse(field, invalidMessage);
                return null;
            }
            conditions.add(condition);
        }
        
        return conditions;
    }
}
