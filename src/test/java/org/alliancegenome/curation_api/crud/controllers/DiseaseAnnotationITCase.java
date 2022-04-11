package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(10)
public class DiseaseAnnotationITCase {

    private final String GENE_DISEASE_ANNOTATION = "GeneDisease:0001";
    private final String ALLELE_DISEASE_ANNOTATION = "AlleleDisease:0001";
    private final String AGM_DISEASE_ANNOTATION = "AgmDisease:0001";
    
    private DOTerm testDoTerm;
    private DOTerm testDoTerm2;
    private DOTerm testObsoleteDoTerm;
    private List<EcoTerm> testEcoTerm;
    private List<EcoTerm> testEcoTerm2;
    private List<EcoTerm> testObsoleteEcoTerm;
    private Gene testGene;
    private Gene testGene2;
    private List<Gene> testWithGene;
    private Allele testAllele;
    private Allele testAllele2;
    private AffectedGenomicModel testAgm;
    private AffectedGenomicModel testAgm2;
    private BiologicalEntity testBiologicalEntity;
    private Vocabulary geneDiseaseRelationVocabulary;
    private Vocabulary alleleDiseaseRelationVocabulary;
    private Vocabulary agmDiseaseRelationVocabulary;
    private Vocabulary geneticSexVocabulary;
    private Vocabulary diseaseGeneticModifierRelationVocabulary;
    private Vocabulary diseaseQualifierVocabulary;
    private Vocabulary annotationTypeVocabulary;
    private VocabularyTerm geneDiseaseRelation;
    private VocabularyTerm geneDiseaseRelation2;
    private VocabularyTerm alleleDiseaseRelation;
    private VocabularyTerm agmDiseaseRelation;
    private VocabularyTerm geneticSex;
    private VocabularyTerm diseaseGeneticModifierRelation;
    private List<VocabularyTerm> diseaseQualifier;
    private VocabularyTerm annotationType;
    private String testPerson;
    private OffsetDateTime testDate;

    private TypeRef<ObjectResponse<GeneDiseaseAnnotation>> getObjectResponseTypeRef() {
        return new TypeRef<>() {
        };
    }

    
    private void createRequiredObjects() {
        testEcoTerm = new ArrayList<EcoTerm>();
        testEcoTerm2 = new ArrayList<EcoTerm>();
        testObsoleteEcoTerm = new ArrayList<EcoTerm>();
        testWithGene = new ArrayList<Gene>();
        diseaseQualifier = new ArrayList<VocabularyTerm>();
        
        testDoTerm = createDiseaseTerm("DOID:da0001", false);
        testDoTerm2 = createDiseaseTerm("DOID:da0002", false);
        testObsoleteDoTerm = createDiseaseTerm("DOID:da0003", true);
        testEcoTerm.add(createEcoTerm("ECO:da0001", "Test evidence code", false));
        testEcoTerm2.add(createEcoTerm("ECO:da0002", "Test evidence code2", false));
        testObsoleteEcoTerm.add(createEcoTerm("ECO:da0003", "Test obsolete evidence code", true));
        testGene = createGene("GENE:da0001", "NCBITaxon:9606");
        testGene2 = createGene("GENE:da0002","NCBITaxon:9606");
        testWithGene.add(createGene("HGNC:1", "NCBITaxon:9606"));
        testAllele = createAllele("ALLELE:da0001", "NCBITaxon:9606");
        testAllele2 = createAllele("ALLELE:da0002", "NCBITaxon:9606");
        testAgm = createModel("MODEL:da0001", "NCBITaxon:9606", "TestAGM");
        testAgm2 = createModel("SGD:da0002", "NCBITaxon:9606", "TestAGM2");
        testBiologicalEntity = createBiologicalEntity("BE:da0001", "NCBITaxon:9606");
        // geneDiseaseRelationVocabulary = createVocabulary("Gene disease relations");
        // alleleDiseaseRelationVocabulary = createVocabulary("Allele disease relations");
        // agmDiseaseRelationVocabulary = createVocabulary("AGM disease relations");
        geneDiseaseRelationVocabulary = getVocabulary("Gene disease relations");
        alleleDiseaseRelationVocabulary = getVocabulary("Allele disease relations");
        agmDiseaseRelationVocabulary = getVocabulary("AGM disease relations");
        geneticSexVocabulary = createVocabulary("Genetic sexes");
        diseaseGeneticModifierRelationVocabulary = createVocabulary("Disease genetic modifier relations");
        diseaseQualifierVocabulary = createVocabulary("Disease qualifiers");
        annotationTypeVocabulary = createVocabulary("Annotation types");
        geneDiseaseRelation = getVocabularyTerm(geneDiseaseRelationVocabulary, "is_implicated_in");
        geneDiseaseRelation2 = createVocabularyTerm(geneDiseaseRelationVocabulary, "is_marker_for");
        alleleDiseaseRelation = getVocabularyTerm(alleleDiseaseRelationVocabulary, "is_implicated_in");
        agmDiseaseRelation = getVocabularyTerm(agmDiseaseRelationVocabulary, "is_model_of");
        diseaseQualifier.add(createVocabularyTerm(diseaseQualifierVocabulary, "severity"));
        geneticSex = createVocabularyTerm(geneticSexVocabulary,"hermaphrodite");
        diseaseGeneticModifierRelation = createVocabularyTerm(diseaseGeneticModifierRelationVocabulary, "ameliorated_by");
        annotationType = createVocabularyTerm(annotationTypeVocabulary,"computational");
        testPerson = "TEST:Person0001";
        testDate = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
    }

