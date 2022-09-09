package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
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
@Order(3)
public class NoteITCase {
	
	private Vocabulary testVocabulary;
	private VocabularyTerm testVocabularyTerm;
	private VocabularyTerm testVocabularyTerm2;
	private VocabularyTerm testObsoleteVocabularyTerm;
	private List<Reference> testReferences = new ArrayList<Reference>();
	private List<Reference> testReferences2 = new ArrayList<Reference>();
	private List<Reference> testObsoleteReferences = new ArrayList<Reference>();
	private Long testNoteId;
	
	private void createRequiredObjects() {
		testVocabulary = createVocabulary("Note test vocabulary", false);
		testVocabularyTerm = createVocabularyTerm("Note test vocabulary term", testVocabulary, false);
		testVocabularyTerm2 = createVocabularyTerm("Note test vocabulary term 2", testVocabulary, false);
		testObsoleteVocabularyTerm = createVocabularyTerm("Obsolete Note test vocabularyTerm", testVocabulary, true);
		Reference testReference = createReference("PMID:14978092", false);
		testReferences.add(testReference);
		Reference testReference2 = createReference("PMID:14978093", false);
		testReferences2.add(testReference2);
		Reference testObsoleteReference = createReference("PMID:0", true);
		testObsoleteReferences.add(testObsoleteReference);
	}

	@Test
	@Order(1)
	public void createNote() {
		createRequiredObjects();
		
		Note note = new Note();
		note.setNoteType(testVocabularyTerm);
		note.setInternal(true);
		note.setObsolete(true);
		note.setReferences(testReferences);
		note.setFreeText("Test text");
		
		ObjectResponse<Note> response = RestAssured.given().
				contentType("application/json").
				body(note).
				when().
				post("/api/note").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefNote());
		
		testNoteId = response.getEntity().getId();
		
