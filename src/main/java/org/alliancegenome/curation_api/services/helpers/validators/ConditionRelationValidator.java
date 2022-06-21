package org.alliancegenome.curation_api.services.helpers.validators;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.*;

@RequestScoped
public class ConditionRelationValidator extends AuditedObjectValidator<ConditionRelation> {

	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	LiteratureReferenceDAO literatureReferenceDAO;
	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	ExperimentalConditionDAO experimentalConditionDAO;
	@Inject
	ReferenceService referenceService;

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

		validateConditionRelationHandlePubUnique(uiEntity, dbEntity);

		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity);

		VocabularyTerm conditionRelationType = validateConditionRelationType(uiEntity, dbEntity);
		dbEntity.setConditionRelationType(conditionRelationType);

		List<ExperimentalCondition> conditions = validateConditions(uiEntity);
		dbEntity.setConditions(conditions);

		//validateReferenceField(uiEntity, dbEntity);

		if (StringUtils.isNotEmpty(uiEntity.getHandle())) {
			dbEntity.setHandle(uiEntity.getHandle());
		}
		// You cannot move from a condition-relation with handle to one without.
		if (StringUtils.isNotEmpty(dbEntity.getHandle()) && StringUtils.isEmpty(uiEntity.getHandle())) {
			addMessageResponse("handle", requiredMessage);
		}

		validateReferenceField(uiEntity, dbEntity);

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

	// check that pub-handle combination is unique
	private void validateConditionRelationHandlePubUnique(ConditionRelation uiEntity, ConditionRelation dbEntity) {
		if (StringUtils.isEmpty(uiEntity.getHandle()))
			return;
		// if handle / pub combination has changed check that the new key is not already taken in the database
		if (!getUniqueKey(uiEntity).equals(getUniqueKey(dbEntity))) {
			HashMap<String, HashMap<String, Object>> singleRefFiltermap = new LinkedHashMap<>();
			singleRefFiltermap.put("singleReferenceFilter", getFilterMap("singleReference.curie", getQueryStringMap(uiEntity.getSingleReference().getCurie())));
			singleRefFiltermap.put("handleFilter", getFilterMap("handle", getQueryStringMap(uiEntity.getHandle())));

			SearchResponse<ConditionRelation> response = conditionRelationDAO.searchByParams(new Pagination(), Map.of("searchFilters", singleRefFiltermap));
			if (response.getTotalResults() > 0) {
				addMessageResponse("handle", "Handle / Pub combination already exists");
			}
		}
	}

	private HashMap<String, Object> getQueryStringMap(String value) {
		LinkedHashMap<String, Object> singleRef = new LinkedHashMap<>();
		// use exact matches
		singleRef.put("useKeywordFields", true);
		singleRef.put("queryString", value);
		return singleRef;
	}

	private HashMap<String, Object> getFilterMap(String parameterName, HashMap<String, Object> value) {
		HashMap<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(parameterName, value);
		return parameterMap;
	}

	private String getUniqueKey(ConditionRelation relation) {
		return relation.getHandle() + relation.getSingleReference().getCurie();
	}


	private void validateReferenceField(ConditionRelation uiEntity, ConditionRelation dbEntity) {
		if((uiEntity == null || uiEntity.getSingleReference() == null) && uiEntity.getHandle() == null){
			return;
		}

		if(uiEntity == null || uiEntity.getSingleReference() == null || uiEntity.getSingleReference().getCurie() == null) {
			addMessageResponse("reference", requiredMessage);
			return;
		}
		String curie = uiEntity.getSingleReference().getCurie();

		Reference reference = referenceDAO.find(curie);
		if (reference == null) {
			reference = referenceService.retrieveFromLiteratureService(curie);
			if (reference == null) {
				addMessageResponse("reference", "Invalid publication ID: " + curie);
			}
		}
		dbEntity.setSingleReference(reference);
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
