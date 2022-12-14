package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
	private VocabularyTerm inheritanceMode;
	private VocabularyTerm inCollection;
	private VocabularyTerm symbolNameType;
	private VocabularyTerm fullNameType;
	private VocabularyTerm systematicNameType;
	private VocabularyTerm exactSynonymScope;
	private VocabularyTerm broadSynonymScope;
	private Reference reference;
	private List<Reference> references = new ArrayList<Reference>();
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private Person person;
	private OffsetDateTime datetime;
	private SOTerm soTerm;
	private SOTerm soTerm2;
	private AlleleMutationTypeSlotAnnotation alleleMutationType;
	private AlleleSymbolSlotAnnotation alleleSymbol;
	private AlleleFullNameSlotAnnotation alleleFullName;
	private AlleleSynonymSlotAnnotation alleleSynonym;
	private AlleleSecondaryIdSlotAnnotation alleleSecondaryId;
	
	private void createRequiredObjects() {
		inheritanceModeVocabulary = getVocabulary(VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY);
		inCollectionVocabulary = getVocabulary(VocabularyConstants.ALLELE_COLLECTION_VOCABULARY);
		nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		synonymScopeVocabulary = getVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY);
		inheritanceMode = getVocabularyTerm(inheritanceModeVocabulary, "dominant");
		inCollection = getVocabularyTerm(inCollectionVocabulary, "Million_mutations_project");
		symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		fullNameType = getVocabularyTerm(nameTypeVocabulary, "full_name");
		systematicNameType = getVocabularyTerm(nameTypeVocabulary, "systematic_name");
		exactSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "exact");
		broadSynonymScope = getVocabularyTerm(synonymScopeVocabulary, "broad");
		reference = createReference("AGRKB:000000003", false);
		soTerm = createSoTerm("SO:00002");
		soTerm2 = createSoTerm("SO:00003");
		references.add(reference);
		taxon = getNCBITaxonTerm("NCBITaxon:10090");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		person = createPerson("TEST:AllelePerson0001");
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		alleleMutationType = createAlleleMutationTypeSlotAnnotation(reference, soTerm);
		alleleSymbol = createAlleleSymbolSlotAnnotation("Test symbol");
		alleleFullName = createAlleleFullNameSlotAnnotation("Test name");
		alleleSynonym = createAlleleSynonymSlotAnnotation("Test synonym");
		alleleSecondaryId = createAlleleSecondaryIdSlotAnnotation("TEST:Secondary");
	}
	
	@Test
	@Order(1)
	public void createValidAllele() {
		
		createRequiredObjects();
		
		Allele allele = new Allele();
		allele.setCurie(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setCreatedBy(person);
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
				body("entity.inheritanceMode.name", is(inheritanceMode.getName())).
				body("entity.inCollection.name", is(inCollection.getName())).
				body("entity.isExtinct", is(false)).
				body("entity.references[0].curie", is(reference.getCurie())).
				body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
				body("entity.createdBy.uniqueId", is("TEST:AllelePerson0001")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.alleleMutationTypes[0].evidence[0].curie", is(reference.getCurie())).
				body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(soTerm.getCurie())).
				body("entity.alleleSymbol.displayText", is("Test symbol")).
				body("entity.alleleSymbol.formatText", is("Test symbol")).
				body("entity.alleleSymbol.nameType.name", is(symbolNameType.getName())).
				body("entity.alleleSymbol.synonymScope.name", is(exactSynonymScope.getName())).
				body("entity.alleleSymbol.synonymUrl", is("https://test.org")).
				body("entity.alleleFullName.displayText", is("Test name")).
				body("entity.alleleFullName.formatText", is("Test name")).
				body("entity.alleleFullName.nameType.name", is(fullNameType.getName())).
				body("entity.alleleFullName.synonymScope.name", is(exactSynonymScope.getName())).
				body("entity.alleleFullName.synonymUrl", is("https://test.org")).
				body("entity.alleleSynonyms[0].displayText", is("Test synonym")).
				body("entity.alleleSynonyms[0].formatText", is("Test synonym")).
				body("entity.alleleSynonyms[0].nameType.name", is(symbolNameType.getName())).
				body("entity.alleleSynonyms[0].synonymScope.name", is(exactSynonymScope.getName())).
				body("entity.alleleSynonyms[0].synonymUrl", is("https://test.org")).
				body("entity.alleleSecondaryIds[0].secondaryId", is("TEST:Secondary"));
	}

	@Test
	@Order(2)
	public void editAllele() {
		Allele allele = getAllele(ALLELE);
		AlleleMutationTypeSlotAnnotation alleleMutationType2 = createAlleleMutationTypeSlotAnnotation(reference, soTerm2);
		
		allele.setTaxon(taxon2);
		allele.setInternal(true);
		allele.setObsolete(true);
		allele.setIsExtinct(true);
		allele.setAlleleMutationTypes(List.of(alleleMutationType2));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSymbolSlotAnnotation newSymbol = allele.getAlleleSymbol();
		newSymbol.setDisplayText("EditedDisplay");
		newSymbol.setFormatText("EditedFormat");
		newSymbol.setNameType(systematicNameType);
		newSymbol.setSynonymScope(broadSynonymScope);
		newSymbol.setSynonymUrl("https://test2.org");
		allele.setAlleleSymbol(newSymbol);
		
		AlleleFullNameSlotAnnotation newName = allele.getAlleleFullName();
		newName.setDisplayText("EditedDisplay");
		newName.setFormatText("EditedFormat");
		newName.setSynonymScope(broadSynonymScope);
		newName.setSynonymUrl("https://test2.org");
		allele.setAlleleFullName(newName);
		
		AlleleSynonymSlotAnnotation newSynonym = allele.getAlleleSynonyms().get(0);
		newSynonym.setDisplayText("EditedDisplay");
		newSynonym.setFormatText("EditedFormat");
		newSynonym.setNameType(fullNameType);
		newSynonym.setSynonymScope(broadSynonymScope);
		newSynonym.setSynonymUrl("https://test2.org");
		allele.setAlleleSynonyms(List.of(newSynonym));
		
		AlleleSecondaryIdSlotAnnotation newSecondaryId = allele.getAlleleSecondaryIds().get(0);
		newSecondaryId.setSecondaryId("TEST:Secondary2");
		allele.setAlleleSecondaryIds(List.of(newSecondaryId));
		
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
				body("entity.taxon.curie", is(taxon2.getCurie())).
				body("entity.internal", is(true)).
				body("entity.obsolete", is(true)).
				body("entity.isExtinct", is(true)).
				body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(soTerm2.getCurie())).
				body("entity.alleleSymbol.displayText", is("EditedDisplay")).
				body("entity.alleleSymbol.formatText", is("EditedFormat")).
				body("entity.alleleSymbol.nameType.name", is(systematicNameType.getName())).
				body("entity.alleleSymbol.synonymScope.name", is(broadSynonymScope.getName())).
				body("entity.alleleSymbol.synonymUrl", is("https://test2.org")).
				body("entity.alleleFullName.displayText", is("EditedDisplay")).
				body("entity.alleleFullName.formatText", is("EditedFormat")).
				body("entity.alleleFullName.nameType.name", is(fullNameType.getName())).
				body("entity.alleleFullName.synonymScope.name", is(broadSynonymScope.getName())).
				body("entity.alleleFullName.synonymUrl", is("https://test2.org")).
				body("entity.alleleSynonyms[0].displayText", is("EditedDisplay")).
				body("entity.alleleSynonyms[0].formatText", is("EditedFormat")).
				body("entity.alleleSynonyms[0].nameType.name", is(fullNameType.getName())).
				body("entity.alleleSynonyms[0].synonymScope.name", is(broadSynonymScope.getName())).
				body("entity.alleleSynonyms[0].synonymUrl", is("https://test2.org")).
				body("entity.alleleSecondaryIds[0].secondaryId", is("TEST:Secondary2"));
	}
	
	@Test
	@Order(3)
	public void createAlleleWithMissingCurie() {
		Allele allele = new Allele();
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
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
	@Order(4)
	public void createAlleleWithMissingTaxon() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0005");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(5)
	public void createAlleleWithInvalidTaxon() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("TEST:invalid");
		nonPersistedTaxon.setName("Invalid");
		
		Allele allele = new Allele();
		allele.setCurie("Allele:0006");
		allele.setTaxon(nonPersistedTaxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(6)
	public void createAlleleWithInvalidInheritanceMode() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0007");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inCollection);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
	

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.inheritanceMode", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(7)
	public void createAlleleWithInvalidInCollection() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0008");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inheritanceMode);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
	

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.inCollection", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(8)
	public void createAlleleWithInvalidReference() {
		List<Reference> invalidReferences = new ArrayList<Reference>();
		Reference invalidReference = new Reference();
		invalidReference.setCurie("Invalid");
		invalidReferences.add(invalidReference);
		
		Allele allele = new Allele();
		allele.setCurie("Allele:0010");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(invalidReferences);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void editAlleleWithMissingCurie() {
		Allele allele = getAllele(ALLELE);
		allele.setCurie(null);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
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
	@Order(10)
	public void editAlleleWithMissingTaxon() {
		Allele allele = getAllele(ALLELE);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setTaxon(null);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.taxon", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(11)
	public void editAlleleWithInvalidTaxon() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("TEST:invalid");
		nonPersistedTaxon.setName("Invalid");
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(nonPersistedTaxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(12)
	public void editAlleleWithInvalidInheritanceMode() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inCollection);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.inheritanceMode", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(13)
	public void editAlleleWithInvalidInCollection() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inheritanceMode);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.inCollection", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(14)
	public void editAlleleWithInvalidReference() {
		List<Reference> invalidReferences = new ArrayList<Reference>();
		Reference invalidReference = new Reference();
		invalidReference.setCurie("Invalid");
		invalidReferences.add(invalidReference);
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(invalidReferences);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(15)
	public void editAlleleWithNullInheritanceMode() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", hasKey("inheritanceMode"));
		
		allele.setInheritanceMode(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", not(hasKey("inheritanceMode")));
	}

	@Test
	@Order(16)
	public void editAlleleWithNullInCollection() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", hasKey("inCollection"));
		
		allele.setInCollection(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", not(hasKey("inCollection")));
	}

	@Test
	@Order(17)
	public void editAlleleWithNullIsExtinct() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setIsExtinct(true);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", hasKey("isExtinct"));
		
		allele.setIsExtinct(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", not(hasKey("isExtinct")));
	}

	@Test
	@Order(18)
	public void editAlleleWithNullReferences() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", hasKey("references"));
		
		allele.setReferences(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", not(hasKey("references")));
	}

	@Test
	@Order(19)
	public void createAlleleWithMissingAlleleMutationTypeMutationTypes() {
		Allele allele = new Allele();
		
		AlleleMutationTypeSlotAnnotation invalidAlleleMutationType = new AlleleMutationTypeSlotAnnotation();
		invalidAlleleMutationType.setEvidence(List.of(reference));
		invalidAlleleMutationType.setMutationTypes(null);

		allele.setCurie("Allele:0022");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(invalidAlleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleMutationTypes", is("mutationTypes - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(20)
	public void createAlleleWithInvalidAlleleMutationTypeMutationTypes() {
		Allele allele = new Allele();
		
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:00004");
		AlleleMutationTypeSlotAnnotation invalidAlleleMutationType = createAlleleMutationTypeSlotAnnotation(reference, nonPersistedSoTerm);
		
		allele.setCurie("Allele:0023");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(invalidAlleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleMutationTypes", is("mutationTypes - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(21)
	public void createAlleleWithInvalidAlleleMutationTypeEvidence() {
		Allele allele = new Allele();
		
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("PMID:00004");
		AlleleMutationTypeSlotAnnotation invalidAlleleMutationType = createAlleleMutationTypeSlotAnnotation(nonPersistedReference, soTerm);
		
		allele.setCurie("Allele:0024");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(invalidAlleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleMutationTypes", is("evidence - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(22)
	public void editAlleleWithMissingAlleleMutationTypeMutationTypes() {
		Allele allele = getAllele(ALLELE);
		
		AlleleMutationTypeSlotAnnotation invalidAlleleMutationType = new AlleleMutationTypeSlotAnnotation();
		invalidAlleleMutationType.setEvidence(List.of(reference));
		invalidAlleleMutationType.setMutationTypes(null);
		
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(invalidAlleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleMutationTypes", is("mutationTypes - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(23)
	public void editAlleleWithInvalidAlleleMutationTypeMutationTypes() {
		Allele allele = getAllele(ALLELE);
		
		SOTerm nonPersistedSoTerm = new SOTerm();
		nonPersistedSoTerm.setCurie("SO:00004");
		AlleleMutationTypeSlotAnnotation invalidAlleleMutationType = createAlleleMutationTypeSlotAnnotation(reference, nonPersistedSoTerm);
		
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(invalidAlleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleMutationTypes", is("mutationTypes - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(24)
	public void editAlleleWithInvalidAlleleMutationTypeEvidence() {
		Allele allele = getAllele(ALLELE);
		
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("PMID:00004");
		AlleleMutationTypeSlotAnnotation invalidAlleleMutationType = createAlleleMutationTypeSlotAnnotation(nonPersistedReference, soTerm);
		
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(invalidAlleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleMutationTypes", is("evidence - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(25)
	public void editAlleleWithNullAlleleMutationTypeEvidence() {
		Allele allele = getAllele(ALLELE);
		AlleleMutationTypeSlotAnnotation noEvidenceAlleleMutationType = new AlleleMutationTypeSlotAnnotation();
		noEvidenceAlleleMutationType.setMutationTypes(List.of(soTerm));
		
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

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
			body("entity.alleleMutationTypes[0]", hasKey("evidence"));

		allele.setAlleleMutationTypes(List.of(noEvidenceAlleleMutationType));
		
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
			body("entity.alleleMutationTypes[0]", not(hasKey("evidence")));
	}
	
	@Test
	@Order(26)
	public void createAlleleWithMissingAlleleSymbol() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0026");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(27)
	public void createAlleleWithMissingAlleleSymbolDisplayText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0027");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setFormatText("MissingDisplayText");
		symbol.setSynonymScope(exactSynonymScope);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(28)
	public void createAlleleWithEmptyAlleleSymbolDisplayText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0028");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText("");
		symbol.setFormatText("MissingDisplayText");
		symbol.setSynonymScope(exactSynonymScope);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(29)
	public void createAlleleWithMissingAlleleSymbolFormatText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0029");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText("MissingDisplayText");
		symbol.setSynonymScope(exactSynonymScope);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(30)
	public void createAlleleWithEmptyAlleleSymbolFormatText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0030");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText("MissingFormatText");
		symbol.setFormatText("");
		symbol.setSynonymScope(exactSynonymScope);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(31)
	public void createAlleleWithMissingAlleleSymbolNameType() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0031");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setNameType(null);
		symbol.setDisplayText("MissingNameType");
		symbol.setFormatText("MissingNameType");
		symbol.setSynonymScope(exactSynonymScope);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(32)
	public void createAlleleWithInvalidSymbolNameType() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0032");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setNameType(fullNameType);
		symbol.setDisplayText("InvalidNameType");
		symbol.setFormatText("InvalidNameType");
		symbol.setSynonymScope(exactSynonymScope);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(33)
	public void createAlleleWithInvalidSymbolSynomyScope() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0033");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText("InvalidSynonymScope");
		symbol.setFormatText("InvalidSynonymScope");
		symbol.setSynonymScope(symbolNameType);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(34)
	public void editAlleleWithMissingAlleleSymbol() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(35)
	public void editAlleleWithMissingAlleleSymbolDisplayText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = allele.getAlleleSymbol();
		symbol.setDisplayText(null);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(36)
	public void editAlleleWithEmptyAlleleSymbolDisplayText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = allele.getAlleleSymbol();
		symbol.setDisplayText("");
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(37)
	public void editAlleleWithMissingAlleleSymbolFormatText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = allele.getAlleleSymbol();
		symbol.setFormatText(null);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(38)
	public void editAlleleWithEmptyAlleleSymbolFormatText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = allele.getAlleleSymbol();
		symbol.setFormatText("");
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(39)
	public void editAlleleWithMissingAlleleSymbolNameType() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = allele.getAlleleSymbol();
		symbol.setNameType(null);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(40)
	public void editAlleleWithInvalidAlleleSymbolNameType() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = allele.getAlleleSymbol();
		symbol.setNameType(fullNameType);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(41)
	public void editAlleleWithInvalidAlleleSymbolSynonymScope() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
		AlleleSymbolSlotAnnotation symbol = allele.getAlleleSymbol();
		symbol.setSynonymScope(symbolNameType);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSymbol", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(42)
	public void editAlleleWithNullAlleleSymbolSynonymScope() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity.alleleSymbol", hasKey("synonymScope"));
		
		AlleleSymbolSlotAnnotation symbol = allele.getAlleleSymbol();
		symbol.setSynonymScope(null);
		allele.setAlleleSymbol(symbol);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity.alleleSymbol", not(hasKey("synonymScope")));
	}
	
	@Test
	@Order(43)
	public void createAlleleWithMissingAlleleFullNameDisplayText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0043");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setFormatText("MissingDisplayText");
		fullName.setSynonymScope(exactSynonymScope);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(44)
	public void createAlleleWithEmptyAlleleFullNameDisplayText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0044");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setDisplayText("");
		fullName.setFormatText("MissingDisplayText");
		fullName.setSynonymScope(exactSynonymScope);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(45)
	public void createAlleleWithMissingAlleleFullNameFormatText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0045");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setDisplayText("MissingFormatText");
		fullName.setSynonymScope(exactSynonymScope);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(46)
	public void createAlleleWithEmptyAlleleFullNameFormatText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0046");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setDisplayText("MissingFormatText");
		fullName.setFormatText("");
		fullName.setSynonymScope(exactSynonymScope);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(47)
	public void createAlleleWithMissingAlleleFullNameNameType() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0047");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setDisplayText("MissingNameType");
		fullName.setFormatText("MissingNameType");
		fullName.setSynonymScope(exactSynonymScope);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(48)
	public void createAlleleWithInvalidFullNameNameType() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0048");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setDisplayText("MissingNameType");
		fullName.setFormatText("MissingNameType");
		fullName.setNameType(symbolNameType);
		fullName.setSynonymScope(exactSynonymScope);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(49)
	public void createAlleleWithInvalidFullNameSynomymScope() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0049");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setNameType(fullNameType);
		fullName.setDisplayText("MissingNameType");
		fullName.setFormatText("MissingNameType");
		fullName.setSynonymScope(symbolNameType);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(50)
	public void editAlleleWithMissingAlleleFullNameDisplayText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = allele.getAlleleFullName();
		fullName.setDisplayText(null);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(51)
	public void editAlleleWithEmptyAlleleFullNameDisplayText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = allele.getAlleleFullName();
		fullName.setDisplayText("");
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(52)
	public void editAlleleWithMissingAlleleFullNameFormatText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = allele.getAlleleFullName();
		fullName.setFormatText(null);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(53)
	public void editAlleleWithEmptyAlleleFullNameFormatText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = allele.getAlleleFullName();
		fullName.setFormatText("");
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(54)
	public void editAlleleWithMissingAlleleFullNameNameType() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = allele.getAlleleFullName();
		fullName.setNameType(null);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(55)
	public void editAlleleWithInvalidAlleleFullNameNameType() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = allele.getAlleleFullName();
		fullName.setNameType(symbolNameType);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(56)
	public void editAlleleWithInvalidAlleleFullNameSynonymScope() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleFullNameSlotAnnotation fullName = allele.getAlleleFullName();
		fullName.setSynonymScope(fullNameType);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleFullName", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(57)
	public void editAlleleWithNullAlleleFullNameSynonymScope() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		allele.setAlleleFullName(alleleFullName);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity.alleleFullName", hasKey("synonymScope"));
		
		AlleleFullNameSlotAnnotation fullName = allele.getAlleleFullName();
		fullName.setSynonymScope(null);
		allele.setAlleleFullName(fullName);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity.alleleFullName", not(hasKey("synonymScope")));
	}
	
	@Test
	@Order(58)
	public void createAlleleWithMissingAlleleSynonymDisplayText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0058");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setFormatText("MissingDisplayText");
		synonym.setSynonymScope(exactSynonymScope);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(59)
	public void createAlleleWithEmptyAlleleSynonymDisplayText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0059");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setDisplayText("");
		synonym.setFormatText("EmptyDisplayText");
		synonym.setSynonymScope(exactSynonymScope);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(60)
	public void createAlleleWithMissingAlleleSynonymFormatText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0060");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setDisplayText("MissingFormatText");
		synonym.setSynonymScope(exactSynonymScope);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(61)
	public void createAlleleWithEmptyAlleleSynonymFormatText() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0061");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setDisplayText("EmptyFormatText");
		synonym.setFormatText("");
		synonym.setSynonymScope(exactSynonymScope);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(62)
	public void createAlleleWithMissingAlleleSynonymNameType() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0062");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setDisplayText("MissingNameType");
		synonym.setFormatText("MissingNameType");
		synonym.setSynonymScope(exactSynonymScope);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(63)
	public void createAlleleWithInvalidSynonymNameType() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0063");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setNameType(exactSynonymScope);
		synonym.setDisplayText("InvalidNameType");
		synonym.setFormatText("InvalidNameType");
		synonym.setSynonymScope(exactSynonymScope);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(64)
	public void createAlleleWithInvalidSynonymSynomymScope() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0064");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setNameType(fullNameType);
		synonym.setDisplayText("InvalidSynonymScope");
		synonym.setFormatText("InvalidSynonymScope");
		synonym.setSynonymScope(fullNameType);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(65)
	public void editAlleleWithMissingAlleleSynonymDisplayText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = allele.getAlleleSynonyms().get(0);
		synonym.setDisplayText(null);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(66)
	public void editAlleleWithEmptyAlleleSynonymDisplayText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = allele.getAlleleSynonyms().get(0);
		synonym.setDisplayText("");
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("displayText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(67)
	public void editAlleleWithMissingAlleleSynonymFormatText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = allele.getAlleleSynonyms().get(0);
		synonym.setFormatText(null);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(68)
	public void editAlleleWithEmptyAlleleSynonymFormatText() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = allele.getAlleleSynonyms().get(0);
		synonym.setFormatText("");
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("formatText - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(69)
	public void editAlleleWithMissingAlleleSynonymNameType() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = allele.getAlleleSynonyms().get(0);
		synonym.setNameType(null);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("nameType - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(70)
	public void editAlleleWithInvalidAlleleSynonymNameType() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = allele.getAlleleSynonyms().get(0);
		synonym.setNameType(inCollection);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("nameType - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(71)
	public void editAlleleWithInvalidAlleleSynonymSynonymScope() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSynonymSlotAnnotation synonym = allele.getAlleleSynonyms().get(0);
		synonym.setSynonymScope(inCollection);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSynonyms", is("synonymScope - " + ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(72)
	public void editAlleleWithNullAlleleSynonymSynonymScope() {
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		allele.setAlleleSymbol(alleleSymbol);
		allele.setAlleleFullName(alleleFullName);
		allele.setAlleleSynonyms(List.of(alleleSynonym));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity.alleleSynonyms[0]", hasKey("synonymScope"));
		
		AlleleSynonymSlotAnnotation synonym = allele.getAlleleSynonyms().get(0);
		synonym.setSynonymScope(null);
		allele.setAlleleSynonyms(List.of(synonym));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity.alleleSynonyms[0]", not(hasKey("synonymScope")));
	}

	@Test
	@Order(73)
	public void createAlleleWithMissingAlleleSecondaryIdSecondaryId() {
		Allele allele = new Allele();
		
		AlleleSecondaryIdSlotAnnotation invalidAlleleSecondaryId = new AlleleSecondaryIdSlotAnnotation();
		invalidAlleleSecondaryId.setEvidence(List.of(reference));

		allele.setCurie("Allele:0073");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleSymbol(alleleSymbol);
		allele.setAlleleSecondaryIds(List.of(invalidAlleleSecondaryId));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(74)
	public void createAlleleWithEmptyAlleleSecondaryIdSecondaryId() {
		Allele allele = new Allele();
		
		AlleleSecondaryIdSlotAnnotation invalidAlleleSecondaryId = new AlleleSecondaryIdSlotAnnotation();
		invalidAlleleSecondaryId.setEvidence(List.of(reference));
		invalidAlleleSecondaryId.setSecondaryId("");
		
		allele.setCurie("Allele:0073");
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleSymbol(alleleSymbol);
		allele.setAlleleSecondaryIds(List.of(invalidAlleleSecondaryId));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(75)
	public void editAlleleWithNullAlleleSecondaryIdSecondaryId() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSecondaryIdSlotAnnotation secondaryId = allele.getAlleleSecondaryIds().get(0);
		secondaryId.setSecondaryId(null);
		allele.setAlleleSecondaryIds(List.of(secondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(76)
	public void editAlleleWithMissingAlleleSecondaryIdSecondaryId() {
		
		Allele allele = getAllele(ALLELE);
		allele.setTaxon(taxon);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setAlleleSymbol(alleleSymbol);
		
		AlleleSecondaryIdSlotAnnotation secondaryId = allele.getAlleleSecondaryIds().get(0);
		secondaryId.setSecondaryId("");
		allele.setAlleleSecondaryIds(List.of(secondaryId));
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.alleleSecondaryIds", is("secondaryId - " + ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(77)
	public void deleteAllele() {

		RestAssured.given().
				when().
				delete("/api/allele/" + ALLELE).
				then().
				statusCode(200);
	}
	
	private AlleleMutationTypeSlotAnnotation createAlleleMutationTypeSlotAnnotation (InformationContentEntity evidence, SOTerm mutationType) {
		AlleleMutationTypeSlotAnnotation amt = new AlleleMutationTypeSlotAnnotation();
		if (evidence != null)
			amt.setEvidence(List.of(evidence));
		if (mutationType != null)
			amt.setMutationTypes(List.of(mutationType));
		
		return amt;
	}

	private AlleleSymbolSlotAnnotation createAlleleSymbolSlotAnnotation(String name) {
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setDisplayText(name);
		symbol.setFormatText(name);
		symbol.setNameType(symbolNameType);
		symbol.setSynonymScope(exactSynonymScope);
		symbol.setSynonymUrl("https://test.org");
		
		return symbol;
	}

	private AlleleFullNameSlotAnnotation createAlleleFullNameSlotAnnotation(String name) {
		AlleleFullNameSlotAnnotation fullName = new AlleleFullNameSlotAnnotation();
		fullName.setDisplayText(name);
		fullName.setFormatText(name);
		fullName.setNameType(fullNameType);
		fullName.setSynonymScope(exactSynonymScope);
		fullName.setSynonymUrl("https://test.org");
		
		return fullName;
	}

	private AlleleSynonymSlotAnnotation createAlleleSynonymSlotAnnotation(String name) {
		AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
		synonym.setDisplayText(name);
		synonym.setFormatText(name);
		synonym.setNameType(symbolNameType);
		synonym.setSynonymScope(exactSynonymScope);
		synonym.setSynonymUrl("https://test.org");
		
		return synonym;
	}

	private AlleleSecondaryIdSlotAnnotation createAlleleSecondaryIdSlotAnnotation(String id) {
		AlleleSecondaryIdSlotAnnotation secondaryId = new AlleleSecondaryIdSlotAnnotation();
		secondaryId.setSecondaryId(id);
		
		return secondaryId;
	}

}
