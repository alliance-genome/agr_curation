package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
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
@Order(12)
public class VocabularyTermITCase extends BaseITCase {
	
	private Vocabulary testVocabulary;	
	private Vocabulary testVocabulary2;
	private Vocabulary testObsoleteVocabulary;
	
	private void createRequiredObjects() {
		testVocabulary = createVocabulary("VocabularyTerm test vocabulary", false);
		testVocabulary2 = createVocabulary("VocabularyTerm test vocabulary 2", false);
		testObsoleteVocabulary = createVocabulary("Obsolete VocabularyTerm test vocabulary", true);
	}

	@Test
	@Order(1)
	public void createVocabularyTerm() {
		createRequiredObjects();
		
		VocabularyTerm vocabularyTerm = new VocabularyTerm();
		vocabularyTerm.setName("Test vocabulary term");
		vocabularyTerm.setObsolete(true);
		vocabularyTerm.setVocabulary(testVocabulary);
		vocabularyTerm.setInternal(false);
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTerm).
				when().
				post("/api/vocabularyterm").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/vocabularyterm/findBy?termName=" + vocabularyTerm.getName() + "&vocabularyName=" + testVocabulary.getName()).
				then().
				statusCode(200).
				body("entity.name", is("Test vocabulary term")).
				body("entity.obsolete", is(true)).
				body("entity.internal", is(false)).
				body("entity.vocabulary.name", is("VocabularyTerm test vocabulary")).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(2)
	public void editVocabularyTerm() {
		
		VocabularyTerm editedTerm = getVocabularyTerm(testVocabulary, "Test vocabulary term");
		
		List<String> synonyms = new ArrayList<String>();
		synonyms.add("VocabularyTerm synonym");
		
		editedTerm.setName("Edited test vocabulary term");
		editedTerm.setObsolete(false);
		editedTerm.setAbbreviation("ABRV");
		editedTerm.setDefinition("Test definition");
		editedTerm.setSynonyms(synonyms);
		editedTerm.setInternal(true);
		editedTerm.setVocabulary(testVocabulary2);
		
		RestAssured.given().
				contentType("application/json").
				body(editedTerm).
				when().
				put("/api/vocabularyterm").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/vocabularyterm/findBy?termName=" + editedTerm.getName() + "&vocabularyName=" + testVocabulary2.getName()).
				then().
				statusCode(200).
				body("entity.name", is("Edited test vocabulary term")).
				body("entity.obsolete", is(false)).
				body("entity.internal", is(true)).
				body("entity.abbreviation", is("ABRV")).
				body("entity.definition", is("Test definition")).
				body("entity.synonyms[0]", is("VocabularyTerm synonym")).
				body("entity.vocabulary.name", is("VocabularyTerm test vocabulary 2"));
	}

	@Test
	@Order(3)
	public void editWithObsoleteVocabulary() {
		
		VocabularyTerm editedTerm = getVocabularyTerm(testVocabulary2, "Edited test vocabulary term");
		
		editedTerm.setVocabulary(testObsoleteVocabulary);
		
		RestAssured.given().
				contentType("application/json").
				body(editedTerm).
				when().
				put("/api/vocabularyterm").
				then().
				statusCode(400);
		
	}
	

	@Test
	@Order(4)
	public void editWithMissingVocabulary() {
		
		VocabularyTerm editedTerm = getVocabularyTerm(testVocabulary2, "Edited test vocabulary term");

		editedTerm.setVocabulary(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedTerm).
				when().
				put("/api/vocabularyterm").
				then().
				statusCode(400);
		
	}
	

	@Test
	@Order(5)
	public void editWithInvalidVocabulary() {
		
		VocabularyTerm editedTerm = getVocabularyTerm(testVocabulary2, "Edited test vocabulary term");
		
		Vocabulary nonPersistedVocabulary = new Vocabulary();
		nonPersistedVocabulary.setName("Non-persisted vocabulary");
		nonPersistedVocabulary.setObsolete(false);
		editedTerm.setVocabulary(nonPersistedVocabulary);
		
		RestAssured.given().
				contentType("application/json").
				body(editedTerm).
				when().
				put("/api/vocabularyterm").
				then().
				statusCode(400);
	}
	
	@Test
	@Order(6)
	public void editWithMissingName() {
		
		VocabularyTerm editedTerm = getVocabularyTerm(testVocabulary2, "Edited test vocabulary term");

		editedTerm.setName(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedTerm).
				when().
				put("/api/vocabularyterm").
				then().
				statusCode(400);
	}
}