		RestAssured.given().
				when().
				get("/api/note/" + testNoteId).
				then().
				statusCode(200).
				body("entity.obsolete", is(true)).
				body("entity.internal", is(true)).
				body("entity.noteType.name", is("Note test vocabulary term")).
				body("entity.references[0].curie", is("PMID:14978092")).
				body("entity.freeText", is("Test text")).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(2)
	public void editVocabularyTerm() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/note/" + testNoteId).
				then().
				statusCode(200).
				body("entity.obsolete", is(false)).
				body("entity.internal", is(false)).
				body("entity.noteType.name", is("Note test vocabulary term 2")).
				body("entity.references[0].curie", is("PMID:14978093")).
				body("entity.freeText", is("Edited test text")).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(3)
	public void editWithMissingNoteType() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(null);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(4)
	public void editWithInvalidNoteType() {
		
		Note editedNote = getNote(testNoteId);
		
		VocabularyTerm nonPersistedVocabularyTerm = new VocabularyTerm();
		nonPersistedVocabularyTerm.setName("Non-persisted vocabulary term");
		nonPersistedVocabularyTerm.setVocabulary(testVocabulary);
		
		editedNote.setNoteType(nonPersistedVocabularyTerm);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(5)
	public void editWithObsoleteNoteType() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testObsoleteVocabularyTerm);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.OBSOLETE_MESSAGE));
	}

	@Test
	@Order(6)
	public void editWithMissingFreeText() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.freeText", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(7)
	public void editWithEmptyFreeText() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testReferences2);
		editedNote.setFreeText("");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.freeText", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(8)
	public void editWithInvalidReference() {
		
		Note editedNote = getNote(testNoteId);
		
		List<Reference> nonPersistedReferences = new ArrayList<Reference>();
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("Invalid");
		nonPersistedReferences.add(nonPersistedReference);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(nonPersistedReferences);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(9)
	public void editWithObsoleteReference() {
		
		Note editedNote = getNote(testNoteId);
		
		editedNote.setNoteType(testVocabularyTerm2);
		editedNote.setObsolete(false);
		editedNote.setInternal(false);
		editedNote.setReferences(testObsoleteReferences);
		editedNote.setFreeText("Edited test text");
		
		RestAssured.given().
				contentType("application/json").
				body(editedNote).
				when().
				put("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.references", is("curie - " + ValidationConstants.OBSOLETE_MESSAGE));
	}

	@Test
	@Order(10)
	public void createWithMissingNoteType() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(null);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(11)
	public void createWithInvalidNoteType() {
		
		Note newNote = new Note();
		
		VocabularyTerm nonPersistedVocabularyTerm = new VocabularyTerm();
		nonPersistedVocabularyTerm.setName("Non-persisted vocabulary term");
		nonPersistedVocabularyTerm.setVocabulary(testVocabulary);
		
		newNote.setNoteType(nonPersistedVocabularyTerm);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(12)
	public void createWithObsoleteNoteType() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(testObsoleteVocabularyTerm);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.noteType", is(ValidationConstants.OBSOLETE_MESSAGE));
	}

	@Test
	@Order(13)
	public void createWithMissingFreeText() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(testVocabularyTerm2);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText(null);
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.freeText", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(14)
	public void createWithEmptyFreeText() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(testVocabularyTerm2);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testReferences2);
		newNote.setFreeText("");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.freeText", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(15)
	public void createWithInvalidReference() {
		
		Note newNote = new Note();
		
		List<Reference> nonPersistedReferences = new ArrayList<Reference>();
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("Invalid");
		nonPersistedReferences.add(nonPersistedReference);
		
		newNote.setNoteType(testVocabularyTerm2);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(nonPersistedReferences);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(16)
	public void createWithObsoleteReference() {
		
		Note newNote = new Note();
		
		newNote.setNoteType(testVocabularyTerm2);
		newNote.setObsolete(false);
		newNote.setInternal(false);
		newNote.setReferences(testObsoleteReferences);
		newNote.setFreeText("New test text");
		
		RestAssured.given().
				contentType("application/json").
				body(newNote).
				when().
				post("/api/note").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.references", is("curie - " + ValidationConstants.OBSOLETE_MESSAGE));
	}


	
	private Vocabulary createVocabulary(String name, Boolean obsolete) {
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.setName(name);
		vocabulary.setObsolete(obsolete);

		ObjectResponse<Vocabulary> response = 
			RestAssured.given().
				contentType("application/json").
				body(vocabulary).
				when().
				post("/api/vocabulary").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabulary());
		
		vocabulary = response.getEntity();
		
		return vocabulary;
	}
	
	private VocabularyTerm createVocabularyTerm(String name, Vocabulary vocabulary, Boolean obsolete) {
		VocabularyTerm vocabularyTerm = new VocabularyTerm();
		vocabularyTerm.setName(name);
		vocabularyTerm.setObsolete(obsolete);
		vocabularyTerm.setVocabulary(vocabulary);

		ObjectResponse<VocabularyTerm> response = 
			RestAssured.given().
				contentType("application/json").
				body(vocabularyTerm).
				when().
				post("/api/vocabularyterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabularyTerm());
		
		vocabularyTerm = response.getEntity();
		
		return vocabularyTerm;
	}
	
	private Note getNote(Long noteId) {
		
		ObjectResponse<Note> response =
				RestAssured.given().
					when().
					get("/api/note/" + noteId).
					then().
					statusCode(200).
					extract().body().as(getObjectResponseTypeRefNote());
		
		Note note = response.getEntity();
		
		return note;
	}
	
	private Reference createReference(String curie, Boolean obsolete) {
		Reference reference = new Reference();
		reference.setCurie(curie);
		reference.setObsolete(obsolete);
		
		ObjectResponse<Reference> response = RestAssured.given().
			contentType("application/json").
			body(reference).
			when().
			post("/api/reference").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefReference());
			
		return response.getEntity();
	}

	private TypeRef<ObjectResponse<Note>> getObjectResponseTypeRefNote() {
		return new TypeRef<ObjectResponse <Note>>() { };
	}

	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}
	
	private TypeRef<ObjectResponse<VocabularyTerm>> getObjectResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectResponse <VocabularyTerm>>() { };
	}

	private TypeRef<ObjectResponse<Reference>> getObjectResponseTypeRefReference() {
		return new TypeRef<ObjectResponse <Reference>>() { };
	}
}
