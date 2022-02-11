package org.alliancegenome.curation_api.crud.controllers.ontology;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(11)
public class DOTermITCase {
    private String DOTERMCURIE = "DOID:10001";

    private TypeRef<ObjectResponse<DOTerm>> getObjectResponseTypeRef() {
        return new TypeRef<ObjectResponse <DOTerm>>() { };
    }

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", 100000)
                        .setParam("http.connection.timeout", 100000));
    }

    @Test
    @Order(1)
    void testCreate() {
        DOTerm doTerm = new DOTerm();
        doTerm.setObsolete(false);
        doTerm.setCurie(DOTERMCURIE);
        doTerm.setDefinition("DO term definition");
        doTerm.setName("DO Term 1");
        doTerm.setNamespace("disease_ontology");
        doTerm.setDefinitionUrls(Arrays.asList("http://url1.com"));
        doTerm.setSecondaryIdentifiers(Arrays.asList("secondary ID 1"));

        RestAssured.given().
            contentType("application/json").
            body(doTerm).
            when().
            post("/api/doterm").
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            get("/api/doterm/" + DOTERMCURIE).
            then().
            statusCode(200).
            body("entity.definition",is("DO term definition")).
            body("entity.name", is("DO Term 1")).
            body("entity.namespace", is("disease_ontology")).
            body("entity.obsolete", is(false));
    }

    @Test
    @Order(2)
    void testEdit() {
        ObjectResponse<DOTerm> response = RestAssured.given().
            when().
            get("/api/doterm/" + DOTERMCURIE).
            then().
            statusCode(200).
            extract().body().as(getObjectResponseTypeRef());

        DOTerm editedDOTerm = response.getEntity();
        editedDOTerm.setDefinition("changed definition");
        editedDOTerm.setNamespace("changed_namespace");
        editedDOTerm.setName("changed name");
        editedDOTerm.setObsolete(true);

        RestAssured.given().
            contentType("application/json").
            body(editedDOTerm).
            when().
            put("/api/doterm").
            then().
            statusCode(200);

        RestAssured.given().
            when().
            get("/api/doterm/" + DOTERMCURIE).
            then().
            statusCode(200).
            body("entity.definition",is("changed definition")).
            body("entity.name", is("changed name")).
            body("entity.namespace", is("changed_namespace")).
            body("entity.obsolete", is(true));
    }

    @Test
    @Order(3)
    void testDelete() {
        RestAssured.given().
            when().
            delete("/api/doterm/" + DOTERMCURIE).
            then().
            statusCode(200);
        
        RestAssured.given().
            when().
            get("/api/doterm/" + DOTERMCURIE).
            then().
            statusCode(200).
            body("isEmpty()", Matchers.is(true));
    }
}
