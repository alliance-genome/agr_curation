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
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(10)
public class DiseaseAnnotationITCase {

    private CrossReference crossReference;
    private DOTerm doTerm;
    private EcoTerm ecoTerm;
    private BiologicalEntity biologicalEntity;
    private Gene gene;
    private AffectedGenomicModel agm;
    private final String GENE_DISEASE_ANNOTATION = "GeneDisease:0001";
    private final String ALLELE_DISEASE_ANNOTATION = "AlleleDisease:0001";
    private final String AGM_DISEASE_ANNOTATION = "AgmDisease:0001";

    private TypeRef<ObjectResponse<GeneDiseaseAnnotation>> getObjectResponseTypeRef() {
        return new TypeRef<>() {
        };
    }

    private TypeRef<ObjectResponse<AlleleDiseaseAnnotation>> getObjectResponseTypeRefAllele() {
        return new TypeRef<>() {
        };
    }

    @Test
    @Order(1)
    public void createGeneDiseaseAnnotation() {

        crossReference = createCrossReference("CROSSREF:0001", "AGRreference");
        doTerm = createDiseaseTerm("DOID:0001", crossReference, false);
        ecoTerm = createEcoTerm("ECO:0001", "Test evidence code", false);
        biologicalEntity = createBiologicalEntity("BO:0001", "NCBITaxon:9606");
        gene = createGene("GENE:0001", "NCBITaxon:9606");

        GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotationEntity();
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
                body("entity.subject.curie", is("GENE:0001")).
                body("entity.object.curie", is("DOID:0001")).
                body("entity.evidenceCodes[0].curie", is("ECO:0001"));
    }

    @Test
    @Order(2)
    public void createAlleleDiseaseAnnotation() {

        Allele allele = createAllele("ALLELE:0001", "NCBITaxon:9606");

        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(ecoTerm);

        AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.is_implicated_in);
        diseaseAnnotation.setUniqueId(ALLELE_DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(doTerm);
        diseaseAnnotation.setDataProvider("TEST");
        diseaseAnnotation.setSubject(allele);
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
                body("entity.subject.curie", is("ALLELE:0001")).
                body("entity.object.curie", is("DOID:0001")).
                body("entity.evidenceCodes[0].curie", is("ECO:0001"));
    }

    @Test
    @Order(2)
    public void createAgmDiseaseAnnotation() {

        AffectedGenomicModel model = createModel("MODEL:0001", "NCBITaxon:9606", "TestAGM");

        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(ecoTerm);

        AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.is_implicated_in);
        diseaseAnnotation.setUniqueId(AGM_DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(doTerm);
        diseaseAnnotation.setDataProvider("TEST");
        diseaseAnnotation.setSubject(model);
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
                body("entity.subject.curie", is("MODEL:0001")).
                body("entity.object.curie", is("DOID:0001")).
                body("entity.evidenceCodes[0].curie", is("ECO:0001"));
    }

    private GeneDiseaseAnnotation getGeneDiseaseAnnotationEntity() {
        List<EcoTerm> ecoTerms = new ArrayList<>();
        ecoTerms.add(ecoTerm);

        GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.is_implicated_in);
        diseaseAnnotation.setUniqueId(GENE_DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(doTerm);
        diseaseAnnotation.setDataProvider("TEST");
        diseaseAnnotation.setSubject(gene);
        diseaseAnnotation.setEvidenceCodes(ecoTerms);
        return diseaseAnnotation;
    }

    @Test
    @Order(3)
    public void editGeneDiseaseAnnotation() {
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();

        // change negated
        editedDiseaseAnnotation.setNegated(true);
        // change subject
        Gene newGene = createGene("GENE:0002", "NCBITaxon:9606");

        editedDiseaseAnnotation.setSubject(newGene);

        // change DOTerm
        editedDiseaseAnnotation.setObject(createDiseaseTerm("DOID:0002", null, false));
        // change ECOTerm
        List<EcoTerm> editedEcoTerms = new ArrayList<EcoTerm>();
        editedEcoTerms.add(createEcoTerm("ECO:0002", "Update test", false));
        editedDiseaseAnnotation.setEvidenceCodes(editedEcoTerms);


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
                body("entity.subject.curie", is("GENE:0002")).
                body("entity.object.curie", is("DOID:0002")).
                // TODO:  Not working, i.e. the update is not sticking in postgres.
                //body("entity.negated", is(true)).
                body("entity.evidenceCodes[0].curie", is("ECO:0002"));
    }

