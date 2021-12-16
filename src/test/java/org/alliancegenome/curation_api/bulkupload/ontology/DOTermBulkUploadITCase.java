package org.alliancegenome.curation_api.bulkupload.ontology;

import java.nio.file.*;
import java.util.concurrent.TimeUnit;

import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.junit.jupiter.api.*;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("06 - DO term bulk upload")
public class DOTermBulkUploadITCase {

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.socket.timeout", 100000)
                .setParam("http.connection.timeout", 100000));
    }

    @Test
    @Order(1)
    public void doTermBulkUpload() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/06_do_term/00_do_agr_slim.owl"));
        // upload file
        RestAssured.given().
            contentType("application/xml").
            accept("application/json").
            body(content).
            when().
            post("/api/doterm/bulk/owl?async=false").
            then().
            statusCode(200);
        
        // check entity count
        RestAssured.given().
            when().
            contentType("application/json").
            body("{\"namespace\":\"disease_ontology\"}").
            post("/api/doterm/find?limit=10&page=0").
            then().
            statusCode(200).
            body("totalResults", is(28));
    }
    
    @Test
    @Order(2) 
    public void doTermRetrievable() throws Exception {
        RestAssured.given().
            when().
            get("/api/doterm/DOID:162").
            then().
            statusCode(200).
            body("entity.curie", is("DOID:162")).
            body("entity.name", is("cancer")).
            body("entity.obsolete", is(false)).
            body("entity.namespace", is("disease_ontology")).
            body("entity.definition", is("A disease of cellular proliferation that is malignant and primary, characterized by uncontrolled cellular proliferation, local cell invasion and metastasis."));
    }
        
}
