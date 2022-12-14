package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneId;

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
@DisplayName("02 - Allele bulk upload")
@Order(2)
public class AlleleBulkUploadITCase extends BaseITCase {
	
	private String requiredReference = "AGRKB:000000001";
	private String requiredSoTerm = "SO:00001";
	
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
			body("results[0].dateUpdated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].alleleSymbol.displayText", is("Ta1")).
			body("results[0].alleleSymbol.formatText", is("Ta<sup>1</sup>")).
			body("results[0].alleleSymbol.synonymScope.name", is("exact")).
			body("results[0].alleleSymbol.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].alleleSymbol.nameType.name", is("nomenclature_symbol")).
			body("results[0].alleleSymbol.evidence[0].curie", is(requiredReference)).
			body("results[0].alleleFullName.displayText", is("Test allele 1")).
			body("results[0].alleleFullName.formatText", is("Test allele<sup>1</sup>")).
			body("results[0].alleleFullName.synonymScope.name", is("exact")).
			body("results[0].alleleFullName.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].alleleFullName.nameType.name", is("full_name")).
			body("results[0].alleleFullName.evidence[0].curie", is(requiredReference)).
			body("results[0].alleleSynonyms", hasSize(1)).
			body("results[0].alleleSynonyms[0].displayText", is("Test allele synonym 1")).
			body("results[0].alleleSynonyms[0].formatText", is("Test allele synonym <sup>1</sup>")).
			body("results[0].alleleSynonyms[0].synonymScope.name", is("exact")).
			body("results[0].alleleSynonyms[0].synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].alleleSynonyms[0].nameType.name", is("unspecified")).
			body("results[0].alleleSynonyms[0].evidence[0].curie", is(requiredReference)).
			body("results[0].alleleMutationTypes", hasSize(1)).
			body("results[0].alleleMutationTypes[0].evidence[0].curie", is(requiredReference)).
			body("results[0].alleleMutationTypes[0].mutationTypes[0].curie", is(requiredSoTerm)).
			body("results[0].alleleSecondaryIds", hasSize(1)).
			body("results[0].alleleSecondaryIds[0].evidence[0].curie", is(requiredReference)).
			body("results[0].alleleSecondaryIds[0].secondaryId", is("TEST:Secondary"));
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
	
