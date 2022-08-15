package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

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
@Order(12)
public class VocabularyTermITCase {
	
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
				get("/api/vocabularyterm/" + vocabularyTerm.getName() + "/" + testVocabulary.getName()).
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
		
		VocabularyTerm editedTerm = getVocabularyTerm("Test vocabulary term", testVocabulary.getName());
		
		List<String> synonyms = new ArrayList<String>();
		synonyms.add("VocabularyTerm synonym");
		
		editedTerm.setName("Edited test vocabulary term");
		editedTerm.setObsolete(false);
		editedTerm.setAbbreviation("ABRV");
		editedTerm.setDefinition("Test definition");
		editedTerm.setTextSynonyms(synonyms);
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
				get("/api/vocabularyterm/" + editedTerm.getName() + "/" + testVocabulary2.getName()).
				then().
				statusCode(200).
				body("entity.name", is("Edited test vocabulary term")).
				body("entity.obsolete", is(false)).
				body("entity.internal", is(true)).
				body("entity.abbreviation", is("ABRV")).
				body("entity.definition", is("Test definition")).
				body("entity.textSynonyms[0]", is("VocabularyTerm synonym")).
				body("entity.vocabulary.name", is("VocabularyTerm test vocabulary 2"));
	}

	@Test
	@Order(3)
	public void editWithObsoleteVocabulary() {
		
		VocabularyTerm editedTerm = getVocabularyTerm("Edited test vocabulary term", testVocabulary2.getName());
		
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
		
		VocabularyTerm editedTerm = getVocabularyTerm("Edited test vocabulary term", testVocabulary2.getName());

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
		
		VocabularyTerm editedTerm = getVocabularyTerm("Edited test vocabulary term", testVocabulary2.getName());
		
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
		
		VocabularyTerm editedTerm = getVocabularyTerm("Edited test vocabulary term", testVocabulary2.getName());

		editedTerm.setName(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedTerm).
				when().
				put("/api/vocabularyterm").
				then().
				statusCode(400);
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
	
	private VocabularyTerm getVocabularyTerm(String termName, String vocabularyName) {
		
		ObjectResponse<VocabularyTerm> response =
				RestAssured.given().
					when().
					get("/api/vocabularyterm/" + termName + "/" + vocabularyName).
					then().
					statusCode(200).
					extract().body().as(getObjectResponseTypeRefVocabularyTerm());
		
		VocabularyTerm vocabularyTerm = response.getEntity();
		
		return vocabularyTerm;
	}

	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}
	
	private TypeRef<ObjectResponse<VocabularyTerm>> getObjectResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectResponse <VocabularyTerm>>() { };
	}
}
