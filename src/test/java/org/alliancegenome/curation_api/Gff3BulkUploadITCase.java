package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.is;

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
		createSoTerm("SO:0000147", "exon", false);
		createSoTerm("SO:0000316", "CDS", false);
	}
	
	@Test
	@Order(1)
	public void gff3DataBulkUploadTranscriptEntity() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(transcriptBulkPostEndpoint, gffDataTestFilePath + "GFF01_transcript.json", 3);
		
		RestAssured.given().
			when().
			get(transcriptGetEndpoint + transcriptId).
			then().
			statusCode(200).
			body("entity.modInternalId", is(transcriptId)).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB")).
			body("entity.transcriptType.curie", is("SO:0000234"));

	}
	
	@Test
	@Order(2)
	public void gff3DataBulkUploadExonEntity() throws Exception {
		checkSuccessfulBulkLoad(exonBulkPostEndpoint, gffDataTestFilePath + "GFF02_exon.json", 3);
		
		RestAssured.given().
			when().
			get(exonGetEndpoint + exonUniqueId).
			then().
			statusCode(200).
			body("entity.uniqueId", is(exonUniqueId)).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB"));

	}
	
	@Test
	@Order(2)
	public void gff3DataBulkUploadCodingSequenceEntity() throws Exception {
		checkSuccessfulBulkLoad(cdsBulkPostEndpoint, gffDataTestFilePath + "GFF03_CDS.json", 3);
		
		RestAssured.given().
			when().
			get(cdsGetEndpoint + cdsUniqueId).
			then().
			statusCode(200).
			body("entity.uniqueId", is(cdsUniqueId)).
			body("entity.taxon.curie", is("NCBITaxon:6239")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is("WB"));

	}

}
