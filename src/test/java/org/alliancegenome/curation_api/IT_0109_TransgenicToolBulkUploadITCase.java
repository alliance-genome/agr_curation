package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.OffsetDateTime;

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

/**
 * SCRUM-6535: bulk load coverage for TransgenicTool. Mirrors IT_0105_ConstructBulkUploadITCase.
 *
 * Runs before the cassette case, which loads associations pointing at the tool created here.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("109 - Transgenic tool bulk upload")
@Order(109)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0109_TransgenicToolBulkUploadITCase extends BaseITCase {

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
	private String dataProvider = "WB";
	private String dataProvider2 = "RGD";

	private final String bulkPostEndpoint = "/api/transgenic-tool/bulk/WB/transgenicTools";
	private final String bulkPostEndpointRGD = "/api/transgenic-tool/bulk/RGD/transgenicTools";
	private final String getEndpoint = "/api/transgenic-tool/";
	private final String testFilePath = "src/test/resources/bulk/09_transgenic_tool/";

	private void loadRequiredEntities() throws Exception {
		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		addVocabularyTermToSet(VocabularyConstants.TRANSGENIC_TOOL_NOTE_TYPES_VOCABULARY_TERM_SET, "test_transgenic_tool_note", noteTypeVocabulary, false);
		// FBcv 'experimental_tool_descriptor' terms, the range of the uses slot.
		createFbcvTerm("FBcv:0003010", "enhancer_trap", false);
		createFbcvTerm("FBcv:0003011", "protein_trap", false);
	}

	@Test
	@Order(1)
	public void transgenicToolBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(bulkPostEndpoint, testFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			get(getEndpoint + "WB:TransgenicTool0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:TransgenicTool0001")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(reference)).
			body("entity.references[0].crossReferences[0].referencedCurie", is(referenceXref)).
			body("entity.transgenicToolSymbol.displayText", is("Tt1")).
			body("entity.transgenicToolSymbol.formatText", is("Tt<sup>1</sup>")).
			body("entity.transgenicToolSymbol.synonymScope.name", is("exact")).
			body("entity.transgenicToolSymbol.synonymUrl", is("https://alliancegenome.org/test")).
			body("entity.transgenicToolSymbol.nameType.name", is("nomenclature_symbol")).
			body("entity.transgenicToolSymbol.evidence[0].curie", is(reference)).
			body("entity.transgenicToolSymbol.internal", is(true)).
			body("entity.transgenicToolSymbol.obsolete", is(true)).
			body("entity.transgenicToolFullName.displayText", is("Test transgenic tool 1")).
			body("entity.transgenicToolFullName.formatText", is("Test transgenic tool<sup>1</sup>")).
			body("entity.transgenicToolFullName.nameType.name", is("full_name")).
			body("entity.transgenicToolSynonyms", hasSize(1)).
			body("entity.transgenicToolSynonyms[0].displayText", is("Test transgenic tool synonym 1")).
			body("entity.transgenicToolSynonyms[0].formatText", is("Test transgenic tool synonym <sup>1</sup>")).
			body("entity.transgenicToolUses", hasSize(1)).
			body("entity.transgenicToolUses[0].uses", hasSize(1)).
			body("entity.transgenicToolUses[0].uses[0].curie", is("FBcv:0003010")).
			body("entity.transgenicToolUses[0].evidence[0].curie", is(reference)).
			body("entity.crossReferences", hasSize(1)).
			body("entity.crossReferences[0].referencedCurie", is("TTTEST:xref0001")).
			body("entity.dataProvider.abbreviation", is(dataProvider)).
			body("entity.secondaryIdentifiers", hasSize(1));
	}

	@Test
	@Order(2)
	public void transgenicToolBulkUploadUpdateFields() throws Exception {
		checkSuccessfulBulkLoad(bulkPostEndpointRGD, testFilePath + "UD_01_update_all_except_default_fields.json");

		RestAssured.given().
			when().
			get(getEndpoint + "WB:TransgenicTool0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:TransgenicTool0001")).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(reference2)).
			body("entity.transgenicToolSymbol.displayText", is("Tt1a")).
			body("entity.transgenicToolSymbol.nameType.name", is("systematic_name")).
			body("entity.transgenicToolFullName.displayText", is("Test transgenic tool 1a")).
			body("entity.transgenicToolSynonyms", hasSize(1)).
			body("entity.transgenicToolSynonyms[0].displayText", is("Test transgenic tool synonym 1a")).
			body("entity.transgenicToolUses", hasSize(1)).
			body("entity.transgenicToolUses[0].uses[0].curie", is("FBcv:0003011")).
			body("entity.dataProvider.abbreviation", is(dataProvider2));
	}

	@Test
	@Order(3)
	public void transgenicToolBulkUploadMissingRequiredFields() throws Exception {
		// use_curies is required on a use annotation: one that names no use carries no information.
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "MR_01_no_transgenic_tool_use_use_curies.json");
	}

	@Test
	@Order(4)
	public void transgenicToolBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_01_empty_transgenic_tool_use_use_curies.json");
	}

	@Test
	@Order(5)
	public void transgenicToolBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_01_invalid_transgenic_tool_use_use_curies.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_02_invalid_transgenic_tool_symbol_name_type.json");
	}

	@Test
	@Order(6)
	public void transgenicToolBulkUploadInvalidReferenceWarning() throws Exception {
		checkWarningBulkLoad(bulkPostEndpoint, testFilePath + "IV_03_invalid_reference.json");
	}

	/**
	 * A transgenic tool symbol is NOT required, unlike a cassette symbol: LinkML puts a required flag
	 * on Cassette's symbol slot and none on TransgenicTool's. This pins that difference.
	 */
	@Test
	@Order(7)
	public void transgenicToolBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(bulkPostEndpoint, testFilePath + "MN_01_no_non_required_fields.json");
	}

	@Test
	@Order(8)
	public void reloadInitialTransgenicTool() throws Exception {
		// Required by the cassette transgenic tool association case, which resolves this record.
		checkSuccessfulBulkLoad(bulkPostEndpoint, testFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			get(getEndpoint + "WB:TransgenicTool0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:TransgenicTool0001"));
	}
}
