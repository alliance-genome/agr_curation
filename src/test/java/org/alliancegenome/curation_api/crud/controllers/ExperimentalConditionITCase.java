package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;
import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.ExperimentalConditionSummary;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(11)
public class ExperimentalConditionITCase {
    
    private ZecoTerm testZecoTerm;
    private ZecoTerm testZecoTerm2;
    private ZecoTerm testZecoTerm3;
    private GOTerm testGoTerm;
    private CHEBITerm testChebiTerm;
    private ZfaTerm testZfaTerm;
    private NCBITaxonTerm testNcbiTaxonTerm;
    private ZecoTerm testObsoleteZecoTerm;
    private GOTerm testObsoleteGoTerm;
    private CHEBITerm testObsoleteChebiTerm;
    private ZfaTerm testObsoleteZfaTerm;
    private NCBITaxonTerm testObsoleteNcbiTaxonTerm;
    private ZecoTerm testNonSlimZecoTerm;
    
    private TypeRef<ObjectResponse<ExperimentalCondition>> getObjectResponseTypeRef() {
        return new TypeRef<>() {
        };
    }
    
    private void createRequiredObjects() {
        testZecoTerm = createZecoTerm("ZECO:ec0001", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
        testZecoTerm2 = createZecoTerm("ZECO:ec0002", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
        testZecoTerm3 = createZecoTerm("ZECO:ec0003", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
        testObsoleteZecoTerm = createZecoTerm("ZECO:ec0005", true, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
        testNonSlimZecoTerm = createZecoTerm("ZECO:ec0006", false, null);
        testGoTerm = createGoTerm("GO:ec0001", false);
        testObsoleteGoTerm = createGoTerm("GO:ec0002", true);
        testChebiTerm = createChebiTerm("CHEBI:ec0001", false);
        testObsoleteChebiTerm = createChebiTerm("CHEBI:ec0002", true);
        testZfaTerm = createZfaTerm("ZFA:ec0001", false);
        testObsoleteZfaTerm = createZfaTerm("ZFA:ec0002", true);
        testNcbiTaxonTerm = getTaxonFromCurie("NCBITaxon:9606");
        testObsoleteNcbiTaxonTerm = getTaxonFromCurie("NCBITaxon:1000");
    }

    @Test
    @Order(1)
    public void createExperimentalCondition() {
        createRequiredObjects();
        
        ExperimentalCondition experimentalCondition = new ExperimentalCondition();
        experimentalCondition.setConditionStatement("CRUD:Statement1");
        experimentalCondition.setConditionClass(testZecoTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(experimentalCondition).
                when().
                post("/api/experimental-condition").
                then().
                statusCode(200);
        RestAssured.given().
                when().
                get("/api/experimental-condition/findBy/CRUD:Statement1").
                then().
                statusCode(200).
                body("entity.conditionClass.curie", is("ZECO:ec0001")).
                body("entity.conditionStatement", is("CRUD:Statement1"));
    }

    @Test
    @Order(2)
    public void editExperimentalcondition() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement1");
        
        editedExperimentalCondition.setConditionClass(testZecoTerm2);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        editedExperimentalCondition.setConditionFreeText("Free text");
        editedExperimentalCondition.setConditionSummary(ExperimentalConditionSummary.getConditionSummary(editedExperimentalCondition));
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(200);
        RestAssured.given().
                when().
                get("/api/experimental-condition/findBy/CRUD:Statement2").
                then().
                statusCode(200).
                body("entity.conditionClass.curie", is("ZECO:ec0002")).
                body("entity.conditionStatement", is("CRUD:Statement2")).
                body("entity.conditionId.curie", is("ZECO:ec0003")).
                body("entity.conditionQuantity", is("Amount")).
                body("entity.conditionGeneOntology.curie", is("GO:ec0001")).
                body("entity.conditionTaxon.curie", is("NCBITaxon:9606")).
                body("entity.conditionChemical.curie", is("CHEBI:ec0001")).
                body("entity.conditionFreeText", is("Free text")).
                body("entity.conditionSummary", is("Test ZecoTerm:Test ZecoTerm:Test ZFATerm:Test GOTerm:Test CHEBITerm:Test NCBITaxonTerm:Amount:Free text"));
        
    }

    @Test
    @Order(3)
    public void editWithObsoleteConditionClass() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        editedExperimentalCondition.setConditionClass(testObsoleteZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(4)
    public void editWithObsoleteConditionId() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        editedExperimentalCondition.setConditionClass(testZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testObsoleteZecoTerm);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(5)
    public void editWithObsoleteConditionAnatomy() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        editedExperimentalCondition.setConditionClass(testZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testObsoleteZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(6)
    public void editWithObsoleteConditionGeneOntology() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        editedExperimentalCondition.setConditionClass(testZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testObsoleteGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(7)
    public void editWithObsoleteConditionTaxon() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        editedExperimentalCondition.setConditionClass(testZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testObsoleteNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(8)
    public void editWithObsoleteConditionChemical() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        editedExperimentalCondition.setConditionClass(testZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testObsoleteChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(9)
    public void editWithInvalidConditionClass() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        ZecoTerm nonPersistedZecoTerm = new ZecoTerm();
        nonPersistedZecoTerm.setCurie("NPZECO:0001");
        nonPersistedZecoTerm.setObsolete(false);
        
        editedExperimentalCondition.setConditionClass(nonPersistedZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(10)
    public void editWithInvalidConditionId() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        ZecoTerm nonPersistedZecoTerm = new ZecoTerm();
        nonPersistedZecoTerm.setCurie("NPZECO:0001");
        nonPersistedZecoTerm.setObsolete(false);
        
        editedExperimentalCondition.setConditionClass(testZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(nonPersistedZecoTerm);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(11)
    public void editWithInvalidConditionAnatomy() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        ZfaTerm nonPersistedZfaTerm = new ZfaTerm();
        nonPersistedZfaTerm.setCurie("NPZFA:0001");
        nonPersistedZfaTerm.setObsolete(false);
        
        editedExperimentalCondition.setConditionClass(testZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(nonPersistedZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(12)
    public void editWithInvalidConditionGeneOntology() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        GOTerm nonPersistedGoTerm = new GOTerm();
        nonPersistedGoTerm.setCurie("NPGO:0001");
        nonPersistedGoTerm.setObsolete(false);
        
        editedExperimentalCondition.setConditionClass(testZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(nonPersistedGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(13)
    public void editWithInvalidConditionChemical() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        CHEBITerm nonPersistedChebiTerm = new CHEBITerm();
        nonPersistedChebiTerm.setCurie("NPCHEBI:0001");
        nonPersistedChebiTerm.setObsolete(false);
        
        editedExperimentalCondition.setConditionClass(testZecoTerm2);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(nonPersistedChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(14)
    public void editWithMissingConditionClass() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        editedExperimentalCondition.setConditionClass(null);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }

    @Test
    @Order(15)
    public void editWithMissingConditionStatement() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        editedExperimentalCondition.setConditionClass(testZecoTerm2);
        editedExperimentalCondition.setConditionStatement(null);
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }
    
    @Test
    @Order(16)
    public void editWithNonSlimConditionClass() {
        
        ExperimentalCondition editedExperimentalCondition = getExperimentalCondition("CRUD:Statement2");
        
        editedExperimentalCondition.setConditionClass(testNonSlimZecoTerm);
        editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
        editedExperimentalCondition.setConditionId(testZecoTerm3);
        editedExperimentalCondition.setConditionQuantity("Amount");
        editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
        editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
        editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
        editedExperimentalCondition.setConditionChemical(testChebiTerm);
        
        RestAssured.given().
                contentType("application/json").
                body(editedExperimentalCondition).
                when().
                put("/api/experimental-condition").
                then().
                statusCode(400);
    }

    private ExperimentalCondition getExperimentalCondition(String conditionStatement) {
        ObjectResponse<ExperimentalCondition> res = RestAssured.given().
                when().
                get("/api/experimental-condition/findBy/" + conditionStatement).
                then().
                statusCode(200).
                extract().body().as(getObjectResponseTypeRef());
        
        return res.getEntity();
    }

    private ZecoTerm createZecoTerm(String curie, Boolean obsolete, String subset) {
        ZecoTerm zecoTerm = new ZecoTerm();
        zecoTerm.setCurie(curie);
        zecoTerm.setName("Test ZecoTerm");
        List<String> subsets = new ArrayList<String>();
        if (subset != null) {
            subsets.add(subset);
            zecoTerm.setSubsets(subsets);
        }
        zecoTerm.setObsolete(obsolete);

        RestAssured.given().
                contentType("application/json").
                body(zecoTerm).
                when().
                post("/api/zecoterm").
                then().
                statusCode(200);
        
        return zecoTerm;
    }


    private GOTerm createGoTerm(String curie, Boolean obsolete) {
        GOTerm goTerm = new GOTerm();
        goTerm.setCurie(curie);
        goTerm.setObsolete(obsolete);
        goTerm.setName("Test GOTerm");

        RestAssured.given().
                contentType("application/json").
                body(goTerm).
                when().
                post("/api/goterm").
                then().
                statusCode(200);
        return goTerm;
    }

    private CHEBITerm createChebiTerm(String curie, Boolean obsolete) {
        CHEBITerm chebiTerm = new CHEBITerm();
        chebiTerm.setCurie(curie);
        chebiTerm.setObsolete(obsolete);
        chebiTerm.setName("Test CHEBITerm");

        RestAssured.given().
                contentType("application/json").
                body(chebiTerm).
                when().
                post("/api/chebiterm").
                then().
                statusCode(200);
        return chebiTerm;
    }
    
    private ZfaTerm createZfaTerm(String curie, Boolean obsolete) {
        ZfaTerm zfaTerm = new ZfaTerm();
        zfaTerm.setCurie(curie);
        zfaTerm.setObsolete(obsolete);
        zfaTerm.setName("Test ZFATerm");

        RestAssured.given().
                contentType("application/json").
                body(zfaTerm).
                when().
                post("/api/zfaterm").
                then().
                statusCode(200);
        return zfaTerm;
    }

    private NCBITaxonTerm getTaxonFromCurie(String taxonCurie) {
        ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
            when().
            get("/api/ncbitaxonterm/" + taxonCurie).
            then().
            statusCode(200).
            extract().body().as(getObjectResponseTypeRefTaxon());
        
        return response.getEntity();
    }

    private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRefTaxon() {
        return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
    }
}
