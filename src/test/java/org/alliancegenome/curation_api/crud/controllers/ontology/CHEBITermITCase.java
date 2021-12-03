package org.alliancegenome.curation_api.crud.controllers.ontology;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CHEBITermITCase {
    private String CHEBITERMCURIE = "CH:0001";

    private TypeRef<ObjectResponse<CHEBITerm>> getObjectResponseTypeRef() {
        return new TypeRef<ObjectResponse <CHEBITerm>>() { };
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
        CHEBITerm chebiTerm = new CHEBITerm();
        chebiTerm.setObsolete(false);
        chebiTerm.setCurie(CHEBITERMCURIE);
        chebiTerm.setDefinition("CH definition");
        chebiTerm.setName("CHEBI Term 1");
        chebiTerm.setNamespace("CHEBI NS");
        chebiTerm.setDefinitionUrls(Arrays.asList("http://url1.com"));
        chebiTerm.setSecondaryIdentifiers(Arrays.asList("secondary ID 1"));

        RestAssured.given().
            contentType("application/json").
            body(chebiTerm).
            when().
            post("/api/chebiterm").
            then().
            statusCode(200);
    }

    @Test
    @Order(2)
    void testEdit() {
        ObjectResponse<CHEBITerm> response = RestAssured.given().
            when().
            get("/api/chebiterm/" + CHEBITERMCURIE).
            then().
            statusCode(200).
            extract().body().as(getObjectResponseTypeRef());

        CHEBITerm editedCHEBITerm = response.getEntity();
        editedCHEBITerm.setDefinition("Changed definition");

        RestAssured.given().
            contentType("application/json").
            body(editedCHEBITerm).
            when().
            put("/api/chebiterm").
            then().
            statusCode(200);

        RestAssured.given().
            when().
            get("/api/chebiterm/" + CHEBITERMCURIE).
            then().
            statusCode(200).
            body("entity.definition",is("Changed definition"));
    }

    @Test
    @Order(3)
    void testDelete() {
        RestAssured.given().
            when().
            delete("/api/chebiterm/" + CHEBITERMCURIE).
            then().
            statusCode(200);
    }
}
