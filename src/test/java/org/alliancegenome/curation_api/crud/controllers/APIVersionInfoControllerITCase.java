package org.alliancegenome.curation_api.crud.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.*;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@QuarkusIntegrationTest
class APIVersionInfoControllerITCase {

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
        log.info("Setting Timeout");
    }
    
    @Test
    void testGet() {

        given()
            .when().get("/api/version")
            .then()
            .statusCode(200)
            .body("name", is("agr_curation_api"));
    }
}
