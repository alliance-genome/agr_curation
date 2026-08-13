package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;

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

/**
 * SCRUM-6258 - a sequence can keep its accession across assembly versions. The mitochondrion is
 * unchanged between GRCz11 and GRCz12tu, so ChromosomeAccessionEnum maps
 * ("MT", "GRCz11") and ("MT", "GRCz12tu") to the same RefSeq:NC_002333.2 (and likewise the rat
 * MT across mRatBN7.2 and GRCr8).
 *
 * AssemblyComponentService looks a component up per assembly but writes a primaryExternalId that
 * is globally unique on BiologicalEntity, so loading MT under the second assembly used to violate
 * biologicalentity_primaryexternalid_uk and fail the whole GFF load - which is what the ZFIN GFF
 * Transcript, CDS and Exon loads were hitting on beta.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("2003 - AssemblyComponentSharedAccessionITCase")
@Order(2003)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_2003_AssemblyComponentSharedAccessionITCase extends BaseITCase {

	private static final String MT_ACCESSION = "RefSeq:NC_002333.2";
	private static final String ZFIN_TAXON = "NCBITaxon:7955";
	private static final String GENE_CURIE = "ZFIN:ZDB-GENE-MTTEST";
	private static final String TRANSCRIPT_ID = "ZFIN:ZDB-TSCRIPT-MTTEST.1";

	private static final String GRCZ11_ENDPOINT = "/api/transcript/bulk/ZFIN_GRCz11/transcripts";
	private static final String GRCZ12TU_ENDPOINT = "/api/transcript/bulk/ZFIN_GRCz12tu/transcripts";
	private static final String FIXTURE = "src/test/resources/bulk/fms/08_gff_data/GFF_05_zfin_mt_transcript.json";
	private static final String CHR1_FIXTURE = "src/test/resources/bulk/fms/08_gff_data/GFF_06_zfin_chr1_transcript.json";
	private static final String CHR1_ACCESSION = "RefSeq:NC_133176.1";

	private static final String ASSEMBLY_COMPONENT_FIND = "/api/assemblycomponent/find?limit=10&page=0";
	private static final String TRANSCRIPT_GET = "/api/transcript/";

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
			.httpClient(HttpClientConfig.httpClientConfig()
				.setParam("http.socket.timeout", 100000)
				.setParam("http.connection.timeout", 100000));
	}

	private void loadRequiredEntities() {
		createSoTerm("SO:0000234", "mRNA", false);
		createGene(GENE_CURIE, ZFIN_TAXON,
			getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol"), false);
	}

	private HashMap<String, HashMap<String, Integer>> allCompleted() {
		HashMap<String, HashMap<String, Integer>> params = new HashMap<>();
		params.put("Entities", createCountParams(1, 0, 1, 0));
		params.put("Locations", createCountParams(1, 0, 1, 0));
		params.put("Associations", createCountParams(1, 0, 1, 0));
		return params;
	}

	// Baseline: MT loads cleanly under the assembly that first creates it.
	@Test
	@Order(1)
	public void mtLoadsUnderFirstAssembly() throws Exception {
		loadRequiredEntities();

		checkBulkLoadRecordCounts(GRCZ11_ENDPOINT, FIXTURE, allCompleted());

		RestAssured.given().
			contentType("application/json").
			body("{\"primaryExternalId\": \"" + MT_ACCESSION + "\" }").
			when().
			post(ASSEMBLY_COMPONENT_FIND).
			then().
			statusCode(200).
			body("returnedRecords", is(1)).
			body("results", hasSize(1)).
			body("results[0].name", is("MT")).
			body("results[0].genomeAssembly.primaryExternalId", is("GRCz11"));
	}

	// The regression: the same MT under a second assembly that shares the accession must not
	// collide on biologicalentity_primaryexternalid_uk. The existing component is reused and
	// re-pointed at the assembly being loaded, so there is still exactly one row for the accession.
	@Test
	@Order(2)
	public void mtLoadsUnderSecondAssemblySharingTheAccession() throws Exception {
		checkBulkLoadRecordCounts(GRCZ12TU_ENDPOINT, FIXTURE, allCompleted());

		RestAssured.given().
			contentType("application/json").
			body("{\"primaryExternalId\": \"" + MT_ACCESSION + "\" }").
			when().
			post(ASSEMBLY_COMPONENT_FIND).
			then().
			statusCode(200).
			// still a single component for the accession - no duplicate was created
			body("returnedRecords", is(1)).
			body("results", hasSize(1)).
			body("results[0].name", is("MT")).
			// and it now reports the assembly that was just loaded
			body("results[0].genomeAssembly.primaryExternalId", is("GRCz12tu"));

		RestAssured.given().
			when().
			get(TRANSCRIPT_GET + TRANSCRIPT_ID).
			then().
			statusCode(200).
			body("entity.transcriptGenomicLocationAssociations", hasSize(1)).
			body("entity.transcriptGenomicLocationAssociations[0].transcriptGenomicLocationAssociationObject.primaryExternalId", is(MT_ACCESSION)).
			body("entity.transcriptGenomicLocationAssociations[0].transcriptGenomicLocationAssociationObject.name", is("MT"));
	}

	// Re-running the same assembly takes the original per-assembly fast path, which the fix
	// leaves untouched.
	@Test
	@Order(3)
	public void reloadingTheSameAssemblyIsStableAndCreatesNoDuplicate() throws Exception {
		checkBulkLoadRecordCounts(GRCZ12TU_ENDPOINT, FIXTURE, allCompleted());

		RestAssured.given().
			contentType("application/json").
			body("{\"primaryExternalId\": \"" + MT_ACCESSION + "\" }").
			when().
			post(ASSEMBLY_COMPONENT_FIND).
			then().
			statusCode(200).
			body("returnedRecords", is(1)).
			body("results", hasSize(1)).
			body("results[0].genomeAssembly.primaryExternalId", is("GRCz12tu"));
	}

	// A chromosome whose accession is NOT shared between assemblies must still get its own new
	// component, so the accession-reuse branch cannot swallow genuinely new components. GRCz12tu
	// chromosome 1 has its own accession (RefSeq:NC_133176.1), unlike MT.
	@Test
	@Order(4)
	public void unsharedChromosomeStillCreatesItsOwnComponent() throws Exception {
		checkBulkLoadRecordCounts(GRCZ12TU_ENDPOINT, CHR1_FIXTURE, allCompleted());

		RestAssured.given().
			contentType("application/json").
			body("{\"primaryExternalId\": \"" + CHR1_ACCESSION + "\" }").
			when().
			post(ASSEMBLY_COMPONENT_FIND).
			then().
			statusCode(200).
			body("returnedRecords", is(1)).
			body("results", hasSize(1)).
			body("results[0].name", is("1")).
			body("results[0].genomeAssembly.primaryExternalId", is("GRCz12tu"));

		// and the MT component is untouched by that load - two distinct components now exist
		RestAssured.given().
			contentType("application/json").
			body("{\"primaryExternalId\": \"" + MT_ACCESSION + "\" }").
			when().
			post(ASSEMBLY_COMPONENT_FIND).
			then().
			statusCode(200).
			body("returnedRecords", is(1)).
			body("results[0].name", is("MT"));
	}
}
