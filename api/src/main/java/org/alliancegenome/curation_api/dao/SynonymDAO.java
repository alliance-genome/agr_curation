package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Synonym;

@ApplicationScoped
public class SynonymDAO extends BaseSQLDAO<Synonym> {

	protected SynonymDAO() {
		super(Synonym.class);
	}

}
