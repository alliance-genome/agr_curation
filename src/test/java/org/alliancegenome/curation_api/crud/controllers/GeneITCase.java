package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
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
@Order(7)
public class GeneITCase extends BaseITCase {

	private final String GENE = "GENE:0001";
	private Vocabulary nameType;
	private Vocabulary synonymScope;
	private VocabularyTerm exactSynonymScope;
	private VocabularyTerm broadSynonymScope;
	private VocabularyTerm symbolNameType;
	private VocabularyTerm systematicNameType;
	private VocabularyTerm fullNameType;
	private SOTerm soTerm;
	private SOTerm soTerm2;
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private Reference reference;
	private Reference reference2;
	private Person person;
	private GeneSymbolSlotAnnotation geneSymbol;
	private GeneFullNameSlotAnnotation geneFullName;
	private GeneSynonymSlotAnnotation geneSynonym;
	private GeneSystematicNameSlotAnnotation geneSystematicName;
	
	private void loadRequiredEntities() {

		soTerm = createSoTerm("SO:0001");
		soTerm2 = createSoTerm("SO:0002");
		nameType = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		synonymScope = getVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY);
		symbolNameType = getVocabularyTerm(nameType, "nomenclature_symbol");
		systematicNameType = getVocabularyTerm(nameType, "systematic_name");
		fullNameType = getVocabularyTerm(nameType, "full_name");
		exactSynonymScope = getVocabularyTerm(synonymScope, "exact");
		broadSynonymScope = getVocabularyTerm(synonymScope, "broad");
		taxon = getNCBITaxonTerm("NCBITaxon:10090");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		reference = createReference("AGRKB:000010003", false);
		reference2 = createReference("AGRKB:000010005", false);
		person = createPerson("TEST:GenePerson0001");
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		geneSymbol = createGeneSymbolSlotAnnotation(List.of(reference), "GT1", symbolNameType, exactSynonymScope, "https://test.org");
		geneFullName = createGeneFullNameSlotAnnotation(List.of(reference), "Gene test 1", fullNameType, exactSynonymScope, "https://test.org");
		geneSynonym = createGeneSynonymSlotAnnotation(List.of(reference), "Gene test synonym 1", symbolNameType, exactSynonymScope, "https://test.org");
		geneSystematicName = createGeneSystematicNameSlotAnnotation(List.of(reference), "GT.1", systematicNameType, exactSynonymScope, "https://test.org");
		
	}
	@Test
	@Order(1)
	public void createValidGene() {
		loadRequiredEntities();
		
		Gene gene = new Gene();
		gene.setCurie(GENE);
		gene.setDateCreated(datetime);
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
			get("/api/gene/" + GENE).
			then().
			statusCode(200).
			body("entity.curie", is(GENE)).
			body("entity.taxon.curie", is(taxon.getCurie())).
			body("entity.geneType.curie", is(soTerm.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.dateCreated", is(datetime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.geneSymbol.displayText", is(geneSymbol.getDisplayText())).
			body("entity.geneSymbol.formatText", is(geneSymbol.getFormatText())).
			body("entity.geneSymbol.nameType.name", is(geneSymbol.getNameType().getName())).
			body("entity.geneSymbol.synonymScope.name", is(geneSymbol.getSynonymScope().getName())).
			body("entity.geneSymbol.synonymUrl", is(geneSymbol.getSynonymUrl())).
			body("entity.geneSymbol.evidence[0].curie", is(geneSymbol.getEvidence().get(0).getCurie())).
			body("entity.geneFullName.displayText", is(geneFullName.getDisplayText())).
			body("entity.geneFullName.formatText", is(geneFullName.getFormatText())).
			body("entity.geneFullName.nameType.name", is(geneFullName.getNameType().getName())).
			body("entity.geneFullName.synonymScope.name", is(geneFullName.getSynonymScope().getName())).
			body("entity.geneFullName.synonymUrl", is(geneFullName.getSynonymUrl())).
			body("entity.geneFullName.evidence[0].curie", is(geneFullName.getEvidence().get(0).getCurie())).
			body("entity.geneSynonyms[0].displayText", is(geneSynonym.getDisplayText())).
			body("entity.geneSynonyms[0].formatText", is(geneSynonym.getFormatText())).
			body("entity.geneSynonyms[0].nameType.name", is(geneSynonym.getNameType().getName())).
			body("entity.geneSynonyms[0].synonymScope.name", is(geneSynonym.getSynonymScope().getName())).
			body("entity.geneSynonyms[0].synonymUrl", is(geneSynonym.getSynonymUrl())).
			body("entity.geneSynonyms[0].evidence[0].curie", is(geneSynonym.getEvidence().get(0).getCurie())).
			body("entity.geneSystematicName.displayText", is(geneSystematicName.getDisplayText())).
			body("entity.geneSystematicName.formatText", is(geneSystematicName.getFormatText())).
			body("entity.geneSystematicName.nameType.name", is(geneSystematicName.getNameType().getName())).
			body("entity.geneSystematicName.synonymScope.name", is(geneSystematicName.getSynonymScope().getName())).
			body("entity.geneSystematicName.synonymUrl", is(geneSystematicName.getSynonymUrl())).
			body("entity.geneSystematicName.evidence[0].curie", is(geneSystematicName.getEvidence().get(0).getCurie()));
	}

	@Test
	@Order(2)
	public void editGene() {
		Gene gene = getGene("GENE:0001");
		gene.setTaxon(taxon2);
		gene.setGeneType(soTerm2);
		gene.setInternal(true);
		gene.setObsolete(true);
		gene.setDateCreated(datetime2);
		gene.setCreatedBy(person);
		
		GeneSymbolSlotAnnotation editedSymbol = gene.getGeneSymbol();
		editedSymbol.setNameType(systematicNameType);
		editedSymbol.setDisplayText("GT2 display");
		editedSymbol.setFormatText("GT2 format");
		editedSymbol.setSynonymScope(broadSynonymScope);
		editedSymbol.setSynonymUrl("https://test2.org");
		editedSymbol.setEvidence(List.of(reference2));
		gene.setGeneSymbol(editedSymbol);
		
		GeneFullNameSlotAnnotation editedFullName = gene.getGeneFullName();
		editedFullName.setDisplayText("Gene test 2 display");
		editedFullName.setFormatText("Gene test 2 format");
		editedFullName.setSynonymScope(broadSynonymScope);
		editedFullName.setSynonymUrl("https://test2.org");
		editedFullName.setEvidence(List.of(reference2));
		gene.setGeneFullName(editedFullName);
		
		GeneSynonymSlotAnnotation editedSynonym = gene.getGeneSynonyms().get(0);
		editedSynonym.setNameType(systematicNameType);
		editedSynonym.setDisplayText("Gene test synonym 2 display");
		editedSynonym.setFormatText("Gene test synonym 2 format");
		editedSynonym.setSynonymScope(broadSynonymScope);
		editedSynonym.setSynonymUrl("https://test2.org");
		editedSynonym.setEvidence(List.of(reference2));
		gene.setGeneSynonyms(List.of(editedSynonym));
		
		GeneSystematicNameSlotAnnotation editedSystematicName = gene.getGeneSystematicName();
		editedSystematicName.setDisplayText("GT.2 display");
		editedSystematicName.setFormatText("GT.2 format");
		editedSystematicName.setSynonymScope(broadSynonymScope);
		editedSystematicName.setSynonymUrl("https://test2.org");
		editedSystematicName.setEvidence(List.of(reference2));
		gene.setGeneSystematicName(editedSystematicName);

		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200);

		RestAssured.given().
			when().
			get("/api/gene/" + GENE).
			then().
			statusCode(200).
			body("entity.curie", is(GENE)).
			body("entity.taxon.curie", is(taxon2.getCurie())).
			body("entity.geneType.curie", is(soTerm2.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.dateCreated", is(datetime2.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.geneSymbol.displayText", is(editedSymbol.getDisplayText())).
			body("entity.geneSymbol.formatText", is(editedSymbol.getFormatText())).
			body("entity.geneSymbol.nameType.name", is(editedSymbol.getNameType().getName())).
			body("entity.geneSymbol.synonymScope.name", is(editedSymbol.getSynonymScope().getName())).
			body("entity.geneSymbol.synonymUrl", is(editedSymbol.getSynonymUrl())).
			body("entity.geneSymbol.evidence[0].curie", is(editedSymbol.getEvidence().get(0).getCurie())).
			body("entity.geneFullName.displayText", is(editedFullName.getDisplayText())).
			body("entity.geneFullName.formatText", is(editedFullName.getFormatText())).
			body("entity.geneFullName.nameType.name", is(editedFullName.getNameType().getName())).
			body("entity.geneFullName.synonymScope.name", is(editedFullName.getSynonymScope().getName())).
			body("entity.geneFullName.synonymUrl", is(editedFullName.getSynonymUrl())).
			body("entity.geneFullName.evidence[0].curie", is(editedFullName.getEvidence().get(0).getCurie())).
			body("entity.geneSynonyms[0].displayText", is(editedSynonym.getDisplayText())).
			body("entity.geneSynonyms[0].formatText", is(editedSynonym.getFormatText())).
			body("entity.geneSynonyms[0].nameType.name", is(editedSynonym.getNameType().getName())).
			body("entity.geneSynonyms[0].synonymScope.name", is(editedSynonym.getSynonymScope().getName())).
			body("entity.geneSynonyms[0].synonymUrl", is(editedSynonym.getSynonymUrl())).
			body("entity.geneSynonyms[0].evidence[0].curie", is(editedSynonym.getEvidence().get(0).getCurie())).
			body("entity.geneSystematicName.displayText", is(editedSystematicName.getDisplayText())).
			body("entity.geneSystematicName.formatText", is(editedSystematicName.getFormatText())).
			body("entity.geneSystematicName.nameType.name", is(editedSystematicName.getNameType().getName())).
			body("entity.geneSystematicName.synonymScope.name", is(editedSystematicName.getSynonymScope().getName())).
			body("entity.geneSystematicName.synonymUrl", is(editedSystematicName.getSynonymUrl())).
			body("entity.geneSystematicName.evidence[0].curie", is(editedSystematicName.getEvidence().get(0).getCurie()));
	}
	
	@Test
	@Order(3)
	public void createGeneWithMissingRequiredFieldsLevel1() {

		Gene gene = new Gene();
		gene.setDateCreated(datetime);
		gene.setGeneType(soTerm);
		gene.setGeneFullName(geneFullName);
		gene.setGeneSynonyms(List.of(geneSynonym));
		gene.setGeneSystematicName(geneSystematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.geneSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editGeneWithMissingCurie() {
		Gene gene = getGene(GENE);
		gene.setCurie(null);
		
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
	@Order(5)
	public void editGeneWithMissingRequiredFieldsLevel1() {
		Gene gene = getGene(GENE);
		gene.setTaxon(null);
		gene.setGeneSymbol(null);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(2))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.geneSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void createGeneWithEmptyRequiredFieldsLevel1() {

		Gene gene = new Gene();
		gene.setCurie("");
		gene.setDateCreated(datetime);
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
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(7)
	public void editGeneWithEmptyCurie() {
		Gene gene = getGene(GENE);
		gene.setCurie("");
		
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
	@Order(8)
	public void createGeneWithMissingRequiredFieldsLevel2() {

		Gene gene = new Gene();
		gene.setCurie("GENE:0008");
		gene.setDateCreated(datetime);
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		GeneSymbolSlotAnnotation invalidSymbol = createGeneSymbolSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		GeneFullNameSlotAnnotation invalidFullName = createGeneFullNameSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		GeneSynonymSlotAnnotation invalidSynonym = createGeneSynonymSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		GeneSystematicNameSlotAnnotation invalidSystematicName = createGeneSystematicNameSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		
		gene.setGeneSymbol(invalidSymbol);
		gene.setGeneFullName(invalidFullName);
		gene.setGeneSynonyms(List.of(invalidSynonym));
		gene.setGeneSystematicName(invalidSystematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.geneSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneSystematicName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(9)
	public void editGeneWithMissingRequiredFieldsLevel2() {

		Gene gene = getGene(GENE);
		
		GeneSymbolSlotAnnotation invalidSymbol = createGeneSymbolSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		GeneFullNameSlotAnnotation invalidFullName = createGeneFullNameSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		GeneSynonymSlotAnnotation invalidSynonym = createGeneSynonymSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		GeneSystematicNameSlotAnnotation invalidSystematicName = createGeneSystematicNameSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		
		gene.setGeneSymbol(invalidSymbol);
		gene.setGeneFullName(invalidFullName);
		gene.setGeneSynonyms(List.of(invalidSynonym));
		gene.setGeneSystematicName(invalidSystematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.geneSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneSystematicName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(10)
	public void createGeneWithEmptyRequiredFieldsLevel2() {

		Gene gene = new Gene();
		gene.setCurie("GENE:0010");
		gene.setDateCreated(datetime);
		gene.setTaxon(taxon);
		gene.setGeneType(soTerm);
		
		GeneSymbolSlotAnnotation invalidSymbol = createGeneSymbolSlotAnnotation(List.of(reference), "", symbolNameType, exactSynonymScope, "https://test.org");
		GeneFullNameSlotAnnotation invalidFullName = createGeneFullNameSlotAnnotation(List.of(reference), "", fullNameType, exactSynonymScope, "https://test.org");
		GeneSynonymSlotAnnotation invalidSynonym = createGeneSynonymSlotAnnotation(List.of(reference), "", symbolNameType, exactSynonymScope, "https://test.org");
		GeneSystematicNameSlotAnnotation invalidSystematicName = createGeneSystematicNameSlotAnnotation(List.of(reference), "", systematicNameType, exactSynonymScope, "https://test.org");
		
		gene.setGeneSymbol(invalidSymbol);
		gene.setGeneFullName(invalidFullName);
		gene.setGeneSynonyms(List.of(invalidSynonym));
		gene.setGeneSystematicName(invalidSystematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.geneSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneSystematicName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(11)
	public void editGeneWithEmptyRequiredFieldsLevel2() {

		Gene gene = getGene(GENE);
		
		GeneSymbolSlotAnnotation invalidSymbol = createGeneSymbolSlotAnnotation(List.of(reference), "", symbolNameType, exactSynonymScope, "https://test.org");
		GeneFullNameSlotAnnotation invalidFullName = createGeneFullNameSlotAnnotation(List.of(reference), "", fullNameType, exactSynonymScope, "https://test.org");
		GeneSynonymSlotAnnotation invalidSynonym = createGeneSynonymSlotAnnotation(List.of(reference), "", symbolNameType, exactSynonymScope, "https://test.org");
		GeneSystematicNameSlotAnnotation invalidSystematicName = createGeneSystematicNameSlotAnnotation(List.of(reference), "", systematicNameType, exactSynonymScope, "https://test.org");
		
		gene.setGeneSymbol(invalidSymbol);
		gene.setGeneFullName(invalidFullName);
		gene.setGeneSynonyms(List.of(invalidSynonym));
		gene.setGeneSystematicName(invalidSystematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.geneSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.geneSystematicName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(12)
	public void createGeneWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:Invalid");
		
		Gene gene = new Gene();
		gene.setCurie("GENE:0012");
		gene.setDateCreated(datetime);
		gene.setTaxon(nonPersistedTaxon);
		gene.setGeneType(nonPersistedSoTerm);
		
		GeneSymbolSlotAnnotation invalidSymbol = createGeneSymbolSlotAnnotation(List.of(nonPersistedReference), "Test symbol", fullNameType, fullNameType, "https://test.org");
		GeneFullNameSlotAnnotation invalidFullName = createGeneFullNameSlotAnnotation(List.of(nonPersistedReference), "Test name", symbolNameType, fullNameType, "https://test.org");
		GeneSynonymSlotAnnotation invalidSynonym = createGeneSynonymSlotAnnotation(List.of(nonPersistedReference), "Test synonym", exactSynonymScope, fullNameType, "https://test.org");
		GeneSystematicNameSlotAnnotation invalidSystematicName = createGeneSystematicNameSlotAnnotation(List.of(nonPersistedReference), "Test name", symbolNameType, fullNameType, "https://test.org");

		gene.setGeneSymbol(invalidSymbol);
		gene.setGeneFullName(invalidFullName);
		gene.setGeneSynonyms(List.of(invalidSynonym));
		gene.setGeneSystematicName(invalidSystematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.geneFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.geneSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.geneSystematicName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(13)
	public void editGeneWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:Invalid");
		
		Gene gene = getGene(GENE);
		gene.setTaxon(nonPersistedTaxon);
		gene.setGeneType(nonPersistedSoTerm);
		
		GeneSymbolSlotAnnotation invalidSymbol = createGeneSymbolSlotAnnotation(List.of(nonPersistedReference), "Test symbol", fullNameType, fullNameType, "https://test.org");
		GeneFullNameSlotAnnotation invalidFullName = createGeneFullNameSlotAnnotation(List.of(nonPersistedReference), "Test name", symbolNameType, fullNameType, "https://test.org");
		GeneSynonymSlotAnnotation invalidSynonym = createGeneSynonymSlotAnnotation(List.of(nonPersistedReference), "Test synonym", exactSynonymScope, fullNameType, "https://test.org");
		GeneSystematicNameSlotAnnotation invalidSystematicName = createGeneSystematicNameSlotAnnotation(List.of(nonPersistedReference), "Test name", symbolNameType, fullNameType, "https://test.org");

		gene.setGeneSymbol(invalidSymbol);
		gene.setGeneFullName(invalidFullName);
		gene.setGeneSynonyms(List.of(invalidSynonym));
		gene.setGeneSystematicName(invalidSystematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.geneFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.geneSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.geneSystematicName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(14)
	public void editGeneWithNullNonRequiredFieldsLevel2() {
		// Level 2 done before 1 to avoid having to restore nulled fields
		Gene gene = getGene(GENE);
		
		GeneSymbolSlotAnnotation editedSymbol = gene.getGeneSymbol();
		editedSymbol.setEvidence(null);
		editedSymbol.setSynonymScope(null);
		editedSymbol.setSynonymUrl(null);
		
		GeneFullNameSlotAnnotation editedFullName = gene.getGeneFullName();
		editedFullName.setEvidence(null);
		editedFullName.setSynonymScope(null);
		editedFullName.setSynonymUrl(null);
		
		GeneSynonymSlotAnnotation editedSynonym = gene.getGeneSynonyms().get(0);
		editedSynonym.setEvidence(null);
		editedSynonym.setSynonymScope(null);
		editedSynonym.setSynonymUrl(null);
		
		GeneSystematicNameSlotAnnotation editedSystematicName = gene.getGeneSystematicName();
		editedSystematicName.setEvidence(null);
		editedSystematicName.setSynonymScope(null);
		editedSystematicName.setSynonymUrl(null);
		
		gene.setGeneSymbol(editedSymbol);
		gene.setGeneFullName(editedFullName);
		gene.setGeneSynonyms(List.of(editedSynonym));
		gene.setGeneSystematicName(editedSystematicName);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200);
	
		RestAssured.given().
			when().
			get("/api/gene/" + GENE).
			then().
			statusCode(200).
			body("entity", hasKey("geneSymbol")).
			body("entity", hasKey("geneFullName")).
			body("entity", hasKey("geneSynonyms")).
			body("entity", hasKey("geneSystematicName")).
			body("entity.geneSymbol", not(hasKey("evidence"))).
			body("entity.geneSymbol", not(hasKey("synonymScope"))).
			body("entity.geneSymbol", not(hasKey("synonymUrl"))).
			body("entity.geneFullName", not(hasKey("evidence"))).
			body("entity.geneFullName", not(hasKey("synonymScope"))).
			body("entity.geneFullName", not(hasKey("synonymUrl"))).
			body("entity.geneSynonyms[0]", not(hasKey("evidence"))).
			body("entity.geneSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.geneSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.geneSystematicName", not(hasKey("evidence"))).
			body("entity.geneSystematicName", not(hasKey("synonymScope"))).
			body("entity.geneSystematicName", not(hasKey("synonymUrl")));
	}
	
	@Test
	@Order(15)
	public void editGeneWithNullNonRequiredFieldsLevel1() {
		Gene gene = getGene(GENE);
		
		gene.setGeneType(null);
		gene.setGeneFullName(null);
		gene.setGeneSynonyms(null);
		gene.setGeneSystematicName(null);
		
		RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			put("/api/gene").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/gene/" + GENE).
			then().
			statusCode(200).
			body("entity", not(hasKey("geneType"))).
			body("entity", not(hasKey("geneFullName"))).
			body("entity", not(hasKey("geneSynonyms"))).
			body("entity", not(hasKey("geneSystematicName")));
	}
	
	@Test
	@Order(16)
	public void deleteGene() {

		RestAssured.given().
				when().
				delete("/api/gene/" + "GENE:0001").
				then().
				statusCode(200);
	}
	
	private GeneSymbolSlotAnnotation createGeneSymbolSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setEvidence(evidence);
		symbol.setDisplayText(name);
		symbol.setFormatText(name);
		symbol.setNameType(nameType);
		symbol.setSynonymScope(synonymScope);
		symbol.setSynonymUrl(synonymUrl);
		
		return symbol;
	}

	private GeneFullNameSlotAnnotation createGeneFullNameSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		GeneFullNameSlotAnnotation fullName = new GeneFullNameSlotAnnotation();
		fullName.setEvidence(evidence);
		fullName.setDisplayText(name);
		fullName.setFormatText(name);
		fullName.setNameType(nameType);
		fullName.setSynonymScope(synonymScope);
		fullName.setSynonymUrl(synonymUrl);
		
		return fullName;
	}

	private GeneSynonymSlotAnnotation createGeneSynonymSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
		synonym.setEvidence(evidence);
		synonym.setDisplayText(name);
		synonym.setFormatText(name);
		synonym.setNameType(nameType);
		synonym.setSynonymScope(synonymScope);
		synonym.setSynonymUrl(synonymUrl);
		
		return synonym;
	}

	private GeneSystematicNameSlotAnnotation createGeneSystematicNameSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		GeneSystematicNameSlotAnnotation systematicName = new GeneSystematicNameSlotAnnotation();
		systematicName.setEvidence(evidence);
		systematicName.setDisplayText(name);
		systematicName.setFormatText(name);
		systematicName.setNameType(nameType);
		systematicName.setSynonymScope(synonymScope);
		systematicName.setSynonymUrl(synonymUrl);
		
		return systematicName;
	}

}
