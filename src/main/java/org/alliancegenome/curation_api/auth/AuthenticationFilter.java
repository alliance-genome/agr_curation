package org.alliancegenome.curation_api.auth;

import java.io.IOException;
import java.util.UUID;

import org.alliancegenome.curation_api.dao.AllianceMemberDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.model.entities.AllianceMember;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.helpers.persons.PersonUniqueIdHelper;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerificationException;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.application.Application;
import com.okta.sdk.resource.group.Group;
import com.okta.sdk.resource.group.GroupList;
import com.okta.sdk.resource.user.User;

import io.quarkus.logging.Log;
import jakarta.annotation.Priority;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	@AuthenticatedUser Event<Person> userAuthenticatedEvent;

	@Inject AuthenticationService authenticationService;

	@Inject PersonDAO personDAO;

	@Inject AllianceMemberDAO allianceMemberDAO;

	@Inject PersonService personService;

	@Inject PersonUniqueIdHelper loggedInPersonUniqueId;

	@ConfigProperty(name = "okta.authentication") Instance<Boolean> oktaAuth;

	@ConfigProperty(name = "okta.url") Instance<String> oktaUrl;

	@ConfigProperty(name = "okta.client.id") Instance<String> clientId;

	@ConfigProperty(name = "okta.client.secret") Instance<String> clientSecret;

	@ConfigProperty(name = "okta.api.token") Instance<String> apiToken;

	// private static final String REALM = "AGR";
	private static final String AUTHENTICATION_SCHEME = "Bearer";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Logic:
		// if okta_auth is off then we are in test mode
		// if okta_auth is on but no okta_creds we are in Test mode (Integration
		// Testing)
		// if okta_auth is on and we have okta_creds validate(token), else fail

		if (oktaAuth.get()) {
			if (!oktaUrl.get().equals("\"\"") && !clientId.get().equals("\"\"") && !clientSecret.get().equals("\"\"") && !apiToken.get().equals("\"\"")) {

				String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

				if (authorizationHeader == null || !authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ")) {
					failAuthentication(requestContext);
				} else {
					String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

					Person person = null;

					try {
						Jwt jsonWebToken = authenticationService.verifyToken(token);

						if (person == null) {
							person = validateUserToken(jsonWebToken);
						}
						if (person == null) {
							person = validateAdminToken(jsonWebToken);
						}
					} catch (JwtVerificationException e) {
						person = personService.findPersonByApiToken(token);
					}

					if (person != null) {
						userAuthenticatedEvent.fire(person);
					} else {
						failAuthentication(requestContext);
					}
				}

			} else {
				// Test / Dev Mode
				loginDevUser();
			}
		} else {
			// Test / Dev Mode
			loginDevUser();
		}

	}

	private void failAuthentication(ContainerRequestContext requestContext) {
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME).build());
	}

	private void loginDevUser() {
		log.debug("OKTA Authentication Disabled using Test Dev User");
		Person authenticatedUser = personService.findPersonByOktaEmail("test@alliancegenome.org");
		if (authenticatedUser == null) {
			Person person = new Person();
			person.setApiToken(UUID.randomUUID().toString());
			person.setOktaEmail("test@alliancegenome.org");
			person.setFirstName("Local");
			person.setLastName("Dev User");
			person.setUniqueId("Local|Dev User|test@alliancegenome.org");
			personDAO.persist(person);
			userAuthenticatedEvent.fire(person);
		} else {
			userAuthenticatedEvent.fire(authenticatedUser);
		}
	}

	// Check Okta(token), Check DB ApiToken(token), else return null
	private Person validateUserToken(Jwt jsonWebToken) {

		String oktaUserId = (String) jsonWebToken.getClaims().get("uid"); // User Id

		if (oktaUserId != null && oktaUserId.length() > 0) {
			String oktaEmail = (String) jsonWebToken.getClaims().get("sub"); // Subject Id

			Person authenticatedUser = personService.findPersonByOktaEmail(oktaEmail);

			if (authenticatedUser != null) {
				if (authenticatedUser.getAllianceMember() == null) {
					User user = getOktaUser(oktaUserId);
					authenticatedUser.setAllianceMember(getAllianceMember(user.listGroups()));
					personDAO.persist(authenticatedUser);
				}
				return authenticatedUser;
			}

			Log.info("Making OKTA call to get user info: ");

			User user = getOktaUser(oktaUserId);

			if (user != null) {
				Person person = new Person();
				person.setApiToken(UUID.randomUUID().toString());
				person.setOktaId(oktaUserId);
				person.setAllianceMember(getAllianceMember(user.listGroups()));
				person.setOktaEmail(user.getProfile().getEmail());
				person.setFirstName(user.getProfile().getFirstName());
				person.setLastName(user.getProfile().getLastName());
				person.setUniqueId(loggedInPersonUniqueId.createLoggedInPersonUniqueId(person));
				personDAO.persist(person);
				return person;
			}
		}

		return null;
	}

	private Person validateAdminToken(Jwt jsonWebToken) {

		String oktaClientId = (String) jsonWebToken.getClaims().get("cid"); // Client Id

		if (oktaClientId != null && oktaClientId.length() > 0) {

			Person authenticatedUser = personService.findPersonByOktaId(oktaClientId);

			if (authenticatedUser != null) {
				return authenticatedUser;
			}

			Log.info("Making OKTA call to get app info: ");

			Application app = getOktaClient(oktaClientId);

			if (app != null) {
				log.debug("OKTA Authentication for Admin user via token");
				String adminEmail = "admin@alliancegenome.org";
				Person person = new Person();
				person.setApiToken(UUID.randomUUID().toString());
				person.setOktaId(app.getId());
				person.setOktaEmail(adminEmail);
				person.setFirstName(app.getLabel());
				person.setLastName(app.getName());
				person.setUniqueId(app.getLabel() + "|" + app.getName() + "|" + adminEmail);
				personDAO.persist(person);
				return person;
			}
		}

		return null;
	}

	private User getOktaUser(String oktaId) {
		Client client = Clients.builder().setOrgUrl(oktaUrl.get()).setClientId(clientId.get()).setClientCredentials(new TokenClientCredentials(apiToken.get())).build();
		return client.getUser(oktaId);
	}

	private Application getOktaClient(String applicationId) {
		Client client = Clients.builder().setOrgUrl(oktaUrl.get()).setClientId(clientId.get()).setClientCredentials(new TokenClientCredentials(apiToken.get())).build();
		return client.getApplication(applicationId);
	}

	private AllianceMember getAllianceMember(GroupList groupList) {
		for (Group group : groupList) {
			String allianceMember = (String) group.getProfile().get("affiliated_alliance_member");
			if (allianceMember != null) {
				SearchResponse<AllianceMember> res = allianceMemberDAO.findByField("uniqueId", allianceMember);
				if (res.getResults().size() == 1) {
					AllianceMember member = res.getResults().get(0);
					return member;
				} else {
					log.info("Alliance Look up error: more than one member found");
				}
			}
		}
		return null;
	}

}