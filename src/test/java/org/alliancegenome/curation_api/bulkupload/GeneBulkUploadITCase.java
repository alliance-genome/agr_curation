package org.alliancegenome.curation_api.bulkupload;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.*;

import org.alliancegenome.curation_api.resources.TestElasticSearchReourse;
import org.junit.jupiter.api.*;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchReourse.Initializer.class)
public class GeneBulkUploadITCase {

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
        log.info("Setting Timeout");
    }
    
    @Test
    public void geneBulkUpload() throws IOException {
        String content = Files.readString(Path.of("src/test/resources/gene/00_mod_examples.json"));

        given().
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
