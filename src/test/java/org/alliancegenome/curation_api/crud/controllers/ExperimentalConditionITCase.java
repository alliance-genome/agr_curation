package org.alliancegenome.curation_api.crud.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZecoTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZfaTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.ExperimentalConditionSummary;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
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
				body("entity.conditionStatement", is("CRUD:Statement1")).
				body("entity.internal", is(false)).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
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
		editedExperimentalCondition.setInternal(true);
		
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
				body("entity.conditionSummary", is("Test ZecoTerm:Test ZecoTerm:Test ZFATerm:Test GOTerm:Test CHEBITerm:Homo sapiens:Amount:Free text")).
				body("entity.internal", is(true));
		
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.OBSOLETE_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionId", is(ValidationConstants.OBSOLETE_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionAnatomy", is(ValidationConstants.OBSOLETE_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionGeneOntology", is(ValidationConstants.OBSOLETE_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionTaxon", is(ValidationConstants.OBSOLETE_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionChemical", is(ValidationConstants.OBSOLETE_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.INVALID_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionId", is(ValidationConstants.INVALID_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionAnatomy", is(ValidationConstants.INVALID_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionGeneOntology", is(ValidationConstants.INVALID_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionChemical", is(ValidationConstants.INVALID_MESSAGE));
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.REQUIRED_MESSAGE));
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
				statusCode(200);
		
		// Reset conditionStatement as used by getExperimentalCondition() in subsequent tests
		// TODO: delete following once conditionSummary used for findBy endpoint
		editedExperimentalCondition.setConditionStatement("CRUD:Statement2");
		RestAssured.given().
			contentType("application/json").
			body(editedExperimentalCondition).
			when().
			put("/api/experimental-condition").
			then().
			statusCode(200);
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
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionClass", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(17)
	public void createWithObsoleteConditionClass() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testObsoleteZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement3");
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
	@Order(18)
	public void createWithObsoleteConditionId() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement4");
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
	@Order(19)
	public void createWithObsoleteConditionAnatomy() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement5");
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
	@Order(20)
	public void createWithObsoleteConditionGeneOntology() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement6");
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
	@Order(21)
	public void createWithObsoleteConditionTaxon() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement7");
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
	@Order(22)
	public void createWithObsoleteConditionChemical() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement8");
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
	@Order(23)
	public void createWithInvalidConditionClass() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		ZecoTerm nonPersistedZecoTerm = new ZecoTerm();
		nonPersistedZecoTerm.setCurie("NPZECO:0001");
		nonPersistedZecoTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(nonPersistedZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement9");
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
	@Order(24)
	public void createWithInvalidConditionId() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		ZecoTerm nonPersistedZecoTerm = new ZecoTerm();
		nonPersistedZecoTerm.setCurie("NPZECO:0001");
		nonPersistedZecoTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement10");
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
	@Order(25)
	public void createWithInvalidConditionAnatomy() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		ZfaTerm nonPersistedZfaTerm = new ZfaTerm();
		nonPersistedZfaTerm.setCurie("NPZFA:0001");
		nonPersistedZfaTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement11");
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
	@Order(26)
	public void createWithInvalidConditionGeneOntology() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		GOTerm nonPersistedGoTerm = new GOTerm();
		nonPersistedGoTerm.setCurie("NPGO:0001");
		nonPersistedGoTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement12");
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
	@Order(27)
	public void createWithInvalidConditionChemical() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		CHEBITerm nonPersistedChebiTerm = new CHEBITerm();
		nonPersistedChebiTerm.setCurie("NPCHEBI:0001");
		nonPersistedChebiTerm.setObsolete(false);
		
		experimentalCondition.setConditionClass(testZecoTerm2);
		experimentalCondition.setConditionStatement("CRUD:Statement13");
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
	@Order(28)
	public void createWithMissingConditionClass() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionStatement("CRUD:Statement14");
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
	@Order(29)
	public void createWithMissingConditionStatement() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm2);
		experimentalCondition.setConditionId(testZecoTerm3);
		experimentalCondition.setConditionQuantity("Amount15");
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
	}
	
	@Test
	@Order(30)
	public void createWithNonSlimConditionClass() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testNonSlimZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement16");
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
	@Order(31)
	public void createDuplicateExperimentalCondition() {
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(testZecoTerm);
		experimentalCondition.setConditionStatement("CRUD:Statement17");
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
		experimentalConditionDuplicate.setConditionStatement("CRUD:Statement17");
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
