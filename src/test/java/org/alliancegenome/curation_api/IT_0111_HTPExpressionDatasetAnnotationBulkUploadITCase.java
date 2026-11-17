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

/**
 * SCRUM-6714: direct LinkML DQM submission for HTPExpressionDatasetAnnotation, alongside the
 * existing FMS path exercised by IT_0610_HTPExpressionDatasetAnnotationFmsITCase. Test files are
 * derived from that FMS suite's scenarios, translated to the new direct-submission DTO shape.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("111 - HTPExpressionDatasetAnnotation bulk upload - direct submission")
@Order(111)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0111_HTPExpressionDatasetAnnotationBulkUploadITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String htpDatasetBulkPostEndpoint = "/api/htpexpressiondatasetannotation/bulk/FB/htpexpressiondatasetannotations";
	private final String htpDatasetTestFilePath = "src/test/resources/bulk/11_htp_dataset/";
	private final String htpDatasetFindEndpoint = "/api/htpexpressiondatasetannotation/find?limit=100&page=0";

	private void loadRequiredEntities() throws Exception {
		createFbcvTerm("FBcv:HTPTEST0000001", "test scRNA-seq analysis type", false);
	}

	@Test
	@Order(1)
	public void htpDatasetBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "AF_01_all_fields.json");

		RestAssured.given().when().header("Content-Type", "application/json").body("{}").post(htpDatasetFindEndpoint)
			.then().statusCode(200).body("returnedRecords", is(1)).body("results", hasSize(1))
			.body("results[0].htpExpressionDataset.curie", is("HTPDATASETTEST:HtpDataset0001"))
			.body("results[0].htpExpressionDataset.secondaryIdentifiers", hasSize(1))
			.body("results[0].htpExpressionDataset.secondaryIdentifiers[0]", is("GEO:GSE00001"))
			.body("results[0].htpExpressionDataset.preferredCrossReference.referencedCurie", is("TEST:0001"))
			.body("results[0].htpExpressionDataset.crossReferences", hasSize(1))
			.body("results[0].name", is("TEST HTP DATASET TITLE"))
			.body("results[0].references", hasSize(1))
			.body("results[0].references[0].crossReferences", hasSize(1))
			.body("results[0].relatedNote.freeText", is("Test HTP dataset note"))
			.body("results[0].relatedNote.noteType.name", is("htp_expression_dataset_summary"))
			.body("results[0].numberOfChannels", is(2))
			.body("results[0].subSeries", hasSize(1))
			.body("results[0].subSeries[0].curie", is("HTPDATASETTEST:HtpDataset0001"))
			.body("results[0].categoryTags[0].name", is("age"))
			.body("results[0].dataProvider.abbreviation", is("FB"))
			.body("results[0].dataProviderCrossReference.referencedCurie", is("TEST:0001"))
			.body("results[0].symbol", is("htp-test-symbol"))
			.body("results[0].analysis.analysisType.curie", is("FBcv:HTPTEST0000001"))
			.body("results[0].analysis.dataProvider.abbreviation", is("FB"))
			.body("results[0].analysis.dataProviderCrossReference.referencedCurie", is("TEST:0001"))
			.body("results[0].analysis.relatedNotes", hasSize(1))
			.body("results[0].analysis.relatedNotes[0].freeText", is("Test analysis note"))
			.body("results[0].internal", is(true)).body("results[0].obsolete", is(true));
	}

	@Test
	@Order(2)
	public void htpDatasetBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "MR_01_no_htp_expression_dataset_dto.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "MR_02_no_name.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "MR_03_no_category_tags.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "MR_04_no_data_provider.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "MR_05_no_htp_expression_dataset_curie.json");
	}

	@Test
	@Order(3)
	public void htpDatasetBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "ER_01_empty_htp_expression_dataset_curie.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "ER_02_empty_name.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "ER_03_empty_category_tags.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "ER_04_empty_data_provider.json");
	}

	@Test
	@Order(4)
	public void htpDatasetBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "IV_01_invalid_number_of_channels.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "IV_02_invalid_category_tag.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "IV_03_invalid_sub_series.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "IV_04_invalid_data_provider.json");
		checkFailedBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "IV_05_invalid_analysis_type_curie.json");
	}

	@Test
	@Order(5)
	public void htpDatasetBulkUploadUpdateNotDuplicateAndMissingNonRequiredFields() throws Exception {
		// Same htp_expression_dataset_dto.curie as AF_01 - must update that same record (matched
		// by dataset id, same as the FMS path), not create a second one.
		checkSuccessfulBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "UM_01_update_no_non_required_fields.json");

		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(htpDatasetFindEndpoint).
			then().
			statusCode(200).
			body("returnedRecords", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("references"))).
			body("results[0]", not(hasKey("subSeries"))).
			body("results[0]", not(hasKey("numberOfChannels"))).
			body("results[0]", not(hasKey("relatedNote"))).
			body("results[0]", not(hasKey("symbol"))).
			body("results[0]", not(hasKey("analysis"))).
			body("results[0]", not(hasKey("dataProviderCrossReference")));
	}

	@Test
	@Order(6)
	public void htpDatasetBulkUploadUpdateEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(htpDatasetBulkPostEndpoint, htpDatasetTestFilePath + "UE_01_update_empty_non_required_fields.json");

		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(htpDatasetFindEndpoint).
			then().
			statusCode(200).
			body("returnedRecords", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("references"))).
			body("results[0]", not(hasKey("subSeries"))).
			body("results[0]", not(hasKey("symbol")));
	}
}
