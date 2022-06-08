package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.interfaces.crud.ExperimentalConditionCrudInterface;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.ExperimentalConditionService;

@RequestScoped
public class ExperimentalConditionController extends BaseCrudController<ExperimentalConditionService, ExperimentalCondition, ExperimentalConditionDAO> implements ExperimentalConditionCrudInterface {

	@Inject ExperimentalConditionService experimentalConditionService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(experimentalConditionService);
	}
	
	@Override
	public ObjectResponse<ExperimentalCondition> get(String conditionStatement) {
		SearchResponse<ExperimentalCondition> ret = findByField("conditionStatement", conditionStatement);
		if(ret != null && ret.getTotalResults() == 1) {
			return new ObjectResponse<ExperimentalCondition>(ret.getResults().get(0));
		} else {
			return new ObjectResponse<ExperimentalCondition>();
		}
	}

}
