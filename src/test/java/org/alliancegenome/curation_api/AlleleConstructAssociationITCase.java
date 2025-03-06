package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
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
@DisplayName("402 - AlleleConstructAssociationITCase")
@Order(402)
public class AlleleConstructAssociationITCase extends BaseITCase {

	private Allele allele;
	private Allele allele2;
	private Allele obsoleteAllele;
	private Construct construct;
	private Construct construct2;
	private Construct obsoleteConstruct;
	private VocabularyTerm relation;
	private VocabularyTerm relation2;
	private VocabularyTerm obsoleteRelation;
	private ECOTerm ecoTerm;
	private ECOTerm ecoTerm2;
	private ECOTerm obsoleteEcoTerm;
	private Reference reference;
	private Reference reference2;
	private Reference obsoleteReference;
	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm obsoleteNoteType;
	private Note note;
	private Note note2;
	
	private final String alleleConstructAssociationGetEndpoint = "/api/alleleconstructassociation/findBy";
	private final String alleleGetEndpoint = "/api/allele/";
	
	private void loadRequiredEntities() {
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		allele = createAllele("ACA:Allele0001", "NCBITaxon:6239", symbolNameType, false);
		allele2 = createAllele("ACA:Allele0002", "NCBITaxon:6239", symbolNameType, false);
		obsoleteAllele = createAllele("ACA:Allele0003", "NCBITaxon:6239", symbolNameType, true);
		construct = createConstruct("ACA:Construct0001", false, symbolNameType);
		construct2 = createConstruct("ACA:Construct0002", false, symbolNameType);
		obsoleteConstruct = createConstruct("ACA:Construct0003", true, symbolNameType);
		Vocabulary relationVocabulary = getVocabulary(VocabularyConstants.ALLELE_RELATION_VOCABULARY);
		relation = getVocabularyTerm(relationVocabulary, "contains");
		relation2 = addVocabularyTermToSet(VocabularyConstants.ALLELE_CONSTRUCT_RELATION_VOCABULARY_TERM_SET, "testRelation", relationVocabulary, false);
		obsoleteRelation = addVocabularyTermToSet(VocabularyConstants.ALLELE_CONSTRUCT_RELATION_VOCABULARY_TERM_SET, "is_obsolete", relationVocabulary, true);
		ecoTerm = createEcoTerm("ECO:ACA0001", "AGA_ECO_test", false, true);
		ecoTerm2 = createEcoTerm("ECO:ACA0002", "AGA_ECO_test2", false, true);
		obsoleteEcoTerm = createEcoTerm("ECO:ACA0003", "AGA_ECO_test3", true, true);
		reference = createReference("AGRKB:ACA0001", false);
		reference2 = createReference("AGRKB:ACA0002", false);
		obsoleteReference = createReference("AGRKB:ACA0003", true);
		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		noteType = getVocabularyTerm(noteTypeVocabulary, "comment");
		noteType2 = getVocabularyTerm(noteTypeVocabulary, "remark");
		obsoleteNoteType = getVocabularyTerm(noteTypeVocabulary, "obsolete_note_type");
		note = createNote(noteType, "ACA Test Note", false, reference);
		note2 = createNote(noteType2, "ACA Test Note 2", false, reference2);
	}
	
