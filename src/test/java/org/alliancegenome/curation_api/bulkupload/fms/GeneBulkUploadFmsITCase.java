package org.alliancegenome.curation_api.bulkupload.fms;

import java.io.IOException;
import java.nio.file.*;

import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.junit.jupiter.api.*;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("01 - Gene bulk upload - FMS")
@Order(1)
public class GeneBulkUploadFmsITCase {
    
    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 200000)
                    .setParam("http.connection.timeout", 200000));
    }

    @Test
    @Order(1)
    public void geneBulkUploadCheckFields() throws Exception {
        createSoTerm("SO:0001217", "protein_coding_gene");
        
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/01_all_fields.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene/bulk/bgifile").
            then().
            statusCode(200);

        // check if all the fields are correctly read
       RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/gene/find?limit=10&page=0").
            then().
            statusCode(200).
               body("totalResults", is(1)).
               body("results", hasSize(1)).
               body("results[0].curie", is("TEST:TestGene00001")).
               body("results[0].taxon.curie", is("NCBITaxon:10090")).
               body("results[0].name", is( "Test gene 1")).
               body("results[0].synonyms[0].name", is("Test1")).
               body("results[0].synonyms[1].name", is("ExampleGene1")).
               body("results[0].crossReferences[0].curie", is("TEST:xref1b")).
               body("results[0].crossReferences[1].curie", is("TEST:xref1a")).
               body("results[0].symbol", is("Tg1")).
               body("results[0].geneSynopsis", is("Test gene with all fields populated")).
               body("results[0].geneSynopsisURL", is("http://test.org/test_synopsis_1")).
               body("results[0].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(2)
    public void geneBulkUploadNoCrossReferences() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/02_no_cross_references.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00002"));
    }

    @Test
    @Order(3)
    public void geneBulkUploadNoGenomeLocations() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/03_no_genome_locations.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00003"));
    }

    @Test
    @Order(4)
    public void geneBulkUploadNoSecondaryIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/05_no_secondary_ids.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00005"));
    }

    @Test
    @Order(5)
    public void geneBulkUploadNoSynonyms() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/06_no_synonyms.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00006"));
    }

    @Test
    @Order(6)
    public void geneBulkUploadNoTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/07_no_taxon_id.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (failed load expected but no TaxonId => no entity removal, expect previously loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00006")); // no genes added
    }

    @Test
    @Order(7)
    public void geneBulkUploadNoGeneSynopsis() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/08_no_gene_synopsis.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00008"));
    }

    @Test
    @Order(8)
    public void geneBulkUploadNoGeneSynopsisURL() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/09_no_gene_synopsis_url.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00009"));
    }

    @Test
    @Order(9)
    public void geneBulkUploadNoName() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/10_no_name.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00010"));
    }

    @Test
    @Order(10)
    public void geneBulkUploadNoTermId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/11_no_so_term_id.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00011"));
    }

    @Test
    @Order(11)
    public void geneBulkUploadNoSymbol() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/12_no_symbol.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00012"));
    }

    @Test
    @Order(12)
    public void geneBulkUploadAdditionalField() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/13_additional_field.json"));

        // upload file and trigger Bad Request response with the additional field
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(400);
    }

    @Test
    @Order(13)
    public void geneBulkUploadNoPrimaryId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/04_no_primary_id.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(0)); // no genes added
    }

    @Test
    @Order(14)
    public void geneBulkUploadInvalidSoTermId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/14_invalid_so_term_id.json"));
        
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(0)); // no genes added
    }

    @Test
    @Order(15)
    public void geneBulkUploadInvalidTaxon() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/15_invalid_taxon_id.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(0)); // no genes added
    }

    @Test
    @Order(16)
    public void geneBulkUploadStartAfterEnd() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/16_start_after_end.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(0)); // no genes added
    }

    @Test
    @Order(17)
    public void geneBulkUploadInvalidStrand() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/17_invalid_strand.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(0)); // no genes added
    }

    @Test
    @Order(18)
    public void geneBulkUploadDuplicatedPrimaryIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/18_duplicate_primary_ids.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);

        // check load (expect loaded entity only)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00018")); // single gene added
    }
    
    private void createSoTerm(String curie, String name) {
        SOTerm soTerm = new SOTerm();
        soTerm.setCurie(curie);
        soTerm.setName(name);
        soTerm.setObsolete(false);

        RestAssured.given().
                contentType("application/json").
                body(soTerm).
                when().
                post("/api/soterm").
                then().
                statusCode(200);
    }

    
}
