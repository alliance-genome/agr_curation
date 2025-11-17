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

	private static final String AUTHENTICATION_BEARER = "Bearer";
	private static final String AUTHENTICATION_APITOKEN = "APIToken";

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

			// SECURITY FIX: Determine token type FIRST to prevent privilege escalation
			// Check if this is a user token (has 'sub' and appropriate token_use)
			String sub = jsonWebToken.getClaim("sub");
			String tokenUse = jsonWebToken.getClaim("token_use");

			// User tokens have 'sub' claim and token_use of "access" or "id"
			boolean isUserToken = (sub != null && !sub.isEmpty()) &&
			                      (tokenUse != null && ("access".equals(tokenUse) || "id".equals(tokenUse)));

			// Admin/client credentials tokens should NOT have 'sub' claim
			boolean isAdminToken = (sub == null || sub.isEmpty()) &&
			                       jsonWebToken.getClaim("client_id") != null;

			if (isUserToken) {
				// This is a user token - try user validation paths only
				person = validateUserTokenById();
				if (person == null) {
					person = validateUserTokenByEmail();
				}
				// DO NOT fall through to admin token validation for user tokens
			} else if (isAdminToken) {
				// This is explicitly an admin/client credentials token
				person = validateAdminToken();
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

	private Person validateUserTokenById() {
		if (jsonWebToken.getClaim("sub") != null) {
			// Cognito uses 'sub' claim for user ID
			return personService.findPersonByOktaId(jsonWebToken.getClaim("sub"));
		}
		return null;
	}

	private Person validateUserTokenByEmail() {
		String cognitoUserId = (String) jsonWebToken.getClaim("sub"); // Cognito user ID in 'sub' claim
		String cognitoUsername = jsonWebToken.getClaim("cognito:username"); // Username claim

		if (cognitoUserId != null && cognitoUserId.length() > 0) {
			String email = jsonWebToken.getClaim("email"); // Email claim

			Person authenticatedUser = personService.findPersonByOktaEmail(email);

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

	private Person validateAdminToken() {
		String cognitoClientId = (String) jsonWebToken.getClaim("client_id"); // Client ID for client credentials flow

		if (cognitoClientId != null && cognitoClientId.length() > 0) {

			Person authenticatedUser = personService.findPersonByOktaId(cognitoClientId);

			if (authenticatedUser != null) {
				return authenticatedUser;
			}

			Log.info("Creating admin user for Cognito client: " + cognitoClientId);

			// For client credentials flow, create an admin user
			Log.debug("Cognito Authentication for Admin user via client credentials");
			String adminEmail = "admin@alliancegenome.org";
			Person person = new Person();
			person.setApiToken(UUID.randomUUID().toString());
			person.setOktaId(cognitoClientId);
			person.setOktaEmail(adminEmail);
			person.setFirstName("Admin");
			person.setLastName(cognitoClientId);
			person.setUniqueId("Admin|" + cognitoClientId + "|" + adminEmail);
			personDAO.persist(person);
			return person;
		}

		return null;
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
