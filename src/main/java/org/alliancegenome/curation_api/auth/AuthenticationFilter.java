package org.alliancegenome.curation_api.auth;

import java.io.IOException;
import java.util.UUID;

import org.alliancegenome.curation_api.dao.AllianceMemberDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.model.entities.AllianceMember;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.helpers.PersonUniqueIdHelper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.logging.Log;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.Priority;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	@AuthenticatedUser
	Event<Person> userAuthenticatedEvent;

	@Inject
	PersonDAO personDAO;

	@Inject
	AllianceMemberDAO allianceMemberDAO;

	@Context
	UriInfo info;

	@Inject
	JsonWebToken jsonWebToken;

	@Inject
	JWTParser parser;

	@Inject
	PersonService personService;

	@Inject
	PersonUniqueIdHelper loggedInPersonUniqueId;

	@ConfigProperty(name = "curation.authentication.enabled")
	Instance<Boolean> curationAuthenticationEnabled;

	private static final String USER_ID_FIELD = "username";
	private static final String USER_EMAIL_FIELD = "email";
	private static final String USER_FIRST_NAME_FIELD = "given_name";
	private static final String USER_LAST_NAME_FIELD = "family_name";

	private static final String ADMIN_CLIENTID_FIELD = "client_id";
	private static final String ADMIN_SCOPE_FIELD = "scope";
	private static final String ADMIN_SCOPE_VALUE = "curation-api/admin";

	private static final String ALLIANCE_MEMBER_FIELD = "custom:allianceMember";

	private static final String AUTHENTICATION_BEARER = "Bearer";
	private static final String AUTHENTICATION_APITOKEN = "APIToken";
	private static final String HTTPHEADER_SITE_IDENTITY = "SiteIdentity";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Log.info("Security Filter Firing: ");
		// Log.info("Token: " + jsonWebToken);
		// Log.info("Claim Names: " + jsonWebToken.getClaimNames());

		if (jsonWebToken.getClaimNames() != null) {
			Person person = validateUserTokenById(); // Does not require id token

			if (person == null) {
				person = validateUserTokenByUsernameLookup(requestContext); // Requires id token
			}
			if (person == null) {
				person = validateAdminToken(); // Does not require id token
			}
			if (person != null) {
				userAuthenticatedEvent.fire(person);
			} else {
				failAuthentication(requestContext, AUTHENTICATION_BEARER);
			}
		} else {
			if (curationAuthenticationEnabled.get()) {
				String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
				String apiToken = null;
				if (authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_APITOKEN.toLowerCase())) {
					apiToken = authorizationHeader.substring(AUTHENTICATION_APITOKEN.length()).trim();
					Person person = personService.findPersonByApiToken(apiToken);
					if (person != null) {
						userAuthenticatedEvent.fire(person);
					} else {
						failAuthentication(requestContext, AUTHENTICATION_APITOKEN);
					}
				} else {
					failAuthentication(requestContext, AUTHENTICATION_APITOKEN);
				}
			} else {
				loginDevUser();
			}
		}
	}

	private void failAuthentication(ContainerRequestContext requestContext, String authType) {
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, authType).build());
	}

	private void loginDevUser() {
		Log.info("Cognito Authentication Disabled using Test Dev User");
		Person authenticatedUser = personService.findPersonByAuthenticationEmail("test@alliancegenome.org");
		if (authenticatedUser == null) {
			Person person = new Person();
			person.setApiToken(UUID.randomUUID().toString());
			person.setAuthEmail("test@alliancegenome.org");
			person.setFirstName("Local");
			person.setLastName("Dev User");
			person.setUniqueId("Local|Dev User|test@alliancegenome.org");
			personDAO.persist(person);
			userAuthenticatedEvent.fire(person);
		} else {
			userAuthenticatedEvent.fire(authenticatedUser);
		}
	}

	private Person validateUserTokenById() {
		if (jsonWebToken.getClaim(USER_ID_FIELD) == null) {
			return null;
		}
		Person person = personService.findPersonByAuthenticationId(jsonWebToken.getClaim(USER_ID_FIELD));
		if (person != null) {
			Log.info("Person Found in the db: " + person.getFirstName() + " " + person.getLastName());
		}
		return person;
	}

	private Person validateUserTokenByUsernameLookup(ContainerRequestContext requestContext) {

		if (jsonWebToken.getClaim(USER_ID_FIELD) == null) {
			return null;
		}

		String siteIdentityHeader = requestContext.getHeaderString(HTTPHEADER_SITE_IDENTITY);

		if (siteIdentityHeader != null && siteIdentityHeader.length() > 0) {

			try {
				JsonWebToken jwt = parser.parseOnly(siteIdentityHeader);

				Person authenticatedUser = personService.findPersonByAuthenticationEmail(jwt.getClaim(USER_EMAIL_FIELD));

				if (authenticatedUser != null) {

					authenticatedUser.setAuthId(jwt.getSubject());
					if (authenticatedUser.getAllianceMember() == null) {
						authenticatedUser.setAllianceMember(getAllianceMember(jwt.getClaim(ALLIANCE_MEMBER_FIELD)));
					}
					personDAO.merge(authenticatedUser);
					return authenticatedUser;
				} else {
					Log.info("authenticatedUser not found");
				}

				authenticatedUser = new Person();
				authenticatedUser.setApiToken(UUID.randomUUID().toString());
				authenticatedUser.setAuthId(jwt.getSubject());
				authenticatedUser.setAllianceMember(getAllianceMember(jwt.getClaim(ALLIANCE_MEMBER_FIELD)));
				authenticatedUser.setAuthEmail(jwt.getClaim(USER_EMAIL_FIELD));
				authenticatedUser.setFirstName(jwt.getClaim(USER_FIRST_NAME_FIELD));
				authenticatedUser.setLastName(jwt.getClaim(USER_LAST_NAME_FIELD));
				authenticatedUser.setUniqueId(loggedInPersonUniqueId.createLoggedInPersonUniqueId(authenticatedUser));

				personDAO.persist(authenticatedUser);

				return authenticatedUser;

			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		return null;

	}

	private Person validateAdminToken() {

		String cognitoClientId = (String) jsonWebToken.getClaim(ADMIN_CLIENTID_FIELD);
		String scope = (String) jsonWebToken.getClaim(ADMIN_SCOPE_FIELD);

		if (cognitoClientId != null && !cognitoClientId.isEmpty() && !scope.isEmpty() && scope.equals(ADMIN_SCOPE_VALUE)) {

			Person authenticatedUser = personService.findPersonByAuthenticationId(cognitoClientId);

			if (authenticatedUser != null) {
				Log.info("Cognito Authentication for Admin user via token");
				return authenticatedUser;
			}

			Log.info("Creating admin user for client_id: " + cognitoClientId);
			String adminEmail = "admin@alliancegenome.org";
			Person person = new Person();
			person.setApiToken(UUID.randomUUID().toString());
			person.setAuthId(cognitoClientId);
			person.setAuthEmail(adminEmail);
			person.setFirstName(cognitoClientId);
			person.setLastName("AppClient");
			person.setUniqueId(cognitoClientId + "|AppClient|" + adminEmail);
			personDAO.persist(person);
			return person;
		}

		return null;
	}

	private AllianceMember getAllianceMember(String allianceMemberValue) {
		SearchResponse<AllianceMember> res = allianceMemberDAO.findByField("abbreviation", allianceMemberValue);
		if (res.getResults().size() == 1) {
			return res.getResults().get(0);
		}
		return null;
	}

}
