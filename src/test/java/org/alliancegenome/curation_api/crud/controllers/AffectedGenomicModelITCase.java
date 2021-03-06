package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel.Subtype;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;


import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(9)
public class AffectedGenomicModelITCase {

	private final String AGM_CURIE = "AGM:0001";
	private final String AGM_TAXON = "NCBITaxon:10090";
	private final String AGM_SUBTYPE = "genotype";
	private final String AGM_NAME = "AGM Test 1";
	private final String INVALID_TAXON = "NCBI:00001";
	private AffectedGenomicModel agm;
	
	@Test
	@Order(1)
	public void createValidAGM() {
		agm = createModel(AGM_CURIE, AGM_NAME, AGM_TAXON, AGM_SUBTYPE);
		
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
		agm.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
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
		AffectedGenomicModel noCurieAgm = createModel(null, AGM_NAME, AGM_TAXON, AGM_SUBTYPE);
		
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
		AffectedGenomicModel noNameAgm = createModel(AGM_CURIE, null, AGM_TAXON, AGM_SUBTYPE);
		
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
		AffectedGenomicModel noTaxonAgm = createModel(AGM_CURIE, AGM_NAME, null, AGM_SUBTYPE);
		
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
		AffectedGenomicModel invalidTaxonAgm = createModel(AGM_CURIE, AGM_NAME, INVALID_TAXON, AGM_SUBTYPE);
	

		RestAssured.given().
			contentType("application/json").
			body(invalidTaxonAgm).
			when().
			put("/api/agm").
			then().
			statusCode(400);
	}

	private AffectedGenomicModel createModel(String curie, String name, String taxon, String subtype) {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setCurie(curie);
		agm.setTaxon(getTaxonFromCurie(taxon));
		agm.setName(name);
		agm.setSubtype(Subtype.valueOf(subtype));
				
		return agm;
	}
	
	private NCBITaxonTerm getTaxonFromCurie(String taxonCurie) {
		ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
			when().
			get("/api/ncbitaxonterm/" + taxonCurie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRef());
		
		return response.getEntity();
	}

	private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRef() {
		return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
	}
}
