package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteUseSlotAnnotation;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

/**
 * SCRUM-6746: editing a cassette from the Cassettes table, through PUT /api/cassette and the
 * cassette slot annotation validate endpoints. Runs on WB:Cassette0001 as the bulk loads left it
 * (IT_0110), with its free text component and its genomic entity and transgenic tool associations
 * (IT_0205, IT_0206).
 *
 * SCRUM-6748: creating a cassette as the create page does, through POST /api/cassette and then its
 * component lists' own endpoints. The test containers have no MaTI, so the new cassette gets no
 * minted curie here (CurieMintService logs the failure); it is read back by its MOD internal ID.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("317 - CassetteITCase")
@Order(317)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0317_CassetteITCase extends BaseITCase {

	private static final String CASSETTE = "WB:Cassette0001";
	private static final String USE_NAMESPACE = "experimental_tool_descriptor";
	private static final String CREATED_CASSETTE = "IT:Cassette0317";

	private FBCVTerm toolDescriptor;
	private int componentCount;
	private int genomicEntityAssociationCount;
	private int transgenicToolAssociationCount;

	private FBCVTerm createFbcvTermInNamespace(String curie, String name, String namespace) {
		FBCVTerm term = new FBCVTerm();
		term.setCurie(curie);
		term.setName(name);
		term.setNamespace(namespace);
		term.setObsolete(false);
		ObjectResponse<FBCVTerm> response = RestAssured.given().
			contentType("application/json").
			body(term).
			when().
			post("/api/fbcvterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefFBCVTerm());
		return response.getEntity();
	}

	// The cassette's first use, pointing at the given term
	private Cassette cassetteWithUse(FBCVTerm term) {
		Cassette cassette = getCassette(CASSETTE);
		CassetteUseSlotAnnotation use = cassette.getCassetteUses().get(0);
		use.setUses(List.of(term));
		cassette.setCassetteUses(List.of(use));
		return cassette;
	}

	@Test
	@Order(1)
	public void editKeepsComponentsAndAssociations() {
		toolDescriptor = createFbcvTermInNamespace("FBcv:0009001", "test_tool_descriptor", USE_NAMESPACE);

		Cassette cassette = cassetteWithUse(toolDescriptor);
		componentCount = cassette.getCassetteComponents().size();
		genomicEntityAssociationCount = cassette.getCassetteGenomicEntityAssociations().size();
		transgenicToolAssociationCount = cassette.getCassetteTransgenicToolAssociations().size();

		CassetteSymbolSlotAnnotation symbol = cassette.getCassetteSymbol();
		symbol.setDisplayText("CsEdited");
		symbol.setFormatText("Cs<sup>Edited</sup>");
		cassette.setCassetteSymbol(symbol);
		cassette.setSecondaryIdentifiers(List.of("secondary_edited"));
		cassette.setPlaceholder(false);

		// The table sends back its trimmed view of these lists; whatever it sends, the stored ones stay
		cassette.setCassetteComponents(List.of());
		cassette.setCassetteGenomicEntityAssociations(List.of());
		cassette.setCassetteTransgenicToolAssociations(null);

		RestAssured.given().
			contentType("application/json").
			body(cassette).
			when().
			put("/api/cassette").
			then().
			log().ifValidationFails().
			statusCode(200);

		RestAssured.given().
			when().
			get("/api/cassette/" + CASSETTE).
			then().
			statusCode(200).
			body("entity.cassetteSymbol.displayText", is("CsEdited")).
			body("entity.secondaryIdentifiers", is(List.of("secondary_edited"))).
			body("entity.placeholder", is(false)).
			body("entity.cassetteUses[0].uses[0].curie", is("FBcv:0009001")).
			body("entity.cassetteComponents", hasSize(componentCount)).
			body("entity.cassetteGenomicEntityAssociations", hasSize(genomicEntityAssociationCount)).
			body("entity.cassetteTransgenicToolAssociations", hasSize(transgenicToolAssociationCount));
	}

	@Test
	@Order(2)
	public void useOutsideToolDescriptorNamespaceIsRejected() {
		FBCVTerm otherNamespace = createFbcvTermInNamespace("FBcv:0009002", "test_other_term", "genotype_phenotype");

		RestAssured.given().
			contentType("application/json").
			body(cassetteWithUse(otherNamespace)).
			when().
			put("/api/cassette").
			then().
			statusCode(400).
			body("errorMessages.cassetteUses", containsString(USE_NAMESPACE));
	}

	@Test
	@Order(3)
	public void slotAnnotationValidateEndpoints() {
		Cassette cassette = getCassette(CASSETTE);
		// As the pop-ups send them: without the audit people, which they strip before validating
		CassetteSymbolSlotAnnotation symbol = cassette.getCassetteSymbol();
		symbol.setCreatedBy(null);
		symbol.setUpdatedBy(null);
		CassetteUseSlotAnnotation use = cassette.getCassetteUses().get(0);
		use.setCreatedBy(null);
		use.setUpdatedBy(null);

		RestAssured.given().
			contentType("application/json").
			body(symbol).
			when().
			post("/api/cassettesymbolslotannotation/validate").
			then().
			statusCode(200);

		RestAssured.given().
			contentType("application/json").
			body(use).
			when().
			post("/api/cassetteuseslotannotation/validate").
			then().
			statusCode(200);

		CassetteUseSlotAnnotation invalidUse = cassetteWithUse(createFbcvTermInNamespace("FBcv:0009003", "test_other_term_2", "genotype_phenotype")).getCassetteUses().get(0);
		invalidUse.setCreatedBy(null);
		invalidUse.setUpdatedBy(null);
		RestAssured.given().
			contentType("application/json").
			body(invalidUse).
			when().
			post("/api/cassetteuseslotannotation/validate").
			then().
			statusCode(400).
			body("errorMessages.uses", containsString(USE_NAMESPACE));
	}

	@Test
	@Order(4)
	public void synonymNameTypeIsSaved() {
		VocabularyTerm systematicName = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "systematic_name");
		Cassette cassette = getCassette(CASSETTE);
		CassetteSynonymSlotAnnotation synonym = cassette.getCassetteSynonyms().get(0);
		synonym.setNameType(systematicName);
		cassette.setCassetteSynonyms(List.of(synonym));

		RestAssured.given().
			contentType("application/json").
			body(cassette).
			when().
			put("/api/cassette").
			then().
			statusCode(200);

		RestAssured.given().
			when().
			get("/api/cassette/" + CASSETTE).
			then().
			statusCode(200).
			body("entity.cassetteSynonyms[0].nameType.name", is("systematic_name"));

		// A synonym's name type is required, as a load requires it
		Cassette withoutNameType = getCassette(CASSETTE);
		withoutNameType.getCassetteSynonyms().get(0).setNameType(null);
		RestAssured.given().
			contentType("application/json").
			body(withoutNameType).
			when().
			put("/api/cassette").
			then().
			statusCode(400).
			body("errorMessages.cassetteSynonyms", notNullValue());
	}

	// A new cassette as the create page sends it: its own fields, without the component lists or a data provider
	private Cassette newCassette(String symbolText) {
		Cassette stored = getCassette(CASSETTE);
		VocabularyTerm fullNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "full_name");
		VocabularyTerm systematicName = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "systematic_name");

		CassetteSymbolSlotAnnotation symbol = new CassetteSymbolSlotAnnotation();
		symbol.setNameType(stored.getCassetteSymbol().getNameType());
		symbol.setDisplayText(symbolText);
		symbol.setFormatText(symbolText);

		CassetteFullNameSlotAnnotation fullName = new CassetteFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setDisplayText(symbolText + " cassette");
		fullName.setFormatText(symbolText + " cassette");

		CassetteSynonymSlotAnnotation synonym = new CassetteSynonymSlotAnnotation();
		synonym.setNameType(systematicName);
		synonym.setDisplayText(symbolText + "-syn");
		synonym.setFormatText(symbolText + "-syn");

		CassetteUseSlotAnnotation use = new CassetteUseSlotAnnotation();
		use.setUses(List.of(toolDescriptor));

		Cassette cassette = new Cassette();
		cassette.setCassetteSymbol(symbol);
		cassette.setCassetteFullName(fullName);
		cassette.setCassetteSynonyms(List.of(synonym));
		cassette.setCassetteUses(List.of(use));
		cassette.setSecondaryIdentifiers(List.of("secondary_created"));
		cassette.setPlaceholder(false);
		cassette.setInternal(false);
		return cassette;
	}

	@Test
	@Order(5)
	public void createCassette() {
		Cassette cassette = newCassette("CsCreated");
		cassette.setModInternalId(CREATED_CASSETTE);

		RestAssured.given().
			contentType("application/json").
			body(cassette).
			when().
			post("/api/cassette").
			then().
			log().ifValidationFails().
			statusCode(200).
			body("entity.id", notNullValue()).
			body("entity.uniqueId", notNullValue()).
			// the curator's MOD, or the Alliance for a curator without one
			body("entity.dataProvider.abbreviation", notNullValue());

		RestAssured.given().
			when().
			get("/api/cassette/" + CREATED_CASSETTE).
			then().
			statusCode(200).
			body("entity.cassetteSymbol.displayText", is("CsCreated")).
			body("entity.cassetteFullName.displayText", is("CsCreated cassette")).
			body("entity.cassetteSynonyms[0].displayText", is("CsCreated-syn")).
			body("entity.cassetteUses[0].uses[0].curie", is("FBcv:0009001")).
			body("entity.secondaryIdentifiers", is(List.of("secondary_created")));
	}

	@Test
	@Order(6)
	public void createdCassetteTakesItsComponentListsThroughTheirOwnEndpoints() {
		Cassette created = getCassette(CREATED_CASSETTE);
		String uniqueIdWithoutComponents = created.getUniqueId();

		CassetteComponentSlotAnnotation component = new CassetteComponentSlotAnnotation();
		component.setRelation(getCassette(CASSETTE).getCassetteComponents().get(0).getRelation());
		component.setComponentSymbol("ITComponent");
		component.setTaxonText("Aequorea victoria");

		RestAssured.given().
			contentType("application/json").
			body(List.of(component)).
			when().
			put("/api/cassette/" + created.getId() + "/components").
			then().
			log().ifValidationFails().
			statusCode(200).
			body("entities", hasSize(1));

		// The unique ID is built from the free text components too
		RestAssured.given().
			when().
			get("/api/cassette/" + CREATED_CASSETTE).
			then().
			statusCode(200).
			body("entity.cassetteComponents[0].componentSymbol", is("ITComponent")).
			body("entity.uniqueId", not(is(uniqueIdWithoutComponents)));
	}

	@Test
	@Order(7)
	public void createNeedsNoModIdentifierAndRejectsADuplicateAndAMissingSymbol() {
		// As the create page sends it, with no MOD internal or primary external ID: its minted curie identifies it
		Cassette first = newCassette("CsTwice");
		RestAssured.given().
			contentType("application/json").
			body(first).
			when().
			post("/api/cassette").
			then().
			log().ifValidationFails().
			statusCode(200);

		// The same symbol and name as a cassette without free text components gives the same unique ID

		RestAssured.given().
			contentType("application/json").
			body(newCassette("CsTwice")).
			when().
			post("/api/cassette").
			then().
			statusCode(400).
			body("errorMessages.uniqueId", notNullValue());

		Cassette withoutSymbol = newCassette("CsNoSymbol");
		withoutSymbol.setCassetteSymbol(null);
		RestAssured.given().
			contentType("application/json").
			body(withoutSymbol).
			when().
			post("/api/cassette").
			then().
			statusCode(400).
			body("errorMessages.cassetteSymbol", notNullValue());
	}

	private int cassetteCount() {
		return RestAssured.given().
			contentType("application/json").
			body("{}").
			when().
			post("/api/cassette/find?limit=1&page=0").
			then().
			statusCode(200).
			extract().path("totalResults");
	}

	@Test
	@Order(8)
	public void createTakesTheFreeTextComponentsAndRefusesADuplicateBeforeSavingAnything() {
		CassetteComponentSlotAnnotation component = new CassetteComponentSlotAnnotation();
		component.setRelation(getCassette(CASSETTE).getCassetteComponents().get(0).getRelation());
		component.setComponentSymbol("ITCreatedComponent");
		component.setTaxonText("Aequorea victoria");

		// As the create page sends it (SCRUM-6748): the free text components with the cassette, as the unique ID is
		// built from them
		Cassette first = newCassette("CsWithComponent");
		first.setModInternalId(CREATED_CASSETTE + "c");
		first.setCassetteComponents(List.of(component));
		RestAssured.given().
			contentType("application/json").
			body(first).
			when().
			post("/api/cassette").
			then().
			log().ifValidationFails().
			statusCode(200).
			body("entity.cassetteComponents", hasSize(1)).
			body("entity.uniqueId", containsString("ITCreatedComponent"));

		RestAssured.given().
			when().
			get("/api/cassette/" + CREATED_CASSETTE + "c").
			then().
			statusCode(200).
			body("entity.cassetteComponents[0].componentSymbol", is("ITCreatedComponent")).
			body("entity.uniqueId", containsString("ITCreatedComponent"));

		// Save & Add Another without a change: the same symbol, name and free text components are refused before
		// anything is saved, not created and then left without the component its unique ID clashes over
		int before = cassetteCount();
		Cassette again = newCassette("CsWithComponent");
		again.setCassetteComponents(List.of(component));
		RestAssured.given().
			contentType("application/json").
			body(again).
			when().
			post("/api/cassette").
			then().
			statusCode(400).
			body("errorMessages.uniqueId", notNullValue());
		Assertions.assertEquals(before, cassetteCount());
	}

	@Test
	@Order(9)
	public void deleteRemovesACassetteThatNoConstructUses() {
		// A cassette as a curator creates it, then gives a free text component
		Cassette cassette = newCassette("CsToDelete");
		cassette.setModInternalId(CREATED_CASSETTE + "d");
		RestAssured.given().
			contentType("application/json").
			body(cassette).
			when().
			post("/api/cassette").
			then().
			log().ifValidationFails().
			statusCode(200);
		Cassette created = getCassette(CREATED_CASSETTE + "d");

		CassetteComponentSlotAnnotation component = new CassetteComponentSlotAnnotation();
		component.setRelation(getCassette(CASSETTE).getCassetteComponents().get(0).getRelation());
		component.setComponentSymbol("ITDeletedComponent");
		RestAssured.given().
			contentType("application/json").
			body(List.of(component)).
			when().
			put("/api/cassette/" + created.getId() + "/components").
			then().
			statusCode(200);

		RestAssured.given().
			when().
			delete("/api/cassette/" + CREATED_CASSETTE + "d").
			then().
			log().ifValidationFails().
			statusCode(200).
			body("entity.id", is(created.getId().intValue()));

		// Gone, with the slot annotations it held
		RestAssured.given().
			when().
			get("/api/cassette/" + CREATED_CASSETTE + "d").
			then().
			statusCode(200).
			body("entity", nullValue());
		RestAssured.given().
			when().
			get("/api/cassette/" + created.getId() + "/components").
			then().
			statusCode(400);
	}

	@Test
	@Order(10)
	public void deleteRefusesACassetteAConstructUsesAndAnUnknownOne() {
		// WB:Cassette0001 is the cassette of WB:Construct0001, from IT_0204's construct cassette association load
		RestAssured.given().
			when().
			delete("/api/cassette/" + CASSETTE).
			then().
			statusCode(400).
			body("errorMessage", containsString("is in use and cannot be deleted"));

		RestAssured.given().
			when().
			get("/api/cassette/" + CASSETTE).
			then().
			statusCode(200).
			body("entity.id", notNullValue());

		RestAssured.given().
			when().
			delete("/api/cassette/IT:NoSuchCassette").
			then().
			statusCode(400).
			body("errorMessage", containsString("Could not find Cassette"));
	}
}
