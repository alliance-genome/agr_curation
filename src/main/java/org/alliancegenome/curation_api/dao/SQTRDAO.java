package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.SQTR;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SQTRDAO extends BaseSQLDAO<SQTR> {

	protected SQTRDAO() {
		super(SQTR.class);
	}

}
