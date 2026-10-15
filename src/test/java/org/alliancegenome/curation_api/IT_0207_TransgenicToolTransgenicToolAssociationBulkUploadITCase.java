package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
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
 * SCRUM-6543: bulk load coverage for Transgenic Tool Transgenic Tool Associations.
 * Mirrors IT_0206_CassetteTransgenicToolAssociationBulkUploadITCase. Both tools come from IT_0109,
 * which leaves WB:TransgenicTool0001 and WB:TransgenicToolMN01 as WB transgenic tools.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("207 - Transgenic Tool Transgenic Tool Associations bulk upload")
@Order(207)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0207_TransgenicToolTransgenicToolAssociationBulkUploadITCase extends BaseITCase {

	private String subjectId = "WB:TransgenicTool0001";
	private String relationName = "is_compatible_with";
	private String objectId = "WB:TransgenicToolMN01";
	private TransgenicTool subject;
	private TransgenicTool object;

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String bulkPostEndpoint = "/api/transgenictooltransgenictoolassociation/bulk/WB/associationFile";
	private final String findByEndpoint = "/api/transgenictooltransgenictoolassociation/findBy";
	private final String transgenicToolGetEndpoint = "/api/transgenic-tool/";
	private final String testFilePath = "src/test/resources/bulk/CA06_transgenic_tool_transgenic_tool_association/";

	private void loadRequiredEntities() throws Exception {
		subject = getTransgenicTool(subjectId);
		object = getTransgenicTool(objectId);
	}

	@Test
	@Order(1)
	public void associationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(bulkPostEndpoint, testFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			get(findByEndpoint + "?transgenicToolSubjectId=" + subject.getId() + "&relationName=" + relationName + "&transgenicToolObjectId=" + object.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.transgenicToolAssociationSubject.primaryExternalId", is(subjectId)).
			body("entity.transgenicToolTransgenicToolAssociationObject.primaryExternalId", is(objectId)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString()));

		// The subject tool lists the association; the object tool reaches it only from the other side.
		RestAssured.given().
			when().
			get(transgenicToolGetEndpoint + subjectId).
			then().
			statusCode(200).
			body("entity.transgenicToolTransgenicToolAssociations", hasSize(1)).
			body("entity.transgenicToolTransgenicToolAssociations[0].relation.name", is(relationName)).
			body("entity.transgenicToolTransgenicToolAssociations[0].transgenicToolTransgenicToolAssociationObject.primaryExternalId", is(objectId));
	}

	@Test
	@Order(2)
	public void associationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_02_empty_relation.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_03_empty_object.json");
	}

	/** IV_02 sends compatible_with, the spelling in FB's 2026_03 file, which the term set does not hold. */
	@Test
	@Order(3)
	public void associationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_01_invalid_subject.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_02_invalid_relation.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_03_invalid_object.json");
	}
}
