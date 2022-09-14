package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
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
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("02 - Allele bulk upload")
@Order(2)
public class AlleleBulkUploadITCase {
	
	private String requiredInheritenceMode = "dominant";
	private String requiredInCollection = "Million mutations project";
	private String requiredSequencingStatus = "sequenced";
	private String requiredReference = "PMID:25920554";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	@Test
	@Order(1)
	public void alleleBulkUploadCheckFields() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
		
		loadRequiredEntities();
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0001")).
			body("results[0].name", is("TestAllele1")).
			body("results[0].symbol", is("ta1")).
			body("results[0].taxon.curie", is("NCBITaxon:6239")).
			body("results[0].internal", is(true)).
			body("results[0].obsolete", is(true)).
			body("results[0].inheritenceMode.name", is("dominant")).
			body("results[0].inCollection.name", is("Million mutation project")).
			body("results[0].sequencingStatus.name", is("sequence")).
			body("results[0].isExtinct", is(false)).
			body("results[0].references", hasSize(1)).
			body("results[0].references[0].curie", is("PMID:25920550")).
			body("results[0].createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("results[0].updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("results[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].dateUpdated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString()));
	}
	
	@Test
	@Order(2)
	public void alleleBulkUploadNoCurie() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/02_no_curie_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(3)
	public void alleleBulkUploadNoName() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/03_no_name_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(4)
	public void alleleBulkUploadNoSymbol() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/04_no_symbol_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0004"));
	}
	
	@Test
	@Order(5)
	public void alleleBulkUploadNoTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/05_no_taxon_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)). 
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0004")); // Entry not loaded but existing not deleted
																					// as different taxon ID
	}
	
	@Test
	@Order(6)
	public void alleleBulkUploadNoInternal() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/06_no_internal_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].curie", is("ALLELETEST:Allele0006")).
			body("results[0].internal", is(false));
	}
	
	@Test
	@Order(7)
	public void alleleBulkUploadNoCreatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/07_no_created_by_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0007"));
	}
	
	@Test
	@Order(8)
	public void alleleBulkUploadNoUpdatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/08_no_updated_by_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0008"));
	}
	
	@Test
	@Order(9)
	public void alleleBulkUploadNoDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/09_no_date_created_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0009"));
	}
	
	@Test
	@Order(10)
	public void alleleBulkUploadNoDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/10_no_date_updated_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0010"));
	}
	
	@Test
	@Order(11)
	public void alleleBulkUploadInvalidTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/11_invalid_taxon_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)). 
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0010")); // Entry not loaded but existing not deleted
																			   // as different taxon ID
	}
	
	@Test
	@Order(12)
	public void alleleBulkUploadInvalidDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/12_invalid_date_created_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(13)
	public void alleleBulkUploadInvalidDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/13_invalid_date_updated_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
			
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	
	@Test
	@Order(14)
	public void alleleBulkUploadNoObsolete() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/14_no_obsolete_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].curie", is("ALLELETEST:Allele0014")).
			body("results[0].obsolete", is(false));
	}
	
	@Test
	@Order(15)
	public void alleleBulkUploadEmptySymbol() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/15_empty_symbol_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0015"));
	}
	
	@Test
	@Order(16)
	public void alleleBulkUploadEmptyUpdatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/16_empty_updated_by_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0016"));
	}
	
	@Test
	@Order(17)
	public void alleleBulkUploadEmptyName() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/17_empty_name_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(18)
	public void alleleBulkUploadEmptyCreatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/18_empty_created_by_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0018"));
	}
	
	@Test
	@Order(19)
	public void alleleBulkUploadEmptyDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/19_empty_date_updated_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0019"));
	}
	
	@Test
	@Order(20)
	public void alleleBulkUploadEmptyDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/20_empty_date_created_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0020"));
	}
	
	@Test
	@Order(21)
	public void alleleBulkUploadEmptyCurie() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/21_empty_curie_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(22)
	public void alleleBulkUploadEmptyTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/22_empty_taxon_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(23)
	public void alleleBulkUploadNoInheritenceMode() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/23_no_inheritence_mode_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0023"));
	}
	
	@Test
	@Order(24)
	public void alleleBulkUploadNoCollection() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/24_no_in_collection_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0024"));
	}
	
	@Test
	@Order(25)
	public void alleleBulkUploadNoSequencingStatus() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/25_no_sequencing_status_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0025"));
	}
	
	@Test
	@Order(26)
	public void alleleBulkUploadNoIsExtinct() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/26_no_is_extinct_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0026")).
			body("results[0].isExtinct", is(false));
	}
	
	@Test
	@Order(27)
	public void alleleBulkUploadNoReferences() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/27_no_references_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0027"));
	}
	
	@Test
	@Order(28)
	public void alleleBulkUploadInvalidInheritenceMode() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/28_invalid_inheritence_mode_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(29)
	public void alleleBulkUploadInvalidInCollection() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/29_invalid_in_collection_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(30)
	public void alleleBulkUploadInvalidSequencingStatus() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/30_invalid_sequencing_status_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(31)
	public void alleleBulkUploadInvalidReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/30_invalid_reference_allele.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	private void loadRequiredEntities() throws Exception {
		loadReference();
		
		Vocabulary inheritenceModeVocabulary = createVocabulary(VocabularyConstants.ALLELE_INHERITENCE_MODE_VOCABULARY);
		Vocabulary inCollectionVocabulary = createVocabulary(VocabularyConstants.ALLELE_COLLECTION_VOCABULARY);
		Vocabulary sequencingStatusVocabulary = createVocabulary(VocabularyConstants.SEQUENCING_STATUS_VOCABULARY);
		createVocabularyTerm(inheritenceModeVocabulary, requiredInheritenceMode);
		createVocabularyTerm(inCollectionVocabulary, requiredInCollection);
		createVocabularyTerm(sequencingStatusVocabulary, requiredSequencingStatus);
	}
	
	private void loadReference() throws Exception {
			
		Reference reference = new Reference();
		reference.setCurie(requiredReference);
		reference.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(reference).
			when().
			put("/api/reference").
			then().
			statusCode(200);
	}
	
	private Vocabulary createVocabulary(String name) {
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.setName(name);
		vocabulary.setInternal(false);
		
		ObjectResponse<Vocabulary> response = 
			RestAssured.given().
				contentType("application/json").
				body(vocabulary).
				when().
				post("/api/vocabulary").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabulary());
		
		vocabulary = response.getEntity();
		
		return vocabulary;
	}

	private void createVocabularyTerm(Vocabulary vocabulary, String name) {
		VocabularyTerm vocabularyTerm = new VocabularyTerm();
		vocabularyTerm.setName(name);
		vocabularyTerm.setVocabulary(vocabulary);
		vocabularyTerm.setInternal(false);
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTerm).
				when().
				post("/api/vocabularyterm").
				then().
				statusCode(200);
	}
	
	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}
}
