package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.ExperimentalConditionValidator;

@RequestScoped
public class ExperimentalConditionService extends BaseCrudService<ExperimentalCondition, ExperimentalConditionDAO> {

    @Inject
    ExperimentalConditionDAO experimentalConditionDAO;
    
    @Inject
    ExperimentalConditionValidator experimentalConditionValidator;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(experimentalConditionDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<ExperimentalCondition> update(ExperimentalCondition uiEntity) {
        ExperimentalCondition dbEntity = experimentalConditionValidator.validateCondition(uiEntity);
        return new ObjectResponse<ExperimentalCondition>(experimentalConditionDAO.persist(dbEntity));
    }
    
}
