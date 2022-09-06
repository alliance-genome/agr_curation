package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneId;

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
@DisplayName("03 - AGM bulk upload")
@Order(3)
public class AgmBulkUploadITCase {
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	@Test
	@Order(1)
	public void agmBulkUploadCheckFields() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/01_all_fields_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0001")).
			body("results[0].name", is("TestAgm1")).
			body("results[0].taxon.curie", is("NCBITaxon:6239")).
			body("results[0].internal", is(true)).
			body("results[0].obsolete", is(true)).
			body("results[0].createdBy.uniqueId", is("AGMTEST:Person0001")).
			body("results[0].updatedBy.uniqueId", is("AGMTEST:Person0002")).
			body("results[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].dateUpdated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString()));
	}
	
	@Test
	@Order(2)
	public void agmBulkUploadNoCurie() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/02_no_curie_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(3)
	public void agmBulkUploadNoName() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/03_no_name_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0003"));
	}
	
	@Test
	@Order(4)
	public void agmBulkUploadNoTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/04_no_taxon_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)). 
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0003")); // Entry not loaded but existing not deleted
			                                                             // as different taxon ID
	}
	
	@Test
	@Order(5)
	public void agmBulkUploadNoInternal() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/05_no_internal_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].curie", is("AGMTEST:Agm0005")).
			body("results[0].internal", is(false));
	}
	
	@Test
	@Order(6)
	public void agmBulkUploadNoCreatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/06_no_created_by_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0006"));
	}
	
	@Test
	@Order(7)
	public void agmBulkUploadNoUpdatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/07_no_updated_by_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0007"));
	}
	
	@Test
	@Order(8)
	public void agmBulkUploadNoDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/08_no_date_created_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0008"));
	}
	
	@Test
	@Order(9)
	public void agmBulkUploadNoDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/09_no_date_updated_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0009"));
	}
	
	@Test
	@Order(10)
	public void agmBulkUploadInvalidTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/10_invalid_taxon_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)). 
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0009")); // Entry not loaded but existing not deleted
			                                                             // as different taxon ID
	}
	
	@Test
	@Order(11)
	public void agmBulkUploadInvalidDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/11_invalid_date_created_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(12)
	public void agmBulkUploadInvalidDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/12_invalid_date_updated_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
		
			
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(13)
	public void agmBulkUploadNoObsolete() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/13_no_obsolete_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].curie", is("AGMTEST:Agm0013")).
			body("results[0].obsolete", is(false));
	}
	
	@Test
	@Order(14)
	public void agmBulkUploadEmptyDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/14_empty_date_updated_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0014"));
	}
	
	@Test
	@Order(15)
	public void agmBulkUploadEmptyCreatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/15_empty_created_by_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0015"));
	}
	
	@Test
	@Order(16)
	public void agmBulkUploadEmptyDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/16_empty_date_created_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0016"));
	}
	
	@Test
	@Order(17)
	public void agmBulkUploadEmptyUpdatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/17_empty_updated_by_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0017"));
	}
	
	@Test
	@Order(18)
	public void agmBulkUploadEmptyCurie() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/18_empty_curie_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(19)
	public void agmBulkUploadEmptyTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/19_empty_taxon_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(20)
	public void agmBulkUploadEmptyName() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/03_agm/20_empty_name_agm.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm/bulk/agms").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("AGMTEST:Agm0020"));
	}
}
