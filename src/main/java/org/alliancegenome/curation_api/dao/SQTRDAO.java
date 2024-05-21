package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Synonym;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SQTRDAO extends BaseSQLDAO<Synonym> {

	protected SQTRDAO() {
		super(Synonym.class);
	}

}
