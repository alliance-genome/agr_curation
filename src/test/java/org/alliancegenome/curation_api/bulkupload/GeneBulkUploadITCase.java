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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GeneBulkUploadITCase {

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
    }

    @Test
    @Order(1)
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
    @Order(2)
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
    @Order(3)
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
    @Order(4)
    public void geneBulkUploadNoSecondaryIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/05_no_secondary_ids.json"));

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
                body("totalResults", is(4)).
                body("results", hasSize(4)).
                body("results[3].curie", is("TEST:TestGene00005")).
                body("results[3].taxon", is("NCBITaxon:10090")).
                body("results[3].name", is( "Test gene 5")).
                body("results[3].symbol", is("Tg5")).
                body("results[3].geneSynopsis", is("Test gene with all fields populated except secondaryIds")).
                body("results[3].geneSynopsisURL", is("http://test.org/test_synopsis_5")).
                body("results[3].type", is("SO:0001217"));
    }

    @Test
    @Order(5)
    public void geneBulkUploadNoSynonyms() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/06_no_synonyms.json"));

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
                body("totalResults", is(5)).
                body("results", hasSize(5)).
                body("results[4].curie", is("TEST:TestGene00006")).
                body("results[4].taxon", is("NCBITaxon:10090")).
                body("results[4].name", is( "Test gene 6")).
                body("results[4].crossReferences[0].curie", is("TEST:xref6b")).
                body("results[4].crossReferences[1].curie", is("TEST:xref6a")).
                body("results[4].symbol", is("Tg6")).
                body("results[4].geneSynopsis", is("Test gene with all fields populated except synonyms")).
                body("results[4].geneSynopsisURL", is("http://test.org/test_synopsis_6")).
                body("results[4].type", is("SO:0001217"));
    }

    @Test
    @Order(6)
    public void geneBulkUploadNoTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/07_no_taxon_id.json"));

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
                body("totalResults", is(6)).
                body("results", hasSize(6)).
                body("results[5].curie", is("TEST:TestGene00007")).
                body("results[5].name", is( "Test gene 7")).
                body("results[5].synonyms[0].name", is("Test7")).
                body("results[5].synonyms[1].name", is("ExampleGene7")).
                body("results[5].crossReferences[0].curie", is("TEST:xref7a")).
                body("results[5].crossReferences[1].curie", is("TEST:xref7b")).
                body("results[5].symbol", is("Tg7")).
                body("results[5].geneSynopsis", is("Test gene with all fields populated except taxonId")).
                body("results[5].geneSynopsisURL", is("http://test.org/test_synopsis_7")).
                body("results[5].type", is("SO:0001217"));
    }

    @Test
    @Order(7)
    public void geneBulkUploadNoGeneSynopsis() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/08_no_gene_synopsis.json"));

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
                body("totalResults", is(7)).
                body("results", hasSize(7)).
                body("results[6].curie", is("TEST:TestGene00008")).
                body("results[6].name", is( "Test gene 8")).
                body("results[6].synonyms[0].name", is("Test8")).
                body("results[6].synonyms[1].name", is("ExampleGene8")).
                body("results[6].crossReferences[0].curie", is("TEST:xref8b")).
                body("results[6].crossReferences[1].curie", is("TEST:xref8a")).
                body("results[6].symbol", is("Tg8")).
                body("results[6].geneSynopsisURL", is("http://test.org/test_synopsis_8")).
                body("results[6].type", is("SO:0001217"));
    }

    @Test
    @Order(8)
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

        // check if all the genes uploaded (834 + 1 per each test above)
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=0").
                then().
                statusCode(200).
                body("totalResults", is(840));
    }
}
