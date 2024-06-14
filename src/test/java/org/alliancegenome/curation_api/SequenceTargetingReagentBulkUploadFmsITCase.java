package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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
@DisplayName("602 - Sequence Targeting Reagent bulk upload - FMS")
@Order(602)
public class SequenceTargetingReagentBulkUploadFmsITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 100000)
					.setParam("http.connection.timeout", 100000));
	}

	private final String sqtrBulkPostEndpoint = "/api/sqtr/bulk/ZFIN/sqtrfile";
	private final String sqtrTestFilePath = "src/test/resources/bulk/fms/05_sqtr/";
	private final String sqtrFindEndpoint = "/api/sqtr/find?limit=100&page=0";
	
	@Test
	@Order(1)
	public void sqtrBulkUploadCheckFields() throws Exception {
		
		checkSuccessfulBulkLoad(sqtrBulkPostEndpoint, sqtrTestFilePath + "AF_01_all_fields.json");

		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post(sqtrFindEndpoint).
				then().
				statusCode(200).
				body("totalResults", is(1)).
				body("results", hasSize(1)).
				body("results[0].primaryId", is("ZFIN:ZDB-TALEN-180503-1")).
				body("results[0].name", is("TALEN-inhbaa")).
				body("results[0].soTermId", is("SO:0000059")).
				body("results[0].taxonId", is("NCBITaxon:7955")).
				body("results[0].synonyms", hasSize(1)).
				body("results[0].secondaryIds", hasSize(1));
	}

	// @Test
	// @Order(2)
	// public void orthologyBulkUploadMissingRequiredFields() throws Exception {
		
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_01_no_gene1.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_02_no_gene2.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_03_no_gene1Species.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_04_no_gene2Species.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_05_no_isBestScore.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_06_no_isBestRevScore.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_07_no_confidence.json");
		
	// }

	// @Test
	// @Order(3)
	// public void orthologyBulkUploadEmptyRequiredFields() throws Exception {

	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_01_empty_gene1.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_02_empty_gene2.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_03_empty_isBestScore.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_04_empty_isBestRevScore.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_05_empty_confidence.json");
		
	// }

	// @Test
	// @Order(4)
	// public void orthologyBulkUploadInvalidFields() throws Exception {

	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_01_invalid_gene1.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_02_invalid_gene2.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_03_invalid_gene1Species.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_04_invalid_gene2Species.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_05_invalid_isBestScore.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_06_invalid_isBestRevScore.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_07_invalid_confidence.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_08_invalid_predictionMethodsMatched.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_09_invalid_predictionMethodsNotMatched.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_10_invalid_predictionMethodsNotCalled.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_11_invalid_gene1Species_for_gene1.json");
	// 	checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_12_invalid_gene2Species_for_gene2.json");
		
	// }

	// @Test
	// @Order(5)
	// public void orthologyBulkUploadUpdateMissingNonRequiredFields() throws Exception {

	// 	checkSuccessfulBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "UM_01_update_no_non_required_fields.json");
		
	// 	RestAssured.given().
	// 		when().
	// 		header("Content-Type", "application/json").
	// 		body("{}").
	// 		post(orthologyFindEndpoint).
	// 		then().
	// 		statusCode(200).
	// 		body("totalResults", is(1)).
	// 		body("results", hasSize(1)).
	// 		body("results[0]", not(hasKey("predictionMethodsMatched"))).
	// 		body("results[0]", not(hasKey("predictionMethodsNotMatched"))).
	// 		body("results[0]", not(hasKey("predictionMethodsNotCalled"))).
	// 		body("results[0]", not(hasKey("strictFilter"))).
	// 		body("results[0]", not(hasKey("moderateFilter")));
	// }

	// @Test
	// @Order(6)
	// public void orthologyBulkUploadUpdateEmptyNonRequiredFields() throws Exception {

	// 	checkSuccessfulBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "AF_01_all_fields.json");

	// 	RestAssured.given().
	// 		when().
	// 		header("Content-Type", "application/json").
	// 		body("{}").
	// 		post(orthologyFindEndpoint).
	// 		then().
	// 		statusCode(200).
	// 		body("totalResults", is(1)).
	// 		body("results", hasSize(1)).
	// 		body("results[0]", hasKey("predictionMethodsMatched")).
	// 		body("results[0]", hasKey("predictionMethodsNotMatched")).
	// 		body("results[0]", hasKey("predictionMethodsNotCalled")).
	// 		body("results[0]", hasKey("strictFilter")).
	// 		body("results[0]", hasKey("moderateFilter"));
		
	// 	checkSuccessfulBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
	// 	RestAssured.given().
	// 		when().
	// 		header("Content-Type", "application/json").
	// 		body("{}").
	// 		post(orthologyFindEndpoint).
	// 		then().
	// 		statusCode(200).
	// 		body("totalResults", is(1)).
	// 		body("results", hasSize(1)).
	// 		body("results[0]", not(hasKey("predictionMethodsMatched"))).
	// 		body("results[0]", not(hasKey("predictionMethodsNotMatched"))).
	// 		body("results[0]", not(hasKey("predictionMethodsNotCalled")));
	// }

}
