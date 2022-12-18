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
@DisplayName("02 - Allele bulk upload")
@Order(2)
public class AlleleBulkUploadITCase extends BaseITCase {
	
	private String requiredReference = "AGRKB:000000001";
	private String requiredSoTerm = "SO:00001";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String alleleBulkPostEndpoint = "/api/allele/bulk/alleles";
	private final String alleleFindEndpoint = "/api/allele/find?limit=10&page=0";
	private final String alleleTestFilePath = "src/test/resources/bulk/02_allele/";

	private void loadRequiredEntities() throws Exception {
		loadSOTerm(requiredSoTerm, "Test SOTerm");
	}
	
	@Test
	@Order(1)
	public void alleleBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "001_all_fields_allele.json");
	
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(alleleFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0001")).
			body("results[0].taxon.curie", is("NCBITaxon:6239")).
			body("results[0].internal", is(true)).
			body("results[0].obsolete", is(true)).
			body("results[0].inheritanceMode.name", is("dominant")).
			body("results[0].inCollection.name", is("Million_mutations_project")).
			body("results[0].isExtinct", is(false)).
			body("results[0].references", hasSize(1)).
			body("results[0].references[0].curie", is(requiredReference)).
			body("results[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("results[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("results[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].dateUpdated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].alleleSymbol.displayText", is("Ta1")).
			body("results[0].alleleSymbol.formatText", is("Ta<sup>1</sup>")).
			body("results[0].alleleSymbol.synonymScope.name", is("exact")).
			body("results[0].alleleSymbol.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].alleleSymbol.nameType.name", is("nomenclature_symbol")).
			body("results[0].alleleSymbol.evidence[0].curie", is(requiredReference)).
			body("results[0].alleleFullName.displayText", is("Test allele 1")).
			body("results[0].alleleFullName.formatText", is("Test allele<sup>1</sup>")).
			body("results[0].alleleFullName.synonymScope.name", is("exact")).
			body("results[0].alleleFullName.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].alleleFullName.nameType.name", is("full_name")).
			body("results[0].alleleFullName.evidence[0].curie", is(requiredReference)).
			body("results[0].alleleSynonyms", hasSize(1)).
			body("results[0].alleleSynonyms[0].displayText", is("Test allele synonym 1")).
			body("results[0].alleleSynonyms[0].formatText", is("Test allele synonym <sup>1</sup>")).
			body("results[0].alleleSynonyms[0].synonymScope.name", is("exact")).
			body("results[0].alleleSynonyms[0].synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].alleleSynonyms[0].nameType.name", is("unspecified")).
			body("results[0].alleleSynonyms[0].evidence[0].curie", is(requiredReference)).
			body("results[0].alleleMutationTypes", hasSize(1)).
			body("results[0].alleleMutationTypes[0].evidence[0].curie", is(requiredReference)).
			body("results[0].alleleMutationTypes[0].mutationTypes[0].curie", is(requiredSoTerm)).
			body("results[0].alleleSecondaryIds", hasSize(1)).
			body("results[0].alleleSecondaryIds[0].evidence[0].curie", is(requiredReference)).
			body("results[0].alleleSecondaryIds[0].secondaryId", is("TEST:Secondary"));
	}
	
	@Test
	@Order(2)
	public void alleleBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "002_no_curie_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "005_no_taxon_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "044_no_allele_mutation_type_mutation_types.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "048_no_allele_symbol.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "049_no_allele_symbol_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "050_no_allele_symbol_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "051_no_allele_symbol_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "060_no_allele_full_name_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "061_no_allele_full_name_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "062_no_allele_full_name_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "071_no_allele_synonym_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "072_no_allele_synonym_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "073_no_allele_synonym_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "092_no_allele_secondary_id_secondary_id.json");
	}
	
	@Test
	@Order(3)
	public void alleleBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "006_no_internal_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "007_no_created_by_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "008_no_updated_by_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "009_no_date_created_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "010_no_date_updated_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "014_no_obsolete_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "023_no_inheritance_mode_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "024_no_in_collection_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "026_no_is_extinct_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "027_no_references_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "042_no_allele_mutation_type_evidence.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "052_no_allele_symbol_synonym_scope.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "053_no_allele_symbol_evidence.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "059_no_allele_full_name.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "063_no_allele_full_name_synonym_scope.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "064_no_allele_full_name_evidence.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "070_no_allele_synonym.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "074_no_allele_synonym_synonym_scope.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "075_no_allele_synonym_evidence.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "090_no_allele_secondary_id.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "094_no_allele_secondary_id_evidence.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "099_no_allele_symbol_synonym_url.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "100_no_allele_full_name_synonym_url.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "101_no_allele_synonym_synonym_url.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "102_no_allele_mutation_type.json");
	}
	
	@Test
	@Order(4)
	public void alleleBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "021_empty_curie_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "022_empty_taxon_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "084_empty_allele_symbol_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "085_empty_allele_symbol_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "086_empty_allele_full_name_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "087_empty_allele_full_name_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "088_empty_allele_synonym_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "089_empty_allele_synonym_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "093_empty_allele_secondary_id_secondary_id.json");
	}
	
	@Test
	@Order(5)
	public void alleleBulkUploadEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "016_empty_updated_by_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "018_empty_created_by_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "019_empty_date_updated_allele.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "020_empty_date_created_allele.json");
	}
	
	@Test
	@Order(6)
	public void alleleBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "011_invalid_taxon_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "012_invalid_date_created_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "013_invalid_date_updated_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "028_invalid_inheritance_mode_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "029_invalid_in_collection_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "031_invalid_reference_allele.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "045_invalid_allele_mutation_type_mutation_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "046_invalid_allele_mutation_type_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "054_invalid_allele_symbol_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "055_invalid_allele_symbol_synonym_scope.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "056_invalid_allele_symbol_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "065_invalid_allele_full_name_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "066_invalid_allele_full_name_synonym_scope.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "067_invalid_allele_full_name_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "076_invalid_allele_synonym_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "077_invalid_allele_synonym_synonym_scope.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "078_invalid_allele_synonym_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "096_invalid_allele_secondary_id_evidence.json");
	}

	@Test
	@Order(7)
	public void alleleBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		String originalFilePath = alleleTestFilePath + "001_all_fields_allele.json";
		
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "033_update_no_created_by_allele.json", "createdBy");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "034_update_no_updated_by_allele.json", "updatedBy");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "035_update_no_date_created_allele.json", "dateCreated");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "036_update_no_date_updated_allele.json", "dateUpdated");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "037_update_no_inheritance_mode_allele.json", "inheritanceMode");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "038_update_no_in_collection_allele.json", "inCollection");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "040_update_no_is_extinct_allele.json", "isExtinct");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "041_update_no_references_allele.json", "references");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "043_update_no_allele_mutation_type_evidence.json", "alleleMutationTypes[0].evidence");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "047_update_no_allele_mutation_types.json", "alleleMutationTypes");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "057_update_no_allele_symbol_synonym_scope.json", "alleleSymbol.synonymScope");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "058_update_no_allele_symbol_evidence.json", "alleleSymbol.evidence");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "068_update_no_allele_full_name_synonym_scope.json", "alleleFullName.synonymScope");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "069_update_no_allele_full_name_evidence.json", "alleleFullName.evidence");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "079_update_no_allele_synonym_synonym_scope.json", "alleleSynonyms[0].synonymScope");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "080_update_no_allele_synonym_evidence.json", "alleleSynonyms[0].evidence");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "081_update_no_allele_symbol_synonym_url.json", "alleleSymbol.synonymUrl");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "082_update_no_allele_full_name_synonym_url.json", "alleleFullName.synonymUrl");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "083_update_no_allele_synonym_synonym_url.json", "alleleSynonyms[0].synonymUrl");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "091_update_no_allele_secondary_id.json", "alleleSecondaryIds");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "095_update_no_allele_secondary_id_evidence.json", "alleleSecondaryIds[0].evidence");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "097_update_no_allele_full_name.json", "alleleFullName");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleBulkPostEndpoint, alleleFindEndpoint,
				originalFilePath, alleleTestFilePath + "098_update_no_allele_synonym.json", "alleleSynonyms");
	}
}
