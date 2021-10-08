package org.alliancegenome.curation_api.bulkupload;

import java.io.IOException;
import java.nio.file.*;

import org.alliancegenome.curation_api.resources.TestElasticSearchReourse;
import org.junit.jupiter.api.*;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchReourse.Initializer.class)
public class GeneBulkUploadITCase {

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
    }

    @Test
    public void geneBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene/bulk/bgifile?async=false").
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
               body("results[0].taxon", is("NCBITaxon:10090")).
               body("results[0].name", is( "Test gene 1")).
               body("results[0].synonyms[0].name", is("Test1")).
               body("results[0].synonyms[1].name", is("ExampleGene1")).
               body("results[0].crossReferences[0].curie", is("TEST:xref1b")).
               body("results[0].crossReferences[1].curie", is("TEST:xref1a")).
               body("results[0].symbol", is("Tg1")).
               body("results[0].geneSynopsis", is("Test gene with all fields populated")).
               body("results[0].geneSynopsisURL", is("http://test.org/test_synopsis_1")).
               body("results[0].type", is("SO:0001217"));
    }

    @Test
    public void geneBulkUploadNoCrossReferences() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/02_no_cross_references.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile?async=false").
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
                body("totalResults", is(2)).
                body("results", hasSize(2)).
                body("results[1].curie", is("TEST:TestGene00002")).
                body("results[1].taxon", is("NCBITaxon:10090")).
                body("results[1].name", is( "Test gene 2")).
                body("results[1].synonyms[0].name", is("Test2")).
                body("results[1].synonyms[1].name", is("ExampleGene2")).
                body("results[1].symbol", is("Tg2")).
                body("results[1].geneSynopsis", is("Test gene with all fields populated except crossReferences")).
                body("results[1].geneSynopsisURL", is("http://test.org/test_synopsis_2")).
                body("results[1].type", is("SO:0001217"));
    }

    @Test
    public void geneBulkUploadNoGenomeLocations() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/03_no_genome_locations.json"));

        // upload file
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile?async=false").
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
                body("totalResults", is(3)).
                body("results", hasSize(3)).
                body("results[2].curie", is("TEST:TestGene00003")).
                body("results[2].taxon", is("NCBITaxon:10090")).
                body("results[2].name", is( "Test gene 3")).
                body("results[2].synonyms[0].name", is("Test3")).
                body("results[2].synonyms[1].name", is("ExampleGene3")).
                body("results[2].symbol", is("Tg3")).
                body("results[2].geneSynopsis", is("Test gene with all fields populated except genomeLocations")).
                body("results[2].geneSynopsisURL", is("http://test.org/test_synopsis_3")).
                body("results[2].type", is("SO:0001217"));
    }

    @Test
    public void geneBulkUploadMany() throws IOException {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/00_mod_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene/bulk/bgifile?async=false").
            then().
            statusCode(200);

        // check if all the genes uploaded (605 + 1 from the test above)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(836));
    }
}
