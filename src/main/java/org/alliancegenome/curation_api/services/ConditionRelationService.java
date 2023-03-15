package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.ConditionRelationValidator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.collect.List;

@RequestScoped
public class ConditionRelationService extends BaseEntityCrudService<ConditionRelation, ConditionRelationDAO> {

	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	ConditionRelationValidator conditionRelationValidator;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	ZecoTermDAO zecoTermDAO;
	@Inject
	ReferenceService referenceService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(conditionRelationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<ConditionRelation> update(ConditionRelation uiEntity) {
		ConditionRelation dbEntity = conditionRelationValidator.validateConditionRelationUpdate(uiEntity, true, true);
		return new ObjectResponse<>(conditionRelationDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<ConditionRelation> create(ConditionRelation uiEntity) {
		ConditionRelation dbEntity = conditionRelationValidator.validateConditionRelationCreate(uiEntity, true, true);
		return new ObjectResponse<>(conditionRelationDAO.persist(dbEntity));
	}

	public ObjectResponse<ConditionRelation> validate(ConditionRelation uiEntity) {
		ConditionRelation conditionRelation;
		if (uiEntity.getId() == null) {
			conditionRelation = conditionRelationValidator.validateConditionRelationCreate(uiEntity, true, false);
		} else {
			conditionRelation = conditionRelationValidator.validateConditionRelationUpdate(uiEntity, true, false);
		}
		return new ObjectResponse<>(conditionRelation);
	}

	public ConditionRelation getStandardExperiment(String experimentName, Reference reference) {
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle(experimentName);
		conditionRelation.setSingleReference(reference);
		conditionRelation.setConditionRelationType(vocabularyTermDAO.getTermInVocabulary("Condition relation types", "has_condition"));
		ExperimentalCondition condition = new ExperimentalCondition();
		condition.setUniqueId("ZECO:0000103");
		HashMap<String, Object> params = new HashMap<>();
		params.put("curie", "ZECO:0000103");
		condition.setConditionClass(zecoTermDAO.findByParams(null, params).getSingleResult());
		conditionRelation.setConditions(List.of(condition));
		return conditionRelation;
	}

	public SearchResponse<ConditionRelation> getConditionRelationSearchResponse(HashMap<String, Object> params) {
		String key = "singleReference.curie";
		String referenceID = (String) params.get(key);
		if (StringUtils.isEmpty(referenceID)) {
			ObjectResponse<ConditionRelation> response = new ObjectResponse<>();
			response.addErrorMessage(key, "Cannot find any reference ID under map key: " + key);
			throw new ApiErrorException(response);
		}
		Reference reference = referenceService.retrieveFromDbOrLiteratureService(referenceID);
		if (ObjectUtils.isEmpty(reference)) {
			ObjectResponse<ConditionRelation> response = new ObjectResponse<>();
			response.addErrorMessage(key, "Cannot find reference for given reference ID: " + referenceID);
			throw new ApiErrorException(response);
		}
		SearchResponse<ConditionRelation> conditionRelationSearchResponse = conditionRelationDAO.findByField(key, referenceID);
		Optional<ConditionRelation> standardOptional = Optional.empty();
		Optional<ConditionRelation> genericOptional = Optional.empty();
		// add standard experiments (standard, generic_control) if not present as per ZFIN requirement
		if (conditionRelationSearchResponse != null) {
			standardOptional = conditionRelationSearchResponse.getResults().stream().filter(conditionRelation -> conditionRelation.getHandle().equals(ConditionRelation.Constant.HANDLE_STANDARD)).findFirst();
			genericOptional = conditionRelationSearchResponse.getResults().stream().filter(conditionRelation -> conditionRelation.getHandle().equals(ConditionRelation.Constant.HANDLE_GENERIC_CONTROL)).findFirst();
		} else {
			conditionRelationSearchResponse = new SearchResponse<>();
		}
		if (standardOptional.isEmpty()) {
			conditionRelationSearchResponse.getResults().add(getStandardExperiment(ConditionRelation.Constant.HANDLE_STANDARD, reference));
		}
		if (genericOptional.isEmpty()) {
			conditionRelationSearchResponse.getResults().add(getStandardExperiment(ConditionRelation.Constant.HANDLE_GENERIC_CONTROL, reference));
		}
		if (genericOptional.isEmpty() || standardOptional.isEmpty()) {
			conditionRelationSearchResponse.setTotalResults(conditionRelationSearchResponse.getTotalResults() == null ? 1 : conditionRelationSearchResponse.getTotalResults() + 1);
			conditionRelationSearchResponse.setReturnedRecords(conditionRelationSearchResponse.getReturnedRecords() == null ? 1 : conditionRelationSearchResponse.getReturnedRecords() + 1);
		}
		return conditionRelationSearchResponse;
	}
}
