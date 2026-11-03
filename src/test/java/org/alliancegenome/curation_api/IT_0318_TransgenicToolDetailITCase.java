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
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
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
import io.restassured.response.Response;

/**
 * SCRUM-6655: what the transgenic tool detail page writes. The tool itself through PUT /transgenic-tool, and the
 * associations it is the subject of through /transgenic-tool/{id}/transgenic-tool-associations, which replaces
 * them with the submitted list. A tool's cross references are saved with the tool, and each must match its page's
 * resource descriptor.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("0318 - TransgenicToolDetailITCase")
@Order(318)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0318_TransgenicToolDetailITCase extends BaseITCase {

	private static final String TOOL = "TTD_TEST:Tool0001";
	private static final String TOOL2 = "TTD_TEST:Tool0002";
	private static final String TOOL3 = "TTD_TEST:Tool0003";

	private Organization dataProvider;
	private VocabularyTerm isCompatibleWith;
	private VocabularyTerm otherVocabularyRelation;
	private VocabularyTerm symbolNameType;
	private TransgenicTool tool;
	private TransgenicTool tool2;
	private TransgenicTool tool3;
	private Long toTool2Id;

	private void loadRequiredEntities() {
		dataProvider = createOrganization("TTD_TEST", false);
		symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
		isCompatibleWith = getVocabularyTerm(getVocabulary("transgenic_tool_association_relation"), "is_compatible_with");
		otherVocabularyRelation = getVocabularyTerm(getVocabulary("cassette_relation"), "is_regulated_by");
		tool = createTool(TOOL);
		tool2 = createTool(TOOL2);
		tool3 = createTool(TOOL3);
	}

	@Test
	@Order(1)
	public void replaceCreatesAssociations() {
		loadRequiredEntities();

		RestAssured.given().
			when().
			get(endpoint(tool.getId())).
			then().
			statusCode(200).
			body("entities", nullValue());

		// The PUT response is read after the write transaction closes, so it is asserted directly.
		Response response = put(tool.getId(), buildAssociation(null, tool2, isCompatibleWith), buildAssociation(null, tool3, isCompatibleWith)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body("entities.transgenicToolTransgenicToolAssociationObject.primaryExternalId", containsInAnyOrder(TOOL2, TOOL3)).
			body("entities.transgenicToolAssociationSubject.primaryExternalId", containsInAnyOrder(TOOL, TOOL)).
			body("entities.relation.name", containsInAnyOrder("is_compatible_with", "is_compatible_with")).
			extract().response();
		toTool2Id = idOf(response, TOOL2);

		RestAssured.given().
			when().
			get("/api/transgenic-tool/" + TOOL).
			then().
			statusCode(200).
			body("entity.associatedTransgenicTools", hasSize(2));
	}

	@Test
	@Order(2)
	public void replaceUpdatesInPlaceAndDeletesOmitted() {
		TransgenicToolTransgenicToolAssociation edited = buildAssociation(toTool2Id, tool2, isCompatibleWith);
		edited.setInternal(true);

		put(tool.getId(), edited).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(toTool2Id.intValue())).
			body("entities[0].internal", is(true)).
			body("entities[0].updatedBy.uniqueId", notNullValue());

		RestAssured.given().
			when().
			get("/api/transgenictooltransgenictoolassociation/findBy?transgenicToolSubjectId=" + tool.getId() + "&relationName=is_compatible_with&transgenicToolObjectId=" + tool3.getId()).
			then().
			statusCode(200).
			body("entity", nullValue());
	}

	@Test
	@Order(3)
	public void replaceRejectsInvalidEntries() {
		TransgenicToolTransgenicToolAssociation noObject = buildAssociation(null, null, isCompatibleWith);
		TransgenicToolTransgenicToolAssociation noRelation = buildAssociation(null, tool3, null);

		put(tool.getId(), buildAssociation(toTool2Id, tool2, isCompatibleWith), noObject, noRelation).
			then().
			statusCode(400).
			body("errorMessages.transgenicToolTransgenicToolAssociations", notNullValue()).
			body("supplementalData.errorMap.transgenicToolTransgenicToolAssociations.'1'.transgenicToolTransgenicToolAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("supplementalData.errorMap.transgenicToolTransgenicToolAssociations.'2'.relation", is(ValidationConstants.REQUIRED_MESSAGE));

		put(tool.getId(), buildAssociation(null, tool3, otherVocabularyRelation)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.transgenicToolTransgenicToolAssociations.'0'.relation", is(ValidationConstants.INVALID_MESSAGE));

		assertHoldsOnly(tool.getId(), toTool2Id);
	}

	@Test
	@Order(4)
	public void replaceRejectsDuplicatesAndForeignIds() {
		put(tool.getId(), buildAssociation(toTool2Id, tool2, isCompatibleWith), buildAssociation(null, tool2, isCompatibleWith)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.transgenicToolTransgenicToolAssociations.'1'.transgenicToolTransgenicToolAssociationObject", is(ValidationConstants.DUPLICATE_MESSAGE));

		put(tool.getId(), buildAssociation(toTool2Id, tool2, isCompatibleWith), buildAssociation(toTool2Id, tool2, isCompatibleWith)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.transgenicToolTransgenicToolAssociations.'1'.id", is(ValidationConstants.DUPLICATE_MESSAGE));

		Long otherToolsId = idOf(put(tool2.getId(), buildAssociation(null, tool3, isCompatibleWith)).then().statusCode(200).extract().response(), TOOL3);
		put(tool.getId(), buildAssociation(otherToolsId, tool3, isCompatibleWith)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.transgenicToolTransgenicToolAssociations.'0'.id", is(ValidationConstants.INVALID_MESSAGE));

		assertHoldsOnly(tool.getId(), toTool2Id);
		assertHoldsOnly(tool2.getId(), otherToolsId);

		put(-1L).
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(5)
	public void updateToolHoldingAssociations() {
		TransgenicTool stored = getTransgenicTool(TOOL);
		stored.setPlaceholder(true);
		stored.setInternal(true);
		stored.setSecondaryIdentifiers(List.of("TTD_TEST:Secondary0001"));
		TransgenicToolSynonymSlotAnnotation synonym = new TransgenicToolSynonymSlotAnnotation();
		synonym.setNameType(getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "systematic_name"));
		synonym.setDisplayText("TTD synonym");
		synonym.setFormatText("TTD synonym");
		stored.setTransgenicToolSynonyms(List.of(synonym));
		// The tool's associations are written through their own endpoint only
		stored.setTransgenicToolTransgenicToolAssociations(null);

		// The PUT response is read after the write transaction closes, so it is asserted directly.
		RestAssured.given().
			contentType("application/json").
			body(stored).
			when().
			put("/api/transgenic-tool").
			then().
			statusCode(200).
			body("entity.placeholder", is(true)).
			body("entity.internal", is(true)).
			body("entity.secondaryIdentifiers", is(List.of("TTD_TEST:Secondary0001"))).
			body("entity.transgenicToolSynonyms[0].nameType.name", is("systematic_name")).
			body("entity.associatedTransgenicTools", hasSize(1));

		assertHoldsOnly(tool.getId(), toTool2Id);
	}

	@Test
	@Order(6)
	public void replaceWithEmptyListDeletesAll() {
		put(tool.getId()).
			then().
			statusCode(200).
			body("entities", nullValue());

		RestAssured.given().
			when().
			get(endpoint(tool.getId())).
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	@Test
	@Order(7)
	public void updateToolCrossReferences() {
		ResourceDescriptor descriptor = createResourceDescriptor("TTDXR", "^TTDXR:\\d+$");
		ResourceDescriptorPage page = createResourceDescriptorPage("default", "http://test.org/ttdxr/[%s]", descriptor);

		// The page's descriptor is returned with the tool, so the detail page can show it
		updateTool(TOOL3, buildCrossReference("TTDXR:0001", page)).
			then().
			statusCode(200).
			body("entity.crossReferences", hasSize(1)).
			body("entity.crossReferences[0].referencedCurie", is("TTDXR:0001")).
			body("entity.crossReferences[0].resourceDescriptorPage.resourceDescriptor.prefix", is("TTDXR"));

		RestAssured.given().
			when().
			get("/api/transgenic-tool/" + TOOL3).
			then().
			statusCode(200).
			body("entity.crossReferences[0].resourceDescriptorPage.resourceDescriptor.prefix", is("TTDXR"));

		updateTool(TOOL3, buildCrossReference("OTHER:0001", page)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.crossReferences.'0'.referencedCurie", is(ValidationConstants.CURIE_PATTERN_MISMATCH_MESSAGE));

		updateTool(TOOL3, buildCrossReference("TTDXR:0002", null)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.crossReferences.'0'.resourceDescriptorPage", is(ValidationConstants.REQUIRED_MESSAGE));

		RestAssured.given().
			when().
			get("/api/transgenic-tool/" + TOOL3).
			then().
			statusCode(200).
			body("entity.crossReferences", hasSize(1)).
			body("entity.crossReferences[0].referencedCurie", is("TTDXR:0001"));
	}

	private TransgenicTool createTool(String primaryExternalId) {
		TransgenicTool newTool = new TransgenicTool();
		newTool.setPrimaryExternalId(primaryExternalId);
		newTool.setDataProvider(dataProvider);
		// A tool's unique ID is built from its symbol and full name, so each needs its own
		TransgenicToolSymbolSlotAnnotation symbol = new TransgenicToolSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText(primaryExternalId);
		symbol.setFormatText(primaryExternalId);
		newTool.setTransgenicToolSymbol(symbol);

		RestAssured.given().
			contentType("application/json").
			body(newTool).
			when().
			post("/api/transgenic-tool").
			then().
			statusCode(200);

		return getTransgenicTool(primaryExternalId);
	}

	// Saves the stored tool with its cross references replaced by the given one
	private Response updateTool(String primaryExternalId, CrossReference crossReference) {
		TransgenicTool stored = getTransgenicTool(primaryExternalId);
		stored.setCrossReferences(List.of(crossReference));
		// The tool's associations are written through their own endpoint only
		stored.setTransgenicToolTransgenicToolAssociations(null);

		return RestAssured.given().
			contentType("application/json").
			body(stored).
			when().
			put("/api/transgenic-tool");
	}

	private CrossReference buildCrossReference(String referencedCurie, ResourceDescriptorPage page) {
		CrossReference crossReference = new CrossReference();
		crossReference.setReferencedCurie(referencedCurie);
		crossReference.setDisplayName(referencedCurie);
		crossReference.setResourceDescriptorPage(page);
		return crossReference;
	}

	private String endpoint(Long id) {
		return "/api/transgenic-tool/" + id + "/transgenic-tool-associations";
	}

	private Response put(Long id, TransgenicToolTransgenicToolAssociation... associations) {
		return RestAssured.given().
			contentType("application/json").
			body(associations).
			when().
			put(endpoint(id));
	}

	private TransgenicToolTransgenicToolAssociation buildAssociation(Long id, TransgenicTool object, VocabularyTerm relation) {
		TransgenicToolTransgenicToolAssociation association = new TransgenicToolTransgenicToolAssociation();
		association.setId(id);
		association.setTransgenicToolTransgenicToolAssociationObject(object);
		association.setRelation(relation);
		return association;
	}

	private Long idOf(Response response, String objectId) {
		return response.jsonPath().getLong("entities.find { it.transgenicToolTransgenicToolAssociationObject.primaryExternalId == '" + objectId + "' }.id");
	}

	private void assertHoldsOnly(Long id, Long associationId) {
		RestAssured.given().
			when().
			get(endpoint(id)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(associationId.intValue()));
	}
}