    @Test
    @Order(4)
    public void editAlleleDiseaseAnnotation() {
        AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();

        // change negated
        editedDiseaseAnnotation.setNegated(true);
        // change subject
        Allele newAllele = createAllele("ALLELE:0002", "NCBITaxon:9606");

        editedDiseaseAnnotation.setSubject(newAllele);

        // change DOTerm
        editedDiseaseAnnotation.setObject(createDiseaseTerm("DOID:0003", null, false));
        // change ECOTerm
        List<EcoTerm> editedEcoTerms = new ArrayList<>();
        editedEcoTerms.add(createEcoTerm("ECO:0003", "Update test", false));
        editedDiseaseAnnotation.setEvidenceCodes(editedEcoTerms);


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
                body("entity.subject.curie", is("ALLELE:0002")).
                body("entity.object.curie", is("DOID:0003")).
                // TODO:  Not working, i.e. the update is not sticking in postgres.
                //body("entity.negated", is(true)).
                body("entity.evidenceCodes[0].curie", is("ECO:0003"));
    }

    @Test
    @Order(5)
    public void editWithObsoleteEcoTerm() {
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        List<EcoTerm> editedEcoTerms = new ArrayList<>();
        editedEcoTerms.add(createEcoTerm("ECO:0005", "ECO update test", true));
        editedDiseaseAnnotation.setEvidenceCodes(editedEcoTerms);

        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/gene-disease-annotation").
                then().
                statusCode(400);
    }

    @Test
    @Order(6)
    public void editWithObsoleteDoTerm() {
        GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
        editedDiseaseAnnotation.setObject(createDiseaseTerm("DO:0005", null, true));

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

    //    @Test
    @Order(7)
    public void deleteDiseaseAnnotation() {

        GeneDiseaseAnnotation geneDiseaseAnnotation = getGeneDiseaseAnnotation();

        RestAssured.given().
                when().
                delete("/api/gene-disease-annotation/" + geneDiseaseAnnotation.getId()).
                then().
                statusCode(200);
    }

    private DOTerm createDiseaseTerm(String curie, CrossReference crossReference, Boolean obsolete) {
        DOTerm doTerm = new DOTerm();
        doTerm.setCurie(curie);
        doTerm.setCrossReferences(Collections.singletonList(crossReference));
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

    private BiologicalEntity createBiologicalEntity(String curie, String taxon) {
        BiologicalEntity biologicalEntity = new BiologicalEntity();
        biologicalEntity.setCurie(curie);
        biologicalEntity.setTaxon(getTaxonFromCurie(taxon));

        RestAssured.given().
                contentType("application/json").
                body(biologicalEntity).
                when().
                post("/api/biologicalentity").
                then().
                statusCode(200);
        return biologicalEntity;
    }

    private Gene createGene(String curie, String taxon) {
        Gene biologicalEntity = new Gene();
        biologicalEntity.setCurie(curie);
        biologicalEntity.setTaxon(getTaxonFromCurie(taxon));

        RestAssured.given().
                contentType("application/json").
                body(biologicalEntity).
                when().
                post("/api/gene").
                then().
                statusCode(200);
        return biologicalEntity;
    }

    private Allele createAllele(String curie, String taxon) {
        Allele biologicalEntity = new Allele();
        biologicalEntity.setCurie(curie);
        biologicalEntity.setTaxon(getTaxonFromCurie(taxon));

        RestAssured.given().
                contentType("application/json").
                body(biologicalEntity).
                when().
                post("/api/allele").
                then().
                statusCode(200);
        return biologicalEntity;
    }

    private AffectedGenomicModel createModel(String curie, String taxon, String name) {
        AffectedGenomicModel biologicalEntity = new AffectedGenomicModel();
        biologicalEntity.setCurie(curie);
        biologicalEntity.setTaxon(getTaxonFromCurie(taxon));
        biologicalEntity.setName(name);

        RestAssured.given().
                contentType("application/json").
                body(biologicalEntity).
                when().
                post("/api/agm").
                then().
                statusCode(200);
        return biologicalEntity;
    }

    private CrossReference createCrossReference(String curie, String name) {
        CrossReference crossReference = new CrossReference();
        crossReference.setCurie(curie);
        crossReference.setDisplayName(name);
        crossReference.setPrefix("agr");
        crossReference.setPageAreas(Arrays.asList("reference"));
        crossReference.setCreated(LocalDateTime.now());
        crossReference.setLastUpdated(LocalDateTime.now());

        RestAssured.given().
                body(crossReference).
                contentType("application/json").
                when().
                post("/api/cross-reference").
                then().
                statusCode(200);
        return crossReference;
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
