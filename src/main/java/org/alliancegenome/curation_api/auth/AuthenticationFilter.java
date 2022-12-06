package org.alliancegenome.curation_api.auth;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.alliancegenome.curation_api.dao.AllianceMemberDAO;
import org.alliancegenome.curation_api.dao.LoggedInPersonDAO;
import org.alliancegenome.curation_api.model.entities.AllianceMember;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.LoggedInPersonService;
import org.alliancegenome.curation_api.services.helpers.persons.LoggedInPersonUniqueIdHelper;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerificationException;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.group.Group;
import com.okta.sdk.resource.group.GroupList;
import com.okta.sdk.resource.user.User;

import io.quarkus.logging.Log;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	@AuthenticatedUser
	Event<LoggedInPerson> userAuthenticatedEvent;

	@Inject
	AuthenticationService authenticationService;

	@Inject
	LoggedInPersonDAO loggedInPersonDAO;

	@Inject
	AllianceMemberDAO allianceMemberDAO;

	@Inject
	LoggedInPersonService loggedInPersonService;

	@Inject
	LoggedInPersonUniqueIdHelper loggedInPersonUniqueId;

	@ConfigProperty(name = "okta.authentication")
	Instance<Boolean> okta_auth;

	@ConfigProperty(name = "okta.url")
	Instance<String> okta_url;

	@ConfigProperty(name = "okta.client.id")
	Instance<String> client_id;

	@ConfigProperty(name = "okta.client.secret")
	Instance<String> client_secret;

	@ConfigProperty(name = "okta.api.token")
	Instance<String> api_token;

	// private static final String REALM = "AGR";
	private static final String AUTHENTICATION_SCHEME = "Bearer";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Logic:
		// if okta_auth is off then we are in test mode
		// if okta_auth is on but no okta_creds we are in Test mode (Integration
		// Testing)
		// if okta_auth is on and we have okta_creds validate(token), else fail

		if (okta_auth.get()) {
			if (!okta_url.get().equals("\"\"") && !client_id.get().equals("\"\"") && !client_secret.get().equals("\"\"") && !api_token.get().equals("\"\"")) {

				String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

				if (authorizationHeader == null || !authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ")) {
					failAuthentication(requestContext);
				} else {
					String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
					LoggedInPerson person = validateToken(token);
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

	private LoggedInPerson validateLocalToken(String token) {
		SearchResponse<LoggedInPerson> res = loggedInPersonDAO.findByField("apiToken", token);
		if (res != null && res.getResults().size() == 1) {
			Log.info("User Found in local DB via: " + token);
			return res.getResults().get(0);
		}
		return null;
	}

	private void failAuthentication(ContainerRequestContext requestContext) {
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME).build());
	}

	private void loginDevUser() {
		log.debug("OKTA Authentication Disabled using Test Dev User");
		LoggedInPerson authenticatedUser = loggedInPersonService.findLoggedInPersonByOktaEmail("test@alliancegenome.org");
		if (authenticatedUser == null) {
			LoggedInPerson person = new LoggedInPerson();
			person.setApiToken(UUID.randomUUID().toString());
			person.setOktaEmail("test@alliancegenome.org");
			person.setFirstName("Local");
			person.setLastName("Dev User");
			person.setUniqueId("Local|Dev User|test@alliancegenome.org");
			loggedInPersonDAO.persist(person);
			userAuthenticatedEvent.fire(person);
		} else {
			userAuthenticatedEvent.fire(authenticatedUser);
		}
	}

	// Check Okta(token), Check DB ApiToken(token), else return null
	private LoggedInPerson validateToken(String token) {

		Jwt jsonWebToken;
		try {
			jsonWebToken = authenticationService.verifyToken(token);
		} catch (JwtVerificationException e) {
			LoggedInPerson person = validateLocalToken(token);
			return person;
		}

		String oktaEmail = (String) jsonWebToken.getClaims().get("sub"); // Subject Id
		String oktaId = (String) jsonWebToken.getClaims().get("uid"); // User Id
		LoggedInPerson authenticatedUser = loggedInPersonService.findLoggedInPersonByOktaEmail(oktaEmail);

		if (authenticatedUser != null) {
			if (authenticatedUser.getAllianceMember() == null) {
				User user = getOktaUser(oktaId);
				authenticatedUser.setAllianceMember(getAllianceMember(user.listGroups()));
				loggedInPersonDAO.persist(authenticatedUser);
			}
			return authenticatedUser;
		}

		Log.info("Making OKTA call to get user info: ");

		User user = getOktaUser(oktaId);

		if (user != null) {
			LoggedInPerson person = new LoggedInPerson();
			person.setApiToken(UUID.randomUUID().toString());
			person.setOktaId(oktaId);
			person.setAllianceMember(getAllianceMember(user.listGroups()));
			person.setOktaEmail(user.getProfile().getEmail());
			person.setFirstName(user.getProfile().getFirstName());
			person.setLastName(user.getProfile().getLastName());
			person.setUniqueId(loggedInPersonUniqueId.createLoggedInPersonUniqueId(person));
			loggedInPersonDAO.persist(person);
			return person;

		}

		return null;
	}

	private User getOktaUser(String oktaId) {
		Client client = Clients.builder().setOrgUrl(okta_url.get()).setClientId(client_id.get()).setClientCredentials(new TokenClientCredentials(api_token.get())).build();

		return client.getUser(oktaId);
	}

	private AllianceMember getAllianceMember(GroupList groupList) {
		for (Group group : groupList) {
			String allianceMember = (String) group.getProfile().get("affiliated_alliance_member");
			if (allianceMember != null) {
				SearchResponse<AllianceMember> res = allianceMemberDAO.findByField("uniqueId", allianceMember);
				if (res.getTotalResults() == 1) {
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