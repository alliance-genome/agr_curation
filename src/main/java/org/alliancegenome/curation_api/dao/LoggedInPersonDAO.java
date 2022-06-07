package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.response.SearchResponse;

@ApplicationScoped
public class LoggedInPersonDAO extends BaseSQLDAO<LoggedInPerson> {

	protected LoggedInPersonDAO() {
		super(LoggedInPerson.class);
	}

}
