package org.alliancegenome.curation_api.services;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.LoggedInPersonDAO;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class LoggedInPersonService extends BaseEntityCrudService<LoggedInPerson, LoggedInPersonDAO> {

	@Inject
	LoggedInPersonDAO loggedInPersonDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(loggedInPersonDAO);
	}

	public LoggedInPerson findLoggedInPersonByOktaEmail(String email) {
		SearchResponse<LoggedInPerson> resp = loggedInPersonDAO.findByField("oktaEmail", email);
		if (resp != null && resp.getTotalResults() == 1) {
			return resp.getSingleResult();
		}

		return null;
	}

	@Transactional
	public LoggedInPerson regenApiToken() {
		LoggedInPerson user = loggedInPersonDAO.find(authenticatedPerson.getId());
		user.setApiToken(UUID.randomUUID().toString());
		return user;
	}

}
