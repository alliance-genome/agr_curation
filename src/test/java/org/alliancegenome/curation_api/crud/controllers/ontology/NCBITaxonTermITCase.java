package org.alliancegenome.curation_api.crud.controllers.ontology;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(1)
public class NCBITaxonTermITCase {
    private String VALID_TAXON_CURIE = "NCBITaxon:9606";
    private String INVALID_TAXON_PREFIX = "NCBI:9606";
    private String INVALID_TAXON_SUFFIX = "NCBITaxon:0000";
    private NcbiTaxonTermService ncbiTaxonTermService;

    private TypeRef<ObjectResponse<DOTerm>> getObjectResponseTypeRef() {
        return new TypeRef<ObjectResponse <DOTerm>>() { };
    }

    @BeforeEach
    public void init() {
        ncbiTaxonTermService = new NcbiTaxonTermService();
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", 100000)
                        .setParam("http.connection.timeout", 100000));
    }

    // @Test
    @Order(1)
    void testValidTaxon() {
        NCBITaxonTerm taxon = ncbiTaxonTermService.getTaxonByCurie(VALID_TAXON_CURIE);
        
        RestAssured.given().
            when().
            get("/api/ncbitaxonterm/" + VALID_TAXON_CURIE).
            then().
            statusCode(200).
            body("entity.name", is("Homo sapiens")).
            body("entity.obsolete", is(false));
    }

    // @Test
    @Order(2)
    void testInvalidPrefix() {
        NCBITaxonTerm taxon = ncbiTaxonTermService.getTaxonByCurie(INVALID_TAXON_PREFIX);
        
        RestAssured.given().
            when().
            get("/api/ncbitaxonterm/" + INVALID_TAXON_PREFIX).
            then().
            statusCode(200).
            body("isEmpty()", Matchers.is(true));
    }

    // @Test
    @Order(3)
    void testInvalidSuffix() {
        NCBITaxonTerm taxon = ncbiTaxonTermService.getTaxonByCurie(INVALID_TAXON_SUFFIX);
        
        RestAssured.given().
            when().
            get("/api/ncbitaxonterm/" + INVALID_TAXON_SUFFIX).
            then().
            statusCode(200).
            body("isEmpty()", Matchers.is(true));
    }
}
