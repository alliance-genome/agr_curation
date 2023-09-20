package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
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
@DisplayName("05 - Construct bulk upload")
@Order(5)
public class ConstructBulkUploadITCase extends BaseITCase {
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}
	
	private String reference = "AGRKB:000000001";
	private String referenceXref = "PMID:25920550";
	private String reference2 = "AGRKB:000000021";
	private String referenceXref2 = "PMID:25920551";
	private String dataProvider = "WB";
	private String dataProvider2 = "RGD";
	
	private final String constructBulkPostEndpoint = "/api/construct/bulk/WB/constructs";
	private final String constructBulkPostEndpointRGD = "/api/construct/bulk/RGD/constructs";
	private final String constructGetEndpoint = "/api/construct/findBy/";
	private final String constructTestFilePath = "src/test/resources/bulk/05_construct/";
	
	private void loadRequiredEntities() throws Exception {
	}

	@Test
	@Order(1)
	public void constructBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("WB:Construct0001")).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.name", is("cnst1")).
			body("entity.references[0].curie", is(reference)).
			body("entity.references[0].crossReferences[0].referencedCurie", is(referenceXref)).
			body("entity.constructComponents[0].componentSymbol", is("cmp1")).
			body("entity.constructComponents[0].taxon.curie", is("NCBITaxon:9606")).
			body("entity.constructComponents[0].taxonText", is("Homo sapiens")).
			body("entity.constructComponents[0].evidence[0].curie", is(reference)).
			body("entity.constructComponents[0].evidence[0].crossReferences[0].referencedCurie", is(referenceXref)).
			body("entity.constructComponents[0].internal", is(true)).
			body("entity.constructComponents[0].obsolete", is(true)).
			body("entity.constructComponents[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructComponents[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructComponents[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.constructComponents[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.constructComponents[0].relatedNotes[0].noteType.name", is("construct_component_note")).
			body("entity.constructComponents[0].relatedNotes[0].freeText", is("Test construct component note")).
			body("entity.constructComponents[0].relatedNotes[0].internal", is(true)).
			body("entity.constructComponents[0].relatedNotes[0].obsolete", is(true)).
			body("entity.constructComponents[0].relatedNotes[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructComponents[0].relatedNotes[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructComponents[0].relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.constructComponents[0].relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(2)
	public void constructBulkUploadUpdateFields() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpointRGD, constructTestFilePath + "UD_01_update_all_except_default_fields.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity.modEntityId", is("WB:Construct0001")).
			body("entity.taxon.curie", is("NCBITaxon:9606")).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.name", is("cnst2")).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(reference2)).
			body("entity.references[0].crossReferences[0].referencedCurie", is(referenceXref2)).
			body("entity.constructComponents", hasSize(1)).
			body("entity.constructComponents[0].componentSymbol", is("cmp2")).
			body("entity.constructComponents[0].taxon.curie", is("NCBITaxon:6239")).
			body("entity.constructComponents[0].taxonText", is("Caenorhabditis elegans")).
			body("entity.constructComponents[0].evidence[0].curie", is(reference2)).
			body("entity.constructComponents[0].evidence[0].crossReferences[0].referencedCurie", is(referenceXref2)).
			body("entity.constructComponents[0].internal", is(false)).
			body("entity.constructComponents[0].obsolete", is(false)).
			body("entity.constructComponents[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructComponents[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructComponents[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.constructComponents[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.constructComponents[0].relatedNotes", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].noteType.name", is("construct_component_summary")).
			body("entity.constructComponents[0].relatedNotes[0].freeText", is("Test construct component note 2")).
			body("entity.constructComponents[0].relatedNotes[0].internal", is(true)).
			body("entity.constructComponents[0].relatedNotes[0].obsolete", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.constructComponents[0].relatedNotes[0].updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.constructComponents[0].relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.constructComponents[0].relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST2:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage2"));
	}
	
	@Test
	@Order(3)
	public void constructBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_01_no_name.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_02_no_construct_component_component_symbol.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_03_no_construct_component_note_note_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_04_no_construct_component_note_free_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_05_no_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_06_no_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_07_no_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_08_no_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MR_09_no_data_provider_cross_reference_page_area.json");
	}
	
	@Test
	@Order(4)
	public void constructBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_01_empty_name.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_02_empty_construct_component_component_symbol.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_03_empty_construct_component_note_note_type.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_04_empty_construct_component_note_free_text.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_05_empty_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_06_empty_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_07_empty_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_08_empty_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "ER_09_empty_data_provider_cross_reference_page_area.json");
	}
	
	@Test
	@Order(5)
	public void constructBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_01_invalid_date_created.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_02_invalid_date_updated.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_03_invalid_taxon.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_04_invalid_reference.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_05_invalid_construct_component_evidence.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_06_invalid_construct_component_taxon.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_07_invalid_construct_component_note_note_type.json");;
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_08_invalid_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_09_invalid_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "IV_10_invalid_data_provider_cross_reference_page_area.json");
	}
	
	@Test
	@Order(6)
	public void constructBulkUploadUpdateMissingNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "UM_01_update_no_non_required_fields_level_1.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("taxon"))).
			body("entity", not(hasKey("constructComponents")));
	}

	@Test
	@Order(7)
	public void constructBulkUploadUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "UM_02_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity.constructComponents[0]", not(hasKey("taxon"))).
			body("entity.constructComponents[0]", not(hasKey("taxon_text"))).
			body("entity.constructComponents[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0]", not(hasKey("createdBy"))).
			body("entity.constructComponents[0]", not(hasKey("updatedBy"))).
			body("entity.constructComponents[0]", not(hasKey("dateCreated"))).
			body("entity.constructComponents[0]", not(hasKey("dateUpdated"))).
			body("entity.constructComponents[0]", not(hasKey("relatedNotes"))).
			body("entity.dataProvider", not(hasKey("crossReferences")));
	}
	
	@Test
	@Order(8)
	public void constructBulkUploadUpdateMissingNonRequiredFieldsLevel3() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "UM_03_update_no_non_required_fields_level_3.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("dateUpdated")));
	}

	@Test
	@Order(9)
	public void constructBulkUploadUpdateEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "AF_01_all_fields.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:Construct0001").
			then().
			statusCode(200).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("taxon"))).
			body("entity", not(hasKey("references"))).
			body("entity.constructComponents[0]", not(hasKey("taxon"))).
			body("entity.constructComponents[0]", not(hasKey("taxon_text"))).
			body("entity.constructComponents[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0]", not(hasKey("createdBy"))).
			body("entity.constructComponents[0]", not(hasKey("updatedBy"))).
			body("entity.constructComponents[0]", not(hasKey("dateCreated"))).
			body("entity.constructComponents[0]", not(hasKey("dateUpdated"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.constructComponents[0].relatedNotes[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(10)
	public void constructBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MN_01_no_non_required_fields_level_1.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MN_02_no_non_required_fields_level_2.json");
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "MN_03_no_non_required_fields_level_3.json");
	}

	@Test
	@Order(11)
	public void geneBulkUploadEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "EN_01_empty_non_required_fields.json");
	}
	
	@Test
	@Order(12)
	public void constructBulkUploadDuplicateNotes() throws Exception {
		checkSuccessfulBulkLoad(constructBulkPostEndpoint, constructTestFilePath + "DN_01_duplicate_notes.json");
		
		RestAssured.given().
			when().
			get(constructGetEndpoint + "WB:DN01").
			then().
			statusCode(200).
			body("entity.modEntityId", is("WB:DN01")).
			body("entity.constructComponents", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes", hasSize(1));
	}
}
