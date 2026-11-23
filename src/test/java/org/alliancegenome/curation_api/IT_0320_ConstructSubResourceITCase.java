package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("0320 - ConstructSubResourceITCase")
@Order(320)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0320_ConstructSubResourceITCase extends BaseITCase {

	private static final String CONSTRUCT = "Construct:6760Sub0001";
	private static final String CASSETTE = "Cassette:6760Sub0001";
	private static final String REFERENCE = "AGRKB:6760000001";
	private static final String OBSOLETE_REFERENCE = "AGRKB:6760000002";
	private static final String SECONDARY_ID = "TEST:6760Secondary1";
	private static final String OTHER_SECONDARY_ID = "TEST:6760Secondary2";

	private Long constructId;
	private VocabularyTerm symbolNameType;
	private VocabularyTerm systematicNameType;
	private VocabularyTerm constructNoteType;
	private VocabularyTerm nonConstructNoteType;
	private VocabularyTerm cassetteRelation;
	private Reference reference;
	private Reference obsoleteReference;
	private Cassette cassette;
	private Long cassetteAssociationId;

	private void loadRequiredEntities() {
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		systematicNameType = getVocabularyTerm(nameTypeVocabulary, "systematic_name");
		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		constructNoteType = addVocabularyTermToSet(VocabularyConstants.CONSTRUCT_NOTE_TYPES_VOCABULARY_TERM_SET, "6760_construct_note_type", noteTypeVocabulary, false);
		// A note type term outside the construct note types set.
		nonConstructNoteType = createVocabularyTerm(noteTypeVocabulary, "6760_non_construct_note_type", false);
		cassetteRelation = getVocabularyTerm(getVocabulary(VocabularyConstants.CONSTRUCT_CASSETTE_RELATION_VOCABULARY_TERM_SET), "has_component");
		reference = createReference(REFERENCE, false);
		obsoleteReference = createReference(OBSOLETE_REFERENCE, true);
		cassette = createCassette(createOrganization("CNST6760", false));
		constructId = createConstruct(CONSTRUCT, false, symbolNameType).getId();
	}

	@Test
	@Order(1)
	public void updateDetailLeavesListsAsStored() {
		loadRequiredEntities();

		Construct construct = getConstruct(CONSTRUCT);
		construct.setSecondaryIdentifiers(List.of(SECONDARY_ID));
		construct.setReferences(List.of(reference));
		construct.setConstructSynonyms(List.of(buildSynonym("6760 synonym")));
		construct.setConstructCassetteAssociations(List.of(buildCassetteAssociation(null)));
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(200);

		Construct stored = getConstruct(CONSTRUCT);
		stored.getConstructSymbol().setDisplayText("6760 symbol");
		stored.getConstructSymbol().setFormatText("6760 symbol");
		stored.setPlaceholder(true);
		// The lists the payload still carries are not the detail endpoint's to write, so emptying them changes nothing.
		stored.setSecondaryIdentifiers(List.of());
		stored.setReferences(List.of());
		stored.setConstructSynonyms(List.of());
		stored.setConstructCassetteAssociations(List.of());
		RestAssured.given().
			contentType("application/json").
			body(stored).
			when().
			put("/api/construct/updateDetail").
			then().
			statusCode(200).
			body("entity.constructSymbol.displayText", is("6760 symbol")).
			body("entity.placeholder", is(true)).
			body("entity.secondaryIdentifiers", nullValue());

		RestAssured.given().
			when().
			get("/api/construct/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity.constructSymbol.displayText", is("6760 symbol")).
			body("entity.placeholder", is(true)).
			body("entity.secondaryIdentifiers", contains(SECONDARY_ID)).
			body("entity.references[0].curie", is(REFERENCE)).
			body("entity.constructSynonyms", hasSize(1)).
			body("entity.constructCassetteAssociations", hasSize(1));
	}

	@Test
	@Order(2)
	public void replaceSecondaryIds() {
		put("secondaryids", SECONDARY_ID, " " + OTHER_SECONDARY_ID + " ").
			then().
			statusCode(200).
			body("entities", contains(SECONDARY_ID, OTHER_SECONDARY_ID));

		put("secondaryids", SECONDARY_ID, "  ", SECONDARY_ID).
			then().
			statusCode(400).
			body("errorMessages.secondaryIdentifiers", notNullValue()).
			body("supplementalData.errorMap.secondaryIdentifiers.'1'.secondaryId", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("supplementalData.errorMap.secondaryIdentifiers.'2'.secondaryId", is(ValidationConstants.DUPLICATE_MESSAGE));

		get("secondaryids").
			then().
			statusCode(200).
			body("entities", contains(SECONDARY_ID, OTHER_SECONDARY_ID));

		RestAssured.given().
			contentType("application/json").
			body("null").
			when().
			put(endpoint("secondaryids")).
			then().
			statusCode(400).
			body("errorMessages.secondaryIdentifiers", is(ValidationConstants.REQUIRED_MESSAGE));

		put("secondaryids").
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	@Test
	@Order(3)
	public void replaceSynonyms() {
		Long synonymId = get("synonyms").then().statusCode(200).body("entities", hasSize(1)).extract().jsonPath().getLong("entities[0].id");

		ConstructSynonymSlotAnnotation kept = buildSynonym("6760 synonym");
		kept.setId(synonymId);
		put("synonyms", kept, buildSynonym("6760 synonym 2")).
			then().
			statusCode(200).
			body("entities", hasSize(2));

		put("synonyms", kept, new ConstructSynonymSlotAnnotation()).
			then().
			statusCode(400).
			body("supplementalData.errorMap.constructSynonyms.'1'.displayText", is(ValidationConstants.REQUIRED_MESSAGE));

		put("synonyms").
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	@Test
	@Order(4)
	public void replaceRelatedNotes() {
		put("relatednotes", createNote(constructNoteType, "6760 note", false, false, reference)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].references[0].curie", is(REFERENCE));

		put("relatednotes", createNote(nonConstructNoteType, "6760 other note", false, false, null)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.relatedNotes.'0'.noteType", is(ValidationConstants.INVALID_MESSAGE));

		get("relatednotes").
			then().
			statusCode(200).
			body("entities", hasSize(1));
	}

	@Test
	@Order(5)
	public void replaceReferences() {
		putReferences(reference, obsoleteReference).
			then().
			statusCode(400).
			body("supplementalData.errorMap.references.'1'.curie", is(ValidationConstants.OBSOLETE_MESSAGE));

		putReferences(reference, reference).
			then().
			statusCode(400).
			body("supplementalData.errorMap.references.'1'.curie", is(ValidationConstants.DUPLICATE_MESSAGE));

		putReferences().
			then().
			statusCode(200).
			body("entities", nullValue());

		get("references").
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	@Test
	@Order(6)
	public void replaceCassetteAssociations() {
		cassetteAssociationId = get("cassetteassociations").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].constructCassetteAssociationObject.primaryExternalId", is(CASSETTE)).
			extract().jsonPath().getLong("entities[0].id");

		put("cassetteassociations", buildCassetteAssociation(cassetteAssociationId), buildCassetteAssociation(null)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.constructCassetteAssociations.'1'.constructCassetteAssociationObject", is(ValidationConstants.DUPLICATE_MESSAGE));

		put("cassetteassociations", buildCassetteAssociation(-1L)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.constructCassetteAssociations.'0'.id", is(ValidationConstants.INVALID_MESSAGE));

		put("cassetteassociations").
			then().
			statusCode(200).
			body("entities", nullValue());

		put("cassetteassociations", buildCassetteAssociation(null)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].relation.name", is(cassetteRelation.getName()));
	}

	private String endpoint(String path) {
		return "/api/construct/" + constructId + "/" + path;
	}

	private Response get(String path) {
		return RestAssured.given().
			when().
			get(endpoint(path));
	}

	private Response put(String path, Object... rows) {
		return RestAssured.given().
			contentType("application/json").
			body(rows).
			when().
			put(endpoint(path));
	}

	private Response putReferences(Reference... references) {
		// A typed array, so each reference carries the type its polymorphic deserialization reads.
		return RestAssured.given().
			contentType("application/json").
			body(references).
			when().
			put(endpoint("references"));
	}

	private ConstructSynonymSlotAnnotation buildSynonym(String text) {
		ConstructSynonymSlotAnnotation synonym = new ConstructSynonymSlotAnnotation();
		synonym.setDisplayText(text);
		synonym.setFormatText(text);
		synonym.setNameType(systematicNameType);
		return synonym;
	}

	private ConstructCassetteAssociation buildCassetteAssociation(Long id) {
		ConstructCassetteAssociation association = new ConstructCassetteAssociation();
		association.setId(id);
		association.setConstructCassetteAssociationObject(cassette);
		association.setRelation(cassetteRelation);
		return association;
	}

	private Cassette createCassette(Organization dataProvider) {
		CassetteSymbolSlotAnnotation symbol = new CassetteSymbolSlotAnnotation();
		symbol.setDisplayText("6760 cassette");
		symbol.setFormatText("6760 cassette");
		symbol.setNameType(symbolNameType);

		Cassette newCassette = new Cassette();
		newCassette.setPrimaryExternalId(CASSETTE);
		newCassette.setCassetteSymbol(symbol);
		newCassette.setDataProvider(dataProvider);

		RestAssured.given().
			contentType("application/json").
			body(newCassette).
			when().
			post("/api/cassette").
			then().
			statusCode(200);

		return getCassette(CASSETTE);
	}
}
