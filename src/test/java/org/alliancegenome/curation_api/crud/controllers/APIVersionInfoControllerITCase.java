package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
class APIVersionInfoControllerITCase {

    @Test
    void testGet() {
        given()
            .when().get("/api/version")
            .then()
            .statusCode(200)
            .body("name", is("agr_curation_api"));
    }
}
