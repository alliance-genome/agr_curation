package org.alliancegenome.curation_api.auth;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.alliancegenome.curation_api.dao.AllianceMemberDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.model.entities.AllianceMember;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.helpers.PersonUniqueIdHelper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.arc.properties.IfBuildProperty;
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

@Provider
@Priority(Priorities.AUTHENTICATION)
@IfBuildProperty(name = "quarkus.oidc.enabled", stringValue = "true")
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	@AuthenticatedUser
	Event<Person> userAuthenticatedEvent;

	@Inject
	PersonDAO personDAO;

	@Inject
	AllianceMemberDAO allianceMemberDAO;

	@Inject
	JsonWebToken jsonWebToken;

	@Inject
	PersonService personService;

	@Inject
	PersonUniqueIdHelper loggedInPersonUniqueId;

	@ConfigProperty(name = "cognito.domain")
	Instance<String> cognitoDomain;

	private static final String AUTHENTICATION_BEARER = "Bearer";
	private static final String AUTHENTICATION_APITOKEN = "APIToken";
	private static final String ADMIN_SCOPE = "curation-api/admin";
	private static final HttpClient httpClient = HttpClient.newHttpClient();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final long USERINFO_CACHE_TTL_MS = 15 * 60 * 1000; // 15 minutes
	private static final ConcurrentHashMap<String, UserInfoCacheEntry> userInfoCache = new ConcurrentHashMap<>();

	private static class UserInfoCacheEntry {
		final Map<String, String> userInfo;
		final long timestamp;

		UserInfoCacheEntry(Map<String, String> userInfo) {
			this.userInfo = userInfo;
			this.timestamp = System.currentTimeMillis();
		}

		boolean isExpired() {
			return System.currentTimeMillis() - timestamp > USERINFO_CACHE_TTL_MS;
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (jsonWebToken.getClaimNames() != null) {
			Person person = null;

			String accessToken = null;
			String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
			if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
				accessToken = authHeader.substring(7).trim();
			}

			if (jsonWebToken.getSubject() != null && !jsonWebToken.getSubject().isEmpty()) {
				person = validateUserToken(accessToken);
			} else if (jsonWebToken.getClaim("client_id") != null) {
				person = validateAdminToken();
			}

			if (person != null) {
				userAuthenticatedEvent.fire(person);
			} else {
				failAuthentication(requestContext, AUTHENTICATION_BEARER);
			}
		} else {
			String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
			if (authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_APITOKEN.toLowerCase())) {
				String apiToken = authorizationHeader.substring(AUTHENTICATION_APITOKEN.length()).trim();
				Person person = personService.findPersonByApiToken(apiToken);
				if (person != null) {
					userAuthenticatedEvent.fire(person);
				} else {
					failAuthentication(requestContext, AUTHENTICATION_APITOKEN);
				}
			} else {
				failAuthentication(requestContext, AUTHENTICATION_APITOKEN);
			}
		}
	}

	private void failAuthentication(ContainerRequestContext requestContext, String authType) {
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, authType).build());
	}

	private Person validateUserToken(String accessToken) {
		String cognitoUserId = jsonWebToken.getSubject();
		String email = jsonWebToken.getClaim("email");

		// Fetch user profile from Cognito userinfo endpoint (with caching)
		Map<String, String> userInfo = fetchUserInfoFromCognito(cognitoUserId, accessToken);
		String givenName = userInfo.get("given_name");
		String familyName = userInfo.get("family_name");

		if (email == null && userInfo.get("email") != null) {
			email = userInfo.get("email");
		}

		List<String> groupNames = extractGroupsFromJwt();

		if (cognitoUserId == null || cognitoUserId.isEmpty()) {
			Log.warn("User token missing sub claim");
			return null;
		}

		Person authenticatedUser = personService.findPersonByAuthId(cognitoUserId);
		if (authenticatedUser != null) {
			updateUserIfNeeded(authenticatedUser, groupNames, givenName, familyName);
			return authenticatedUser;
		}

		if (email != null) {
			authenticatedUser = personService.findPersonByAuthEmail(email);
		}

		if (authenticatedUser != null) {
			updateUserIfNeeded(authenticatedUser, groupNames, givenName, familyName);
			return authenticatedUser;
		}

		Person person = new Person();
		person.setApiToken(UUID.randomUUID().toString());
		person.setAuthId(cognitoUserId);
		person.setEmail(email);
		person.setFirstName(givenName);
		person.setLastName(familyName);

		if (!groupNames.isEmpty()) {
			AllianceMember member = getAllianceMemberFromGroupNames(groupNames);
			if (member != null) {
				person.setAllianceMember(member);
			} else {
				Log.warn("Could not determine alliance member from groups: " + groupNames);
			}
		} else {
			Log.warn("No groups found in JWT for new user");
		}

		person.setUniqueId(loggedInPersonUniqueId.createLoggedInPersonUniqueId(person));
		personDAO.persist(person);

		Log.info("Created new user: " + email);
		return person;
	}

	private List<String> extractGroupsFromJwt() {
		List<String> groupNames = new ArrayList<>();
		try {
			Object groupsClaim = jsonWebToken.getClaim("cognito:groups");
			if (groupsClaim == null) {
				groupsClaim = jsonWebToken.getClaim("groups");
			}

			if (groupsClaim instanceof List) {
				List<?> groupsList = (List<?>) groupsClaim;
				for (Object group : groupsList) {
					if (group != null) {
						groupNames.add(group.toString());
					}
				}
			} else if (groupsClaim instanceof String[]) {
				groupNames.addAll(Arrays.asList((String[]) groupsClaim));
			} else if (groupsClaim instanceof String) {
				groupNames.add((String) groupsClaim);
			}
		} catch (Exception e) {
			Log.error("Error extracting groups from JWT", e);
		}
		return groupNames;
	}

	private void updateUserIfNeeded(Person user, List<String> groupNames, String givenName, String familyName) {
		if (user.getAllianceMember() == null && !groupNames.isEmpty()) {
			AllianceMember member = getAllianceMemberFromGroupNames(groupNames);
			if (member != null) {
				user.setAllianceMember(member);
			}
		}
		if (user.getFirstName() == null && givenName != null) {
			user.setFirstName(givenName);
		}
		if (user.getLastName() == null && familyName != null) {
			user.setLastName(familyName);
		}
	}

	private Person validateAdminToken() {
		try {
			String cognitoClientId = jsonWebToken.getClaim("client_id");

			if (cognitoClientId == null || cognitoClientId.isEmpty()) {
				Log.warn("Client credentials token missing client_id claim");
				return null;
			}

			String scopes = jsonWebToken.getClaim("scope");
			if (scopes == null) {
				Log.warn("Client credentials token missing scope claim for client: " + cognitoClientId);
				return null;
			}

			boolean hasAdminScope = false;
			for (String scope : scopes.split("\\s+")) {
				if (ADMIN_SCOPE.equals(scope)) {
					hasAdminScope = true;
					break;
				}
			}

			if (!hasAdminScope) {
				Log.warn("Client credentials token missing required '" + ADMIN_SCOPE + "' scope for client: " + cognitoClientId);
				return null;
			}

			Person authenticatedUser = personService.findPersonByAuthId(cognitoClientId);

			if (authenticatedUser != null) {
				return authenticatedUser;
			}

			String adminEmail = "admin-" + cognitoClientId + "@alliancegenome.org";
			Person person = new Person();
			person.setApiToken(UUID.randomUUID().toString());
			person.setAuthId(cognitoClientId);
			person.setEmail(adminEmail);
			person.setFirstName("Admin");
			person.setLastName(cognitoClientId);
			person.setUniqueId("Admin|" + cognitoClientId + "|" + adminEmail);
			personDAO.persist(person);

			Log.info("Created admin user for client: " + cognitoClientId);
			return person;

		} catch (Exception e) {
			Log.error("Error validating admin token", e);
			return null;
		}
	}

	private Map<String, String> fetchUserInfoFromCognito(String userId, String accessToken) {
		userInfoCache.entrySet().removeIf(entry -> entry.getValue().isExpired());

		if (userId != null) {
			UserInfoCacheEntry cached = userInfoCache.get(userId);
			if (cached != null && !cached.isExpired()) {
				return cached.userInfo;
			}
		}

		Map<String, String> userInfo = new ConcurrentHashMap<>();

		if (accessToken == null || accessToken.isEmpty()) {
			Log.warn("No access token provided for userinfo request");
			return userInfo;
		}

		try {
			String userInfoUrl = "https://" + cognitoDomain.get() + "/oauth2/userInfo";

			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(userInfoUrl))
				.header("Authorization", "Bearer " + accessToken)
				.GET()
				.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				Map<String, Object> responseMap = objectMapper.readValue(
					response.body(),
					new TypeReference<Map<String, Object>>() { }
				);

				if (responseMap.get("given_name") != null) {
					userInfo.put("given_name", responseMap.get("given_name").toString());
				}
				if (responseMap.get("family_name") != null) {
					userInfo.put("family_name", responseMap.get("family_name").toString());
				}
				if (responseMap.get("email") != null) {
					userInfo.put("email", responseMap.get("email").toString());
				}

				if (userId != null) {
					userInfoCache.put(userId, new UserInfoCacheEntry(userInfo));
				}
			} else {
				Log.warn("Failed to fetch userinfo, status: " + response.statusCode());
			}
		} catch (Exception e) {
			Log.error("Error fetching userinfo from Cognito", e);
		}

		return userInfo;
	}

	private AllianceMember getAllianceMemberFromGroupNames(List<String> groupNames) {
		if (groupNames == null || groupNames.isEmpty()) {
			return null;
		}

		for (String groupName : groupNames) {
			if (groupName.endsWith("Staff")) {
				String allianceAbbreviation = groupName.replace("Staff", "");

				SearchResponse<AllianceMember> res = allianceMemberDAO.findByField("abbreviation", allianceAbbreviation);
				if (res.getResults().size() == 1) {
					return res.getResults().get(0);
				} else if (res.getResults().size() > 1) {
					Log.warn("Multiple alliance members found for abbreviation: " + allianceAbbreviation);
				}
			}
		}

		return null;
	}

}
