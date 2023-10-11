package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
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
@DisplayName("06 - Variant bulk upload")
@Order(6)
public class VariantBulkUploadITCase extends BaseITCase {
	
	private String variantType = "SO:VT001";
	private String variantType2 = "SO:VT002";
	private String variantStatus = "status_test";
	private String variantStatus2 = "status_test_2";
	private String sourceGeneralConsequence = "SO:SGC001";
	private String sourceGeneralConsequence2 = "SO:SGC002";
	private String reference = "AGRKB:000000001";
	private String reference2 = "AGRKB:000000021";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String variantBulkPostEndpoint = "/api/variant/bulk/WB/variants";
	private final String variantGetEndpoint = "/api/variant/";
	private final String variantTestFilePath = "src/test/resources/bulk/06_variant/";

	private void loadRequiredEntities() throws Exception {
		loadSOTerm(variantType, "Test variant type SOTerm");
		loadSOTerm(variantType2, "Second test variant type SOTerm");
		loadSOTerm(sourceGeneralConsequence, "Test source general consequence SOTerm");
		loadSOTerm(sourceGeneralConsequence2, "Second test source general consequence SOTerm");
		Vocabulary variantStatusVocabulary = getVocabulary(VocabularyConstants.VARIANT_STATUS_VOCABULARY);
		createVocabularyTerm(variantStatusVocabulary, variantStatus, false);
		createVocabularyTerm(variantStatusVocabulary, variantStatus2, false);
	}
	
	@Test
	@Order(1)
	public void variantBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(variantBulkPostEndpoint, variantTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(variantGetEndpoint + "VARIANTTEST:Variant0001").
			then().
			statusCode(200).
			body("entity.curie", is("VARIANTTEST:Variant0001")).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.variantType.curie", is(variantType)).
			body("entity.variantStatus.name", is(variantStatus)).
			body("entity.sourceGeneralConsequence.curie", is(sourceGeneralConsequence)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].obsolete", is(true)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].freeText", is("Test note")).
			body("entity.relatedNotes[0].noteType.name", is("comment")).
			body("entity.relatedNotes[0].references[0].curie", is(reference)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB")).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
}
