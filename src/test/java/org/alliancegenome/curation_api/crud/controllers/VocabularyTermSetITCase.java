package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
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
@Order(13)
public class VocabularyTermSetITCase {
	
	private Vocabulary testVocabulary1;	
	private Vocabulary testVocabulary2;
	private Vocabulary testObsoleteVocabulary;
	private VocabularyTerm testVocabularyTerm1;
	private VocabularyTerm testVocabularyTerm2;
	private VocabularyTerm testVocabularyTerm3;
	private VocabularyTerm testObsoleteVocabularyTerm;
	private String testVocabularyTermSetName1 = "VTS0001";
	private String testVocabularyTermSetName2 = "VTS0002";
	
	private void createRequiredObjects() {
		testVocabulary1 = createVocabulary("VocabularyTermSet test vocabulary", false);
		testVocabulary2 = createVocabulary("VocabularyTermSet test vocabulary 2", false);
		testObsoleteVocabulary = createVocabulary("Obsolete VocabularyTermSet test vocabulary", true);
		testVocabularyTerm1 = createVocabularyTerm("VocabularyTermSet_test_vocabulary_term_1", testVocabulary1, false);
		testVocabularyTerm2 = createVocabularyTerm("VocabularyTermSet_test_vocabulary_term_2", testVocabulary1, false);
		testVocabularyTerm3 = createVocabularyTerm("VocabularyTermSet_test_vocabulary_term_3", testVocabulary2, false);
		testObsoleteVocabularyTerm = createVocabularyTerm("VocabularyTermSet_test_vocabulary_term_3", testVocabulary1, true);
		
	}

