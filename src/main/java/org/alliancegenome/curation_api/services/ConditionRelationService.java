package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;

@RequestScoped
public class ConditionRelationService extends BaseCrudService<ConditionRelation, ConditionRelationDAO> {

    @Inject
    ConditionRelationDAO conditionRelationDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(conditionRelationDAO);
    }
    
}