	@Test
	@Order(1)
	public void createValidAlleleConstructAssociation() {
		loadRequiredEntities();
		
		AlleleConstructAssociation association = new AlleleConstructAssociation();
		association.setAlleleAssociationSubject(allele);
		association.setAlleleConstructAssociationObject(construct);
		association.setRelation(relation);
		association.setEvidenceCode(ecoTerm);
		association.setInternal(true);
		association.setObsolete(true);
		association.setRelatedNote(note);
		association.setEvidence(List.of(reference));
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/alleleconstructassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(alleleConstructAssociationGetEndpoint + "?alleleId=" + allele.getId() + "&relationName=" + relation.getName() + "&constructId=" + construct.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relation.getName())).
			body("entity.alleleConstructAssociationObject.primaryExternalId", is(construct.getPrimaryExternalId())).
			body("entity.alleleAssociationSubject.primaryExternalId", is(allele.getPrimaryExternalId())).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference.getCurie())).
			body("entity.evidenceCode.curie", is(ecoTerm.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.internal", is(false)).
			body("entity.relatedNote.obsolete", is(false)).
			body("entity.relatedNote.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.freeText", is(note.getFreeText())).
			body("entity.relatedNote.noteType.name", is(noteType.getName())).
			body("entity.relatedNote.references[0].curie", is(reference.getCurie()));

		RestAssured.given().
			when().
			get(alleleGetEndpoint + allele.getPrimaryExternalId()).
			then().
			statusCode(200).
			body("entity.alleleConstructAssociations", hasSize(1)).
			body("entity.alleleConstructAssociations[0].relation.name", is(relation.getName())).
			body("entity.alleleConstructAssociations[0].alleleConstructAssociationObject.primaryExternalId", is(construct.getPrimaryExternalId())).
			body("entity.alleleConstructAssociations[0].alleleAssociationSubject", not(hasKey("alleleConstructAssociations")));
	}
	
	@Test
	@Order(2)
	public void editAlleleConstructAssociation() {
		AlleleConstructAssociation association = getAlleleConstructAssociation(allele.getId(), relation.getName(), construct.getId());
		association.setAlleleAssociationSubject(allele2);
		association.setRelation(relation2);
		association.setAlleleConstructAssociationObject(construct2);
		association.setEvidenceCode(ecoTerm2);
		association.setInternal(false);
		association.setObsolete(false);
		association.setEvidence(List.of(reference2));
		association.setRelatedNote(note2);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/alleleconstructassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(alleleConstructAssociationGetEndpoint + "?alleleId=" + allele2.getId() + "&relationName=" + relation2.getName() + "&constructId=" + construct2.getId()).
			then().
			statusCode(200).
			body("entity.relation.name", is(relation2.getName())).
			body("entity.alleleConstructAssociationObject.primaryExternalId", is(construct2.getPrimaryExternalId())).
			body("entity.alleleAssociationSubject.primaryExternalId", is(allele2.getPrimaryExternalId())).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference2.getCurie())).
			body("entity.evidenceCode.curie", is(ecoTerm2.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.internal", is(false)).
			body("entity.relatedNote.obsolete", is(false)).
			body("entity.relatedNote.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.relatedNote.freeText", is(note2.getFreeText())).
			body("entity.relatedNote.noteType.name", is(noteType2.getName())).
			body("entity.relatedNote.references[0].curie", is(reference2.getCurie()));
	}
	
	@Test
	@Order(3)
	public void createAlleleConstructAssociationWithMissingRequiredFieldsLevel1() {
		AlleleConstructAssociation association = new AlleleConstructAssociation();
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleConstructAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void createAlleleConstructAssociationWithMissingRequiredFieldsLevel2() {
		AlleleConstructAssociation association = new AlleleConstructAssociation();
		association.setAlleleAssociationSubject(allele2);
		association.setRelation(relation);
		association.setAlleleConstructAssociationObject(construct);
		
		Note emptyNote = new Note();
		association.setRelatedNote(emptyNote);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(5)
	public void createAlleleConstructAssociationWithEmptyRequiredFieldsLevel2() {
		AlleleConstructAssociation association = new AlleleConstructAssociation();
		association.setAlleleAssociationSubject(allele2);
		association.setRelation(relation);
		association.setAlleleConstructAssociationObject(construct);
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(noteType);
		association.setRelatedNote(invalidNote);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNote", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void editAlleleConstructAssociationWithMissingRequiredFieldsLevel1() {
		AlleleConstructAssociation association = getAlleleConstructAssociation(allele2.getId(), relation2.getName(), construct2.getId());
		association.setAlleleAssociationSubject(null);
		association.setRelation(null);
		association.setAlleleConstructAssociationObject(null);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(3))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.alleleConstructAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(7)
	public void editAlleleConstructAssociationWithMissingRequiredFieldsLevel2() {
		AlleleConstructAssociation association = getAlleleConstructAssociation(allele2.getId(), relation2.getName(), construct2.getId());
	
		Note invalidNote = association.getRelatedNote();
		invalidNote.setFreeText(null);
		invalidNote.setNoteType(null);
		association.setRelatedNote(invalidNote);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE))));
	}
	
	@Test
	@Order(8)
	public void editAlleleConstructAssociationWithEmptyRequiredFieldsLevel2() {
		AlleleConstructAssociation association = getAlleleConstructAssociation(allele2.getId(), relation2.getName(), construct2.getId());
		
		Note invalidNote = association.getRelatedNote();
		invalidNote.setFreeText("");
		association.setRelatedNote(invalidNote);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNote", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void createAlleleConstructAssociationWithInvalidFields() {
		Allele nonPersistedAllele = new Allele();
		nonPersistedAllele.setPrimaryExternalId("NP:Allele01");
		Construct nonPersistedConstruct = new Construct();
		nonPersistedConstruct.setPrimaryExternalId("NP:Construct01");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		ECOTerm nonPersistedEcoTerm = new ECOTerm();
		nonPersistedEcoTerm.setCurie("ECO:Invalid");
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(relation);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("Invalid");
		
		AlleleConstructAssociation association = new AlleleConstructAssociation();
		association.setAlleleAssociationSubject(nonPersistedAllele);
		association.setRelation(noteType);
		association.setAlleleConstructAssociationObject(nonPersistedConstruct);
		association.setEvidence(List.of(nonPersistedReference));
		association.setRelatedNote(invalidNote);
		association.setEvidenceCode(nonPersistedEcoTerm);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleConstructAssociationObject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCode", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(9)
	public void editAlleleConstructAssociationWithInvalidFields() {
		Allele nonPersistedAllele = new Allele();
		nonPersistedAllele.setPrimaryExternalId("NP:Allele01");
		Construct nonPersistedConstruct = new Construct();
		nonPersistedConstruct.setPrimaryExternalId("NP:Construct01");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		ECOTerm nonPersistedEcoTerm = new ECOTerm();
		nonPersistedEcoTerm.setCurie("ECO:Invalid");
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(relation);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("Invalid");
		
		AlleleConstructAssociation association = getAlleleConstructAssociation(allele2.getId(), relation2.getName(), construct2.getId());
		association.setAlleleAssociationSubject(nonPersistedAllele);
		association.setRelation(noteType);
		association.setAlleleConstructAssociationObject(nonPersistedConstruct);
		association.setEvidence(List.of(nonPersistedReference));
		association.setRelatedNote(invalidNote);
		association.setEvidenceCode(nonPersistedEcoTerm);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.alleleConstructAssociationObject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCode", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(10)
	public void createAlleleConstructAssociationWithObsoleteFields() {
		AlleleConstructAssociation association = new AlleleConstructAssociation();
		
		association.setAlleleAssociationSubject(obsoleteAllele);
		association.setRelation(obsoleteRelation);
		association.setAlleleConstructAssociationObject(obsoleteConstruct);
		association.setEvidenceCode(obsoleteEcoTerm);
		association.setEvidence(List.of(obsoleteReference));
		
		Note note = new Note();
		note.setFreeText("Obsolete note");
		note.setNoteType(obsoleteNoteType);
		note.setReferences(List.of(obsoleteReference));
		
		association.setRelatedNote(note);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			post("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.alleleConstructAssociationObject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCode", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(11)
	public void editAlleleConstructAssociationWithObsoleteFields() {
		AlleleConstructAssociation association = getAlleleConstructAssociation(allele2.getId(), relation2.getName(), construct2.getId());
		
		association.setAlleleAssociationSubject(obsoleteAllele);
		association.setRelation(obsoleteRelation);
		association.setAlleleConstructAssociationObject(obsoleteConstruct);
		association.setEvidenceCode(obsoleteEcoTerm);
		association.setEvidence(List.of(obsoleteReference));
		
		Note note = association.getRelatedNote();
		note.setNoteType(obsoleteNoteType);
		note.setReferences(List.of(obsoleteReference));
		
		association.setRelatedNote(note);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/alleleconstructassociation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.alleleAssociationSubject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.alleleConstructAssociationObject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidence", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCode", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.relatedNote", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(12)
	public void editAlleleConstructAssociationWithNullNonRequiredFieldsLevel2() {
		AlleleConstructAssociation association = getAlleleConstructAssociation(allele2.getId(), relation2.getName(), construct2.getId());
		
		Note editedNote = association.getRelatedNote();
		editedNote.setReferences(null);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/alleleconstructassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(alleleConstructAssociationGetEndpoint + "?alleleId=" + allele2.getId() + "&relationName=" + relation2.getName() + "&constructId=" + construct2.getId()).
			then().
			statusCode(200).
			body("entity", hasKey("relatedNote")).
			body("entity.relatedNote", not(hasKey("references")));
	}
	
	@Test
	@Order(13)
	public void editAlleleConstructAssociationWithNullNonRequiredFieldsLevel1() {
		AlleleConstructAssociation association = getAlleleConstructAssociation(allele2.getId(), relation2.getName(), construct2.getId());
		
		association.setEvidence(null);
		association.setEvidenceCode(null);
		association.setRelatedNote(null);
		
		RestAssured.given().
			contentType("application/json").
			body(association).
			when().
			put("/api/alleleconstructassociation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get(alleleConstructAssociationGetEndpoint + "?alleleId=" + allele2.getId() + "&relationName=" + relation2.getName() + "&constructId=" + construct2.getId()).
			then().
			statusCode(200).
			body("entity", not(hasKey("evidence"))).
			body("entity", not(hasKey("evidenceCode"))).
			body("entity", not(hasKey("relatedNote")));
	}
}
