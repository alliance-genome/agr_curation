package org.alliancegenome.curation_api.auth;

import java.io.IOException;
import java.util.UUID;

import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.services.PersonService;

import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.logging.Log;
import jakarta.annotation.Priority;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
@IfBuildProperty(name = "quarkus.oidc.enabled", stringValue = "false")
public class DevAuthenticationFilter implements ContainerRequestFilter {

	private static final String AUTHENTICATION_APITOKEN = "APIToken";

	@Inject
	@AuthenticatedUser
	Event<Person> userAuthenticatedEvent;

	@Inject
	PersonDAO personDAO;

	@Inject
	PersonService personService;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_APITOKEN.toLowerCase())) {
			String apiToken = authorizationHeader.substring(AUTHENTICATION_APITOKEN.length()).trim();
			Person person = personService.findPersonByApiToken(apiToken);
			if (person != null) {
				userAuthenticatedEvent.fire(person);
			} else {
				failAuthentication(requestContext);
			}
		} else {
			loginDevUser();
		}
	}

	private void failAuthentication(ContainerRequestContext requestContext) {
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_APITOKEN).build());
	}

	private void loginDevUser() {
		Log.debug("OIDC Authentication Disabled using Test Dev User");
		Person authenticatedUser = personService.findPersonByAuthEmail("test@alliancegenome.org");
		if (authenticatedUser == null) {
			Person person = new Person();
			person.setApiToken(UUID.randomUUID().toString());
			person.setEmail("test@alliancegenome.org");
			person.setFirstName("Local");
			person.setLastName("Dev User");
			person.setUniqueId("Local|Dev User|test@alliancegenome.org");
			personDAO.persist(person);
			userAuthenticatedEvent.fire(person);
		} else {
			userAuthenticatedEvent.fire(authenticatedUser);
		}
	}

}