	@Test
	@Order(1)
	public void createVocabularyTermSet() {
		createRequiredObjects();
		
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(testVocabularyTermSetName1);
		vocabularyTermSet.setObsolete(true);
		vocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary1);
		vocabularyTermSet.setInternal(false);
		vocabularyTermSet.setVocabularyTermSetDescription("Creation test VocabularyTermSet");
		vocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm1));
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/vocabularytermset/findBy/" + testVocabularyTermSetName1).
				then().
				statusCode(200).
				body("entity.name", is(testVocabularyTermSetName1)).
				body("entity.obsolete", is(true)).
				body("entity.internal", is(false)).
				body("entity.vocabularyTermSetVocabulary.name", is(testVocabulary1.getName())).
				body("entity.vocabularyTermSetDescription", is("Creation test VocabularyTermSet")).
				body("entity.memberTerms", hasSize(1)).
				body("entity.memberTerms[0].name", is(testVocabularyTerm1.getName())).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(2)
	public void editVocabularyTermSet() {
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName1);
		
		editedVocabularyTermSet.setName(testVocabularyTermSetName2);
		editedVocabularyTermSet.setObsolete(false);
		editedVocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary2);
		editedVocabularyTermSet.setInternal(true);
		editedVocabularyTermSet.setVocabularyTermSetDescription("Update test VocabularyTermSet");
		editedVocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm3));
		
		RestAssured.given().
				contentType("application/json").
				body(editedVocabularyTermSet).
				when().
				put("/api/vocabularytermset").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/vocabularytermset/findBy/" + testVocabularyTermSetName2).
				then().
				statusCode(200).
				body("entity.name", is(testVocabularyTermSetName2)).
				body("entity.obsolete", is(false)).
				body("entity.internal", is(true)).
				body("entity.vocabularyTermSetVocabulary.name", is(testVocabulary2.getName())).
				body("entity.vocabularyTermSetDescription", is("Update test VocabularyTermSet")).
				body("entity.memberTerms", hasSize(1)).
				body("entity.memberTerms[0].name", is(testVocabularyTerm3.getName())).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}
	
	@Test
	@Order(3)
	public void createMissingNameVocabularyTermSet() {
		
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(null);
		vocabularyTermSet.setObsolete(true);
		vocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary1);
		vocabularyTermSet.setInternal(false);
		vocabularyTermSet.setVocabularyTermSetDescription("Creation test VocabularyTermSet");
		vocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm1));
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.name", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(4)
	public void editMissingNameVocabularyTermSet() {
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName2);
		
		editedVocabularyTermSet.setName(null);
		editedVocabularyTermSet.setObsolete(false);
		editedVocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary2);
		editedVocabularyTermSet.setInternal(true);
		editedVocabularyTermSet.setVocabularyTermSetDescription("Update test VocabularyTermSet");
		editedVocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm3));
		
		RestAssured.given().
				contentType("application/json").
				body(editedVocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.name", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(5)
	public void createEmptyNameVocabularyTermSet() {
		
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName("");
		vocabularyTermSet.setObsolete(true);
		vocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary1);
		vocabularyTermSet.setInternal(false);
		vocabularyTermSet.setVocabularyTermSetDescription("Creation test VocabularyTermSet");
		vocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm1));
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.name", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(6)
	public void editEmptyNameVocabularyTermSet() {
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName2);
		
		editedVocabularyTermSet.setName("");
		editedVocabularyTermSet.setObsolete(false);
		editedVocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary2);
		editedVocabularyTermSet.setInternal(true);
		editedVocabularyTermSet.setVocabularyTermSetDescription("Update test VocabularyTermSet");
		editedVocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm3));
		
		RestAssured.given().
				contentType("application/json").
				body(editedVocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.name", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(7)
	public void createObsoleteVocabularyVocabularyTermSet() {
		
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(testVocabularyTermSetName1);
		vocabularyTermSet.setObsolete(true);
		vocabularyTermSet.setVocabularyTermSetVocabulary(testObsoleteVocabulary);
		vocabularyTermSet.setInternal(false);
		vocabularyTermSet.setVocabularyTermSetDescription("Creation test VocabularyTermSet");
		vocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm1));
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.vocabularyTermSetVocabulary", is(ValidationConstants.OBSOLETE_MESSAGE));
	}

	@Test
	@Order(8)
	public void editObsoleteVocabularyVocabularyTermSet() {
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName2);
		
		editedVocabularyTermSet.setName(testVocabularyTermSetName1);
		editedVocabularyTermSet.setObsolete(false);
		editedVocabularyTermSet.setVocabularyTermSetVocabulary(testObsoleteVocabulary);
		editedVocabularyTermSet.setInternal(true);
		editedVocabularyTermSet.setVocabularyTermSetDescription("Update test VocabularyTermSet");
		editedVocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm1));
		
		RestAssured.given().
				contentType("application/json").
				body(editedVocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.vocabularyTermSetVocabulary", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void createMissingVocabularyVocabularyTermSet() {
		
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(testVocabularyTermSetName1);
		vocabularyTermSet.setObsolete(true);
		vocabularyTermSet.setVocabularyTermSetVocabulary(null);
		vocabularyTermSet.setInternal(false);
		vocabularyTermSet.setVocabularyTermSetDescription("Creation test VocabularyTermSet");
		vocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm1));
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.vocabularyTermSetVocabulary", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(10)
	public void editMissingVocabularyVocabularyTermSet() {
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName2);
		
		editedVocabularyTermSet.setName(testVocabularyTermSetName1);
		editedVocabularyTermSet.setObsolete(false);
		editedVocabularyTermSet.setVocabularyTermSetVocabulary(null);
		editedVocabularyTermSet.setInternal(true);
		editedVocabularyTermSet.setVocabularyTermSetDescription("Update test VocabularyTermSet");
		editedVocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm3));
		
		RestAssured.given().
				contentType("application/json").
				body(editedVocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.vocabularyTermSetVocabulary", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void createObsoleteMemberTermVocabularyTermSet() {
		
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(testVocabularyTermSetName1);
		vocabularyTermSet.setObsolete(true);
		vocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary1);
		vocabularyTermSet.setInternal(false);
		vocabularyTermSet.setVocabularyTermSetDescription("Creation test VocabularyTermSet");
		vocabularyTermSet.setMemberTerms(List.of(testObsoleteVocabularyTerm));
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.memberTerms", is(ValidationConstants.OBSOLETE_MESSAGE));
	}

	@Test
	@Order(12)
	public void editObsoleteMemberTermVocabularyTermSet() {
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName2);
		
		editedVocabularyTermSet.setName(testVocabularyTermSetName1);
		editedVocabularyTermSet.setObsolete(false);
		editedVocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary1);
		editedVocabularyTermSet.setInternal(true);
		editedVocabularyTermSet.setVocabularyTermSetDescription("Update test VocabularyTermSet");
		editedVocabularyTermSet.setMemberTerms(List.of(testObsoleteVocabularyTerm));
		
		RestAssured.given().
				contentType("application/json").
				body(editedVocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.memberTerms", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(13)
	public void createInvalidMemberTermVocabularyTermSet() {
		
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(testVocabularyTermSetName1);
		vocabularyTermSet.setObsolete(true);
		vocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary1);
		vocabularyTermSet.setInternal(false);
		vocabularyTermSet.setVocabularyTermSetDescription("Creation test VocabularyTermSet");
		vocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm3));
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.memberTerms", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(14)
	public void editInvalidMemberTermVocabularyTermSet() {
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName2);
		
		editedVocabularyTermSet.setName(testVocabularyTermSetName1);
		editedVocabularyTermSet.setObsolete(false);
		editedVocabularyTermSet.setVocabularyTermSetVocabulary(testVocabulary2);
		editedVocabularyTermSet.setInternal(true);
		editedVocabularyTermSet.setVocabularyTermSetDescription("Update test VocabularyTermSet");
		editedVocabularyTermSet.setMemberTerms(List.of(testVocabularyTerm2));
		
		RestAssured.given().
				contentType("application/json").
				body(editedVocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.memberTerms", is(ValidationConstants.INVALID_MESSAGE));
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
		
		return response.getEntity();
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
		
		return response.getEntity();
	}
	
	private VocabularyTermSet getVocabularyTermSet(String name) {
		
		ObjectResponse<VocabularyTermSet> response =
				RestAssured.given().
					when().
					get("/api/vocabularytermset/findBy/" + name).
					then().
					statusCode(200).
					extract().body().as(getObjectResponseTypeRefVocabularyTermSet());
		
		return response.getEntity();
	}

	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}
	
	private TypeRef<ObjectResponse<VocabularyTerm>> getObjectResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectResponse <VocabularyTerm>>() { };
	}
	
	private TypeRef<ObjectResponse<VocabularyTermSet>> getObjectResponseTypeRefVocabularyTermSet() {
		return new TypeRef<ObjectResponse <VocabularyTermSet>>() { };
	}
}
