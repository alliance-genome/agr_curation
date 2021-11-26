package org.alliancegenome.curation_api.bulkupload;

import java.io.IOException;
import java.nio.file.*;

import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.junit.jupiter.api.*;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("04 - Disease annotation bulk upload")
public class DiseaseAnnotationBulkUploadITCase {

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
    }

    @Test
    @Order(1)
    public void geneBulkUpload() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/01_gene/00_mod_examples.json"));
        
            // upload file
            RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/gene/bulk/bgifile?async=false").
                then().
                statusCode(200);
            
            // check entity count
               RestAssured.given().
                    when().
                    header("Content-Type", "application/json").
                    body("{}").
                    post("/api/gene/find?limit=10&page=0").
                    then().
                    statusCode(200).
                       body("totalResults", is(833));
    }

    @Test
    @Order(2)
    public void alleleBulkUpload() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/02_allele/00_mod_examples.json"));
        
            // upload file
            RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/allele/bulk/allelefile?async=false").
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
    @Order(3)
    public void agmBulkUpload() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/03_affected_genomic_model/00_mod_examples.json"));
        
            // upload file
            RestAssured.given().
                contentType("application/json").
                body(content).
                when().
                post("/api/agm/bulk/agmfile?async=false").
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
    @Order(4)
    public void diseaseAnnotationBulkUploadFB() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/00_FB_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/fbAnnotationFile").
            then().
            statusCode(200);

        // check entity count
       RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
               body("totalResults", is(40));
    }

    @Test
    @Order(5)
    public void diseaseAnnotationBulkUploadHuman() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/00_HUMAN_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/humanAnnotationFile").
            then().
            statusCode(200);

        // check entity count
       RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
               body("totalResults", is(100)); // expect 102 if 'with' field added to human curie generator
    }

    @Test
    @Order(6)
    public void diseaseAnnotationBulkUploadMGI() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/00_MGI_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/mgiAnnotationFile").
            then().
            statusCode(200);

        // check entity count
       RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
               body("totalResults", is(159)); // annotation with MGI:5431572 as subject not loaded as not in AGM file
    }

    @Test
    @Order(7)
    public void diseaseAnnotationBulkUploadRGD() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/00_RGD_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/rgdAnnotationFile").
            then().
            statusCode(200);

        // check entity count
       RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
               body("totalResults", is(313));
    }

    @Test
    @Order(8)
    public void diseaseAnnotationBulkUploadSGD() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/00_SGD_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/sgdAnnotationFile").
            then().
            statusCode(200);

        // check entity count
       RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
               body("totalResults", is(353));
    }

    @Test
    @Order(9)
    public void diseaseAnnotationBulkUploadWB() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/00_WB_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/wbAnnotationFile").
            then().
            statusCode(200);

        // check entity count
       RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
               body("totalResults", is(484)); // Should be 494 once curie components changed to ensure unique annotations
    }

    @Test
    @Order(10)
    public void diseaseAnnotationBulkUploadZFIN() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/00_ZFIN_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/zfinAnnotationFile").
            then().
            statusCode(200);

        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
               body("totalResults", is(504));
    }
    
    @Test
    @Order(11)
    public void geneDiseaseAnnotationBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_primary_annotation.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/fbAnnotationFile").
            then().
            statusCode(200);
    
        
        // check entity count and fields correctly read
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=46").
            then().
            statusCode(200).
            body("totalResults", is(465)). // 40 FB annotations replaced with 1
            body("results", hasSize(5)).
            body("results[4].subject.curie", is("FB:FBgn0260936")).
            body("results[4].object.curie", is("DOID:4")).
            body("results[4].diseaseRelation", is("is_implicated_in")).
            // body("results[4].conditionRelations", hasSize(1)).
            // body("results[4].conditionRelations[0].conditionRelationType", is("induces")).
            // body("results[4].conditionRelations[0].conditions", hasSize(1)).
            // body("results[4].conditionRelations[0].conditions[0].conditionClass.curie", is("ZECO:0000101")).
            // body("results[4].conditionRelations[0].conditions[0].conditionId.curie", is("XCO:0000131")).
            // body("results[4].conditionRelations[0].conditions[0].conditionStatement", is("chemical treatment:4-nitroquinoline N-oxide")).
            // body("results[4].conditionRelations[0].conditions[0].conditionQuantity", is("some amount")).
            // body("results[4].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("ZFA:0000001")).
            // body("results[4].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("GO:0007569")).
            // body("results[4].conditionRelations[0].conditions[0].conditionTaxon", is("NCBITaxon:1781")).
            // body("results[4].conditionRelations[0].conditions[0].conditionChemical.curie", is("CHEBI:46631")).
            body("results[4].negated", is(true)).
            // body("results[4].with", containsInAnyOrder("HGNC:1121", "HGNC:323")).
            // body("results[4].evidenceCodes", hasSize(1)).
            // body("results[4].evidenceCodes[0].curie", is("ECO:0000033")).
            body("results[4].referenceList", hasSize(1)).
            body("results[4].referenceList[0].curie", is("PMID:25920554"));
    }
    
    @Test
    @Order(12)
    public void geneDiseaseAnnotationBulkUploadSecondaryAnnotation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/02_all_fields_gene_secondary_annotation.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/mgiAnnotationFile").
            then().
            statusCode(200);
    
        
        // check entity count and fields correctly read
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(406)); // 59 MGI annotations replaced with 0 as secondary annotations ignored
    }
    
    @Test
    @Order(13)
    public void agmDiseaseAnnotationBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/03_all_fields_agm_primary_annotation.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/wbAnnotationFile").
            then().
            statusCode(200);
    
        
        // check entity count and fields correctly read
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=27").
            then().
            statusCode(200).
            body("totalResults", is(276)). // 131 WB annotations replaced with 1
            body("results", hasSize(6)).
            body("results[5].subject.curie", is("WB:WBStrain00005113")).
            body("results[5].object.curie", is("DOID:4")).
            body("results[5].diseaseRelation", is("is_model_of")).
            // body("results[5].conditionRelations", hasSize(1)).
            // body("results[5].conditionRelations[0].conditionRelationType", is("induces")).
            // body("results[5].conditionRelations[0].conditions", hasSize(1)).
            // body("results[5].conditionRelations[0].conditions[0].conditionClass.curie", is("ZECO:0000101")).
            // body("results[5].conditionRelations[0].conditions[0].conditionId.curie", is("XCO:0000131")).
            // body("results[5].conditionRelations[0].conditions[0].conditionStatement", is("chemical treatment:4-nitroquinoline N-oxide")).
            // body("results[5].conditionRelations[0].conditions[0].conditionQuantity", is("some amount")).
            // body("results[5].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("ZFA:0000001")).
            // body("results[5].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("GO:0007569")).
            // body("results[5].conditionRelations[0].conditions[0].conditionTaxon", is("NCBITaxon:1781")).
            // body("results[5].conditionRelations[0].conditions[0].conditionChemical.curie", is("CHEBI:46631")).
            body("results[5].negated", is(true)).
            // body("results[5].with", containsInAnyOrder("HGNC:1121", "HGNC:323")).
            // body("results[5].evidenceCodes", hasSize(1)).
            // body("results[5].evidenceCodes[0].curie", is("ECO:0000033"))
            body("results[5].referenceList", hasSize(1)).
            body("results[5].referenceList[0].curie", is("PMID:25920554"));
    }
    
    @Test
    @Order(14)
    public void alleleDiseaseAnnotationBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/04_all_fields_allele_primary_annotation.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/zfinAnnotationFile").
            then().
            statusCode(200);
    
        
        // check entity count and fields correctly read
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=25").
            then().
            statusCode(200).
            body("totalResults", is(257)). // 20 ZFIN annotation replaced with 1
            body("results", hasSize(7)).
            body("results[6].subject.curie", is("ZFIN:ZDB-ALT-200608-1")).
            body("results[6].object.curie", is("DOID:4")).
            body("results[6].diseaseRelation", is("is_implicated_in")).
            // body("results[6].conditionRelations", hasSize(1)).
            // body("results[6].conditionRelations[0].conditionRelationType", is("induces")).
            // body("results[6].conditionRelations[0].conditions", hasSize(1)).
            // body("results[6].conditionRelations[0].conditions[0].conditionClass.curie", is("ZECO:0000101")).
            // body("results[6].conditionRelations[0].conditions[0].conditionId.curie", is("XCO:0000131")).
            // body("results[6].conditionRelations[0].conditions[0].conditionStatement", is("chemical treatment:4-nitroquinoline N-oxide")).
            // body("results[6].conditionRelations[0].conditions[0].conditionQuantity", is("some amount")).
            // body("results[6].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("ZFA:0000001")).
            // body("results[6].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("GO:0007569")).
            // body("results[6].conditionRelations[0].conditions[0].conditionTaxon", is("NCBITaxon:1781")).
            // body("results[6].conditionRelations[0].conditions[0].conditionChemical.curie", is("CHEBI:46631")).
            // body("results[6].with", containsInAnyOrder("HGNC:1121", "HGNC:323")).
            // body("results[6].evidenceCodes", hasSize(1)).
            // body("results[6].evidenceCodes[0].curie", is("ECO:0000033"))
            body("results[6].referenceList", hasSize(1)).
            body("results[6].referenceList[0].curie", is("PMID:25920554")).
            body("results[6].negated", is(true));
    }
    
    @Test
    @Order(15)
    public void diseaseAnnotationBulkUploadNoDOid() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/05_no_doid.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/rgdAnnotationFile").
            then().
            statusCode(200);
    
        // check entity count   
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(103)); // 154 annotations replaced with 0
    }
    
    @Test
    @Order(16)
    public void diseaseAnnotationBulkUploadNoDataProvider() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/06_no_data_provider.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/sgdAnnotationFile").
            then().
            statusCode(200);
    
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(64)); // 40 SGD annotations replaced with 1
    }
    
    @Test
    @Order(17)
    public void diseaseAnnotationBulkUploadNoDataProviderXref() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/07_no_data_provider_cross_reference.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/sgdAnnotationFile").
            then().
            statusCode(200);
    
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(64)); // Count remains the same as SGD annotation replaced
    }
    
    @Test
    @Order(18)
    public void diseaseAnnotationBulkUploadNoDataProviderType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/08_no_data_provider_type.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/wbAnnotationFile").
            then().
            statusCode(200);
    
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(64)); // Count remains the same as WB annotation replaced
    }
    
    @Test
    @Order(19)
    public void diseaseAnnotationBulkUploadNoDateAssigned() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/09_no_date_assigned.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease/bulk/wbAnnotationFile").
            then().
            statusCode(200);
    
        
        // check entity count
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(63)); // 1 WB annotation replaced with 0
    }
    
}
