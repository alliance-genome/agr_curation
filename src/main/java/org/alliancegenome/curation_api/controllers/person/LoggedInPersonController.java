package org.alliancegenome.curation_api.controllers.person;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.interfaces.person.LoggedInPersonInterface;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.LoggedInPersonService;

@RequestScoped
public class LoggedInPersonController implements LoggedInPersonInterface {

	@Inject @AuthenticatedUser LoggedInPerson authenticatedPerson;
	@Inject LoggedInPersonService loggedInPersonService;
	
	public ObjectResponse<LoggedInPerson> create(LoggedInPerson person) {
		return loggedInPersonService.create(person);
	}
	
	@Override
	public LoggedInPerson getLoggedInPerson() {
		return authenticatedPerson;
	}

	@Override
	public LoggedInPerson regenApiToken() {
		return loggedInPersonService.regenApiToken();
	}
}
