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

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
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
@DisplayName("01 - Gene bulk upload")
@Order(1)
public class GeneBulkUploadITCase {
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}
	
	private String requiredReference = "AGRKB:000000001";
	private String requiredReferenceXref = "PMID:25920550";

	@Test
	@Order(1)
	public void geneBulkUploadCheckFields() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
		
		loadRequiredEntities();
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].taxon.curie", is("NCBITaxon:6239")).
			body("results[0].internal", is(true)).
			body("results[0].obsolete", is(true)).
			body("results[0].createdBy.uniqueId", is("GENETEST:Person0001")).
			body("results[0].updatedBy.uniqueId", is("GENETEST:Person0002")).
			body("results[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].dateUpdated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].geneSymbol.displayText", is("Tg1")).
			body("results[0].geneSymbol.formatText", is("Tg<sup>1</sup>")).
			body("results[0].geneSymbol.synonymScope.name", is("exact")).
			body("results[0].geneSymbol.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].geneSymbol.nameType.name", is("nomenclature_symbol")).
			body("results[0].geneSymbol.evidence[0].curie", is(requiredReference)).
			body("results[0].geneFullName.displayText", is("Test gene 1")).
			body("results[0].geneFullName.formatText", is("Test gene<sup>1</sup>")).
			body("results[0].geneFullName.synonymScope.name", is("exact")).
			body("results[0].geneFullName.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].geneFullName.nameType.name", is("full_name")).
			body("results[0].geneFullName.evidence[0].curie", is(requiredReference)).
			body("results[0].geneSystematicName.displayText", is("Tg1.1")).
			body("results[0].geneSystematicName.formatText", is("Tg1.1")).
			body("results[0].geneSystematicName.synonymScope.name", is("exact")).
			body("results[0].geneSystematicName.synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].geneSystematicName.nameType.name", is("systematic_name")).
			body("results[0].geneSystematicName.evidence[0].curie", is(requiredReference)).
			body("results[0].geneSynonyms", hasSize(1)).
			body("results[0].geneSynonyms[0].displayText", is("Test gene synonym 1")).
			body("results[0].geneSynonyms[0].formatText", is("Test gene synonym <sup>1</sup>")).
			body("results[0].geneSynonyms[0].synonymScope.name", is("exact")).
			body("results[0].geneSynonyms[0].synonymUrl", is("https://alliancegenome.org/test")).
			body("results[0].geneSynonyms[0].nameType.name", is("unspecified")).
			body("results[0].geneSynonyms[0].evidence[0].curie", is(requiredReference));
	}
	
	@Test
	@Order(2)
	public void geneBulkUploadNoCurie() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/02_no_curie_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(5)
	public void geneBulkUploadNoTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/05_no_taxon_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(6)
	public void geneBulkUploadNoInternal() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/06_no_internal_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].curie", is("GENETEST:Gene0006")).
			body("results[0].internal", is(false));
	}
	
	@Test
	@Order(7)
	public void geneBulkUploadNoCreatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/07_no_created_by_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0007"));
	}
	
	@Test
	@Order(8)
	public void geneBulkUploadNoUpdatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/08_no_updated_by_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0008"));
	}
	
	@Test
	@Order(9)
	public void geneBulkUploadNoDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/09_no_date_created_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0009"));
	}
	
	@Test
	@Order(10)
	public void geneBulkUploadNoDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/10_no_date_updated_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0010"));
	}
	
	@Test
	@Order(11)
	public void geneBulkUploadInvalidTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/11_invalid_taxon_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)). 
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0010")); // Entry not loaded but existing not deleted
																			   // as different taxon ID
	}
	
	@Test
	@Order(12)
	public void geneBulkUploadInvalidDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/12_invalid_date_created_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(13)
	public void geneBulkUploadInvalidDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/13_invalid_date_updated_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
			
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(14)
	public void geneBulkUploadNoObsolete() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/14_no_obsolete_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].curie", is("GENETEST:Gene0014")).
			body("results[0].obsolete", is(false));
	}
	
	@Test
	@Order(15)
	public void geneBulkUploadEmptyDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/15_empty_date_created_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0015"));
	}
	
	@Test
	@Order(17)
	public void geneBulkUploadEmptyCurie() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/17_empty_curie_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(18)
	public void geneBulkUploadEmptyCreatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/18_empty_created_by_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0018"));
	}
	
	@Test
	@Order(19)
	public void geneBulkUploadEmptyUpdatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/19_empty_updated_by_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0019"));
	}
	
	@Test
	@Order(20)
	public void geneBulkUploadEmptyDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/20_empty_date_updated_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0020"));
	}
	
	@Test
	@Order(22)
	public void geneBulkUploadEmptyTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/22_empty_taxon_gene.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0020")); // Entry not loaded but existing not deleted
															   // as different taxon ID
	}
	
	@Test
	@Order(24)
	public void geneBulkUploadUpdateNoCreatedBy() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/24_update_no_created_by_gene.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0]", not(hasKey("createdBy")));
	}
	
	@Test
	@Order(25)
	public void geneBulkUploadUpdateNoUpdatedBy() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/25_update_no_updated_by_gene.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0]", not(hasKey("updatedBy")));
	}
	
	@Test
	@Order(26)
	public void geneBulkUploadUpdateNoDateCreated() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/26_update_no_date_created_gene.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0]", not(hasKey("dateCreated")));
	}
	
	@Test
	@Order(27)
	public void geneBulkUploadUpdateNoDateUpdated() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/27_update_no_date_updated_gene.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(28)
	public void geneBulkUploadNoGeneSymbol() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/28_no_gene_symbol.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(29)
	public void geneBulkUploadNoGeneFullName() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/29_no_gene_full_name.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0029"));
	}
	
	@Test
	@Order(30)
	public void geneBulkUploadNoGeneSystematicName() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/30_no_gene_systematic_name.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0030"));
	}
	
	@Test
	@Order(31)
	public void geneBulkUploadNoGeneSynonym() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/31_no_gene_synonym.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0031"));
	}
	
	@Test
	@Order(32)
	public void geneBulkUploadNoGeneSymbolDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/32_no_gene_symbol_display_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(33)
	public void geneBulkUploadNoGeneFullNameDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/33_no_gene_full_name_display_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(34)
	public void geneBulkUploadNoGeneSystematicNameDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/34_no_gene_systematic_name_display_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(35)
	public void geneBulkUploadNoGeneSynonymDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/35_no_gene_synonym_display_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(36)
	public void geneBulkUploadEmptyGeneSymbolDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/36_empty_gene_symbol_display_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(37)
	public void geneBulkUploadEmptyGeneFullNameDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/37_empty_gene_full_name_display_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(38)
	public void geneBulkUploadEmptyGeneSystematicNameDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/38_empty_gene_systematic_name_display_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(39)
	public void geneBulkUploadEmptyGeneSynonymDisplayText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/39_empty_gene_synonym_display_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(40)
	public void geneBulkUploadNoGeneSymbolFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/40_no_gene_symbol_format_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(41)
	public void geneBulkUploadNoGeneFullNameFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/41_no_gene_full_name_format_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(42)
	public void geneBulkUploadNoGeneSystematicNameFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/42_no_gene_systematic_name_format_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(43)
	public void geneBulkUploadNoGeneSynonymFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/43_no_gene_synonym_format_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(44)
	public void geneBulkUploadEmptyGeneSymbolFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/44_empty_gene_symbol_format_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(45)
	public void geneBulkUploadEmptyGeneFullNameFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/45_empty_gene_full_name_format_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(46)
	public void geneBulkUploadEmptyGeneSystematicNameFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/46_empty_gene_systematic_name_format_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(47)
	public void geneBulkUploadEmptyGeneSynonymFormatText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/47_empty_gene_synonym_format_text.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(48)
	public void geneBulkUploadNoGeneSymbolNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/48_no_gene_symbol_name_type.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(49)
	public void geneBulkUploadNoGeneFullNameNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/49_no_gene_full_name_name_type.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(50)
	public void geneBulkUploadNoGeneSystematicNameNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/50_no_gene_systematic_name_name_type.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(51)
	public void geneBulkUploadNoGeneSynonymNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/51_no_gene_synonym_name_type.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(52)
	public void geneBulkUploadInvalidGeneSymbolNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/52_invalid_gene_symbol_name_type.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(53)
	public void geneBulkUploadInvalidGeneFullNameNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/53_invalid_gene_full_name_name_type.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(54)
	public void geneBulkUploadInvalidGeneSystematicNameNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/54_invalid_gene_systematic_name_name_type.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(55)
	public void geneBulkUploadInvalidGeneSynonymNameType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/55_invalid_gene_synonym_name_type.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(56)
	public void geneBulkUploadNoGeneSymbolSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/56_no_gene_symbol_synonym_scope.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0056"));
	}
	
	@Test
	@Order(57)
	public void geneBulkUploadNoGeneFullNameSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/57_no_gene_full_name_synonym_scope.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0057"));
	}
	
	@Test
	@Order(58)
	public void geneBulkUploadNoGeneSystematicNameSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/58_no_gene_systematic_name_synonym_scope.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0058"));
	}
	
	@Test
	@Order(59)
	public void geneBulkUploadNoGeneSynonymSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/59_no_gene_synonym_synonym_scope.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0059"));
	}
	
	@Test
	@Order(60)
	public void geneBulkUploadInvalidGeneSymbolSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/60_invalid_gene_symbol_synonym_scope.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(61)
	public void geneBulkUploadInvalidGeneFullNameSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/61_invalid_gene_full_name_synonym_scope.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(62)
	public void geneBulkUploadInvalidGeneSystematicNameSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/62_invalid_gene_systematic_name_synonym_scope.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(63)
	public void geneBulkUploadInvalidGeneSynonymSynonymScope() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/63_invalid_gene_synonym_synonym_scope.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(64)
	public void geneBulkUploadUpdateNoGeneSymbolSynonymUrl() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/64_update_no_gene_symbol_synonym_url.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].geneSymbol", not(hasKey("synonymUrl")));
	}
	
	@Test
	@Order(65)
	public void geneBulkUploadUpdateNoGeneFullNameSynonymUrl() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/65_update_no_gene_full_name_synonym_url.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].geneFullName", not(hasKey("synonymUrl")));
	}
	
	@Test
	@Order(66)
	public void geneBulkUploadUpdateNoGeneSystematicNameSynonymUrl() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/66_update_no_gene_systematic_name_synonym_url.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].geneSystematicName", not(hasKey("synonymUrl")));
	}
	
	@Test
	@Order(67)
	public void geneBulkUploadUpdateNoGeneSynonymSynonymUrl() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/67_update_no_gene_synonym_synonym_url.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].geneSynonyms[0]", not(hasKey("synonymUrl")));
	}
	
	@Test
	@Order(68)
	public void geneBulkUploadNoGeneSymybolEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/68_no_gene_symbol_evidence.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0068"));
	}
	
	@Test
	@Order(69)
	public void geneBulkUploadNoGeneFullNameEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/69_no_gene_full_name_evidence.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0069"));
	}
	
	@Test
	@Order(70)
	public void geneBulkUploadNoGeneSystematicNameEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/70_no_gene_systematic_name_evidence.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0070"));
	}
	
	@Test
	@Order(71)
	public void geneBulkUploadNoGeneSynonymEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/71_no_gene_synonym_evidence.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0071"));
	}
	
	@Test
	@Order(72)
	public void geneBulkUploadInvalidGeneSymbolEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/72_invalid_gene_symbol_evidence.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(73)
	public void geneBulkUploadInvalidGeneFullNameEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/73_invalid_gene_full_name_evidence.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(74)
	public void geneBulkUploadInvalidGeneSystematicNameEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/74_invalid_gene_systematic_name_evidence.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(75)
	public void geneBulkUploadInvalidGeneSynonymEvidence() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/75_invalid_gene_synonym_evidence.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(76)
	public void geneBulkUploadUpdateNoGeneSymbolEvidence() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/76_update_no_gene_symbol_evidence.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].geneSymbol", not(hasKey("evidence")));
	}
	
	@Test
	@Order(77)
	public void geneBulkUploadUpdateNoGeneFullNameEvidence() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/77_update_no_gene_full_name_evidence.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].geneFullName", not(hasKey("evidence")));
	}
	
	@Test
	@Order(78)
	public void geneBulkUploadUpdateNoGeneSystematicNameEvidence() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/78_update_no_gene_systematic_name_evidence.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].geneSystematicName", not(hasKey("evidence")));
	}
	
	@Test
	@Order(67)
	public void geneBulkUploadUpdateNoGeneSynonymEvidence() throws Exception {
		String originalContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields_gene.json"));
	
		RestAssured.given().
			contentType("application/json").
			body(originalContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		String updateContent = Files.readString(Path.of("src/test/resources/bulk/01_gene/79_update_no_gene_synonym_evidence.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updateContent).
			when().
			post("/api/gene/bulk/genes").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].curie", is("GENETEST:Gene0001")).
			body("results[0].geneSynonyms[0]", not(hasKey("evidence")));
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
