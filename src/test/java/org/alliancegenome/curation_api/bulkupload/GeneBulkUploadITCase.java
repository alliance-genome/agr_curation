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
	private String requiredReference2 = "AGRKB:000000021";
	private String requiredReferenceXref2 = "PMID:25920551";
	
	private final String geneBulkPostEndpoint = "/api/gene/bulk/WB/genes";
	private final String geneGetEndpoint = "/api/gene/";
	private final String geneTestFilePath = "src/test/resources/bulk/01_gene/";
	
	private void loadRequiredEntities() throws Exception {
		loadReference(requiredReference, requiredReferenceXref);
		loadReference(requiredReference2, requiredReferenceXref2);
	}

	@Test
	@Order(1)
	public void geneBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "AF_01_all_fields.json");
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + "GENETEST:Gene0001").
			then().
			statusCode(200).
			body("entity.curie", is("GENETEST:Gene0001")).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSymbol.displayText", is("Tg1")).
			body("entity.geneSymbol.formatText", is("Tg<sup>1</sup>")).
			body("entity.geneSymbol.synonymScope.name", is("exact")).
			body("entity.geneSymbol.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.geneSymbol.nameType.name", is("nomenclature_symbol")).
			body("entity.geneSymbol.evidence[0].curie", is(requiredReference)).
			body("entity.geneSymbol.internal", is(true)).
			body("entity.geneSymbol.obsolete", is(true)).
			body("entity.geneSymbol.createdBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.geneSymbol.updatedBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.geneSymbol.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSymbol.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneFullName.displayText", is("Test gene 1")).
			body("entity.geneFullName.formatText", is("Test gene<sup>1</sup>")).
			body("entity.geneFullName.synonymScope.name", is("exact")).
			body("entity.geneFullName.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.geneFullName.nameType.name", is("full_name")).
			body("entity.geneFullName.evidence[0].curie", is(requiredReference)).
			body("entity.geneFullName.internal", is(true)).
			body("entity.geneFullName.obsolete", is(true)).
			body("entity.geneFullName.createdBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.geneFullName.updatedBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.geneFullName.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneFullName.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSystematicName.displayText", is("Tg1.1")).
			body("entity.geneSystematicName.formatText", is("Tg1.1")).
			body("entity.geneSystematicName.synonymScope.name", is("exact")).
			body("entity.geneSystematicName.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.geneSystematicName.nameType.name", is("systematic_name")).
			body("entity.geneSystematicName.evidence[0].curie", is(requiredReference)).
			body("entity.geneSystematicName.internal", is(true)).
			body("entity.geneSystematicName.obsolete", is(true)).
			body("entity.geneSystematicName.createdBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.geneSystematicName.updatedBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.geneSystematicName.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSystematicName.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSynonyms", hasSize(1)).
			body("entity.geneSynonyms[0].displayText", is("Test gene synonym 1")).
			body("entity.geneSynonyms[0].formatText", is("Test gene synonym <sup>1</sup>")).
			body("entity.geneSynonyms[0].synonymScope.name", is("exact")).
			body("entity.geneSynonyms[0].synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.geneSynonyms[0].nameType.name", is("unspecified")).
			body("entity.geneSynonyms[0].evidence[0].curie", is(requiredReference)).
			body("entity.geneSynonyms[0].internal", is(true)).
			body("entity.geneSynonyms[0].obsolete", is(true)).
			body("entity.geneSynonyms[0].createdBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.geneSynonyms[0].updatedBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.geneSynonyms[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSynonyms[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString()));
	}
	
	@Test
	@Order(2)
	public void geneBulkUploadUpdateFields() throws Exception {
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "UD_01_update_all_except_default_fields.json");
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + "GENETEST:Gene0001").
			then().
			statusCode(200).
			body("entity.curie", is("GENETEST:Gene0001")).
			body("entity.taxon.curie", is("NCBITaxon:9606")).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSymbol.displayText", is("Tg1a")).
			body("entity.geneSymbol.formatText", is("Tg<sup>1a</sup>")).
			body("entity.geneSymbol.synonymScope.name", is("broad")).
			body("entity.geneSymbol.synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.geneSymbol.nameType.name", is("systematic_name")).
			body("entity.geneSymbol.evidence[0].curie", is(requiredReference2)).
			body("entity.geneSymbol.internal", is(false)).
			body("entity.geneSymbol.obsolete", is(false)).
			body("entity.geneSymbol.createdBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.geneSymbol.updatedBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.geneSymbol.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSymbol.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneFullName.displayText", is("Test gene 1a")).
			body("entity.geneFullName.formatText", is("Test gene<sup>1a</sup>")).
			body("entity.geneFullName.synonymScope.name", is("broad")).
			body("entity.geneFullName.synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.geneFullName.nameType.name", is("full_name")).
			body("entity.geneFullName.evidence[0].curie", is(requiredReference2)).
			body("entity.geneFullName.internal", is(false)).
			body("entity.geneFullName.obsolete", is(false)).
			body("entity.geneFullName.createdBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.geneFullName.updatedBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.geneFullName.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneFullName.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSystematicName.displayText", is("Tg1.1a")).
			body("entity.geneSystematicName.formatText", is("Tg1.1a")).
			body("entity.geneSystematicName.synonymScope.name", is("broad")).
			body("entity.geneSystematicName.synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.geneSystematicName.nameType.name", is("systematic_name")).
			body("entity.geneSystematicName.evidence[0].curie", is(requiredReference2)).
			body("entity.geneSystematicName.internal", is(false)).
			body("entity.geneSystematicName.obsolete", is(false)).
			body("entity.geneSystematicName.createdBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.geneSystematicName.updatedBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.geneSystematicName.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSystematicName.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSynonyms", hasSize(1)).
			body("entity.geneSynonyms[0].displayText", is("Test gene synonym 1a")).
			body("entity.geneSynonyms[0].formatText", is("Test gene synonym <sup>1a</sup>")).
			body("entity.geneSynonyms[0].synonymScope.name", is("broad")).
			body("entity.geneSynonyms[0].synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.geneSynonyms[0].nameType.name", is("systematic_name")).
			body("entity.geneSynonyms[0].evidence[0].curie", is(requiredReference2)).
			body("entity.geneSynonyms[0].internal", is(false)).
			body("entity.geneSynonyms[0].obsolete", is(false)).
			body("entity.geneSynonyms[0].createdBy.uniqueId", is("GENETEST:Person0002")).
			body("entity.geneSynonyms[0].updatedBy.uniqueId", is("GENETEST:Person0001")).
			body("entity.geneSynonyms[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneSynonyms[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString()));
	}
	
	@Test
	@Order(3)
	public void geneBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_01_no_curie.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_02_no_taxon.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_03_no_gene_symbol.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_04_no_gene_symbol_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_05_no_gene_full_name_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_06_no_gene_systematic_name_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_07_no_gene_synonym_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_08_no_gene_symbol_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_09_no_gene_full_name_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_10_no_gene_systematic_name_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_11_no_gene_synonym_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_12_no_gene_symbol_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_13_no_gene_full_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_14_no_gene_systematic_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MR_15_no_gene_synonym_name_type.json");
	}
	
	@Test
	@Order(4)
	public void geneBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_01_empty_curie.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_02_empty_taxon.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_03_empty_gene_symbol_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_04_empty_gene_full_name_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_05_empty_gene_systematic_name_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_06_empty_gene_synonym_display_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_07_empty_gene_symbol_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_08_empty_gene_full_name_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_09_empty_gene_systematic_name_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_10_empty_gene_synonym_format_text.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_11_empty_gene_symbol_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_12_empty_gene_full_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_13_empty_gene_systematic_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "ER_14_empty_gene_synonym_name_type.json");
	}
	
	@Test
	@Order(5)
	public void geneBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_01_invalid_date_created.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_02_invalid_date_updated.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_03_invalid_taxon.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_04_invalid_gene_symbol_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_05_invalid_gene_full_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_06_invalid_gene_systematic_name_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_07_invalid_gene_synonym_name_type.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_08_invalid_gene_symbol_synonym_scope.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_09_invalid_gene_full_name_synonym_scope.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_10_invalid_gene_systematic_name_synonym_scope.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_11_invalid_gene_synonym_synonym_scope.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_12_invalid_gene_symbol_evidence.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_13_invalid_gene_full_name_evidence.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_14_invalid_gene_systematic_name_evidence.json");
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "IV_15_invalid_gene_synonym_evidence.json");
	}
	
	@Test
	@Order(6)
	public void agmBulkUploadUnsupportedFields() throws Exception {
		checkFailedBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "US_01_unsupported_taxon.json");
	}

	@Test
	@Order(7)
	public void geneBulkUploadUpdateMissingNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "UM_01_update_no_non_required_fields_level_1.json");
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + "GENETEST:Gene0001").
			then().
			statusCode(200).
			body("entity.curie", is("GENETEST:Gene0001")).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("geneFullName"))).
			body("entity", not(hasKey("geneSystematicName"))).
			body("entity", not(hasKey("geneSynonyms")));
	}

	@Test
	@Order(8)
	public void geneBulkUploadUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "UM_02_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + "GENETEST:Gene0001").then().
			statusCode(200).
			body("entity.curie", is("GENETEST:Gene0001")).
			body("entity.geneSymbol", not(hasKey("synonymScope"))).
			body("entity.geneSymbol", not(hasKey("synonymUrl"))).
			body("entity.geneSymbol", not(hasKey("evidence"))).
			body("entity.geneSymbol", not(hasKey("createdBy"))).
			body("entity.geneSymbol", not(hasKey("updatedBy"))).
			body("entity.geneSymbol", not(hasKey("dateCreated"))).
			body("entity.geneSymbol", not(hasKey("dateUpdated"))).
			body("entity.geneFullName", not(hasKey("synonymScope"))).
			body("entity.geneFullName", not(hasKey("synonymUrl"))).
			body("entity.geneFullName", not(hasKey("evidence"))).
			body("entity.geneFullName", not(hasKey("createdBy"))).
			body("entity.geneFullName", not(hasKey("updatedBy"))).
			body("entity.geneFullName", not(hasKey("dateCreated"))).
			body("entity.geneFullName", not(hasKey("dateUpdated"))).
			body("entity.geneSystematicName", not(hasKey("synonymScope"))).
			body("entity.geneSystematicName", not(hasKey("synonymUrl"))).
			body("entity.geneSystematicName", not(hasKey("evidence"))).
			body("entity.geneSystematicName", not(hasKey("createdBy"))).
			body("entity.geneSystematicName", not(hasKey("updatedBy"))).
			body("entity.geneSystematicName", not(hasKey("dateCreated"))).
			body("entity.geneSystematicName", not(hasKey("dateUpdated"))).
			body("entity.geneSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.geneSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.geneSynonyms[0]", not(hasKey("evidence"))).
			body("entity.geneSynonyms[0]", not(hasKey("createdBy"))).
			body("entity.geneSynonyms[0]", not(hasKey("updatedBy"))).
			body("entity.geneSynonyms[0]", not(hasKey("dateCreated"))).
			body("entity.geneSynonyms[0]", not(hasKey("dateUpdated")));
	}

	@Test
	@Order(9)
	public void geneBulkUploadUpdateEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
			when().
			get(geneGetEndpoint + "GENETEST:Gene0001").
			then().
			statusCode(200).
			body("entity.curie", is("GENETEST:Gene0001")).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity.geneSymbol", not(hasKey("synonymScope"))).
			body("entity.geneSymbol", not(hasKey("synonymUrl"))).
			body("entity.geneSymbol", not(hasKey("evidence"))).
			body("entity.geneSymbol", not(hasKey("createdBy"))).
			body("entity.geneSymbol", not(hasKey("updatedBy"))).
			body("entity.geneSymbol", not(hasKey("dateCreated"))).
			body("entity.geneSymbol", not(hasKey("dateUpdated"))).
			body("entity.geneFullName", not(hasKey("synonymScope"))).
			body("entity.geneFullName", not(hasKey("synonymUrl"))).
			body("entity.geneFullName", not(hasKey("evidence"))).
			body("entity.geneFullName", not(hasKey("createdBy"))).
			body("entity.geneFullName", not(hasKey("updatedBy"))).
			body("entity.geneFullName", not(hasKey("dateCreated"))).
			body("entity.geneFullName", not(hasKey("dateUpdated"))).
			body("entity.geneSystematicName", not(hasKey("synonymScope"))).
			body("entity.geneSystematicName", not(hasKey("synonymUrl"))).
			body("entity.geneSystematicName", not(hasKey("evidence"))).
			body("entity.geneSystematicName", not(hasKey("createdBy"))).
			body("entity.geneSystematicName", not(hasKey("updatedBy"))).
			body("entity.geneSystematicName", not(hasKey("dateCreated"))).
			body("entity.geneSystematicName", not(hasKey("dateUpdated"))).
			body("entity.geneSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.geneSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.geneSynonyms[0]", not(hasKey("evidence"))).
			body("entity.geneSynonyms[0]", not(hasKey("createdBy"))).
			body("entity.geneSynonyms[0]", not(hasKey("updatedBy"))).
			body("entity.geneSynonyms[0]", not(hasKey("dateCreated"))).
			body("entity.geneSynonyms[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(10)
	public void geneBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MN_01_no_non_required_fields_level_1.json");
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "MN_02_no_non_required_fields_level_2.json");
	}

	@Test
	@Order(11)
	public void geneBulkUploadEmptyNonRequiredFieldsLevel() throws Exception {
		checkSuccessfulBulkLoad(geneBulkPostEndpoint, geneTestFilePath + "EN_01_empty_non_required_fields.json");
	}
}
