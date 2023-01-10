package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.ZecoTermDAO;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.ConditionRelationValidator;
import org.elasticsearch.common.collect.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;

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
		HashMap<String, Object> params = new HashMap<>();
		params.put("curie", "ZECO:0000103");
		condition.setConditionClass(zecoTermDAO.findByParams(null, params).getSingleResult());
		conditionRelation.setConditions(List.of(condition));
		return conditionRelation;
	}
}
