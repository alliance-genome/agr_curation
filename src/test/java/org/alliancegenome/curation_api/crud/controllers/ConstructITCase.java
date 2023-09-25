package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
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
@Order(18)
public class ConstructITCase extends BaseITCase {

	private final String CONSTRUCT = "Construct:0001";
	
	private Vocabulary noteTypeVocabulary;
	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm obsoleteNoteType;
	private Vocabulary alleleNoteTypeVocabulary;
	private VocabularyTerm alleleNoteType;
	private Reference reference;
	private Reference reference2;
	private Reference obsoleteReference;
	private NCBITaxonTerm taxon;
	private NCBITaxonTerm taxon2;
	private NCBITaxonTerm obsoleteTaxon;
	private Note relatedNote;
	private Note relatedNote2;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private DataProvider dataProvider;
	private DataProvider dataProvider2;
	private DataProvider obsoleteDataProvider;
	private Person person;
	private ConstructComponentSlotAnnotation constructComponent;
	
	private void loadRequiredEntities() {
		noteTypeVocabulary = getVocabulary(VocabularyConstants.CONSTRUCT_COMPONENT_NOTE_TYPES_VOCABULARY);
		alleleNoteTypeVocabulary = getVocabulary(VocabularyConstants.ALLELE_NOTE_TYPES_VOCABULARY);
		noteType = getVocabularyTerm(noteTypeVocabulary, "test_construct_component_note");
		noteType2 = getVocabularyTerm(noteTypeVocabulary, "test_construct_component_summary");
		obsoleteNoteType = createVocabularyTerm(noteTypeVocabulary, "obsolete_type", true);
		alleleNoteType = getVocabularyTerm(alleleNoteTypeVocabulary, "notes_on_origin");
		taxon = getNCBITaxonTerm("NCBITaxon:6239");
		taxon2 = getNCBITaxonTerm("NCBITaxon:9606");
		obsoleteTaxon = getNCBITaxonTerm("NCBITaxon:0000");
		reference = createReference("AGRKB:000020003", false);
		reference2 = createReference("AGRKB:000020005", false);
		relatedNote = createNote(noteType, "Test text", false, reference);
		relatedNote2 = createNote(noteType2, "Test text 2", false, reference2);
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		obsoleteReference = createReference("AGRKB:000020000", true);
		dataProvider = createDataProvider("CNST_TEST", false);
		dataProvider2 = createDataProvider("CNST_TEST2", false);
		obsoleteDataProvider = createDataProvider("CNST_ODP", true);
		person = createPerson("TEST:ConstructPerson0001");
		constructComponent = createConstructComponentSlotAnnotation(List.of(reference), "cc1", taxon, "Caenorhabditis elegans", List.of(relatedNote));
	}
	
