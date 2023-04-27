package org.alliancegenome.curation_api.controllers.crud;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.interfaces.crud.ExperimentalConditionCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ExperimentalConditionService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ExperimentalConditionController extends BaseEntityCrudController<ExperimentalConditionService, ExperimentalCondition, ExperimentalConditionDAO>
	implements ExperimentalConditionCrudInterface {

	@Inject
	ExperimentalConditionService experimentalConditionService;

	@Override
	@PostConstruct
	protected void init() {
		setService(experimentalConditionService);
	}

	@Override
	public ObjectResponse<ExperimentalCondition> get(String conditionSummary) {
		SearchResponse<ExperimentalCondition> ret = findByField("conditionSummary", conditionSummary);
		if (ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<ExperimentalCondition>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<ExperimentalCondition>();
		}
	}

}
