package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
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
@Order(8)
public class AlleleITCase extends BaseITCase {

	private final String ALLELE = "Allele:0001";
	
	private Vocabulary inheritanceModeVocabulary;
	private Vocabulary inCollectionVocabulary;
	private Vocabulary nameTypeVocabulary;
	private Vocabulary synonymScopeVocabulary;
	private VocabularyTerm dominantInheritanceMode;
	private VocabularyTerm recessiveInheritanceMode;
	private VocabularyTerm mmpInCollection;
	private VocabularyTerm wgsInCollection;
	private VocabularyTerm symbolNameType;
	private VocabularyTerm fullNameType;
	private VocabularyTerm systematicNameType;
	private VocabularyTerm exactSynonymScope;
	private VocabularyTerm broadSynonymScope;
	private Reference reference;
	private Reference reference2;
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private Person person;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private SOTerm soTerm;
	private SOTerm soTerm2;
	private AlleleMutationTypeSlotAnnotation alleleMutationType;
	private AlleleSymbolSlotAnnotation alleleSymbol;
	private AlleleFullNameSlotAnnotation alleleFullName;
	private AlleleSynonymSlotAnnotation alleleSynonym;
	private AlleleSecondaryIdSlotAnnotation alleleSecondaryId;
	
	
	private void loadRequiredEntities() {
		inheritanceModeVocabulary = getVocabulary(VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY);
		inCollectionVocabulary = getVocabulary(VocabularyConstants.ALLELE_COLLECTION_VOCABULARY);
		nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		synonymScopeVocabulary = getVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY);
		dominantInheritanceMode = getVocabularyTerm(inheritanceModeVocabulary, "dominant");
		recessiveInheritanceMode = getVocabularyTerm(inheritanceModeVocabulary, "recessive");
		mmpInCollection = getVocabularyTerm(inCollectionVocabulary, "Million_mutations_project");
		wgsInCollection = getVocabularyTerm(inCollectionVocabulary, "WGS_Hobert");
		symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		fullNameType = getVocabularyTerm(nameTypeVocabulary, "full_name");
		systematicNameType = getVocabularyTerm(nameTypeVocabulary, "systematic_name");
		exactSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "exact");
		broadSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "broad");
		reference = createReference("AGRKB:000000003", false);
		reference2 = createReference("AGRKB:000000005", false);
		taxon = getNCBITaxonTerm("NCBITaxon:10090");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		person = createPerson("TEST:AllelePerson0001");
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		soTerm = createSoTerm("SO:00002");
		soTerm2 = createSoTerm("SO:00003");
		alleleMutationType = createAlleleMutationTypeSlotAnnotation(List.of(reference), List.of(soTerm));
		alleleSymbol = createAlleleSymbolSlotAnnotation(List.of(reference), "Test symbol", symbolNameType, exactSynonymScope, "https://test.org");
		alleleFullName = createAlleleFullNameSlotAnnotation(List.of(reference), "Test name", fullNameType, exactSynonymScope, "https://test.org");
		alleleSynonym = createAlleleSynonymSlotAnnotation(List.of(reference), "Test synonym", systematicNameType, exactSynonymScope, "https://test.org");
		alleleSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(reference), "TEST:Secondary");
	}
	
	@Test
	@Order(1)
	public void createValidAllele() {
		loadRequiredEntities();
		
		Allele allele = new Allele();
		allele.setCurie(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(dominantInheritanceMode);
		allele.setInCollection(mmpInCollection);
		allele.setReferences(List.of(reference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		allele.setAlleleFullName(alleleFullName);
		allele.setAlleleSynonyms(List.of(alleleSynonym));
		allele.setAlleleSecondaryIds(List.of(alleleSecondaryId));
		
		RestAssured.given().
				contentType("application/json").
				body(allele).
				when().
				post("/api/allele").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/allele/" + ALLELE).
				then().
				statusCode(200).
				body("entity.curie", is(ALLELE)).
				body("entity.taxon.curie", is(taxon.getCurie())).
				body("entity.internal", is(false)).
				body("entity.obsolete", is(false)).
				body("entity.inheritanceMode.name", is(dominantInheritanceMode.getName())).
				body("entity.inCollection.name", is(mmpInCollection.getName())).
				body("entity.isExtinct", is(false)).
				body("entity.references[0].curie", is(reference.getCurie())).
				body("entity.dateCreated", is(datetime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.alleleMutationTypes[0].evidence[0].curie", is(reference.getCurie())).
				body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(soTerm.getCurie())).
				body("entity.alleleSymbol.displayText", is(alleleSymbol.getDisplayText())).
				body("entity.alleleSymbol.formatText", is(alleleSymbol.getFormatText())).
				body("entity.alleleSymbol.nameType.name", is(alleleSymbol.getNameType().getName())).
				body("entity.alleleSymbol.synonymScope.name", is(alleleSymbol.getSynonymScope().getName())).
				body("entity.alleleSymbol.synonymUrl", is(alleleSymbol.getSynonymUrl())).
				body("entity.alleleSymbol.evidence[0].curie", is(alleleSymbol.getEvidence().get(0).getCurie())).
				body("entity.alleleFullName.displayText", is(alleleFullName.getDisplayText())).
				body("entity.alleleFullName.formatText", is(alleleFullName.getFormatText())).
				body("entity.alleleFullName.nameType.name", is(alleleFullName.getNameType().getName())).
				body("entity.alleleFullName.synonymScope.name", is(alleleFullName.getSynonymScope().getName())).
				body("entity.alleleFullName.synonymUrl", is(alleleFullName.getSynonymUrl())).
				body("entity.alleleFullName.evidence[0].curie", is(alleleFullName.getEvidence().get(0).getCurie())).
				body("entity.alleleSynonyms[0].displayText", is(alleleSynonym.getDisplayText())).
				body("entity.alleleSynonyms[0].formatText", is(alleleSynonym.getFormatText())).
				body("entity.alleleSynonyms[0].nameType.name", is(alleleSynonym.getNameType().getName())).
				body("entity.alleleSynonyms[0].synonymScope.name", is(alleleSynonym.getSynonymScope().getName())).
				body("entity.alleleSynonyms[0].synonymUrl", is(alleleSynonym.getSynonymUrl())).
				body("entity.alleleSynonyms[0].evidence[0].curie", is(alleleSynonym.getEvidence().get(0).getCurie())).
				body("entity.alleleSecondaryIds[0].secondaryId", is(alleleSecondaryId.getSecondaryId())).
				body("entity.alleleSecondaryIds[0].evidence[0].curie", is(alleleSecondaryId.getEvidence().get(0).getCurie()));	
	}

	@Test
	@Order(2)
	public void editAllele() {
		Allele allele = getAllele(ALLELE);
		allele.setCreatedBy(person);
		allele.setCurie(ALLELE);
		allele.setTaxon(taxon2);
		allele.setInheritanceMode(recessiveInheritanceMode);
		allele.setInCollection(wgsInCollection);
		allele.setReferences(List.of(reference2));
		allele.setIsExtinct(true);
		allele.setInternal(true);
		allele.setObsolete(true);
		allele.setDateCreated(datetime2);
		
		AlleleMutationTypeSlotAnnotation editedMutationType = allele.getAlleleMutationTypes().get(0);
		editedMutationType.setMutationTypes(List.of(soTerm2));
		editedMutationType.setEvidence(List.of(reference2));
		allele.setAlleleMutationTypes(List.of(editedMutationType));
		
		AlleleSymbolSlotAnnotation editedSymbol = allele.getAlleleSymbol();
		editedSymbol.setDisplayText("EditedDisplay");
		editedSymbol.setFormatText("EditedFormat");
		editedSymbol.setNameType(systematicNameType);
		editedSymbol.setSynonymScope(broadSynonymScope);
		editedSymbol.setSynonymUrl("https://test2.org");
		editedSymbol.setEvidence(List.of(reference2));
		allele.setAlleleSymbol(editedSymbol);
		
		AlleleFullNameSlotAnnotation editedFullName = allele.getAlleleFullName();
		editedFullName.setDisplayText("EditedDisplay");
		editedFullName.setFormatText("EditedFormat");
		editedFullName.setSynonymScope(broadSynonymScope);
		editedFullName.setSynonymUrl("https://test2.org");
		editedFullName.setEvidence(List.of(reference2));
		allele.setAlleleFullName(editedFullName);
		
		AlleleSynonymSlotAnnotation editedSynonym = allele.getAlleleSynonyms().get(0);
		editedSynonym.setDisplayText("EditedDisplay");
		editedSynonym.setFormatText("EditedFormat");
		editedSynonym.setNameType(fullNameType);
		editedSynonym.setSynonymScope(broadSynonymScope);
		editedSynonym.setSynonymUrl("https://test2.org");
		editedSynonym.setEvidence(List.of(reference2));
		allele.setAlleleSynonyms(List.of(editedSynonym));
		
		AlleleSecondaryIdSlotAnnotation editedSecondaryId = allele.getAlleleSecondaryIds().get(0);
		editedSecondaryId.setSecondaryId("TEST:Secondary2");
		editedSecondaryId.setEvidence(List.of(reference2));
		allele.setAlleleSecondaryIds(List.of(editedSecondaryId));
		
		RestAssured.given().
				contentType("application/json").
				body(allele).
				when().
				put("/api/allele").
				then().
				statusCode(200);

		RestAssured.given().
				when().
				get("/api/allele/" + ALLELE).
				then().
				statusCode(200).
				body("entity.curie", is(ALLELE)).
				body("entity.inheritanceMode.name", is(recessiveInheritanceMode.getName())).
				body("entity.inCollection.name", is(wgsInCollection.getName())).
				body("entity.references[0].curie", is(reference2.getCurie())).
				body("entity.dateCreated", is(datetime2.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
				body("entity.createdBy.uniqueId", is(person.getUniqueId())).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.taxon.curie", is(taxon2.getCurie())).
				body("entity.internal", is(true)).
				body("entity.obsolete", is(true)).
				body("entity.isExtinct", is(true)).
				body("entity.createdBy.uniqueId", is(person.getUniqueId())).
				body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(editedMutationType.getMutationTypes().get(0).getCurie())).
				body("entity.alleleMutationTypes[0].evidence[0].curie", is(editedMutationType.getEvidence().get(0).getCurie())).
				body("entity.alleleSymbol.displayText", is(editedSymbol.getDisplayText())).
				body("entity.alleleSymbol.formatText", is(editedSymbol.getFormatText())).
				body("entity.alleleSymbol.nameType.name", is(editedSymbol.getNameType().getName())).
				body("entity.alleleSymbol.synonymScope.name", is(editedSymbol.getSynonymScope().getName())).
				body("entity.alleleSymbol.synonymUrl", is(editedSymbol.getSynonymUrl())).
				body("entity.alleleSymbol.evidence[0].curie", is(editedSymbol.getEvidence().get(0).getCurie())).
				body("entity.alleleFullName.displayText", is(editedFullName.getDisplayText())).
				body("entity.alleleFullName.formatText", is(editedFullName.getFormatText())).
				body("entity.alleleFullName.nameType.name", is(editedFullName.getNameType().getName())).
				body("entity.alleleFullName.synonymScope.name", is(editedFullName.getSynonymScope().getName())).
				body("entity.alleleFullName.synonymUrl", is(editedFullName.getSynonymUrl())).
				body("entity.alleleFullName.evidence[0].curie", is(editedFullName.getEvidence().get(0).getCurie())).
				body("entity.alleleSynonyms[0].displayText", is(editedSynonym.getDisplayText())).
				body("entity.alleleSynonyms[0].formatText", is(editedSynonym.getFormatText())).
				body("entity.alleleSynonyms[0].nameType.name", is(editedSynonym.getNameType().getName())).
				body("entity.alleleSynonyms[0].synonymScope.name", is(editedSynonym.getSynonymScope().getName())).
				body("entity.alleleSynonyms[0].synonymUrl", is(editedSynonym.getSynonymUrl())).
				body("entity.alleleSynonyms[0].evidence[0].curie", is(editedSynonym.getEvidence().get(0).getCurie())).
				body("entity.alleleSecondaryIds[0].secondaryId", is("TEST:Secondary2")).
				body("entity.alleleSecondaryIds[0].evidence[0].curie", is(editedSecondaryId.getEvidence().get(0).getCurie()));
	}
	
	@Test
	@Order(3)
	public void createAlleleWithMissingRequiredFieldsLevel1() {
		Allele allele = new Allele();
		allele.setInheritanceMode(dominantInheritanceMode);
		allele.setInCollection(mmpInCollection);
		allele.setReferences(List.of(reference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleFullName(alleleFullName);
		allele.setAlleleSynonyms(List.of(alleleSynonym));
		allele.setAlleleSecondaryIds(List.of(alleleSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editAlleleWithMissingCurie() {
		Allele allele = getAllele(ALLELE);
		allele.setCurie(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(5)
	public void editAlleleWithMissingRequiredFieldsLevel1() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(null);
		allele.setAlleleSymbol(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(2))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void createAlleleWithEmptyRequiredFieldsLevel1() {
		Allele allele = new Allele();
		allele.setCurie("");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(dominantInheritanceMode);
		allele.setInCollection(mmpInCollection);
		allele.setReferences(List.of(reference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		allele.setAlleleFullName(alleleFullName);
		allele.setAlleleSynonyms(List.of(alleleSynonym));
		allele.setAlleleSecondaryIds(List.of(alleleSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(7)
	public void editAlleleWithEmptyCurie() {
		Allele allele = getAllele(ALLELE);
		allele.setCurie("");
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.curie", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(8)
	public void createAlleleWithMissingRequiredFieldsLevel2() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0007");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(dominantInheritanceMode);
		allele.setInCollection(mmpInCollection);
		allele.setReferences(List.of(reference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		
		AlleleMutationTypeSlotAnnotation invalidMutationType = createAlleleMutationTypeSlotAnnotation(List.of(reference), null);
		AlleleSymbolSlotAnnotation invalidSymbol = createAlleleSymbolSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		AlleleFullNameSlotAnnotation invalidFullName = createAlleleFullNameSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		AlleleSynonymSlotAnnotation invalidSynonym = createAlleleSynonymSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(reference), null);
		
		allele.setAlleleMutationTypes(List.of(invalidMutationType));
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.alleleMutationTypes", is("mutationTypes - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void editAlleleWithMissingRequiredFieldsLevel2() {
		Allele allele = getAllele(ALLELE);
		
		AlleleMutationTypeSlotAnnotation invalidMutationType = createAlleleMutationTypeSlotAnnotation(List.of(reference), null);
		AlleleSymbolSlotAnnotation invalidSymbol = createAlleleSymbolSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		AlleleFullNameSlotAnnotation invalidFullName = createAlleleFullNameSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		AlleleSynonymSlotAnnotation invalidSynonym = createAlleleSynonymSlotAnnotation(List.of(reference), null, null, exactSynonymScope, "https://test.org");
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(reference), null);
		
		allele.setAlleleMutationTypes(List.of(invalidMutationType));
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(5))).
			body("errorMessages.alleleMutationTypes", is("mutationTypes - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE,
					"nameType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(10)
	public void createAlleleWithEmptyRequiredFieldsLevel2() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0009");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(dominantInheritanceMode);
		allele.setInCollection(mmpInCollection);
		allele.setReferences(List.of(reference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation invalidSymbol = createAlleleSymbolSlotAnnotation(List.of(reference), "", symbolNameType, exactSynonymScope, "https://test.org");
		AlleleFullNameSlotAnnotation invalidFullName = createAlleleFullNameSlotAnnotation(List.of(reference), "", fullNameType, exactSynonymScope, "https://test.org");
		AlleleSynonymSlotAnnotation invalidSynonym = createAlleleSynonymSlotAnnotation(List.of(reference), "", systematicNameType, exactSynonymScope, "https://test.org");
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(reference), "");
		
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void editAlleleWithEmptyRequiredFieldsLevel2() {
		Allele allele = getAllele(ALLELE);
		
		AlleleSymbolSlotAnnotation invalidSymbol = createAlleleSymbolSlotAnnotation(List.of(reference), "", symbolNameType, exactSynonymScope, "https://test.org");
		AlleleFullNameSlotAnnotation invalidFullName = createAlleleFullNameSlotAnnotation(List.of(reference), "", fullNameType, exactSynonymScope, "https://test.org");
		AlleleSynonymSlotAnnotation invalidSynonym = createAlleleSynonymSlotAnnotation(List.of(reference), "", systematicNameType, exactSynonymScope, "https://test.org");
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(reference), "");
		
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"displayText - " + ValidationConstants.REQUIRED_MESSAGE,
					"formatText - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(12)
	public void createAlleleWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:Invalid");
		
		Allele allele = new Allele();
		allele.setCurie("Allele:0012");
		allele.setTaxon(nonPersistedTaxon);
		allele.setInheritanceMode(mmpInCollection);
		allele.setInCollection(dominantInheritanceMode);
		allele.setReferences(List.of(nonPersistedReference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		
		AlleleMutationTypeSlotAnnotation invalidMutationType = createAlleleMutationTypeSlotAnnotation(List.of(nonPersistedReference), List.of(nonPersistedSoTerm));
		AlleleSymbolSlotAnnotation invalidSymbol = createAlleleSymbolSlotAnnotation(List.of(nonPersistedReference), "Test symbol", fullNameType, dominantInheritanceMode, "https://test.org");
		AlleleFullNameSlotAnnotation invalidFullName = createAlleleFullNameSlotAnnotation(List.of(nonPersistedReference), "Test name", symbolNameType, dominantInheritanceMode, "https://test.org");
		AlleleSynonymSlotAnnotation invalidSynonym = createAlleleSynonymSlotAnnotation(List.of(nonPersistedReference), "Test synonym", mmpInCollection, dominantInheritanceMode, "https://test.org");
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(nonPersistedReference), "TEST:Secondary");
		
		allele.setAlleleMutationTypes(List.of(invalidMutationType));
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(9))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inheritanceMode", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inCollection", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleMutationTypes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"mutationTypes - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("evidence - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(13)
	public void editAlleleWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:Invalid");
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(nonPersistedTaxon);
		allele.setInheritanceMode(mmpInCollection);
		allele.setInCollection(dominantInheritanceMode);
		allele.setReferences(List.of(nonPersistedReference));
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		
		AlleleMutationTypeSlotAnnotation invalidMutationType = createAlleleMutationTypeSlotAnnotation(List.of(nonPersistedReference), List.of(nonPersistedSoTerm));
		AlleleSymbolSlotAnnotation invalidSymbol = createAlleleSymbolSlotAnnotation(List.of(nonPersistedReference), "Test symbol", fullNameType, dominantInheritanceMode, "https://test.org");
		AlleleFullNameSlotAnnotation invalidFullName = createAlleleFullNameSlotAnnotation(List.of(nonPersistedReference), "Test name", symbolNameType, dominantInheritanceMode, "https://test.org");
		AlleleSynonymSlotAnnotation invalidSynonym = createAlleleSynonymSlotAnnotation(List.of(nonPersistedReference), "Test synonym", mmpInCollection, dominantInheritanceMode, "https://test.org");
		AlleleSecondaryIdSlotAnnotation invalidSecondaryId = createAlleleSecondaryIdSlotAnnotation(List.of(nonPersistedReference), "TEST:Secondary");
		
		allele.setAlleleMutationTypes(List.of(invalidMutationType));
		allele.setAlleleSymbol(invalidSymbol);
		allele.setAlleleFullName(invalidFullName);
		allele.setAlleleSynonyms(List.of(invalidSynonym));
		allele.setAlleleSecondaryIds(List.of(invalidSecondaryId));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(9))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inheritanceMode", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inCollection", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleMutationTypes", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"mutationTypes - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSymbol", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleFullName", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSynonyms", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"nameType - " + ValidationConstants.INVALID_MESSAGE,
					"synonymScope - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.alleleSecondaryIds", is("evidence - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(14)
	public void editAlleleWithNullNonRequiredFieldsLevel1() {
		Allele allele = getAllele(ALLELE);

		allele.setInheritanceMode(null);
		allele.setInCollection(null);
		allele.setReferences(null);
		allele.setIsExtinct(null);
		allele.setDateCreated(null);
		allele.setAlleleMutationTypes(null);
		allele.setAlleleFullName(null);
		allele.setAlleleSynonyms(null);
		allele.setAlleleSecondaryIds(null);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", not(hasKey("inheritanceMode"))).
			body("entity", not(hasKey("inCollection"))).
			body("entity", not(hasKey("references"))).
			body("entity", not(hasKey("isExtinct"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("alleleMutationTypes"))).
			body("entity", not(hasKey("alleleFullName"))).
			body("entity", not(hasKey("alleleSynonyms"))).
			body("entity", not(hasKey("alleleSecondaryIds")));
	}

	@Test
	@Order(15)
	public void editAlleleWithNullNonRequiredFieldsLevel2() {
		Allele allele = getAllele(ALLELE);

		AlleleMutationTypeSlotAnnotation editedMutationType = createAlleleMutationTypeSlotAnnotation(null, List.of(soTerm));
		AlleleSymbolSlotAnnotation editedSymbol = createAlleleSymbolSlotAnnotation(null, "Test symbol", symbolNameType, null, null);
		AlleleFullNameSlotAnnotation editedFullName = createAlleleFullNameSlotAnnotation(null, "Test name", fullNameType, null, null);
		AlleleSynonymSlotAnnotation editedSynonym = createAlleleSynonymSlotAnnotation(null, "Test synonym", systematicNameType, null, null);
		AlleleSecondaryIdSlotAnnotation editedSecondaryId = createAlleleSecondaryIdSlotAnnotation(null, "TEST:Secondary");
		
		allele.setAlleleMutationTypes(List.of(editedMutationType));
		allele.setAlleleSymbol(editedSymbol);
		allele.setAlleleFullName(editedFullName);
		allele.setAlleleSynonyms(List.of(editedSynonym));
		allele.setAlleleSecondaryIds(List.of(editedSecondaryId));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", hasKey("alleleMutationTypes")).
			body("entity", hasKey("alleleSymbol")).
			body("entity", hasKey("alleleFullName")).
			body("entity", hasKey("alleleSynonyms")).
			body("entity", hasKey("alleleSecondaryIds")).
			body("entity.alleleMutationTypes[0]", not(hasKey("evidence"))).
			body("entity.alleleSymbol", not(hasKey("evidence"))).
			body("entity.alleleSymbol", not(hasKey("synonymScope"))).
			body("entity.alleleSymbol", not(hasKey("synonymUrl"))).
			body("entity.alleleFullName", not(hasKey("evidence"))).
			body("entity.alleleFullName", not(hasKey("synonymScope"))).
			body("entity.alleleFullName", not(hasKey("synonymUrl"))).
			body("entity.alleleSynonyms[0]", not(hasKey("evidence"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymScope"))).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymUrl"))).
			body("entity.alleleSecondaryIds[0]", not(hasKey("evidence")));
	}

	@Test
	@Order(16)
	public void deleteAllele() {

		RestAssured.given().
				when().
				delete("/api/allele/" + ALLELE).
				then().
				statusCode(200);
	}
	
	private AlleleMutationTypeSlotAnnotation createAlleleMutationTypeSlotAnnotation (List<InformationContentEntity> evidence, List<SOTerm> mutationTypes) {
		AlleleMutationTypeSlotAnnotation amt = new AlleleMutationTypeSlotAnnotation();
		amt.setEvidence(evidence);
		amt.setMutationTypes(mutationTypes);
		
		return amt;
	}

	private AlleleSymbolSlotAnnotation createAlleleSymbolSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setEvidence(evidence);
		symbol.setDisplayText(name);
		symbol.setFormatText(name);
		symbol.setNameType(nameType);
		symbol.setSynonymScope(synonymScope);
		symbol.setSynonymUrl(synonymUrl);
		
		return symbol;
	}

	private AlleleFullNameSlotAnnotation createAlleleFullNameSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setEvidence(evidence);
		fullName.setDisplayText(name);
		fullName.setFormatText(name);
		fullName.setNameType(nameType);
		fullName.setSynonymScope(synonymScope);
		fullName.setSynonymUrl(synonymUrl);
		
		return fullName;
	}

	private AlleleSynonymSlotAnnotation createAlleleSynonymSlotAnnotation(List<InformationContentEntity> evidence, String name, VocabularyTerm nameType, VocabularyTerm synonymScope, String synonymUrl) {
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setEvidence(evidence);
		synonym.setDisplayText(name);
		synonym.setFormatText(name);
		synonym.setNameType(nameType);
		synonym.setSynonymScope(synonymScope);
		synonym.setSynonymUrl(synonymUrl);
		
		return synonym;
	}

	private AlleleSecondaryIdSlotAnnotation createAlleleSecondaryIdSlotAnnotation(List<InformationContentEntity> evidence, String id) {
		AlleleSecondaryIdSlotAnnotation secondaryId = new AlleleSecondaryIdSlotAnnotation();
		secondaryId.setSecondaryId(id);
		secondaryId.setEvidence(evidence);
		
		return secondaryId;
	}

}