    @Test
    @Order(1)
    public void createGeneDiseaseAnnotation() {
        createRequiredObjects();
        
        GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        diseaseAnnotation.setUniqueId(GENE_DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(testDoTerm);
        diseaseAnnotation.setDataProvider("TEST");
        diseaseAnnotation.setSubject(testGene);
        diseaseAnnotation.setEvidenceCodes(testEcoTerm);
        diseaseAnnotation.setModifiedBy(testPerson);
        diseaseAnnotation.setCreatedBy(testPerson);

        RestAssured.given().
                contentType("application/json").
                body(diseaseAnnotation).
                when().
                post("/api/gene-disease-annotation").
                then().
                statusCode(200);
        RestAssured.given().
                when().
                get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
                then().
                statusCode(200).
                body("entity.uniqueId", is(GENE_DISEASE_ANNOTATION)).
                body("entity.subject.curie", is("GENE:da0001")).
                body("entity.object.curie", is("DOID:da0001")).
                body("entity.diseaseRelation.name", is("is_implicated_in")).
                body("entity.negated", is(false)).
                body("entity.dataProvider", is("TEST")).
                body("entity.evidenceCodes[0].curie", is("ECO:da0001"));
    }

    @Test
    @Order(2)
    public void createAlleleDiseaseAnnotation() {

        AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
        diseaseAnnotation.setUniqueId(ALLELE_DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(testDoTerm);
        diseaseAnnotation.setDataProvider("TEST");
        diseaseAnnotation.setSubject(testAllele);
        diseaseAnnotation.setEvidenceCodes(testEcoTerm);
        diseaseAnnotation.setModifiedBy(testPerson);
        diseaseAnnotation.setCreatedBy(testPerson);

        RestAssured.given().
                contentType("application/json").
                body(diseaseAnnotation).
                when().
                post("/api/allele-disease-annotation").
                then().
                statusCode(200);
        RestAssured.given().
                when().
                get("/api/allele-disease-annotation/findBy/" + ALLELE_DISEASE_ANNOTATION).
                then().
                statusCode(200).
                body("entity.uniqueId", is(ALLELE_DISEASE_ANNOTATION)).
                body("entity.subject.curie", is("ALLELE:da0001")).
                body("entity.object.curie", is("DOID:da0001")).
                body("entity.diseaseRelation.name", is("is_implicated_in")).
                body("entity.negated", is(false)).
                body("entity.dataProvider", is("TEST")).
                body("entity.evidenceCodes[0].curie", is("ECO:da0001"));
    }

    @Test
    @Order(3)
    public void createAgmDiseaseAnnotation() {

        AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
        diseaseAnnotation.setUniqueId(AGM_DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(testDoTerm);
        diseaseAnnotation.setDataProvider("TEST");
        diseaseAnnotation.setSubject(testAgm);
        diseaseAnnotation.setEvidenceCodes(testEcoTerm);
        diseaseAnnotation.setModifiedBy(testPerson);
        diseaseAnnotation.setCreatedBy(testPerson);

        RestAssured.given().
                contentType("application/json").
                body(diseaseAnnotation).
                when().
                post("/api/agm-disease-annotation").
                then().
                statusCode(200);
        RestAssured.given().
                when().
                get("/api/agm-disease-annotation/findBy/" + AGM_DISEASE_ANNOTATION).
                then().
                statusCode(200).
                body("entity.uniqueId", is(AGM_DISEASE_ANNOTATION)).
                body("entity.subject.curie", is("MODEL:da0001")).
                body("entity.object.curie", is("DOID:da0001")).
                body("entity.diseaseRelation.name", is("is_model_of")).
                body("entity.negated", is(false)).
                body("entity.dataProvider", is("TEST")).
                body("entity.evidenceCodes[0].curie", is("ECO:da0001"));
    }

    @Test
    @Order(4)
    public void editGeneDiseaseAnnotation() {
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation2);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(200);

        RestAssured.given().
                when().
                get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
                then().
                statusCode(200).
                body("entity.uniqueId", is(GENE_DISEASE_ANNOTATION)).
                body("entity.modEntityId", is("TEST:Mod0001")).
                body("entity.subject.curie", is("GENE:da0002")).
                body("entity.object.curie", is("DOID:da0002")).
                body("entity.negated", is(true)).
                body("entity.evidenceCodes[0].curie", is("ECO:da0002")).
                body("entity.diseaseRelation.name", is("is_marker_for")).
                body("entity.dataProvider", is("TEST2")).
                body("entity.secondaryDataProvider", is("TEST3")).
                body("entity.geneticSex.name", is("hermaphrodite")).
                body("entity.diseaseGeneticModifierRelation.name", is("ameliorated_by")).
                body("entity.diseaseGeneticModifier.curie", is("BE:da0001")).
                body("entity.annotationType.name", is("computational")).
                body("entity.diseaseQualifiers[0].name", is("severity")).
                body("entity.with[0].curie", is("HGNC:1")).
                body("entity.sgdStrainBackground.curie", is("SGD:da0002")).
                body("entity.modifiedBy", is("Local Dev User")).
                body("entity.createdBy", is("TEST:Person0001")).
                body("entity.creationDate".toString(), is("2022-03-09T22:10:12Z"));
    }

    @Test
    @Order(5)
    public void editAlleleDiseaseAnnotation() {
        
        AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testAllele2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0002");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/allele-disease-annotation").
                then().
                statusCode(200);

        RestAssured.given().
                when().
                get("/api/allele-disease-annotation/findBy/" + ALLELE_DISEASE_ANNOTATION).
                then().
                statusCode(200).
                body("entity.uniqueId", is(ALLELE_DISEASE_ANNOTATION)).
                body("entity.modEntityId", is("TEST:Mod0002")).
                body("entity.subject.curie", is("ALLELE:da0002")).
                body("entity.object.curie", is("DOID:da0002")).
                body("entity.negated", is(true)).
                body("entity.evidenceCodes[0].curie", is("ECO:da0002")).
                body("entity.diseaseRelation.name", is("is_implicated_in")).
                body("entity.dataProvider", is("TEST2")).
                body("entity.secondaryDataProvider", is("TEST3")).
                body("entity.geneticSex.name", is("hermaphrodite")).
                body("entity.diseaseGeneticModifier.curie", is("BE:da0001")).
                body("entity.diseaseGeneticModifierRelation.name", is("ameliorated_by")).
                body("entity.annotationType.name", is("computational")).
                body("entity.diseaseQualifiers[0].name", is("severity")).
                body("entity.with[0].curie", is("HGNC:1")).
                body("entity.modifiedBy", is("Local Dev User")).
                body("entity.createdBy", is("TEST:Person0001")).
                body("entity.creationDate".toString(), is("2022-03-09T22:10:12Z"));

    }
    
    @Test
    @Order(6)
    public void editAgmDiseaseAnnotation() {
        
        AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testAgm2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0003");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/agm-disease-annotation").
                then().
                statusCode(200);

        RestAssured.given().
                when().
                get("/api/agm-disease-annotation/findBy/" + AGM_DISEASE_ANNOTATION).
                then().
                statusCode(200).
                body("entity.uniqueId", is(AGM_DISEASE_ANNOTATION)).
                body("entity.modEntityId", is("TEST:Mod0003")).
                body("entity.subject.curie", is("SGD:da0002")).
                body("entity.object.curie", is("DOID:da0002")).
                body("entity.negated", is(true)).
                body("entity.evidenceCodes[0].curie", is("ECO:da0002")).
                body("entity.diseaseRelation.name", is("is_model_of")).
                body("entity.dataProvider", is("TEST2")).
                body("entity.secondaryDataProvider", is("TEST3")).
                body("entity.geneticSex.name", is("hermaphrodite")).
                body("entity.diseaseGeneticModifier.curie", is("BE:da0001")).
                body("entity.diseaseGeneticModifierRelation.name", is("ameliorated_by")).
                body("entity.annotationType.name", is("computational")).
                body("entity.diseaseQualifiers[0].name", is("severity")).
                body("entity.with[0].curie", is("HGNC:1")).
                body("entity.modifiedBy", is("Local Dev User")).
                body("entity.createdBy", is("TEST:Person0001")).
                body("entity.creationDate".toString(), is("2022-03-09T22:10:12Z"));

    }

