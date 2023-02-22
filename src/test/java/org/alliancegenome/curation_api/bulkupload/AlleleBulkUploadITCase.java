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
@DisplayName("02 - Allele bulk upload")
@Order(2)
public class AlleleBulkUploadITCase extends BaseITCase {
	
	private String requiredReference = "AGRKB:000000001";
	private String requiredReference2 = "AGRKB:000000021";
	private String requiredSoTerm = "SO:00001";
	private String requiredSoTerm2 = "SO:00002";
	private String requiredMpTerm = "MP:00001";
	private String requiredMpTerm2 = "MP:00002";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String alleleBulkPostEndpoint = "/api/allele/bulk/alleles";
	private final String alleleGetEndpoint = "/api/allele/";
	private final String alleleTestFilePath = "src/test/resources/bulk/02_allele/";

	private void loadRequiredEntities() throws Exception {
		loadSOTerm(requiredSoTerm, "Test SOTerm");
		loadSOTerm(requiredSoTerm2, "Test SOTerm2");
		loadMPTerm(requiredMpTerm, "Test MPTerm");
		loadMPTerm(requiredMpTerm2, "Test MPTerm2");
	}
	
	@Test
	@Order(1)
	public void alleleBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(alleleGetEndpoint + "ALLELETEST:Allele0001").
			then().
			statusCode(200).
			body("entity.curie", is("ALLELETEST:Allele0001")).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.inCollection.name", is("Million_mutations_project")).
			body("entity.isExtinct", is(false)).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(requiredReference)).
			body("entity.alleleSymbol.displayText", is("Ta1")).
			body("entity.alleleSymbol.formatText", is("Ta<sup>1</sup>")).
			body("entity.alleleSymbol.synonymScope.name", is("exact")).
			body("entity.alleleSymbol.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.alleleSymbol.nameType.name", is("nomenclature_symbol")).
			body("entity.alleleSymbol.evidence[0].curie", is(requiredReference)).
			body("entity.alleleSymbol.internal", is(true)).
			body("entity.alleleSymbol.obsolete", is(true)).
			body("entity.alleleSymbol.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSymbol.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSymbol.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSymbol.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleFullName.displayText", is("Test allele 1")).
			body("entity.alleleFullName.formatText", is("Test allele<sup>1</sup>")).
			body("entity.alleleFullName.synonymScope.name", is("exact")).
			body("entity.alleleFullName.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.alleleFullName.nameType.name", is("full_name")).
			body("entity.alleleFullName.evidence[0].curie", is(requiredReference)).
			body("entity.alleleFullName.internal", is(true)).
			body("entity.alleleFullName.obsolete", is(true)).
			body("entity.alleleFullName.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleFullName.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleFullName.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleFullName.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSynonyms", hasSize(1)).
			body("entity.alleleSynonyms[0].displayText", is("Test allele synonym 1")).
			body("entity.alleleSynonyms[0].formatText", is("Test allele synonym <sup>1</sup>")).
			body("entity.alleleSynonyms[0].synonymScope.name", is("exact")).
			body("entity.alleleSynonyms[0].synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.alleleSynonyms[0].nameType.name", is("unspecified")).
			body("entity.alleleSynonyms[0].evidence[0].curie", is(requiredReference)).
			body("entity.alleleSynonyms[0].internal", is(true)).
			body("entity.alleleSynonyms[0].obsolete", is(true)).
			body("entity.alleleSynonyms[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSynonyms[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSynonyms[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSynonyms[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleMutationTypes", hasSize(1)).
			body("entity.alleleMutationTypes[0].evidence[0].curie", is(requiredReference)).
			body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(requiredSoTerm)).
			body("entity.alleleMutationTypes[0].internal", is(true)).
			body("entity.alleleMutationTypes[0].obsolete", is(true)).
			body("entity.alleleMutationTypes[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleMutationTypes[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleMutationTypes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleMutationTypes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleInheritanceModes", hasSize(1)).
			body("entity.alleleInheritanceModes[0].evidence[0].curie", is(requiredReference)).
			body("entity.alleleInheritanceModes[0].inheritanceMode.name", is("dominant")).
			body("entity.alleleInheritanceModes[0].phenotypeTerm.curie", is(requiredMpTerm)).
			body("entity.alleleInheritanceModes[0].phenotypeStatement", is("Phenotype statement")).
			body("entity.alleleInheritanceModes[0].internal", is(true)).
			body("entity.alleleInheritanceModes[0].obsolete", is(true)).
			body("entity.alleleInheritanceModes[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleInheritanceModes[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleInheritanceModes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleInheritanceModes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSecondaryIds", hasSize(1)).
			body("entity.alleleSecondaryIds[0].evidence[0].curie", is(requiredReference)).
			body("entity.alleleSecondaryIds[0].secondaryId", is("TEST:Secondary")).
			body("entity.alleleSecondaryIds[0].internal", is(true)).
			body("entity.alleleSecondaryIds[0].obsolete", is(true)).
			body("entity.alleleSecondaryIds[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSecondaryIds[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSecondaryIds[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSecondaryIds[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString()));
	}
	
	@Test
	@Order(2)
	public void alleleBulkUploadUpdateCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "UD_01_update_all_except_default_fields.json");
	
		RestAssured.given().
			when().
			get(alleleGetEndpoint + "ALLELETEST:Allele0001").
			then().
			statusCode(200).
			body("entity.curie", is("ALLELETEST:Allele0001")).
			body("entity.taxon.curie", is("NCBITaxon:9606")).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.inCollection.name", is("WGS_Yanai")).
			body("entity.isExtinct", is(true)).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(requiredReference2)).
			body("entity.alleleSymbol.displayText", is("Ta1a")).
			body("entity.alleleSymbol.formatText", is("Ta<sup>1a</sup>")).
			body("entity.alleleSymbol.synonymScope.name", is("broad")).
			body("entity.alleleSymbol.synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.alleleSymbol.nameType.name", is("systematic_name")).
			body("entity.alleleSymbol.evidence[0].curie", is(requiredReference2)).
			body("entity.alleleSymbol.internal", is(false)).
			body("entity.alleleSymbol.obsolete", is(false)).
			body("entity.alleleSymbol.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSymbol.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSymbol.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSymbol.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleFullName.displayText", is("Test allele 1a")).
			body("entity.alleleFullName.formatText", is("Test allele<sup>1a</sup>")).
			body("entity.alleleFullName.synonymScope.name", is("broad")).
			body("entity.alleleFullName.synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.alleleFullName.nameType.name", is("full_name")).
			body("entity.alleleFullName.evidence[0].curie", is(requiredReference2)).
			body("entity.alleleFullName.internal", is(false)).
			body("entity.alleleFullName.obsolete", is(false)).
			body("entity.alleleFullName.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleFullName.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleFullName.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleFullName.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSynonyms", hasSize(1)).
			body("entity.alleleSynonyms[0].displayText", is("Test allele synonym 1a")).
			body("entity.alleleSynonyms[0].formatText", is("Test allele synonym <sup>1a</sup>")).
			body("entity.alleleSynonyms[0].synonymScope.name", is("broad")).
			body("entity.alleleSynonyms[0].synonymUrl", is("https://alliancegenome.org/test2")).
			body("entity.alleleSynonyms[0].nameType.name", is("nomenclature_symbol")).
			body("entity.alleleSynonyms[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleSynonyms[0].internal", is(false)).
			body("entity.alleleSynonyms[0].obsolete", is(false)).
			body("entity.alleleSynonyms[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSynonyms[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSynonyms[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSynonyms[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleMutationTypes", hasSize(1)).
			body("entity.alleleMutationTypes[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(requiredSoTerm2)).
			body("entity.alleleMutationTypes[0].internal", is(false)).
			body("entity.alleleMutationTypes[0].obsolete", is(false)).
			body("entity.alleleMutationTypes[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleMutationTypes[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleMutationTypes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleMutationTypes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleInheritanceModes", hasSize(1)).
			body("entity.alleleInheritanceModes[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleInheritanceModes[0].inheritanceMode.name", is("recessive")).
			body("entity.alleleInheritanceModes[0].phenotypeTerm.curie", is(requiredMpTerm2)).
			body("entity.alleleInheritanceModes[0].phenotypeStatement", is("Phenotype statement 2")).
			body("entity.alleleInheritanceModes[0].internal", is(false)).
			body("entity.alleleInheritanceModes[0].obsolete", is(false)).
			body("entity.alleleInheritanceModes[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleInheritanceModes[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleInheritanceModes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleInheritanceModes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSecondaryIds", hasSize(1)).
			body("entity.alleleSecondaryIds[0].evidence[0].curie", is(requiredReference2)).
			body("entity.alleleSecondaryIds[0].secondaryId", is("TEST:Secondary2")).
			body("entity.alleleSecondaryIds[0].internal", is(false)).
			body("entity.alleleSecondaryIds[0].obsolete", is(false)).
			body("entity.alleleSecondaryIds[0].createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.alleleSecondaryIds[0].updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.alleleSecondaryIds[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.alleleSecondaryIds[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString()));
	}
	
	@Test
	@Order(3)
	public void alleleBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_01_no_curie.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_02_no_taxon.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_03_no_allele_mutation_type_mutation_types.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_04_no_allele_symbol.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_05_no_allele_symbol_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_06_no_allele_full_name_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_07_no_allele_synonym_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_08_no_allele_symbol_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_09_no_allele_full_name_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_10_no_allele_synonym_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_11_no_allele_symbol_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_12_no_allele_full_name_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_13_no_allele_synonym_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_14_no_allele_secondary_id_secondary_id.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MR_15_no_allele_inheritance_mode_inheritance_mode.json");
	}
	
	@Test
	@Order(4)
	public void alleleBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_01_empty_curie.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_02_empty_taxon.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_03_empty_allele_mutation_type_mutation_types.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_04_empty_allele_symbol_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_05_empty_allele_full_name_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_06_empty_allele_synonym_display_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_07_empty_allele_symbol_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_08_empty_allele_full_name_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_09_empty_allele_synonym_format_text.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_10_empty_allele_symbol_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_11_empty_allele_full_name_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_12_empty_allele_synonym_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_13_empty_allele_secondary_id_secondary_id.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "ER_14_empty_allele_inheritance_mode_inheritance_mode.json");
	}
	
	@Test
	@Order(5)
	public void alleleBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_01_invalid_date_created.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_02_invalid_date_updated.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_03_invalid_taxon.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_04_invalid_in_collection.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_05_invalid_reference.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_06_invalid_allele_mutation_type_mutation_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_07_invalid_allele_mutation_type_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_08_invalid_allele_symbol_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_09_invalid_allele_full_name_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_10_invalid_allele_synonym_name_type.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_11_invalid_allele_symbol_synonym_scope.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_12_invalid_allele_full_name_synonym_scope.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_13_invalid_allele_synonym_synonym_scope.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_14_invalid_allele_symbol_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_15_invalid_allele_full_name_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_16_invalid_allele_synonym_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_17_invalid_allele_secondary_id_evidence.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_18_invalid_allele_inheritance_mode_inheritance_mode.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_19_invalid_allele_inheritance_mode_phenotype_term.json");
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "IV_20_invalid_allele_inheritance_mode_evidence.json");
	}
	
	@Test
	@Order(6)
	public void agmBulkUploadUnsupportedFields() throws Exception {
		checkFailedBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "US_01_unsupported_taxon.json");
	}

	@Test
	@Order(7)
	public void alleleBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "UM_01_update_no_non_required_fields_level_1.json");
		
		RestAssured.given().
			when().
			get(alleleGetEndpoint + "ALLELETEST:Allele0001").
			then().
			statusCode(200).
			body("entity.curie", is("ALLELETEST:Allele0001")).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("inCollection"))).
			body("entity", not(hasKey("references"))).
			body("entity", not(hasKey("alleleMutationTypes"))).
			body("entity", not(hasKey("alleleFullName"))).
			body("entity", not(hasKey("alleleSynonyms"))).
			body("entity", not(hasKey("alleleSecondaryIds"))).
			body("entity", not(hasKey("alleleInheritanceModes")));
	}

	@Test
	@Order(8)
	public void alleleBulkUploadUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "UM_02_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(alleleGetEndpoint + "ALLELETEST:Allele0001").then().
			statusCode(200).
			body("entity.curie", is("ALLELETEST:Allele0001")).
			body("entity.alleleMutationTypes[0]", not(hasKey("evidence"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("createdBy"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("updatedBy"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("dateCreated"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleSymbol", not(hasKey("synonymScope"))).
			body("entity.alleleSymbol", not(hasKey("synonymUrl"))).
			body("entity.alleleSymbol", not(hasKey("evidence"))).
			body("entity.alleleSymbol", not(hasKey("createdBy"))).
			body("entity.alleleSymbol", not(hasKey("updatedBy"))).
			body("entity.alleleSymbol", not(hasKey("dateCreated"))).
			body("entity.alleleSymbol", not(hasKey("dateUpdated"))).
			body("entity.alleleFullName", not(hasKey("synonymScope"))).
			body("entity.alleleFullName", not(hasKey("synonymUrl"))).
			body("entity.alleleFullName", not(hasKey("evidence"))).
			body("entity.alleleFullName", not(hasKey("createdBy"))).
			body("entity.alleleFullName", not(hasKey("updatedBy"))).
			body("entity.alleleFullName", not(hasKey("dateCreated"))).
			body("entity.alleleFullName", not(hasKey("dateUpdated"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.alleleSynonyms[0]", not(hasKey("evidence"))).
			body("entity.alleleSynonyms[0]", not(hasKey("createdBy"))).
			body("entity.alleleSynonyms[0]", not(hasKey("updatedBy"))).
			body("entity.alleleSynonyms[0]", not(hasKey("dateCreated"))).
			body("entity.alleleSynonyms[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("evidence"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("createdBy"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("updatedBy"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("dateCreated"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("evidence"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeTerm"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeStatement"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("createdBy"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("updatedBy"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("dateCreated"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(9)
	public void alleleBulkUploadUpdateEmptyNonRequiredFieldsLevel() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
		when().
		get(alleleGetEndpoint + "ALLELETEST:Allele0001").then().
		statusCode(200).
			body("entity.curie", is("ALLELETEST:Allele0001")).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("inheritanceMode"))).
			body("entity", not(hasKey("inCollection"))).
			body("entity", not(hasKey("references"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("evidence"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("createdBy"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("updatedBy"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("dateCreated"))).
			body("entity.alleleMutationTypes[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleSymbol", not(hasKey("synonymScope"))).
			body("entity.alleleSymbol", not(hasKey("synonymUrl"))).
			body("entity.alleleSymbol", not(hasKey("evidence"))).
			body("entity.alleleSymbol", not(hasKey("createdBy"))).
			body("entity.alleleSymbol", not(hasKey("updatedBy"))).
			body("entity.alleleSymbol", not(hasKey("dateCreated"))).
			body("entity.alleleSymbol", not(hasKey("dateUpdated"))).
			body("entity.alleleFullName", not(hasKey("synonymScope"))).
			body("entity.alleleFullName", not(hasKey("synonymUrl"))).
			body("entity.alleleFullName", not(hasKey("evidence"))).
			body("entity.alleleFullName", not(hasKey("createdBy"))).
			body("entity.alleleFullName", not(hasKey("updatedBy"))).
			body("entity.alleleFullName", not(hasKey("dateCreated"))).
			body("entity.alleleFullName", not(hasKey("dateUpdated"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.alleleSynonyms[0]", not(hasKey("evidence"))).
			body("entity.alleleSynonyms[0]", not(hasKey("createdBy"))).
			body("entity.alleleSynonyms[0]", not(hasKey("updatedBy"))).
			body("entity.alleleSynonyms[0]", not(hasKey("dateCreated"))).
			body("entity.alleleSynonyms[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("evidence"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("createdBy"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("updatedBy"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("dateCreated"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("dateUpdated"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("evidence"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeTerm"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("phenotypeStatement"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("createdBy"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("updatedBy"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("dateCreated"))).
			body("entity.alleleInheritanceModes[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(10)
	public void alleleBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MN_01_no_non_required_fields_level_1.json");
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "MN_02_no_non_required_fields_level_2.json");
	}

	@Test
	@Order(11)
	public void alleleBulkUploadEmptyNonRequiredFieldsLevel() throws Exception {
		checkSuccessfulBulkLoad(alleleBulkPostEndpoint, alleleTestFilePath + "EN_01_empty_non_required_fields.json");
	}
}
