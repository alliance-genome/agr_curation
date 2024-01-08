package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExperimentalConditionDAO extends BaseSQLDAO<ExperimentalCondition> {

	protected ExperimentalConditionDAO() {
		super(ExperimentalCondition.class);
	}

}
