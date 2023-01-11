package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.interfaces.crud.ConditionRelationCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ConditionRelationService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Optional;


@RequestScoped
public class ConditionRelationCrudController extends BaseEntityCrudController<ConditionRelationService, ConditionRelation, ConditionRelationDAO> implements ConditionRelationCrudInterface {

	@Inject
	ConditionRelationService conditionRelationService;

	@Inject
	ReferenceService referenceService;

	@Override
	@PostConstruct
	protected void init() {
		setService(conditionRelationService);
	}

	public ObjectResponse<ConditionRelation> validate(ConditionRelation entity) {
		return conditionRelationService.validate(entity);
	}

	public ObjectResponse<ConditionRelation> update(ConditionRelation entity) {
		return super.update(entity);
	}

	@Override
	public SearchResponse<ConditionRelation> findExperiments(Integer page, Integer limit, HashMap<String, Object> params) {
		String key = "singleReference.curie";
		String referenceID = (String) params.get(key);
		if (StringUtils.isEmpty(referenceID)) {
			ObjectResponse<ConditionRelation> response = new ObjectResponse<>();
			response.addErrorMessage(key, "Cannot find any reference ID under map key: " + key);
			throw new ApiErrorException(response);
		}
		Reference reference = referenceService.get(referenceID).getEntity();
		if (ObjectUtils.isEmpty(reference)) {
			ObjectResponse<ConditionRelation> response = new ObjectResponse<>();
			response.addErrorMessage(key, "Cannot find reference for given reference ID: " + referenceID);
			throw new ApiErrorException(response);
		}
		SearchResponse<ConditionRelation> conditionRelationSearchResponse = find(page, limit, params);
		Optional<ConditionRelation> standardOptional = conditionRelationSearchResponse.getResults().stream().filter(conditionRelation -> conditionRelation.getHandle().equals(ConditionRelation.Constant.HANDLE_STANDARD)).findFirst();
		Optional<ConditionRelation> genericOptional = conditionRelationSearchResponse.getResults().stream().filter(conditionRelation -> conditionRelation.getHandle().equals(ConditionRelation.Constant.HANDLE_GENERIC_CONTROL)).findFirst();
		// add standard experiments (standard, generic_control) if not present as per ZFIN requirement
		if (standardOptional.isEmpty()) {
			conditionRelationSearchResponse.getResults().add(conditionRelationService.getStandardExperiment(ConditionRelation.Constant.HANDLE_STANDARD, reference));
			conditionRelationSearchResponse.setTotalResults(conditionRelationSearchResponse.getTotalResults() + 1);
			conditionRelationSearchResponse.setReturnedRecords(conditionRelationSearchResponse.getReturnedRecords() + 1);
		}
		if (genericOptional.isEmpty()) {
			conditionRelationSearchResponse.getResults().add(conditionRelationService.getStandardExperiment(ConditionRelation.Constant.HANDLE_GENERIC_CONTROL, reference));
			conditionRelationSearchResponse.setTotalResults(conditionRelationSearchResponse.getTotalResults() + 1);
			conditionRelationSearchResponse.setReturnedRecords(conditionRelationSearchResponse.getReturnedRecords() + 1);
		}
		return conditionRelationSearchResponse;
	}
}
