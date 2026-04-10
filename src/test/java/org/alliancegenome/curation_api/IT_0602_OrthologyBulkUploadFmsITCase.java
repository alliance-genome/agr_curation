package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.zip.GZIPOutputStream;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
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
@DisplayName("602 - Orthology bulk upload - FMS")
@Order(602)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0602_OrthologyBulkUploadFmsITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 100000)
					.setParam("http.connection.timeout", 100000));
	}

	private final String orthologyBulkPostEndpoint = "/api/orthologygenerated/bulk/WB/orthologyfile";
	private final String orthologyTestFilePath = "src/test/resources/bulk/fms/02_orthology/";
	private final String orthologyFindEndpoint = "/api/orthologygenerated/find?limit=100&page=0";
	
	@Test
	@Order(1)
	public void orthologyBulkUploadCheckFields() throws Exception {
		
		checkSuccessfulBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "AF_01_all_fields.json");

		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post(orthologyFindEndpoint).
				then().
				statusCode(200).
				body("returnedRecords", is(1)).
				body("results", hasSize(1)).
				body("results[0].subjectGene.primaryExternalId", is("GENETEST:Gene0001")).
				body("results[0].objectGene.primaryExternalId", is("HGNC:0001")).
				body("results[0].subjectGene.taxon.curie", is("NCBITaxon:6239")).
				body("results[0].objectGene.taxon.curie", is("NCBITaxon:9606")).
				body("results[0].moderateFilter", is(false)).
				body("results[0].strictFilter", is(true)).
				body("results[0].predictionMethodsMatched", hasSize(1)).
				body("results[0].predictionMethodsMatched[0].name", is("Ensembl Compara")).
				body("results[0].predictionMethodsNotMatched", hasSize(8)).
				body("results[0].predictionMethodsNotCalled", hasSize(3)).
				body("results[0].isBestScore.name", is("Yes")).
				body("results[0].isBestScoreReverse.name", is("No")).
				body("results[0].confidence.name", is("low"));
	}

	@Test
	@Order(2)
	public void orthologyBulkUploadMissingRequiredFields() throws Exception {
		
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_01_no_gene1.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_02_no_gene2.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_03_no_gene1Species.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_04_no_gene2Species.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_05_no_isBestScore.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_06_no_isBestRevScore.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "MR_07_no_confidence.json");
		
	}

	@Test
	@Order(3)
	public void orthologyBulkUploadEmptyRequiredFields() throws Exception {

		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_01_empty_gene1.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_02_empty_gene2.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_03_empty_isBestScore.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_04_empty_isBestRevScore.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "ER_05_empty_confidence.json");
		
	}

	@Test
	@Order(4)
	public void orthologyBulkUploadInvalidFields() throws Exception {

		checkSkippedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_01_invalid_gene1.json");
		checkSkippedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_02_invalid_gene2.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_03_invalid_gene1Species.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_04_invalid_gene2Species.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_05_invalid_isBestScore.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_06_invalid_isBestRevScore.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_07_invalid_confidence.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_08_invalid_predictionMethodsMatched.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_09_invalid_predictionMethodsNotMatched.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_10_invalid_predictionMethodsNotCalled.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_11_invalid_gene1Species_for_gene1.json");
		checkFailedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "IV_12_invalid_gene2Species_for_gene2.json");
		
	}

	@Test
	@Order(5)
	public void orthologyBulkUploadExcludedKnownIssuePair() throws Exception {
		// KANBAN-965: this fixture represents the known bad VMA21/pdcd-2 DIOPT pair that is
		// temporarily excluded in OrthologyFmsDTOValidator.EXCLUDED_ORTHOLOGY_PAIRS.
		// Remove this test together with that exclusion, the KI_01 fixture below, and the cleanup
		// test if/when the upstream DIOPT data is fixed and regenerated cleanly.
		checkSkippedBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "KI_01_excluded_vma21_pdcd2_pair.json");
	}

	@Test
	@Order(6)
	public void orthologyBulkExecutorCleanupDeletesExcludedKnownIssuePair() throws Exception {
		// KANBAN-965: this covers the failure mode that originally escaped the loader-side fix:
		// a bad row can already exist in agr_curation even though new ingest now skips it.
		// Keep this aligned with OrthologyFmsDTOValidator.EXCLUDED_ORTHOLOGY_PAIRS and the
		// KI_01_excluded_vma21_pdcd2_pair.json fixture, and remove all three together once the
		// upstream DIOPT source data is corrected. This still needs the DQM manual submission
		// path because the raw JSON bulk endpoint does not invoke executor cleanup, but it avoids
		// asserting on bulk-load history internals and waits on the deleted orthology row instead.
		Organization mgiDataProvider = getOrganization("MGI");
		Organization wbDataProvider = getOrganization("WB");
		VocabularyTerm symbolTerm = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");

		Gene subjectGene = createGene("MGI:1914298", "NCBITaxon:10090", symbolTerm, false, mgiDataProvider);
		Gene objectGene = createGene("WB:WBGene00011116", "NCBITaxon:6239", symbolTerm, false, wbDataProvider);

		GeneToGeneOrthologyGenerated staleOrthology = new GeneToGeneOrthologyGenerated();
		staleOrthology.setSubjectGene(subjectGene);
		staleOrthology.setObjectGene(objectGene);
		staleOrthology.setIsBestScore(getVocabularyTerm(getVocabulary(VocabularyConstants.ORTHOLOGY_BEST_SCORE_VOCABULARY), "Yes"));
		staleOrthology.setIsBestScoreReverse(getVocabularyTerm(getVocabularyTermSet(VocabularyConstants.ORTHOLOGY_BEST_REVERSE_SCORE_VOCABULARY_TERM_SET).getVocabularyTermSetVocabulary(), "Yes"));
		staleOrthology.setConfidence(getVocabularyTerm(getVocabulary(VocabularyConstants.HOMOLOGY_CONFIDENCE_VOCABULARY), "high"));
		staleOrthology.setStrictFilter(true);
		staleOrthology.setModerateFilter(true);
		staleOrthology.setObsolete(false);
		staleOrthology.setInternal(false);

		Long staleOrthologyId = RestAssured.given().
			contentType("application/json").
			body(staleOrthology).
			when().
			post("/api/orthologygenerated").
			then().
			statusCode(200).
			extract().jsonPath().getLong("entity.id");

		assertNotNull(staleOrthologyId);
		assertEquals(staleOrthologyId, RestAssured.given().
			when().
			get("/api/orthologygenerated/" + staleOrthologyId).
			then().
			statusCode(200).
			extract().jsonPath().getLong("entity.id"));

		// KANBAN-965: reuse the same temporary KI_01 fixture here so the cleanup-path coverage
		// stays tied to the exact excluded pair list used by the validator. The payload gets a
		// unique dateProduced value so the DQM submission is not deduplicated by gzip MD5.
		Path gzipFile = createUniqueGzipFromResource(orthologyTestFilePath + "KI_01_excluded_vma21_pdcd2_pair.json");

		RestAssured.given().
			multiPart("ORTHOLOGY_MGI", gzipFile.toFile(), "application/gzip").
			when().
			post("/api/data/submit?cleanUp=true").
			then().
			statusCode(200).
			body(is("OK"));

		waitForOrthologyDeletion(staleOrthologyId);
	}

	@Test
	@Order(7)
	public void orthologyBulkUploadUpdateMissingNonRequiredFields() throws Exception {

		checkSuccessfulBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "UM_01_update_no_non_required_fields.json");
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(orthologyFindEndpoint).
			then().
			statusCode(200).
			body("returnedRecords", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("predictionMethodsMatched"))).
			body("results[0]", not(hasKey("predictionMethodsNotMatched"))).
			body("results[0]", not(hasKey("predictionMethodsNotCalled"))).
			body("results[0]", not(hasKey("strictFilter"))).
			body("results[0]", not(hasKey("moderateFilter")));
	}

	@Test
	@Order(8)
	public void orthologyBulkUploadUpdateEmptyNonRequiredFields() throws Exception {

		checkSuccessfulBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(orthologyFindEndpoint).
			then().
			statusCode(200).
			body("returnedRecords", is(1)).
			body("results", hasSize(1)).
			body("results[0]", hasKey("predictionMethodsMatched")).
			body("results[0]", hasKey("predictionMethodsNotMatched")).
			body("results[0]", hasKey("predictionMethodsNotCalled")).
			body("results[0]", hasKey("strictFilter")).
			body("results[0]", hasKey("moderateFilter"));
		
		checkSuccessfulBulkLoad(orthologyBulkPostEndpoint, orthologyTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(orthologyFindEndpoint).
			then().
			statusCode(200).
			body("returnedRecords", is(1)).
			body("results", hasSize(1)).
			body("results[0]", not(hasKey("predictionMethodsMatched"))).
			body("results[0]", not(hasKey("predictionMethodsNotMatched"))).
			body("results[0]", not(hasKey("predictionMethodsNotCalled")));
	}

	private Path createUniqueGzipFromResource(String resourcePath) throws Exception {
		String content = Files.readString(Path.of(resourcePath));
		String uniqueContent = content.replaceFirst(
			"\"dateProduced\"\\s*:\\s*\"[^\"]+\"",
			"\"dateProduced\": \"" + Instant.now() + "\""
		);
		if (uniqueContent.equals(content)) {
			fail("Failed to update dateProduced in " + resourcePath);
		}

		Path gzipFile = Files.createTempFile("kanban-965-orthology-", ".json.gz");
		gzipFile.toFile().deleteOnExit();
		try (GZIPOutputStream outputStream = new GZIPOutputStream(Files.newOutputStream(gzipFile))) {
			outputStream.write(uniqueContent.getBytes(StandardCharsets.UTF_8));
		}
		return gzipFile;
	}

	private void waitForOrthologyDeletion(Long orthologyId) throws Exception {
		long timeoutAt = System.currentTimeMillis() + 30000;
		while (System.currentTimeMillis() < timeoutAt) {
			Object entity = RestAssured.given().
				when().
				get("/api/orthologygenerated/" + orthologyId).
				then().
				statusCode(200).
				extract().path("entity");

			if (entity == null) {
				return;
			}

			Thread.sleep(500);
		}

		fail("Timed out waiting for cleanup to delete orthology " + orthologyId);
	}

}
