package org.alliancegenome.curation_api.bulkupload;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
public class GeneBulkUploadITCase {

    @Test
    public void geneBulkUpload() throws IOException {
        String content = Files.readString(Path.of("src/test/resources/gene/00_mod_examples.json"));

        given().
            contentType("application/json").
            body(content).
            when().
            post("/api/gene/bulk/bgifile").
            then().
            statusCode(200).
            body("$.size()", is(605),
                 "[0].primaryId", is("MGI:1934609"));
    }
}
