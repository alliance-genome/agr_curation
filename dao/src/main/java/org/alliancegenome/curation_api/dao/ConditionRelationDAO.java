package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;

@ApplicationScoped
public class ConditionRelationDAO extends BaseSQLDAO<ConditionRelation> {

	protected ConditionRelationDAO() {
		super(ConditionRelation.class);
	}

}
