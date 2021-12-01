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

import java.util.Arrays;

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
        return new TypeRef<ObjectResponse <DiseaseAnnotation>>() { };
    }

    @Test
    @Order(1)
    public void createDiseaseAnnotation() throws Exception {

        crossReference = new CrossReference();
        crossReference.setDisplayName("AGRreference");
        crossReference.setCurie("DOID:0001");
        crossReference.setPrefix("agr");

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
            get("/api/disease-annotation/"+DISEASE_ANNOTATION).
            then().
            statusCode(200).
            body("entity.curie", is(DISEASE_ANNOTATION)).
            body("entity.subject.curie", is("BO:0001")).
            body("entity.object.curie", is("DOT:0001"));
    }

    @Test
    @Order(2)
    public void editDiseaseAnnotation() throws Exception {
        ObjectResponse<DiseaseAnnotation> res = RestAssured.given().
            when().
            get("/api/disease-annotation/"+DISEASE_ANNOTATION).
            then().
            statusCode(200).
            extract().body().as(getObjectResponseTypeRef());

        DiseaseAnnotation editedDiseaseAnnotation = res.getEntity();
        editedDiseaseAnnotation.setNegated(true);
        RestAssured.given().
            contentType("application/json").
            body(editedDiseaseAnnotation).
            when().
            put("/api/disease-annotation").
            then().
            statusCode(200);
    }
}
