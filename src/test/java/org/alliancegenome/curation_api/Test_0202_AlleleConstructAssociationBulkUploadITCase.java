package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
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


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("202 - Allele Construct Associations bulk upload")
@Order(202)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class Test_0202_AlleleConstructAssociationBulkUploadITCase extends BaseITCase {
	
	private Allele allele;
	private Construct construct;
	private String alleleCurie = "ALLELETEST:Allele0001";
	private String relationName = "contains";
	private String constructCurie = "WB:Construct0001";
	private String reference = "AGRKB:000000001";
	private String reference2 = "AGRKB:000000021";
	private String evidenceCodeCurie = "DATEST:Evidence0001";
	private String evidenceCodeCurie2 = "DATEST:Evidence0002";
	private String noteType = "comment";
	private String noteType2 = "remark";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String alleleConstructAssociationBulkPostEndpoint = "/api/alleleconstructassociation/bulk/WB/associationFile";
	private final String alleleConstructAssociationGetEndpoint = "/api/alleleconstructassociation/findBy";
	private final String alleleConstructAssociationTestFilePath = "src/test/resources/bulk/AA02_allele_construct_association/";
	private final String alleleGetEndpoint = "/api/allele/";

	private void loadRequiredEntities() throws Exception {
		allele = getAllele(alleleCurie);
		construct = getConstruct(constructCurie);
		Vocabulary alleleRelationVocabulary = getVocabulary(VocabularyConstants.ALLELE_RELATION_VOCABULARY);
		VocabularyTerm contains = createVocabularyTerm(alleleRelationVocabulary, "contains", false);
		createVocabularyTermSet(VocabularyConstants.ALLELE_CONSTRUCT_RELATION_VOCABULARY_TERM_SET, alleleRelationVocabulary, List.of(contains));
	}
	
	@Test
	@Order(1)
	public void alleleConstructAssociationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(alleleConstructAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&constructId=" + construct.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.alleleConstructAssociationObject.primaryExternalId", is(constructCurie)).
			body("entity.alleleAssociationSubject.primaryExternalId", is(alleleCurie)).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference)).
			body("entity.evidenceCode.curie", is(evidenceCodeCurie)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.relatedNote.internal", is(false)).
			body("entity.relatedNote.obsolete", is(true)).
			body("entity.relatedNote.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.relatedNote.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.relatedNote.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").toString())).
			body("entity.relatedNote.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").toString())).
			body("entity.relatedNote.freeText", is("Test note")).
			body("entity.relatedNote.noteType.name", is(noteType)).
			body("entity.relatedNote.references[0].curie", is(reference));
		
		RestAssured.given().
			when().
			get(alleleGetEndpoint + alleleCurie).
			then().
			statusCode(200).
			body("entity.alleleConstructAssociations", hasSize(1)).
			body("entity.alleleConstructAssociations[0].relation.name", is(relationName)).
			body("entity.alleleConstructAssociations[0].alleleConstructAssociationObject.primaryExternalId", is(constructCurie)).
			body("entity.alleleConstructAssociations[0].alleleAssociationSubject", not(hasKey("alleleConstructAssociations")));
	}
	
	@Test
	@Order(2)
	public void alleleConstructAssociationBulkUploadUpdateCheckFields() throws Exception {
		checkSuccessfulBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
	
		RestAssured.given().
			when().
			get(alleleConstructAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&constructId=" + construct.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relationName)).
			body("entity.alleleConstructAssociationObject.primaryExternalId", is(constructCurie)).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference2)).
			body("entity.evidenceCode.curie", is(evidenceCodeCurie2)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.relatedNote.internal", is(true)).
			body("entity.relatedNote.obsolete", is(false)).
			body("entity.relatedNote.updatedBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.relatedNote.createdBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.relatedNote.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").toString())).
			body("entity.relatedNote.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").toString())).
			body("entity.relatedNote.freeText", is("Test note 2")).
			body("entity.relatedNote.noteType.name", is(noteType2)).
			body("entity.relatedNote.references[0].curie", is(reference2));
		
		RestAssured.given().
			when().
			get(alleleGetEndpoint + alleleCurie).
			then().
			statusCode(200).
			body("entity.alleleConstructAssociations", hasSize(1));
	}
	
	@Test
	@Order(3)
	public void alleleConstructAssociationBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "MR_01_no_subject.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "MR_02_no_relation.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "MR_03_no_object.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "MR_04_no_related_note_note_type.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "MR_05_no_related_note_free_text.json");
	}
	
	@Test
	@Order(4)
	public void alleleConstructAssociationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "ER_02_empty_relation.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "ER_03_empty_object.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "ER_04_empty_related_note_note_type.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "ER_05_empty_related_note_free_text.json");
	}
	
	@Test
	@Order(5)
	public void alleleConstructAssociationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "IV_01_invalid_subject.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "IV_02_invalid_relation.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "IV_03_invalid_object.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "IV_04_invalid_date_created.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "IV_05_invalid_date_updated.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "IV_06_invalid_evidence_code.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "IV_07_invalid_evidence.json");
		checkFailedBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "IV_08_invalid_related_note_note_type.json");
	}
	
	@Test
	@Order(6)
	public void alleleConstructAssociationBulkUploadUpdateMissingNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
		checkSuccessfulBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "UM_01_update_no_non_required_fields_level_1.json");
		
		RestAssured.given().
			when().
			get(alleleConstructAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&constructId=" + construct.getId()).
			then().
			statusCode(200).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("evidenceCode"))).
			body("entity", not(hasKey("relatedNote")));
	}
	
	@Test
	@Order(7)
	public void alleleConstructAssociationBulkUploadUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
		checkSuccessfulBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "UM_02_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(alleleConstructAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&constructId=" + construct.getId()).
			then().
			statusCode(200).
			body("entity.relatedNote", not(hasKey("createdBy"))).
			body("entity.relatedNote", not(hasKey("updatedBy"))).
			body("entity.relatedNote", not(hasKey("evidence")));
	}
	
	@Test
	@Order(8)
	public void alleleConstructAssociationBulkUploadUpdateEmptyNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "UD_01_update_all_except_default_fields.json");
		checkSuccessfulBulkLoad(alleleConstructAssociationBulkPostEndpoint, alleleConstructAssociationTestFilePath + "UE_01_update_empty_non_required_fields.json");
		
		RestAssured.given().
			when().
			get(alleleConstructAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relationName + "&constructId=" + construct.getId()).
			then().
			statusCode(200).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("evidenceCode"))).
			body("entity.relatedNote", not(hasKey("createdBy"))).
			body("entity.relatedNote", not(hasKey("updatedBy"))).
			body("entity.relatedNote", not(hasKey("evidence")));
	}
	
}
