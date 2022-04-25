package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.*;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class ConditionRelationValidator {

    @Inject
    ConditionRelationDAO conditionRelationDAO;
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    @Inject
    ExperimentalConditionDAO experimentalConditionDAO;
    
    
    
    protected String invalidMessage = "Not a valid entry";
    protected String obsoleteMessage = "Obsolete term specified";
    protected String requiredMessage = "Required field is empty";
    
    protected ObjectResponse<ConditionRelation> response;
    
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
        
    protected void addMessageResponse(String message) {
        response.setErrorMessage(message);
    }
    
    protected void addMessageResponse(String fieldName, String message) {
        response.addErrorMessage(fieldName, message);
    }
}
