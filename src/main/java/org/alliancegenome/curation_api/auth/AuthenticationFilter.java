package org.alliancegenome.curation_api.auth;

import java.io.IOException;
import java.util.List;
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

import com.nimbusds.jwt.JWTClaimsSet;

import io.quarkus.logging.Log;
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
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GroupType;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	@AuthenticatedUser
	Event<Person> userAuthenticatedEvent;

	@Inject
	AuthenticationService authenticationService;

	@Inject
	PersonDAO personDAO;

	@Inject
	AllianceMemberDAO allianceMemberDAO;

	@Context
	UriInfo info;

	@Inject
	JsonWebToken jsonWebToken;

	@Inject
	PersonService personService;

	@Inject
	PersonUniqueIdHelper loggedInPersonUniqueId;

	@ConfigProperty(name = "cognito.authentication")
	Instance<Boolean> cognitoAuth;

	@ConfigProperty(name = "cognito.user.pool.id")
	Instance<String> userPoolId;

	@ConfigProperty(name = "cognito.region")
	Instance<String> region;

	@ConfigProperty(name = "cognito.client.id")
	Instance<String> clientId;

	@ConfigProperty(name = "cognito.admin.client.ids")
	Instance<String> adminClientIds;

	private static final String AUTHENTICATION_BEARER = "Bearer";
	private static final String AUTHENTICATION_APITOKEN = "APIToken";
	private static final String TOKEN_COOKIE_NAME = "cognito-token-cookie";

	private CognitoIdentityProviderClient cognitoClient;

	private CognitoIdentityProviderClient getCognitoClient() {
		if (cognitoClient == null) {
			cognitoClient = CognitoIdentityProviderClient.builder()
				.region(Region.of(region.get()))
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build();
		}
		return cognitoClient;
	}

	@jakarta.annotation.PreDestroy
	public void cleanup() {
		if (cognitoClient != null) {
			cognitoClient.close();
			Log.info("Cognito client closed");
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (jsonWebToken.getClaimNames() != null) {
			Person person = null;

			// SECURITY FIX: Extract raw token and verify with AuthenticationService
			String rawToken = extractRawToken(requestContext);
			if (rawToken == null) {
				Log.warn("JWT claims present but raw token not found");
				failAuthentication(requestContext, AUTHENTICATION_BEARER);
				return;
			}

			try {
				// First, try to verify as a user token
				try {
					JWTClaimsSet userClaims = authenticationService.verifyUserToken(rawToken);
					person = validateUserToken(userClaims);
				} catch (Exception userTokenEx) {
					// Not a valid user token, try client credentials
					Log.debug("Token is not a valid user token, trying client credentials: " + userTokenEx.getMessage());
					try {
						JWTClaimsSet adminClaims = authenticationService.verifyClientCredentialsToken(rawToken);
						person = validateAdminToken(adminClaims);
					} catch (Exception adminTokenEx) {
						Log.error("Token verification failed for both user and admin: " + adminTokenEx.getMessage());
						throw adminTokenEx;
					}
				}
			} catch (Exception e) {
				Log.error("Token verification failed", e);
				failAuthentication(requestContext, AUTHENTICATION_BEARER);
				return;
			}

			if (person != null) {
				userAuthenticatedEvent.fire(person);
			} else {
				failAuthentication(requestContext, AUTHENTICATION_BEARER);
			}
		} else {
			if (cognitoAuth.get()) {
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

	private String extractRawToken(ContainerRequestContext requestContext) {
		// Try to get token from cookie first
		String cookieHeader = requestContext.getHeaderString(HttpHeaders.COOKIE);
		if (cookieHeader != null) {
			String[] cookies = cookieHeader.split(";");
			for (String cookie : cookies) {
				String trimmedCookie = cookie.trim();
				if (trimmedCookie.startsWith(TOKEN_COOKIE_NAME + "=")) {
					return trimmedCookie.substring(TOKEN_COOKIE_NAME.length() + 1);
				}
			}
		}

		// Fallback to Authorization header
		String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
			return authHeader.substring(7);
		}

		return null;
	}

	private void loginDevUser() {
		Log.debug("Cognito Authentication Disabled using Test Dev User");
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

	private Person validateUserToken(JWTClaimsSet claims) {
		try {
			String cognitoUserId = claims.getSubject(); // Cognito user ID in 'sub' claim
			String cognitoUsername = claims.getStringClaim("cognito:username"); // Username claim
			String email = claims.getStringClaim("email"); // Email claim

			if (cognitoUserId == null || cognitoUserId.isEmpty()) {
				Log.warn("User token missing sub claim");
				return null;
			}

			// Try to find existing user by Cognito ID first
			Person authenticatedUser = personService.findPersonByOktaId(cognitoUserId);
			if (authenticatedUser != null) {
				// Update alliance member if needed
				if (authenticatedUser.getAllianceMember() == null) {
					String username = cognitoUsername != null ? cognitoUsername : email;
					updateUserAllianceMember(authenticatedUser, username);
				}
				return authenticatedUser;
			}

			// Try by email
			if (email != null) {
				authenticatedUser = personService.findPersonByOktaEmail(email);
			}

			if (authenticatedUser != null) {
				if (authenticatedUser.getAllianceMember() == null) {
					String username = cognitoUsername != null ? cognitoUsername : email;
					AdminGetUserResponse userDetails = getCognitoUser(username);
					if (userDetails != null) {
						AdminListGroupsForUserResponse groups = getCognitoUserGroups(username);
						// NULL GUARD: Check if groups response is valid before accessing
						if (groups != null && groups.groups() != null) {
							authenticatedUser.setAllianceMember(getAllianceMemberFromCognitoGroups(groups.groups()));
							personDAO.persist(authenticatedUser);
						} else {
							Log.warn("Could not retrieve groups for user: " + username);
						}
					}
				}
				return authenticatedUser;
			}

			Log.info("Making Cognito call to get user info for: " + email);

			String username = cognitoUsername != null ? cognitoUsername : email;
			AdminGetUserResponse userDetails = getCognitoUser(username);

			if (userDetails != null) {
				Person person = new Person();
				person.setApiToken(UUID.randomUUID().toString());
				person.setOktaId(cognitoUserId);

				AdminListGroupsForUserResponse groups = getCognitoUserGroups(username);
				// NULL GUARD: Check if groups response is valid before accessing
				if (groups != null && groups.groups() != null) {
					person.setAllianceMember(getAllianceMemberFromCognitoGroups(groups.groups()));
				} else {
					Log.warn("Could not retrieve groups for new user: " + username);
					// User will be created without alliance member association
				}

				person.setOktaEmail(getAttributeValue(userDetails.userAttributes(), "email"));
				person.setFirstName(getAttributeValue(userDetails.userAttributes(), "given_name"));
				person.setLastName(getAttributeValue(userDetails.userAttributes(), "family_name"));
				person.setUniqueId(loggedInPersonUniqueId.createLoggedInPersonUniqueId(person));
				personDAO.persist(person);
				return person;
			}
		}

		return null;
	}

	private Person validateAdminToken(JWTClaimsSet claims) {
		try {
			String cognitoClientId = claims.getStringClaim("client_id");

			if (cognitoClientId == null || cognitoClientId.isEmpty()) {
				Log.warn("Client credentials token missing client_id claim");
				return null;
			}

			// SECURITY: Verify admin scope is present
			String scopes = claims.getStringClaim("scope");
			if (scopes == null || !scopes.contains("admin")) {
				Log.warn("Client credentials token missing required 'admin' scope for client: " + cognitoClientId);
				return null;
			}

			// Lookup existing admin person
			Person authenticatedUser = personService.findPersonByOktaId(cognitoClientId);

			if (authenticatedUser != null) {
				return authenticatedUser;
			}

			// Create new admin person for this client
			Log.info("Creating admin user for Cognito client: " + cognitoClientId);
			String adminEmail = "admin-" + cognitoClientId + "@alliancegenome.org";
			Person person = new Person();
			person.setApiToken(UUID.randomUUID().toString());
			person.setOktaId(cognitoClientId);
			person.setOktaEmail(adminEmail);
			person.setFirstName("Admin");
			person.setLastName(cognitoClientId);
			person.setUniqueId("Admin|" + cognitoClientId + "|" + adminEmail);
			personDAO.persist(person);
			return person;

		} catch (Exception e) {
			Log.error("Error validating admin token", e);
			return null;
		}
	}

	private AdminGetUserResponse getCognitoUser(String username) {
		try {
			AdminGetUserRequest request = AdminGetUserRequest.builder()
				.userPoolId(userPoolId.get())
				.username(username)
				.build();

			return getCognitoClient().adminGetUser(request);
		} catch (Exception e) {
			Log.error("Error getting Cognito user: " + username, e);
			return null;
		}
	}

	private AdminListGroupsForUserResponse getCognitoUserGroups(String username) {
		try {
			AdminListGroupsForUserRequest request = AdminListGroupsForUserRequest.builder()
				.userPoolId(userPoolId.get())
				.username(username)
				.build();

			return getCognitoClient().adminListGroupsForUser(request);
		} catch (Exception e) {
			Log.error("Error getting Cognito user groups: " + username, e);
			return null;
		}
	}

	private String getAttributeValue(List<AttributeType> attributes, String attributeName) {
		return attributes.stream()
			.filter(attr -> attr.name().equals(attributeName))
			.findFirst()
			.map(AttributeType::value)
			.orElse(null);
	}

	private AllianceMember getAllianceMemberFromCognitoGroups(List<GroupType> groups) {
		// TODO: Coordinate with Blue Team on Cognito group mapping strategy
		// This implementation assumes group names follow the pattern "{AbbreviationStaff}" (e.g., FBStaff, WBStaff)
		// to determine alliance membership. The group structure and mapping logic may require revision
		// based on finalized Cognito group organization and access control requirements.

		// NULL GUARD: Handle null or empty groups list
		if (groups == null || groups.isEmpty()) {
			Log.debug("No groups provided for alliance member lookup");
			return null;
		}

		// In Cognito, we'll use group names to determine alliance membership
		// Groups are named like: FBStaff, WBStaff, MGIStaff, etc.
		// We'll extract the alliance abbreviation from the group name

		for (GroupType group : groups) {
			String groupName = group.groupName();

			// Check if this is a "Staff" group
			if (groupName.endsWith("Staff")) {
				// Extract the alliance abbreviation (e.g., "FB" from "FBStaff")
				String allianceAbbreviation = groupName.replace("Staff", "");

				SearchResponse<AllianceMember> res = allianceMemberDAO.findByField("abbreviation", allianceAbbreviation);
				if (res.getResults().size() == 1) {
					AllianceMember member = res.getResults().get(0);
					Log.info("Found alliance member for group " + groupName + ": " + member.getAbbreviation());
					return member;
				} else if (res.getResults().size() > 1) {
					Log.info("Alliance lookup error: more than one member found for " + allianceAbbreviation);
				}
			}
		}
		return null;
	}

}
