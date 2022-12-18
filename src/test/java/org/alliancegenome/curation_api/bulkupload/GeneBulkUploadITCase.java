package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.nio.file.Files;
import java.nio.file.Path;
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
@DisplayName("01 - Gene bulk upload")
@Order(1)
public class GeneBulkUploadITCase extends BaseITCase {
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}
	
	private String requiredReference = "AGRKB:000000001";
	private String requiredReferenceXref = "PMID:25920550";
	
	private final String geneBulkPostEndpoint = "/api/gene/bulk/genes";
	private final String geneFindEndpoint = "/api/gene/find?limit=10&page=0";
	private final String geneTestFilePath = "src/test/resources/bulk/01_gene/";
	
	private void loadRequiredEntities() throws Exception {
		loadReference(requiredReference, requiredReferenceXref);
	}

	@Test
	@Order(1)
	public void geneBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "01_all_fields_gene.json");
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(geneFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].taxon.curie", is("NCBITaxon:6239")).
			body("results[0].internal", is(true)).
			body("results[0].obsolete", is(false)).
			body("results[0].createdBy.uniqueId", is("GENETEST:Person0001")).
			body("results[0].updatedBy.uniqueId", is("GENETEST:Person0002")).
			body("results[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].dateUpdated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].geneSymbol.displayText", is("Tg1")).
			body("results[0].geneSymbol.formatText", is("Tg<sup>1</sup>")).
			body("results[0].geneSymbol.synonymScope.name", is("exact")).
			body("results[0].geneSymbol.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].geneSymbol.nameType.name", is("nomenclature_symbol")).
			body("results[0].geneSymbol.evidence[0].curie", is(requiredReference)).
			body("results[0].geneFullName.displayText", is("Test gene 1")).
			body("results[0].geneFullName.formatText", is("Test gene<sup>1</sup>")).
			body("results[0].geneFullName.synonymScope.name", is("exact")).
			body("results[0].geneFullName.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].geneFullName.nameType.name", is("full_name")).
			body("results[0].geneFullName.evidence[0].curie", is(requiredReference)).
			body("results[0].geneSystematicName.displayText", is("Tg1.1")).
			body("results[0].geneSystematicName.formatText", is("Tg1.1")).
			body("results[0].geneSystematicName.synonymScope.name", is("exact")).
			body("results[0].geneSystematicName.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].geneSystematicName.nameType.name", is("systematic_name")).
			body("results[0].geneSystematicName.evidence[0].curie", is(requiredReference)).
			body("results[0].geneSynonyms", hasSize(1)).
			body("results[0].geneSynonyms[0].displayText", is("Test gene synonym 1")).
			body("results[0].geneSynonyms[0].formatText", is("Test gene synonym <sup>1</sup>")).
			body("results[0].geneSynonyms[0].synonymScope.name", is("exact")).
			body("results[0].geneSynonyms[0].synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].geneSynonyms[0].nameType.name", is("unspecified")).
			body("results[0].geneSynonyms[0].evidence[0].curie", is(requiredReference));
	}
	
	@Test
	@Order(2)
	public void geneBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "02_no_curie_gene.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "05_no_taxon_gene.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "28_no_gene_symbol.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "32_no_gene_symbol_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "33_no_gene_full_name_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "34_no_gene_systematic_name_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "35_no_gene_synonym_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "40_no_gene_symbol_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "41_no_gene_full_name_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "42_no_gene_systematic_name_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "43_no_gene_synonym_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "48_no_gene_symbol_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "49_no_gene_full_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "50_no_gene_systematic_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "51_no_gene_synonym_name_type.json");
	}
	
	@Test
	@Order(3)
	public void geneBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "06_no_internal_gene.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "07_no_created_by_gene.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "08_no_updated_by_gene.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "09_no_date_created_gene.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "10_no_date_updated_gene.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "14_no_obsolete_gene.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "29_no_gene_full_name.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "30_no_gene_systematic_name.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "31_no_gene_synonym.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "56_no_gene_symbol_synonym_scope.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "57_no_gene_full_name_synonym_scope.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "58_no_gene_systematic_name_synonym_scope.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "59_no_gene_synonym_synonym_scope.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "68_no_gene_symbol_evidence.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "69_no_gene_full_name_evidence.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "70_no_gene_systematic_name_evidence.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "71_no_gene_synonym_evidence.json");
	}
	
	@Test
	@Order(4)
	public void geneBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "17_empty_curie_gene.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "22_empty_taxon_gene.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "36_empty_gene_symbol_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "37_empty_gene_full_name_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "38_empty_gene_systematic_name_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "39_empty_gene_synonym_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "44_empty_gene_symbol_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "45_empty_gene_full_name_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "46_empty_gene_systematic_name_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "47_empty_gene_synonym_format_text.json");
	}
	
	@Test
	@Order(5)
	public void geneBulkUploadEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "15_empty_date_created_gene.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "18_empty_created_by_gene.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "19_empty_updated_by_gene.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "20_empty_date_updated_gene.json");
	}
	
	@Test
	@Order(6)
	public void geneBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "11_invalid_taxon_gene.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "12_invalid_date_created_gene.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "13_invalid_date_updated_gene.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "52_invalid_gene_symbol_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "53_invalid_gene_full_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "54_invalid_gene_systematic_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "55_invalid_gene_synonym_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "60_invalid_gene_symbol_synonym_scope.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "61_invalid_gene_full_name_synonym_scope.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "62_invalid_gene_systematic_name_synonym_scope.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "63_invalid_gene_synonym_synonym_scope.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "72_invalid_gene_symbol_evidence.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "73_invalid_gene_full_name_evidence.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "74_invalid_gene_systematic_name_evidence.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "75_invalid_gene_synonym_evidence.json");
	}

	@Test
	@Order(7)
	public void geneBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		String originalFilePath = geneTestFilePath + "01_all_fields_gene.json";
		
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "24_update_no_created_by_gene.json", "createdBy");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "25_update_no_updated_by_gene.json", "updatedBy");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "26_update_no_date_created_gene.json", "dateCreated");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "27_update_no_date_updated_gene.json", "dateUpdated");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "64_update_no_gene_symbol_synonym_url.json", "geneSymbol.synonymUrl");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "65_update_no_gene_full_name_synonym_url.json", "geneFullName.synonymUrl");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "66_update_no_gene_systematic_name_synonym_url.json", "geneSystematicName.synonymUrl");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "67_update_no_gene_synonym_synonym_url.json", "geneSynonyms[0].synonymUrl");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "76_update_no_gene_symbol_evidence.json", "geneSymbol.evidence");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "77_update_no_gene_full_name_evidence.json", "geneFullName.evidence");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "78_update_no_gene_systematic_name_evidence.json", "geneSystematicName.evidence");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "79_update_no_gene_synonym_evidence.json", "geneSynonyms[0].evidence");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "80_update_no_gene_full_name.json", "geneFullName");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "81_update_no_gene_systematic_name.json", "geneSystematicName");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneBulkPostEndpoint, geneFindEndpoint,
				originalFilePath, geneTestFilePath + "82_update_no_gene_synonym.json", "geneSynonyms");
	}
}
