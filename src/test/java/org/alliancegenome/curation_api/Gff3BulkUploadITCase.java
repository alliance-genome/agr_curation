package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
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
@DisplayName("608 - GFF data bulk upload - FMS")
@Order(608)
public class Gff3BulkUploadITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 100000)
					.setParam("http.connection.timeout", 100000));
	}

	private final String transcriptBulkPostEndpoint = "/api/transcript/bulk/WB_WBcel235/transcripts";
	private final String exonBulkPostEndpoint = "/api/exon/bulk/WB_WBcel235/exons";
	private final String cdsBulkPostEndpoint = "/api/cds/bulk/WB_WBcel235/codingSequences";
	private final String gffDataTestFilePath = "src/test/resources/bulk/fms/08_gff_data/";
	private final String transcriptGetEndpoint = "/api/transcript/";
	private final String exonGetEndpoint = "/api/exon/";
	private final String cdsGetEndpoint = "/api/cds/";
	private final String transcriptId = "WB:Y74C9A.2a.1";
	private final String exonUniqueId = "WB:Y74C9A.2a_exon|WB:Y74C9A.2a.1|I|1|100|+";
	private final String cdsUniqueId = "WB:Y74C9A.2a|WB:Y74C9A.2a.1|I|10|100|+";
	
	private void loadRequiredEntities() throws Exception {
		createSoTerm("SO:0000234", "mRNA", false);
		createSoTerm("SO:0001035", "piRNA", false);
		createSoTerm("SO:0000147", "exon", false);
		createSoTerm("SO:0000316", "CDS", false);
		createGene("WB:WBGene00022276", "NCBITaxon:6239", false, getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol"));
	}
	
	@Test
	@Order(1)
	public void gff3DataBulkUploadTranscriptEntity() throws Exception {
		loadRequiredEntities();
		
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "GFF_01_transcript.json", "Entities", 1, 0, 1);
		
		RestAssured.given().
			when().
			get(transcriptGetEndpoint + transcriptId).
			then().
			statusCode(200).
			body("entity.modInternalId", is(transcriptId)).
			body("entity.name", is("Y74C9A.2a.1")).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB")).
			body("entity.transcriptType.curie", is("SO:0000234")).
			body("entity.transcriptGenomicLocationAssociations", hasSize(1)).
			body("entity.transcriptGenomicLocationAssociations[0].relation.name", is("located_on")).
			body("entity.transcriptGenomicLocationAssociations[0].transcriptGenomicLocationAssociationObject.name", is("I")).
			body("entity.transcriptGenomicLocationAssociations[0].transcriptGenomicLocationAssociationObject.taxon.curie", is("NCBITaxon:6239")).
			body("entity.transcriptGenomicLocationAssociations[0].start", is(1)).
			body("entity.transcriptGenomicLocationAssociations[0].end", is(1000)).
			body("entity.transcriptGenomicLocationAssociations[0].phase", is(0)).
			body("entity.transcriptGenomicLocationAssociations[0].strand", is("+")).
			body("entity.transcriptGeneAssociations", hasSize(1)).
			body("entity.transcriptGeneAssociations[0].relation.name", is("is_child_of")).
			body("entity.transcriptGeneAssociations[0].transcriptGeneAssociationObject.modEntityId", is("WB:WBGene00022276"));

	}
	
	@Test
	@Order(2)
	public void gff3DataBulkUploadExonEntity() throws Exception {
		checkFailedBulkLoad(exonBulkPostEndpoint, gffDataTestFilePath + "GFF_02_exon.json", "Entities", 1, 0, 1);
		
		RestAssured.given().
			when().
			get(exonGetEndpoint + exonUniqueId).
			then().
			statusCode(200).
			body("entity.uniqueId", is(exonUniqueId)).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB")).
			body("entity.exonGenomicLocationAssociations", hasSize(1)).
			body("entity.exonGenomicLocationAssociations[0].relation.name", is("located_on")).
			body("entity.exonGenomicLocationAssociations[0].exonGenomicLocationAssociationObject.name", is("I")).
			body("entity.exonGenomicLocationAssociations[0].exonGenomicLocationAssociationObject.taxon.curie", is("NCBITaxon:6239")).
			body("entity.exonGenomicLocationAssociations[0].start", is(1)).
			body("entity.exonGenomicLocationAssociations[0].end", is(100)).
			body("entity.exonGenomicLocationAssociations[0].strand", is("+"));

		RestAssured.given().
			when().
			get(transcriptGetEndpoint + transcriptId).
			then().
			statusCode(200).
			body("entity.transcriptExonAssociations", hasSize(1)).
			body("entity.transcriptExonAssociations[0].relation.name", is("is_parent_of")).
			body("entity.transcriptExonAssociations[0].transcriptExonAssociationObject.uniqueId", is(exonUniqueId));
	}
	
	@Test
	@Order(3)
	public void gff3DataBulkUploadCodingSequenceEntity() throws Exception {
		checkFailedBulkLoad(cdsBulkPostEndpoint, gffDataTestFilePath + "GFF_03_CDS.json", "Entities", 1, 0, 1);
		
		RestAssured.given().
			when().
			get(cdsGetEndpoint + cdsUniqueId).
			then().
			statusCode(200).
			body("entity.uniqueId", is(cdsUniqueId)).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB")).
			body("entity.codingSequenceGenomicLocationAssociations", hasSize(1)).
			body("entity.codingSequenceGenomicLocationAssociations[0].relation.name", is("located_on")).
			body("entity.codingSequenceGenomicLocationAssociations[0].codingSequenceGenomicLocationAssociationObject.name", is("I")).
			body("entity.codingSequenceGenomicLocationAssociations[0].codingSequenceGenomicLocationAssociationObject.taxon.curie", is("NCBITaxon:6239")).
			body("entity.codingSequenceGenomicLocationAssociations[0].start", is(10)).
			body("entity.codingSequenceGenomicLocationAssociations[0].end", is(100)).
			body("entity.codingSequenceGenomicLocationAssociations[0].phase", is(1)).
			body("entity.codingSequenceGenomicLocationAssociations[0].strand", is("+"));

		RestAssured.given().
			when().
			get(transcriptGetEndpoint + transcriptId).
			then().
			statusCode(200).
			body("entity.transcriptCodingSequenceAssociations", hasSize(1)).
			body("entity.transcriptCodingSequenceAssociations[0].relation.name", is("is_parent_of")).
			body("entity.transcriptCodingSequenceAssociations[0].transcriptCodingSequenceAssociationObject.uniqueId", is(cdsUniqueId));

	}
	
	@Test
	@Order(4)
	public void gff3DataBulkUploadUpdateTranscriptEntity() throws Exception {
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "UD_01_update_transcript.json", "Entities", 1, 0, 1);
		
		RestAssured.given().
			when().
			get(transcriptGetEndpoint + transcriptId).
			then().
			statusCode(200).
			body("entity.modInternalId", is(transcriptId)).
			body("entity.name", is("Y74C9A.2a.1")).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB")).
			body("entity.transcriptType.curie", is("SO:0001035")).
			body("entity.transcriptGenomicLocationAssociations", hasSize(1)).
			body("entity.transcriptGenomicLocationAssociations[0].relation.name", is("located_on")).
			body("entity.transcriptGenomicLocationAssociations[0].transcriptGenomicLocationAssociationObject.name", is("II")).
			body("entity.transcriptGenomicLocationAssociations[0].transcriptGenomicLocationAssociationObject.taxon.curie", is("NCBITaxon:6239")).
			body("entity.transcriptGenomicLocationAssociations[0].start", is(2)).
			body("entity.transcriptGenomicLocationAssociations[0].end", is(2000)).
			body("entity.transcriptGenomicLocationAssociations[0].phase", is(1)).
			body("entity.transcriptGenomicLocationAssociations[0].strand", is("-"));

	}
	
	@Test
	@Order(5)
	public void gff3DataBulkUploadMissingRequiredFields() throws Exception {
		int total = 1;
		int failed = 0;
		int completed = 1;
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "MR_01_no_seq_id.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "MR_02_no_start.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "MR_03_no_end.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "MR_04_no_strand.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "MR_05_no_transcript_parent.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "MR_06_no_exon_parent.json", "Entities", 1, failed, 1);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "MR_07_no_cds_parent.json", "Entities", 1, failed, 1);
	}

	@Test
	@Order(6)
	public void gff3DataBulkUploadEmptyRequiredFields() throws Exception {
		int total = 1;
		int failed = 0;
		int completed = 1;
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "ER_01_empty_seq_id.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "ER_02_empty_strand.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "ER_03_empty_transcript_parent.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "ER_04_empty_exon_parent.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "ER_05_empty_cds_parent.json", "Entities", total, failed, completed);
	}

	@Test
	@Order(7)
	public void gff3DataBulkUploadInvalidFields() throws Exception {
		int total = 1;
		int failed = 0;
		int completed = 1;
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "IV_01_invalid_strand.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "IV_02_invalid_phase.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "IV_03_invalid_transcript_parent.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "IV_04_invalid_exon_parent.json", "Entities", total, failed, completed);
		checkFailedBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "IV_05_invalid_cds_parent.json", "Entities", total, failed, completed);
	}

}
