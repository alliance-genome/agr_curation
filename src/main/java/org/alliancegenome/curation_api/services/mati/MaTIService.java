package org.alliancegenome.curation_api.services.mati;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.NoArgsConstructor;
import org.alliancegenome.curation_api.model.entities.IdentifiersRange;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Base64;

@NoArgsConstructor
@ApplicationScoped
public class MaTIService {

	@Inject
	@ConfigProperty(name = "okta.client.id")
	String client_id;
	@Inject
	@ConfigProperty(name = "okta.client.secret")
	String client_secret;
	@Inject
	@ConfigProperty(name = "okta.url")
	String okta_url;
	@Inject
	@ConfigProperty(name = "okta.scopes")
	String okta_scopes;
	@Inject
	@ConfigProperty(name = "mati.url")
	String mati_url;


	private String fetchOktaToken() throws IOException {
		String authorization = "Basic " +
			Base64.getEncoder().encodeToString((client_id+":"+client_secret).getBytes());
		String token = RestAssured.given().
			contentType(ContentType.URLENC).
			header("Accept", "application/json").
			header("Cache-Control", "no-cache").
			header("Authorization", authorization).
			formParam("grant_type", "client_credentials").
			formParam("scope", okta_scopes).
			when().
			post(okta_url + "/oauth2/default/v1/token").
			then().
			extract().path("access_token").toString();
		return token;
	}

	public String mintIdentifier(String subdomain)  throws IOException {
		String token = fetchOktaToken();
		String authorization = "Bearer: " + token;
		String identifier = RestAssured.given().
			contentType(ContentType.JSON).
			header("Accept", "application/json").
			header("Authorization", authorization).
			header("subdomain", subdomain).
			when().
			put(mati_url).
			then().
			extract().path("value").toString();
		return identifier;
	}

	public IdentifiersRange mintIdentifierRange(String subdomain, String howMany)  throws IOException {
		String token = fetchOktaToken();
		String authorization = "Bearer: " + token;
		IdentifiersRange range = RestAssured.given().
			contentType(ContentType.JSON).
			header("Accept", "application/json").
			header("Authorization", authorization).
			header("subdomain", subdomain).
			header("value", howMany).
			when().
			post(mati_url).
			then().
			extract().body().as(IdentifiersRange.class);
		return range;
	}
}
