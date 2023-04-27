package org.alliancegenome.curation_api.auth;


import org.alliancegenome.curation_api.model.entities.Person;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;

@RequestScoped
public class AuthenticatedUserProducer {

	@Produces
	@RequestScoped
	@AuthenticatedUser
	Person authenticatedPerson = new Person();

	public void handleAuthenticationEvent(@Observes @AuthenticatedUser Person authenticatedPerson) {
		this.authenticatedPerson = authenticatedPerson;
	}
}
