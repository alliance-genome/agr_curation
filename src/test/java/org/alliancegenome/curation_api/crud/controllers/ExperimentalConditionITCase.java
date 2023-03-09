package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Person;
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
	
	private ZECOTerm zecoTerm;
	private ZECOTerm zecoTerm2;
	private ZECOTerm zecoTerm3;
	private GOTerm goTerm;
	private GOTerm goTerm2;
	private CHEBITerm chebiTerm;
	private CHEBITerm chebiTerm2;
	private ZFATerm zfaTerm;
	private ZFATerm zfaTerm2;
	private NCBITaxonTerm ncbiTaxonTerm;
	private NCBITaxonTerm ncbiTaxonTerm2;
	private ZECOTerm obsoleteZecoTerm;
	private GOTerm obsoleteGoTerm;
	private CHEBITerm obsoleteChebiTerm;
	private ZFATerm obsoleteZfaTerm;
	private NCBITaxonTerm obsoleteNcbiTaxonTerm;
	private ZECOTerm nonSlimZecoTerm;
	private String conditionSummary;
	private Person person;
	
	private void createRequiredObjects() {
		zecoTerm = createZecoTerm("ZECO:ec0001", "Test ZecoTerm", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		zecoTerm2 = createZecoTerm("ZECO:ec0002", "Test ZecoTerm", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		zecoTerm3 = createZecoTerm("ZECO:ec0003", "Test ZecoTerm", false, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		obsoleteZecoTerm = createZecoTerm("ZECO:ec0005", "Test ZecoTerm", true, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		nonSlimZecoTerm = createZecoTerm("ZECO:ec0006", "Test ZecoTerm", false, null);
		goTerm = createGoTerm("GO:ec0001", "Test GOTerm", false);
		goTerm2 = createGoTerm("GO:ec0002", "Test GOTerm", false);
		obsoleteGoTerm = createGoTerm("GO:ec0003", "Test GOTerm", true);
		chebiTerm = createChebiTerm("CHEBI:ec0001", "Test CHEBITerm", false);
		chebiTerm2 = createChebiTerm("CHEBI:ec0002", "Test CHEBITerm", false);
		obsoleteChebiTerm = createChebiTerm("CHEBI:ec0004", "Test CHEBITerm", true);
		zfaTerm = createZfaTerm("ZFA:ec0001", false);
		zfaTerm2 = createZfaTerm("ZFA:ec0002", false);
		obsoleteZfaTerm = createZfaTerm("ZFA:ec0003", true);
		ncbiTaxonTerm = getNCBITaxonTerm("NCBITaxon:9606");
		ncbiTaxonTerm2 = getNCBITaxonTerm("NCBITaxon:6239");
		obsoleteNcbiTaxonTerm = getNCBITaxonTerm("NCBITaxon:1000");
		person = createPerson("TEST:ECPerson0001");
	}

	@Test
	@Order(1)
	public void createValidExperimentalCondition() {
		createRequiredObjects();
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		experimentalCondition.setConditionClass(zecoTerm);
		experimentalCondition.setConditionId(zecoTerm2);
		experimentalCondition.setConditionQuantity("Test quantity");
		experimentalCondition.setConditionAnatomy(zfaTerm);
		experimentalCondition.setConditionGeneOntology(goTerm);
		experimentalCondition.setConditionTaxon(ncbiTaxonTerm);
		experimentalCondition.setConditionChemical(chebiTerm);
		experimentalCondition.setConditionFreeText("Test text");
		
		conditionSummary = ExperimentalConditionSummary.getConditionSummary(experimentalCondition);
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/experimental-condition/findBy/" + conditionSummary).
			then().
			statusCode(200).
			body("entity.conditionClass.curie", is(zecoTerm.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.conditionId.curie", is(zecoTerm2.getCurie())).
			body("entity.conditionQuantity", is("Test quantity")).
			body("entity.conditionAnatomy.curie", is(zfaTerm.getCurie())).
			body("entity.conditionGeneOntology.curie", is(goTerm.getCurie())).
			body("entity.conditionTaxon.curie", is(ncbiTaxonTerm.getCurie())).
			body("entity.conditionChemical.curie", is(chebiTerm.getCurie())).
			body("entity.conditionFreeText", is("Test text"));
	}

	@Test
	@Order(2)
	public void editExperimentalcondition() {
		
		ExperimentalCondition experimentalCondition = getExperimentalCondition(conditionSummary);
		
		experimentalCondition.setCreatedBy(person);
		experimentalCondition.setConditionClass(zecoTerm2);
		experimentalCondition.setConditionId(zecoTerm);
		experimentalCondition.setConditionQuantity("Edited quantity");
		experimentalCondition.setConditionAnatomy(zfaTerm2);
		experimentalCondition.setConditionGeneOntology(goTerm2);
		experimentalCondition.setConditionTaxon(ncbiTaxonTerm2);
		experimentalCondition.setConditionChemical(chebiTerm2);
		experimentalCondition.setConditionFreeText("Edited text");
		experimentalCondition.setInternal(true);
		experimentalCondition.setObsolete(true);
		experimentalCondition.setCreatedBy(person);
		
		conditionSummary = ExperimentalConditionSummary.getConditionSummary(experimentalCondition);
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			put("/api/experimental-condition").
			then().
			statusCode(200);
		RestAssured.given().
			when().
			get("/api/experimental-condition/findBy/" + conditionSummary).
			then().
			statusCode(200).
			body("entity.conditionClass.curie", is(zecoTerm2.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.conditionId.curie", is(zecoTerm.getCurie())).
			body("entity.conditionQuantity", is("Edited quantity")).
			body("entity.conditionAnatomy.curie", is(zfaTerm2.getCurie())).
			body("entity.conditionGeneOntology.curie", is(goTerm2.getCurie())).
			body("entity.conditionTaxon.curie", is(ncbiTaxonTerm2.getCurie())).
			body("entity.conditionChemical.curie", is(chebiTerm2.getCurie())).
			body("entity.conditionFreeText", is("Edited text"));
	}

	@Test
	@Order(3)
	public void createExperimentalConditionWithMissingRequiredFields() {
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
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
	@Order(4)
	public void editExperimentalConditionWithMissingRequiredFields() {
		
		ExperimentalCondition experimentalCondition = getExperimentalCondition(conditionSummary);
		experimentalCondition.setConditionClass(null);
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			put("/api/experimental-condition").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.conditionClass", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(5)
	public void createExperimentalConditionWithInvalidFields() {
		ZECOTerm nonPersistedZecoTerm = new ZECOTerm();
		nonPersistedZecoTerm.setCurie("ZECO:Invalid");
		GOTerm nonPersistedGoTerm = new GOTerm();
		nonPersistedGoTerm.setCurie("GO:Invalid");
		CHEBITerm nonPersistedChebiTerm = new CHEBITerm();
		nonPersistedChebiTerm.setCurie("CHEBI:Invalid");
		ZFATerm nonPersistedZfaTerm = new ZFATerm();
		nonPersistedZfaTerm.setCurie("ZFA:Invalid");
		NCBITaxonTerm nonPersistedNcbiTaxonTerm = new NCBITaxonTerm();
		nonPersistedNcbiTaxonTerm.setCurie("NCBITaxon:Invalid");
		
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(nonSlimZecoTerm);
		experimentalCondition.setConditionId(nonPersistedZecoTerm);
		experimentalCondition.setConditionAnatomy(nonPersistedZfaTerm);
		experimentalCondition.setConditionGeneOntology(nonPersistedGoTerm);
		experimentalCondition.setConditionTaxon(nonPersistedNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(nonPersistedChebiTerm);
		
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.conditionClass", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionId", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionAnatomy", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionGeneOntology", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionTaxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionChemical", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void editExperimentalConditionWithInvalidFields() {
		ZECOTerm nonPersistedZecoTerm = new ZECOTerm();
		nonPersistedZecoTerm.setCurie("ZECO:Invalid");
		GOTerm nonPersistedGoTerm = new GOTerm();
		nonPersistedGoTerm.setCurie("GO:Invalid");
		CHEBITerm nonPersistedChebiTerm = new CHEBITerm();
		nonPersistedChebiTerm.setCurie("CHEBI:Invalid");
		ZFATerm nonPersistedZfaTerm = new ZFATerm();
		nonPersistedZfaTerm.setCurie("ZFA:Invalid");
		NCBITaxonTerm nonPersistedNcbiTaxonTerm = new NCBITaxonTerm();
		nonPersistedNcbiTaxonTerm.setCurie("NCBITaxon:Invalid");
		
		ExperimentalCondition experimentalCondition = getExperimentalCondition(conditionSummary);
		
		experimentalCondition.setConditionClass(nonSlimZecoTerm);
		experimentalCondition.setConditionId(nonPersistedZecoTerm);
		experimentalCondition.setConditionAnatomy(nonPersistedZfaTerm);
		experimentalCondition.setConditionGeneOntology(nonPersistedGoTerm);
		experimentalCondition.setConditionTaxon(nonPersistedNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(nonPersistedChebiTerm);
		
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			put("/api/experimental-condition").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.conditionClass", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionId", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionAnatomy", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionGeneOntology", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionTaxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionChemical", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(7)
	public void createExperimentalConditionWithObsoleteFields() {
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		
		experimentalCondition.setConditionClass(obsoleteZecoTerm);
		experimentalCondition.setConditionId(obsoleteZecoTerm);
		experimentalCondition.setConditionAnatomy(obsoleteZfaTerm);
		experimentalCondition.setConditionGeneOntology(obsoleteGoTerm);
		experimentalCondition.setConditionTaxon(obsoleteNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(obsoleteChebiTerm);
		
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.conditionClass", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionId", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionAnatomy", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionGeneOntology", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionTaxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionChemical", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(8)
	public void editExperimentalConditionWithObsoleteFields() {
		ExperimentalCondition experimentalCondition = getExperimentalCondition(conditionSummary);
		
		experimentalCondition.setConditionClass(obsoleteZecoTerm);
		experimentalCondition.setConditionId(obsoleteZecoTerm);
		experimentalCondition.setConditionAnatomy(obsoleteZfaTerm);
		experimentalCondition.setConditionGeneOntology(obsoleteGoTerm);
		experimentalCondition.setConditionTaxon(obsoleteNcbiTaxonTerm);
		experimentalCondition.setConditionChemical(obsoleteChebiTerm);
		
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			put("/api/experimental-condition").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.conditionClass", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionId", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionAnatomy", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionGeneOntology", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionTaxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionChemical", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void editExperimentalConditionWithNullNonRequiredFields() {
		ExperimentalCondition experimentalCondition = getExperimentalCondition(conditionSummary);
		
		experimentalCondition.setConditionId(null);
		experimentalCondition.setConditionAnatomy(null);
		experimentalCondition.setConditionGeneOntology(null);
		experimentalCondition.setConditionTaxon(null);
		experimentalCondition.setConditionChemical(null);
		experimentalCondition.setConditionQuantity(null);
		experimentalCondition.setConditionFreeText(null);
		
		conditionSummary = ExperimentalConditionSummary.getConditionSummary(experimentalCondition);
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			put("/api/experimental-condition").
			then().
			statusCode(200).
			body("entity", not(hasKey("conditionId"))).
			body("entity", not(hasKey("conditionAnatomy"))).
			body("entity", not(hasKey("conditionGeneOntology"))).
			body("entity", not(hasKey("conditionTaxon"))).
			body("entity", not(hasKey("conditionChemical"))).
			body("entity", not(hasKey("conditionQuantity"))).
			body("entity", not(hasKey("conditionFreeText")));
	}
	
	@Test
	@Order(10)
	public void createDuplicateExperimentalCondition() {
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		experimentalCondition.setConditionClass(zecoTerm2);
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.uniqueId", is(ValidationConstants.NON_UNIQUE_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void editDuplicateExperimentalCondition() {
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		experimentalCondition.setConditionClass(zecoTerm2);
		experimentalCondition.setConditionId(nonSlimZecoTerm);

		String newConditionSummary = ExperimentalConditionSummary.getConditionSummary(experimentalCondition);
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(200);
		
		ExperimentalCondition experimentalCondition2 = getExperimentalCondition(newConditionSummary);
		experimentalCondition2.setConditionId(null);
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.uniqueId", is(ValidationConstants.NON_UNIQUE_MESSAGE));
	}
	
	@Test
	@Order(12)
	public void createExperimentalConditionWithOnlyRequiredFields() {
		ExperimentalCondition experimentalCondition = new ExperimentalCondition();
		experimentalCondition.setConditionClass(zecoTerm3);
		
		RestAssured.given().
			contentType("application/json").
			body(experimentalCondition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(200);
	}
}
