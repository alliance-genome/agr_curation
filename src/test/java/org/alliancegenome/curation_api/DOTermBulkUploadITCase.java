package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.is;

import java.nio.file.Files;
import java.nio.file.Path;

import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("108 - DO term bulk upload")
@Order(108)
public class DOTermBulkUploadITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
				.setParam("http.socket.timeout", 100000)
				.setParam("http.connection.timeout", 100000));
	}

	@Test
	@Order(1)
	public void doTermBulkUpload() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/ontology/01_do_term/00_do_agr_slim.owl"));
		// upload file
		RestAssured.given().
			contentType("application/xml").
			accept("application/json").
			body(content).
			when().
			post("/api/doterm/bulk/owl").
			then().
			statusCode(200);
		
		// check entity count
		RestAssured.given().
			when().
			contentType("application/json").
			body("{\"namespace\":\"disease_ontology\"}").
			post("/api/doterm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(28));
	}
	
	@Test
	@Order(2) 
	public void doTermRetrievable() throws Exception {
		RestAssured.given().
			when().
			get("/api/doterm/DOID:162").
			then().
			statusCode(200).
			body("entity.curie", is("DOID:162")).
			body("entity.name", is("cancer")).
			body("entity.obsolete", is(false)).
			body("entity.namespace", is("disease_ontology")).
			body("entity.definition", is("A disease of cellular proliferation that is malignant and primary, characterized by uncontrolled cellular proliferation, local cell invasion and metastasis."));
	}
		
}
