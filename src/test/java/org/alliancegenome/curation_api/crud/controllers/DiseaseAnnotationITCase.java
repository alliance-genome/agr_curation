package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.alliancegenome.curation_api.interfaces.rest.DiseaseAnnotationRESTInterfaceRescu;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.junit.jupiter.api.*;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DiseaseAnnotationITCase {

    @Test
    @Order(1)
    public void createDiseaseAnnotation() {

        CrossReference crossReference = new CrossReference();
        crossReference.setDisplayName("AGRreference");
        crossReference.setCurie("CROSSREF:0001");
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

        DOTerm doTerm = new DOTerm();
        doTerm.setCurie("DOID:0001");
        doTerm.setCrossReferences(Collections.singletonList(crossReference));

        RestAssured.given().
                contentType("application/json").
                body(doTerm).
                when().
                post("/api/doterm").
                then().
                statusCode(200);

        BiologicalEntity biologicalEntity = new BiologicalEntity();
        biologicalEntity.setCurie("BO:0001");
        biologicalEntity.setTaxon("taxon:0001");

        RestAssured.given().
                contentType("application/json").
                body(biologicalEntity).
                when().
                post("/api/biologicalentity").
                then().
                statusCode(200);

        Reference reference = new Reference();
        reference.setCurie("REF:0001");

        DiseaseAnnotation diseaseAnnotation = new DiseaseAnnotation();
        diseaseAnnotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.is_implicated_in);
        diseaseAnnotation.setCurie("Disease0001");
        diseaseAnnotation.setNegated(false);
        diseaseAnnotation.setObject(doTerm);
        diseaseAnnotation.setSubject(biologicalEntity);
//        Not ready for references yet:
//        diseaseAnnotation.setReferenceList(Arrays.asList(reference));

        RestAssured.given().
                contentType("application/json").
                body(diseaseAnnotation).
                when().
                post("/api/disease-annotation").
                then().
                statusCode(200);

        final ValidatableResponse validatableResponse = RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/disease-annotation/find?limit=10&page=0").
                then().
                statusCode(200);
        validatableResponse.
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("Disease0001")).
                body("results[0].subject.curie", is("BO:0001")).
                body("results[0].object.curie", is("DOID:0001"));
    }

    @Test
    @Order(2)
    public void updateDiseaseAnnotation() {
        ClientConfig config = new ClientConfig();
        config.setJacksonObjectMapperFactory(new JacksonObjectMapperFactory());

        String path = "http://0.0.0.0:8081/api";
        DiseaseAnnotationRESTInterfaceRescu api = RestProxyFactory.createProxy(DiseaseAnnotationRESTInterfaceRescu.class, path, config);
        //ObjectResponse<DiseaseAnnotation> annotation = api.get("Disease0001");
        HashMap<String,Object> map = new HashMap<>();
        SearchResponse<DiseaseAnnotation> searchAnnotation = api.find(0,10,map);

        DiseaseAnnotation annotation = searchAnnotation.getResults().get(0);

        CrossReference crossReference = new CrossReference();
        crossReference.setDisplayName("AGRreference");
        crossReference.setCurie("CROSSREF:0002");
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

        DOTerm doTerm = new DOTerm();
        doTerm.setCurie("DOID:0002");
        doTerm.setCrossReferences(List.of(crossReference));
        annotation.setObject(doTerm);

        RestAssured.given().
                contentType("application/json").
                body(doTerm).
                when().
                post("/api/doterm").
                then().
                statusCode(200);

        BiologicalEntity biologicalEntity = new BiologicalEntity();
        biologicalEntity.setCurie("BO:0002");
        biologicalEntity.setTaxon("taxon:0001");
        annotation.setSubject(biologicalEntity);

        RestAssured.given().
                contentType("application/json").
                body(biologicalEntity).
                when().
                post("/api/biologicalentity").
                then().
                statusCode(200);

        RestAssured.given().
                contentType("application/json").
                body(annotation).
                when().
                put("/api/disease-annotation").
                then().
                statusCode(200);

        final ValidatableResponse validatableResponse = RestAssured.given().
                when().
                header("Content-Type", "application/json").
                body("{}").
                post("/api/disease-annotation/find?limit=10&page=0").
                then().
                statusCode(200);
        validatableResponse.
                body("totalResults", is(1)).
                body("results", hasSize(1)).
                body("results[0].curie", is("Disease0001")).
                body("results[0].subject.curie", is("BO:0002")).
                body("results[0].object.curie", is("DOID:0002"));

    }
}
