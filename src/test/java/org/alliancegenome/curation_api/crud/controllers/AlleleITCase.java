package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;


import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(8)
public class AlleleITCase {

	private final String ALLELE_CURIE = "Allele:0001";
	private final String ALLELE_TAXON = "NCBITaxon:10090";
	private final String ALLELE_SYMBOL = "Allele<sup>test</sup>";
	private final String ALLELE_DESCRIPTION = "Allele test description";
	private final String INVALID_TAXON = "NCBITaxon:0000";
	private Allele allele;
	
	@Test
	@Order(1)
	public void createValidAllele() {
		allele = createAllele(ALLELE_CURIE, ALLELE_TAXON, ALLELE_SYMBOL, ALLELE_DESCRIPTION);
		
		RestAssured.given().
				contentType("application/json").
				body(allele).
				when().
				post("/api/allele").
				then().
				statusCode(200);
		RestAssured.given().
				when().
				get("/api/allele/" + ALLELE_CURIE).
				then().
				statusCode(200).
				body("entity.curie", is(ALLELE_CURIE)).
				body("entity.symbol", is(ALLELE_SYMBOL)).
				body("entity.taxon.curie", is(ALLELE_TAXON)).
				body("entity.internal", is(false)).
				body("entity.description", is(ALLELE_DESCRIPTION));
	}

	@Test
	@Order(2)
	public void editAllele() {
		allele.setSymbol("Allele<sup>edited</sup>");
		allele.setDescription("Edited allele description");
		allele.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		allele.setInternal(true);

		RestAssured.given().
				contentType("application/json").
				body(allele).
				when().
				put("/api/allele").
				then().
				statusCode(200);

		RestAssured.given().
				when().
				get("/api/allele/" + ALLELE_CURIE).
				then().
				statusCode(200).
				body("entity.curie", is(ALLELE_CURIE)).
				body("entity.symbol", is("Allele<sup>edited</sup>")).
				body("entity.taxon.curie", is("NCBITaxon:9606")).
				body("entity.internal", is(true)).
				body("entity.description", is("Edited allele description"));
	}

	@Test
	@Order(3)
	public void deleteAGM() {

		RestAssured.given().
				when().
				delete("/api/allele/" + ALLELE_CURIE).
				then().
				statusCode(200);
	}
	
	@Test
	@Order(4)
	public void createMissingCurieAllele() {
		Allele noCurieAllele = createAllele(null, ALLELE_TAXON, ALLELE_SYMBOL, ALLELE_DESCRIPTION);
		
		RestAssured.given().
			contentType("application/json").
			body(noCurieAllele).
			when().
			put("/api/allele").
			then().
			statusCode(400);
	}
	
	@Test
	@Order(5)
	public void createMissingSymbolAllele() {
		Allele noSymbolAllele = createAllele(ALLELE_CURIE, ALLELE_TAXON, null, ALLELE_DESCRIPTION);
		
		RestAssured.given().
			contentType("application/json").
			body(noSymbolAllele).
			when().
			put("/api/allele").
			then().
			statusCode(400);
	}
	
	@Test
	@Order(6)
	public void createMissingTaxonAllele() {
		Allele noTaxonAllele = createAllele(ALLELE_CURIE, null, ALLELE_SYMBOL, ALLELE_DESCRIPTION);
		
		RestAssured.given().
			contentType("application/json").
			body(noTaxonAllele).
			when().
			put("/api/allele").
			then().
			statusCode(400);
	}

	@Test
	@Order(7)
	public void createInvalidTaxonAllele() {
		Allele invalidTaxonAllele = createAllele(ALLELE_CURIE, INVALID_TAXON, ALLELE_SYMBOL, ALLELE_DESCRIPTION);
	

		RestAssured.given().
			contentType("application/json").
			body(invalidTaxonAllele).
			when().
			put("/api/allele").
			then().
			statusCode(400);
	}

	private Allele createAllele(String curie, String taxon, String symbol, String description) {
		Allele allele = new Allele();
		allele.setCurie(curie);
		allele.setTaxon(getTaxonFromCurie(taxon));
		allele.setSymbol(symbol);
		allele.setDescription(description);
				
		return allele;
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
