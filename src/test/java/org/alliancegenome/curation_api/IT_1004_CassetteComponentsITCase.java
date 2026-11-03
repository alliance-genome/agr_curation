package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteStrAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
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
 * SCRUM-6752: the cassette's component lists as the Cassettes table's pop-ups read and replace them, through
 * /api/cassette/{id}/components, /genomic-entity-associations, /transgenic-tool-associations and /str-associations.
 * Runs on WB:Cassette0001 as the loads left it: a free text component (IT_0110), a genomic entity (IT_0205), a
 * transgenic tool (IT_0206) and an STR association (IT_1003).
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("1004 - CassetteComponentsITCase")
@Order(1004)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_1004_CassetteComponentsITCase extends BaseITCase {

	private static final String CASSETTE = "WB:Cassette0001";

	private Long cassetteId;
	private VocabularyTerm expresses;
	private VocabularyTerm isRegulatedBy;
	private VocabularyTerm targets;
	private VocabularyTerm notForTools;

	private void loadRequiredEntities() {
		Vocabulary relations = getVocabulary("cassette_relation");
		expresses = getVocabularyTerm(relations, "expresses");
		isRegulatedBy = getVocabularyTerm(relations, "is_regulated_by");
		targets = getVocabularyTerm(relations, "targets");
		// a cassette relation, but not one a transgenic tool component may have
		notForTools = getVocabularyTerm(relations, "expresses_3'_UTR_of");
		cassetteId = getCassette(CASSETTE).getId();
	}

	@Test
	@Order(1)
	public void readsEachListThroughItsOwnEndpoint() {
		loadRequiredEntities();

		get("components").then().statusCode(200).body("entities", hasSize(1));
		get("genomic-entity-associations").then().statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].cassetteGenomicEntityAssociationObject.primaryExternalId", is("GENETEST:Gene0001"));
		get("transgenic-tool-associations").then().statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].cassetteTransgenicToolAssociationObject.primaryExternalId", is("WB:TransgenicTool0001"));
		get("str-associations").then().statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].cassetteStrAssociationObject.primaryExternalId", is("ZFIN:ZDB-TALEN-180503-1"));
	}

	@Test
	@Order(2)
	public void replaceUpdatesCreatesAndDeletesToolAssociations() {
		Long storedId = storedIds("transgenic-tool-associations").get(0);
		TransgenicTool tool = getTransgenicTool("WB:TransgenicTool0001");
		TransgenicTool otherTool = getTransgenicTool("WB:TransgenicToolMN01");

		CassetteTransgenicToolAssociation edited = toolAssociation(storedId, tool, isRegulatedBy);
		edited.setInternal(true);
		Response response = put("transgenic-tool-associations", edited, toolAssociation(null, otherTool, expresses)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body("entities.cassetteTransgenicToolAssociationObject.primaryExternalId", containsInAnyOrder("WB:TransgenicTool0001", "WB:TransgenicToolMN01")).
			extract().response();
		response.then().body(row(response, "WB:TransgenicTool0001", "cassetteTransgenicToolAssociationObject") + ".id", is(storedId.intValue())).
			body(row(response, "WB:TransgenicTool0001", "cassetteTransgenicToolAssociationObject") + ".relation.name", is("is_regulated_by")).
			body(row(response, "WB:TransgenicTool0001", "cassetteTransgenicToolAssociationObject") + ".internal", is(true));

		put("transgenic-tool-associations", toolAssociation(null, otherTool, expresses)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].cassetteTransgenicToolAssociationObject.primaryExternalId", is("WB:TransgenicToolMN01")).
			body("entities[0].id", not(is(storedId.intValue())));
	}

	@Test
	@Order(3)
	public void replaceRejectsInvalidAssociations() {
		Long storedId = storedIds("transgenic-tool-associations").get(0);
		TransgenicTool tool = getTransgenicTool("WB:TransgenicToolMN01");

		put("transgenic-tool-associations", toolAssociation(null, null, expresses), toolAssociation(null, tool, null), toolAssociation(null, tool, notForTools)).
			then().
			statusCode(400).
			body("errorMessages.cassetteTransgenicToolAssociations", notNullValue()).
			body("supplementalData.errorMap.cassetteTransgenicToolAssociations.'0'.cassetteTransgenicToolAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("supplementalData.errorMap.cassetteTransgenicToolAssociations.'1'.relation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("supplementalData.errorMap.cassetteTransgenicToolAssociations.'2'.relation", is(ValidationConstants.INVALID_MESSAGE));

		put("transgenic-tool-associations", toolAssociation(storedId, tool, expresses), toolAssociation(null, tool, expresses)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.cassetteTransgenicToolAssociations.'1'.cassetteTransgenicToolAssociationObject", is(ValidationConstants.DUPLICATE_MESSAGE));

		put("transgenic-tool-associations", toolAssociation(storedId, tool, expresses), toolAssociation(storedId, tool, targets)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.cassetteTransgenicToolAssociations.'1'.id", is(ValidationConstants.DUPLICATE_MESSAGE));

		// The stored list is left as it was
		assertHoldsOnly("transgenic-tool-associations", storedId);
	}

	@Test
	@Order(4)
	public void replaceRejectsRowsNotInTheList() {
		Long storedId = storedIds("genomic-entity-associations").get(0);
		// A row of the cassette's, but of another list
		Long toolAssociationId = storedIds("transgenic-tool-associations").get(0);

		CassetteGenomicEntityAssociation otherList = new CassetteGenomicEntityAssociation();
		otherList.setId(toolAssociationId);
		otherList.setCassetteGenomicEntityAssociationObject(getGene("GENETEST:Gene0001"));
		otherList.setRelation(expresses);
		put("genomic-entity-associations", otherList).
			then().
			statusCode(400).
			body("supplementalData.errorMap.cassetteGenomicEntityAssociations.'0'.id", is(ValidationConstants.INVALID_MESSAGE));

		CassetteGenomicEntityAssociation unknown = new CassetteGenomicEntityAssociation();
		unknown.setId(-1L);
		unknown.setCassetteGenomicEntityAssociationObject(getGene("GENETEST:Gene0001"));
		unknown.setRelation(expresses);
		put("genomic-entity-associations", unknown).
			then().
			statusCode(400).
			body("supplementalData.errorMap.cassetteGenomicEntityAssociations.'0'.id", is(ValidationConstants.INVALID_MESSAGE));

		assertHoldsOnly("genomic-entity-associations", storedId);

		RestAssured.given().
			contentType("application/json").
			body(List.of()).
			when().
			put("/api/cassette/-1/genomic-entity-associations").
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(5)
	public void replaceWithEmptyListDeletesStrAssociations() {
		Long storedId = storedIds("str-associations").get(0);
		SequenceTargetingReagent str = getSequenceTargetingReagent("ZFIN:ZDB-TALEN-180503-1");

		put("str-associations").then().statusCode(200).body("entities", nullValue());

		CassetteStrAssociation restored = new CassetteStrAssociation();
		restored.setCassetteStrAssociationObject(str);
		restored.setRelation(expresses);
		put("str-associations", restored).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].cassetteStrAssociationObject.primaryExternalId", is("ZFIN:ZDB-TALEN-180503-1")).
			body("entities[0].id", not(is(storedId.intValue())));
	}

	@Test
	@Order(6)
	public void replacingComponentsChangesTheUniqueId() {
		String uniqueIdBefore = getCassette(CASSETTE).getUniqueId();
		Long storedId = storedIds("components").get(0);

		CassetteComponentSlotAnnotation component = new CassetteComponentSlotAnnotation();
		component.setId(storedId);
		component.setRelation(targets);
		component.setComponentSymbol("IT1004 component");
		component.setTaxon(getNCBITaxonTerm("NCBITaxon:6239"));
		component.setTaxonText("Caenorhabditis elegans");
		put("components", component).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(storedId.intValue())).
			body("entities[0].componentSymbol", is("IT1004 component")).
			body("entities[0].relation.name", is("targets"));

		Cassette cassette = getCassette(CASSETTE);
		assertNotEquals(uniqueIdBefore, cassette.getUniqueId());
		assertThat(cassette.getUniqueId(), containsString("IT1004 component"));

		CassetteComponentSlotAnnotation noSymbol = new CassetteComponentSlotAnnotation();
		noSymbol.setRelation(targets);
		put("components", component, noSymbol).
			then().
			statusCode(400).
			body("supplementalData.errorMap.cassetteComponents.'1'.componentSymbol", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	private String endpoint(Long id, String list) {
		return "/api/cassette/" + id + "/" + list;
	}

	private Response get(String list) {
		return RestAssured.given().when().get(endpoint(cassetteId, list));
	}

	private Response put(String list, Object... rows) {
		return RestAssured.given().
			contentType("application/json").
			body(rows).
			when().
			put(endpoint(cassetteId, list));
	}

	private List<Long> storedIds(String list) {
		return storedIds(cassetteId, list);
	}

	private List<Long> storedIds(Long id, String list) {
		List<Long> ids = RestAssured.given().when().get(endpoint(id, list)).then().statusCode(200).extract().jsonPath().getList("entities.id", Long.class);
		return ids == null ? List.of() : ids;
	}

	private String row(Response response, String objectId, String objectField) {
		return "entities.find { it." + objectField + ".primaryExternalId == '" + objectId + "' }";
	}

	private CassetteTransgenicToolAssociation toolAssociation(Long id, TransgenicTool tool, VocabularyTerm relation) {
		CassetteTransgenicToolAssociation association = new CassetteTransgenicToolAssociation();
		association.setId(id);
		association.setCassetteTransgenicToolAssociationObject(tool);
		association.setRelation(relation);
		return association;
	}

	private void assertHoldsOnly(String list, Long id) {
		get(list).then().statusCode(200).body("entities", hasSize(1)).body("entities[0].id", is(id.intValue()));
	}
}
