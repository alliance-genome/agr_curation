package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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
	
	private String dataProvider = "WB";
	private String dataProvider2 = "RGD";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}
	
	private final String agmBulkPostEndpoint = "/api/agm/bulk/WB/agms";
	private final String agmGetEndpoint = "/api/agm/";
	private final String agmTestFilePath = "src/test/resources/bulk/03_agm/";

	@Test
	@Order(1)
	public void agmBulkUploadCheckFields() throws Exception {
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "AF_01_all_fields.json");
		
		RestAssured.given().
			when().
			get(agmGetEndpoint + "AGMTEST:Agm0001").
			then().
			statusCode(200).
			body("entity.curie", is("AGMTEST:Agm0001")).
			body("entity.name", is("TestAgm1")).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.subtype.name", is("fish")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("AGMTEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("AGMTEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(2)
	public void agmBulkUploadUpdateCheckFields() throws Exception {
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "UD_01_update_all_except_default_fields.json");
		
		RestAssured.given().
			when().
			get(agmGetEndpoint + "AGMTEST:Agm0001").
			then().
			statusCode(200).
			body("entity.curie", is("AGMTEST:Agm0001")).
			body("entity.name", is("TestAgm1a")).
			body("entity.taxon.curie", is("NCBITaxon:9606")).
			body("entity.subtype.name", is("genotype")).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("AGMTEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("AGMTEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage2"));
	}
	
	@Test
	@Order(3)
	public void agmBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MR_01_no_curie.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MR_02_no_taxon.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MR_03_no_subtype.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MR_04_no_data_provider.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MR_05_no_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MR_06_no_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MR_07_no_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MR_08_no_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MR_09_no_data_provider_cross_reference_page_area.json");
	}
	
	@Test
	@Order(4)
	public void agmBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "ER_01_empty_curie.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "ER_02_empty_taxon.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "ER_03_empty_subtype.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "ER_04_empty_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "ER_05_empty_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "ER_06_empty_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "ER_07_empty_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "ER_08_empty_data_provider_cross_reference_page_area.json");
	}
		
	@Test
	@Order(5)
	public void agmBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "IV_01_invalid_date_created.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "IV_02_invalid_date_updated.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "IV_03_invalid_taxon.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "IV_04_invalid_subtype.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "IV_05_invalid_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "IV_06_invalid_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "IV_07_invalid_data_provider_cross_reference_page_area.json");
	}
	
	@Test
	@Order(6)
	public void agmBulkUploadUnsupportedFields() throws Exception {
		checkFailedBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "US_01_unsupported_taxon.json");
	}

	@Test
	@Order(7)
	public void agmBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "UM_01_update_no_non_required_fields.json");
		
		RestAssured.given().
			when().
			get(agmGetEndpoint + "AGMTEST:Agm0001").
			then().
			statusCode(200).
			body("entity.curie", is("AGMTEST:Agm0001")).
			body("entity", not(hasKey("name"))).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated")));
	}

	@Test
	@Order(8)
	public void agmBulkUploadUpdateEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
			when().
			get(agmGetEndpoint + "AGMTEST:Agm0001").
			then().
			statusCode(200).
			body("entity.curie", is("AGMTEST:Agm0001")).
			body("entity", not(hasKey("name"))).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(9)
	public void agmBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "MN_01_no_non_required_fields.json");
	}

	@Test
	@Order(10)
	public void agmBulkUploadEmptyNonRequiredFieldsLevel() throws Exception {
		checkSuccessfulBulkLoad(agmBulkPostEndpoint, agmTestFilePath + "EN_01_empty_non_required_fields.json");
	}
}
