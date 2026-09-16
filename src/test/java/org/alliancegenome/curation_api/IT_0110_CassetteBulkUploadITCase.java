package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
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
 * SCRUM-6535: bulk load coverage for Cassette. Mirrors IT_0105_ConstructBulkUploadITCase.
 *
 * The relation vocabularies the cassette validators name do not exist in any environment yet, so
 * loadRequiredEntities creates them here, as the construct case does for its own.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("110 - Cassette bulk upload")
@Order(110)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0110_CassetteBulkUploadITCase extends BaseITCase {

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

	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm relation;
	private VocabularyTerm relation2;

	private final String bulkPostEndpoint = "/api/cassette/bulk/WB/cassettes";
	private final String bulkPostEndpointRGD = "/api/cassette/bulk/RGD/cassettes";
	private final String getEndpoint = "/api/cassette/";
	private final String testFilePath = "src/test/resources/bulk/10_cassette/";

	private void loadRequiredEntities() throws Exception {
		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		noteType = addVocabularyTermToSet(VocabularyConstants.CASSETTE_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET, "test_cassette_component_note", noteTypeVocabulary, false);
		noteType2 = addVocabularyTermToSet(VocabularyConstants.CASSETTE_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET, "test_cassette_component_summary", noteTypeVocabulary, false);
		addVocabularyTermToSet(VocabularyConstants.CASSETTE_NOTE_TYPES_VOCABULARY_TERM_SET, "test_cassette_note", noteTypeVocabulary, false);
		addVocabularyTermToSet(VocabularyConstants.CASSETTE_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET, "test_cassette_association_note", noteTypeVocabulary, false);

		Vocabulary relationVocabulary = createVocabulary(VocabularyConstants.CASSETTE_RELATION_VOCABULARY, false);
		relation = createVocabularyTerm(relationVocabulary, "is_regulated_by", false);
		relation2 = createVocabularyTerm(relationVocabulary, "targets", false);
		VocabularyTerm expresses = createVocabularyTerm(relationVocabulary, "expresses", false);
		createVocabularyTermSet(VocabularyConstants.CASSETTE_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, relationVocabulary, List.of(relation, relation2, expresses));
		createVocabularyTermSet(VocabularyConstants.CASSETTE_TRANSGENIC_TOOL_RELATION_VOCABULARY_TERM_SET, relationVocabulary, List.of(relation, relation2, expresses));
		createVocabularyTermSet(VocabularyConstants.CASSETTE_STR_RELATION_VOCABULARY_TERM_SET, relationVocabulary, List.of(expresses));

		createFbcvTerm("FBcv:0003010", "enhancer_trap", false);
		createFbcvTerm("FBcv:0003011", "protein_trap", false);
	}

	@Test
	@Order(1)
	public void cassetteBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();

		checkSuccessfulBulkLoad(bulkPostEndpoint, testFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			get(getEndpoint + "WB:Cassette0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:Cassette0001")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("CONSTRUCTTEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("CONSTRUCTTEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(reference)).
			body("entity.references[0].crossReferences[0].referencedCurie", is(referenceXref)).
			body("entity.cassetteSymbol.displayText", is("Cs1")).
			body("entity.cassetteSymbol.formatText", is("Cs<sup>1</sup>")).
			body("entity.cassetteSymbol.synonymScope.name", is("exact")).
			body("entity.cassetteSymbol.nameType.name", is("nomenclature_symbol")).
			body("entity.cassetteSymbol.evidence[0].curie", is(reference)).
			body("entity.cassetteFullName.displayText", is("Test cassette 1")).
			body("entity.cassetteFullName.nameType.name", is("full_name")).
			body("entity.cassetteSynonyms", hasSize(1)).
			body("entity.cassetteSynonyms[0].displayText", is("Test cassette synonym 1")).
			body("entity.cassetteComponents", hasSize(1)).
			body("entity.cassetteComponents[0].componentSymbol", is("ccmp1")).
			body("entity.cassetteComponents[0].relation.name", is(relation.getName())).
			body("entity.cassetteComponents[0].taxon.curie", is("NCBITaxon:9606")).
			body("entity.cassetteComponents[0].taxonText", is("Homo sapiens")).
			body("entity.cassetteComponents[0].relatedNotes", hasSize(1)).
			body("entity.cassetteComponents[0].relatedNotes[0].noteType.name", is(noteType.getName())).
			body("entity.cassetteComponents[0].relatedNotes[0].freeText", is("Test cassette component note")).
			body("entity.cassetteUses", hasSize(1)).
			body("entity.cassetteUses[0].uses", hasSize(1)).
			body("entity.cassetteUses[0].uses[0].curie", is("FBcv:0003010")).
			body("entity.dataProvider.abbreviation", is(dataProvider)).
			body("entity.secondaryIdentifiers", hasSize(1));
	}

	@Test
	@Order(2)
	public void cassetteBulkUploadUpdateFields() throws Exception {
		checkSuccessfulBulkLoad(bulkPostEndpointRGD, testFilePath + "UD_01_update_all_except_default_fields.json");

		RestAssured.given().
			when().
			get(getEndpoint + "WB:Cassette0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:Cassette0001")).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.references", hasSize(1)).
			body("entity.references[0].curie", is(reference2)).
			body("entity.cassetteSymbol.displayText", is("Cs1a")).
			body("entity.cassetteSymbol.nameType.name", is("systematic_name")).
			body("entity.cassetteFullName.displayText", is("Test cassette 1a")).
			body("entity.cassetteSynonyms", hasSize(1)).
			body("entity.cassetteSynonyms[0].displayText", is("Test cassette synonym 1a")).
			body("entity.cassetteComponents", hasSize(1)).
			body("entity.cassetteComponents[0].componentSymbol", is("ccmp2")).
			body("entity.cassetteComponents[0].relation.name", is(relation2.getName())).
			body("entity.cassetteComponents[0].taxon.curie", is("NCBITaxon:6239")).
			body("entity.cassetteComponents[0].relatedNotes", hasSize(1)).
			body("entity.cassetteComponents[0].relatedNotes[0].noteType.name", is(noteType2.getName())).
			body("entity.cassetteUses", hasSize(1)).
			body("entity.cassetteUses[0].uses[0].curie", is("FBcv:0003011")).
			body("entity.dataProvider.abbreviation", is(dataProvider2));
	}

	/**
	 * cassette_symbol_dto is required, where transgenic_tool_symbol_dto is not. MR_01 pins that a
	 * cassette with no symbol is rejected; IT_0109 pins the opposite for transgenic tools.
	 */
	@Test
	@Order(3)
	public void cassetteBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "MR_01_no_cassette_symbol.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "MR_02_no_cassette_component_component_symbol.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "MR_03_no_cassette_component_relation.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "MR_04_no_cassette_use_use_curies.json");
	}

	@Test
	@Order(4)
	public void cassetteBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_01_empty_cassette_component_component_symbol.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_02_empty_cassette_component_relation.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "ER_03_empty_cassette_use_use_curies.json");
	}

	@Test
	@Order(5)
	public void cassetteBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_01_invalid_cassette_component_relation.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_02_invalid_cassette_component_taxon.json");
		checkFailedBulkLoad(bulkPostEndpoint, testFilePath + "IV_03_invalid_cassette_use_use_curies.json");
	}

	@Test
	@Order(6)
	public void cassetteBulkUploadInvalidReferenceWarning() throws Exception {
		checkWarningBulkLoad(bulkPostEndpoint, testFilePath + "IV_04_invalid_reference.json");
	}

	@Test
	@Order(7)
	public void cassetteBulkUploadDuplicateNotes() throws Exception {
		checkSuccessfulBulkLoad(bulkPostEndpoint, testFilePath + "DN_01_duplicate_notes.json");

		RestAssured.given().
			when().
			get(getEndpoint + "WB:CassetteDN01").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:CassetteDN01")).
			body("entity.cassetteComponents", hasSize(1)).
			body("entity.cassetteComponents[0].relatedNotes", hasSize(1));
	}

	@Test
	@Order(8)
	public void reloadInitialCassette() throws Exception {
		// Required by the association cases, which resolve this record by identifier.
		checkSuccessfulBulkLoad(bulkPostEndpoint, testFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			get(getEndpoint + "WB:Cassette0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:Cassette0001"));
	}
}
