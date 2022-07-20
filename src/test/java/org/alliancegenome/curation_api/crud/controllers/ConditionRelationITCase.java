package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(11)
public class ConditionRelationITCase {

	private Vocabulary conditionRelationTypeVocabulary;
	private VocabularyTerm conditionRelationType;
	private ExperimentalCondition experimentalCondition;
	private ConditionRelation conditionRelation;
	private ConditionRelation conditionRelation2;
	private Reference testReference;

	private void createRequiredObjects() {

		testReference = createReference("PMID:11213376");
		conditionRelationTypeVocabulary = createVocabulary("Condition relation type vocabulary");
		conditionRelationType = createVocabularyTerm(conditionRelationTypeVocabulary, "relation_type", false);
		experimentalCondition = createExperimentalCondition();
		conditionRelation = createConditionRelation("fructose", testReference, conditionRelationType);
		conditionRelation2 = createConditionRelation("vasilin", testReference, conditionRelationType);

	}

	@Test
	@Order(1)
	public void createConditionRelationRecords() {
		createRequiredObjects();

		given().
			when().
			get("/api/condition-relation/" + conditionRelation.getId()).
			then().
			statusCode(200).
			body("entity.handle", is("fructose")).
			body("entity.conditionRelationType.name", is("relation_type")).
			body("entity.singleReference.curie", is(testReference.getCurie()));

		given().
			when().
			get("/api/condition-relation/" + conditionRelation2.getId()).
			then().
			statusCode(200).
			body("entity.handle", is("vasilin")).
			body("entity.conditionRelationType.name", is("relation_type")).
			body("entity.singleReference.curie", is(testReference.getCurie()));
	}

	@Test
	@Order(2)
	public void updateConditionRelation() {

		// change handle
		conditionRelation2.setHandle("butane");
		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation2).
			when().
			put("/api/condition-relation").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefConditionRelation());

		ConditionRelation conditionRel = response.getEntity();
		assertThat(conditionRel.getHandle(), equalTo("butane"));
	}

	@Test
	@Order(3)
	public void updateConditionRelationNullHandle() {

		// change handle to empty
		conditionRelation2.setHandle("");
		ObjectResponse response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation2).
			when().
			put("/api/condition-relation").
			then().
			// error: cannot update a non-empty handle to an empty one
				statusCode(400).
				extract().body().as(getObjectResponseTypeRef());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not update ConditionRelation"));
		assertEquals(response.getErrorMessages().get("handle"), "Required field is empty");
	}

	@Test
	@Order(4)
	public void updateConditionRelationSameHandleRef() {

		// change handle to empty
		conditionRelation2.setHandle("fructose");
		ObjectResponse response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation2).
			when().
			put("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRef());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not update ConditionRelation"));
		assertEquals(response.getErrorMessages().get("handle"), "Handle / Pub combination already exists");
	}


	private ConditionRelation createConditionRelation(String handle, Reference reference, VocabularyTerm relationType) {
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle(handle);
		conditionRelation.setSingleReference(reference);
		conditionRelation.setConditionRelationType(relationType);
		conditionRelation.setConditions(List.of(experimentalCondition));

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation).
			when().
			post("/api/condition-relation").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefConditionRelation());


		return response.getEntity();
	}


	private Reference createReference(String curie) {
		Reference reference = new Reference();
		reference.setCurie(curie);

		ObjectResponse<Reference> response = given().
			contentType("application/json").
			body(reference).
			when().
			post("/api/reference").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefReference());

		return response.getEntity();
	}

	private VocabularyTerm createVocabularyTerm(Vocabulary vocabulary, String name, Boolean obsolete) {
		VocabularyTerm vocabularyTerm = new VocabularyTerm();
		vocabularyTerm.setName(name);
		vocabularyTerm.setVocabulary(vocabulary);
		vocabularyTerm.setObsolete(obsolete);
		vocabularyTerm.setInternal(false);

		ObjectResponse<VocabularyTerm> response =
			given().
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
	
	private Vocabulary createVocabulary(String name) {
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.setName(name);
		vocabulary.setInternal(false);
		
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

	private VocabularyTerm getVocabularyTerm(Vocabulary vocabulary, String name) {
		ObjectListResponse<VocabularyTerm> response =
			given().
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

	private ZecoTerm getZecoTerm(String curie) {
		ObjectResponse<ZecoTerm> response =
			given().
				when().
				get("/api/zecoterm/" + curie).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefZecoTerm());

		return response.getEntity();
	}

	private ExperimentalCondition createExperimentalCondition() {
		ExperimentalCondition condition = new ExperimentalCondition();
		condition.setConditionClass(createZecoTerm("ZECO:da00001"));
		condition.setConditionStatement("Statement1");
		condition.setUniqueId("Statement1");

		ObjectResponse<ExperimentalCondition> response = given().
			contentType("application/json").
			body(condition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefExperimentalCondition());

		return response.getEntity();
	}

	private ZecoTerm createZecoTerm(String curie) {
		ZecoTerm zecoTerm = new ZecoTerm();
		zecoTerm.setCurie(curie);
		zecoTerm.setName("Test");
		zecoTerm.setObsolete(false);

		given().
			contentType("application/json").
			body(zecoTerm).
			when().
			post("/api/zecoterm").
			then().
			statusCode(200);
		return zecoTerm;
	}

	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<>() {
		};
	}

	private TypeRef<ObjectResponse<VocabularyTerm>> getObjectResponseTypeRefVocabularyTerm() {
		return new TypeRef<>() {
		};
	}

	private TypeRef<ObjectListResponse<VocabularyTerm>> getObjectListResponseTypeRefVocabularyTerm() {
		return new TypeRef<>() {
		};
	}

	private TypeRef<ObjectResponse<ExperimentalCondition>> getObjectResponseTypeRefExperimentalCondition() {
		return new TypeRef<>() {
		};
	}

	private TypeRef<ObjectResponse<ConditionRelation>> getObjectResponseTypeRefConditionRelation() {
		return new TypeRef<>() {
		};
	}

	private TypeRef<ObjectResponse<Reference>> getObjectResponseTypeRefReference() {
		return new TypeRef<>() {
		};
	}

	private TypeRef<ObjectResponse<ZecoTerm>> getObjectResponseTypeRefZecoTerm() {
		return new TypeRef<>() {
		};
	}

	private TypeRef<ObjectResponse> getObjectResponseTypeRef() {
		return new TypeRef<>() {
		};
	}

}
