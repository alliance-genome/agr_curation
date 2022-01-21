package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.junit.jupiter.api.*;


import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(7)
public class AlleleITCase {

    private final String ALLELE_CURIE = "Allele:0001";
    private final String ALLELE_TAXON = "NCBITaxon:10090";
    private final String ALLELE_SYMBOL = "Allele<sup>test</sup>";
    private final String ALLELE_DESCRIPTION = "Allele test description";
    private final String INVALID_TAXON = "NCBITaxon:0000";
    private Allele allele = createAllele(ALLELE_CURIE, ALLELE_TAXON, ALLELE_SYMBOL, ALLELE_DESCRIPTION);
    
    @Test
    @Order(1)
    public void createValidAllele() {

        RestAssured.given().
                contentType("application/json").
                body(allele).
                when().
                post("/api/allele").
                then().
                statusCode(200);
        RestAssured.given().
                when().
                get("/api/allele/" + ALLELE_CURIE).
                then().
                statusCode(200).
                body("entity.curie", is(ALLELE_CURIE)).
                body("entity.symbol", is(ALLELE_SYMBOL)).
                body("entity.taxon", is(ALLELE_TAXON)).
                body("entity.description", is(ALLELE_DESCRIPTION));
    }

    @Test
    @Order(2)
    public void editAllele() {
        allele.setSymbol("Allele<sup>edited</sup>");
        allele.setDescription("Edited allele description");
        allele.setTaxon("NCBITaxon:9606");

        RestAssured.given().
                contentType("application/json").
                body(allele).
                when().
                put("/api/allele").
                then().
                statusCode(200);

        RestAssured.given().
                when().
                get("/api/allele/" + ALLELE_CURIE).
                then().
                statusCode(200).
                body("entity.curie", is(ALLELE_CURIE)).
                body("entity.symbol", is("Allele<sup>edited</sup>")).
                body("entity.taxon", is("NCBITaxon:9606")).
                body("entity.description", is("Edited allele description"));
    }

    @Test
    @Order(3)
    public void deleteAGM() {

        RestAssured.given().
                when().
                delete("/api/allele/" + ALLELE_CURIE).
                then().
                statusCode(200);
    }
    
    @Test
    @Order(4)
    public void createMissingCurieAllele() {
        Allele noCurieAllele = createAllele(null, ALLELE_SYMBOL, ALLELE_TAXON, ALLELE_DESCRIPTION);
        
        RestAssured.given().
            contentType("application/json").
            body(noCurieAllele).
            when().
            put("/api/allele").
            then().
            statusCode(400);
    }
    
    @Test
    @Order(5)
    public void createMissingSymbolAGM() {
        Allele noSymbolAllele = createAllele(ALLELE_CURIE, null, ALLELE_TAXON, ALLELE_DESCRIPTION);
        
        RestAssured.given().
            contentType("application/json").
            body(noSymbolAllele).
            when().
            put("/api/allele").
            then().
            statusCode(400);
    }
    
    @Test
    @Order(6)
    public void createMissingTaxonAllele() {
        Allele noTaxonAllele = createAllele(ALLELE_CURIE, ALLELE_SYMBOL, null, ALLELE_DESCRIPTION);
        
        RestAssured.given().
            contentType("application/json").
            body(noTaxonAllele).
            when().
            put("/api/allele").
            then().
            statusCode(400);
    }

    @Test
    @Order(7)
    public void createInvalidTaxonAGM() {
        Allele invalidTaxonAllele = createAllele(ALLELE_CURIE, ALLELE_SYMBOL, INVALID_TAXON, ALLELE_DESCRIPTION);
    

        RestAssured.given().
            contentType("application/json").
            body(invalidTaxonAllele).
            when().
            put("/api/allele").
            then().
            statusCode(400);
    }

    private Allele createAllele(String curie, String taxon, String symbol, String description) {
        Allele allele = new Allele();
        allele.setCurie(curie);
        allele.setTaxon(taxon);
        allele.setSymbol(symbol);
        allele.setDescription(description);
                
        return allele;
    }

}
