package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.alliancegenome.curation_api.base.BaseITCase;
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


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("03 - AGM bulk upload")
@Order(3)
public class AgmBulkUploadITCase extends BaseITCase {
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}
	
	private final String agmBulkPostEndpoint = "/api/agm/bulk/agms";
	private final String agmFindEndpoint = "/api/agm/find?limit=10&page=0";
	private final String agmTestFilePath = "src/test/resources/bulk/03_agm/";

	@Test
	@Order(1)
	public void agmBulkUploadCheckFields() throws Exception {
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "01_all_fields_agm.json");
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(agmFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0001")).
			body("results[0].name", is("TestAgm1")).
			body("results[0].taxon.curie", is("NCBITaxon:6239")).
			body("results[0].internal", is(true)).
			body("results[0].obsolete", is(true)).
			body("results[0].createdBy.uniqueId", is("AGMTEST:Person0001")).
			body("results[0].updatedBy.uniqueId", is("AGMTEST:Person0002")).
			body("results[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].dateUpdated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString()));
	}
	
	@Test
	@Order(2)
	public void agmBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "02_no_curie_agm.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "04_no_taxon_agm.json");
	}
	
	@Test
	@Order(3)
	public void agmBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "03_no_name_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "05_no_internal_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "06_no_created_by_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "07_no_updated_by_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "08_no_date_created_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "09_no_date_updated_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "13_no_obsolete_agm.json");
	}
	
	@Test
	@Order(4)
	public void agmBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "18_empty_curie_agm.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "19_empty_taxon_agm.json");
	}
	
	@Test
	@Order(5)
	public void agmBulkUploadEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "20_empty_name_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "15_empty_created_by_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "17_empty_updated_by_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "16_empty_date_created_agm.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "14_empty_date_updated_agm.json");
	}
		
	@Test
	@Order(6)
	public void agmBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "10_invalid_taxon_agm.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "11_invalid_date_created_agm.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "12_invalid_date_updated_agm.json");
	}

	@Test
	@Order(7)
	public void agmBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		String originalFilePath = agmTestFilePath + "01_all_fields_agm.json";
		
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(agmBulkPostEndpoint, agmFindEndpoint,
				originalFilePath, agmTestFilePath + "21_update_no_name_agm.json", "name");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(agmBulkPostEndpoint, agmFindEndpoint,
				originalFilePath, agmTestFilePath + "22_update_no_created_by_agm.json", "createdBy");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(agmBulkPostEndpoint, agmFindEndpoint,
				originalFilePath, agmTestFilePath + "23_update_no_updated_by_agm.json", "updatedBy");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(agmBulkPostEndpoint, agmFindEndpoint,
				originalFilePath, agmTestFilePath + "24_update_no_date_created_agm.json", "dateCreated");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(agmBulkPostEndpoint, agmFindEndpoint,
				originalFilePath, agmTestFilePath + "25_update_no_date_updated_agm.json", "dateUpdated");
	}
}
