package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("303 - AffectedGenomicModelITCase")
@Order(303)
public class AffectedGenomicModelITCase extends BaseITCase {

	private static final String AGM = "AGM:0001";
	
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private NCBITaxonTerm obsoleteTaxon;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private Person person;
	private VocabularyTerm subtype;
	private VocabularyTerm subtype2;
	private VocabularyTerm obsoleteSubtype;
	private Organization dataProvider;
	private Organization dataProvider2;
	private Organization obsoleteDataProvider;
	private Organization nonPersistedOrganization;
	private Reference reference;
	private Reference reference2;
	private Reference obsoleteReference;
	private Vocabulary nameType;
	private Vocabulary synonymScope;
	private VocabularyTerm exactSynonymScope;
	private VocabularyTerm broadSynonymScope;
	private VocabularyTerm obsoleteSynonymScope;
	private VocabularyTerm symbolNameType;
	private VocabularyTerm fullNameType;
	private VocabularyTerm obsoleteNameType;
	private VocabularyTerm obsoleteFullNameType;
	private AgmFullNameSlotAnnotation agmFullName;
	private AgmSynonymSlotAnnotation agmSynonym;
	private AgmSecondaryIdSlotAnnotation agmSecondaryId;
	
	private void loadRequiredEntities() {
		taxon = getNCBITaxonTerm("NCBITaxon:10090");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		person = createPerson("TEST:AGMPerson0001");
		Vocabulary subtypeVocabulary = getVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY);
		subtype = getVocabularyTerm(subtypeVocabulary, "fish");
		subtype2 = getVocabularyTerm(subtypeVocabulary, "genotype");
		obsoleteSubtype = createVocabularyTerm(subtypeVocabulary, "obsolete", true);
		obsoleteTaxon = getNCBITaxonTerm("NCBITaxon:0000");
		dataProvider = getOrganization("TEST");
		dataProvider2 = getOrganization("TEST2");
		obsoleteDataProvider = getOrganization("ODP");
		nonPersistedOrganization = new Organization();
		nonPersistedOrganization.setAbbreviation("INV");
		reference = createReference("AGRKB:000021003", false);
		reference2 = createReference("AGRKB:000021005", false);
		obsoleteReference = createReference("AGRKB:000021007", true);
		nameType = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		synonymScope = getVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY);
		fullNameType = getVocabularyTerm(nameType, "full_name");
		symbolNameType = getVocabularyTerm(nameType, "nomenclature_symbol");
		obsoleteNameType = getVocabularyTerm(nameType, "obsolete_name");
		obsoleteFullNameType = getVocabularyTerm(nameType, "obsolete_full_name");
		exactSynonymScope = getVocabularyTerm(synonymScope, "exact");
		broadSynonymScope = getVocabularyTerm(synonymScope, "broad");
		obsoleteSynonymScope = getVocabularyTerm(synonymScope, "obsolete");
		agmFullName = createAgmFullNameSlotAnnotation(List.of(reference), "AGM test 1", fullNameType, exactSynonymScope, "https://test.org");
		agmSynonym = createAgmSynonymSlotAnnotation(List.of(reference), "AGM test synonym 1", symbolNameType, exactSynonymScope, "https://test.org");
		agmSecondaryId = createAgmSecondaryIdSlotAnnotation(List.of(reference), "SecondaryTest");
		
		
	}
	
	@Test
	@Order(1)
	public void createValidAGM() {
		loadRequiredEntities();
		
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId(AGM);
		agm.setTaxon(taxon);
		agm.setAgmFullName(agmFullName);
		agm.setDateCreated(datetime);
		agm.setSubtype(subtype);
		agm.setDataProvider(dataProvider);
		agm.setAgmFullName(agmFullName);
		agm.setAgmSynonyms(List.of(agmSynonym));
		agm.setAgmSecondaryIds(List.of(agmSecondaryId));
		
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
			body("entity.primaryExternalId", is(AGM)).
			body("entity.agmFullName.formatText", is("AGM test 1")).
			body("entity.subtype.name", is(subtype.getName())).
			body("entity.taxon.curie", is(taxon.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.agmSynonyms[0].displayText", is("AGM test synonym 1")).
			body("entity.agmSecondaryIds[0].secondaryId", is("SecondaryTest")).
			body("entity.dateCreated", is(datetime.toString())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dataProvider.abbreviation", is(dataProvider.getAbbreviation()));
	}

	@Test
	@Order(2)
	public void editAGM() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setTaxon(taxon2);
		agm.setInternal(true);
		agm.setObsolete(true);
		agm.setDateCreated(datetime2);
		agm.setCreatedBy(person);
		agm.setSubtype(subtype2);
		agm.setDataProvider(dataProvider2);
		
		AgmFullNameSlotAnnotation editedFullName = agm.getAgmFullName();
		editedFullName.setDisplayText("AGM test 2 display");
		editedFullName.setFormatText("AGM test 2 format");
		editedFullName.setSynonymScope(broadSynonymScope);
		editedFullName.setSynonymUrl("https://test2.org");
		editedFullName.setEvidence(List.of(reference2));
		agm.setAgmFullName(editedFullName);
		
		AgmSynonymSlotAnnotation editedSynonym = agm.getAgmSynonyms().get(0);
		editedSynonym.setNameType(fullNameType);
		editedSynonym.setDisplayText("AGM test synonym 2 display");
		editedSynonym.setFormatText("AGM test synonym 2 format");
		editedSynonym.setSynonymScope(broadSynonymScope);
		editedSynonym.setSynonymUrl("https://test2.org");
		editedSynonym.setEvidence(List.of(reference2));
		agm.setAgmSynonyms(List.of(editedSynonym));
		
		AgmSecondaryIdSlotAnnotation editedSecondaryId = agm.getAgmSecondaryIds().get(0);
		editedSecondaryId.setSecondaryId("SecondaryTest2");
		editedSecondaryId.setEvidence(List.of(reference2));

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
			body("entity.primaryExternalId", is(AGM)).
			body("entity.agmFullName.formatText", is("AGM test 2 format")).
			body("entity.agmSynonyms[0].displayText", is("AGM test synonym 2 display")).
			body("entity.agmSecondaryIds[0].secondaryId", is("SecondaryTest2")).
			body("entity.subtype.name", is(subtype2.getName())).
			body("entity.taxon.curie", is(taxon2.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.dateCreated", is(datetime2.toString())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dataProvider.abbreviation", is(dataProvider2.getAbbreviation()));
	}
	
	@Test
	@Order(3)
	public void createAGMWithMissingRequiredFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + " primaryExternalId")).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editAGMWithMissingPrimaryExternalId() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setPrimaryExternalId(null);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + " primaryExternalId"));
	}
	
	@Test
	@Order(5)
	public void editAGMWithMissingRequiredFields() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setTaxon(null);
		agm.setSubtype(null);
		agm.setDataProvider(null);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void createAGMWithEmptyRequiredFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId("");
		agm.setTaxon(taxon);
		agm.setSubtype(subtype);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + " primaryExternalId"));
	}

	@Test
	@Order(7)
	public void editAGMWithEmptyPrimaryExternalId() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setPrimaryExternalId("");

		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.modInternalId", is(ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + " primaryExternalId"));
	}
	
	@Test
	@Order(8)
	public void createAGMWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		VocabularyTerm nonPersistedTerm = new VocabularyTerm();
		nonPersistedTerm.setName("invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId("AGM:0008");
		agm.setTaxon(nonPersistedTaxon);
		agm.setSubtype(nonPersistedTerm);
		agm.setDataProvider(nonPersistedOrganization);
		
		AgmFullNameSlotAnnotation invalidFullName = createAgmFullNameSlotAnnotation(List.of(nonPersistedReference), "Test name", symbolNameType, fullNameType, "https://test.org");
		AgmSynonymSlotAnnotation invalidSynonym = createAgmSynonymSlotAnnotation(List.of(nonPersistedReference), "Test synonym", exactSynonymScope, fullNameType, "https://test.org");
		AgmSecondaryIdSlotAnnotation invalidSecondaryId = createAgmSecondaryIdSlotAnnotation(List.of(nonPersistedReference), "SecondaryTest");
		
		agm.setAgmFullName(invalidFullName);
		agm.setAgmSynonyms(List.of(invalidSynonym));
		agm.setAgmSecondaryIds(List.of(invalidSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.agmFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.agmSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.agmSecondaryIds", is("evidence - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void editAGMWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		VocabularyTerm nonPersistedTerm = new VocabularyTerm();
		nonPersistedTerm.setName("invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setTaxon(nonPersistedTaxon);
		agm.setSubtype(nonPersistedTerm);
		agm.setDataProvider(nonPersistedOrganization);
		
		AgmFullNameSlotAnnotation invalidFullName = agm.getAgmFullName();
		invalidFullName.setEvidence(List.of(nonPersistedReference));
		invalidFullName.setNameType(symbolNameType);
		invalidFullName.setSynonymScope(fullNameType);
		AgmSynonymSlotAnnotation invalidSynonym = agm.getAgmSynonyms().get(0);
		invalidSynonym.setEvidence(List.of(nonPersistedReference));
		invalidSynonym.setNameType(broadSynonymScope);
		invalidSynonym.setSynonymScope(fullNameType);
		AgmSecondaryIdSlotAnnotation invalidSecondaryId = agm.getAgmSecondaryIds().get(0);
		invalidSecondaryId.setEvidence(List.of(nonPersistedReference));
		
		agm.setAgmFullName(invalidFullName);
		agm.setAgmSynonyms(List.of(invalidSynonym));
		agm.setAgmSecondaryIds(List.of(invalidSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.agmFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.agmSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.agmSecondaryIds", is("evidence - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(10)
	public void createAGMWithObsoleteFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId("AGM:0010");
		agm.setTaxon(obsoleteTaxon);
		agm.setSubtype(obsoleteSubtype);
		agm.setDataProvider(obsoleteDataProvider);
		
		AgmFullNameSlotAnnotation obsoleteFullName = createAgmFullNameSlotAnnotation(List.of(obsoleteReference), "Test name", obsoleteFullNameType, obsoleteSynonymScope, "https://test.org");
		AgmSynonymSlotAnnotation obsoleteSynonym = createAgmSynonymSlotAnnotation(List.of(obsoleteReference), "Test synonym", obsoleteNameType, obsoleteSynonymScope, "https://test.org");
		AgmSecondaryIdSlotAnnotation obsoleteSecondaryId = createAgmSecondaryIdSlotAnnotation(List.of(obsoleteReference), "SecondaryTest");

		agm.setAgmFullName(obsoleteFullName);
		agm.setAgmSynonyms(List.of(obsoleteSynonym));
		agm.setAgmSecondaryIds(List.of(obsoleteSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.agmFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.agmSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.agmSecondaryIds", is("evidence - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void editAGMWithObsoleteFields() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setTaxon(obsoleteTaxon);
		agm.setSubtype(obsoleteSubtype);
		agm.setDataProvider(obsoleteDataProvider);
		
		AgmFullNameSlotAnnotation obsoleteFullName = agm.getAgmFullName();
		obsoleteFullName.setEvidence(List.of(obsoleteReference));
		obsoleteFullName.setNameType(obsoleteFullNameType);
		obsoleteFullName.setSynonymScope(obsoleteSynonymScope);
		AgmSynonymSlotAnnotation obsoleteSynonym = agm.getAgmSynonyms().get(0);
		obsoleteSynonym.setEvidence(List.of(obsoleteReference));
		obsoleteSynonym.setNameType(obsoleteNameType);
		obsoleteSynonym.setSynonymScope(obsoleteSynonymScope);
		AgmSecondaryIdSlotAnnotation obsoleteSecondaryId = agm.getAgmSecondaryIds().get(0);
		obsoleteSecondaryId.setEvidence(List.of(obsoleteReference));
		
		agm.setAgmFullName(obsoleteFullName);
		agm.setAgmSynonyms(List.of(obsoleteSynonym));
		agm.setAgmSecondaryIds(List.of(obsoleteSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			put("/api/agm").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.subtype", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.agmFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.agmSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"nameType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"synonymScope - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.agmSecondaryIds", is("evidence - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(12)
	public void editAGMWithNullNonRequiredFields() {
		AffectedGenomicModel agm = getAffectedGenomicModel(AGM);
		agm.setAgmFullName(null);
		agm.setAgmSynonyms(null);
		agm.setAgmSecondaryIds(null);
		agm.setDataProviderCrossReference(null);
		
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
			body("entity", not(hasKey("dataProviderCrossReference"))).
			body("entity", not(hasKey("agmFullName"))).
			body("entity", not(hasKey("agmSynonyms"))).
			body("entity", not(hasKey("agmSecondaryIds")));
		
	}
	
	@Test
	@Order(13)
	public void createAGMWithOnlyRequiredFields() {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId("AGM:0015");
		agm.setTaxon(taxon);
		agm.setSubtype(subtype);
		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(14)
	public void deleteAGM() {

		RestAssured.given().
				when().
				delete("/api/agm/" + AGM).
				then().
				statusCode(200);
	}
	
	private AgmFullNameSlotAnnotation createAgmFullNameSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		AgmFullNameSlotAnnotation fullName = new AgmFullNameSlotAnnotation();
		fullName.setEvidence(evidence);
		fullName.setDisplayText(name);
		fullName.setFormatText(name);
		fullName.setNameType(nameType);
		fullName.setSynonymScope(synonymScope);
		fullName.setSynonymUrl(synonymUrl);
		
		return fullName;
	}

	private AgmSynonymSlotAnnotation createAgmSynonymSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		AgmSynonymSlotAnnotation synonym = new AgmSynonymSlotAnnotation();
		synonym.setEvidence(evidence);
		synonym.setDisplayText(name);
		synonym.setFormatText(name);
		synonym.setNameType(nameType);
		synonym.setSynonymScope(synonymScope);
		synonym.setSynonymUrl(synonymUrl);
		
		return synonym;
	}
	
	private AgmSecondaryIdSlotAnnotation createAgmSecondaryIdSlotAnnotation(List<InformationContentEntity> evidence, String secondaryId) {
		AgmSecondaryIdSlotAnnotation secondaryIdAnnotation = new AgmSecondaryIdSlotAnnotation();
		secondaryIdAnnotation.setEvidence(evidence);
		secondaryIdAnnotation.setSecondaryId(secondaryId);
		
		return secondaryIdAnnotation;
	}
}
