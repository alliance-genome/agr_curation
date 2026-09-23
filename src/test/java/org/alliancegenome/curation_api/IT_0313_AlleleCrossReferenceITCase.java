package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
	private static final String OTHER_ALLELE = "Allele:6503Xref0002";
	private static final String XREF_KEPT = "XRSUB:0001";
	private static final String XREF_DROPPED = "XRSUB:0002";
	private static final String XREF_REJECTED = "XRSUB:0003";
	private static final String XREF_FOREIGN = "XRSUB:0005";

	private Long alleleId;
	private VocabularyTerm symbolNameType;
	private ResourceDescriptorPage defaultPage;
	private ResourceDescriptorPage genePage;

	private void loadRequiredEntities() {
		symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
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
	public void resourceDescriptorIsVisibleThroughBothViews() {
		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities[0].resourceDescriptorPage.resourceDescriptor.prefix", is("XRSUB")).
			body("entities[0].resourceDescriptorPage.resourceDescriptor.resourcePages", nullValue());

		// find serializes AlleleView, which is what the Alleles table and its dialog read. The absent
		// resourcePages pins that the descriptor's own lists stay out of both views.
		RestAssured.given().
			contentType("application/json").
			body("{\"primaryExternalId\": \"" + ALLELE + "\"}").
			when().
			post("/api/allele/find?limit=1&page=0").
			then().
			statusCode(200).
			body("results[0].crossReferences[0].resourceDescriptorPage.resourceDescriptor.prefix", is("XRSUB")).
			body("results[0].crossReferences[0].resourceDescriptorPage.resourceDescriptor.resourcePages", nullValue());
	}

	@Test
	@Order(4)
	public void rejectedReplaceChangesNothing() {
		CrossReference missingCurie = new CrossReference();
		missingCurie.setDisplayName("no referenced curie");
		missingCurie.setInternal(false);
		missingCurie.setObsolete(false);

		RestAssured.given().
			contentType("application/json").
			body(List.of(buildXref(XREF_REJECTED, defaultPage), missingCurie)).
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(400).
			body("errorMessages.crossReferences", notNullValue()).
			body("supplementalData.errorMap.crossReferences.'1'.referencedCurie", is(ValidationConstants.REQUIRED_MESSAGE));

		// The existing entry must survive.
		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].referencedCurie", is(XREF_KEPT));

		// The valid entry in that same payload is persisted before the invalid one is reached, so the reject
		// has to roll it back rather than leave a row nothing points at. find only counts when page and
		// limit are both 0.
		RestAssured.given().
			contentType("application/json").
			body("{\"referencedCurie\": \"" + XREF_REJECTED + "\"}").
			when().
			post("/api/cross-reference/find?limit=0&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}

	@Test
	@Order(5)
	public void foreignCrossReferenceIsRejected() {
		Long otherAlleleId = createAllele(OTHER_ALLELE, "NCBITaxon:6239", symbolNameType, false).getId();

		RestAssured.given().
			contentType("application/json").
			body(List.of(buildXref(XREF_FOREIGN, defaultPage))).
			when().
			put("/api/allele/" + otherAlleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(1));

		Long foreignId = RestAssured.given().
			when().
			get("/api/allele/" + otherAlleleId + "/cross-references").
			then().
			statusCode(200).
			extract().
			jsonPath().
			getLong("entities[0].id");

		// Nothing on a cross reference row records its owner, so an id has to be checked against the owner's
		// own list. Claiming another allele's row would re-parent it and leave that allele's next save to
		// orphan-delete it.
		CrossReference claimed = buildXref(XREF_FOREIGN, defaultPage);
		claimed.setId(foreignId);

		RestAssured.given().
			contentType("application/json").
			body(List.of(claimed)).
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(400).
			body("supplementalData.errorMap.crossReferences.'0'.id", is(ValidationConstants.INVALID_MESSAGE));

		// An id that belongs to nothing is rejected the same way, rather than failing once the write flushes.
		CrossReference unknown = buildXref(XREF_FOREIGN, defaultPage);
		unknown.setId(-1L);

		RestAssured.given().
			contentType("application/json").
			body(List.of(unknown)).
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(400).
			body("supplementalData.errorMap.crossReferences.'0'.id", is(ValidationConstants.INVALID_MESSAGE));

		// Both alleles keep what they had.
		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].referencedCurie", is(XREF_KEPT));

		RestAssured.given().
			when().
			get("/api/allele/" + otherAlleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].referencedCurie", is(XREF_FOREIGN));
	}

	@Test
	@Order(6)
	public void nullBodyIsRejected() {
		// An empty list is a deliberate clear, so a missing list must not be read as one.
		RestAssured.given().
			contentType("application/json").
			body("null").
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(400).
			body("errorMessages.crossReferences", is(ValidationConstants.REQUIRED_MESSAGE));

		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].referencedCurie", is(XREF_KEPT));
	}

	@Test
	@Order(7)
	public void clearingThePageStoresItCleared() {
		CrossReference withoutPage = RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			extract().
			jsonPath().
			getObject("entities[0]", CrossReference.class);
		withoutPage.setResourceDescriptorPage(null);

		// The page is applied whether or not the payload names one, so omitting it clears the stored one
		// rather than leaving it in place.
		RestAssured.given().
			contentType("application/json").
			body(List.of(withoutPage)).
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0]", not(hasKey("resourceDescriptorPage")));

		RestAssured.given().
			when().
			get("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities[0]", not(hasKey("resourceDescriptorPage"))).
			body("entities[0].referencedCurie", is(XREF_KEPT));

		// Put it back, so the tests after this one see the allele they expect.
		CrossReference restored = buildXref(XREF_KEPT, defaultPage);
		restored.setId(withoutPage.getId());

		RestAssured.given().
			contentType("application/json").
			body(List.of(restored)).
			when().
			put("/api/allele/" + alleleId + "/cross-references").
			then().
			statusCode(200).
			body("entities[0].resourceDescriptorPage.name", is("default"));
	}

	@Test
	@Order(8)
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
	@Order(9)
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
