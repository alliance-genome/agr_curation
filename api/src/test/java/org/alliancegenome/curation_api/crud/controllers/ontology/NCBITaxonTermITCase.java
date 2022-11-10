package org.alliancegenome.curation_api.crud.controllers.ontology;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(13)
public class NCBITaxonTermITCase {
	private String VALID_TAXON_CURIE = "NCBITaxon:1";
	private String INVALID_TAXON_PREFIX = "NCBI:1";
	private String INVALID_TAXON_SUFFIX = "NCBITaxon:0";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
						.setParam("http.socket.timeout", 100000)
						.setParam("http.connection.timeout", 100000));
	}

	@Test
	@Order(1)
	void testValidTaxon() {
		loadNCBITaxonTerm(VALID_TAXON_CURIE);
		
		RestAssured.given().
			when().
			get("/api/ncbitaxonterm/" + VALID_TAXON_CURIE).
			then().
			statusCode(200).
			body("entity.name", is("Test NCBITaxonTerm")).
			body("entity.obsolete", is(false));
	}

	@Test
	@Order(2)
	void testInvalidPrefix() {
		RestAssured.given().
			when().
			get("/api/ncbitaxonterm/" + INVALID_TAXON_PREFIX).
			then().
			statusCode(200).
			body("isEmpty()", Matchers.is(true));
	}

	@Test
	@Order(3)
	void testInvalidSuffix() {
		RestAssured.given().
			when().
			get("/api/ncbitaxonterm/" + INVALID_TAXON_SUFFIX).
			then().
			statusCode(200).
			body("isEmpty()", Matchers.is(true));
	}
	
	private void loadNCBITaxonTerm(String taxonTerm) {
		NCBITaxonTerm ncbiTaxonTerm = new NCBITaxonTerm();
		ncbiTaxonTerm.setCurie(taxonTerm);
		ncbiTaxonTerm.setName("Test NCBITaxonTerm");
		ncbiTaxonTerm.setObsolete(false);
	
		RestAssured.given().
			contentType("application/json").
			body(ncbiTaxonTerm).
			when().
			put("/api/ncbitaxonterm").
			then().
			statusCode(200);
	}
}
