package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.*;

import java.nio.file.*;

import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.junit.jupiter.api.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.*;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("04 - Disease annotation bulk upload")
@Order(4)
public class DiseaseAnnotationBulkUploadITCase {

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 30000)
                    .setParam("http.connection.timeout", 30000));
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
                post("/api/gene/bulk/bgifile").
                then().
                statusCode(200);
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
                post("/api/allele/bulk/allelefile").
                then().
                statusCode(200);
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
                post("/api/agm/bulk/agmfile").
                then().
                statusCode(200);
    }
    
    @Test
    @Order(4)
    public void diseaseAnnotationBulkUploadFB() throws Exception {
        
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/00_FB_examples.json"));

        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/fbAnnotationFileFms").
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

        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/humanAnnotationFileFms").
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

        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/mgiAnnotationFileFms").
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

        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/rgdAnnotationFileFms").
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

        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/sgdAnnotationFileFms").
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

        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/wbAnnotationFileFms").
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

        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/zfinAnnotationFileFms").
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
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/fbAnnotationFileFms").
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
            // body("results[4].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:1781")).
            // body("results[4].conditionRelations[0].conditions[0].conditionChemical.curie", is("CHEBI:46631")).
            body("results[4].negated", is(true)).
            // body("results[4].with", containsInAnyOrder("HGNC:1121", "HGNC:323")).
            // body("results[4].evidenceCodes", hasSize(1)).
            // body("results[4].evidenceCodes[0].curie", is("ECO:0000033")).
            //body("results[4].referenceList", hasSize(1)).
            body("results[4].reference.curie", is("PMID:25920554"));
    }
    
    @Test
    @Order(12)
    public void geneDiseaseAnnotationBulkUploadSecondaryAnnotation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/02_all_fields_gene_secondary_annotation.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/mgiAnnotationFileFms").
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
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/wbAnnotationFileFms").
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
            // body("results[5].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:1781")).
            // body("results[5].conditionRelations[0].conditions[0].conditionChemical.curie", is("CHEBI:46631")).
            body("results[5].negated", is(true)).
            // body("results[5].with", containsInAnyOrder("HGNC:1121", "HGNC:323")).
            // body("results[5].evidenceCodes", hasSize(1)).
            // body("results[5].evidenceCodes[0].curie", is("ECO:0000033"))
            //body("results[5].referenceList", hasSize(1)).
            body("results[5].reference.curie", is("PMID:25920554"));
    }
    
    @Test
    @Order(14)
    public void alleleDiseaseAnnotationBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/04_all_fields_allele_primary_annotation.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/zfinAnnotationFileFms").
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
            // body("results[6].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:1781")).
            // body("results[6].conditionRelations[0].conditions[0].conditionChemical.curie", is("CHEBI:46631")).
            // body("results[6].with", containsInAnyOrder("HGNC:1121", "HGNC:323")).
            // body("results[6].evidenceCodes", hasSize(1)).
            // body("results[6].evidenceCodes[0].curie", is("ECO:0000033"))
            //body("results[6].referenceList", hasSize(1)).
            body("results[6].reference.curie", is("PMID:25920554")).
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
            post("/api/disease-annotation/bulk/rgdAnnotationFileFms").
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
            body("totalResults", is(103)); // 154 RGD annotations replaced with 0
    }
    
    @Test
    @Order(16)
    public void diseaseAnnotationBulkUploadNoDataProvider() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/06_no_data_provider.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/sgdAnnotationFileFms").
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
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/sgdAnnotationFileFms").
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
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/wbAnnotationFileFms").
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
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/wbAnnotationFileFms").
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

    @Test
    @Order(20)
    public void diseaseAnnotationBulkUploadNoEvidence() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/10_no_evidence.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/wbAnnotationFileFms").
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
            body("totalResults", is(63)); // no annotations loaded
    }
    
    @Test
    @Order(21)
    public void diseaseAnnotationBulkUploadNoEvidenceCodes() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/11_no_evidence_evidence_codes.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/wbAnnotationFileFms").
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
            body("totalResults", is(63)); // no annotations loaded
    }

    @Test
    @Order(22)
    public void diseaseAnnotationBulkUploadNoPublication() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/12_no_evidence_publication.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/humanAnnotationFileFms").
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
            body("totalResults", is(3)); // 60 human annotations replaced with 0
    }

    @Test
    @Order(23)
    public void diseaseAnnotationBulkUploadNoPublicationXref() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/13_no_evidence_publication_cross_reference.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/humanAnnotationFileFms").
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
            body("totalResults", is(4)); // 1 human annotation added
    }

    @Test
    @Order(24)
    public void diseaseAnnotationBulkUploadNoPublicationXrefId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/14_no_evidence_publication_cross_reference_id.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/humanAnnotationFileFms").
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
            body("totalResults", is(4)); // 1 human annotation replaced with 1
    }

    @Test
    @Order(25)
    public void diseaseAnnotationBulkUploadNoPublicationXrefPages() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/15_no_evidence_publication_cross_reference_pages.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/humanAnnotationFileFms").
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
            body("totalResults", is(4)); // 1 human annotation replaced with 1
    }

    @Test
    @Order(26)
    public void diseaseAnnotationBulkUploadNoPublicationId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/16_no_evidence_publication_publication_id.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/humanAnnotationFileFms").
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
            body("totalResults", is(3)); // 1 human annotation replaced with 0
    }

    @Test
    @Order(27)
    public void diseaseAnnotationBulkUploadNoNegation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/17_no_negation.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/humanAnnotationFileFms").
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
            body("totalResults", is(4)); // 1 human annotation added
    }

    @Test
    @Order(28)
    public void diseaseAnnotationBulkUploadNoObjectId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/18_no_object_id.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/humanAnnotationFileFms").
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
            body("totalResults", is(3)); // 1 human annotation replaced with 0
    }

    @Test
    @Order(29)
    public void diseaseAnnotationBulkUploadNoObjectName() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/19_no_object_name.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/humanAnnotationFileFms").
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
            body("totalResults", is(4)); // 1 human annotation added
    }

    @Test
    @Order(30)
    public void diseaseAnnotationBulkUploadNoObjectRelation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/20_no_object_relation.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/mgiAnnotationFileFms").
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
            body("totalResults", is(4)); // No change in MGI annotations
    }

    @Test
    @Order(31)
    public void diseaseAnnotationBulkUploadNoObjectRelationAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/21_no_object_relation_association_type.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/mgiAnnotationFileFms").
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
            body("totalResults", is(4)); // No change in MGI annotations
    }

    @Test
    @Order(32)
    public void diseaseAnnotationBulkUploadNoObjectRelationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/22_no_object_relation_object_type.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/mgiAnnotationFileFms").
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
            body("totalResults", is(4)); // No change in MGI annotations
    }

    @Test
    @Order(33)
    public void diseaseAnnotationBulkUploadNoWith() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/23_no_with.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/mgiAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 MGI annotation added
    }

    @Test
    @Order(34)
    public void diseaseAnnotationBulkUploadNoConditionRelations() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/24_no_condition_relations.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/mgiAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 MGI annotation replaced with 1
    }

    @Test
    @Order(35)
    public void diseaseAnnotationBulkUploadNoConditionRelationsType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/25_no_condition_relations_condition_relation_type.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/fbAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 FB annotation replaced with 1
    }

    @Test
    @Order(36)
    public void diseaseAnnotationBulkUploadNoConditions() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/26_no_condition_relations_conditions.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/fbAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 FB annotation replaced with 1
    }

    @Test
    @Order(37)
    public void diseaseAnnotationBulkUploadNoConditionClassId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/27_no_condition_relations_conditions_condition_class_id.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/fbAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 FB annotation replaced with 1
    }

    @Test
    @Order(38)
    public void diseaseAnnotationBulkUploadNoConditionId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/28_no_condition_relations_conditions_condition_id.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/fbAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 FB annotation replaced with 1
    }

    @Test
    @Order(39)
    public void diseaseAnnotationBulkUploadNoConditionStatement() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/29_no_condition_relations_conditions_condition_statement.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/fbAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 FB annotation replaced with 1
    }

    @Test
    @Order(40)
    public void diseaseAnnotationBulkUploadNoConditionQuantity() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/30_no_condition_relations_conditions_condition_quantity.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/fbAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 FB annotation replaced with 1
    }

    @Test
    @Order(41)
    public void diseaseAnnotationBulkUploadNoAnatomicalOntologyId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/31_no_condition_relations_conditions_anatomical_ontology_id.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/fbAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 FB annotation replaced with 1
    }

    @Test
    @Order(42)
    public void diseaseAnnotationBulkUploadNoTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/32_no_condition_relations_conditions_ncbi_taxon_id.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/zfinAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 ZFIN annotation replaced with 1
    }

    @Test
    @Order(43)
    public void diseaseAnnotationBulkUploadNoChemicalOntologyId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/33_no_condition_relations_conditions_chemical_ontology_id.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/zfinAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 ZFIN annotation replaced with 1
    }

    @Test
    @Order(44)
    public void diseaseAnnotationBulkUploadNoInferredGeneAssociation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/34_no_object_relation_inferred_gene_association.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/wbAnnotationFileFms").
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
            body("totalResults", is(6)); // 1 WB annotation added
    }

    @Test
    @Order(45)
    public void diseaseAnnotationBulkUploadDuplicateCuries() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/35_duplicate_curies.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/sgdAnnotationFileFms").
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
            body("totalResults", is(6)); // 1 SGD annotation replaced with 1
    }

    @Test
    @Order(46)
    public void diseaseAnnotationBulkUploadInvalidGeneAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/36_invalid_gene_association_type.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/zfinAnnotationFileFms").
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
            body("totalResults", is(5)); // 1 ZFIN annotation replaced with 0
    }

    @Test
    @Order(47)
    public void diseaseAnnotationBulkUploadInvalidAgmAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/37_invalid_agm_association_type.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/zfinAnnotationFileFms").
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
            body("totalResults", is(5)); // 0 ZFIN annotations added
    }

    @Test
    @Order(48)
    public void diseaseAnnotationBulkUploadInvalidAlleleAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/38_invalid_allele_association_type.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/mgiAnnotationFileFms").
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
            body("totalResults", is(4)); // 1 MGI annotation replaced with 0
    }

    @Test
    @Order(49)
    public void diseaseAnnotationBulkUploadInvalidObjectType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/39_invalid_object_type.json"));
        
        loadDOTerms(content);
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/disease-annotation/bulk/zfinAnnotationFileFms").
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
            body("totalResults", is(4)); // No ZFIN annotations added
    }
    
    private void loadDOTerms(String content) throws Exception {
        DiseaseAnnotationMetaDataFmsDTO da = new ObjectMapper().readValue(content, DiseaseAnnotationMetaDataFmsDTO.class);
        da.getData().forEach(dat -> {
            RestAssured.given().
            contentType("application/json").
            body("{ \"curie\": \"" + dat.getDoId() + "\"}").
            when().
            put("/api/doterm").
            then().
            statusCode(200);
        });
    }
}
