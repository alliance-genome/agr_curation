package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(9)
public class AffectedGenomicModelITCase extends BaseITCase {

	private final String AGM = "AGM:0001";
	
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private Person person;
	
	private void loadRequiredEntities() {
		taxon = getNCBITaxonTerm("NCBITaxon:10090");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		person = createPerson("TEST:AGMPerson0001");
	}
	
	@Test
	@Order(1)
	public void createValidAGM() {
		loadRequiredEntities();
		
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setCurie(AGM);
		agm.setTaxon(taxon);
		agm.setName("Test AGM");
		agm.setDateCreated(datetime);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/agm/" + AGM).
			then().
			statusCode(200).
			body("entity.curie", is(AGM)).
			body("entity.name", is("Test AGM")).
			body("entity.taxon.curie", is(taxon.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.dateCreated", is(datetime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(2)
	public void editAGM() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setName("AGM edited");;
		agm.setTaxon(taxon2);
		agm.setInternal(true);
		agm.setObsolete(true);
		agm.setDateCreated(datetime2);
		agm.setCreatedBy(person);

		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(200);

		RestAssured.given().
			when().
			get("/api/agm/" + AGM).
			then().
			statusCode(200).
			body("entity.curie", is(AGM)).
			body("entity.name", is("AGM edited")).
			body("entity.taxon.curie", is(taxon2.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.dateCreated", is(datetime2.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}
	
	@Test
	@Order(3)
	public void createAGMWithMissingRequiredFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setName("Test AGM");
		agm.setDateCreated(datetime);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(2))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editAGMWithMissingCurie() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setCurie(null);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(5)
	public void editAGMWithMissingRequiredFields() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setTaxon(null);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void createAGMWithEmptyRequiredFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setCurie("");
		agm.setName("Test AGM");
		agm.setTaxon(taxon);
		agm.setDateCreated(datetime);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(7)
	public void editAGMWithEmptyCurie() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setCurie("");

		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(8)
	public void createAGMWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setCurie("AGM:0008");
		agm.setName("Test AGM");
		agm.setTaxon(nonPersistedTaxon);
		agm.setDateCreated(datetime);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void editAGMWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setTaxon(nonPersistedTaxon);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(10)
	public void editAGMWithNullNonRequiredFields() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setName(null);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/agm/" + AGM).
			then().
			statusCode(200).
			body("entity", not(hasKey("name")));
		
	}
	
	@Test
	@Order(11)
	public void deleteAGM() {

		RestAssured.given().
				when().
				delete("/api/agm/" + AGM).
				then().
				statusCode(200);
	}
}
