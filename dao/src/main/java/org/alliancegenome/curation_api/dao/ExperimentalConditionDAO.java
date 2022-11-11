package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;

@ApplicationScoped
public class ExperimentalConditionDAO extends BaseSQLDAO<ExperimentalCondition> {

	protected ExperimentalConditionDAO() {
		super(ExperimentalCondition.class);
	}

}
