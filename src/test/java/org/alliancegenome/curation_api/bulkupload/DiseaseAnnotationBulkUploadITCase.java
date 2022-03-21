package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.*;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
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
@Order(11)
public class DiseaseAnnotationBulkUploadITCase {
    
    private ArrayList<String> requiredGenes = new ArrayList<String>(Arrays.asList( "DATEST:Gene0001", "DATEST:Gene0002", "HGNC:0001"));
    private String requiredDoTerm = "DATEST:Disease0001";
    private String requiredEcoTerm = "DATEST:Evidence0001";
    private String requiredGoTerm = "DATEST:GOTerm0001";
    private String requiredAnatomicalTerm = "DATEST:AnatomyTerm0001";
    private String requiredChemicalTerm = "DATEST:ChemicalTerm0001";
    private String requiredAllele = "DATEST:Allele0001";
    private String requiredAgm = "DATEST:AGM0001";
    private String requiredSgdBackgroundStrain = "SGD:AGM0001";
    private String requiredZecoTerm = "DATEST:ExpCondTerm0001";
    private String requiredNonSlimZecoTerm = "DATEST:NSExpCondTerm0001";
    private String requiredExpCondTerm = "DATEST:ExpCondTerm0002";
    private String requiredGeneDiseaseRelation = "is_implicated_in";
    private String requiredAlleleDiseaseRelation = "is_implicated_in";
    private String requiredAgmDiseaseRelation = "is_model_of";
    private String requiredGeneticSex = "male";
    private String requiredDiseaseGeneticModifierRelation = "ameliorated_by";
    private String requiredDiseaseQualifier = "susceptibility";
    private String requiredAnnotationType = "manually_curated";
    private String requiredNoteType = "disease_summary";
    private String requiredConditionRelationType = "exacerbated_by";
    

    
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
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
        
