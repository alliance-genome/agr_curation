package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DiseaseAnnotationITCase {

    private CrossReference crossReference;
    private DOTerm doTerm;
    private BiologicalEntity biologicalEntity;
    private Reference reference;
    private DiseaseAnnotation diseaseAnnotation;
    private final String DISEASE_ANNOTATION = "Disease:0001";

    private TypeRef<ObjectResponse<DiseaseAnnotation>> getObjectResponseTypeRef() {
        return new TypeRef<>() {
        };
    }

    @Test
    @Order(1)
    public void createDiseaseAnnotation() {

        crossReference = createCrossReference("CROSSREF:0001", "AGRreference");
        doTerm = createDiseaseTerm("DOID:0001", crossReference);
        biologicalEntity = createBiologicalEntity("BO:0001", "taxon:0001");
        diseaseAnnotation = new DiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.is_implicated_in);
        diseaseAnnotation.setCurie(DISEASE_ANNOTATION);
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(doTerm);
        diseaseAnnotation.setSubject(biologicalEntity);

        RestAssured.given().
                contentType("application/json").
                body(diseaseAnnotation).
                when().
                post("/api/disease-annotation").
                then().
                statusCode(200);

        RestAssured.given().
                when().
                get("/api/disease-annotation/" + DISEASE_ANNOTATION).
                then().
                statusCode(200).
                body("entity.curie", is(DISEASE_ANNOTATION)).
                body("entity.subject.curie", is("BO:0001")).
                body("entity.object.curie", is("DOID:0001"));
    }

    @Test
    @Order(2)
    public void editDiseaseAnnotation() {
        ObjectResponse<DiseaseAnnotation> res = RestAssured.given().
                when().
                get("/api/disease-annotation/" + DISEASE_ANNOTATION).
                then().
                statusCode(200).
                extract().body().as(getObjectResponseTypeRef());

        DiseaseAnnotation editedDiseaseAnnotation = res.getEntity();
        editedDiseaseAnnotation.setNegated(true);

        // change subject
        BiologicalEntity newSubject = createBiologicalEntity("BO:0002", "taxon:0001");
        editedDiseaseAnnotation.setSubject(newSubject);
        // change DOTerm
        editedDiseaseAnnotation.setObject(createDiseaseTerm("DOID:0002", null));

        RestAssured.given().
                contentType("application/json").
                body(editedDiseaseAnnotation).
                when().
                put("/api/disease-annotation").
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
                body("results[0].curie", is(DISEASE_ANNOTATION)).
                body("results[0].subject.curie", is("BO:0002")).
                body("results[0].object.curie", is("DOID:0002")).
                body("results[0].negated", is(true));
    }

    @Test
    @Order(3)
    public void deleteDiseaseAnnotation() {
        RestAssured.given().
                when().
                delete("/api/disease-annotation/" + DISEASE_ANNOTATION).
                then().
                statusCode(200);
    }


    private DOTerm createDiseaseTerm(String curie, CrossReference crossReference) {
        DOTerm doTerm = new DOTerm();
        doTerm.setCurie(curie);
        doTerm.setCrossReferences(Collections.singletonList(crossReference));

        RestAssured.given().
                contentType("application/json").
                body(doTerm).
                when().
                post("/api/doterm").
                then().
                statusCode(200);
        return doTerm;
    }

    private BiologicalEntity createBiologicalEntity(String curie, String taxon) {
        BiologicalEntity biologicalEntity = new BiologicalEntity();
        biologicalEntity.setCurie(curie);
        biologicalEntity.setTaxon(taxon);

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
        biologicalEntity.setTaxon(taxon);

        RestAssured.given().
                contentType("application/json").
                body(biologicalEntity).
                when().
                post("/api/gene").
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
}