	@Test
	@Order(42)
	public void alleleBulkUploadNoAlleleMutationTypeEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/42_no_allele_mutation_type_evidence.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
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
			body("results[0].curie", is("ALLELETEST:Allele0042"));
	}
	
	@Test
	@Order(43)
	public void alleleBulkUploadUpdateNoAlleleMutationTypeEvidence() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/43_update_no_allele_mutation_type_evidence.json"));
		
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
			body("results[0]", hasKey("alleleMutationTypes")).
			body("results[0].alleleMutationTypes[0]", not(hasKey("references")));
	}
	
	@Test
	@Order(44)
	public void alleleBulkUploadNoAlleleMutationTypeMutationTypes() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/44_no_allele_mutation_type_mutation_types.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
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
			body("totalResults", is(0));
	}
	
	@Test
	@Order(45)
	public void alleleBulkUploadInvalidAlleleMutationTypeMutationType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/45_invalid_allele_mutation_type_mutation_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
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
			body("totalResults", is(0));
	}
	
	@Test
	@Order(46)
	public void alleleBulkUploadInvalidAlleleMutationTypeEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/46_invalid_allele_mutation_type_evidence.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
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
			body("totalResults", is(0));
	}
	
	@Test
	@Order(47)
	public void alleleBulkUploadUpdateNoAlleleMutationTypes() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/47_update_no_allele_mutation_types.json"));
		
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
			body("results[0]", not(hasKey("alleleMutationTypes")));
	}
	
	@Test
	@Order(48)
	public void alleleBulkUploadNoAlleleSymbol() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/48_no_allele_symbol.json"));
		
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
	@Order(49)
	public void alleleBulkUploadNoAlleleSymbolDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/49_no_allele_symbol_display_text.json"));
		
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
	@Order(50)
	public void alleleBulkUploadNoAlleleSymbolFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/50_no_allele_symbol_format_text.json"));
		
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
	@Order(51)
	public void alleleBulkUploadNoAlleleSymbolNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/51_no_allele_symbol_name_type.json"));
		
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
	@Order(52)
	public void alleleBulkUploadNoAlleleSymbolSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/52_no_allele_symbol_synonym_scope.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0052"));
	}
	
	@Test
	@Order(53)
	public void alleleBulkUploadNoAlleleSymbolEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/53_no_allele_symbol_evidence.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0053"));
	}
	
	@Test
	@Order(54)
	public void alleleBulkUploadInvalidAlleleSymbolNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/54_invalid_allele_symbol_name_type.json"));
		
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
	@Order(55)
	public void alleleBulkUploadInvalidAlleleSymbolSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/55_invalid_allele_symbol_synonym_scope.json"));
		
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
	@Order(56)
	public void alleleBulkUploadInvalidAlleleSymbolEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/56_invalid_allele_symbol_evidence.json"));
		
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
	@Order(57)
	public void alleleBulkUploadUpdateNoAlleleSymbolSynonymScope() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/57_update_no_allele_symbol_synonym_scope.json"));
		
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
			body("results[0]", hasKey("alleleSymbol")).
			body("results[0].alleleSymbol", not(hasKey("synonymScope")));
	}
	
	@Test
	@Order(58)
	public void alleleBulkUploadUpdateNoAlleleSymbolEvidence() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/58_update_no_allele_symbol_evidence.json"));
		
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
			body("results[0]", hasKey("alleleSymbol")).
			body("results[0].alleleSymbol", not(hasKey("evidence")));
	}
	
	@Test
	@Order(59)
	public void alleleBulkUploadNoAlleleFullName() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/59_no_allele_full_name.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0059"));
	}
	
	@Test
	@Order(60)
	public void alleleBulkUploadNoAlleleFullNameDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/60_no_allele_full_name_display_text.json"));
		
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
	@Order(61)
	public void alleleBulkUploadNoAlleleFullNameFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/61_no_allele_full_name_format_text.json"));
		
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
	@Order(62)
	public void alleleBulkUploadNoAlleleFullNameNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/62_no_allele_full_name_name_type.json"));
		
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
	@Order(63)
	public void alleleBulkUploadNoAlleleFullNameSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/63_no_allele_full_name_synonym_scope.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0063"));
	}
	
	@Test
	@Order(64)
	public void alleleBulkUploadNoAlleleFullNameEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/64_no_allele_full_name_evidence.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0064"));
	}
	
	@Test
	@Order(65)
	public void alleleBulkUploadInvalidAlleleFullNameNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/65_invalid_allele_full_name_name_type.json"));
		
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
	@Order(66)
	public void alleleBulkUploadInvalidAlleleFullNameSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/66_invalid_allele_full_name_synonym_scope.json"));
		
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
	@Order(67)
	public void alleleBulkUploadInvalidAlleleFullNameEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/67_invalid_allele_full_name_evidence.json"));
		
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
	@Order(68)
	public void alleleBulkUploadUpdateNoAlleleFullNameSynonymScope() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/68_update_no_allele_full_name_synonym_scope.json"));
		
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
			body("results[0]", hasKey("alleleFullName")).
			body("results[0].alleleFullName", not(hasKey("synonymScope")));
	}
	
	@Test
	@Order(69)
	public void alleleBulkUploadUpdateNoAlleleFullNameEvidence() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/69_update_no_allele_full_name_evidence.json"));
		
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
			body("results[0]", hasKey("alleleFullName")).
			body("results[0].alleleFullName", not(hasKey("evidence")));
	}
	
	@Test
	@Order(70)
	public void alleleBulkUploadNoAlleleSynonym() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/70_no_allele_synonym.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0070"));
	}
	
	@Test
	@Order(71)
	public void alleleBulkUploadNoAlleleSynonymDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/71_no_allele_synonym_display_text.json"));
		
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
	@Order(72)
	public void alleleBulkUploadNoAlleleSynonymFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/72_no_allele_synonym_format_text.json"));
		
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
	@Order(73)
	public void alleleBulkUploadNoAlleleSynonymNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/73_no_allele_synonym_name_type.json"));
		
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
	@Order(74)
	public void alleleBulkUploadNoAlleleSynonymSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/74_no_allele_synonym_synonym_scope.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0074"));
	}
	
	@Test
	@Order(75)
	public void alleleBulkUploadNoAlleleSynonymEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/75_no_allele_synonym_evidence.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0075"));
	}
	
	@Test
	@Order(76)
	public void alleleBulkUploadInvalidAlleleSynonymNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/76_invalid_allele_synonym_name_type.json"));
		
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
	@Order(77)
	public void alleleBulkUploadInvalidAlleleSynonymSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/77_invalid_allele_synonym_synonym_scope.json"));
		
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
	@Order(78)
	public void alleleBulkUploadInvalidAlleleSynonymEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/78_invalid_allele_synonym_evidence.json"));
		
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
	@Order(79)
	public void alleleBulkUploadUpdateNoAlleleSynonymSynonymScope() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/79_update_no_allele_synonym_synonym_scope.json"));
		
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
			body("results[0]", hasKey("alleleSynonyms")).
			body("results[0].alleleSynonyms[0]", not(hasKey("synonymScope")));
	}
	
	@Test
	@Order(80)
	public void alleleBulkUploadUpdateNoAlleleSynonymEvidence() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/80_update_no_allele_synonym_evidence.json"));
		
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
			body("results[0]", hasKey("alleleSynonyms")).
			body("results[0].alleleSynonyms[0]", not(hasKey("evidence")));
	}
	
	@Test
	@Order(81)
	public void alleleBulkUploadUpdateNoAlleleSymbolSynonymUrl() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/81_update_no_allele_symbol_synonym_url.json"));
		
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
			body("results[0]", hasKey("alleleSymbol")).
			body("results[0].alleleSymbol", not(hasKey("synonymUrl")));
	}
	
	@Test
	@Order(82)
	public void alleleBulkUploadUpdateNoAlleleFullNameSynonymUrl() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/82_update_no_allele_full_name_synonym_url.json"));
		
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
			body("results[0]", hasKey("alleleFullName")).
			body("results[0].alleleFullName", not(hasKey("synonymUrl")));
	}
	
	@Test
	@Order(83)
	public void alleleBulkUploadUpdateNoAlleleSynonymSynonymUrl() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/83_update_no_allele_synonym_synonym_url.json"));
		
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
			body("results[0]", hasKey("alleleSynonyms")).
			body("results[0].alleleSynonyms[0]", not(hasKey("synonymUrl")));
	}
	
	@Test
	@Order(84)
	public void alleleBulkUploadEmptyAlleleSymbolDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/84_empty_allele_symbol_display_text.json"));
		
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
	@Order(85)
	public void alleleBulkUploadEmptyAlleleSymbolFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/85_empty_allele_symbol_format_text.json"));
		
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
	@Order(86)
	public void alleleBulkUploadEmptyAlleleFullNameDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/86_empty_allele_full_name_display_text.json"));
		
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
	@Order(87)
	public void alleleBulkUploadEmptyAlleleFullNameFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/87_empty_allele_full_name_format_text.json"));
		
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
	@Order(88)
	public void alleleBulkUploadEmptyAlleleSynonymDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/88_empty_allele_synonym_display_text.json"));
		
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
	@Order(89)
	public void alleleBulkUploadEmptyAlleleSynonymFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/89_empty_allele_synonym_format_text.json"));
		
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
	@Order(90)
	public void alleleBulkUploadNoAlleleSecondaryId() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/90_no_allele_secondary_id.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0090"));
	}
	
	@Test
	@Order(91)
	public void alleleBulkUploadUpdateNoAlleleSecondaryId() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/91_update_no_allele_secondary_id.json"));
		
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
			body("results[0]", not(hasKey("alleleSecondaryIds")));
	}
	
	@Test
	@Order(92)
	public void alleleBulkUploadNoAlleleSecondaryIdSecondaryId() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/92_no_allele_secondary_id_secondary_id.json"));
		
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
	@Order(93)
	public void alleleBulkUploadEmptyAlleleSecondaryIdSecondaryId() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/93_empty_allele_secondary_id_secondary_id.json"));
		
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
	@Order(94)
	public void alleleBulkUploadNoAlleleSecondaryIdEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/94_no_allele_secondary_id_evidence.json"));
		
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
			body("results[0].curie", is("ALLELETEST:Allele0094"));
	}
	
	@Test
	@Order(95)
	public void alleleBulkUploadUpdateNoAlleleSecondaryIdEvidence() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/95_update_no_allele_secondary_id_evidence.json"));
		
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
			body("results[0]", hasKey("alleleSecondaryIds")).
			body("results[0].alleleSecondaryIds[0]", not(hasKey("evidence")));
	}
	
	@Test
	@Order(96)
	public void alleleBulkUploadInvalidAlleleSecondaryIdEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/96_invalid_allele_secondary_id_evidence.json"));
		
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
	@Order(97)
	public void alleleBulkUploadUpdateNoAlleleFullName() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/97_update_no_allele_full_name.json"));
		
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
			body("results[0]", not(hasKey("alleleFullName")));
	}
	
	@Test
	@Order(98)
	public void alleleBulkUploadUpdateNoAlleleSynonym() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields_allele.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/allele/bulk/alleles").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/02_allele/98_update_no_allele_synonym.json"));
		
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
			body("results[0]", not(hasKey("alleleSynonyms")));
	}
	
	private void loadRequiredEntities() throws Exception {
		loadSOTerm(requiredSoTerm, "Test SOTerm");
	}
}
