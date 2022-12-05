package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
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

	private final String GENE_TAXON = "NCBITaxon:10090";
	private final String GENE_TYPE = "SO:0001";
	private Vocabulary nameType;
	private Vocabulary synonymScope;
	private VocabularyTerm exactSynonymScope;
	private VocabularyTerm broadSynonymScope;
	private VocabularyTerm symbolNameType;
	private VocabularyTerm systematicNameType;
	private VocabularyTerm fullNameType;
	private SOTerm soTerm;
	private NCBITaxonTerm taxon;
	private GeneSymbolSlotAnnotation geneSymbol;
	private GeneFullNameSlotAnnotation geneFullName;
	private GeneSynonymSlotAnnotation geneSynonym;
	private GeneSystematicNameSlotAnnotation geneSystematicName;
	
	
	@Test
	@Order(1)
	public void createValidGene() {
		loadRequiredEntities();
		
		Gene gene = new Gene();
		gene.setCurie("GENE:0001");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		gene.setGeneFullName(geneFullName);
		gene.setGeneSynonyms(List.of(geneSynonym));
		gene.setGeneSystematicName(geneSystematicName);
		
		RestAssured.given().
				contentType("application/json").
				body(gene).
				when().
				post("/api/gene").
				then().
				statusCode(200);
		RestAssured.given().
				when().
				get("/api/gene/" + "GENE:0001").
				then().
				statusCode(200).
				body("entity.curie", is("GENE:0001")).
				body("entity.taxon.curie", is(GENE_TAXON)).
				body("entity.geneType.curie", is(GENE_TYPE)).
				body("entity.internal", is(false)).
				body("entity.geneSymbol.displayText", is("GT1")).
				body("entity.geneSymbol.formatText", is("GT1")).
				body("entity.geneSymbol.nameType.name", is(symbolNameType.getName())).
				body("entity.geneSymbol.synonymScope.name", is(exactSynonymScope.getName())).
				body("entity.geneFullName.displayText", is("Gene test 1")).
				body("entity.geneFullName.formatText", is("Gene test 1")).
				body("entity.geneFullName.nameType.name", is(fullNameType.getName())).
				body("entity.geneFullName.synonymScope.name", is(exactSynonymScope.getName())).
				body("entity.geneSynonyms[0].displayText", is("Gene test synonym 1")).
				body("entity.geneSynonyms[0].formatText", is("Gene test synonym 1")).
				body("entity.geneSynonyms[0].nameType.name", is(symbolNameType.getName())).
				body("entity.geneSynonyms[0].synonymScope.name", is(exactSynonymScope.getName())).
				body("entity.geneSystematicName.displayText", is("GT.1")).
				body("entity.geneSystematicName.formatText", is("GT.1")).
				body("entity.geneSystematicName.nameType.name", is(systematicNameType.getName())).
				body("entity.geneSystematicName.synonymScope.name", is(exactSynonymScope.getName()));
	}

	@Test
	@Order(2)
	public void editGene() {
		SOTerm newSoTerm = createSoTerm("SO:0001000", "Test SO term 2");
		
		Gene gene = getGene();
		gene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		gene.setGeneType(newSoTerm);
		gene.setInternal(true);
		
		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		symbol.setNameType(systematicNameType);
		symbol.setDisplayText("GT2");
		symbol.setFormatText("GT2");
		symbol.setSynonymScope(broadSynonymScope);
		gene.setGeneSymbol(symbol);
		
		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		fullName.setDisplayText("Gene test 2");
		fullName.setFormatText("Gene test 2");
		fullName.setSynonymScope(broadSynonymScope);
		gene.setGeneFullName(fullName);
		
		GeneSynonymSlotAnnotation synonym = gene.getGeneSynonyms().get(0);
		synonym.setNameType(systematicNameType);
		synonym.setDisplayText("Gene test synonym 2");
		synonym.setFormatText("Gene test synonym 2");
		synonym.setSynonymScope(broadSynonymScope);
		gene.setGeneSynonyms(List.of(synonym));
		
		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		systematicName.setDisplayText("GT.2");
		systematicName.setFormatText("GT.2");
		systematicName.setSynonymScope(broadSynonymScope);
		gene.setGeneSystematicName(systematicName);

		RestAssured.given().
				contentType("application/json").
				body(gene).
				when().
				put("/api/gene").
				then().
				statusCode(200);

		RestAssured.given().
				when().
				get("/api/gene/" + "GENE:0001").
				then().
				statusCode(200).
				body("entity.curie", is("GENE:0001")).
				body("entity.taxon.curie", is("NCBITaxon:9606")).
				body("entity.geneType.curie", is("SO:0001000")).
				body("entity.internal", is(true)).
				body("entity.geneSymbol.displayText", is("GT2")).
				body("entity.geneSymbol.formatText", is("GT2")).
				body("entity.geneSymbol.nameType.name", is(systematicNameType.getName())).
				body("entity.geneSymbol.synonymScope.name", is(broadSynonymScope.getName())).
				body("entity.geneFullName.displayText", is("Gene test 2")).
				body("entity.geneFullName.formatText", is("Gene test 2")).
				body("entity.geneFullName.nameType.name", is(fullNameType.getName())).
				body("entity.geneFullName.synonymScope.name", is(broadSynonymScope.getName())).
				body("entity.geneSynonyms[0].displayText", is("Gene test synonym 2")).
				body("entity.geneSynonyms[0].formatText", is("Gene test synonym 2")).
				body("entity.geneSynonyms[0].nameType.name", is(systematicNameType.getName())).
				body("entity.geneSynonyms[0].synonymScope.name", is(broadSynonymScope.getName())).
				body("entity.geneSystematicName.displayText", is("GT.2")).
				body("entity.geneSystematicName.formatText", is("GT.2")).
				body("entity.geneSystematicName.nameType.name", is(systematicNameType.getName())).
				body("entity.geneSystematicName.synonymScope.name", is(broadSynonymScope.getName()));
	}
	
	@Test
	@Order(3)
	public void createMissingCurieGene() {

		GeneSymbolSlotAnnotation symbol = createGeneSymbolSlotAnnotation("GT1");
		Gene gene = new Gene();
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void createMissingTaxonGene() {

		GeneSymbolSlotAnnotation symbol = createGeneSymbolSlotAnnotation("GT1");
		Gene gene = new Gene();
		gene.setCurie("GENE:0001");
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(5)
	public void createInvalidTaxonGene() {

		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("TEST:invalid");
		nonPersistedTaxon.setName("Invalid");
		
		GeneSymbolSlotAnnotation symbol = createGeneSymbolSlotAnnotation("GT1");
		Gene gene = new Gene();
		gene.setCurie("GENE:0001");
		gene.setTaxon(nonPersistedTaxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(symbol);

		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void createInvalidTypeGene() {

		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setName("SO:00000");
		
		GeneSymbolSlotAnnotation symbol = createGeneSymbolSlotAnnotation("GT1");
		Gene gene = new Gene();
		gene.setCurie("GENE:0001");
		gene.setTaxon(taxon);
		gene.setGeneType(nonPersistedSoTerm);
		gene.setGeneSymbol(symbol);
	
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneType", is(ValidationConstants.INVALID_MESSAGE));
	}
	

	
	@Test
	@Order(7)
	public void createGeneWithMissingGeneSymbol() {
		
		Gene gene = new Gene();
		gene.setCurie("GENE:0007");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(8)
	public void createGeneWithMissingGeneSymbolDisplayText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0008");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setFormatText("MissingDisplayText");
		symbol.setSynonymScope(exactSynonymScope);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void createGeneWithEmptyGeneSymbolDisplayText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0009");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText("");
		symbol.setFormatText("MissingDisplayText");
		symbol.setSynonymScope(exactSynonymScope);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(10)
	public void createGeneWithMissingGeneSymbolFormatText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0010");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText("MissingDisplayText");
		symbol.setSynonymScope(exactSynonymScope);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void createGeneWithEmptyGeneSymbolFormatText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0011");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText("MissingFormatText");
		symbol.setFormatText("");
		symbol.setSynonymScope(exactSynonymScope);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(12)
	public void createGeneWithMissingGeneSymbolNameType() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0012");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(null);
		symbol.setDisplayText("MissingNameType");
		symbol.setFormatText("MissingNameType");
		symbol.setSynonymScope(exactSynonymScope);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(13)
	public void createGeneWithInvalidSymbolNameType() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0013");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(fullNameType);
		symbol.setDisplayText("InvalidNameType");
		symbol.setFormatText("InvalidNameType");
		symbol.setSynonymScope(exactSynonymScope);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(14)
	public void createGeneWithInvalidSymbolSynomyScope() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0014");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText("InvalidSynonymScope");
		symbol.setFormatText("InvalidSynonymScope");
		symbol.setSynonymScope(symbolNameType);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(15)
	public void editGeneWithMissingGeneSymbol() {
		
		Gene gene = getGene();
		gene.setGeneSymbol(null);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(16)
	public void editGeneWithMissingGeneSymbolDisplayText() {
		
		Gene gene = getGene();
		
		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		symbol.setDisplayText(null);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(17)
	public void editGeneWithEmptyGeneSymbolDisplayText() {
		
		Gene gene = getGene();
		
		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		symbol.setDisplayText("");
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(18)
	public void editGeneWithMissingGeneSymbolFormatText() {
		
		Gene gene = getGene();
		
		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		symbol.setFormatText(null);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(19)
	public void editGeneWithEmptyGeneSymbolFormatText() {
		
		Gene gene = getGene();
		
		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		symbol.setFormatText("");
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(20)
	public void editGeneWithMissingGeneSymbolNameType() {
		
		Gene gene = getGene();
		
		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		symbol.setNameType(null);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(21)
	public void editGeneWithInvalidGeneSymbolNameType() {
		
		Gene gene = getGene();
		
		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		symbol.setNameType(fullNameType);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(22)
	public void editGeneWithInvalidGeneSymbolSynonymScope() {
		
		Gene gene = getGene();
		
		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		symbol.setSynonymScope(symbolNameType);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSymbol", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(23)
	public void editGeneWithNullGeneSymbolSynonymScope() {
		Gene gene = getGene();
		gene.setGeneSymbol(geneSymbol);

		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200).
			body("entity.geneSymbol", hasKey("synonymScope"));
		
		GeneSymbolSlotAnnotation symbol = gene.getGeneSymbol();
		symbol.setSynonymScope(null);
		gene.setGeneSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200).
			body("entity.geneSymbol", not(hasKey("synonymScope")));
	}
	
	@Test
	@Order(24)
	public void createGeneWithMissingGeneFullNameDisplayText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0024");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneFullNameSlotAnnotation fullName = new GeneFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setFormatText("MissingDisplayText");
		fullName.setSynonymScope(exactSynonymScope);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(25)
	public void createGeneWithEmptyGeneFullNameDisplayText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0025");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneFullNameSlotAnnotation fullName = new GeneFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setDisplayText("");
		fullName.setFormatText("MissingDisplayText");
		fullName.setSynonymScope(exactSynonymScope);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(26)
	public void createGeneWithMissingGeneFullNameFormatText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0026");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneFullNameSlotAnnotation fullName = new GeneFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setDisplayText("MissingFormatText");
		fullName.setSynonymScope(exactSynonymScope);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(27)
	public void createGeneWithEmptyGeneFullNameFormatText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0027");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneFullNameSlotAnnotation fullName = new GeneFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setDisplayText("MissingFormatText");
		fullName.setFormatText("");
		fullName.setSynonymScope(exactSynonymScope);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(28)
	public void createGeneWithMissingGeneFullNameNameType() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0028");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneFullNameSlotAnnotation fullName = new GeneFullNameSlotAnnotation();
		fullName.setDisplayText("MissingNameType");
		fullName.setFormatText("MissingNameType");
		fullName.setSynonymScope(exactSynonymScope);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(29)
	public void createGeneWithInvalidFullNameNameType() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0029");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneFullNameSlotAnnotation fullName = new GeneFullNameSlotAnnotation();
		fullName.setDisplayText("MissingNameType");
		fullName.setFormatText("MissingNameType");
		fullName.setNameType(symbolNameType);
		fullName.setSynonymScope(exactSynonymScope);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(30)
	public void createGeneWithInvalidFullNameSynomymScope() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0030");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneFullNameSlotAnnotation fullName = new GeneFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setDisplayText("MissingNameType");
		fullName.setFormatText("MissingNameType");
		fullName.setSynonymScope(symbolNameType);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(31)
	public void editGeneWithMissingGeneFullNameDisplayText() {
		
		Gene gene = getGene();
		
		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		fullName.setDisplayText(null);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(32)
	public void editGeneWithEmptyGeneFullNameDisplayText() {
		
		Gene gene = getGene();
		
		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		fullName.setDisplayText("");
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(33)
	public void editGeneWithMissingGeneFullNameFormatText() {
		
		Gene gene = getGene();
		
		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		fullName.setFormatText(null);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(34)
	public void editGeneWithEmptyGeneFullNameFormatText() {
		
		Gene gene = getGene();
		
		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		fullName.setFormatText("");
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(35)
	public void editGeneWithMissingGeneFullNameNameType() {
		
		Gene gene = getGene();
		
		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		fullName.setNameType(null);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(36)
	public void editGeneWithInvalidGeneFullNameNameType() {
		
		Gene gene = getGene();
		
		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		fullName.setNameType(symbolNameType);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(37)
	public void editGeneWithInvalidGeneFullNameSynonymScope() {
		
		Gene gene = getGene();
		
		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		fullName.setSynonymScope(fullNameType);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneFullName", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(38)
	public void editGeneWithNullGeneFullNameSynonymScope() {
		Gene gene = getGene();

		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200).
			body("entity.geneFullName", hasKey("synonymScope"));
		
		GeneFullNameSlotAnnotation fullName = gene.getGeneFullName();
		fullName.setSynonymScope(null);
		gene.setGeneFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200).
			body("entity.geneFullName", not(hasKey("synonymScope")));
	}
	
	@Test
	@Order(39)
	public void createGeneWithMissingAlleleSynonymDisplayText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0039");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setFormatText("MissingDisplayText");
		synonym.setSynonymScope(exactSynonymScope);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(40)
	public void createGeneWithEmptyAlleleSynonymDisplayText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0040");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setDisplayText("");
		synonym.setFormatText("EmptyDisplayText");
		synonym.setSynonymScope(exactSynonymScope);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(41)
	public void createGeneWithMissingAlleleSynonymFormatText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0041");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setDisplayText("MissingFormatText");
		synonym.setSynonymScope(exactSynonymScope);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(42)
	public void createGeneWithEmptyAlleleSynonymFormatText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0042");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setDisplayText("EmptyFormatText");
		synonym.setFormatText("");
		synonym.setSynonymScope(exactSynonymScope);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(43)
	public void createGeneWithMissingAlleleSynonymNameType() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0043");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
		synonym.setDisplayText("MissingNameType");
		synonym.setFormatText("MissingNameType");
		synonym.setSynonymScope(exactSynonymScope);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(44)
	public void createGeneWithInvalidSynonymNameType() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0044");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
		synonym.setNameType(exactSynonymScope);
		synonym.setDisplayText("InvalidNameType");
		synonym.setFormatText("InvalidNameType");
		synonym.setSynonymScope(exactSynonymScope);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(45)
	public void createGeneWithInvalidSynonymSynomymScope() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0045");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setDisplayText("InvalidSynonymScope");
		synonym.setFormatText("InvalidSynonymScope");
		synonym.setSynonymScope(fullNameType);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(46)
	public void editGeneWithMissingAlleleSynonymDisplayText() {
		
		Gene gene = getGene();
		
		GeneSynonymSlotAnnotation synonym = gene.getGeneSynonyms().get(0);
		synonym.setDisplayText(null);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(47)
	public void editGeneWithEmptyAlleleSynonymDisplayText() {
		
		Gene gene = getGene();
		
		GeneSynonymSlotAnnotation synonym = gene.getGeneSynonyms().get(0);
		synonym.setDisplayText("");
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(48)
	public void editGeneWithMissingAlleleSynonymFormatText() {
		
		Gene gene = getGene();
		
		GeneSynonymSlotAnnotation synonym = gene.getGeneSynonyms().get(0);
		synonym.setFormatText(null);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(49)
	public void editGeneWithEmptyAlleleSynonymFormatText() {
		
		Gene gene = getGene();
		
		GeneSynonymSlotAnnotation synonym = gene.getGeneSynonyms().get(0);
		synonym.setFormatText("");
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(50)
	public void editGeneWithMissingAlleleSynonymNameType() {
		
		Gene gene = getGene();
		
		GeneSynonymSlotAnnotation synonym = gene.getGeneSynonyms().get(0);
		synonym.setNameType(null);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(51)
	public void editGeneWithInvalidAlleleSynonymNameType() {
		
		Gene gene = getGene();
		
		GeneSynonymSlotAnnotation synonym = gene.getGeneSynonyms().get(0);
		synonym.setNameType(exactSynonymScope);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(52)
	public void editGeneWithInvalidAlleleSynonymSynonymScope() {
		
		Gene gene = getGene();
		
		GeneSynonymSlotAnnotation synonym = gene.getGeneSynonyms().get(0);
		synonym.setSynonymScope(symbolNameType);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSynonyms", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(53)
	public void editGeneWithNullAlleleSynonymSynonymScope() {
		Gene gene = getGene();

		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200).
			body("entity.geneSynonyms[0]", hasKey("synonymScope"));
		
		GeneSynonymSlotAnnotation synonym = gene.getGeneSynonyms().get(0);
		synonym.setSynonymScope(null);
		gene.setGeneSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200).
			body("entity.geneSynonyms[0]", not(hasKey("synonymScope")));
	}
	
	@Test
	@Order(54)
	public void createGeneWithMissingGeneSystematicNameDisplayText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0024");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSystematicNameSlotAnnotation systematicName = new GeneSystematicNameSlotAnnotation();
		systematicName.setNameType(systematicNameType);
		systematicName.setFormatText("MissingDisplayText");
		systematicName.setSynonymScope(exactSynonymScope);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(55)
	public void createGeneWithEmptyGeneSystematicNameDisplayText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0025");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSystematicNameSlotAnnotation systematicName = new GeneSystematicNameSlotAnnotation();
		systematicName.setNameType(systematicNameType);
		systematicName.setDisplayText("");
		systematicName.setFormatText("MissingDisplayText");
		systematicName.setSynonymScope(exactSynonymScope);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(56)
	public void createGeneWithMissingGeneSystematicNameFormatText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0026");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSystematicNameSlotAnnotation systematicName = new GeneSystematicNameSlotAnnotation();
		systematicName.setNameType(systematicNameType);
		systematicName.setDisplayText("MissingFormatText");
		systematicName.setSynonymScope(exactSynonymScope);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(57)
	public void createGeneWithEmptyGeneSystematicNameFormatText() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0027");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSystematicNameSlotAnnotation systematicName = new GeneSystematicNameSlotAnnotation();
		systematicName.setNameType(systematicNameType);
		systematicName.setDisplayText("MissingFormatText");
		systematicName.setFormatText("");
		systematicName.setSynonymScope(exactSynonymScope);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(58)
	public void createGeneWithMissingGeneSystematicNameNameType() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0028");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSystematicNameSlotAnnotation systematicName = new GeneSystematicNameSlotAnnotation();
		systematicName.setDisplayText("MissingNameType");
		systematicName.setFormatText("MissingNameType");
		systematicName.setSynonymScope(exactSynonymScope);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(59)
	public void createGeneWithInvalidSystematicNameNameType() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0029");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSystematicNameSlotAnnotation systematicName = new GeneSystematicNameSlotAnnotation();
		systematicName.setDisplayText("MissingNameType");
		systematicName.setFormatText("MissingNameType");
		systematicName.setNameType(symbolNameType);
		systematicName.setSynonymScope(exactSynonymScope);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(60)
	public void createGeneWithInvalidSystematicNameSynomymScope() {
		Gene gene = new Gene();
		gene.setCurie("GENE:0030");
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		gene.setGeneSymbol(geneSymbol);
		
		GeneSystematicNameSlotAnnotation systematicName = new GeneSystematicNameSlotAnnotation();
		systematicName.setNameType(systematicNameType);
		systematicName.setDisplayText("MissingNameType");
		systematicName.setFormatText("MissingNameType");
		systematicName.setSynonymScope(symbolNameType);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(61)
	public void editGeneWithMissingGeneSystematicNameDisplayText() {
		
		Gene gene = getGene();
		
		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		systematicName.setDisplayText(null);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(62)
	public void editGeneWithEmptyGeneSystematicNameDisplayText() {
		
		Gene gene = getGene();
		
		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		systematicName.setDisplayText("");
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(63)
	public void editGeneWithMissingGeneSystematicNameFormatText() {
		
		Gene gene = getGene();
		
		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		systematicName.setFormatText(null);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(64)
	public void editGeneWithEmptyGeneSystematicNameFormatText() {
		
		Gene gene = getGene();
		
		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		systematicName.setFormatText("");
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(65)
	public void editGeneWithMissingGeneSystematicNameNameType() {
		
		Gene gene = getGene();
		
		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		systematicName.setNameType(null);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(66)
	public void editGeneWithInvalidGeneSystematicNameNameType() {
		
		Gene gene = getGene();
		
		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		systematicName.setNameType(symbolNameType);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(67)
	public void editGeneWithInvalidGeneSystematicNameSynonymScope() {
		
		Gene gene = getGene();
		
		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		systematicName.setSynonymScope(systematicNameType);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.geneSystematicName", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(68)
	public void editGeneWithNullGeneSystematicNameSynonymScope() {
		Gene gene = getGene();

		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200).
			body("entity.geneSystematicName", hasKey("synonymScope"));
		
		GeneSystematicNameSlotAnnotation systematicName = gene.getGeneSystematicName();
		systematicName.setSynonymScope(null);
		gene.setGeneSystematicName(systematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200).
			body("entity.geneSystematicName", not(hasKey("synonymScope")));
	}

	@Test
	@Order(69)
	public void deleteGene() {

		RestAssured.given().
				when().
				delete("/api/gene/" + "GENE:0001").
				then().
				statusCode(200);
	}
	
	private void loadRequiredEntities() {

		soTerm = createSoTerm(GENE_TYPE, "Test SO term");
		nameType = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		synonymScope = getVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY);
		symbolNameType = getVocabularyTerm(nameType, "nomenclature_symbol");
		systematicNameType = getVocabularyTerm(nameType, "systematic_name");
		fullNameType = getVocabularyTerm(nameType, "full_name");
		exactSynonymScope = getVocabularyTerm(synonymScope, "exact");
		broadSynonymScope = getVocabularyTerm(synonymScope, "broad");
		taxon = getTaxonFromCurie(GENE_TAXON);
		geneSymbol = createGeneSymbolSlotAnnotation("GT1");
		geneFullName = createGeneFullNameSlotAnnotation("Gene test 1");
		geneSynonym = createGeneSynonymSlotAnnotation("Gene test synonym 1");
		geneSystematicName = createGeneSystematicNameSlotAnnotation("GT.1");
		
	}

	private Gene getGene() {
		ObjectResponse<Gene> res = RestAssured.given().
				when().
				get("/api/gene/GENE:0001").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefGene());

		return res.getEntity();
	}
	
	private GeneSymbolSlotAnnotation createGeneSymbolSlotAnnotation(String name) {
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setDisplayText(name);
		symbol.setFormatText(name);
		symbol.setNameType(symbolNameType);
		symbol.setSynonymScope(exactSynonymScope);
		
		return symbol;
	}

	private GeneFullNameSlotAnnotation createGeneFullNameSlotAnnotation(String name) {
		GeneFullNameSlotAnnotation fullName = new GeneFullNameSlotAnnotation();
		fullName.setDisplayText(name);
		fullName.setFormatText(name);
		fullName.setNameType(fullNameType);
		fullName.setSynonymScope(exactSynonymScope);
		
		return fullName;
	}

	private GeneSynonymSlotAnnotation createGeneSynonymSlotAnnotation(String name) {
		GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
		synonym.setDisplayText(name);
		synonym.setFormatText(name);
		synonym.setNameType(symbolNameType);
		synonym.setSynonymScope(exactSynonymScope);
		
		return synonym;
	}

	private GeneSystematicNameSlotAnnotation createGeneSystematicNameSlotAnnotation(String name) {
		GeneSystematicNameSlotAnnotation fullName = new GeneSystematicNameSlotAnnotation();
		fullName.setDisplayText(name);
		fullName.setFormatText(name);
		fullName.setNameType(systematicNameType);
		fullName.setSynonymScope(exactSynonymScope);
		
		return fullName;
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
			extract().body().as(getObjectResponseTypeRefNCBITaxonTerm());
		
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

	private TypeRef<ObjectResponse<Gene>> getObjectResponseTypeRefGene() {
		return new TypeRef<ObjectResponse <Gene>>() { };
	}

	private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRefNCBITaxonTerm() {
		return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
	}
	
	private TypeRef<ObjectListResponse<VocabularyTerm>> getObjectListResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectListResponse <VocabularyTerm>>() { };
	}

	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}

}
