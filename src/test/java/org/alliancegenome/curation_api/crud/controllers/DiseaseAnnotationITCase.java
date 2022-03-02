package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.AnnotationType;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseGeneticModifierRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseQualifier;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.GeneticSex;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;

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
    private EcoTerm testEcoTerm;
    private EcoTerm testEcoTerm2;
    private EcoTerm testObsoleteEcoTerm;
    private Gene testGene;
    private Gene testGene2;
    private Gene testGene3;
    private Gene testWithGene;
    private Allele testAllele;
    private Allele testAllele2;
    private AffectedGenomicModel testAgm;
    private AffectedGenomicModel testAgm2;
    private BiologicalEntity testBiologicalEntity;

    private TypeRef<ObjectResponse<GeneDiseaseAnnotation>> getObjectResponseTypeRef() {
        return new TypeRef<>() {
        };
    }

    
    private void createRequiredObjects() {
        testDoTerm = createDiseaseTerm("DOID:da0001", false);
        testDoTerm2 = createDiseaseTerm("DOID:da0002", false);
        testObsoleteDoTerm = createDiseaseTerm("DOID:da0003", true);
        testEcoTerm = createEcoTerm("ECO:da0001", "Test evidence code", false);
        testEcoTerm2 = createEcoTerm("ECO:da0002", "Test evidence code2", false);
        testObsoleteEcoTerm = createEcoTerm("ECO:da0003", "Test obsolete evidence code", true);
        testGene = createGene("GENE:da0001", "NCBITaxon:9606");
        testGene2 = createGene("GENE:da0002","NCBITaxon:9606");
        testGene3 = createGene("GENE:da0003","NCBITaxon:9606");
        testWithGene = createGene("HGNC:1", "NCBITaxon:9606");
        testAllele = createAllele("ALLELE:da0001", "NCBITaxon:9606");
        testAllele2 = createAllele("ALLELE:da0002", "NCBITaxon:9606");
        testAgm = createModel("MODEL:da0001", "NCBITaxon:9606", "TestAGM");
        testAgm2 = createModel("MODEL:da0002", "NCBITaxon:9606", "TestAGM2");
        testBiologicalEntity = createBiologicalEntity("BE:da0001", "NCBITaxon:9606");
    }

    @Test
    @Order(1)
    public void createGeneDiseaseAnnotation() {
        createRequiredObjects();
        
        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(testEcoTerm);
        
        GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.is_implicated_in);
        diseaseAnnotation.setUniqueId(GENE_DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(testDoTerm);
        diseaseAnnotation.setDataProvider("TEST");
        diseaseAnnotation.setSubject(testGene);
        diseaseAnnotation.setEvidenceCodes(ecoTerms);

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
                body("entity.diseaseRelation", is("is_implicated_in")).
                body("entity.negated", is(false)).
                body("entity.dataProvider", is("TEST")).
                body("entity.evidenceCodes[0].curie", is("ECO:da0001"));
    }

    @Test
    @Order(2)
    public void createAlleleDiseaseAnnotation() {

        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(testEcoTerm);

        AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.is_implicated_in);
        diseaseAnnotation.setUniqueId(ALLELE_DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(testDoTerm);
        diseaseAnnotation.setDataProvider("TEST");
        diseaseAnnotation.setSubject(testAllele);
        diseaseAnnotation.setEvidenceCodes(ecoTerms);

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
                body("entity.diseaseRelation", is("is_implicated_in")).
                body("entity.negated", is(false)).
                body("entity.dataProvider", is("TEST")).
                body("entity.evidenceCodes[0].curie", is("ECO:da0001"));
    }

    @Test
    @Order(3)
    public void createAgmDiseaseAnnotation() {

        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(testEcoTerm);

        AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.is_model_of);
        diseaseAnnotation.setUniqueId(AGM_DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(testDoTerm);
        diseaseAnnotation.setDataProvider("TEST");
        diseaseAnnotation.setSubject(testAgm);
        diseaseAnnotation.setEvidenceCodes(ecoTerms);

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
                body("entity.diseaseRelation", is("is_model_of")).
                body("entity.negated", is(false)).
                body("entity.dataProvider", is("TEST")).
                body("entity.evidenceCodes[0].curie", is("ECO:da0001"));
    }

    @Test
    @Order(4)
    public void editGeneDiseaseAnnotation() {
        
        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(testEcoTerm2);
        List<DiseaseQualifier> diseaseQualifiers = new ArrayList<>();
        diseaseQualifiers.add(DiseaseQualifier.severity);
        List<Gene> withGenes = new ArrayList<>();
        withGenes.add(testWithGene);
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(DiseaseRelation.is_marker_for);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(ecoTerms);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(GeneticSex.hermaphrodite);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(DiseaseGeneticModifierRelation.ameliorated_by);
        editedDiseaseAnnotation.setAnnotationType(AnnotationType.computational);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
        editedDiseaseAnnotation.setWith(withGenes);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        
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
                body("entity.diseaseRelation", is("is_marker_for")).
                body("entity.dataProvider", is("TEST2")).
                body("entity.secondaryDataProvider", is("TEST3")).
                body("entity.geneticSex", is("hermaphrodite")).
                body("entity.diseaseGeneticModifierRelation", is("ameliorated_by")).
                body("entity.diseaseGeneticModifier.curie", is("BE:da0001")).
                body("entity.annotationType", is("computational")).
                body("entity.diseaseQualifiers[0]", is("severity")).
                body("entity.with[0].curie", is("HGNC:1")).
                body("entity.sgdStrainBackground.curie", is("MODEL:da0002"));
    }

    @Test
    @Order(5)
    public void editAlleleDiseaseAnnotation() {
        
        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(testEcoTerm2);
        List<DiseaseQualifier> diseaseQualifiers = new ArrayList<>();
        diseaseQualifiers.add(DiseaseQualifier.severity);
        List<Gene> withGenes = new ArrayList<>();
        withGenes.add(testWithGene);
        
        AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(DiseaseRelation.is_implicated_in);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testAllele2);
        editedDiseaseAnnotation.setEvidenceCodes(ecoTerms);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0002");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(GeneticSex.hermaphrodite);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(DiseaseGeneticModifierRelation.ameliorated_by);
        editedDiseaseAnnotation.setAnnotationType(AnnotationType.computational);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
        editedDiseaseAnnotation.setWith(withGenes);
        
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
                body("entity.diseaseRelation", is("is_implicated_in")).
                body("entity.dataProvider", is("TEST2")).
                body("entity.secondaryDataProvider", is("TEST3")).
                body("entity.geneticSex", is("hermaphrodite")).
                body("entity.diseaseGeneticModifier.curie", is("BE:da0001")).
                body("entity.diseaseGeneticModifierRelation", is("ameliorated_by")).
                body("entity.annotationType", is("computational")).
                body("entity.diseaseQualifiers[0]", is("severity")).
                body("entity.with[0].curie", is("HGNC:1"));

    }
    
    @Test
    @Order(6)
    public void editAgmDiseaseAnnotation() {
        
        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(testEcoTerm2);
        List<DiseaseQualifier> diseaseQualifiers = new ArrayList<>();
        diseaseQualifiers.add(DiseaseQualifier.severity);
        List<Gene> withGenes = new ArrayList<>();
        withGenes.add(testWithGene);
        
        AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(DiseaseRelation.is_model_of);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testAgm2);
        editedDiseaseAnnotation.setEvidenceCodes(ecoTerms);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0003");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(GeneticSex.hermaphrodite);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(DiseaseGeneticModifierRelation.ameliorated_by);
        editedDiseaseAnnotation.setAnnotationType(AnnotationType.computational);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
        editedDiseaseAnnotation.setWith(withGenes);
        
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
                body("entity.subject.curie", is("MODEL:da0002")).
                body("entity.object.curie", is("DOID:da0002")).
                body("entity.negated", is(true)).
                body("entity.evidenceCodes[0].curie", is("ECO:da0002")).
                body("entity.diseaseRelation", is("is_model_of")).
                body("entity.dataProvider", is("TEST2")).
                body("entity.secondaryDataProvider", is("TEST3")).
                body("entity.geneticSex", is("hermaphrodite")).
                body("entity.diseaseGeneticModifier.curie", is("BE:da0001")).
                body("entity.diseaseGeneticModifierRelation", is("ameliorated_by")).
                body("entity.annotationType", is("computational")).
                body("entity.diseaseQualifiers[0]", is("severity")).
                body("entity.with[0].curie", is("HGNC:1"));

    }

    @Test
    @Order(7)
    public void editWithObsoleteEcoTerm() {
        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(testObsoleteEcoTerm);
        List<DiseaseQualifier> diseaseQualifiers = new ArrayList<>();
        diseaseQualifiers.add(DiseaseQualifier.severity);
        List<Gene> withGenes = new ArrayList<>();
        withGenes.add(testWithGene);
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(DiseaseRelation.is_marker_for);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testDoTerm2);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(ecoTerms);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(GeneticSex.hermaphrodite);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(DiseaseGeneticModifierRelation.ameliorated_by);
        editedDiseaseAnnotation.setAnnotationType(AnnotationType.computational);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
        editedDiseaseAnnotation.setWith(withGenes);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);

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
        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(testEcoTerm2);
        List<DiseaseQualifier> diseaseQualifiers = new ArrayList<>();
        diseaseQualifiers.add(DiseaseQualifier.severity);
        List<Gene> withGenes = new ArrayList<>();
        withGenes.add(testWithGene);
        
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setDiseaseRelation(DiseaseRelation.is_marker_for);
        editedDiseaseAnnotation.setNegated(true);
        editedDiseaseAnnotation.setObject(testObsoleteDoTerm);
        editedDiseaseAnnotation.setDataProvider("TEST2");
        editedDiseaseAnnotation.setSubject(testGene2);
        editedDiseaseAnnotation.setEvidenceCodes(ecoTerms);
        editedDiseaseAnnotation.setModEntityId("TEST:Mod0001");
        editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
        editedDiseaseAnnotation.setGeneticSex(GeneticSex.hermaphrodite);
        editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
        editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(DiseaseGeneticModifierRelation.ameliorated_by);
        editedDiseaseAnnotation.setAnnotationType(AnnotationType.computational);
        editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
        editedDiseaseAnnotation.setWith(withGenes);
        editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
        
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
}
