package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.is;

import java.util.List;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(7)
public class GeneITCase {

	private final String GENE_CURIE = "GENE:0001";
	private final String GENE_TAXON = "NCBITaxon:10090";
	private final String GENE_NAME = "Gene Test 1";
	private final String GENE_SYMBOL = "GT1";
	private final String GENE_TYPE = "SO:0001";
	private final String INVALID_TAXON = "NCBI:00001";
	private final String INVALID_GENE_TYPE = "SO:0000";
	private final Vocabulary nameType = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
	private final VocabularyTerm symbolTerm = getVocabularyTerm(nameType, "nomenclature_symbol");
	private SOTerm soTerm; 
	private Gene gene;
	
	@Test
	@Order(1)
	public void createValidGene() {
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolTerm);
		symbol.setDisplayText(GENE_SYMBOL);
		symbol.setFormatText(GENE_SYMBOL);
		soTerm = createSoTerm(GENE_TYPE, "Test SO term");
		gene = createGene(GENE_CURIE, GENE_TAXON, soTerm, symbol);
		
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
				body("entity.taxon.curie", is(GENE_TAXON)).
				body("entity.geneType.curie", is(GENE_TYPE)).
				body("entity.internal", is(false)).
				body("entity.geneSymbol.displayText", is(GENE_SYMBOL));
	}

	@Test
	@Order(2)
	public void editGene() {
		SOTerm newSoTerm = createSoTerm("SO:0001000", "Test SO term 2");
		
		gene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		gene.setGeneType(newSoTerm);
		gene.setInternal(true);
		
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolTerm);
		symbol.setDisplayText("GT2");
		symbol.setFormatText("GT2");
		gene.setGeneSymbol(symbol);

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
				body("entity.taxon.curie", is("NCBITaxon:9606")).
				body("entity.geneSymbol.displayText", is("GT2")).
				body("entity.geneType.curie", is("SO:0001000")).
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

		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolTerm);
		symbol.setDisplayText(GENE_SYMBOL);
		symbol.setFormatText(GENE_SYMBOL);
		
		Gene noCurieGene = createGene(null, GENE_TAXON, soTerm, symbol);
		
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
	public void createMissingTaxonGene() {

		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolTerm);
		symbol.setDisplayText(GENE_SYMBOL);
		symbol.setFormatText(GENE_SYMBOL);
		
		Gene noTaxonGene = createGene(GENE_CURIE, null, soTerm, symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(noTaxonGene).
			when().
			put("/api/gene").
			then().
			statusCode(400);
	}

	@Test
	@Order(6)
	public void createInvalidTaxonGene() {

		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolTerm);
		symbol.setDisplayText(GENE_SYMBOL);
		symbol.setFormatText(GENE_SYMBOL);
		
		Gene invalidTaxonGene = createGene(GENE_CURIE, INVALID_TAXON, soTerm, symbol);

		RestAssured.given().
			contentType("application/json").
			body(invalidTaxonGene).
			when().
			put("/api/gene").
			then().
			statusCode(400);
	}
	
	@Test
	@Order(7)
	public void createInvalidTypeGene() {

		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolTerm);
		symbol.setDisplayText(GENE_SYMBOL);
		symbol.setFormatText(GENE_SYMBOL);
		
		SOTerm invalidSoTerm = new SOTerm();
		invalidSoTerm.setCurie(INVALID_GENE_TYPE);
		invalidSoTerm.setName("Unloaded SO Term");
		Gene invalidTypeGene = createGene(GENE_CURIE, GENE_TAXON, invalidSoTerm, symbol);
	
		RestAssured.given().
			contentType("application/json").
			body(invalidTypeGene).
			when().
			put("/api/gene").
			then().
			statusCode(400);
	}
	
	private Gene createGene(String curie, String taxon, SOTerm type, GeneSymbolSlotAnnotation symbol) {
		Gene gene = new Gene();
		gene.setCurie(curie);
		gene.setTaxon(getTaxonFromCurie(taxon));
		gene.setGeneType(type);
		gene.setGeneSymbol(symbol);
		
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

	private Vocabulary getVocabulary(String name) {
		ObjectResponse<Vocabulary> response = 
			RestAssured.given().
				when().
				get("/api/vocabulary/findBy/" + name).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabulary());
		
		Vocabulary vocabulary = response.getEntity();
		
		return vocabulary;
	}
	
	private VocabularyTerm getVocabularyTerm(Vocabulary vocabulary, String name) {
		ObjectListResponse<VocabularyTerm> response = 
			RestAssured.given().
				when().
				get("/api/vocabulary/" + vocabulary.getId() + "/terms").
				then().
				statusCode(200).
				extract().body().as(getObjectListResponseTypeRefVocabularyTerm());
		
		List<VocabularyTerm> vocabularyTerms = response.getEntities();
		for (VocabularyTerm vocabularyTerm : vocabularyTerms) {
			if (vocabularyTerm.getName().equals(name)) {
				return vocabularyTerm;
			}
		}
		
		return null;
	}

	private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRef() {
		return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
	}
	
	private TypeRef<ObjectListResponse<VocabularyTerm>> getObjectListResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectListResponse <VocabularyTerm>>() { };
	}

	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}

}
