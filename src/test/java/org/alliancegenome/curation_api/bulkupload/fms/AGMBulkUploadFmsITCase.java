package org.alliancegenome.curation_api.bulkupload.fms;

import java.nio.file.*;

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
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("03 - AGM bulk upload - FMS")
@Order(3)
public class AGMBulkUploadFmsITCase {
    
    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
    }

    //@Test
    @Order(1)
    public void agmBulkUploadCheckFields() throws Exception {
        // Load required gene (objectRelation of Allele that is required as affectedGenomicModelComponents of AGM)
        String gene_content = Files.readString(Path.of("src/test/resources/bulk/fms/01_gene/01_all_fields.json"));

        RestAssured.given().
            contentType("application/json").
            body(gene_content).
            when().
            post("/api/gene/bulk/bgifile").
            then().
            statusCode(200);

        // Load required allele (affectedGenomicModelComponents of AGM)
        String allele_content = Files.readString(Path.of("src/test/resources/bulk/fms/02_allele/01_all_fields.json"));

        RestAssured.given().
            contentType("application/json").
            body(allele_content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);

        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/01_all_fields.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);

        // check if all the fields are correctly read
        // TODO: uncomment lines once appropriate fields loaded - will require load of STR
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00001")).
            body("results[0].taxon.curie", is("NCBITaxon:10090")).
            body("results[0].name", is( "Test AGM 1")).
            body("results[0].internal", is(false)).
            body("results[0].synonyms[0].name", is("TAGM1")).
            body("results[0].crossReferences[0].curie", is("TEST:TestAGM00001")).
            body("results[0].secondaryIdentifiers[0]", is("TEST:AGM1")).
            // body("results[0].components[0].allele.curie", is(TESTALLELE)).
            // body("results[0].components[0].zygosity", is("GENO:0000136")).
            // body("results[0].parentalPopulations[0]", is("TEST:TestComponent00001")).
            // body("results[0].sequenceTargetingReagents[0].curie", is("TEST:TestSTR00001")).
            body("results[0].subtype", is("genotype"));
    }
    
    //@Test
    @Order(2)
    public void agmBulkUploadNoComponents() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/02_no_agm_components.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (expect loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00002"));
    }

    //@Test
    @Order(3)
    public void agmBulkUploadNoComponentAlleleId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/03_no_agm_components_allele_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0));
    }

    //@Test
    @Order(4)
    public void agmBulkUploadNoComponentZygosity() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/04_no_agm_components_zygosity.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0));
    }

    //@Test
    @Order(5)
    public void agmBulkUploadNoCrossReference() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/05_no_cross_reference.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (expect loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00005"));
    }

    //@Test
    @Order(6)
    public void agmBulkUploadNoCrossReferenceId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/06_no_cross_reference_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); //no entity added due to missing ID
    }

    //@Test
    @Order(7)
    public void agmBulkUploadNoCrossReferencePages() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/07_no_cross_reference_pages.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (expect loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00007"));
    }

    //@Test
    @Order(8)
    public void agmBulkUploadNoName() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/08_no_name.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); // Name is required field so entity skipped in load
    }

    //@Test
    @Order(9)
    public void agmBulkUploadNoParentalPopulationIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/09_no_parental_population_ids.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (expect loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00009"));
    }

    //@Test
    @Order(10)
    public void agmBulkUploadNoPrimaryId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/10_no_primary_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); // PrimaryID is required field so entity skipped in load
    }

    //@Test
    @Order(11)
    public void agmBulkUploadNoSecondaryIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/11_no_secondary_ids.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (expect loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00011"));
    }

    //@Test
    @Order(12)
    public void agmBulkUploadNoSequenceTargetingReagentIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/12_no_str_ids.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (expect loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00012"));
    }

    //@Test
    @Order(13)
    public void agmBulkUploadNoSubtype() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/13_no_subtype.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (expect loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00013"));
    }

    //@Test
    @Order(14)
    public void agmBulkUploadNoSynonyms() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/14_no_synonyms.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (expect loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00014"));
    }

    //@Test
    @Order(15)
    public void agmBulkUploadNoTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/15_no_taxon_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (failed load expected but no TaxonId => no entity removal, expect previously loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAGM00014")); // taxonId is a required field so entity skipped in load;
    }

    //@Test
    @Order(16)
    public void agmBulkUploadInvalidComponentAllele() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/16_invalid_agm_components_allele_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0));
    }

    //@Test
    @Order(17)
    public void agmBulkUploadInvalidComponentZygosity() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/17_invalid_agm_components_allele_zygosity.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0));
    }

    // TODO: re-enable test once loading and validation of STRs in place
    // //@Test
    @Order(18)
    public void agmBulkUploadInvalidSequenceTargetingReagentId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/18_invalid_str_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (expect loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0));
    }

    //@Test
    @Order(19)
    public void agmBulkUploadInvalidSubtype() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/19_invalid_subtype.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(400);
        
        // check entity count (failed load expected => 0 entities in DB after load)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0));
    }
    
    // NOTE: validation currently only based on regex, not DB lookup
    //@Test
    @Order(20)
    public void agmBulkUploadInvalidTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/fms/03_affected_genomic_model/20_invalid_taxon_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check load (failed load expected but invalid TaxonId => no entity removal, expect previously loaded entity only)
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0));
    }   
}
