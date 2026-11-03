package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
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
import io.restassured.path.json.JsonPath;

/**
 * Saving an entity that cites a reference, or an annotation whose evidence item is one, only names that
 * reference: the stored reference is left as it is, whatever else the submitted copy carries.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("0315 - AttachedReferenceITCase")
@Order(315)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0315_AttachedReferenceITCase extends BaseITCase {

	private static final String ALLELE = "Allele:6626Attached0001";
	private static final String ALLELE_REFERENCE = "AGRKB:6626100001";
	private static final String NOTE_REFERENCE = "AGRKB:6626100002";
	private static final String EVIDENCE_REFERENCE = "AGRKB:6626100003";
	private static final String OBSOLETE_REFERENCE = "AGRKB:6626100004";

	private VocabularyTerm noteType;
	private VocabularyTerm diseaseRelation;
	private Gene gene;
	private DOTerm doTerm;
	private ECOTerm ecoTerm;

	private void loadRequiredEntities() {
		VocabularyTerm symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
		noteType = getVocabularyTerm(getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY), "comment");
		diseaseRelation = getVocabularyTerm(getVocabulary(VocabularyConstants.DISEASE_RELATION_VOCABULARY), "is_implicated_in");
		createReference(ALLELE_REFERENCE, "PMID:6626100001", false);
		createReference(NOTE_REFERENCE, "PMID:6626100002", false);
		createReference(EVIDENCE_REFERENCE, "PMID:6626100003", false);
		createReference(OBSOLETE_REFERENCE, "PMID:6626100004", true);
		createAllele(ALLELE, "NCBITaxon:6239", symbolNameType, false);
		gene = createGene("GENE:6626Attached0001", "NCBITaxon:9606", symbolNameType, false);
		doTerm = createDoTerm("DOID:6626001", false);
		ecoTerm = createEcoTerm("ECO:6626001", "Attached reference evidence code", false, true);
	}

	@Test
	@Order(1)
	public void alleleSaveLeavesItsReferencesAsStored() {
		loadRequiredEntities();
		Map<String, Object> before = storedReference(ALLELE_REFERENCE);

		Allele allele = getAllele(ALLELE);
		allele.setReferences(List.of(submittedAsObsoleteAndInternal(ALLELE_REFERENCE)));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200);

		assertEquals(before, storedReference(ALLELE_REFERENCE));
	}

	@Test
	@Order(2)
	public void noteSaveLeavesItsReferencesAsStored() {
		Map<String, Object> before = storedReference(NOTE_REFERENCE);

		Allele allele = getAllele(ALLELE);
		allele.setRelatedNotes(List.of(createNote(noteType, "Attached reference note", false, false, submittedAsObsoleteAndInternal(NOTE_REFERENCE))));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(200);

		assertEquals(before, storedReference(NOTE_REFERENCE));
	}

	@Test
	@Order(3)
	public void annotationSaveLeavesItsEvidenceItemAsStored() {
		Map<String, Object> before = storedReference(EVIDENCE_REFERENCE);

		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setRelation(diseaseRelation);
		diseaseAnnotation.setDiseaseAnnotationObject(doTerm);
		diseaseAnnotation.setDataProvider(getOrganization("WB"));
		diseaseAnnotation.setDiseaseAnnotationSubject(gene);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setEvidenceItem(submittedAsObsoleteAndInternal(EVIDENCE_REFERENCE));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(200);

		assertEquals(before, storedReference(EVIDENCE_REFERENCE));
	}

	// The submitted copy says nothing about whether the reference is obsolete; the stored reference is.
	@Test
	@Order(4)
	public void newlyAttachedStoredObsoleteReferenceIsRejected() {
		Reference submitted = new Reference();
		submitted.setCurie(OBSOLETE_REFERENCE);

		Allele allele = getAllele(ALLELE);
		allele.setReferences(List.of(submitted));

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele").
			then().
			statusCode(400).
			body("errorMessages.references", is("curie - " + ValidationConstants.OBSOLETE_MESSAGE));

		RestAssured.given().
			when().
			get("/api/reference/" + OBSOLETE_REFERENCE).
			then().
			statusCode(200).
			body("entity.obsolete", is(true));
	}

	private Reference submittedAsObsoleteAndInternal(String curie) {
		Reference reference = new Reference();
		reference.setCurie(curie);
		reference.setObsolete(true);
		reference.setInternal(true);
		return reference;
	}

	// The fields a save that rewrote the stored reference would change.
	private Map<String, Object> storedReference(String curie) {
		JsonPath stored = RestAssured.given().
			when().
			get("/api/reference/" + curie).
			then().
			statusCode(200).
			extract().
			jsonPath();

		Map<String, Object> fields = new HashMap<>();
		fields.put("obsolete", stored.get("entity.obsolete"));
		fields.put("internal", stored.get("entity.internal"));
		fields.put("dateCreated", stored.get("entity.dateCreated"));
		fields.put("dateUpdated", stored.get("entity.dateUpdated"));
		fields.put("updatedBy", stored.get("entity.updatedBy.uniqueId"));
		return fields;
	}
}
