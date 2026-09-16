package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.OffsetDateTime;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;

/**
 * SCRUM-6535: bulk load coverage for Construct Cassette Associations.
 * Mirrors IT_0203_ConstructGenomicEntityAssociationBulkUploadITCase.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("204 - Construct Cassette Associations bulk upload")
@Order(204)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0204_ConstructCassetteAssociationBulkUploadITCase extends BaseITCase {

	private String subjectId = "WB:Construct0001";
	private String relationName = "has_component";
	private String objectId = "WB:Cassette0001";
	private String reference = "AGRKB:000000001";
	private String noteType = "test_construct_cassette_association_note";
	private Construct construct;
	private Cassette cassette;

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String bulkPostEndpoint = "/api/constructcassetteassociation/bulk/WB/associationFile";
	private final String findByEndpoint = "/api/constructcassetteassociation/findBy";
	private final String testFilePath = "src/test/resources/bulk/CA02_construct_cassette_association/";

	private void loadRequiredEntities() throws Exception {
		construct = getConstruct(subjectId);
		cassette = getCassette(objectId);

		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		addVocabularyTermToSet(VocabularyConstants.CONSTRUCT_CASSETTE_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET, noteType, noteTypeVocabulary, false);

		// LinkML gives this relation as has_part (BFO:0000051), but the new_construct_relation
		// vocabulary already on alpha holds has_component and three siblings instead. The test
		// follows alpha; see the open question on the ticket.
		Vocabulary relationVocabulary = createVocabulary(VocabularyConstants.NEW_CONSTRUCT_RELATION_VOCABULARY, false);
		VocabularyTerm hasComponent = createVocabularyTerm(relationVocabulary, relationName, false);
		createVocabularyTermSet(VocabularyConstants.CONSTRUCT_CASSETTE_RELATION_VOCABULARY_TERM_SET, relationVocabulary, List.of(hasComponent));
	}

	@Test
	@Order(1)
	public void associationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(bulkPostEndpoint, testFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			get(findByEndpoint + "?constructId=" + construct.getId() + "&relationName=" + relationName + "&cassetteId=" + cassette.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.constructAssociationSubject.primaryExternalId", is(subjectId)).
			body("entity.constructCassetteAssociationObject.primaryExternalId", is(objectId)).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].freeText", is("Test note")).
			body("entity.relatedNotes[0].noteType.name", is(noteType));
	}

	@Test
	@Order(2)
	public void associationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_02_empty_relation.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_03_empty_object.json");
	}

	@Test
	@Order(3)
	public void associationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_01_invalid_subject.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_02_invalid_relation.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_03_invalid_object.json");
	}
}
