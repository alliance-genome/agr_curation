package org.alliancegenome.curation_api.services.mati;

import java.io.IOException;
import java.util.Base64;

import org.alliancegenome.curation_api.interfaces.okta.OktaTokenInterface;
import org.alliancegenome.curation_api.model.mati.Identifier;
import org.alliancegenome.curation_api.model.mati.IdentifiersRange;
import org.alliancegenome.curation_api.model.okta.OktaToken;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.enterprise.context.ApplicationScoped;
import si.mazi.rescu.RestProxyFactory;

@ApplicationScoped
public class MaTIService {

	@ConfigProperty(name = "okta.client.id")
	String client_id;

	@ConfigProperty(name = "okta.client.secret")
	String client_secret;

	@ConfigProperty(name = "okta.url")
	String okta_url;

	@ConfigProperty(name = "okta.scopes")
	String okta_scopes;

	@ConfigProperty(name = "mati.url")
	String mati_url;

	private String fetchOktaToken() throws IOException {
		OktaTokenInterface oktaAPI = RestProxyFactory.createProxy(OktaTokenInterface.class, okta_url);
		String authorization = "Basic " + Base64.getEncoder().encodeToString((client_id + ":" + client_secret).getBytes());
		OktaToken oktaToken = oktaAPI.getClientCredentialsAccessToken(authorization, "client_credentials", okta_scopes);
		return oktaToken.getAccess_token();
	}

	public Identifier mintIdentifier(String subdomain) throws IOException {
		String token = fetchOktaToken();
		String authorization = "Bearer: " + token;
		Identifier identifier = RestAssured.given().contentType(ContentType.JSON).header("Accept", "application/json").header("Authorization", authorization).header("subdomain", subdomain).when()
			.put(mati_url + "/api/identifier").then().extract().body().as(Identifier.class);
		return identifier;
	}

	public IdentifiersRange mintIdentifierRange(String subdomain, String howMany) throws IOException {
		String token = fetchOktaToken();
		String authorization = "Bearer: " + token;
		IdentifiersRange range = RestAssured.given().contentType(ContentType.JSON).header("Accept", "application/json").header("Authorization", authorization).header("subdomain", subdomain)
			.header("value", howMany).when().post(mati_url + "/api/identifier").then().extract().body().as(IdentifiersRange.class);
		return range;
	}
}
