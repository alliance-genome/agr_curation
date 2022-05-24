package org.alliancegenome.curation_api.services.helpers.validators;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.dao.LiteratureReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.document.LiteratureReference;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class ConditionRelationValidator extends AuditedObjectValidator<ConditionRelation> {

   @Inject
   ConditionRelationDAO conditionRelationDAO;
   @Inject
   VocabularyTermDAO vocabularyTermDAO;
   @Inject
   LiteratureReferenceDAO literatureReferenceDAO;
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

      //validateReferenceField(uiEntity, dbEntity);

      if (StringUtils.isNotEmpty(uiEntity.getHandle())) {
			dbEntity.setHandle(uiEntity.getHandle());
		} else {
			addMessageResponse("handle", requiredMessage);
		}


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

   private void validateReferenceField(ConditionRelation uiEntity, ConditionRelation dbEntity) {
      if (uiEntity.getSingleReference() == null || uiEntity.getSingleReference().getCurie() == null) {
         addMessageResponse("reference", requiredMessage);
         return;
      }

      LiteratureReference reference = literatureReferenceDAO.find(uiEntity.getSingleReference().getCurie());
      if (reference != null) {
         dbEntity.setSingleReference(uiEntity.getSingleReference());
         return;
      }
      addMessageResponse("reference", invalidMessage);
   }

   public VocabularyTerm validateConditionRelationType(ConditionRelation uiEntity, ConditionRelation dbEntity) {
      String field = "conditionRelationType";
      if (uiEntity.getConditionRelationType() == null) {
         addMessageResponse(field, requiredMessage);
         return null;
      }

      VocabularyTerm conditionRelationType = null;
      // first check if an id is provided. If not then check for term Name
      if (uiEntity.getConditionRelationType().getId() != null) {
         conditionRelationType = vocabularyTermDAO.find(uiEntity.getConditionRelationType().getId());
      } else if (uiEntity.getConditionRelationType().getName() != null) {
         conditionRelationType =
            vocabularyTermDAO.getTermInVocabulary(uiEntity.getConditionRelationType().getName(),
               VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
      }
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

      List<ExperimentalCondition> conditions = new ArrayList<>();
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
