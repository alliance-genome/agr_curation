package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
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
@Order(8)
public class AlleleITCase {

	private final String ALLELE = "Allele:0001";
	
	private Vocabulary inheritanceModeVocabulary;
	private Vocabulary inCollectionVocabulary;
	private VocabularyTerm inheritanceMode;
	private VocabularyTerm inCollection;
	private Reference reference;
	private List<Reference> references = new ArrayList<Reference>();
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private Person person;
	private OffsetDateTime datetime;
	private SOTerm soTerm;
	private AlleleMutationTypeSlotAnnotation alleleMutationType;
	
	private void createRequiredObjects() {
		inheritanceModeVocabulary = getVocabulary(VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY);
		inCollectionVocabulary = getVocabulary(VocabularyConstants.ALLELE_COLLECTION_VOCABULARY);
		inheritanceMode = getVocabularyTerm(inheritanceModeVocabulary, "dominant");
		inCollection = getVocabularyTerm(inCollectionVocabulary, "Million_mutations_project");
		reference = createReference("AGRKB:000000003");
		soTerm = createSoTerm("SO:00002");
		references.add(reference);
		taxon = getTaxonFromCurie("NCBITaxon:10090");
		taxon2 = getTaxonFromCurie("NCBITaxon:9606");
		person = createPerson("TEST:AllelePerson0001");
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		alleleMutationType = createAlleleMutationTypeSlotAnnotation(List.of(reference), List.of(soTerm));
	}
	
