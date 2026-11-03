package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
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
@DisplayName("0318 - AlleleRelatedNoteSubResourceITCase")
@Order(318)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0318_AlleleRelatedNoteSubResourceITCase extends BaseITCase {

	private static final String ALLELE = "Allele:6687Note0001";
	private static final String OTHER_ALLELE = "Allele:6687Note0002";
	private static final String NOTE_REFERENCE = "AGRKB:6687000011";
	private static final String NOTE_REFERENCE_XREF = "PMID:6687000011";
	private static final String FIRST_TEXT = "6687 note 1";
	private static final String SECOND_TEXT = "6687 note 2";

	private Long alleleId;
	private Long otherAlleleId;
	private VocabularyTerm commentType;
	private VocabularyTerm userSubmittedType;
	private VocabularyTerm deprecationType;
	private VocabularyTerm nonAlleleType;
	private Reference noteReference;
	private Long firstId;
	private Long secondId;

	private void loadRequiredEntities() {
		VocabularyTerm symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		commentType = getVocabularyTerm(noteTypeVocabulary, "comment");
		userSubmittedType = getVocabularyTerm(noteTypeVocabulary, "user_submitted_note");
		deprecationType = getVocabularyTerm(noteTypeVocabulary, VocabularyConstants.DEPRECATION_REASON_TERM);
		// A note type term outside the allele note types set.
		nonAlleleType = createVocabularyTerm(noteTypeVocabulary, "6687_non_allele_note_type", false);
		noteReference = createReference(NOTE_REFERENCE, NOTE_REFERENCE_XREF, false);
		alleleId = createAllele(ALLELE, "NCBITaxon:6239", symbolNameType, false).getId();
		otherAlleleId = createAllele(OTHER_ALLELE, "NCBITaxon:6239", symbolNameType, false).getId();
	}

	@Test
	@Order(1)
	public void replaceCreatesNotes() {
		loadRequiredEntities();

		// createAllele leaves the allele with none, and NON_EMPTY drops the key rather than sending [].
		RestAssured.given().
			when().
			get(endpoint(alleleId)).
			then().
			statusCode(200).
			body("entities", nullValue());

		// The PUT response is serialized after the write transaction closes, so it is asserted directly.
		Response response = put(alleleId, createNote(commentType, FIRST_TEXT, false, false, noteReference), buildNote(null, userSubmittedType, SECOND_TEXT)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body("entities.freeText", containsInAnyOrder(FIRST_TEXT, SECOND_TEXT)).
			extract().response();

		assertReferencedNote(response);
		firstId = idOf(response, FIRST_TEXT);
		secondId = idOf(response, SECOND_TEXT);

		Response getResponse = RestAssured.given().
			when().
			get(endpoint(alleleId)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			extract().response();
		assertReferencedNote(getResponse);
	}

	@Test
	@Order(2)
	public void replaceUpdatesNoteInPlace() {
		put(alleleId, buildNote(firstId, userSubmittedType, FIRST_TEXT), buildNote(secondId, userSubmittedType, SECOND_TEXT)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body(rowPath(FIRST_TEXT) + ".id", is(firstId.intValue())).
			body(rowPath(FIRST_TEXT) + ".noteType.name", is(userSubmittedType.getName())).
			body(rowPath(FIRST_TEXT) + ".references", nullValue()).
			body(rowPath(SECOND_TEXT) + ".id", is(secondId.intValue()));
	}

	@Test
	@Order(3)
	public void replaceDeletesOmittedNote() {
		put(alleleId, buildNote(firstId, userSubmittedType, FIRST_TEXT)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(firstId.intValue()));

		assertHoldsOnly(alleleId, firstId);
	}

	@Test
	@Order(4)
	public void missingEntryAndFieldsAreRejectedByIndex() {
		put(alleleId, buildNote(firstId, userSubmittedType, FIRST_TEXT), new Note(), null).
			then().
			statusCode(400).
			body("errorMessages.relatedNotes", notNullValue()).
			body("supplementalData.errorMap.relatedNotes.'1'.noteType", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("supplementalData.errorMap.relatedNotes.'1'.freeText", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("supplementalData.errorMap.relatedNotes.'2'.freeText", is(ValidationConstants.REQUIRED_MESSAGE));

		assertHoldsOnly(alleleId, firstId);
	}

	@Test
	@Order(5)
	public void unknownIdIsRejectedByIndex() {
		put(alleleId, buildNote(firstId, userSubmittedType, FIRST_TEXT), buildNote(-1L, commentType, SECOND_TEXT)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.relatedNotes.'1'.id", is(ValidationConstants.INVALID_MESSAGE));

		assertHoldsOnly(alleleId, firstId);
	}

	@Test
	@Order(6)
	public void heldIdNamedTwiceIsRejected() {
		put(alleleId, buildNote(firstId, userSubmittedType, FIRST_TEXT), buildNote(firstId, commentType, SECOND_TEXT)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.relatedNotes.'1'.id", is(ValidationConstants.DUPLICATE_MESSAGE));

		assertHoldsOnly(alleleId, firstId);
	}

	@Test
	@Order(7)
	public void anotherAllelesNoteIsRejected() {
		Long otherNoteId = idOf(put(otherAlleleId, buildNote(null, commentType, SECOND_TEXT)).then().statusCode(200).extract().response(), SECOND_TEXT);

		put(alleleId, buildNote(firstId, userSubmittedType, FIRST_TEXT), buildNote(otherNoteId, commentType, SECOND_TEXT)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.relatedNotes.'1'.id", is(ValidationConstants.INVALID_MESSAGE));

		assertHoldsOnly(alleleId, firstId);
		assertHoldsOnly(otherAlleleId, otherNoteId);
	}

	@Test
	@Order(8)
	public void noteTypeOutsideTheAlleleSetIsRejected() {
		put(alleleId, buildNote(firstId, userSubmittedType, FIRST_TEXT), buildNote(null, nonAlleleType, SECOND_TEXT)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.relatedNotes.'1'.noteType", is(ValidationConstants.INVALID_MESSAGE));

		assertHoldsOnly(alleleId, firstId);
	}

	@Test
	@Order(9)
	public void duplicateNoteIsRejected() {
		put(alleleId, buildNote(firstId, userSubmittedType, FIRST_TEXT), buildNote(null, commentType, SECOND_TEXT), buildNote(null, commentType, SECOND_TEXT)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.relatedNotes.'2'.freeText", startsWith(ValidationConstants.DUPLICATE_MESSAGE + " ("));

		assertHoldsOnly(alleleId, firstId);
	}

	@Test
	@Order(10)
	public void deprecationReasonNoteIsAccepted() {
		put(alleleId, buildNote(firstId, userSubmittedType, FIRST_TEXT), buildNote(null, deprecationType, SECOND_TEXT)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body(rowPath(SECOND_TEXT) + ".noteType.name", is(VocabularyConstants.DEPRECATION_REASON_TERM));
	}

	@Test
	@Order(11)
	public void emptyListClearsNotes() {
		put(alleleId).
			then().
			statusCode(200).
			body("entities", nullValue());

		RestAssured.given().
			when().
			get(endpoint(alleleId)).
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	@Test
	@Order(12)
	public void missingBodyIsRejected() {
		RestAssured.given().
			contentType("application/json").
			body("null").
			when().
			put(endpoint(alleleId)).
			then().
			statusCode(400).
			body("errorMessages.relatedNotes", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(13)
	public void unknownAlleleIsRejected() {
		RestAssured.given().
			when().
			get(endpoint(-1L)).
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));

		put(-1L, buildNote(null, commentType, FIRST_TEXT)).
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));
	}

	private String endpoint(Long id) {
		return "/api/allele/" + id + "/relatednotes";
	}

	private Response put(Long id, Note... notes) {
		return RestAssured.given().
			contentType("application/json").
			body(notes).
			when().
			put(endpoint(id));
	}

	private Note buildNote(Long id, VocabularyTerm noteType, String text) {
		Note note = createNote(noteType, text, false, false, null);
		note.setId(id);
		return note;
	}

	private String rowPath(String text) {
		return "entities.find { it.freeText == '" + text + "' }";
	}

	private Long idOf(Response response, String text) {
		return response.jsonPath().getLong(rowPath(text) + ".id");
	}

	private void assertReferencedNote(Response response) {
		String row = rowPath(FIRST_TEXT);
		response.then().
			body(row + ".id", notNullValue()).
			body(row + ".noteType.name", is(commentType.getName())).
			body(row + ".references[0].curie", is(NOTE_REFERENCE)).
			body(row + ".references[0].crossReferences[0].referencedCurie", is(NOTE_REFERENCE_XREF)).
			body(rowPath(SECOND_TEXT) + ".references", nullValue());
	}

	private void assertHoldsOnly(Long id, Long noteId) {
		RestAssured.given().
			when().
			get(endpoint(id)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(noteId.intValue()));
	}
}
