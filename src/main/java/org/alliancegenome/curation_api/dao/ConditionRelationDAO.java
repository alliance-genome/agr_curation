package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConditionRelationDAO extends BaseSQLDAO<ConditionRelation> {

	protected ConditionRelationDAO() {
		super(ConditionRelation.class);
	}

}
