package org.alliancegenome.curation_api.auth;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import org.alliancegenome.curation_api.model.entities.LoggedInPerson;

@RequestScoped
public class AuthenticatedUserProducer {

	@Produces
	@RequestScoped
	@AuthenticatedUser
	LoggedInPerson authenticatedPerson = new LoggedInPerson();

	public void handleAuthenticationEvent(@Observes @AuthenticatedUser LoggedInPerson authenticatedPerson) {
		this.authenticatedPerson = authenticatedPerson;
	}
}
