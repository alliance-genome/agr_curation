package org.alliancegenome.curation_api.bulkupload;

import java.io.IOException;
import java.nio.file.*;

import org.alliancegenome.curation_api.resources.TestElasticSearchReourse;
import org.junit.jupiter.api.*;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.*;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchReourse.Initializer.class)
public class GeneBulkUploadITCase {

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
    }
    
    @Test
    public void geneBulkUpload() throws IOException {
        String content = Files.readString(Path.of("src/test/resources/gene/00_mod_examples.json"));

        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene/bulk/bgifile?async=false").
            then().
            statusCode(200);
        //body("$.size()", is(605),
        //   "[0].primaryId", is("MGI:1934609"));

    }
}