	@Test
	@Order(1)
	public void createValidAllele() {
		
		createRequiredObjects();
		
		Allele allele = new Allele();
		allele.setCurie(ALLELE);
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setCreatedBy(person);
		allele.setIsExtinct(false);
		allele.setDateCreated(datetime);
		allele.setAlleleMutationTypes(List.of(alleleMutationType));
		
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
				body("entity.symbol", is("Allele<sup>test</sup>")).
				body("entity.taxon.curie", is(taxon.getCurie())).
				body("entity.internal", is(false)).
				body("entity.obsolete", is(false)).
				body("entity.name", is("TestAllele")).
				body("entity.inheritanceMode.name", is(inheritanceMode.getName())).
				body("entity.inCollection.name", is(inCollection.getName())).
				body("entity.isExtinct", is(false)).
				body("entity.references[0].curie", is(reference.getCurie())).
				body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
				body("entity.createdBy.uniqueId", is("TEST:AllelePerson0001")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.alleleMutationTypes[0].evidence[0].curie", is(reference.getCurie())).
				body("entity.alleleMutationTypes[0].mutationTypes[0].curie", is(soTerm.getCurie()));
	}

	@Test
	@Order(2)
	public void editAllele() {
		Allele allele = getAllele();
		allele.setSymbol("Allele<sup>edited</sup>");
		allele.setTaxon(taxon2);
		allele.setInternal(true);
		allele.setObsolete(true);
		allele.setIsExtinct(true);

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
				body("entity.symbol", is("Allele<sup>edited</sup>")).
				body("entity.taxon.curie", is(taxon2.getCurie())).
				body("entity.internal", is(true)).
				body("entity.obsolete", is(true)).
				body("entity.isExtinct", is(true));
	}
	
	@Test
	@Order(3)
	public void createAlleleWithMissingCurie() {
		Allele allele = new Allele();
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		
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
	public void createAlleleWithMissingSymbol() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0004");
		allele.setTaxon(taxon);
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.symbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(5)
	public void createAlleleWithMissingTaxon() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0005");
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		
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
	@Order(6)
	public void createAlleleWithInvalidTaxon() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("TEST:invalid");
		nonPersistedTaxon.setName("Invalid");
		
		Allele allele = new Allele();
		allele.setCurie("Allele:0006");
		allele.setTaxon(nonPersistedTaxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);

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
	@Order(7)
	public void createAlleleWithInvalidInheritanceMode() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0007");
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inCollection);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		
	

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
	@Order(8)
	public void createAlleleWithInvalidInCollection() {
		Allele allele = new Allele();
		allele.setCurie("Allele:0008");
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inheritanceMode);
		allele.setReferences(references);
		
	

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
	@Order(9)
	public void createAlleleWithInvalidReference() {
		List<Reference> invalidReferences = new ArrayList<Reference>();
		Reference invalidReference = new Reference();
		invalidReference.setCurie("Invalid");
		invalidReferences.add(invalidReference);
		
		Allele allele = new Allele();
		allele.setCurie("Allele:0010");
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(invalidReferences);
		
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
	@Order(10)
	public void editAlleleWithMissingCurie() {
		Allele allele = getAllele();
		allele.setCurie(null);
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		
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
	@Order(11)
	public void editAlleleWithMissingSymbol() {
		Allele allele = getAllele();
		allele.setTaxon(taxon);
		allele.setName("TestAllele");
		allele.setSymbol(null);
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.symbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(12)
	public void editAlleleWithMissingTaxon() {
		Allele allele = getAllele();
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setTaxon(null);
		
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
	@Order(13)
	public void editAlleleWithInvalidTaxon() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("TEST:invalid");
		nonPersistedTaxon.setName("Invalid");
		
		Allele allele = getAllele();
		allele.setTaxon(nonPersistedTaxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		
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
	@Order(14)
	public void editAlleleWithInvalidInheritanceMode() {
		Allele allele = getAllele();
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inCollection);
		allele.setInCollection(inCollection);
		allele.setReferences(references);

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
	@Order(15)
	public void editAlleleWithInvalidInCollection() {
		Allele allele = getAllele();
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inheritanceMode);
		allele.setReferences(references);

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
	@Order(16)
	public void editAlleleWithInvalidReference() {
		List<Reference> invalidReferences = new ArrayList<Reference>();
		Reference invalidReference = new Reference();
		invalidReference.setCurie("Invalid");
		invalidReferences.add(invalidReference);
		
		Allele allele = getAllele();
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(invalidReferences);
		
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
	@Order(17)
	public void editAlleleWithNullInheritanceMode() {
		Allele allele = getAllele();
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);

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
	@Order(18)
	public void editAlleleWithNullInCollection() {
		Allele allele = getAllele();
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);

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
	@Order(19)
	public void editAlleleWithNullIsExtinct() {
		Allele allele = getAllele();
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);
		allele.setIsExtinct(true);

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
	@Order(20)
	public void editAlleleWithNullReferences() {
		Allele allele = getAllele();
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);

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
	@Order(21)
	public void editAlleleWithNullName() {
		Allele allele = getAllele();
		allele.setTaxon(taxon);
		allele.setSymbol("Allele<sup>test</sup>");
		allele.setName("TestAllele");
		allele.setInheritanceMode(inheritanceMode);
		allele.setInCollection(inCollection);
		allele.setReferences(references);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", hasKey("name"));
		
		allele.setName(null);
		
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200).
			body("entity", not(hasKey("name")));
	}

	@Test
	@Order(22)
	public void deleteAllele() {

		RestAssured.given().
				when().
				delete("/api/allele/" + ALLELE).
				then().
				statusCode(200);
	}

	private Allele getAllele() {
		ObjectResponse<Allele> res = RestAssured.given().
				when().
				get("/api/allele/" + ALLELE).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAllele());

		return res.getEntity();
	}
	
	private NCBITaxonTerm getTaxonFromCurie(String taxonCurie) {
		ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
			when().
			get("/api/ncbitaxonterm/" + taxonCurie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefTaxonTerm());
		
		return response.getEntity();
	}

	private Vocabulary getVocabulary(String name) {
		ObjectResponse<Vocabulary> response = 
			RestAssured.given().
				when().
				get("/api/vocabulary/findBy/" + name).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabulary());
		
		Vocabulary vocabulary = response.getEntity();
		
		return vocabulary;
	}
	
	private VocabularyTerm getVocabularyTerm(Vocabulary vocabulary, String name) {
		ObjectListResponse<VocabularyTerm> response = 
			RestAssured.given().
				when().
				get("/api/vocabulary/" + vocabulary.getId() + "/terms").
				then().
				statusCode(200).
				extract().body().as(getObjectListResponseTypeRefVocabularyTerm());
		
		List<VocabularyTerm> vocabularyTerms = response.getEntities();
		for (VocabularyTerm vocabularyTerm : vocabularyTerms) {
			if (vocabularyTerm.getName().equals(name)) {
				return vocabularyTerm;
			}
		}
		
		return null;
	}
	
	private Reference createReference(String curie) {
		Reference reference = new Reference();
		reference.setCurie(curie);
		
		ObjectResponse<Reference> response = RestAssured.given().
			contentType("application/json").
			body(reference).
			when().
			post("/api/reference").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefReference());
			
		return response.getEntity();
	}
	
	private Person createPerson(String uniqueId) {
		LoggedInPerson person = new LoggedInPerson();
		person.setUniqueId(uniqueId);
		
		ObjectResponse<LoggedInPerson> response = RestAssured.given().
				contentType("application/json").
				body(person).
				when().
				post("/api/loggedinperson").
				then().
				statusCode(200).extract().
				body().as(getObjectResponseTypeRefLoggedInPerson());
		
		person = response.getEntity();
		return (Person) person;
	}
	
	private SOTerm createSoTerm(String curie) {
		SOTerm term = new SOTerm();
		term.setCurie(curie);
		
		ObjectResponse<SOTerm> response = RestAssured.given().
				contentType("application/json").
				body(term).
				when().
				post("/api/soterm").
				then().
				statusCode(200).extract().
				body().as(getObjectResponseTypeRefSOTerm());
		
		return response.getEntity();
	}
	
	private AlleleMutationTypeSlotAnnotation createAlleleMutationTypeSlotAnnotation (List<InformationContentEntity> evidence, List<SOTerm> mutationTypes) {
		AlleleMutationTypeSlotAnnotation amt = new AlleleMutationTypeSlotAnnotation();
		amt.setEvidence(evidence);
		amt.setMutationTypes(mutationTypes);
		
		return amt;
	}

	private TypeRef<ObjectResponse<Allele>> getObjectResponseTypeRefAllele() {
		return new TypeRef<ObjectResponse <Allele>>() { };
	}

	private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRefTaxonTerm() {
		return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
	}

	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}
	
	private TypeRef<ObjectListResponse<VocabularyTerm>> getObjectListResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectListResponse <VocabularyTerm>>() { };
	}

	private TypeRef<ObjectResponse<Reference>> getObjectResponseTypeRefReference() {
		return new TypeRef<ObjectResponse <Reference>>() {
		};
	}

	private TypeRef<ObjectResponse<LoggedInPerson>> getObjectResponseTypeRefLoggedInPerson() {
		return new TypeRef<ObjectResponse <LoggedInPerson>>() {
		};
	}

	private TypeRef<ObjectResponse<SOTerm>> getObjectResponseTypeRefSOTerm() {
		return new TypeRef<ObjectResponse <SOTerm>>() {
		};
	}

}
