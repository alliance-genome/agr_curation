package org.alliancegenome.curation_api;

import java.util.List;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
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
 * SCRUM-6715: direct LinkML DQM submission for HTPExpressionDatasetSampleAnnotation, alongside the
 * existing FMS path exercised by IT_0612_HTPExpressionDatasetSampleAnnotationFmsITCase. Mirrors
 * IT_0111_HTPExpressionDatasetAnnotationBulkUploadITCase's structure for the sibling entity.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("112 - HTPExpressionDatasetSampleAnnotation bulk upload - direct submission")
@Order(112)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0112_HTPExpressionDatasetSampleAnnotationBulkUploadITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String htpDatasetBulkPostEndpoint = "/api/htpexpressiondatasetannotation/bulk/FB/htpexpressiondatasetannotations";
	private final String htpDatasetSampleBulkPostEndpointFb = "/api/htpexpressiondatasetsampleannotation/bulk/FB/htpexpressiondatasetsampleannotations";
	private final String htpDatasetSampleBulkPostEndpointMgi = "/api/htpexpressiondatasetsampleannotation/bulk/MGI/htpexpressiondatasetsampleannotations";
	private final String testFilePath = "src/test/resources/bulk/12_htp_dataset_sample/";
	private final String htpDatasetSampleFindEndpoint = "/api/htpexpressiondatasetsampleannotation/find?limit=100&page=0";
	private final String htpDatasetFindEndpoint = "/api/htpexpressiondatasetannotation/find?limit=100&page=0";

	private void loadRequiredEntities() throws Exception {
		// Dependency for dataset_ids - a real HTPExpressionDatasetAnnotation must already exist, per
		// the stricter validation this direct path adds over the FMS path (see IV_01/IV_02 below).
		checkSuccessfulBulkLoad(htpDatasetBulkPostEndpoint, testFilePath + "dataset_dependency.json");

		createFbcvTerm("HTPSAMPLETEST:Fbcv0001", "test scRNA-seq analysis type", false);
		createObiTerm("HTPSAMPLETEST:Obi0001", "test sample type");
		createMmoTerm("HTPSAMPLETEST:Mmo0001", "test assay");

		// AnatomicalSite - one of every field the FMS fixture's own sampleLocations entry populates
		// (ANAT:001/002, GOTEST:0012, the UBERON:002-007 qualifiers/slim terms, FBCV:001), so this
		// direct path's AnatomicalSiteDTOValidator gets the same field-by-field coverage.
		createAnatomicalTerm("HTPSAMPLETEST:Anat0001", "test anatomical structure");
		createAnatomicalTerm("HTPSAMPLETEST:Anat0002", "test anatomical substructure");
		createGoTerm("HTPSAMPLETEST:Go0001", "test cellular component", false);
		createGoTerm("HTPSAMPLETEST:Go0002", "test cellular component ribbon term", false);
		createOntologyTerm("HTPSAMPLETEST:Ont0001", "test qualifier", false);
		createUberonTerm("HTPSAMPLETEST:Uberon0001", "test anatomical structure uberon term");
		createUberonTerm("HTPSAMPLETEST:Uberon0002", "test anatomical substructure uberon term");

		// TemporalContext (via BioSampleAge.stage) - developmental_stage_start/stop (StageTerm, like
		// the FMS fixture's ZFS:001 stageTermId) plus stage_uberon_slim_terms, which - unlike
		// AnatomicalSite's own UBERON fields - resolves against the "stage_uberon_slim_terms"
		// VocabularyTerm set, not real ontology terms; UBERON:0000113 is one of its migration-seeded
		// members (v0.36.0.7__expression_pattern.sql), so it's used as-is rather than creating a term.
		createStageTerm("HTPSAMPLETEST:Stage0001", "test developmental stage start");
		createStageTerm("HTPSAMPLETEST:Stage0002", "test developmental stage stop");

		// BioSampleGenomicInformation - the FMS fixture's AGA:Allele0001/"fish" pair actually resolves
		// through the AGM fallback (findByIdentifierString fails for Allele, succeeds for AGM), not
		// the Allele branch its name implies. AF_01 below exercises the Allele branch directly, and
		// AF_02 exercises this AGM branch - both need their own backing entity.
		createAffectedGenomicModel("HTPSAMPLETEST:Agm0001", "NCBITaxon:7227", "fish", false);

		ResourceDescriptor rd = createResourceDescriptor("HTPSAMPLETEST");
		createResourceDescriptorPage("homepage", "http://htpsampletest.org", rd);

		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		Organization dataProvider = getOrganization("FB");
		createAllele("HTPSAMPLETEST:Allele0001", "TestAllele", "NCBITaxon:7227", symbolTerm, false, dataProvider);
	}

	@Test
	@Order(1)
	public void htpDatasetSampleBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "AF_01_all_fields.json");

		RestAssured.given().when().header("Content-Type", "application/json").body("{}").post(htpDatasetSampleFindEndpoint)
			.then().statusCode(200).body("returnedRecords", is(1)).body("results", hasSize(1))
			.body("results[0].htpExpressionSample.curie", is("HTPSAMPLETEST:Sample0001"))
			.body("results[0].htpExpressionSample.secondaryIdentifiers", hasSize(1))
			.body("results[0].htpExpressionSampleTitle", is("TEST SAMPLE TITLE"))
			.body("results[0].htpExpressionSampleType.curie", is("HTPSAMPLETEST:Obi0001"))
			.body("results[0].expressionAssayUsed.curie", is("HTPSAMPLETEST:Mmo0001"))
			.body("results[0].htpExpressionSampleAge.age", is("P14D"))
			.body("results[0].htpExpressionSampleAge.whenExpressedStageName", is("test stage"))
			.body("results[0].htpExpressionSampleAge.stage.developmentalStageStart.curie", is("HTPSAMPLETEST:Stage0001"))
			.body("results[0].htpExpressionSampleAge.stage.developmentalStageStop.curie", is("HTPSAMPLETEST:Stage0002"))
			.body("results[0].htpExpressionSampleAge.stage.stageUberonSlimTerms[0].name", is("UBERON:0000113"))
			.body("results[0].htpExpressionSampleLocations", hasSize(1))
			.body("results[0].htpExpressionSampleLocations[0].anatomicalStructure.curie", is("HTPSAMPLETEST:Anat0001"))
			.body("results[0].htpExpressionSampleLocations[0].anatomicalSubstructure.curie", is("HTPSAMPLETEST:Anat0002"))
			.body("results[0].htpExpressionSampleLocations[0].cellularComponentTerm.curie", is("HTPSAMPLETEST:Go0001"))
			.body("results[0].htpExpressionSampleLocations[0].cellularComponentRibbonTerms[0].curie", is("HTPSAMPLETEST:Go0002"))
			.body("results[0].htpExpressionSampleLocations[0].anatomicalStructureQualifiers[0].curie", is("HTPSAMPLETEST:Ont0001"))
			.body("results[0].htpExpressionSampleLocations[0].anatomicalSubstructureQualifiers[0].curie", is("HTPSAMPLETEST:Ont0001"))
			.body("results[0].htpExpressionSampleLocations[0].cellularComponentQualifiers[0].curie", is("HTPSAMPLETEST:Ont0001"))
			.body("results[0].htpExpressionSampleLocations[0].anatomicalStructureUberonTerms[0].curie", is("HTPSAMPLETEST:Uberon0001"))
			.body("results[0].htpExpressionSampleLocations[0].anatomicalSubstructureUberonTerms[0].curie", is("HTPSAMPLETEST:Uberon0002"))
			.body("results[0].htpExpressionSampleLocations[0].anatomicalStructureUberonTermOther", is(true))
			.body("results[0].htpExpressionSampleLocations[0].anatomicalSubStructureUberonTermOther", is(true))
			.body("results[0].htpExpressionSampleLocations[0].cellularComponentOther", is(true))
			.body("results[0].abundance", is("test abundance"))
			.body("results[0].genomicInformation.bioSampleAllele.primaryExternalId", is("HTPSAMPLETEST:Allele0001"))
			.body("results[0].taxon.curie", is("NCBITaxon:7227"))
			.body("results[0].sequencingFormat.name", is("single"))
			.body("results[0].assemblyVersions", hasSize(1))
			.body("results[0].relatedNotes", hasSize(1))
			.body("results[0].relatedNotes[0].freeText", is("Test sample note"))
			.body("results[0].datasetIds", hasSize(1))
			.body("results[0].datasetIds[0].curie", is("HTPDATASETTEST:SampleTestDataset0001"))
			.body("results[0].microarraySampleDetails.channelId", is("TEST:channel1"))
			.body("results[0].microarraySampleDetails.channelNumber", is(1))
			.body("results[0].dataProvider.abbreviation", is("FB"))
			.body("results[0].dataProviderCrossReference.referencedCurie", is("HTPSAMPLETEST:0001"))
			.body("results[0].symbol", is("htp-sample-test-symbol"))
			.body("results[0].analysis.analysisType.curie", is("HTPSAMPLETEST:Fbcv0001"))
			.body("results[0].analysis.dataProvider.abbreviation", is("FB"))
			.body("results[0].analysis.relatedNotes", hasSize(1))
			.body("results[0].biologicalReference.curie", is("HTPSAMPLETEST:BioRef0001"))
			.body("results[0].internal", is(true)).body("results[0].obsolete", is(true));
	}

	@Test
	@Order(2)
	public void htpDatasetSampleBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "MR_01_no_sample_id_or_title.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "MR_02_no_sample_type.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "MR_03_no_assay_used.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "MR_04_no_dataset_ids.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "MR_05_no_data_provider.json");
	}

	@Test
	@Order(3)
	public void htpDatasetSampleBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "ER_01_empty_dataset_ids.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "ER_02_empty_data_provider.json");
	}

	@Test
	@Order(4)
	public void htpDatasetSampleBulkUploadInvalidFields() throws Exception {
		// IV_01: the dataset_ids curie doesn't resolve to any ExternalDataBaseEntity at all.
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "IV_01_unknown_dataset_id.json");
		// IV_02: the curie resolves to a real ExternalDataBaseEntity (the sample's own id from test 1)
		// but isn't backed by any HTPExpressionDatasetAnnotation - the new, stricter check this direct
		// path adds over the FMS path.
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "IV_02_dataset_id_without_dataset_annotation.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "IV_03_data_provider_mismatch.json");
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "IV_04_invalid_sample_type.json");
		// IV_05: the genomic information identifier resolves as neither an Allele nor an AGM.
		checkFailedBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "IV_05_invalid_genomic_information_biosample_id.json");
	}

	@Test
	@Order(5)
	public void htpDatasetSampleBulkUploadUpdateOmittingOptionalFields() throws Exception {
		// AF_03 shares AF_01's biological_reference_dto.curie (HTPSAMPLETEST:BioRef0001) - both rows
		// reference the same ExternalDataBaseEntity before UM_01 clears it from AF_01 below. Now 2
		// rows, so filter by sample curie rather than relying on results[0].
		checkSuccessfulBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "AF_03_shares_biological_reference.json");

		// Same htp_expression_sample_dto.curie as AF_01 - must update that same record (matched by
		// sample id, same as the FMS path), not create a second one.
		checkSuccessfulBulkLoad(htpDatasetSampleBulkPostEndpointFb, testFilePath + "UM_01_update_no_optional_fields.json");

		RestAssured.given().when().header("Content-Type", "application/json").body("{}").post(htpDatasetSampleFindEndpoint)
			.then().statusCode(200).body("returnedRecords", is(2))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("symbol")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("analysis")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("biologicalReference")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("genomicInformation")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("microarraySampleDetails")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("dataProviderCrossReference")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("htpExpressionSampleAge")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("htpExpressionSampleLocations")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("assemblyVersions")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("taxon")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("geneticSex")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("sequencingFormat")))
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0001' }", not(hasKey("abundance")))
			// AF_03's own row still references the same, shared ExternalDataBaseEntity row - it must
			// not have been deleted (orphanRemoval) or hit its FK constraint when AF_01 dropped it.
			.body("results.find { it.htpExpressionSample.curie == 'HTPSAMPLETEST:Sample0003' }.biologicalReference.curie", is("HTPSAMPLETEST:BioRef0001"));
	}

	@Test
	@Order(6)
	public void htpDatasetSampleBulkUploadMatchBySampleTitle() throws Exception {
		// SCRUM-6585 (ported from the FMS validator): no sample id submitted - matched instead by
		// (dataProvider, title, dataset_ids set, anatomical structure set). Submitting the identical
		// payload twice must update the same row, not create a second one. Also covers the AGM branch
		// of genomic_information_dto (the FMS fixture's own AGA:Allele0001/"fish" pair actually
		// resolves through this same AGM fallback, not the Allele branch AF_01 exercises).
		checkSuccessfulBulkLoad(htpDatasetSampleBulkPostEndpointMgi, testFilePath + "AF_02_sample_title_match.json");
		RestAssured.given().when().header("Content-Type", "application/json").body("{}").post(htpDatasetSampleFindEndpoint)
			.then().statusCode(200).body("returnedRecords", is(3))
			.body("results.find { it.htpExpressionSampleTitle == 'TEST TITLE MATCH SAMPLE' }.genomicInformation.bioSampleAgm.primaryExternalId", is("HTPSAMPLETEST:Agm0001"))
			.body("results.find { it.htpExpressionSampleTitle == 'TEST TITLE MATCH SAMPLE' }.genomicInformation.bioSampleAgmType.name", is("fish"));

		checkSuccessfulBulkLoad(htpDatasetSampleBulkPostEndpointMgi, testFilePath + "AF_02_sample_title_match.json");
		RestAssured.given().when().header("Content-Type", "application/json").body("{}").post(htpDatasetSampleFindEndpoint)
			.then().statusCode(200).body("returnedRecords", is(3));

		// TO_04 (FMS fixture naming) - same title/provider/dataset but a different anatomical
		// structure is a different identity, so this must create a new row, not match the above.
		checkSuccessfulBulkLoad(htpDatasetSampleBulkPostEndpointMgi, testFilePath + "AF_02b_sample_title_different_structure.json");
		RestAssured.given().when().header("Content-Type", "application/json").body("{}").post(htpDatasetSampleFindEndpoint)
			.then().statusCode(200).body("returnedRecords", is(4));
	}

	@Test
	@Order(7)
	public void htpDatasetSampleBulkUploadCleanup() throws Exception {
		// IT_0610/IT_0612 (run later, sharing this same database) find their own records via a
		// blanket /find with no filter and assert exact counts - delete this test's leftover rows
		// (both sample annotations and the dataset dependency) so they don't inflate those counts.
		// Fetch both ids up front rather than re-querying the search index between deletes - doing so
		// risks a stale/empty result if the index hasn't caught up with the previous delete yet.
		List<Integer> ids = RestAssured.given().when().header("Content-Type", "application/json").body("{}")
			.post(htpDatasetSampleFindEndpoint).then().statusCode(200).body("returnedRecords", is(4))
			.extract().jsonPath().getList("results.id", Integer.class);
		for (Integer id : ids) {
			RestAssured.given().when().delete("/api/htpexpressiondatasetsampleannotation/" + id).then().statusCode(200);
		}
		RestAssured.given().when().header("Content-Type", "application/json").body("{}")
			.post(htpDatasetSampleFindEndpoint).then().statusCode(200).body("returnedRecords", is(0));

		int datasetId = RestAssured.given().when().header("Content-Type", "application/json").body("{}")
			.post(htpDatasetFindEndpoint).then().statusCode(200).extract().path("results[0].id");
		RestAssured.given().when().delete("/api/htpexpressiondatasetannotation/" + datasetId).then().statusCode(200);
		RestAssured.given().when().header("Content-Type", "application/json").body("{}")
			.post(htpDatasetFindEndpoint).then().statusCode(200).body("returnedRecords", is(0));
	}
}
