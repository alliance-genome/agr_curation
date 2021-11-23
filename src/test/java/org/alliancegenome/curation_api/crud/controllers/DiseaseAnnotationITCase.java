package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

import io.restassured.common.mapper.TypeRef;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

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

    private TypeRef<SearchResponse<DiseaseAnnotation>> getSearchResponseTypeRef() {
        return new TypeRef<SearchResponse <DiseaseAnnotation>>() {
        };
    }

    @Test
    @Order(1)
    public void createDiseaseAnnotation() throws Exception {

        crossReference = new CrossReference();
        crossReference.setDisplayName("AGRreference");
        crossReference.setCurie("DOID:0001");
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

        doTerm = new DOTerm();
        doTerm.setCurie("DOT:0001");
        doTerm.setCrossReferences(Arrays.asList(crossReference));

        RestAssured.given().
            contentType("application/json").
            body(doTerm).
            when().
            post("/api/doterm").
            then().
            statusCode(200);

        biologicalEntity = new BiologicalEntity();
        biologicalEntity.setCurie("BO:0001");
        biologicalEntity.setTaxon("taxon:0001");

        RestAssured.given().
            contentType("application/json").
            body(biologicalEntity).
            when().
            post("/api/biologicalentity").
            then().
            statusCode(200);

        diseaseAnnotation = new DiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.is_implicated_in);
        diseaseAnnotation.setCurie("Disease:0001");
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(doTerm);
        diseaseAnnotation.setSubject(biologicalEntity);
//        Not ready for references yet:
//        reference = new Reference();
//        reference.setCurie("REF:0001");
//        diseaseAnnotation.setReferenceList(Arrays.asList(reference));

        RestAssured.given().
            contentType("application/json").
            body(diseaseAnnotation).
            when().
            post("/api/disease-annotation").
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
            body("results", hasSize(1)).
            body("results[0].curie", is("Disease:0001")).
            body("results[0].subject.curie", is("BO:0001")).
            body("results[0].object.curie", is("DOT:0001"));
    }

    @Test
    @Order(2)
    public void editDiseaseAnnotation() throws Exception {
        SearchResponse<DiseaseAnnotation> res = RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/disease-annotation/find?limit=10&page=0").
            then().
            statusCode(200).
            extract().body().as(getSearchResponseTypeRef());

        DiseaseAnnotation editedDiseaseAnnotation = res.getResults().get(0);
        editedDiseaseAnnotation.setNegated(true);
//        editedDiseaseAnnotation.setReferenceList(Arrays.asList());
//        editedDiseaseAnnotation.setEvidenceCodes(Arrays.asList());
//
//        RestAssured.given().
//            contentType("application/json").
//            body(diseaseAnnotation).
//            when().
//            put("/api/disease-annotation").
//            then().
//            statusCode(200);
    }
}
