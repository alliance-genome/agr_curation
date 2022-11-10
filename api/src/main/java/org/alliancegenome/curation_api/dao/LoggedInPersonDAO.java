package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;

@ApplicationScoped
public class LoggedInPersonDAO extends BaseSQLDAO<LoggedInPerson> {

	protected LoggedInPersonDAO() {
		super(LoggedInPerson.class);
	}

}