	@Test
	@Order(1)
	public void createValidConstruct() {
		loadRequiredEntities();
		
		Construct construct = new Construct();
		construct.setModEntityId(CONSTRUCT);
		construct.setName("cnstTst1");
		construct.setTaxon(taxon);
		construct.setReferences(List.of(reference));
		construct.setDateCreated(datetime);
		construct.setConstructComponents(List.of(constructComponent));
		construct.setDataProvider(dataProvider);
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/findBy/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity.modEntityId", is(CONSTRUCT)).
			body("entity.name", is("cnstTst1")).
			body("entity.taxon.curie", is(taxon.getCurie())).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.references[0].curie", is(reference.getCurie())).
			body("entity.dateCreated", is(datetime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation())).
			body("entity.constructComponents", hasSize(1)).
			body("entity.constructComponents[0].componentSymbol", is("cc1")).
			body("entity.constructComponents[0].taxon.curie", is(taxon.getCurie())).
			body("entity.constructComponents[0].taxonText", is("Caenorhabditis elegans")).
			body("entity.constructComponents[0].relatedNotes", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].noteType.name", is(relatedNote.getNoteType().getName())).
			body("entity.constructComponents[0].relatedNotes[0].freeText", is(relatedNote.getFreeText())).
			body("entity.constructComponents[0].relatedNotes[0].internal", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].obsolete", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].references", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].references[0].curie", is(reference.getCurie())).
			body("entity.constructComponents[0].evidence", hasSize(1)).
			body("entity.constructComponents[0].evidence[0].curie", is(reference.getCurie()));	
	}

	@Test
	@Order(2)
	public void editConstruct() {
		Construct construct = getConstruct(CONSTRUCT);
		construct.setCreatedBy(person);
		construct.setName("cnstTst2");
		construct.setTaxon(taxon2);
		construct.setReferences(List.of(reference2));
		construct.setDateCreated(datetime2);
		construct.setDataProvider(dataProvider2);
		construct.setInternal(true);
		construct.setObsolete(true);
	
		
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setComponentSymbol("cc2");
		component.setTaxon(taxon2);
		component.setTaxonText("Homo sapiens");
		component.setEvidence(List.of(reference2));
		component.setRelatedNotes(List.of(relatedNote2));
		construct.setConstructComponents(List.of(component));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/findBy/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity.modEntityId", is(CONSTRUCT)).
			body("entity.name", is("cnstTst2")).
			body("entity.taxon.curie", is(taxon2.getCurie())).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.references[0].curie", is(reference2.getCurie())).
			body("entity.dateCreated", is(datetime2.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation())).
			body("entity.constructComponents", hasSize(1)).
			body("entity.constructComponents[0].componentSymbol", is("cc2")).
			body("entity.constructComponents[0].taxon.curie", is(taxon2.getCurie())).
			body("entity.constructComponents[0].taxonText", is("Homo sapiens")).
			body("entity.constructComponents[0].relatedNotes", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].noteType.name", is(relatedNote2.getNoteType().getName())).
			body("entity.constructComponents[0].relatedNotes[0].freeText", is(relatedNote2.getFreeText())).
			body("entity.constructComponents[0].relatedNotes[0].internal", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].obsolete", is(false)).
			body("entity.constructComponents[0].relatedNotes[0].references", hasSize(1)).
			body("entity.constructComponents[0].relatedNotes[0].references[0].curie", is(reference2.getCurie())).
			body("entity.constructComponents[0].evidence", hasSize(1)).
			body("entity.constructComponents[0].evidence[0].curie", is(reference2.getCurie()));	
	}
	
	@Test
	@Order(3)
	public void createConstructWithMissingRequiredFieldsLevel1() {
		Construct construct = new Construct();
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.name", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(4)
	public void editConstructWithMissingRequiredFieldsLevel1() {
		Construct construct = getConstruct(CONSTRUCT);
		construct.setName(null);
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.name", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(5)
	public void createConstructWithMissingRequiredFieldsLevel2() {
		Construct construct = new Construct();
		construct.setName("cnstTst");
		construct.setDataProvider(dataProvider);
		
		ConstructComponentSlotAnnotation invalidComponent = new ConstructComponentSlotAnnotation();
		construct.setConstructComponents(List.of(invalidComponent));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.constructComponents", is("componentSymbol - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(6)
	public void editConstructWithMissingRequiredFieldsLevel2() {
		Construct construct = getConstruct(CONSTRUCT);
		ConstructComponentSlotAnnotation invalidComponent = construct.getConstructComponents().get(0);
		invalidComponent.setComponentSymbol(null);
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.constructComponents", is("componentSymbol - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(7)
	public void createConstructWithEmptyRequiredFields() {
		Construct construct = new Construct();
		construct.setName("");
		construct.setDataProvider(dataProvider);
		ConstructComponentSlotAnnotation component = new ConstructComponentSlotAnnotation();
		component.setComponentSymbol("");
		construct.setConstructComponents(List.of(component));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(2))).
			body("errorMessages.name", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.constructComponents", is("componentSymbol - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(8)
	public void editConstructWithEmptyRequiredFields() {
		Construct construct = getConstruct(CONSTRUCT);
		construct.setName("");
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setComponentSymbol("");
		construct.setConstructComponents(List.of(component));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(2))).
			body("errorMessages.name", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.constructComponents", is("componentSymbol - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void createConstructWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		Organization nonPersistedOrganization = new Organization();
		nonPersistedOrganization.setAbbreviation("INV");
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(alleleNoteType);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("invalid");
		
		ConstructComponentSlotAnnotation invalidComponent = createConstructComponentSlotAnnotation(List.of(nonPersistedReference), "invComp", nonPersistedTaxon, null, List.of(invalidNote));
		
		Construct construct = new Construct();
		construct.setModEntityId(CONSTRUCT);
		construct.setName("cnstTst1");
		construct.setTaxon(nonPersistedTaxon);
		construct.setReferences(List.of(nonPersistedReference));
		construct.setDateCreated(datetime);
		construct.setDataProvider(invalidDataProvider);
		construct.setConstructComponents(List.of(invalidComponent));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.constructComponents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.INVALID_MESSAGE,
					"relatedNotes - " + String.join( " | ", List.of(
							"noteType - " + ValidationConstants.INVALID_MESSAGE,
							"references - " + ValidationConstants.INVALID_MESSAGE
							)),
					"taxon - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(10)
	public void editConstructWithInvalidFields() {
		NCBITaxonTerm nonPersistedTaxon = new NCBITaxonTerm();
		nonPersistedTaxon.setCurie("NCBITaxon:Invalid");
		Reference nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		Organization nonPersistedOrganization = new Organization();
		nonPersistedOrganization.setAbbreviation("INV");
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		
		Note invalidNote = new Note();
		invalidNote.setNoteType(alleleNoteType);
		invalidNote.setReferences(List.of(nonPersistedReference));
		invalidNote.setFreeText("invalid");
		
		Construct construct = getConstruct(CONSTRUCT);
		construct.setTaxon(nonPersistedTaxon);
		construct.setReferences(List.of(nonPersistedReference));
		construct.setDataProvider(invalidDataProvider);
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setEvidence(List.of(nonPersistedReference));
		component.setRelatedNotes(List.of(invalidNote));
		component.setTaxon(nonPersistedTaxon);
		construct.setConstructComponents(List.of(component));
		
		RestAssured.given().
		contentType("application/json").
		body(construct).
		when().
		put("/api/construct").
		then().
		statusCode(400).
		body("errorMessages", is(aMapWithSize(4))).
		body("errorMessages.taxon", is(ValidationConstants.INVALID_MESSAGE)).
		body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
		body("errorMessages.references", is("curie - " + ValidationConstants.INVALID_MESSAGE)).
		body("errorMessages.constructComponents", is(String.join(" | ", List.of(
				"evidence - " + ValidationConstants.INVALID_MESSAGE,
				"relatedNotes - " + String.join( " | ", List.of(
						"noteType - " + ValidationConstants.INVALID_MESSAGE,
						"references - " + ValidationConstants.INVALID_MESSAGE
						)),
				"taxon - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(11)
	public void createConstructWithObsoleteFields() {
		
		Note obsoleteNote = new Note();
		obsoleteNote.setFreeText("obsolete note type");
		obsoleteNote.setNoteType(obsoleteNoteType);
		
		ConstructComponentSlotAnnotation obsoleteComponent = createConstructComponentSlotAnnotation(List.of(obsoleteReference), "invComp", obsoleteTaxon, null, List.of(obsoleteNote));
		
		Construct construct = new Construct();
		construct.setModEntityId(CONSTRUCT);
		construct.setName("cnstTst1");
		construct.setTaxon(obsoleteTaxon);
		construct.setReferences(List.of(obsoleteReference));
		construct.setDateCreated(datetime);
		construct.setDataProvider(obsoleteDataProvider);
		construct.setConstructComponents(List.of(obsoleteComponent));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.constructComponents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"relatedNotes - noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"taxon - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(12)
	public void editConstructWithObsoleteFields() {
		Note obsoleteNote = new Note();
		obsoleteNote.setFreeText("obsolete note type");
		obsoleteNote.setNoteType(obsoleteNoteType);
		
		Construct construct = getConstruct(CONSTRUCT);
		construct.setTaxon(obsoleteTaxon);
		construct.setReferences(List.of(obsoleteReference));
		construct.setDataProvider(obsoleteDataProvider);
		
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setEvidence(List.of(obsoleteReference));
		component.setRelatedNotes(List.of(obsoleteNote));
		component.setTaxon(obsoleteTaxon);
		construct.setConstructComponents(List.of(component));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.taxon", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.references", is("curie - " + ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.constructComponents", is(String.join(" | ", List.of(
					"evidence - " + ValidationConstants.OBSOLETE_MESSAGE,
					"relatedNotes - noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"taxon - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(13)
	public void editConstructWithNullNonRequiredFieldsLevel2() {
		// Level 2 done before 1 to avoid having to restore nulled fields
		Construct construct = getConstruct(CONSTRUCT);
		
		ConstructComponentSlotAnnotation component = construct.getConstructComponents().get(0);
		component.setEvidence(null);
		component.setTaxon(null);
		component.setTaxonText(null);
		component.setRelatedNotes(null);
		
		construct.setConstructComponents(List.of(component));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/findBy/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity", hasKey("constructComponents")).
			body("entity.constructComponents[0]", not(hasKey("evidence"))).
			body("entity.constructComponents[0]", not(hasKey("taxon"))).
			body("entity.constructComponents[0]", not(hasKey("taxonText"))).
			body("entity.constructComponents[0]", not(hasKey("relatedNotes")));
	}
	
	@Test
	@Order(14)
	public void editConstructWithNullNonRequiredFieldsLevel1() {
		Construct construct = getConstruct(CONSTRUCT);
		
		construct.setConstructComponents(null);
		construct.setReferences(null);
		construct.setTaxon(null);
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			put("/api/construct").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/construct/findBy/" + CONSTRUCT).
			then().
			statusCode(200).
			body("entity", not(hasKey("constructComponents"))).
			body("entity", not(hasKey("taxon"))).
			body("entity", not(hasKey("references")));
	}
	
	@Test
	@Order(15)
	public void createConstructWithOnlyRequiredFieldsLevel1() {
		Construct construct = new Construct();
		construct.setName("MinimalTestCnst");
		construct.setDataProvider(dataProvider);
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(16)
	public void createConstructWithOnlyRequiredFieldsLevel2() {
		Construct construct = new Construct();
		construct.setName("MinimalTestCnst2");
		construct.setDataProvider(dataProvider);
		
		ConstructComponentSlotAnnotation minimalComponent = createConstructComponentSlotAnnotation(null, "minimalCmp", null, null, null);
		construct.setConstructComponents(List.of(minimalComponent));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(200);
	}
	
	@Test
	@Order(17)
	public void createConstructWithDuplicateNote() {
		Construct construct = new Construct();
		construct.setName("duplicateNoteTest");
		construct.setDataProvider(dataProvider);
		
		ConstructComponentSlotAnnotation component = createConstructComponentSlotAnnotation(null, "dnCmp", null, null, List.of(relatedNote, relatedNote));
		construct.setConstructComponents(List.of(component));
		
		RestAssured.given().
			contentType("application/json").
			body(construct).
			when().
			post("/api/construct").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.constructComponents", is("relatedNotes - " + ValidationConstants.DUPLICATE_MESSAGE + " (Test text|AGRKB:000020003|test_construct_component_note|false|false)"));
		
	}
	
	private ConstructComponentSlotAnnotation createConstructComponentSlotAnnotation (List<InformationContentEntity> evidence, String symbol, NCBITaxonTerm taxon, String taxonText, List<Note> notes) {
		ConstructComponentSlotAnnotation cc = new ConstructComponentSlotAnnotation();
		cc.setComponentSymbol(symbol);
		cc.setEvidence(evidence);
		cc.setRelatedNotes(notes);
		cc.setTaxon(taxon);
		cc.setTaxonText(taxonText);
		return cc;
	}

}
