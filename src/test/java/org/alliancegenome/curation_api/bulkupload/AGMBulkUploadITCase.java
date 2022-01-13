package org.alliancegenome.curation_api.bulkupload;

import java.io.IOException;
import java.nio.file.*;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
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
@Order(3)
public class AGMBulkUploadITCase {
    
    private final String TESTALLELE = "TEST:TestAllele00001";
    
    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
    }

    @Test
    @Order(1)
    public void agmBulkUploadMany() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/00_mod_examples.json"));
            
        Allele allele = createAllele(TESTALLELE, "NCBITaxon:10090");
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
                
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(613));
    }
    
    @Test
    @Order(2)
    public void agmBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/01_all_fields.json"));

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
            post("/api/agm/find?limit=10&page=61").
            then().
            statusCode(200).
            body("totalResults", is(614)).
            body("results", hasSize(4)).
            body("results[3].curie", is("TEST:TestAGM00001")).
            body("results[3].taxon", is("NCBITaxon:10090")).
            body("results[3].name", is( "Test AGM 1")).
            body("results[3].synonyms[0].name", is("TAGM1")).
            body("results[3].crossReferences[0].curie", is("TEST:TestAGM00001")).
            body("results[3].secondaryIdentifiers[0]", is("TEST:AGM1")).
            // body("results[3].components[0].allele.curie", is(TESTALLELE)).
            // body("results[3].components[0].zygosity", is("GENO:0000136")).
            // body("results[3].parentalPopulations[0]", is("TEST:TestComponent00001")).
            // body("results[3].sequenceTargetingReagents[0].curie", is("TEST:TestSTR00001")).
            body("results[3].subtype", is("genotype"));
    }
    
    @Test
    @Order(3)
    public void agmBulkUploadNoComponents() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/02_no_agm_components.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=61").
            then().
            statusCode(200).
            body("totalResults", is(615));
    }

    @Test
    @Order(4)
    public void agmBulkUploadNoComponentAlleleId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/03_no_agm_components_allele_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=61").
            then().
            statusCode(200).
            body("totalResults", is(615));
    }

    @Test
    @Order(5)
    public void agmBulkUploadNoComponentZygosity() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/04_no_agm_components_zygosity.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=61").
            then().
            statusCode(200).
            body("totalResults", is(615));
    }

    @Test
    @Order(6)
    public void agmBulkUploadNoCrossReference() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/05_no_cross_reference.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=61").
            then().
            statusCode(200).
            body("totalResults", is(616));
    }

    @Test
    @Order(7)
    public void agmBulkUploadNoCrossReferenceId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/06_no_cross_reference_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(500);
    }

    // TODO: adjust count (and subsequent test counts) once validation of xref pages in place
    @Test
    @Order(8)
    public void agmBulkUploadNoCrossReferencePages() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/07_no_cross_reference_pages.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(617));
    }

    @Test
    @Order(9)
    public void agmBulkUploadNoName() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/08_no_name.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(617)); // Name is required field so entity skipped in load
    }

    @Test
    @Order(10)
    public void agmBulkUploadNoParentalPopulationIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/09_no_parental_population_ids.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(618));
    }

    @Test
    @Order(11)
    public void agmBulkUploadNoPrimaryId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/10_no_primary_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=61").
            then().
            statusCode(200).
            body("totalResults", is(618)); // PrimaryID is required field so entity skipped in load
    }

    @Test
    @Order(12)
    public void agmBulkUploadNoSecondaryIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/11_no_secondary_ids.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(619));
    }

    @Test
    @Order(13)
    public void agmBulkUploadNoSequenceTargetingReagentIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/12_no_str_ids.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(620));
    }

    @Test
    @Order(14)
    public void agmBulkUploadNoSubtype() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/13_no_subtype.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(621));
    }

    @Test
    @Order(15)
    public void agmBulkUploadNoSynonyms() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/14_no_synonyms.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(622));
    }

    @Test
    @Order(16)
    public void agmBulkUploadNoTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/15_no_taxon_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(622)); // taxonId is a required field so entity skipped in load;
    }

    @Test
    @Order(17)
    public void agmBulkUploadInvalidComponentAllele() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/16_invalid_agm_components_allele_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(622));
    }

    @Test
    @Order(18)
    public void agmBulkUploadInvalidComponentZygosity() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/17_invalid_agm_components_allele_zygosity.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(622));
    }

    // TODO: adjust count (and subsequent test counts) once loading and validation of STRs in place
    @Test
    @Order(19)
    public void agmBulkUploadInvalidSequenceTargetingReagentId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/18_invalid_str_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(623));
    }

    @Test
    @Order(20)
    public void agmBulkUploadInvalidSubtype() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/19_invalid_subtype.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(400);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(623));
    }
    
    // TODO: adjust count (and subsequent test counts) once loading and validation of taxons in place
    @Test
    @Order(21)
    public void agmBulkUploadInvalidTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/20_invalid_taxon_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm/bulk/agmfile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/agm/find?limit=10&page=62").
            then().
            statusCode(200).
            body("totalResults", is(624));
    }
    
    // NOTE: this test needs to be run last to cleanup dummy allele
    @Test
    @Order(22)
    public void deleteDiseaseAnnotation() throws Exception {

        RestAssured.given().
            when().
            delete("/api/allele/" + TESTALLELE).
            then().
            statusCode(200);
    }

    private Allele createAllele(String curie, String taxon) {
        Allele biologicalEntity = new Allele();
        biologicalEntity.setCurie(curie);
        biologicalEntity.setTaxon(taxon);

        RestAssured.given().
                contentType("application/json").
                body(biologicalEntity).
                when().
                post("/api/allele").
                then().
                statusCode(200);
        return biologicalEntity;
    }
    
    
}
