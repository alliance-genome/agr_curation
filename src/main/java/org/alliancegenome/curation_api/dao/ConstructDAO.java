package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Construct;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConstructDAO extends BaseSQLDAO<Construct> {
	
	protected ConstructDAO() {
		super(Construct.class);
	}

}
