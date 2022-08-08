package org.alliancegenome.curation_api.crud.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectListResponse;
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
@Order(11)
public class ConditionRelationITCase {

	private Vocabulary conditionRelationTypeVocabulary;
	private VocabularyTerm conditionRelationType;
	private ExperimentalCondition experimentalCondition;
	private ConditionRelation conditionRelationNoHandle;
	private ConditionRelation conditionRelationHandle1;
	private ConditionRelation conditionRelationHandle2;
	private Reference testReference;

	private void createRequiredObjects() {

		testReference = createReference("PMID:11213376");
		conditionRelationTypeVocabulary = createVocabulary("Condition relation type vocabulary");
		conditionRelationType = createVocabularyTerm(conditionRelationTypeVocabulary, "relation_type", false);
		experimentalCondition = createExperimentalCondition();
		conditionRelationNoHandle = createConditionRelation(null, null, conditionRelationType, List.of(experimentalCondition));
		conditionRelationHandle1 = createConditionRelation("fructose", testReference, conditionRelationType, List.of(experimentalCondition));
		conditionRelationHandle2 = createConditionRelation("vasilin", testReference, conditionRelationType, List.of(experimentalCondition));

	}

	@Test
	@Order(1)
	public void createConditionRelationRecords() {
		createRequiredObjects();

		given().
			when().
			get("/api/condition-relation/" + conditionRelationNoHandle.getId()).
			then().
			statusCode(200).
			body("entity.conditionRelationType.name", is("relation_type"));
		
		given().
			when().
			get("/api/condition-relation/" + conditionRelationHandle1.getId()).
			then().
			statusCode(200).
			body("entity.handle", is("fructose")).
			body("entity.conditionRelationType.name", is("relation_type")).
			body("entity.singleReference.curie", is(testReference.getCurie()));

		given().
			when().
			get("/api/condition-relation/" + conditionRelationHandle2.getId()).
			then().
			statusCode(200).
			body("entity.handle", is("vasilin")).
			body("entity.conditionRelationType.name", is("relation_type")).
			body("entity.singleReference.curie", is(testReference.getCurie()));
	}

	@Test
	@Order(2)
	public void updateConditionRelation() {
		ConditionRelation cr = getConditionRelation(conditionRelationHandle2.getId());
		
		// change handle
		cr.setHandle("butanol");
		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(cr).
			when().
			put("/api/condition-relation").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefConditionRelation());

