package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;

@RequestScoped
public class ExperimentalConditionService extends BaseCrudService<ExperimentalCondition, ExperimentalConditionDAO> {

    @Inject
    ExperimentalConditionDAO experimentalConditionDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(experimentalConditionDAO);
    }
    
}
