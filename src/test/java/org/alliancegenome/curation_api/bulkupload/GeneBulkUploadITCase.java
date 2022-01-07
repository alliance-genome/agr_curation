package org.alliancegenome.curation_api.bulkupload;

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
@Order(2)
public class GeneBulkUploadITCase {
    
    private SOTerm soTerm;
    
    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
    }

    @Test
    @Order(1)
    public void geneBulkUploadMany() throws IOException {
        soTerm = createSoTerm("SO:0001217", "protein_coding_gene");
        
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/00_mod_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene/bulk/bgifile").
            then().
            statusCode(200);

        // check if all the genes uploaded
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=0&page=0").
                then().
                statusCode(200).
                body("totalResults", is(833));
    }
    
    @Test
    @Order(2)
    public void geneBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/01_all_fields.json"));

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
            post("/api/gene/find?limit=10&page=83").
            then().
            statusCode(200).
               body("totalResults", is(834)).
               body("results", hasSize(4)).
               body("results[3].curie", is("TEST:TestGene00001")).
               body("results[3].taxon", is("NCBITaxon:10090")).
               body("results[3].name", is( "Test gene 1")).
               body("results[3].synonyms[0].name", is("Test1")).
               body("results[3].synonyms[1].name", is("ExampleGene1")).
               body("results[3].crossReferences[0].curie", is("TEST:xref1b")).
               body("results[3].crossReferences[1].curie", is("TEST:xref1a")).
               body("results[3].symbol", is("Tg1")).
               body("results[3].geneSynopsis", is("Test gene with all fields populated")).
               body("results[3].geneSynopsisURL", is("http://test.org/test_synopsis_1")).
               body("results[3].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(3)
    public void geneBulkUploadNoCrossReferences() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/02_no_cross_references.json"));

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
                post("/api/gene/find?limit=10&page=83").
                then().
                statusCode(200).
                body("totalResults", is(835)).
                body("results", hasSize(5)).
                body("results[4].curie", is("TEST:TestGene00002")).
                body("results[4].taxon", is("NCBITaxon:10090")).
                body("results[4].name", is( "Test gene 2")).
                body("results[4].synonyms[0].name", is("Test2")).
                body("results[4].synonyms[1].name", is("ExampleGene2")).
                body("results[4].symbol", is("Tg2")).
                body("results[4].geneSynopsis", is("Test gene with all fields populated except crossReferences")).
                body("results[4].geneSynopsisURL", is("http://test.org/test_synopsis_2")).
                body("results[4].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(4)
    public void geneBulkUploadNoGenomeLocations() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/03_no_genome_locations.json"));

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
                post("/api/gene/find?limit=10&page=83").
                then().
                statusCode(200).
                body("totalResults", is(836)).
                body("results", hasSize(6)).
                body("results[5].curie", is("TEST:TestGene00003")).
                body("results[5].taxon", is("NCBITaxon:10090")).
                body("results[5].name", is( "Test gene 3")).
                body("results[5].synonyms[0].name", is("Test3")).
                body("results[5].synonyms[1].name", is("ExampleGene3")).
                body("results[5].symbol", is("Tg3")).
                body("results[5].geneSynopsis", is("Test gene with all fields populated except genomeLocations")).
                body("results[5].geneSynopsisURL", is("http://test.org/test_synopsis_3")).
                body("results[5].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(5)
    public void geneBulkUploadNoSecondaryIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/05_no_secondary_ids.json"));

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
                post("/api/gene/find?limit=10&page=83").
                then().
                statusCode(200).
                body("totalResults", is(837)).
                body("results", hasSize(7)).
                body("results[6].curie", is("TEST:TestGene00005")).
                body("results[6].taxon", is("NCBITaxon:10090")).
                body("results[6].name", is( "Test gene 5")).
                body("results[6].symbol", is("Tg5")).
                body("results[6].geneSynopsis", is("Test gene with all fields populated except secondaryIds")).
                body("results[6].geneSynopsisURL", is("http://test.org/test_synopsis_5")).
                body("results[6].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(6)
    public void geneBulkUploadNoSynonyms() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/06_no_synonyms.json"));

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
                post("/api/gene/find?limit=10&page=83").
                then().
                statusCode(200).
                body("totalResults", is(838)).
                body("results", hasSize(8)).
                body("results[7].curie", is("TEST:TestGene00006")).
                body("results[7].taxon", is("NCBITaxon:10090")).
                body("results[7].name", is( "Test gene 6")).
                body("results[7].crossReferences[0].curie", is("TEST:xref6b")).
                body("results[7].crossReferences[1].curie", is("TEST:xref6a")).
                body("results[7].symbol", is("Tg6")).
                body("results[7].geneSynopsis", is("Test gene with all fields populated except synonyms")).
                body("results[7].geneSynopsisURL", is("http://test.org/test_synopsis_6")).
                body("results[7].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(7)
    public void geneBulkUploadNoTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/07_no_taxon_id.json"));

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
                post("/api/gene/find?limit=10&page=83").
                then().
                statusCode(200).
                body("totalResults", is(838)); // no genes added
    }

    @Test
    @Order(8)
    public void geneBulkUploadNoGeneSynopsis() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/08_no_gene_synopsis.json"));

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
                post("/api/gene/find?limit=10&page=83").
                then().
                statusCode(200).
                body("totalResults", is(839)).
                body("results", hasSize(9)).
                body("results[8].curie", is("TEST:TestGene00008")).
                body("results[8].name", is( "Test gene 8")).
                body("results[8].synonyms[0].name", is("Test8")).
                body("results[8].synonyms[1].name", is("ExampleGene8")).
                body("results[8].crossReferences[0].curie", is("TEST:xref8b")).
                body("results[8].crossReferences[1].curie", is("TEST:xref8a")).
                body("results[8].symbol", is("Tg8")).
                body("results[8].geneSynopsisURL", is("http://test.org/test_synopsis_8")).
                body("results[8].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(9)
    public void geneBulkUploadNoGeneSynopsisURL() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/09_no_gene_synopsis_url.json"));

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
                post("/api/gene/find?limit=10&page=83").
                then().
                statusCode(200).
                body("totalResults", is(840)).
                body("results", hasSize(10)).
                body("results[9].curie", is("TEST:TestGene00009")).
                body("results[9].taxon", is("NCBITaxon:10090")).
                body("results[9].name", is( "Test gene 9")).
                body("results[9].synonyms[0].name", is("Test9")).
                body("results[9].synonyms[1].name", is("ExampleGene9")).
                body("results[9].crossReferences[0].curie", is("TEST:xref9b")).
                body("results[9].crossReferences[1].curie", is("TEST:xref9a")).
                body("results[9].symbol", is("Tg9")).
                body("results[9].geneSynopsis", is("Test gene with all fields populated except geneSynopsisUrl")).
                body("results[9].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(10)
    public void geneBulkUploadNoName() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/10_no_name.json"));

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
                post("/api/gene/find?limit=10&page=84").
                then().
                statusCode(200).
                body("totalResults", is(841)).
                body("results", hasSize(1)).
                body("results[0].curie", is("TEST:TestGene00010")).
                body("results[0].taxon", is("NCBITaxon:10090")).
                body("results[0].synonyms[0].name", is("Test10")).
                body("results[0].synonyms[1].name", is("ExampleGene10")).
                body("results[0].crossReferences[0].curie", is("TEST:xref10a")).
                body("results[0].crossReferences[1].curie", is("TEST:xref10b")).
                body("results[0].symbol", is("Tg10")).
                body("results[0].geneSynopsis", is("Test gene with all fields populated except name")).
                body("results[0].geneSynopsisURL", is("http://test.org/test_synopsis_10")).
                body("results[0].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(11)
    public void geneBulkUploadNoTermId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/11_no_so_term_id.json"));

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
                post("/api/gene/find?limit=10&page=84").
                then().
                statusCode(200).
                body("totalResults", is(842)).
                body("results", hasSize(2)).
                body("results[1].curie", is("TEST:TestGene00011")).
                body("results[1].taxon", is("NCBITaxon:10090")).
                body("results[1].name", is( "Test gene 11")).
                body("results[1].synonyms[0].name", is("Test11")).
                body("results[1].synonyms[1].name", is("ExampleGene11")).
                body("results[1].crossReferences[0].curie", is("TEST:xref11b")).
                body("results[1].crossReferences[1].curie", is("TEST:xref11a")).
                body("results[1].symbol", is("Tg11")).
                body("results[1].geneSynopsis", is("Test gene with all fields populated except soTermId")).
                body("results[1].geneSynopsisURL", is("http://test.org/test_synopsis_11"));
    }

    @Test
    @Order(12)
    public void geneBulkUploadNoSymbol() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/12_no_symbol.json"));

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
                post("/api/gene/find?limit=10&page=84").
                then().
                statusCode(200).
                body("totalResults", is(843)).
                body("results", hasSize(3)).
                body("results[2].curie", is("TEST:TestGene00012")).
                body("results[2].taxon", is("NCBITaxon:10090")).
                body("results[2].name", is( "Test gene 12")).
                body("results[2].synonyms[0].name", is("Test12")).
                body("results[2].synonyms[1].name", is("ExampleGene12")).
                body("results[2].crossReferences[0].curie", is("TEST:xref12a")).
                body("results[2].crossReferences[1].curie", is("TEST:xref12b")).
                body("results[2].geneSynopsis", is("Test gene with all fields populated except symbol")).
                body("results[2].geneSynopsisURL", is("http://test.org/test_synopsis_12")).
                body("results[2].geneType.curie", is("SO:0001217"));
    }

    @Test
    @Order(13)
    public void geneBulkUploadAdditionalField() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/13_additional_field.json"));

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
    @Order(14)
    public void geneBulkUploadNoPrimaryId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/04_no_primary_id.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);
        
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=84").
                then().
                statusCode(200).
                body("totalResults", is(843)); // no genes added
    }

    // TODO: adapt & enable this test when checking for valid SO Terms in bulk upload is in place
    // NOTE: probably want a 200 response and test that entity count hasn't increased
    // @Test
    @Order(15)
    public void geneBulkUploadInvalidSoTermId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/14_invalid_so_term_id.json"));
        
        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(400);
    }

    
    // TODO: adapt & enable this test when checking for valid taxon IDs in bulk upload is in place
    // NOTE: probably want a 200 response and test that entity count hasn't increased
    // @Test
    @Order(16)
    public void geneBulkUploadInvalidTaxon() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/15_invalid_taxon_id.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(400);
    }

    @Test
    @Order(17)
    public void geneBulkUploadStartAfterEnd() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/16_start_after_end.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);
        
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=84").
                then().
                statusCode(200).
                body("totalResults", is(843)); // no genes added
    }

    @Test
    @Order(18)
    public void geneBulkUploadInvalidStrand() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/17_invalid_strand.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);
        
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=84").
                then().
                statusCode(200).
                body("totalResults", is(843)); // no genes added
    }

    @Test
    @Order(19)
    public void geneBulkUploadDuplicatedPrimaryIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/18_duplicate_primary_ids.json"));

        RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);
        
        RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/gene/find?limit=10&page=84").
                then().
                statusCode(200).
                body("totalResults", is(844)); // single gene added
    }
    
    private SOTerm createSoTerm(String curie, String name) {
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
        return soTerm;
    }

    
}