		ConditionRelation conditionRel = response.getEntity();
		assertThat(conditionRel.getHandle(), equalTo("butanol"));
	}

	@Test
	@Order(3)
	public void updateConditionRelationNullHandle() {
		ConditionRelation cr = getConditionRelation(conditionRelationHandle2.getId());
		
		// change handle to empty
		cr.setHandle("");
		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(cr).
			when().
			put("/api/condition-relation").
			then().
			// error: cannot update a non-empty handle to an empty one
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not update ConditionRelation"));
		assertEquals(response.getErrorMessages().get("handle"), ValidationConstants.REQUIRED_MESSAGE);
	}

	@Test
	@Order(4)
	public void updateConditionRelationSameHandleRef() {
		ConditionRelation cr = getConditionRelation(conditionRelationHandle2.getId());
		
		// change handle to empty
		cr.setHandle("fructose");
		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(cr).
			when().
			put("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not update ConditionRelation"));
		assertEquals(response.getErrorMessages().get("handle"), "Handle / Pub combination already exists");
	}
	
	@Test
	@Order(5)
	public void updateConditionRelationWithHandleWithoutReference() {
		ConditionRelation cr = getConditionRelation(conditionRelationHandle2.getId());
		
		cr.setSingleReference(null);
		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(cr).
			when().
			put("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not update ConditionRelation"));
		assertEquals(response.getErrorMessages().get("handle"), "Invalid without value for singleReference");
	}
	
	@Test
	@Order(6)
	public void createConditionRelationWithHandleWithoutReference() {
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle("handle");
		conditionRelation.setConditionRelationType(conditionRelationType);
		conditionRelation.setConditions(List.of(experimentalCondition));

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation).
			when().
			post("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not create ConditionRelation"));
		assertEquals(response.getErrorMessages().get("handle"), "Invalid without value for singleReference");
	}

	@Test
	@Order(7)
	public void createConditionRelationWithNoRelationType() {
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle("no_relation_type");
		conditionRelation.setSingleReference(testReference);
		conditionRelation.setConditions(List.of(experimentalCondition));

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation).
			when().
			post("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not create ConditionRelation"));
		assertEquals(response.getErrorMessages().get("conditionRelationType"), ValidationConstants.REQUIRED_MESSAGE);
	}

	@Test
	@Order(8)
	public void createConditionRelationWithNoConditions() {
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle("no_conditions");
		conditionRelation.setSingleReference(testReference);
		conditionRelation.setConditionRelationType(conditionRelationType);

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation).
			when().
			post("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not create ConditionRelation"));
		assertEquals(response.getErrorMessages().get("conditions"), ValidationConstants.REQUIRED_MESSAGE);
	}

	@Test
	@Order(9)
	public void updateConditionRelationWithNoRelationType() {
		ConditionRelation cr = getConditionRelation(conditionRelationHandle2.getId());
		
		cr.setHandle("no_relation_type");
		cr.setConditionRelationType(null);

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(cr).
			when().
			put("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not update ConditionRelation"));
		assertEquals(response.getErrorMessages().get("conditionRelationType"), ValidationConstants.REQUIRED_MESSAGE);
	}

	@Test
	@Order(10)
	public void updateConditionRelationWithNoConditions() {
		ConditionRelation cr = getConditionRelation(conditionRelationHandle2.getId());
		
		cr.setHandle("no_conditions");
		cr.setConditions(null);

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(cr).
			when().
			put("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not update ConditionRelation"));
		assertEquals(response.getErrorMessages().get("conditions"), ValidationConstants.REQUIRED_MESSAGE);
	}

	@Test
	@Order(11)
	public void createConditionRelationWithInvalidRelationType() {
		VocabularyTerm nonPersistedRelationType = new VocabularyTerm();
		nonPersistedRelationType.setName("Invalid term");
		
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle("invalid_relation_type");
		conditionRelation.setSingleReference(testReference);
		conditionRelation.setConditions(List.of(experimentalCondition));
		conditionRelation.setConditionRelationType(nonPersistedRelationType);

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation).
			when().
			post("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not create ConditionRelation"));
		assertEquals(response.getErrorMessages().get("conditionRelationType"), ValidationConstants.INVALID_MESSAGE);
	}

	@Test
	@Order(12)
	public void createConditionRelationWithInvalidCondition() {
		ExperimentalCondition nonPersistedCondition = new ExperimentalCondition();
		nonPersistedCondition.setConditionClass(getZecoTerm("ZECO:da00001"));
		nonPersistedCondition.setConditionStatement("Statement2");
		nonPersistedCondition.setUniqueId("Statement2");
		
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle("invalid_condition");
		conditionRelation.setSingleReference(testReference);
		conditionRelation.setConditionRelationType(conditionRelationType);
		conditionRelation.setConditions(List.of(nonPersistedCondition));

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation).
			when().
			post("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not create ConditionRelation"));
		assertEquals(response.getErrorMessages().get("conditions"), ValidationConstants.INVALID_MESSAGE);
	}

	@Test
	@Order(13)
	public void updateConditionRelationWithInvalidRelationType() {
		ConditionRelation cr = getConditionRelation(conditionRelationHandle2.getId());
		
		VocabularyTerm nonPersistedRelationType = new VocabularyTerm();
		nonPersistedRelationType.setName("Invalid term");
		cr.setHandle("invalid_relation_type");
		cr.setConditionRelationType(nonPersistedRelationType);
		
		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(cr).
			when().
			put("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not update ConditionRelation"));
		assertEquals(response.getErrorMessages().get("conditionRelationType"), ValidationConstants.INVALID_MESSAGE);
	}

	@Test
	@Order(14)
	public void updateConditionRelationWithInvalidCondition() {
		ConditionRelation cr = getConditionRelation(conditionRelationHandle2.getId());
		
		ExperimentalCondition nonPersistedCondition = new ExperimentalCondition();
		nonPersistedCondition.setConditionClass(getZecoTerm("ZECO:da00001"));
		nonPersistedCondition.setConditionStatement("Statement2");
		nonPersistedCondition.setUniqueId("Statement2");
		
		cr.setHandle("invalid_conditions");
		cr.setConditions(List.of(nonPersistedCondition));

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(cr).
			when().
			put("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertTrue(response.getErrorMessage().startsWith("Could not update ConditionRelation"));
		assertEquals(response.getErrorMessages().get("conditions"), ValidationConstants.INVALID_MESSAGE);
	}
	
	@Test
	@Order(15)
	public void createNonUniqueConditionRelation() {
		ConditionRelation cr = new ConditionRelation();
		
		cr.setConditions(conditionRelationNoHandle.getConditions());
		cr.setConditionRelationType(conditionRelationNoHandle.getConditionRelationType());
		cr.setSingleReference(conditionRelationNoHandle.getSingleReference());

		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(cr).
			when().
			post("/api/condition-relation").
			then().
			// error: cannot update to an handle that already exists under the same reference.
				statusCode(400).
				extract().body().as(getObjectResponseTypeRefConditionRelation());

		assertNotNull(response);
		assertEquals(response.getErrorMessage(), "Could not create ConditionRelation");
		assertEquals(response.getErrorMessages().get("uniqueId"), ValidationConstants.NON_UNIQUE_MESSAGE);
	}
	
	private ConditionRelation createConditionRelation(String handle, Reference reference, VocabularyTerm relationType, List<ExperimentalCondition> conditions) {
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle(handle);
		conditionRelation.setSingleReference(reference);
		conditionRelation.setConditionRelationType(relationType);
		conditionRelation.setConditions(conditions);

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

	private ConditionRelation getConditionRelation(Long id) {
		ObjectResponse<ConditionRelation> response =
				given().
					when().
					get("/api/condition-relation/" + id).
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
		List<String> subsets = new ArrayList<String>();
		subsets.add(OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		zecoTerm.setSubsets(subsets);

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


}
