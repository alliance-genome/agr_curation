package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
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
@DisplayName("0314 - AlleleReferenceITCase")
@Order(314)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0314_AlleleReferenceITCase extends BaseITCase {

	private static final String ALLELE = "Allele:6626Ref0001";
	private static final String REFERENCE_KEPT = "AGRKB:6626000001";
	private static final String REFERENCE_DROPPED = "AGRKB:6626000002";
	private static final String REFERENCE_OBSOLETE = "AGRKB:6626000003";
	private static final String REFERENCE_OBSOLETED_LATER = "AGRKB:6626000004";
	private static final String REFERENCE_KEPT_XREF = "PMID:6626000001";
	private static final String REFERENCE_UNKNOWN = "AGRKB:6626999999";

	private Long alleleId;
	private Reference obsoletedLaterReference;

	private void loadRequiredEntities() {
		VocabularyTerm symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
		createReference(REFERENCE_KEPT, REFERENCE_KEPT_XREF, false);
		createReference(REFERENCE_DROPPED, "PMID:6626000002", false);
		createReference(REFERENCE_OBSOLETE, "PMID:6626000003", true);
		obsoletedLaterReference = createReference(REFERENCE_OBSOLETED_LATER, "PMID:6626000004", false);
		alleleId = createAllele(ALLELE, "NCBITaxon:6239", symbolNameType, false).getId();
	}

	@Test
	@Order(1)
	public void replaceAddsReferences() {
		loadRequiredEntities();

		// createAllele leaves the allele with none, and NON_EMPTY drops the key rather than sending [].
		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities", nullValue());

		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { buildReference(REFERENCE_KEPT), buildReference(REFERENCE_DROPPED) }).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body("entities.curie", containsInAnyOrder(REFERENCE_KEPT, REFERENCE_DROPPED)).
			body("entities.find { it.curie == '" + REFERENCE_KEPT + "' }.crossReferences[0].referencedCurie", is(REFERENCE_KEPT_XREF));

		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body("entities.curie", containsInAnyOrder(REFERENCE_KEPT, REFERENCE_DROPPED)).
			body("entities.find { it.curie == '" + REFERENCE_KEPT + "' }.crossReferences[0].referencedCurie", is(REFERENCE_KEPT_XREF));
	}

	@Test
	@Order(2)
	public void replaceRemovesOmittedReference() {
		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { buildReference(REFERENCE_KEPT) }).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].curie", is(REFERENCE_KEPT));

		// Omitting a reference detaches it from the allele without deleting it.
		RestAssured.given().
			when().
			get("/api/reference/" + REFERENCE_DROPPED).
			then().
			statusCode(200).
			body("entity.curie", is(REFERENCE_DROPPED));
	}

	@Test
	@Order(3)
	public void replaceResolvesCrossReferenceCurie() {
		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { buildReference(REFERENCE_KEPT_XREF) }).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].curie", is(REFERENCE_KEPT));
	}

	@Test
	@Order(4)
	public void replaceDoesNotModifyStoredReference() {
		Reference submitted = buildReference(REFERENCE_KEPT);
		submitted.setObsolete(true);
		submitted.setInternal(true);

		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { submitted }).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities[0].obsolete", is(false)).
			body("entities[0].internal", is(false));

		RestAssured.given().
			when().
			get("/api/reference/" + REFERENCE_KEPT).
			then().
			statusCode(200).
			body("entity.obsolete", is(false)).
			body("entity.internal", is(false));
	}

	@Test
	@Order(5)
	public void unknownReferenceIsRejectedByIndex() {
		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { buildReference(REFERENCE_KEPT), buildReference(REFERENCE_UNKNOWN) }).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(400).
			body("errorMessages.references", notNullValue()).
			body("supplementalData.errorMap.references.'1'.curie", is(ValidationConstants.INVALID_MESSAGE));

		assertHoldsOnly(REFERENCE_KEPT);
	}

	@Test
	@Order(6)
	public void missingCurieIsRejectedByIndex() {
		Reference[] submitted = { buildReference(REFERENCE_KEPT), new Reference(), null };

		RestAssured.given().
			contentType("application/json").
			body(submitted).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(400).
			body("supplementalData.errorMap.references.'1'.curie", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("supplementalData.errorMap.references.'2'.curie", is(ValidationConstants.REQUIRED_MESSAGE));

		assertHoldsOnly(REFERENCE_KEPT);
	}

	@Test
	@Order(7)
	public void duplicateReferenceIsRejected() {
		// The cross reference curie names the same reference as the AGRKB curie before it.
		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { buildReference(REFERENCE_KEPT), buildReference(REFERENCE_KEPT_XREF) }).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(400).
			body("supplementalData.errorMap.references.'1'.curie", is(ValidationConstants.DUPLICATE_MESSAGE));

		assertHoldsOnly(REFERENCE_KEPT);
	}

	@Test
	@Order(8)
	public void newlyAttachedObsoleteReferenceIsRejected() {
		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { buildReference(REFERENCE_KEPT), buildReference(REFERENCE_OBSOLETE) }).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(400).
			body("supplementalData.errorMap.references.'1'.curie", is(ValidationConstants.OBSOLETE_MESSAGE));

		assertHoldsOnly(REFERENCE_KEPT);
	}

	@Test
	@Order(9)
	public void alreadyAttachedObsoleteReferenceIsKept() {
		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { buildReference(REFERENCE_KEPT), buildReference(REFERENCE_OBSOLETED_LATER) }).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities", hasSize(2));

		obsoletedLaterReference.setObsolete(true);
		RestAssured.given().
			contentType("application/json").
			body(obsoletedLaterReference).
			when().
			put("/api/reference").
			then().
			statusCode(200);

		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { buildReference(REFERENCE_KEPT), buildReference(REFERENCE_OBSOLETED_LATER) }).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities.curie", containsInAnyOrder(REFERENCE_KEPT, REFERENCE_OBSOLETED_LATER));
	}

	@Test
	@Order(10)
	public void emptyListClearsReferences() {
		RestAssured.given().
			contentType("application/json").
			body(new Reference[0]).
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities", nullValue());

		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	@Test
	@Order(11)
	public void missingBodyIsRejected() {
		RestAssured.given().
			contentType("application/json").
			body("null").
			when().
			put("/api/allele/" + alleleId + "/references").
			then().
			statusCode(400).
			body("errorMessages.references", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(12)
	public void unknownAlleleIsRejected() {
		RestAssured.given().
			when().
			get("/api/allele/-1/references").
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));

		RestAssured.given().
			contentType("application/json").
			body(new Reference[] { buildReference(REFERENCE_KEPT) }).
			when().
			put("/api/allele/-1/references").
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));
	}

	// Bodies are sent as Reference arrays so each element is written with the `type` the API reads it by.
	private Reference buildReference(String curie) {
		Reference reference = new Reference();
		reference.setCurie(curie);
		return reference;
	}

	private void assertHoldsOnly(String curie) {
		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].curie", is(curie));
	}
}
