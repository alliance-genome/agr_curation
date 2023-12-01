package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Reagent;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReagentDAO extends BaseSQLDAO<Reagent> {

	protected ReagentDAO() {
		super(Reagent.class);
	}

}
