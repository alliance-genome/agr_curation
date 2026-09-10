package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
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
@DisplayName("0313 - AlleleCrossReferenceITCase")
@Order(313)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0313_AlleleCrossReferenceITCase extends BaseITCase {

	private static final String ALLELE = "Allele:6503Xref0001";
	private static final String XREF_KEPT = "XRSUB:0001";
	private static final String XREF_DROPPED = "XRSUB:0002";

	private Long alleleId;
	private ResourceDescriptorPage defaultPage;
	private ResourceDescriptorPage genePage;

	private void loadRequiredEntities() {
		VocabularyTerm symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
		ResourceDescriptor resourceDescriptor = createResourceDescriptor("XRSUB");
		defaultPage = createResourceDescriptorPage("default", "http://test.org/[%s]", resourceDescriptor);
		genePage = createResourceDescriptorPage("gene", "http://test.org/gene/[%s]", resourceDescriptor);
		alleleId = createAllele(ALLELE, "NCBITaxon:6239", symbolNameType, false).getId();
	}

	@Test
	@Order(1)
	public void replaceAddsCrossReferences() {
		loadRequiredEntities();

		// createAllele leaves the allele with none, and NON_EMPTY drops the key rather than sending [].
		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", nullValue());

		RestAssured.given().
			contentType("application/json").
			body(List.of(buildXref(XREF_KEPT, defaultPage), buildXref(XREF_DROPPED, genePage))).
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(2));

		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body("entities.referencedCurie", containsInAnyOrder(XREF_KEPT, XREF_DROPPED)).
			body("entities.find { it.referencedCurie == '" + XREF_KEPT + "' }.resourceDescriptorPage.name", is("default"));
	}

	@Test
	@Order(2)
	public void replaceRemovesOmittedCrossReference() {
		Long droppedId = RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			extract().
			jsonPath().
			getLong("entities.find { it.referencedCurie == '" + XREF_DROPPED + "' }.id");

		RestAssured.given().
			contentType("application/json").
			body(List.of(buildXref(XREF_KEPT, defaultPage))).
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].referencedCurie", is(XREF_KEPT));

		// Omitting an entry deletes its row through orphanRemoval rather than just unlinking it.
		RestAssured.given().
			when().
			get("/api/cross-reference/" + droppedId).
			then().
			statusCode(200).
			body("entity", nullValue());
	}

	@Test
	@Order(3)
	public void rejectedReplaceChangesNothing() {
		CrossReference missingCurie = new CrossReference();
		missingCurie.setDisplayName("no referenced curie");
		missingCurie.setInternal(false);
		missingCurie.setObsolete(false);

		RestAssured.given().
			contentType("application/json").
			body(List.of(buildXref("XRSUB:0003", defaultPage), missingCurie)).
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(400).
			body("errorMessages.crossReferences", notNullValue()).
			body("supplementalData.errorMap.crossReferences.'1'.referencedCurie", is(ValidationConstants.REQUIRED_MESSAGE));

		// The valid entry in that same payload must not have been stored, and the existing one must survive.
		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].referencedCurie", is(XREF_KEPT));
	}

	@Test
	@Order(4)
	public void replaceWithEmptyListClearsCrossReferences() {
		RestAssured.given().
			contentType("application/json").
			body(List.of()).
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", nullValue());

		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	@Test
	@Order(5)
	public void unknownAlleleIsRejected() {
		RestAssured.given().
			when().
			get("/api/allele/-1/cross-references").
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));

		RestAssured.given().
			contentType("application/json").
			body(List.of(buildXref("XRSUB:0004", defaultPage))).
			when().
			put("/api/allele/-1/cross-references").
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));
	}

	private CrossReference buildXref(String referencedCurie, ResourceDescriptorPage page) {
		CrossReference crossReference = new CrossReference();
		crossReference.setReferencedCurie(referencedCurie);
		crossReference.setDisplayName(referencedCurie);
		crossReference.setResourceDescriptorPage(page);
		crossReference.setInternal(false);
		crossReference.setObsolete(false);
		return crossReference;
	}
}
