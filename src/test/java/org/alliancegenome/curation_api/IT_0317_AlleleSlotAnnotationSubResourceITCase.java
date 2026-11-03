package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleNomenclatureEventSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
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

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("0317 - AlleleSlotAnnotationSubResourceITCase")
@Order(317)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0317_AlleleSlotAnnotationSubResourceITCase extends BaseITCase {

	private static final String ALLELE = "Allele:6687Slot0001";
	private static final String OTHER_ALLELE = "Allele:6687Slot0002";
	private static final String EVIDENCE = "AGRKB:6687000001";
	private static final String EVIDENCE_XREF = "PMID:6687000001";

	/**
	 * One of the allele's slot annotation lists.
	 *
	 * @param path the list's sub-resource path under the allele
	 * @param field the field the list's errors are keyed under
	 * @param requiredField the entry field a missing entry is reported against
	 * @param valueKey the JSON key, within a row, that tells the two sample rows apart
	 * @param values each sample row's value under valueKey
	 * @param build builds sample row 0 or 1
	 * @param blank builds a row with none of its fields set
	 */
	private record SlotList(String path, String field, String requiredField, String valueKey, List<String> values, IntFunction<SlotAnnotation> build, Supplier<SlotAnnotation> blank) {
	}

	private Long alleleId;
	private Long otherAlleleId;
	private Reference evidence;
	private List<SlotList> slotLists;
	private final Map<String, Long> firstIds = new HashMap<>();
	private final Map<String, Long> secondIds = new HashMap<>();

	private void loadRequiredEntities() {
		VocabularyTerm symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
		VocabularyTerm systematicNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "systematic_name");
		Vocabulary nomenclatureEventVocabulary = getVocabulary(VocabularyConstants.ALLELE_NOMENCLATURE_EVENT_VOCABULARY);
		List<VocabularyTerm> nomenclatureEvents = List.of(getVocabularyTerm(nomenclatureEventVocabulary, "data_merged"), getVocabularyTerm(nomenclatureEventVocabulary, "symbol_updated"));
		Vocabulary functionalImpactVocabulary = getVocabulary(VocabularyConstants.ALLELE_FUNCTIONAL_IMPACT_VOCABULARY);
		List<VocabularyTerm> functionalImpacts = List.of(getVocabularyTerm(functionalImpactVocabulary, "hypermorphic"), getVocabularyTerm(functionalImpactVocabulary, "neomorphic"));
		Vocabulary inheritanceModeVocabulary = getVocabulary(VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY);
		List<VocabularyTerm> inheritanceModes = List.of(getVocabularyTerm(inheritanceModeVocabulary, "dominant"), getVocabularyTerm(inheritanceModeVocabulary, "recessive"));
		List<SOTerm> soTerms = List.of(createSoTerm("SO:6687001", "6687 mutation type 1", false), createSoTerm("SO:6687002", "6687 mutation type 2", false));
		MPTerm mpTerm = createMpTerm("MP:6687001", "6687 phenotype", false);
		evidence = createReference(EVIDENCE, EVIDENCE_XREF, false);
		alleleId = createAllele(ALLELE, "NCBITaxon:6239", symbolNameType, false).getId();
		otherAlleleId = createAllele(OTHER_ALLELE, "NCBITaxon:6239", symbolNameType, false).getId();

		List<String> synonyms = List.of("6687 synonym 1", "6687 synonym 2");
		List<String> secondaryIds = List.of("TEST:6687Secondary1", "TEST:6687Secondary2");

		slotLists = List.of(
			new SlotList("synonyms", "alleleSynonyms", "displayText", "displayText", synonyms, variant -> {
				AlleleSynonymSlotAnnotation synonym = new AlleleSynonymSlotAnnotation();
				synonym.setDisplayText(synonyms.get(variant));
				synonym.setFormatText(synonyms.get(variant));
				synonym.setNameType(systematicNameType);
				return synonym;
			}, AlleleSynonymSlotAnnotation::new),
			new SlotList("secondaryids", "alleleSecondaryIds", "secondaryId", "secondaryId", secondaryIds, variant -> {
				AlleleSecondaryIdSlotAnnotation secondaryId = new AlleleSecondaryIdSlotAnnotation();
				secondaryId.setSecondaryId(secondaryIds.get(variant));
				return secondaryId;
			}, AlleleSecondaryIdSlotAnnotation::new),
			new SlotList("nomenclatureevents", "alleleNomenclatureEvents", "nomenclatureEvent", "nomenclatureEvent.name", names(nomenclatureEvents), variant -> {
				AlleleNomenclatureEventSlotAnnotation event = new AlleleNomenclatureEventSlotAnnotation();
				event.setNomenclatureEvent(nomenclatureEvents.get(variant));
				return event;
			}, AlleleNomenclatureEventSlotAnnotation::new),
			new SlotList("mutationtypes", "alleleMutationTypes", "mutationTypes", "mutationTypes[0].curie", List.of(soTerms.get(0).getCurie(), soTerms.get(1).getCurie()), variant -> {
				AlleleMutationTypeSlotAnnotation mutationType = new AlleleMutationTypeSlotAnnotation();
				mutationType.setMutationTypes(List.of(soTerms.get(variant)));
				return mutationType;
			}, AlleleMutationTypeSlotAnnotation::new),
			new SlotList("functionalimpacts", "alleleFunctionalImpacts", "functionalImpacts", "functionalImpacts[0].name", names(functionalImpacts), variant -> {
				AlleleFunctionalImpactSlotAnnotation functionalImpact = new AlleleFunctionalImpactSlotAnnotation();
				functionalImpact.setFunctionalImpacts(List.of(functionalImpacts.get(variant)));
				functionalImpact.setPhenotypeTerm(mpTerm);
				functionalImpact.setPhenotypeStatement("6687 statement");
				return functionalImpact;
			}, AlleleFunctionalImpactSlotAnnotation::new),
			new SlotList("inheritancemodes", "alleleInheritanceModes", "inheritanceMode", "inheritanceMode.name", names(inheritanceModes), variant -> {
				AlleleInheritanceModeSlotAnnotation inheritanceMode = new AlleleInheritanceModeSlotAnnotation();
				inheritanceMode.setInheritanceMode(inheritanceModes.get(variant));
				inheritanceMode.setPhenotypeTerm(mpTerm);
				inheritanceMode.setPhenotypeStatement("6687 statement");
				return inheritanceMode;
			}, AlleleInheritanceModeSlotAnnotation::new)
		);
	}

	@Test
	@Order(1)
	public void replaceCreatesRows() {
		loadRequiredEntities();

		for (SlotList slotList : slotLists) {
			// createAllele leaves the allele with none, and NON_EMPTY drops the key rather than sending [].
			RestAssured.given().
				when().
				get(endpoint(alleleId, slotList)).
				then().
				statusCode(200).
				body("entities", nullValue());

			SlotAnnotation withEvidence = slotList.build().apply(0);
			withEvidence.setEvidence(List.<InformationContentEntity>of(evidence));

			// The PUT response is serialized after the write transaction closes, so it is asserted directly.
			Response response = put(alleleId, slotList, withEvidence, slotList.build().apply(1)).
				then().
				statusCode(200).
				body("entities", hasSize(2)).
				body("entities.collect { it." + slotList.valueKey() + " }", containsInAnyOrder(slotList.values().toArray())).
				extract().response();

			assertEvidence(response, slotList);
			firstIds.put(slotList.path(), idOf(response, slotList, 0));
			secondIds.put(slotList.path(), idOf(response, slotList, 1));

			Response getResponse = RestAssured.given().
				when().
				get(endpoint(alleleId, slotList)).
				then().
				statusCode(200).
				body("entities", hasSize(2)).
				extract().response();
			assertEvidence(getResponse, slotList);
		}
	}

	@Test
	@Order(2)
	public void replaceUpdatesRowInPlace() {
		for (SlotList slotList : slotLists) {
			put(alleleId, slotList, row(slotList, 0, firstId(slotList)), row(slotList, 1, secondId(slotList))).
				then().
				statusCode(200).
				body("entities", hasSize(2)).
				body(rowPath(slotList, 0) + ".id", is(firstId(slotList).intValue())).
				body(rowPath(slotList, 0) + ".evidence", nullValue()).
				body(rowPath(slotList, 1) + ".id", is(secondId(slotList).intValue()));
		}
	}

	@Test
	@Order(3)
	public void replaceDeletesOmittedRow() {
		for (SlotList slotList : slotLists) {
			put(alleleId, slotList, row(slotList, 0, firstId(slotList))).
				then().
				statusCode(200).
				body("entities", hasSize(1)).
				body("entities[0].id", is(firstId(slotList).intValue()));

			assertHoldsOnly(alleleId, slotList, firstId(slotList));
		}
	}

	@Test
	@Order(4)
	public void missingEntryAndFieldAreRejectedByIndex() {
		for (SlotList slotList : slotLists) {
			String errors = "supplementalData.errorMap." + slotList.field();
			put(alleleId, slotList, row(slotList, 0, firstId(slotList)), slotList.blank().get(), null).
				then().
				statusCode(400).
				body("errorMessages." + slotList.field(), notNullValue()).
				body(errors + ".'1'." + slotList.requiredField(), is(ValidationConstants.REQUIRED_MESSAGE)).
				body(errors + ".'2'." + slotList.requiredField(), is(ValidationConstants.REQUIRED_MESSAGE));

			assertHoldsOnly(alleleId, slotList, firstId(slotList));
		}
	}

	@Test
	@Order(5)
	public void unknownIdIsRejectedByIndex() {
		for (SlotList slotList : slotLists) {
			put(alleleId, slotList, row(slotList, 0, firstId(slotList)), row(slotList, 1, -1L)).
				then().
				statusCode(400).
				body("supplementalData.errorMap." + slotList.field() + ".'1'.id", is(ValidationConstants.INVALID_MESSAGE));

			assertHoldsOnly(alleleId, slotList, firstId(slotList));
		}
	}

	@Test
	@Order(6)
	public void heldIdNamedTwiceIsRejected() {
		for (SlotList slotList : slotLists) {
			put(alleleId, slotList, row(slotList, 0, firstId(slotList)), row(slotList, 1, firstId(slotList))).
				then().
				statusCode(400).
				body("supplementalData.errorMap." + slotList.field() + ".'1'.id", is(ValidationConstants.DUPLICATE_MESSAGE));

			assertHoldsOnly(alleleId, slotList, firstId(slotList));
		}
	}

	@Test
	@Order(7)
	public void anotherAllelesRowIsRejected() {
		for (SlotList slotList : slotLists) {
			Long otherRowId = idOf(put(otherAlleleId, slotList, slotList.build().apply(1)).then().statusCode(200).extract().response(), slotList, 1);

			put(alleleId, slotList, row(slotList, 0, firstId(slotList)), row(slotList, 1, otherRowId)).
				then().
				statusCode(400).
				body("supplementalData.errorMap." + slotList.field() + ".'1'.id", is(ValidationConstants.INVALID_MESSAGE));

			assertHoldsOnly(alleleId, slotList, firstId(slotList));
			assertHoldsOnly(otherAlleleId, slotList, otherRowId);
		}
	}

	@Test
	@Order(8)
	public void emptyListClearsRows() {
		for (SlotList slotList : slotLists) {
			put(alleleId, slotList).
				then().
				statusCode(200).
				body("entities", nullValue());

			RestAssured.given().
				when().
				get(endpoint(alleleId, slotList)).
				then().
				statusCode(200).
				body("entities", nullValue());
		}
	}

	@Test
	@Order(9)
	public void missingBodyIsRejected() {
		for (SlotList slotList : slotLists) {
			RestAssured.given().
				contentType("application/json").
				body("null").
				when().
				put(endpoint(alleleId, slotList)).
				then().
				statusCode(400).
				body("errorMessages." + slotList.field(), is(ValidationConstants.REQUIRED_MESSAGE));
		}
	}

	@Test
	@Order(10)
	public void unknownAlleleIsRejected() {
		for (SlotList slotList : slotLists) {
			RestAssured.given().
				when().
				get(endpoint(-1L, slotList)).
				then().
				statusCode(400).
				body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));

			put(-1L, slotList, slotList.build().apply(0)).
				then().
				statusCode(400).
				body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));
		}
	}

	private static List<String> names(List<VocabularyTerm> terms) {
		return terms.stream().map(VocabularyTerm::getName).toList();
	}

	private String endpoint(Long id, SlotList slotList) {
		return "/api/allele/" + id + "/" + slotList.path();
	}

	private Response put(Long id, SlotList slotList, SlotAnnotation... rows) {
		return RestAssured.given().
			contentType("application/json").
			body(rows).
			when().
			put(endpoint(id, slotList));
	}

	private SlotAnnotation row(SlotList slotList, int variant, Long id) {
		SlotAnnotation row = slotList.build().apply(variant);
		row.setId(id);
		return row;
	}

	private Long firstId(SlotList slotList) {
		return firstIds.get(slotList.path());
	}

	private Long secondId(SlotList slotList) {
		return secondIds.get(slotList.path());
	}

	private String rowPath(SlotList slotList, int variant) {
		return "entities.find { it." + slotList.valueKey() + " == '" + slotList.values().get(variant) + "' }";
	}

	private Long idOf(Response response, SlotList slotList, int variant) {
		return response.jsonPath().getLong(rowPath(slotList, variant) + ".id");
	}

	private void assertEvidence(Response response, SlotList slotList) {
		String row = rowPath(slotList, 0);
		response.then().
			body(row + ".id", notNullValue()).
			body(row + ".evidence[0].curie", is(EVIDENCE)).
			body(row + ".evidence[0].crossReferences[0].referencedCurie", is(EVIDENCE_XREF)).
			body(rowPath(slotList, 1) + ".evidence", nullValue());
	}

	private void assertHoldsOnly(Long id, SlotList slotList, Long rowId) {
		RestAssured.given().
			when().
			get(endpoint(id, slotList)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(rowId.intValue()));
	}
}
