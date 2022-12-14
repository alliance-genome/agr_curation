package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZFATerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.ExperimentalConditionSummary;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(11)
public class ExperimentalConditionITCase extends BaseITCase {
	
	private ZECOTerm testZecoTerm;
	private ZECOTerm testZecoTerm2;
	private ZECOTerm testZecoTerm3;
	private GOTerm testGoTerm;
	private CHEBITerm testChebiTerm;
	private ZFATerm testZfaTerm;
	private NCBITaxonTerm testNcbiTaxonTerm;
	private ZECOTerm testObsoleteZecoTerm;
	private GOTerm testObsoleteGoTerm;
	private CHEBITerm testObsoleteChebiTerm;
	private ZFATerm testObsoleteZfaTerm;
	private NCBITaxonTerm testObsoleteNcbiTaxonTerm;
	private ZECOTerm testNonSlimZecoTerm;
	private String testConditionSummary;
	
	private void createRequiredObjects() {
		testZecoTerm = createZecoTerm("ZECO:ec0001", "Test ZecoTerm", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		testZecoTerm2 = createZecoTerm("ZECO:ec0002", "Test ZecoTerm", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		testZecoTerm3 = createZecoTerm("ZECO:ec0003", "Test ZecoTerm", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		testObsoleteZecoTerm = createZecoTerm("ZECO:ec0005", "Test ZecoTerm", true, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		testNonSlimZecoTerm = createZecoTerm("ZECO:ec0006", "Test ZecoTerm", false, null);
		testGoTerm = createGoTerm("GO:ec0001", "Test GOTerm", false);
		testObsoleteGoTerm = createGoTerm("GO:ec0002", "Test GOTerm", true);
		testChebiTerm = createChebiTerm("CHEBI:ec0001", "Test CHEBITerm", false);
		testObsoleteChebiTerm = createChebiTerm("CHEBI:ec0002", "Test CHEBITerm", true);
		testZfaTerm = createZfaTerm("ZFA:ec0001", false);
		testObsoleteZfaTerm = createZfaTerm("ZFA:ec0002", true);
		testNcbiTaxonTerm = getNCBITaxonTerm("NCBITaxon:9606");
		testObsoleteNcbiTaxonTerm = getNCBITaxonTerm("NCBITaxon:1000");
	}

	@Test
	@Order(1)
	public void createExperimentalCondition() {
		createRequiredObjects();
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		experimentalCondition.setConditionClass(testZecoTerm);
		

		testConditionSummary = ExperimentalConditionSummary.getConditionSummary(experimentalCondition);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(200);
		RestAssured.given().
				when().
				get("/api/experimental-condition/findBy/" + testConditionSummary).
				then().
				statusCode(200).
				body("entity.conditionClass.curie", is("ZECO:ec0001")).
				body("entity.internal", is(false)).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(2)
	public void editExperimentalcondition() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm2);
		editedExperimentalCondition.setConditionId(testZecoTerm3);
		editedExperimentalCondition.setConditionQuantity("Amount");
		editedExperimentalCondition.setConditionAnatomy(testZfaTerm);
		editedExperimentalCondition.setConditionGeneOntology(testGoTerm);
		editedExperimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		editedExperimentalCondition.setConditionChemical(testChebiTerm);
		editedExperimentalCondition.setConditionFreeText("Free text");
		editedExperimentalCondition.setInternal(true);
		
		testConditionSummary = ExperimentalConditionSummary.getConditionSummary(editedExperimentalCondition);
		
		RestAssured.given().
				contentType("application/json").
				body(editedExperimentalCondition).
				when().
				put("/api/experimental-condition").
				then().
				statusCode(200);
		RestAssured.given().
				when().
				get("/api/experimental-condition/findBy/" + testConditionSummary).
				then().
				statusCode(200).
				body("entity.conditionClass.curie", is("ZECO:ec0002")).
				body("entity.conditionId.curie", is("ZECO:ec0003")).
				body("entity.conditionQuantity", is("Amount")).
				body("entity.conditionGeneOntology.curie", is("GO:ec0001")).
				body("entity.conditionTaxon.curie", is("NCBITaxon:9606")).
				body("entity.conditionChemical.curie", is("CHEBI:ec0001")).
				body("entity.conditionFreeText", is("Free text")).
				body("entity.conditionSummary", is("Test ZecoTerm:Test ZecoTerm:Test ZFATerm:Test GOTerm:Test CHEBITerm:Homo sapiens:Amount:Free text")).
				body("entity.internal", is(true));
		
	}

	@Test
	@Order(3)
	public void editWithObsoleteConditionClass() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testObsoleteZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editWithObsoleteConditionId() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionId", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(5)
	public void editWithObsoleteConditionAnatomy() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionAnatomy", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void editWithObsoleteConditionGeneOntology() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionGeneOntology", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(7)
	public void editWithObsoleteConditionTaxon() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionTaxon", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(8)
	public void editWithObsoleteConditionChemical() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionChemical", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void editWithInvalidConditionClass() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		ZECOTerm nonPersistedZecoTerm = new ZECOTerm();
		nonPersistedZecoTerm.setCurie("NPZECO:0001");
		nonPersistedZecoTerm.setObsolete(false);
		
		editedExperimentalCondition.setConditionClass(nonPersistedZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(10)
	public void editWithInvalidConditionId() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		ZECOTerm nonPersistedZecoTerm = new ZECOTerm();
		nonPersistedZecoTerm.setCurie("NPZECO:0001");
		nonPersistedZecoTerm.setObsolete(false);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionId", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void editWithInvalidConditionAnatomy() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		ZFATerm nonPersistedZfaTerm = new ZFATerm();
		nonPersistedZfaTerm.setCurie("NPZFA:0001");
		nonPersistedZfaTerm.setObsolete(false);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionAnatomy", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(12)
	public void editWithInvalidConditionGeneOntology() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		GOTerm nonPersistedGoTerm = new GOTerm();
		nonPersistedGoTerm.setCurie("NPGO:0001");
		nonPersistedGoTerm.setObsolete(false);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionGeneOntology", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(13)
	public void editWithInvalidConditionChemical() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		CHEBITerm nonPersistedChebiTerm = new CHEBITerm();
		nonPersistedChebiTerm.setCurie("NPCHEBI:0001");
		nonPersistedChebiTerm.setObsolete(false);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm2);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionChemical", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(14)
	public void editWithMissingConditionClass() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(null);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(15)
	public void editWithNonSlimConditionClass() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testNonSlimZecoTerm);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(16)
	public void createWithObsoleteConditionClass() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testObsoleteZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount3");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(17)
	public void createWithObsoleteConditionId() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionId(testObsoleteZecoTerm);
		experimentalCondition.setConditionQuantity("Amount4");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionId", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(18)
	public void createWithObsoleteConditionAnatomy() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount5");
		experimentalCondition.setConditionAnatomy(testObsoleteZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionAnatomy", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(19)
	public void createWithObsoleteConditionGeneOntology() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount6");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testObsoleteGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionGeneOntology", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(20)
	public void createWithObsoleteConditionTaxon() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount7");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testObsoleteNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionTaxon", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(21)
	public void createWithObsoleteConditionChemical() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount8");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testObsoleteChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionChemical", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(22)
	public void createWithInvalidConditionClass() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		ZECOTerm nonPersistedZecoTerm = new ZECOTerm();
		nonPersistedZecoTerm.setCurie("NPZECO:0001");
		nonPersistedZecoTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(nonPersistedZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount9");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(23)
	public void createWithInvalidConditionId() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		ZECOTerm nonPersistedZecoTerm = new ZECOTerm();
		nonPersistedZecoTerm.setCurie("NPZECO:0001");
		nonPersistedZecoTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionId(nonPersistedZecoTerm);
		experimentalCondition.setConditionQuantity("Amount10");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionId", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(24)
	public void createWithInvalidConditionAnatomy() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		ZFATerm nonPersistedZfaTerm = new ZFATerm();
		nonPersistedZfaTerm.setCurie("NPZFA:0001");
		nonPersistedZfaTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount11");
		experimentalCondition.setConditionAnatomy(nonPersistedZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionAnatomy", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(25)
	public void createWithInvalidConditionGeneOntology() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		GOTerm nonPersistedGoTerm = new GOTerm();
		nonPersistedGoTerm.setCurie("NPGO:0001");
		nonPersistedGoTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount12");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(nonPersistedGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionGeneOntology", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(26)
	public void createWithInvalidConditionChemical() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		CHEBITerm nonPersistedChebiTerm = new CHEBITerm();
		nonPersistedChebiTerm.setCurie("NPCHEBI:0001");
		nonPersistedChebiTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(testZecoTerm2);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount13");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(nonPersistedChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionChemical", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(27)
	public void createWithMissingConditionClass() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount14");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(28)
	public void createWithNonSlimConditionClass() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testNonSlimZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount16");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(29)
	public void createDuplicateExperimentalCondition() {
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount17");
		experimentalCondition.setConditionAnatomy(testZfaTerm);
		experimentalCondition.setConditionGeneOntology(testGoTerm);
		experimentalCondition.setConditionTaxon(testNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalCondition).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(200);
		
		ExperimentalCondition experimentalConditionDuplicate = new ExperimentalCondition();
		
		experimentalConditionDuplicate.setConditionClass(testZecoTerm);
		experimentalConditionDuplicate.setConditionId(testZecoTerm3);
		experimentalConditionDuplicate.setConditionQuantity("Amount17");
		experimentalConditionDuplicate.setConditionAnatomy(testZfaTerm);
		experimentalConditionDuplicate.setConditionGeneOntology(testGoTerm);
		experimentalConditionDuplicate.setConditionTaxon(testNcbiTaxonTerm);
		experimentalConditionDuplicate.setConditionChemical(testChebiTerm);
		
		RestAssured.given().
				contentType("application/json").
				body(experimentalConditionDuplicate).
				when().
				post("/api/experimental-condition").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.uniqueId", is(ValidationConstants.NON_UNIQUE_MESSAGE));
	}
	
	@Test
	@Order(30)
	public void editWithNullConditionId() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(200).
				body("entity", hasKey("conditionId"));
				
		editedExperimentalCondition.setConditionId(null);
				
		RestAssured.given().
				contentType("application/json").
				body(editedExperimentalCondition).
				when().
				put("/api/experimental-condition").
				then().
				statusCode(200).
				body("entity", not(hasKey("conditionId")));
		
		testConditionSummary = ExperimentalConditionSummary.getConditionSummary(editedExperimentalCondition);
	}
	
	@Test
	@Order(31)
	public void editWithNullConditionQuantity() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(200).
				body("entity", hasKey("conditionQuantity"));
				
		editedExperimentalCondition.setConditionQuantity(null);
				
		RestAssured.given().
				contentType("application/json").
				body(editedExperimentalCondition).
				when().
				put("/api/experimental-condition").
				then().
				statusCode(200).
				body("entity", not(hasKey("conditionQuantity")));

		testConditionSummary = ExperimentalConditionSummary.getConditionSummary(editedExperimentalCondition);
	}
	
	@Test
	@Order(32)
	public void editWithNullConditionGeneOntology() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(200).
				body("entity", hasKey("conditionGeneOntology"));
				
		editedExperimentalCondition.setConditionGeneOntology(null);
				
		RestAssured.given().
				contentType("application/json").
				body(editedExperimentalCondition).
				when().
				put("/api/experimental-condition").
				then().
				statusCode(200).
				body("entity", not(hasKey("conditionGeneOntology")));

		testConditionSummary = ExperimentalConditionSummary.getConditionSummary(editedExperimentalCondition);
	}
	
	@Test
	@Order(33)
	public void editWithNullConditionAnatomy() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(200).
				body("entity", hasKey("conditionAnatomy"));
				
		editedExperimentalCondition.setConditionAnatomy(null);
				
		RestAssured.given().
				contentType("application/json").
				body(editedExperimentalCondition).
				when().
				put("/api/experimental-condition").
				then().
				statusCode(200).
				body("entity", not(hasKey("conditionAnatomy")));

		testConditionSummary = ExperimentalConditionSummary.getConditionSummary(editedExperimentalCondition);
	}
	
	@Test
	@Order(34)
	public void editWithNullConditionTaxon() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(200).
				body("entity", hasKey("conditionTaxon"));
				
		editedExperimentalCondition.setConditionTaxon(null);
				
		RestAssured.given().
				contentType("application/json").
				body(editedExperimentalCondition).
				when().
				put("/api/experimental-condition").
				then().
				statusCode(200).
				body("entity", not(hasKey("conditionTaxon")));

		testConditionSummary = ExperimentalConditionSummary.getConditionSummary(editedExperimentalCondition);
	}
	
	@Test
	@Order(35)
	public void editWithNullConditionChemical() {
		
		ExperimentalCondition editedExperimentalCondition = getExperimentalCondition(testConditionSummary);
		
		editedExperimentalCondition.setConditionClass(testZecoTerm);
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
				statusCode(200).
				body("entity", hasKey("conditionChemical"));
				
		editedExperimentalCondition.setConditionChemical(null);
				
		RestAssured.given().
				contentType("application/json").
				body(editedExperimentalCondition).
				when().
				put("/api/experimental-condition").
				then().
				statusCode(200).
				body("entity", not(hasKey("conditionChemical")));
		
		testConditionSummary = ExperimentalConditionSummary.getConditionSummary(editedExperimentalCondition);
	}

	private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRefTaxon() {
		return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
	}
}
