package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
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
@Order(13)
public class VocabularyTermSetITCase extends BaseITCase {
	
	private Vocabulary testVocabulary1;
	private Vocabulary testVocabulary2;
	private Vocabulary testObsoleteVocabulary;
	private VocabularyTerm testVocabularyTerm1;
	private VocabularyTerm testVocabularyTerm2;
	private VocabularyTerm testVocabularyTerm3;
	private VocabularyTerm testObsoleteVocabularyTerm;
	private String testVocabularyTermSetName1 = "VTS0001";
	private String testVocabularyTermSetName2 = "VTS0002";
	private String testVocabularyTermSetName3 = "VTS0003";
	
	private void createRequiredObjects() {
		testVocabulary1 = createVocabulary("VocabularyTermSet test vocabulary", false);
		testVocabulary2 = createVocabulary("VocabularyTermSet test vocabulary 2", false);
		testObsoleteVocabulary = createVocabulary("Obsolete VocabularyTermSet test vocabulary", true);
		testVocabularyTerm1 = createVocabularyTerm(testVocabulary1, "VocabularyTermSet_test_vocabulary_term_1", false);
		testVocabularyTerm2 = createVocabularyTerm(testVocabulary1, "VocabularyTermSet_test_vocabulary_term_2", false);
		testVocabularyTerm3 = createVocabularyTerm(testVocabulary2, "VocabularyTermSet_test_vocabulary_term_3", false);
		testObsoleteVocabularyTerm = createVocabularyTerm(testVocabulary1, "VocabularyTermSet_test_vocabulary_term_3", true);
		
	}

	@Test
	@Order(1)
	public void createVocabularyTermSet() {
		createRequiredObjects();
		
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(testVocabularyTermSetName1);
		vocabularyTermSet.setVocabularyLabel(testVocabularyTermSetName1);
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
				contentType("application/json").
				body("{\"name\": \"" + testVocabularyTermSetName1 + "\" }").
				when().
				post("/api/vocabularytermset/find").
				then().
				statusCode(200).
				body("results[0].name", is(testVocabularyTermSetName1)).
				body("results[0].obsolete", is(true)).
				body("results[0].internal", is(false)).
				body("results[0].vocabularyTermSetVocabulary.name", is(testVocabulary1.getName())).
				body("results[0].vocabularyTermSetDescription", is("Creation test VocabularyTermSet")).
				body("results[0].memberTerms", hasSize(1)).
				body("results[0].memberTerms[0].name", is(testVocabularyTerm1.getName())).
				body("results[0].createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("results[0].updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
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
		contentType("application/json").
				body("{\"name\": \"" + testVocabularyTermSetName2 + "\" }").
				when().
				post("/api/vocabularytermset/find").
				then().
				statusCode(200).
				body("results[0].name", is(testVocabularyTermSetName2)).
				body("results[0].obsolete", is(false)).
				body("results[0].internal", is(true)).
				body("results[0].vocabularyTermSetVocabulary.name", is(testVocabulary2.getName())).
				body("results[0].vocabularyTermSetDescription", is("Update test VocabularyTermSet")).
				body("results[0].memberTerms", hasSize(1)).
				body("results[0].memberTerms[0].name", is(testVocabularyTerm3.getName())).
				body("results[0].createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("results[0].updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}
	
	@Test
	@Order(3)
	public void createMissingNameVocabularyTermSet() {
		
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(null);
		vocabularyTermSet.setVocabularyLabel(testVocabularyTermSetName3);
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
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName1);
		
		editedVocabularyTermSet.setName(null);
		editedVocabularyTermSet.setVocabularyLabel(testVocabularyTermSetName1);
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
		vocabularyTermSet.setVocabularyLabel(testVocabularyTermSetName3);
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
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName1);
		
		editedVocabularyTermSet.setName("");
		editedVocabularyTermSet.setVocabularyLabel(testVocabularyTermSetName1);
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
		vocabularyTermSet.setName(testVocabularyTermSetName3);
		vocabularyTermSet.setVocabularyLabel(testVocabularyTermSetName3);
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
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName1);
		
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
		vocabularyTermSet.setName(testVocabularyTermSetName3);
		vocabularyTermSet.setVocabularyLabel(testVocabularyTermSetName3);
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
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName1);
		
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
		vocabularyTermSet.setName(testVocabularyTermSetName3);
		vocabularyTermSet.setVocabularyLabel(testVocabularyTermSetName3);
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
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName1);
		
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
		vocabularyTermSet.setName(testVocabularyTermSetName3);
		vocabularyTermSet.setVocabularyLabel(testVocabularyTermSetName3);
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
		
		VocabularyTermSet editedVocabularyTermSet = getVocabularyTermSet(testVocabularyTermSetName1);
		
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

}
