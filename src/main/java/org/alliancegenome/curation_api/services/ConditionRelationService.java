package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.ConditionRelationValidator;

@RequestScoped
public class ConditionRelationService extends BaseCrudService<ConditionRelation, ConditionRelationDAO> {

	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	ConditionRelationValidator conditionRelationValidator;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(conditionRelationDAO);
	}
	
	@Override
	@Transactional
	public ObjectResponse<ConditionRelation> update(ConditionRelation uiEntity) {
		ConditionRelation dbEntity = conditionRelationValidator.validateConditionRelation(uiEntity, true);
		return new ObjectResponse<ConditionRelation>(conditionRelationDAO.persist(dbEntity));
	}
	
	public ObjectResponse<ConditionRelation> validate(ConditionRelation uiEntity) {
		ConditionRelation conditionRelation = conditionRelationValidator.validateConditionRelation(uiEntity, true);
		return new ObjectResponse<ConditionRelation>(conditionRelation);
	}
}