        loadRequiredEntities();
        clearExistingDiseaseAnnotations();
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
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
            body("results[0].modEntityId", is("DATEST:Annot0001")).
            body("results[0].uniqueId", is("DATEST:Annot0001")).
            body("results[0].subject.curie", is("DATEST:Gene0001")).
            body("results[0].object.curie", is("DATEST:Disease0001")).
            body("results[0].diseaseRelation.name", is("is_implicated_in")).
            body("results[0].geneticSex.name", is("male")).
            body("results[0].modifiedBy", is("DATEST:Person0001")).
            body("results[0].dateLastModified".toString(), is("2022-03-09T22:10:12Z")).
            body("results[0].createdBy", is("DATEST:Person0001")).
            body("results[0].creationDate".toString(), is("2022-03-09T22:10:12Z")).
            body("results[0].conditionRelations", hasSize(1)).
            body("results[0].conditionRelations[0].conditionRelationType.name", is("exacerbated_by")).
            body("results[0].conditionRelations[0].conditions", hasSize(1)).
            body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is("DATEST:ExpCondTerm0001")).
            body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is("DATEST:ExpCondTerm0002")).
            body("results[0].conditionRelations[0].conditions[0].conditionStatement", is("Condition statement")).
            body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount")).
            body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("DATEST:AnatomyTerm0001")).
            body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("DATEST:GOTerm0001")).
            body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239")).
            body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is("DATEST:ChemicalTerm0001")).
            body("results[0].conditionRelations[0].conditions[0].conditionFreeText", is("Free text")).
            body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm:Test GOTerm:Test ChemicalTerm:Test NCBITaxonTerm:Some amount:Free text")).
            body("results[0].negated", is(true)).
            body("results[0].diseaseGeneticModifier.curie", is("DATEST:Gene0002")).
            body("results[0].diseaseGeneticModifierRelation.name", is("ameliorated_by")).
            body("results[0].with", hasSize(1)).
            body("results[0].with[0].curie", is("HGNC:0001")).
            body("results[0].singleReference.curie", is("PMID:25920554")).
            body("results[0].relatedNotes[0].freeText", is("Test note")).
            body("results[0].relatedNotes[0].noteType.name", is("disease_summary")).
            body("results[0].relatedNotes[0].internal", is(false)).
            body("results[0].relatedNotes[0].references[0].curie", is("PMID:25920554")).
            body("results[0].annotationType.name", is("manually_curated")).
            body("results[0].diseaseQualifiers[0].name", is("susceptibility")).
            body("results[0].sgdStrainBackground.curie", is("SGD:AGM0001")).
            body("results[0].evidenceCodes", hasSize(1)).
            body("results[0].evidenceCodes[0].curie", is("DATEST:Evidence0001"));
    }
    
    @Test
    @Order(2)
    public void alleleDiseaseAnnotationBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/02_all_fields_allele_annotation.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
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
            body("results[1].modEntityId", is("DATEST:Annot0002")).
            body("results[1].uniqueId", is("DATEST:Annot0002")).
            body("results[1].subject.curie", is("DATEST:Allele0001")).
            body("results[1].object.curie", is("DATEST:Disease0001")).
            body("results[1].diseaseRelation.name", is("is_implicated_in")).
            body("results[1].geneticSex.name", is("male")).
            body("results[1].modifiedBy", is("DATEST:Person0001")).
            body("results[1].dateLastModified".toString(), is("2022-03-09T22:10:12Z")).
            body("results[1].createdBy", is("DATEST:Person0001")).
            body("results[1].creationDate".toString(), is("2022-03-09T22:10:12Z")).
            body("results[1].conditionRelations", hasSize(1)).
            body("results[1].conditionRelations[0].conditionRelationType.name", is("exacerbated_by")).
            body("results[1].conditionRelations[0].conditions", hasSize(1)).
            body("results[1].conditionRelations[0].conditions[0].conditionClass.curie", is("DATEST:ExpCondTerm0001")).
            body("results[1].conditionRelations[0].conditions[0].conditionId.curie", is("DATEST:ExpCondTerm0002")).
            body("results[1].conditionRelations[0].conditions[0].conditionStatement", is("Condition statement")).
            body("results[1].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount")).
            body("results[1].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("DATEST:AnatomyTerm0001")).
            body("results[1].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("DATEST:GOTerm0001")).
            body("results[1].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239")).
            body("results[1].conditionRelations[0].conditions[0].conditionChemical.curie", is("DATEST:ChemicalTerm0001")).
            body("results[1].conditionRelations[0].conditions[0].conditionFreeText", is("Free text")).
            body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm:Test GOTerm:Test ChemicalTerm:Test NCBITaxonTerm:Some amount:Free text")).
            body("results[1].negated", is(true)).
            body("results[1].diseaseGeneticModifier.curie", is("DATEST:Gene0002")).
            body("results[1].diseaseGeneticModifierRelation.name", is("ameliorated_by")).
            body("results[1].with", hasSize(1)).
            body("results[1].with[0].curie", is("HGNC:0001")).
            body("results[1].singleReference.curie", is("PMID:25920554")).
            body("results[1].relatedNotes[0].freeText", is("Test note")).
            body("results[1].relatedNotes[0].noteType.name", is("disease_summary")).
            body("results[1].relatedNotes[0].internal", is(false)).
            body("results[1].relatedNotes[0].references[0].curie", is("PMID:25920554")).
            body("results[0].annotationType.name", is("manually_curated")).
            body("results[0].diseaseQualifiers[0].name", is("susceptibility")).
            body("results[1].evidenceCodes", hasSize(1)).
            body("results[1].evidenceCodes[0].curie", is("DATEST:Evidence0001"));
    }
    
    @Test
    @Order(3)
    public void agmDiseaseAnnotationBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/03_all_fields_agm_annotation.json"));
        
        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
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
            body("totalResults", is(3)).
            body("results", hasSize(3)).
            body("results[2].modEntityId", is("DATEST:Annot0003")).
            body("results[2].uniqueId", is("DATEST:Annot0003")).
            body("results[2].subject.curie", is("DATEST:AGM0001")).
            body("results[2].object.curie", is("DATEST:Disease0001")).
            body("results[2].diseaseRelation.name", is("is_model_of")).
            body("results[2].geneticSex.name", is("male")).
            body("results[2].modifiedBy", is("DATEST:Person0001")).
            body("results[2].dateLastModified".toString(), is("2022-03-09T22:10:12Z")).
            body("results[2].createdBy", is("DATEST:Person0001")).
            body("results[2].creationDate".toString(), is("2022-03-09T22:10:12Z")).
            body("results[2].conditionRelations", hasSize(1)).
            body("results[2].conditionRelations[0].conditionRelationType.name", is("exacerbated_by")).
            body("results[2].conditionRelations[0].conditions", hasSize(1)).
            body("results[2].conditionRelations[0].conditions[0].conditionClass.curie", is("DATEST:ExpCondTerm0001")).
            body("results[2].conditionRelations[0].conditions[0].conditionId.curie", is("DATEST:ExpCondTerm0002")).
            body("results[2].conditionRelations[0].conditions[0].conditionStatement", is("Condition statement")).
            body("results[2].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount")).
            body("results[2].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("DATEST:AnatomyTerm0001")).
            body("results[2].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("DATEST:GOTerm0001")).
            body("results[2].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239")).
            body("results[2].conditionRelations[0].conditions[0].conditionChemical.curie", is("DATEST:ChemicalTerm0001")).
            body("results[2].conditionRelations[0].conditions[0].conditionFreeText", is("Free text")).
            body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm:Test GOTerm:Test ChemicalTerm:Test NCBITaxonTerm:Some amount:Free text")).
            body("results[2].negated", is(true)).
            body("results[2].diseaseGeneticModifier.curie", is("DATEST:Gene0002")).
            body("results[2].diseaseGeneticModifierRelation.name", is("ameliorated_by")).
            body("results[2].with", hasSize(1)).
            body("results[2].with[0].curie", is("HGNC:0001")).
            body("results[2].singleReference.curie", is("PMID:25920554")).
            body("results[2].relatedNotes[0].freeText", is("Test note")).
            body("results[2].relatedNotes[0].noteType.name", is("disease_summary")).
            body("results[2].relatedNotes[0].internal", is(false)).
            body("results[2].relatedNotes[0].references[0].curie", is("PMID:25920554")).
            body("results[0].annotationType.name", is("manually_curated")).
            body("results[0].diseaseQualifiers[0].name", is("susceptibility")).
            body("results[2].evidenceCodes", hasSize(1)).
            body("results[2].evidenceCodes[0].curie", is("DATEST:Evidence0001"));
    }
    
    @Test
    @Order(4)
    public void diseaseAnnotationBulkUploadNoModEntityId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/04_no_mod_id.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)). // Replace 1 WB gene disease annotation with 1
            body("results", hasSize(3)).
            body("results[2].uniqueId", is("DATEST:Gene0001|DATEST:Disease0001|PMID:25920554"));
    }
    
    @Test
    @Order(5)
    public void diseaseAnnotationBulkUploadNoSubject() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/05_no_subject.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); // Replace 1 WB gene disease annotation with 0 
    }
    
    @Test
    @Order(6)
    public void diseaseAnnotationBulkUploadNoObject() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/06_no_object.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(7)
    public void diseaseAnnotationBulkUploadNoDataProvider() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/07_no_data_provider.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }   
    
    @Test
    @Order(8)
    public void diseaseAnnotationBulkUploadNoNegated() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/08_no_negated.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0008")).
            body("results[2].negated", is(false)); 
    }
    
    @Test
    @Order(9)
    public void diseaseAnnotationBulkUploadNoPredicate() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/09_no_predicate.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2));
    }
    
    @Test
    @Order(10)
    public void diseaseAnnotationBulkUploadNoGeneticSex() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/10_no_genetic_sex.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0010"));  
    }
    
    @Test
    @Order(11)
    public void diseaseAnnotationBulkUploadNoModifiedBy() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/11_no_modified_by.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(12)
    public void diseaseAnnotationBulkUploadNoDateLastModified() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/12_no_date_last_modified.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0012")); 
    }
    
    @Test
    @Order(13)
    public void diseaseAnnotationBulkUploadNoCreatedBy() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/13_no_created_by.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(14)
    public void diseaseAnnotationBulkUploadNoCreationDate() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/14_no_creation_date.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0014")); 
    }
    
    @Test
    @Order(15)
    public void diseaseAnnotationBulkUploadNoEvidenceCodes() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/15_no_evidence_codes.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(16)
    public void diseaseAnnotationBulkUploadNoDiseaseGeneticModifier() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/16_no_disease_genetic_modifier.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2));  
    }
    
    @Test
    @Order(17)
    public void diseaseAnnotationBulkUploadNoDiseaseGeneticModifierRelation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/17_no_disease_genetic_modifier_relation.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(18)
    public void diseaseAnnotationBulkUploadNoWith() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/18_no_with.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0018")); 
    }
    
    @Test
    @Order(19)
    public void diseaseAnnotationBulkUploadNoSingleReference() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/19_no_single_reference.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2));  
    }
    
    @Test
    @Order(20)
    public void diseaseAnnotationBulkUploadNoRelatedNotes() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/20_no_related_notes.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0020")); 
    }
    
    @Test
    @Order(21)
    public void diseaseAnnotationBulkUploadNoSgdStrainBackground() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/21_no_sgd_strain_background.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0021")); 
    }
    
    @Test
    @Order(22)
    public void diseaseAnnotationBulkUploadNoConditionRelations() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/22_no_condition_relations.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0022")); 
    }
    
    @Test
    @Order(23)
    public void diseaseAnnotationBulkUploadNoConditionRelationsType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/23_no_condition_relations_type.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(24)
    public void diseaseAnnotationBulkUploadNoConditionRelationExperimentalConditions() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/24_no_condition_relation_experimental_conditions.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); //
    }
    
    @Test
    @Order(25)
    public void diseaseAnnotationBulkUploadNoConditionClass() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/25_no_condition_class.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(26)
    public void diseaseAnnotationBulkUploadNoConditionStatement() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/26_no_condition_statement.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(27)
    public void diseaseAnnotationBulkUploadNoConditionId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/27_no_condition_id.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0027")); 
    }
    
    @Test
    @Order(28)
    public void diseaseAnnotationBulkUploadNoConditionQuantity() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/28_no_condition_quantity.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0028")); 
    }
    
    @Test
    @Order(29)
    public void diseaseAnnotationBulkUploadNoConditionGeneOntology() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/29_no_condition_gene_ontology.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0029"));
    }
    
    @Test
    @Order(30)
    public void diseaseAnnotationBulkUploadNoConditionAnatomy() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/30_no_condition_anatomy.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0030")); 
    }
    
    @Test
    @Order(31)
    public void diseaseAnnotationBulkUploadNoConditionTaxon() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/31_no_condition_taxon.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0031")); 
    }
    
    @Test
    @Order(32)
    public void diseaseAnnotationBulkUploadNoConditionChemical() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/32_no_condition_chemical.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(3)).
            body("results[2].uniqueId", is("DATEST:Annot0032")); 
    }
    
    @Test
    @Order(33)
    public void diseaseAnnotationBulkUploadInvalidSubject() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/33_invalid_subject.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(34)
    public void diseaseAnnotationBulkUploadInvalidObject() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/34_invalid_object.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(35)
    public void diseaseAnnotationBulkUploadInvalidGenePredicate() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/35_invalid_gene_predicate.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(2)); 
    }
    
    @Test
    @Order(36)
    public void diseaseAnnotationBulkUploadInvalidAllelePredicate() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/36_invalid_allele_predicate.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)); // Replace 1 WB allele disease annotation with 0 
    }
    
    @Test
    @Order(37)
    public void diseaseAnnotationBulkUploadInvalidAgmPredicate() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/37_invalid_agm_predicate.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); // Replace 1 WB AGM disease annotation with 0 
    }
    
    @Test
    @Order(38)
    public void diseaseAnnotationBulkUploadInvalidGeneticSex() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/38_invalid_genetic_sex.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(39)
    public void diseaseAnnotationBulkUploadInvalidEvidenceCode() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/39_invalid_evidence_code.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(40)
    public void diseaseAnnotationBulkUploadInvalidGeneticModifier() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/40_invalid_genetic_modifier.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0));
    }
    
    @Test
    @Order(41)
    public void diseaseAnnotationBulkUploadInvalidGeneticModifierRelation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/41_invalid_genetic_modifier_relation.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    // TODO: Update count once validation for field in place
    @Test
    @Order(42)
    public void diseaseAnnotationBulkUploadInvalidSingleReference() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/42_invalid_single_reference.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)); 
    }
    
    @Test
    @Order(43)
    public void diseaseAnnotationBulkUploadInvalidConditionClass() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/43_invalid_condition_class.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(44)
    public void diseaseAnnotationBulkUploadInvalidConditionId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/44_invalid_condition_id.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(45)
    public void diseaseAnnotationBulkUploadInvalidConditionGeneOntology() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/45_invalid_condition_gene_ontology.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0));
    }
    
    @Test
    @Order(46)
    public void diseaseAnnotationBulkUploadInvalidConditionAnatomy() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/46_invalid_condition_anatomy.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(47)
    public void diseaseAnnotationBulkUploadInvalidConditionTaxon() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/47_invalid_condition_taxon.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(48)
    public void diseaseAnnotationBulkUploadInvalidConditionChemical() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/48_invalid_condition_chemical.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(49)
    public void diseaseAnnotationBulkUploadNoAnnotationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/49_no_annotation_type.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)); 
    }
    
    @Test
    @Order(50)
    public void diseaseAnnotationBulkUploadNoDiseaseQualifiers() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/50_no_disease_qualifiers.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)); 
    }
    
    @Test
    @Order(51)
    public void diseaseAnnotationBulkUploadNoRelatedNoteFreeText() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/51_no_related_note_free_text.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(52)
    public void diseaseAnnotationBulkUploadNoRelatedNoteType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/52_no_related_note_type.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(53)
    public void diseaseAnnotationBulkUploadNoRelatedNoteInternal() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/53_no_related_note_internal.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(54)
    public void diseaseAnnotationBulkUploadNoRelatedNoteReferences() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/54_no_related_note_references.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)); 
    }
    
    @Test
    @Order(55)
    public void diseaseAnnotationBulkUploadInvalidSgdStrainBackground() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/55_invalid_sgd_strain_background.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    // TODO: Replace status code when enum changed to vocabulary term
    @Test
    @Order(56)
    public void diseaseAnnotationBulkUploadInvalidAnnotationType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/56_invalid_annotation_type.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(57)
    public void diseaseAnnotationBulkUploadInvalidDiseaseQualifier() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/57_invalid_disease_qualifier.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(58)
    public void diseaseAnnotationBulkUploadInvalidRelatedNoteType() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/58_invalid_related_note_type.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    // TODO: update count once validation for references is in place
    @Test
    @Order(59)
    public void diseaseAnnotationBulkUploadInvalidRelatedNoteReference() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/59_invalid_related_note_reference.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)); 
    }
    
    @Test
    @Order(60)
    public void diseaseAnnotationBulkUploadNoSecondaryDataProvider() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/60_no_secondary_data_provider.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results[0].uniqueId", is("DATEST:Annot0060")); 
    }
    
    @Test
    @Order(61)
    public void diseaseAnnotationBulkUploadNoDiseaseGeneticModifierAndRelation() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/61_no_disease_genetic_modifier_and_relation.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)).
            body("results[0].uniqueId", is("DATEST:Annot0061")); 
    }
    
    @Test
    @Order(62)
    public void diseaseAnnotationBulkUploadInvalidCreationDate() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/62_invalid_creation_date.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(63)
    public void diseaseAnnotationBulkUploadInvalidDateLastModified() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/63_invalid_date_last_modified.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    @Test
    @Order(64)
    public void diseaseAnnotationBulkUploadMissingConditionFreeText() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/64_no_condition_free_text.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(1)); 
    }
    
    @Test
    @Order(65)
    public void diseaseAnnotationBulkUploadNonSlimConditionClass() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/65_non_slim_condition_class.json"));
        
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(0)); 
    }
    
    private void loadRequiredEntities() throws Exception {
        loadDOTerm();
        loadECOTerm();
        loadGOTerm();
        loadExpCondTerm();
        loadZecoTerm(requiredZecoTerm, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
        loadZecoTerm(requiredNonSlimZecoTerm, null);
        loadChemicalTerm();
        loadAnatomyTerm();
        loadGenes();  
        loadAllele();
        loadAGM(requiredAgm);
        loadAGM(requiredSgdBackgroundStrain);
        Vocabulary noteTypeVocabulary = createVocabulary("Disease annotation note types");
        // Vocabulary geneDiseaseRelationVocabulary = createVocabulary("Gene disease relations");
        // Vocabulary alleleDiseaseRelationVocabulary = createVocabulary("Allele disease relations");
        // Vocabulary agmDiseaseRelationVocabulary = createVocabulary("AGM disease relations");
        Vocabulary geneticSexVocabulary = getVocabulary("Genetic sexes");
        // Vocabulary diseaseGeneticModifierRelationVocabulary = createVocabulary("Disease genetic modifier relations");
        Vocabulary diseaseQualifierVocabulary = getVocabulary("Disease qualifiers");
        Vocabulary annotationTypeVocabulary = getVocabulary("Annotation types");
        Vocabulary conditionRelationTypeVocabulary = getVocabulary("Condition relation types");
        createVocabularyTerm(noteTypeVocabulary, requiredNoteType);
        // createVocabularyTerm(geneDiseaseRelationVocabulary, requiredGeneDiseaseRelation);
        // createVocabularyTerm(alleleDiseaseRelationVocabulary, requiredAlleleDiseaseRelation);
        // createVocabularyTerm(agmDiseaseRelationVocabulary, requiredAgmDiseaseRelation);
        createVocabularyTerm(diseaseQualifierVocabulary, requiredDiseaseQualifier);
        createVocabularyTerm(geneticSexVocabulary, requiredGeneticSex);
        // createVocabularyTerm(diseaseGeneticModifierRelationVocabulary, requiredDiseaseGeneticModifierRelation);
        createVocabularyTerm(annotationTypeVocabulary, requiredAnnotationType);
        createVocabularyTerm(conditionRelationTypeVocabulary, requiredConditionRelationType);
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

    private void loadAnatomyTerm() throws Exception {
        AnatomicalTerm anatomicalTerm = new AnatomicalTerm();
        anatomicalTerm.setCurie(requiredAnatomicalTerm);
        anatomicalTerm.setName("Test AnatomicalTerm");
        anatomicalTerm.setObsolete(false);
        
        RestAssured.given().
            contentType("application/json").
            body(anatomicalTerm).
            when().
            put("/api/anatomicalterm").
            then().
            statusCode(200);
    }

    private void loadChemicalTerm() throws Exception {
        ChemicalTerm chemicalTerm = new ChemicalTerm();
        chemicalTerm.setCurie(requiredChemicalTerm);
        chemicalTerm.setName("Test ChemicalTerm");
        chemicalTerm.setObsolete(false);
        
        RestAssured.given().
            contentType("application/json").
            body(chemicalTerm).
            when().
            put("/api/chemicalterm").
            then().
            statusCode(200);
    }
    
    private void loadZecoTerm(String name, String subset) throws Exception {
        ZecoTerm zecoTerm = new ZecoTerm();
        zecoTerm.setCurie(name);
        zecoTerm.setName("Test ExperimentalConditionOntologyTerm");
        zecoTerm.setObsolete(false);
        List<String> subsets = new ArrayList<String>();
        if (subset != null) {
            subsets.add(subset);
            zecoTerm.setSubsets(subsets);
        }
            
        RestAssured.given().
            contentType("application/json").
            body(zecoTerm).
            when().
            post("/api/zecoterm").
            then().
            statusCode(200);
    }
    
    private void loadExpCondTerm() throws Exception {
        ExperimentalConditionOntologyTerm ecTerm = new ExperimentalConditionOntologyTerm();
        ecTerm.setCurie(requiredExpCondTerm);
        ecTerm.setName("Test ExperimentalConditionOntologyTerm");
        ecTerm.setObsolete(false);

        RestAssured.given().
            contentType("application/json").
            body(ecTerm).
            when().
            post("/api/experimentalconditionontologyterm").
            then().
            statusCode(200);
    }
    
    private void loadGenes() throws Exception {
        for (String geneCurie : requiredGenes) {
            Gene gene = new Gene();
            gene.setCurie(geneCurie);
            gene.setTaxon(getTaxon("NCBITaxon:6239"));

            RestAssured.given().
                    contentType("application/json").
                    body(gene).
                    when().
                    post("/api/gene").
                    then().
                    statusCode(200);
        }
    }

    private void loadAGM(String agmCurie) throws Exception {
        AffectedGenomicModel agm = new AffectedGenomicModel();
        agm.setCurie(agmCurie);
        agm.setTaxon(getTaxon("NCBITaxon:6239"));
        agm.setName("Test AGM");

        RestAssured.given().
            contentType("application/json").
            body(agm).
            when().
            post("/api/agm").
            then().
            statusCode(200);
    }

    private void loadAllele() throws Exception {
        Allele allele = new Allele();
        allele.setCurie(requiredAllele);
        allele.setTaxon(getTaxon("NCBITaxon:6239"));

        RestAssured.given().
            contentType("application/json").
            body(allele).
            when().
            post("/api/allele").
            then().
            statusCode(200);
    }
    
    private Vocabulary createVocabulary(String name) {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setName(name);
        
        ObjectResponse<Vocabulary> response = 
            RestAssured.given().
                contentType("application/json").
                body(vocabulary).
                when().
                post("/api/vocabulary").
                then().
                statusCode(200).
                extract().body().as(getObjectResponseTypeRefVocabulary());
        
        vocabulary = response.getEntity();
        
        return vocabulary;
    }
    
    private Vocabulary getVocabulary(String name) {
        ObjectResponse<Vocabulary> response = 
            RestAssured.given().
                when().
                get("/api/vocabulary/findBy/" + name).
                then().
                statusCode(200).
                extract().body().as(getObjectResponseTypeRefVocabulary());
        
        Vocabulary vocabulary = response.getEntity();
        
        return vocabulary;
    }

    private void createVocabularyTerm(Vocabulary vocabulary, String name) {
        VocabularyTerm vocabularyTerm = new VocabularyTerm();
        vocabularyTerm.setName(name);
        vocabularyTerm.setVocabulary(vocabulary);
        
        RestAssured.given().
                contentType("application/json").
                body(vocabularyTerm).
                when().
                post("/api/vocabularyterm").
                then().
                statusCode(200);
    }
    
    private NCBITaxonTerm getTaxon(String taxonCurie) {
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
    
    private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
        return new TypeRef<ObjectResponse <Vocabulary>>() { };
    }
    
    private void clearExistingDiseaseAnnotations() throws Exception {
        ArrayList<String> diseaseAnnotationMods = new ArrayList<String>(Arrays.asList("human", "fb", "mgi", "rgd", "sgd", "wb", "zfin"));
        for (String mod : diseaseAnnotationMods) {
            RestAssured.given().
            contentType("application/json").
            body("{\"data\":[],\"metaData\":{}}").
            when().
            post("/api/disease-annotation/bulk/" + mod + "AnnotationFileFms").
            then().
            statusCode(200);
        }
    
    }
    
}
