package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
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
	
	private String requiredReference = "AGRKB:000000001";
	private String requiredReferenceXref = "PMID:25920550";
	
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
			body("results[0].inheritanceMode.name", is("dominant")).
			body("results[0].inCollection.name", is("Million_mutations_project")).
			body("results[0].isExtinct", is(false)).
			body("results[0].references", hasSize(1)).
			body("results[0].references[0].curie", is(requiredReference)).
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
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0003"));
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
			body("totalResults", is(0));
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
			body("totalResults", is(0)); // Note, if alleles loaded this failure won't delete them as taxon ID is different
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
			body("totalResults", is(0));
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
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("ALLELETEST:Allele0017"));
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
	public void alleleBulkUploadNoInheritanceMode() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/23_no_inheritance_mode_allele.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0026"));
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
	public void alleleBulkUploadInvalidInheritanceMode() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/28_invalid_inheritance_mode_allele.json"));
		
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
	@Order(31)
	public void alleleBulkUploadInvalidReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/31_invalid_reference_allele.json"));
		
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
	@Order(32)
	public void alleleBulkUploadUpdateNoName() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/32_update_no_name_allele.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
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
			body("results[0]", not(hasKey("name")));
	}
	
	@Test
	@Order(33)
	public void alleleBulkUploadUpdateNoCreatedBy() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/33_update_no_created_by_allele.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
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
			body("results[0]", not(hasKey("createdBy")));
	}
	
	@Test
	@Order(34)
	public void alleleBulkUploadUpdateNoUpdatedBy() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/34_update_no_updated_by_allele.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
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
			body("results[0]", not(hasKey("updatedBy")));
	}
	
	@Test
	@Order(35)
	public void alleleBulkUploadUpdateNoDateCreated() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/35_update_no_date_created_allele.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
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
			body("results[0]", not(hasKey("dateCreated")));
	}
	
	@Test
	@Order(36)
	public void alleleBulkUploadUpdateNoDateUpdated() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/36_update_no_date_updated_allele.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
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
			body("results[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(37)
	public void alleleBulkUploadUpdateNoInheritanceMode() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/37_update_no_inheritance_mode_allele.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
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
			body("results[0]", not(hasKey("inheritanceMode")));
	}
	
	@Test
	@Order(38)
	public void alleleBulkUploadUpdateNoInCollection() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/38_update_no_in_collection_allele.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
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
			body("results[0]", not(hasKey("inCollection")));
	}
	
	@Test
	@Order(40)
	public void alleleBulkUploadUpdateNoIsExtinct() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/40_update_no_is_extinct_allele.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
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
			body("results[0]", not(hasKey("isExtinct")));
	}
	
	@Test
	@Order(41)
	public void alleleBulkUploadUpdateNoReferences() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/41_update_no_references_allele.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
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
			body("results[0]", not(hasKey("references")));
	}
	
	private void loadRequiredEntities() throws Exception {
		loadReference();
	}
	
	private void loadReference() throws Exception {
			
		CrossReference xref = new CrossReference();
		xref.setCurie(requiredReferenceXref);
		
		ObjectResponse<CrossReference> response = 
			RestAssured.given().
				contentType("application/json").
				body(xref).
				when().
				put("/api/cross-reference").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefCrossReference());
		
		Reference reference = new Reference();
		reference.setCurie(requiredReference);
		reference.setCrossReferences(List.of(response.getEntity()));
		reference.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(reference).
			when().
			put("/api/reference").
			then().
			statusCode(200);
	}
	
	private TypeRef<ObjectResponse<CrossReference>> getObjectResponseTypeRefCrossReference() {
		return new TypeRef<ObjectResponse <CrossReference>>() { };
	}
}
