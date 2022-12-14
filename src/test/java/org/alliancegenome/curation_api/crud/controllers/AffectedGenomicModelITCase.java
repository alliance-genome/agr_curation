package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.is;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel.Subtype;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(9)
public class AffectedGenomicModelITCase extends BaseITCase{

	private final String AGM_CURIE = "AGM:0001";
	private final String AGM_TAXON = "NCBITaxon:10090";
	private final String AGM_SUBTYPE = "genotype";
	private final String AGM_NAME = "AGM Test 1";
	private final String INVALID_TAXON = "NCBI:00001";
	private AffectedGenomicModel agm;
	
	@Test
	@Order(1)
	public void createValidAGM() {
		agm = createAffectedGenomicModel(AGM_CURIE, AGM_NAME, AGM_TAXON, AGM_SUBTYPE);
		
		RestAssured.given().
				contentType("application/json").
				body(agm).
				when().
				post("/api/agm").
				then().
				statusCode(200);
		RestAssured.given().
				when().
				get("/api/agm/" + AGM_CURIE).
				then().
				statusCode(200).
				body("entity.curie", is(AGM_CURIE)).
				body("entity.name", is(AGM_NAME)).
				body("entity.taxon.curie", is(AGM_TAXON)).
				body("entity.internal", is(false)).
				body("entity.subtype", is(AGM_SUBTYPE));
	}

	@Test
	@Order(2)
	public void editAGM() {
		
		
		agm.setName("AGM edited");
		agm.setSubtype(Subtype.valueOf("strain"));
		agm.setTaxon(getNCBITaxonTerm("NCBITaxon:9606"));
		agm.setInternal(true);

		RestAssured.given().
				contentType("application/json").
				body(agm).
				when().
				put("/api/agm").
				then().
				statusCode(200);

		RestAssured.given().
				when().
				get("/api/agm/" + AGM_CURIE).
				then().
				statusCode(200).
				body("entity.curie", is(AGM_CURIE)).
				body("entity.name", is("AGM edited")).
				body("entity.taxon.curie", is("NCBITaxon:9606")).
				body("entity.internal", is(true)).
				body("entity.subtype", is("strain"));
	}

	@Test
	@Order(3)
	public void deleteAGM() {

		RestAssured.given().
				when().
				delete("/api/agm/" + AGM_CURIE).
				then().
				statusCode(200);
	}
	
	@Test
	@Order(4)
	public void createMissingCurieAGM() {
		AffectedGenomicModel noCurieAgm = createAffectedGenomicModel(null, AGM_NAME, AGM_TAXON, AGM_SUBTYPE);
		
		RestAssured.given().
			contentType("application/json").
			body(noCurieAgm).
			when().
			put("/api/agm").
			then().
			statusCode(400);
	}
	
	@Test
	@Order(5)
	public void createMissingNameAGM() {
		AffectedGenomicModel noNameAgm = createAffectedGenomicModel(AGM_CURIE, null, AGM_TAXON, AGM_SUBTYPE);
		
		RestAssured.given().
			contentType("application/json").
			body(noNameAgm).
			when().
			put("/api/agm").
			then().
			statusCode(400);
	}
	
	@Test
	@Order(6)
	public void createMissingTaxonAGM() {
		AffectedGenomicModel noTaxonAgm = createAffectedGenomicModel(AGM_CURIE, AGM_NAME, null, AGM_SUBTYPE);
		
		RestAssured.given().
			contentType("application/json").
			body(noTaxonAgm).
			when().
			put("/api/agm").
			then().
			statusCode(400);
	}

	@Test
	@Order(7)
	public void createInvalidTaxonAGM() {
		AffectedGenomicModel invalidTaxonAgm = createAffectedGenomicModel(AGM_CURIE, AGM_NAME, INVALID_TAXON, AGM_SUBTYPE);
	

		RestAssured.given().
			contentType("application/json").
			body(invalidTaxonAgm).
			when().
			put("/api/agm").
			then().
			statusCode(400);
	}

	private AffectedGenomicModel createAffectedGenomicModel(String curie, String name, String taxon, String subtype) {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setCurie(curie);
		agm.setTaxon(getNCBITaxonTerm(taxon));
		agm.setName(name);
		agm.setSubtype(Subtype.valueOf(subtype));
				
		return agm;
	}
}
