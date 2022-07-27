package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.*;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurie;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
	@Inject
	ReferenceValidator referenceValidator;

	private String errorMessage;
	
	public ObjectResponse<ConditionRelation> validateConditionRelation(ConditionRelation uiEntity) {
		ConditionRelation conditionRelation;
		if (uiEntity.getId() == null) {
			conditionRelation = validateConditionRelationCreate(uiEntity, false, false);
		} else {
			conditionRelation = validateConditionRelationUpdate(uiEntity, false, false);
		}
		response.setEntity(conditionRelation);
		return response;
	}

	public ConditionRelation validateConditionRelationUpdate(ConditionRelation uiEntity, Boolean throwError, Boolean checkUniqueness) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update ConditionRelation: [" + uiEntity.getId() + "]";

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
		
		return validateConditionRelation(uiEntity, dbEntity, throwError, checkUniqueness);
	}
	
	public ConditionRelation validateConditionRelationCreate(ConditionRelation uiEntity, Boolean throwError, Boolean checkUniqueness) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create ConditionRelation";
		
		ConditionRelation dbEntity = new ConditionRelation();
		
		return validateConditionRelation(uiEntity, dbEntity, throwError, checkUniqueness);
	}
	
	public ConditionRelation validateConditionRelation(ConditionRelation uiEntity, ConditionRelation dbEntity, Boolean throwError, Boolean checkUniqueness) {
		dbEntity = (ConditionRelation) validateAuditedObjectFields(uiEntity, dbEntity);
		
		String handle = validateHandle(uiEntity, dbEntity);
		dbEntity.setHandle(handle);

		VocabularyTerm conditionRelationType = validateConditionRelationType(uiEntity, dbEntity);
		dbEntity.setConditionRelationType(conditionRelationType);

		List<ExperimentalCondition> conditions = validateConditions(uiEntity);
		dbEntity.setConditions(conditions);

		Reference singleReference = validateSingleReference(uiEntity);
		dbEntity.setSingleReference(singleReference);
		
		String uniqueId = DiseaseAnnotationCurie.getConditionRelationUnique(dbEntity);
		if (checkUniqueness && !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<ConditionRelation> crSearchResponse = conditionRelationDAO.findByField("uniqueId", uniqueId);
			if (crSearchResponse != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
			} else {
				dbEntity.setUniqueId(uniqueId);
			}
		} else {
			dbEntity.setUniqueId(uniqueId);
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorMessage);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

	private String validateHandle(ConditionRelation uiEntity, ConditionRelation dbEntity) {
		// You cannot move from a condition-relation with handle to one without.
		if (!StringUtils.isBlank(dbEntity.getHandle()) && StringUtils.isBlank(uiEntity.getHandle())) {
			addMessageResponse("handle", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		
		if (StringUtils.isBlank(uiEntity.getHandle())) {
			return null;
		}
		
		if (uiEntity.getSingleReference() == null || StringUtils.isBlank(uiEntity.getSingleReference().getCurie())) {
			addMessageResponse("handle", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "singleReference");
			return null;
		}
		
		// if handle / pub combination has changed check that the new key is not already taken in the database
		if (!StringUtils.isBlank(dbEntity.getHandle()) && !getUniqueKey(uiEntity).equals(getUniqueKey(dbEntity))) {
			HashMap<String, HashMap<String, Object>> singleRefFiltermap = new LinkedHashMap<>();
			singleRefFiltermap.put("singleReferenceFilter", getFilterMap("singleReference.curie", getQueryStringMap(uiEntity.getSingleReference().getCurie())));
			singleRefFiltermap.put("handleFilter", getFilterMap("handle", getQueryStringMap(uiEntity.getHandle())));

			SearchResponse<ConditionRelation> response = conditionRelationDAO.searchByParams(new Pagination(), Map.of("searchFilters", singleRefFiltermap));
			if (response.getTotalResults() > 0) {
				addMessageResponse("handle", "Handle / Pub combination already exists");
				return null;
			}
		}
		
		return uiEntity.getHandle();
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


	private Reference validateSingleReference(ConditionRelation uiEntity) {
		String field = "singleReference";
		if(StringUtils.isBlank(uiEntity.getHandle())) {
			return null; // Reference only required if handle present
		}
				
		if (uiEntity.getSingleReference() == null || StringUtils.isBlank(uiEntity.getSingleReference().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity.getSingleReference());
		if (singleRefResponse.getEntity() == null) {
			Map<String, String> errors = singleRefResponse.getErrorMessages();
			for (String refField : errors.keySet()) {
				addMessageResponse(field, refField + " - " + errors.get(refField));
			}
			return null;
		}
		
		return singleRefResponse.getEntity();
	}

	public VocabularyTerm validateConditionRelationType(ConditionRelation uiEntity, ConditionRelation dbEntity) {
		String field = "conditionRelationType";
		if (uiEntity.getConditionRelationType() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
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
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (conditionRelationType.getObsolete() && !conditionRelationType.getName().equals(dbEntity.getConditionRelationType().getName())) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return conditionRelationType;
	}

	public List<ExperimentalCondition> validateConditions(ConditionRelation uiEntity) {
		String field = "conditions";
		if (CollectionUtils.isEmpty(uiEntity.getConditions())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		List<ExperimentalCondition> conditions = new ArrayList<>();
		for (ExperimentalCondition condition : uiEntity.getConditions()) {
			SearchResponse<ExperimentalCondition> conditionResponse =
				experimentalConditionDAO.findByField("uniqueId", condition.getUniqueId());
			if (conditionResponse == null || conditionResponse.getSingleResult() == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			conditions.add(condition);
		}

		return conditions;
	}
}
