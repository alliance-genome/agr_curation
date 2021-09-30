package org.alliancegenome.curation_api.bulkupload;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.nio.file.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@QuarkusIntegrationTest
public class GeneBulkUploadITCase {

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
