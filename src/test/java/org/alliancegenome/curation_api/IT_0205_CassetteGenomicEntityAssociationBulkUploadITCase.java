package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Gene;
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
 * SCRUM-6535: bulk load coverage for Cassette Genomic Entity Associations.
 * Mirrors IT_0203_ConstructGenomicEntityAssociationBulkUploadITCase.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("205 - Cassette Genomic Entity Associations bulk upload")
@Order(205)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0205_CassetteGenomicEntityAssociationBulkUploadITCase extends BaseITCase {

	private String subjectId = "WB:Cassette0001";
	private String relationName = "is_regulated_by";
	private String objectId = "GENETEST:Gene0001";
	private String reference = "AGRKB:000000001";
	private String noteType = "test_cassette_association_note";
	private Cassette cassette;
	private Gene gene;

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String bulkPostEndpoint = "/api/cassettegenomicentityassociation/bulk/WB/associationFile";
	private final String findByEndpoint = "/api/cassettegenomicentityassociation/findBy";
	private final String testFilePath = "src/test/resources/bulk/CA03_cassette_genomic_entity_association/";

	private void loadRequiredEntities() throws Exception {
		cassette = getCassette(subjectId);
		gene = getGene(objectId);
		// component_type_curies is optional, but every curie supplied must resolve.
		createSoTerm("SO:0000001", "test_component_type", false);
	}

	@Test
	@Order(1)
	public void associationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(bulkPostEndpoint, testFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			get(findByEndpoint + "?cassetteId=" + cassette.getId() + "&relationName=" + relationName + "&genomicEntityId=" + gene.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.cassetteAssociationSubject.primaryExternalId", is(subjectId)).
			body("entity.cassetteGenomicEntityAssociationObject.primaryExternalId", is(objectId)).
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
			body("entity.relatedNotes[0].noteType.name", is(noteType))
			.body("entity.componentTypes", hasSize(1)).
			body("entity.componentTypes[0].curie", is("SO:0000001"));
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

	@Test
	@Order(4)
	public void associationBulkUploadInvalidComponentType() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_04_invalid_component_type.json");
	}
}
