package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.controllers.BaseCrudController;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.interfaces.crud.ConditionRelationCrudInterface;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ConditionRelationService;

import java.util.HashMap;

@RequestScoped
public class ConditionRelationCrudController extends BaseCrudController<ConditionRelationService, ConditionRelation, ConditionRelationDAO> implements ConditionRelationCrudInterface {

    @Inject ConditionRelationService conditionRelationService;

    @Override
    @PostConstruct
    protected void init() {
        setService(conditionRelationService);
    }

    public ObjectResponse<ConditionRelation> validate(ConditionRelation entity) {
        return conditionRelationService.validate(entity);
    }

	@Override
	public SearchResponse<ConditionRelation> findHandles(Integer page, Integer limit, HashMap<String, Object> params) {
		if(params == null) params = new HashMap<>();

		Pagination pagination = new Pagination();
		pagination.setLimit(limit);
		pagination.setPage(page);
		return conditionRelationService.searchByParams(pagination, params, "handle");
	}

}