    @Test
    @Order(7)
    public void editWithObsoleteEcoTerm() {
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testObsoleteEcoTerm);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);

        RestAssured.given().
            contentType("application/json").
            body(editedDiseaseAnnotation).
            when().
            put("/api/gene-disease-annotation").
            then().
            statusCode(400);
    }

    @Test
    @Order(8)
    public void editWithObsoleteDoTerm() {
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testObsoleteDoTerm);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
            contentType("application/json").
            body(editedDiseaseAnnotation).
            when().
            put("/api/gene-disease-annotation").
            then().
            statusCode(400);
    }
    
    @Test
    @Order(9)
    public void editWithMissingSubject() {
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(null);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(10)
    public void editWithMissingObject() {
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(null);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(11)
    public void editWithMissingDiseaseRelation() {
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(null);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    // TODO: re-enable test response once field can be added in UI
    // @Test
    @Order(12)
    public void editWithMissingDataProvider() {
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider(null);
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(13)
    public void editWithInvalidSubject() {
        
        Gene nonPersistedGene = new Gene();
        nonPersistedGene.setCurie("NPGene:0001");
        nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(nonPersistedGene);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(14)
    public void editWithInvalidObject() {
        
        DOTerm nonPersistedDoTerm = new DOTerm();
        nonPersistedDoTerm.setCurie("NPDO:0001");
        nonPersistedDoTerm.setObsolete(false);
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(nonPersistedDoTerm);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(15)
    public void editWithInvalidDiseaseRelation() {
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider(null);
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(16)
    public void editWithInvalidEvidenceCode() {
        
        EcoTerm nonPersistedEcoTerm = new EcoTerm();
        nonPersistedEcoTerm.setCurie("NPECO:0001");
        nonPersistedEcoTerm.setObsolete(false);
        
        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(nonPersistedEcoTerm);
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(ecoTerms);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(17)
    public void editWithInvalidDiseaseGeneticModifier() {
        
        BiologicalEntity nonPersistedBiologicalEntity = new BiologicalEntity();
        nonPersistedBiologicalEntity.setCurie("NPBE:0001");
        nonPersistedBiologicalEntity.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(nonPersistedBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(18)
    public void editWithInvalidWithGene() {
        
        List<Gene> withGenes = new ArrayList<>();
        withGenes.add(testGene2);
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(withGenes);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(19)
    public void editWithInvalidSgdStrainBackground() {
        
        AffectedGenomicModel nonPersistedModel = new AffectedGenomicModel();
        nonPersistedModel.setCurie("NPModel:0001");
        nonPersistedModel.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(nonPersistedModel);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);
        
        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }
    
    // TODO: re-enable test once field can be added in UI
    // @Test
    @Order(20)
    public void editWithMissingCreatedBy() {
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(testPerson);
        editedDiseaseAnnotation.setCreatedBy(null);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);

        RestAssured.given().
            contentType("application/json").
            body(editedDiseaseAnnotation).
            when().
            put("/api/gene-disease-annotation").
            then().
            statusCode(400);
    }
    
    // TODO: re-enable test once field can be added in UI
    // @Test
    @Order(21)
    public void editWithMissingModifiedBy() {
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(testEcoTerm2);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(geneticSex);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
        editedDiseaseAnnotation.setAnnotationType(annotationType);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifier);
        editedDiseaseAnnotation.setWith(testWithGene);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        editedDiseaseAnnotation.setModifiedBy(null);
        editedDiseaseAnnotation.setCreatedBy(testPerson);
        editedDiseaseAnnotation.setDateLastModified(testDate);
        editedDiseaseAnnotation.setCreationDate(testDate);

        RestAssured.given().
            contentType("application/json").
            body(editedDiseaseAnnotation).
            when().
            put("/api/gene-disease-annotation").
            then().
            statusCode(400);
    }

    private GeneDiseaseAnnotation getGeneDiseaseAnnotation() {
        ObjectResponse<GeneDiseaseAnnotation> res = RestAssured.given().
                when().
                get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
                then().
                statusCode(200).
                extract().body().as(getObjectResponseTypeRef());

        return res.getEntity();
    }

    private AlleleDiseaseAnnotation getAlleleDiseaseAnnotation() {
        ObjectResponse<AlleleDiseaseAnnotation> res = RestAssured.given().
                when().
                get("/api/allele-disease-annotation/findBy/" + ALLELE_DISEASE_ANNOTATION).
                then().
                statusCode(200).
                extract().body().as(new TypeRef<>() {
        });

        return res.getEntity();
    }

    private AGMDiseaseAnnotation getAgmDiseaseAnnotation() {
        ObjectResponse<AGMDiseaseAnnotation> res = RestAssured.given().
                when().
                get("/api/agm-disease-annotation/findBy/" + AGM_DISEASE_ANNOTATION).
                then().
                statusCode(200).
                extract().body().as(new TypeRef<>() {
        });

        return res.getEntity();
    }
    
    private BiologicalEntity createBiologicalEntity(String curie, String taxon) {
        BiologicalEntity bioEntity = new BiologicalEntity();
        bioEntity.setCurie(curie);
        bioEntity.setTaxon(getTaxonFromCurie(taxon));
        
        RestAssured.given().
                contentType("application/json").
                body(bioEntity).
                when().
                post("/api/biologicalentity").
                then().
                statusCode(200);
        
        return bioEntity;
    }


    private DOTerm createDiseaseTerm(String curie, Boolean obsolete) {
        DOTerm doTerm = new DOTerm();
        doTerm.setCurie(curie);
        doTerm.setObsolete(obsolete);

        RestAssured.given().
                contentType("application/json").
                body(doTerm).
                when().
                post("/api/doterm").
                then().
                statusCode(200);
        return doTerm;
    }


    private EcoTerm createEcoTerm(String curie, String name, Boolean obsolete) {
        EcoTerm ecoTerm = new EcoTerm();
        ecoTerm.setCurie(curie);
        ecoTerm.setName(name);
        ecoTerm.setObsolete(obsolete);

        RestAssured.given().
                contentType("application/json").
                body(ecoTerm).
                when().
                post("/api/ecoterm").
                then().
                statusCode(200);
        return ecoTerm;
    }


    private Gene createGene(String curie, String taxon) {
        Gene gene = new Gene();
        gene.setCurie(curie);
        gene.setTaxon(getTaxonFromCurie(taxon));

        RestAssured.given().
                contentType("application/json").
                body(gene).
                when().
                post("/api/gene").
                then().
                statusCode(200);
        return gene;
    }

    private Allele createAllele(String curie, String taxon) {
        Allele allele = new Allele();
        allele.setCurie(curie);
        allele.setTaxon(getTaxonFromCurie(taxon));

        RestAssured.given().
                contentType("application/json").
                body(allele).
                when().
                post("/api/allele").
                then().
                statusCode(200);
        return allele;
    }

    private AffectedGenomicModel createModel(String curie, String taxon, String name) {
        AffectedGenomicModel model = new AffectedGenomicModel();
        model.setCurie(curie);
        model.setTaxon(getTaxonFromCurie(taxon));
        model.setName(name);

        RestAssured.given().
                contentType("application/json").
                body(model).
                when().
                post("/api/agm").
                then().
                statusCode(200);
        return model;
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

    private VocabularyTerm createVocabularyTerm(Vocabulary vocabulary, String name) {
        VocabularyTerm vocabularyTerm = new VocabularyTerm();
        vocabularyTerm.setName(name);
        vocabularyTerm.setVocabulary(vocabulary);
        
        ObjectResponse<VocabularyTerm> response = 
            RestAssured.given().
                contentType("application/json").
                body(vocabularyTerm).
                when().
                post("/api/vocabularyterm").
                then().
                statusCode(200).
                extract().body().as(getObjectResponseTypeRefVocabularyTerm());
        
        vocabularyTerm = response.getEntity();
        
        return vocabularyTerm;
    }
    
    private VocabularyTerm getVocabularyTerm(Vocabulary vocabulary, String name) {
        ObjectListResponse<VocabularyTerm> response = 
            RestAssured.given().
                when().
                get("/api/vocabulary/" + vocabulary.getId() + "/terms").
                then().
                statusCode(200).
                extract().body().as(getObjectListResponseTypeRefVocabularyTerm());
        
        List<VocabularyTerm> vocabularyTerms = response.getEntities();
        for (VocabularyTerm vocabularyTerm : vocabularyTerms) {
            if (vocabularyTerm.getName().equals(name)) {
                return vocabularyTerm;
            }
        }
        
        return null;
    }
    
    private NCBITaxonTerm getTaxonFromCurie(String taxonCurie) {
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
    
    private TypeRef<ObjectResponse<VocabularyTerm>> getObjectResponseTypeRefVocabularyTerm() {
        return new TypeRef<ObjectResponse <VocabularyTerm>>() { };
    }
    
    private TypeRef<ObjectListResponse<VocabularyTerm>> getObjectListResponseTypeRefVocabularyTerm() {
        return new TypeRef<ObjectListResponse <VocabularyTerm>>() { };
    }
}
