package org.alliancegenome.curation_api.services.mati;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.alliancegenome.curation_api.interfaces.okta.OktaTokenInterface;
import org.alliancegenome.curation_api.model.mati.Identifier;
import org.alliancegenome.curation_api.model.mati.IdentifiersRange;
import org.alliancegenome.curation_api.model.okta.OktaToken;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.RequestScoped;
import si.mazi.rescu.RestProxyFactory;

/**
 * TEMPORARY FIX: Changed to @RequestScoped with Optional properties to prevent startup failures
 * during Cognito migration. MaTI service needs to be properly migrated from Okta to Cognito.
 * See SCRUM-5613 for full MaTI migration work.
 */
@RequestScoped
public class MaTIService {

	@ConfigProperty(name = "okta.client.id")
	Optional<String> clientId;

	@ConfigProperty(name = "okta.client.secret")
	Optional<String> clientSecret;

	@ConfigProperty(name = "okta.url")
	Optional<String> oktaUrl;

	@ConfigProperty(name = "okta.scopes")
	Optional<String> oktaScopes;

	@ConfigProperty(name = "mati.url")
	String matiUrl;

	private String fetchOktaToken() throws IOException {
		OktaTokenInterface oktaAPI = RestProxyFactory.createProxy(OktaTokenInterface.class, oktaUrl.orElse(""));
		String authorization = "Basic " + Base64.getEncoder().encodeToString((clientId.orElse("") + ":" + clientSecret.orElse("")).getBytes());
		OktaToken oktaToken = oktaAPI.getClientCredentialsAccessToken(authorization, "client_credentials", oktaScopes.orElse(""));
		return oktaToken.getAccessToken();
	}

	public Identifier mintIdentifier(String subdomain) throws IOException {
		String token = fetchOktaToken();
		String authorization = "Bearer: " + token;
		Identifier identifier = RestAssured.given().contentType(ContentType.JSON).header("Accept", "application/json").header("Authorization", authorization).header("subdomain", subdomain).when()
			.put(matiUrl + "/api/identifier").then().extract().body().as(Identifier.class);
		return identifier;
	}

	public IdentifiersRange mintIdentifierRange(String subdomain, String howMany) throws IOException {
		String token = fetchOktaToken();
		String authorization = "Bearer: " + token;
		IdentifiersRange range = RestAssured.given().contentType(ContentType.JSON).header("Accept", "application/json").header("Authorization", authorization).header("subdomain", subdomain)
			.header("value", howMany).when().post(matiUrl + "/api/identifier").then().extract().body().as(IdentifiersRange.class);
		return range;
	}
}
