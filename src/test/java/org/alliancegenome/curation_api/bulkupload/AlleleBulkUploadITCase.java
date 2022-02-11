package org.alliancegenome.curation_api.bulkupload;

import java.io.IOException;
import java.nio.file.*;

import org.alliancegenome.curation_api.model.entities.Gene;
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
public class AlleleBulkUploadITCase {
    
    private final String TESTGENE = "TEST:TestGene00001";
    
    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
    }

    @Test
    @Order(1)
    public void alleleBulkUploadMany() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/00_mod_examples.json"));
            
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
                
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(490));
    }
    
    @Test
    @Order(2)
    public void alleleBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/01_all_fields.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);

        // check if all the fields are correctly read
        // TODO: uncomment lines once appropriate fields loaded - will require load of STR
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(491)).
            body("results", hasSize(1)).
            body("results[0].curie", is("TEST:TestAllele00001")).
            body("results[0].taxon.curie", is("NCBITaxon:10090")).
            body("results[0].symbol", is("Test<sup>allele</sup>")).
            body("results[0].synonyms[0].name", is("test<sup>+</sup>")).
            body("results[0].crossReferences[0].curie", is("TEST:TestAllele00001")).
            body("results[0].secondaryIdentifiers[0]", is("TEST:A1")).
            body("results[0].description", is("Test description of allele"));
    }
    
    @Test
    @Order(3)
    public void alleleBulkUploadNoAlleleObjectRelations() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/02_no_allele_object_relations.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(492));
    }

    @Test
    @Order(4)
    public void alleleBulkUploadNoGeneAlleleObjectRelation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/03_no_gene_allele_object_relation.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(493));
    }

    @Test
    @Order(5)
    public void alleleBulkUploadNoConstructAlleleObjectRelation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/04_no_construct_allele_object_relation.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(494));
    }

    @Test
    @Order(6)
    public void alleleBulkUploadNoAlleleObjectRelationAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/05_no_allele_object_relation_association_type.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(495));
    }

    @Test
    @Order(7)
    public void alleleBulkUploadNoAlleleObjectRelationEntity() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/06_no_allele_object_relation_entity.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(495));
    }

    // TODO: adjust count (and subsequent test counts) once validation of xref pages in place
    @Test
    @Order(8)
    public void alleleBulkUploadNoCrossReferences() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/07_no_cross_references.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(496));
    }

    @Test
    @Order(9)
    public void alleleBulkUploadNoCrossReferenceId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/08_no_cross_reference_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
            RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/allele/find?limit=10&page=49").
                then().
                statusCode(200).
                body("totalResults", is(496)); // no entity added due to missing ID
    }

    @Test
    @Order(10)
    public void alleleBulkUploadNoCrossReferencePages() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/09_no_cross_reference_pages.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(497));
    }

    @Test
    @Order(11)
    public void alleleBulkUploadNoDescription() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/10_no_description.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(498)); // PrimaryID is required field so entity skipped in load
    }

    @Test
    @Order(12)
    public void alleleBulkUploadNoPrimaryId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/11_no_primary_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(498));
    }

    @Test
    @Order(13)
    public void alleleBulkUploadNoSymbol() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/12_no_symbol.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(498));
    }

    @Test
    @Order(14)
    public void alleleBulkUploadNoSymbolText() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/13_no_symbol_text.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(498));
    }

    @Test
    @Order(15)
    public void alleleBulkUploadNoTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/14_no_taxon_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(498));
    }

    @Test
    @Order(16)
    public void alleleBulkUploadNoSynonyms() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/15_no_synonyms.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(499)); // taxonId is a required field so entity skipped in load;
    }

    @Test
    @Order(17)
    public void alleleBulkUploadNoSecondaryIds() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/16_no_secondary_ids.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=49").
            then().
            statusCode(200).
            body("totalResults", is(500));
    }

    @Test
    @Order(18)
    public void alleleBulkUploadInvalidAlleleObjectRelationGene() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/17_invalid_allele_object_relation_gene.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=50").
            then().
            statusCode(200).
            body("totalResults", is(500));
    }

    // TODO: adjust count (and subsequent test counts) once loading and validation of components in place
    @Test
    @Order(19)
    public void alleleBulkUploadInvalidAlleleObjectRelationComponent() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/18_invalid_allele_object_relation_construct.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=50").
            then().
            statusCode(200).
            body("totalResults", is(501));
    }

    @Test
    @Order(20)
    public void alleleBulkUploadInvalidAlleleObjectRelationAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/19_invalid_allele_object_relation_association_type.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=50").
            then().
            statusCode(200).
            body("totalResults", is(501));
    }
    
    // NOTE: validation currently only based on regex, not DB lookup
    @Test
    @Order(21)
    public void alleleBulkUploadInvalidTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/20_invalid_taxon_id.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele/bulk/allelefile").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/allele/find?limit=10&page=50").
            then().
            statusCode(200).
            body("totalResults", is(501));
    }
    
}
