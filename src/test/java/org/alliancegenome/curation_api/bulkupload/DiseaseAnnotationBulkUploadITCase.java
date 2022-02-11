package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.*;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.XcoTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.*;


@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("04 - Disease annotation bulk upload")
@Order(4)
public class DiseaseAnnotationBulkUploadITCase {
    
    private String requiredDoTerm = "DOID:4";
    private String requiredEcoTerm = "ECO:0000033";
    private String requiredZecoTerm = "ZECO:0000101";
    private String requiredXcoTerm = "XCO:0000131";
    private String requiredGoTerm = "GO:0007569";
    private String requiredChebiTerm = "CHEBI:46631";
    private String requiredZfaTerm = "ZFA:0000001";
    private String requiredNcbiTaxonTerm = "NCBITaxon:1781";
    private ArrayList<String> requiredGenes = new ArrayList<String>(Arrays.asList( "FB:FBgn0010620", "FB:FBgn0032699", "FB:FBgn0045473", "FB:FBgn0054033", "FB:FBgn0259758", "FB:FBgn0260766",
                                                                            "FB:FBgn0260936", "FB:FBgn0264086", "HGNC:29488", "HGNC:3028", "HGNC:30470", "HGNC:447", "HGNC:460", "HGNC:1121", "HGNC:323",
                                                                            "HGNC:47869", "HGNC:5813", "MGI:105042", "MGI:1098239", "MGI:1346858", "MGI:1347355", "MGI:2139535",
                                                                            "RGD:9222273", "SGD:S000004649", "SGD:S000005882", "SGD:S000007272", "WB:WBGene00001010", "WB:WBGene00010748",
                                                                            "WB:WBGene00018054", "WB:WBGene00022693", "ZFIN:ZDB-GENE-080613-2", "ZFIN:ZDB-GENE-090312-196", "ZFIN:ZDB-GENE-130116-1"));
    private ArrayList<String> requiredAGMs = new ArrayList<String>(Arrays.asList("WB:WBStrain00005113", "WB:WBStrain00026486", "ZFIN:ZDB-FISH-150901-5755"));
    private ArrayList<String> requiredAlleles = new ArrayList<String>(Arrays.asList("MGI:4879001", "ZFIN:ZDB-ALT-190906-11", "ZFIN:ZDB-ALT-200608-1"));
    private Map<String, String> modTaxons = Map.of(
            "HGNC", "NCBITaxon:9606",
            "FB", "NCBITaxon:7227",
            "MGI", "NCBITaxon:10090",
            "RGD", "NCBITaxon:10116",
            "SGD", "NCBITaxon:559292",
            "WB", "NCBITaxon:6239",
            "ZFIN", "NCBITaxon:7955"
            );  
    
    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 60000)
                    .setParam("http.connection.timeout", 60000));
    }

    @Test
    @Order(1)
    public void geneDiseaseAnnotationBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_primary_annotation.json"));
        
        loadRequiredEntities();
        
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
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results", hasSize(1)).
            body("results[0].subject.curie", is("FB:FBgn0260936")).
            body("results[0].object.curie", is("DOID:4")).
            body("results[0].diseaseRelation", is("is_implicated_in")).
            body("results[0].conditionRelations", hasSize(1)).
            body("results[0].conditionRelations[0].conditionRelationType", is("induced_by")).
            body("results[0].conditionRelations[0].conditions", hasSize(1)).
            body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is("ZECO:0000101")).
            body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is("XCO:0000131")).
            body("results[0].conditionRelations[0].conditions[0].conditionStatement", is("chemical treatment:4-nitroquinoline N-oxide")).
            body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("some amount")).
            body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("ZFA:0000001")).
            body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("GO:0007569")).
            body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:1781")).
            body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is("CHEBI:46631")).
            body("results[0].negated", is(true)).
            body("results[0].with", hasSize(2)).
            body("results[0].with[0].curie", is("HGNC:1121")).
            body("results[0].with[1].curie", is("HGNC:323")).
            body("results[0].evidenceCodes", hasSize(1)).
            body("results[0].evidenceCodes[0].curie", is("ECO:0000033")).
            body("results[0].singleReference.curie", is("PMID:25920554"));
    }
    
    @Test
    @Order(2)
    public void geneDiseaseAnnotationBulkUploadSecondaryAnnotation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/02_all_fields_gene_secondary_annotation.json"));
        
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
            body("totalResults", is(1)); // secondary annotation not loaded
    }
    
    @Test
    @Order(3)
    public void agmDiseaseAnnotationBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/03_all_fields_agm_primary_annotation.json"));
        
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
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)). 
            body("results", hasSize(2)).
            body("results[1].subject.curie", is("WB:WBStrain00005113")).
            body("results[1].object.curie", is("DOID:4")).
            body("results[1].diseaseRelation", is("is_model_of")).
            body("results[1].conditionRelations", hasSize(1)).
            body("results[1].conditionRelations[0].conditionRelationType", is("has_condition")).
            body("results[1].conditionRelations[0].conditions", hasSize(1)).
            body("results[1].conditionRelations[0].conditions[0].conditionClass.curie", is("ZECO:0000101")).
            body("results[1].conditionRelations[0].conditions[0].conditionId.curie", is("XCO:0000131")).
            body("results[1].conditionRelations[0].conditions[0].conditionStatement", is("stress:auditory stimulus")).
            body("results[1].conditionRelations[0].conditions[0].conditionQuantity", is("some amount")).
            body("results[1].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("ZFA:0000001")).
            body("results[1].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("GO:0007569")).
            body("results[1].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:1781")).
            body("results[1].conditionRelations[0].conditions[0].conditionChemical.curie", is("CHEBI:46631")).
            body("results[1].negated", is(true)).
            body("results[0].with", hasSize(2)).
            body("results[0].with[0].curie", is("HGNC:1121")).
            body("results[0].with[1].curie", is("HGNC:323")).
            body("results[1].evidenceCodes", hasSize(1)).
            body("results[1].evidenceCodes[0].curie", is("ECO:0000033")).
            body("results[1].singleReference.curie", is("PMID:25920554"));
    }
    
    @Test
    @Order(4)
    public void alleleDiseaseAnnotationBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/04_all_fields_allele_primary_annotation.json"));
        
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
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)). // 
            body("results", hasSize(3)).
            body("results[2].subject.curie", is("ZFIN:ZDB-ALT-200608-1")).
            body("results[2].object.curie", is("DOID:4")).
            body("results[2].diseaseRelation", is("is_implicated_in")).
            body("results[2].conditionRelations", hasSize(1)).
            body("results[2].conditionRelations[0].conditionRelationType", is("has_condition")).
            body("results[2].conditionRelations[0].conditions", hasSize(1)).
            body("results[2].conditionRelations[0].conditions[0].conditionClass.curie", is("ZECO:0000101")).
            body("results[2].conditionRelations[0].conditions[0].conditionId.curie", is("XCO:0000131")).
            body("results[2].conditionRelations[0].conditions[0].conditionStatement", is("stress:auditory stimulus")).
            body("results[2].conditionRelations[0].conditions[0].conditionQuantity", is("some amount")).
            body("results[2].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("ZFA:0000001")).
            body("results[2].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("GO:0007569")).
            body("results[2].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:1781")).
            body("results[2].conditionRelations[0].conditions[0].conditionChemical.curie", is("CHEBI:46631")).
            body("results[0].with", hasSize(2)).
            body("results[0].with[0].curie", is("HGNC:1121")).
            body("results[0].with[1].curie", is("HGNC:323")).
            body("results[2].evidenceCodes", hasSize(1)).
            body("results[2].evidenceCodes[0].curie", is("ECO:0000033")).
            body("results[2].singleReference.curie", is("PMID:25920554")).
            body("results[2].negated", is(true));
    }
    
    @Test
    @Order(5)
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
            body("totalResults", is(3)); // 0 RGD annotations added
    }
    
    @Test
    @Order(6)
    public void diseaseAnnotationBulkUploadNoDataProvider() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/06_no_data_provider.json"));
        
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
            body("totalResults", is(4));
    }
    
    @Test
    @Order(7)
    public void diseaseAnnotationBulkUploadNoDataProviderXref() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/07_no_data_provider_cross_reference.json"));
        
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
            body("totalResults", is(4)); // Count remains the same as SGD annotation replaced
    }
    
    @Test
    @Order(8)
    public void diseaseAnnotationBulkUploadNoDataProviderType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/08_no_data_provider_type.json"));
        
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
            body("totalResults", is(4)); // Count remains the same as WB annotation replaced
    }
    
    @Test
    @Order(9)
    public void diseaseAnnotationBulkUploadNoDateAssigned() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/09_no_date_assigned.json"));
        
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
            body("totalResults", is(3)); // 1 WB annotation replaced with 0
    }

    @Test
    @Order(10)
    public void diseaseAnnotationBulkUploadNoEvidence() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/10_no_evidence.json"));
        
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
            body("totalResults", is(3)); // no annotations loaded
    }
    
    @Test
    @Order(11)
    public void diseaseAnnotationBulkUploadNoEvidenceCodes() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/11_no_evidence_evidence_codes.json"));
        
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
            body("totalResults", is(3)); // no annotations loaded
    }

    @Test
    @Order(12)
    public void diseaseAnnotationBulkUploadNoPublication() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/12_no_evidence_publication.json"));
        
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
            body("totalResults", is(3)); // 0 annotations loaded
    }

    @Test
    @Order(13)
    public void diseaseAnnotationBulkUploadNoPublicationXref() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/13_no_evidence_publication_cross_reference.json"));
        
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
    @Order(14)
    public void diseaseAnnotationBulkUploadNoPublicationXrefId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/14_no_evidence_publication_cross_reference_id.json"));
        
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
    @Order(15)
    public void diseaseAnnotationBulkUploadNoPublicationXrefPages() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/15_no_evidence_publication_cross_reference_pages.json"));
        
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
    @Order(16)
    public void diseaseAnnotationBulkUploadNoPublicationId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/16_no_evidence_publication_publication_id.json"));
        
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
    @Order(17)
    public void diseaseAnnotationBulkUploadNoNegation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/17_no_negation.json"));
        
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
    @Order(18)
    public void diseaseAnnotationBulkUploadNoObjectId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/18_no_object_id.json"));
        
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
    @Order(19)
    public void diseaseAnnotationBulkUploadNoObjectName() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/19_no_object_name.json"));
        
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
    @Order(20)
    public void diseaseAnnotationBulkUploadNoObjectRelation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/20_no_object_relation.json"));
        
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
            body("totalResults", is(4)); // No MGI annotations loaded
    }

    @Test
    @Order(21)
    public void diseaseAnnotationBulkUploadNoObjectRelationAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/21_no_object_relation_association_type.json"));
        
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
            body("totalResults", is(4)); // No MGI annotations loaded
    }

    @Test
    @Order(22)
    public void diseaseAnnotationBulkUploadNoObjectRelationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/22_no_object_relation_object_type.json"));
        
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
            body("totalResults", is(4)); // No MGI annotations loaded
    }

    @Test
    @Order(23)
    public void diseaseAnnotationBulkUploadNoWith() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/23_no_with.json"));
        
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
    @Order(24)
    public void diseaseAnnotationBulkUploadNoConditionRelations() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/24_no_condition_relations.json"));
        
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
    @Order(25)
    public void diseaseAnnotationBulkUploadNoConditionRelationsType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/25_no_condition_relations_condition_relation_type.json"));
        
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
            body("totalResults", is(4)); // 1 FB annotation replaced with 0
    }

    @Test
    @Order(26)
    public void diseaseAnnotationBulkUploadNoConditions() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/26_no_condition_relations_conditions.json"));
        
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
            body("totalResults", is(4)); // 0 FB annotations added
    }

    @Test
    @Order(27)
    public void diseaseAnnotationBulkUploadNoConditionClassId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/27_no_condition_relations_conditions_condition_class_id.json"));
        
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
            body("totalResults", is(4)); // 0 FB annotations added
    }

    @Test
    @Order(28)
    public void diseaseAnnotationBulkUploadNoConditionId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/28_no_condition_relations_conditions_condition_id.json"));
        
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
            body("totalResults", is(5)); // 1 FB annotation added
    }

    @Test
    @Order(29)
    public void diseaseAnnotationBulkUploadNoConditionStatement() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/29_no_condition_relations_conditions_condition_statement.json"));
        
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
            body("totalResults", is(4)); // 1 FB annotation replaced with 0
    }

    @Test
    @Order(30)
    public void diseaseAnnotationBulkUploadNoConditionQuantity() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/30_no_condition_relations_conditions_condition_quantity.json"));
        
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
            body("totalResults", is(5)); // 1 FB annotation added
    }

    @Test
    @Order(31)
    public void diseaseAnnotationBulkUploadNoAnatomicalOntologyId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/31_no_condition_relations_conditions_anatomical_ontology_id.json"));
        
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
    @Order(32)
    public void diseaseAnnotationBulkUploadNoTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/32_no_condition_relations_conditions_ncbi_taxon_id.json"));
        
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
    @Order(33)
    public void diseaseAnnotationBulkUploadNoChemicalOntologyId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/33_no_condition_relations_conditions_chemical_ontology_id.json"));
        
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
    @Order(34)
    public void diseaseAnnotationBulkUploadNoInferredGeneAssociation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/34_no_object_relation_inferred_gene_association.json"));
        
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
    @Order(35)
    public void diseaseAnnotationBulkUploadDuplicateCuries() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/35_duplicate_curies.json"));
        
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
    @Order(36)
    public void diseaseAnnotationBulkUploadInvalidGeneAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/36_invalid_gene_association_type.json"));
        
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
    @Order(37)
    public void diseaseAnnotationBulkUploadInvalidAgmAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/37_invalid_agm_association_type.json"));
        
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
    @Order(38)
    public void diseaseAnnotationBulkUploadInvalidAlleleAssociationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/38_invalid_allele_association_type.json"));
        
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
    @Order(39)
    public void diseaseAnnotationBulkUploadInvalidObjectType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/39_invalid_object_type.json"));
        
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
    
    @Test
    @Order(40)
    public void diseaseAnnotationBulkUploadInvalidConditionClassId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/40_invalid_condition_class_id.json"));
            
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
    
    @Test
    @Order(41)
    public void diseaseAnnotationBulkUploadInvalidConditionId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/41_invalid_condition_id.json"));
            
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
    
    @Test
    @Order(42)
    public void diseaseAnnotationBulkUploadInvalidAnatomicalOntologyId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/42_invalid_anatomical_ontology_id.json"));
            
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
    
    @Test
    @Order(43)
    public void diseaseAnnotationBulkUploadInvalidGeneOntologyId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/43_invalid_gene_ontology_id.json"));
            
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
    
    @Test
    @Order(44)
    public void diseaseAnnotationBulkUploadInvalidNcbiTaxonId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/44_invalid_ncbi_taxon_id.json"));
            
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
    
    @Test
    @Order(45)
    public void diseaseAnnotationBulkUploadInvalidChemicalOntologyId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/45_invalid_chemical_ontology_id.json"));
            
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
    
    private void loadRequiredEntities() throws Exception {
        loadDOTerm();
        loadECOTerm();
        loadGOTerm();
        loadXCOTerm();
        loadZECOTerm();
        loadZFATerm();
        loadCHEBITerm();
        loadNCBITaxonTerms();
        loadGenes();  
        loadAlleles();
        loadAGMs();
    }
    
    private void loadDOTerm() throws Exception {
        DOTerm doTerm = new DOTerm();
        doTerm.setCurie(requiredDoTerm);
        doTerm.setName("Test DOTerm");
        doTerm.setObsolete(false);
        
        RestAssured.given().
            contentType("application/json").
            body(doTerm).
            when().
            put("/api/doterm").
            then().
            statusCode(200);
    }


    private void loadECOTerm() throws Exception {
        EcoTerm ecoTerm = new EcoTerm();
        ecoTerm.setCurie(requiredEcoTerm);
        ecoTerm.setName("Test ECOTerm");
        ecoTerm.setObsolete(false);
        
        RestAssured.given().
            contentType("application/json").
            body(ecoTerm).
            when().
            put("/api/ecoterm").
            then().
            statusCode(200);
    }


    private void loadGOTerm() throws Exception {
        GOTerm goTerm = new GOTerm();
        goTerm.setCurie(requiredGoTerm);
        goTerm.setName("Test GOTerm");
        goTerm.setObsolete(false);
        
        RestAssured.given().
            contentType("application/json").
            body(goTerm).
            when().
            put("/api/goterm").
            then().
            statusCode(200);
    }
    

    private void loadXCOTerm() throws Exception {
        XcoTerm xcoTerm = new XcoTerm();
        xcoTerm.setCurie(requiredXcoTerm);
        xcoTerm.setName("Test XCOTerm");
        xcoTerm.setObsolete(false);
        
        RestAssured.given().
            contentType("application/json").
            body(xcoTerm).
            when().
            put("/api/xcoterm").
            then().
            statusCode(200);
    }


    private void loadZECOTerm() throws Exception {
        ZecoTerm zecoTerm = new ZecoTerm();
        zecoTerm.setCurie(requiredZecoTerm);
        zecoTerm.setName("Test ZECOTerm");
        zecoTerm.setObsolete(false);
        
        RestAssured.given().
            contentType("application/json").
            body(zecoTerm).
            when().
            put("/api/zecoterm").
            then().
            statusCode(200);
    }
    

    private void loadZFATerm() throws Exception {
        ZfaTerm zfaTerm = new ZfaTerm();
        zfaTerm.setCurie(requiredZfaTerm);
        zfaTerm.setName("Test DOTerm");
        zfaTerm.setObsolete(false);
        
        RestAssured.given().
            contentType("application/json").
            body(zfaTerm).
            when().
            put("/api/zfaterm").
            then().
            statusCode(200);
    }


    private void loadCHEBITerm() throws Exception {
        CHEBITerm chebiTerm = new CHEBITerm();
        chebiTerm.setCurie(requiredChebiTerm);
        chebiTerm.setName("Test CHEBITerm");
        chebiTerm.setObsolete(false);
        
        RestAssured.given().
            contentType("application/json").
            body(chebiTerm).
            when().
            put("/api/chebiterm").
            then().
            statusCode(200);
    }


    private void loadNCBITaxonTerms() throws Exception {
        for (String taxonTerm : modTaxons.values()) {
            loadNCBITaxonTerm(taxonTerm);
        }
        loadNCBITaxonTerm(requiredNcbiTaxonTerm);
    }
    
    private void loadNCBITaxonTerm(String taxonTerm) {
        NCBITaxonTerm ncbiTaxonTerm = new NCBITaxonTerm();
        ncbiTaxonTerm.setCurie(taxonTerm);
        ncbiTaxonTerm.setName("Test NCBITaxonTerm");
        ncbiTaxonTerm.setObsolete(false);
    
        RestAssured.given().
            contentType("application/json").
            body(ncbiTaxonTerm).
            when().
            put("/api/ncbitaxonterm").
            then().
            statusCode(200);
    }
    
    
    private void loadGenes() throws Exception {
        for (String geneCurie : requiredGenes) {
            Gene gene = new Gene();
            gene.setCurie(geneCurie);
            gene.setTaxon(getTaxonFromEntityOrTaxonCurie(geneCurie));

            RestAssured.given().
                    contentType("application/json").
                    body(gene).
                    when().
                    post("/api/gene").
                    then().
                    statusCode(200);
        }
    }

    private void loadAGMs() throws Exception {
        for (String agmCurie : requiredAGMs) {
            AffectedGenomicModel agm = new AffectedGenomicModel();
            agm.setCurie(agmCurie);
            agm.setTaxon(getTaxonFromEntityOrTaxonCurie(agmCurie));
            agm.setName("Test AGM");

            RestAssured.given().
                    contentType("application/json").
                    body(agm).
                    when().
                    post("/api/agm").
                    then().
                    statusCode(200);
        }
    }

    private void loadAlleles() throws Exception {
        for (String alleleCurie : requiredAlleles) {
            Allele allele = new Allele();
            allele.setCurie(alleleCurie);
            allele.setTaxon(getTaxonFromEntityOrTaxonCurie(alleleCurie));

            RestAssured.given().
                    contentType("application/json").
                    body(allele).
                    when().
                    post("/api/allele").
                    then().
                    statusCode(200);
        }
    }
    
    private NCBITaxonTerm getTaxonFromEntityOrTaxonCurie(String curie) {
        Pattern pattern = Pattern.compile("^([^:]+):");
        Matcher matcher = pattern.matcher(curie);
        
        String taxonCurie = curie;
        if (matcher.find()) {
            taxonCurie = modTaxons.get(matcher.group(1));
        }
        
        ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
            when().
            get("/api/ncbitaxonterm/" + taxonCurie).
            then().
            statusCode(200).
            extract().body().as(getObjectResponseTypeRefTaxon());
        
        return response.getEntity();
    }
    
    private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRefTaxon() {
        return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
    }
}
