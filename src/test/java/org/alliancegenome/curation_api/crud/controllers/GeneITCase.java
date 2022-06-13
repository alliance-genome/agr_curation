package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;


import static org.hamcrest.Matchers.is;


@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(7)
public class GeneITCase {

	private final String GENE_CURIE = "GENE:0001";
	private final String GENE_TAXON = "NCBITaxon:10090";
	private final String GENE_NAME = "Gene Test 1";
	private final String GENE_SYMBOL = "GT1";
	private final String GENE_TYPE = "SO:0001";
	private final String GENE_AUTO_DESC = "Test auto description";
	private final String INVALID_TAXON = "NCBI:00001";
	private final String INVALID_GENE_TYPE = "SO:0000";
	private SOTerm soTerm; 
	private Gene gene;
	
	@Test
	@Order(1)
	public void createValidGene() {
		soTerm = createSoTerm(GENE_TYPE, "Test SO term");
		gene = createGene(GENE_CURIE, GENE_NAME, GENE_TAXON, GENE_SYMBOL, soTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(gene).
				when().
				post("/api/gene").
				then().
				statusCode(200);
		RestAssured.given().
				when().
				get("/api/gene/" + GENE_CURIE).
				then().
				statusCode(200).
				body("entity.curie", is(GENE_CURIE)).
				body("entity.name", is(GENE_NAME)).
				body("entity.taxon.curie", is(GENE_TAXON)).
				body("entity.symbol", is(GENE_SYMBOL)).
				body("entity.geneType.curie", is(GENE_TYPE)).
				body("entity.automatedGeneDescription", is(GENE_AUTO_DESC)).
				body("entity.internal", is(false));
	}

	@Test
	@Order(2)
	public void editGene() {
		SOTerm newSoTerm = createSoTerm("SO:0001000", "Test SO term 2");
		
		gene.setName("Gene edited");
		gene.setSymbol("GT2");
		gene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		gene.setGeneType(newSoTerm);
		gene.setAutomatedGeneDescription("Edited auto description");
		gene.setInternal(true);

		RestAssured.given().
				contentType("application/json").
				body(gene).
				when().
				put("/api/gene").
				then().
				statusCode(200);

		RestAssured.given().
				when().
				get("/api/gene/" + GENE_CURIE).
				then().
				statusCode(200).
				body("entity.curie", is(GENE_CURIE)).
				body("entity.name", is("Gene edited")).
				body("entity.taxon.curie", is("NCBITaxon:9606")).
				body("entity.symbol", is("GT2")).
				body("entity.geneType.curie", is("SO:0001000")).
				body("entity.automatedGeneDescription", is("Edited auto description")).
				body("entity.internal", is(true));
	}

	@Test
	@Order(3)
	public void deleteGene() {

		RestAssured.given().
				when().
				delete("/api/gene/" + GENE_CURIE).
				then().
				statusCode(200);
	}
	
	@Test
	@Order(4)
	public void createMissingCurieGene() {
		Gene noCurieGene = createGene(null, GENE_NAME, GENE_TAXON, GENE_SYMBOL, soTerm);
		
		RestAssured.given().
			contentType("application/json").
			body(noCurieGene).
			when().
			put("/api/gene").
			then().
			statusCode(400);
	}
	
	@Test
	@Order(5)
	public void createMissingNameGene() {
		Gene noNameGene = createGene(GENE_CURIE, null, GENE_TAXON, GENE_SYMBOL, soTerm);
		
		RestAssured.given().
			contentType("application/json").
			body(noNameGene).
			when().
			put("/api/gene").
			then().
			statusCode(400);
	}
	
	@Test
	@Order(6)
	public void createMissingTaxonGene() {
		Gene noTaxonGene = createGene(GENE_CURIE, GENE_NAME, null, GENE_SYMBOL, soTerm);
		
		RestAssured.given().
			contentType("application/json").
			body(noTaxonGene).
			when().
			put("/api/gene").
			then().
			statusCode(400);
	}

	@Test
	@Order(7)
	public void createMissingSymbolGene() {
		Gene noSymbolGene = createGene(GENE_CURIE, GENE_NAME, GENE_TAXON, null, soTerm);

		RestAssured.given().
			contentType("application/json").
			body(noSymbolGene).
			when().
			put("/api/gene").
			then().
			statusCode(400);
	}
	
	@Test
	@Order(8)
	public void createInvalidTaxonGene() {
		Gene invalidTaxonGene = createGene(GENE_CURIE, GENE_NAME, INVALID_TAXON, GENE_SYMBOL, soTerm);

		RestAssured.given().
			contentType("application/json").
			body(invalidTaxonGene).
			when().
			put("/api/gene").
			then().
			statusCode(400);
	}
	
	@Test
	@Order(9)
	public void createInvalidTypeGene() {
		SOTerm invalidSoTerm = new SOTerm();
		invalidSoTerm.setCurie(INVALID_GENE_TYPE);
		invalidSoTerm.setName("Unloaded SO Term");
		Gene invalidTypeGene = createGene(GENE_CURIE, GENE_NAME, GENE_TAXON, GENE_SYMBOL, invalidSoTerm);
	
		RestAssured.given().
			contentType("application/json").
			body(invalidTypeGene).
			when().
			put("/api/gene").
			then().
			statusCode(400);
	}
	
	private Gene createGene(String curie, String name, String taxon, String symbol, SOTerm type) {
		Gene gene = new Gene();
		gene.setCurie(curie);
		gene.setName(name);
		gene.setTaxon(getTaxonFromCurie(taxon));
		gene.setSymbol(symbol);
		gene.setGeneType(type);
		gene.setAutomatedGeneDescription(GENE_AUTO_DESC);
		
		return gene;
	}
	
	private SOTerm createSoTerm(String curie, String name) {
		SOTerm soTerm = new SOTerm();
		soTerm.setCurie(curie);
		soTerm.setName(name);
		soTerm.setObsolete(false);

		RestAssured.given().
				contentType("application/json").
				body(soTerm).
				when().
				post("/api/soterm").
				then().
				statusCode(200);
		return soTerm;
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
