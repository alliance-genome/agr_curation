package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSynonymSlotAnnotation;
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
@DisplayName("0319 - GeneSubResourceITCase")
@Order(319)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0319_GeneSubResourceITCase extends BaseITCase {

	private static final String GENE = "GENE:6782Sub0001";
	private static final String OTHER_GENE = "GENE:6782Sub0002";
	private static final String GCRP_GENE = "GENE:6782Sub0003";
	private static final String EVIDENCE = "AGRKB:6782000001";
	private static final String EVIDENCE_XREF = "PMID:6782000001";
	private static final String FIRST_NOTE = "6782 note 1";
	private static final String SECOND_NOTE = "6782 note 2";

	/**
	 * One of the gene's slot annotation lists.
	 *
	 * @param path the list's sub-resource path under the gene
	 * @param field the field the list's errors are keyed under
	 * @param requiredField the entry field a missing entry is reported against
	 * @param valueKey the JSON key, within a row, that tells the two sample rows apart
	 * @param values each sample row's value under valueKey
	 * @param build builds sample row 0 or 1
	 * @param blank builds a row with none of its fields set
	 */
	private record SlotList(String path, String field, String requiredField, String valueKey, List<String> values, IntFunction<SlotAnnotation> build, Supplier<SlotAnnotation> blank) {
	}

	private Long geneId;
	private Long otherGeneId;
	private Reference evidence;
	private VocabularyTerm commentType;
	private VocabularyTerm nonGeneNoteType;
	private ResourceDescriptorPage crossReferencePage;
	private List<SlotList> slotLists;
	private final Map<String, Long> firstIds = new HashMap<>();
	private final Map<String, Long> secondIds = new HashMap<>();
	private Long firstNoteId;

	private void loadRequiredEntities() {
		VocabularyTerm symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
		VocabularyTerm systematicNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "systematic_name");
		Vocabulary noteTypeVocabulary = getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY);
		commentType = getVocabularyTerm(noteTypeVocabulary, "comment");
		// A note type term outside the gene note types set.
		nonGeneNoteType = createVocabularyTerm(noteTypeVocabulary, "6782_non_gene_note_type", false);
		evidence = createReference(EVIDENCE, EVIDENCE_XREF, false);
		crossReferencePage = createResourceDescriptorPage("default", "http://test.org/[%s]", createResourceDescriptor("GENESUBXR"));
		geneId = createGene(GENE, "NCBITaxon:6239", symbolNameType, false).getId();
		otherGeneId = createGene(OTHER_GENE, "NCBITaxon:6239", symbolNameType, false).getId();

		List<String> synonyms = List.of("6782 synonym 1", "6782 synonym 2");
		List<String> secondaryIds = List.of("TEST:6782Secondary1", "TEST:6782Secondary2");

		slotLists = List.of(
			new SlotList("synonyms", "geneSynonyms", "displayText", "displayText", synonyms, variant -> {
				GeneSynonymSlotAnnotation synonym = new GeneSynonymSlotAnnotation();
				synonym.setDisplayText(synonyms.get(variant));
				synonym.setFormatText(synonyms.get(variant));
				synonym.setNameType(systematicNameType);
				return synonym;
			}, GeneSynonymSlotAnnotation::new),
			new SlotList("secondaryids", "geneSecondaryIds", "secondaryId", "secondaryId", secondaryIds, variant -> {
				GeneSecondaryIdSlotAnnotation secondaryId = new GeneSecondaryIdSlotAnnotation();
				secondaryId.setSecondaryId(secondaryIds.get(variant));
				return secondaryId;
			}, GeneSecondaryIdSlotAnnotation::new)
		);
	}

	@Test
	@Order(1)
	public void replaceCreatesSlotRows() {
		loadRequiredEntities();

		for (SlotList slotList : slotLists) {
			// createGene leaves the gene with none, and NON_EMPTY drops the key rather than sending [].
			RestAssured.given().
				when().
				get(endpoint(geneId, slotList.path())).
				then().
				statusCode(200).
				body("entities", nullValue());

			SlotAnnotation withEvidence = slotList.build().apply(0);
			withEvidence.setEvidence(List.<InformationContentEntity>of(evidence));

			// The PUT response is serialized after the write transaction closes, so it is asserted directly.
			Response response = put(geneId, slotList.path(), withEvidence, slotList.build().apply(1)).
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
				get(endpoint(geneId, slotList.path())).
				then().
				statusCode(200).
				body("entities", hasSize(2)).
				extract().response();
			assertEvidence(getResponse, slotList);
		}
	}

	@Test
	@Order(2)
	public void replaceUpdatesSlotRowInPlace() {
		for (SlotList slotList : slotLists) {
			put(geneId, slotList.path(), row(slotList, 0, firstId(slotList)), row(slotList, 1, secondId(slotList))).
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
	public void replaceDeletesOmittedSlotRow() {
		for (SlotList slotList : slotLists) {
			put(geneId, slotList.path(), row(slotList, 0, firstId(slotList))).
				then().
				statusCode(200).
				body("entities", hasSize(1)).
				body("entities[0].id", is(firstId(slotList).intValue()));

			assertHoldsOnly(geneId, slotList.path(), firstId(slotList));
		}
	}

	@Test
	@Order(4)
	public void slotRowErrorsAreKeyedByIndex() {
		for (SlotList slotList : slotLists) {
			String errors = "supplementalData.errorMap." + slotList.field();
			put(geneId, slotList.path(), row(slotList, 0, firstId(slotList)), slotList.blank().get(), null).
				then().
				statusCode(400).
				body("errorMessages." + slotList.field(), notNullValue()).
				body(errors + ".'1'." + slotList.requiredField(), is(ValidationConstants.REQUIRED_MESSAGE)).
				body(errors + ".'2'." + slotList.requiredField(), is(ValidationConstants.REQUIRED_MESSAGE));

			put(geneId, slotList.path(), row(slotList, 0, firstId(slotList)), row(slotList, 1, -1L)).
				then().
				statusCode(400).
				body(errors + ".'1'.id", is(ValidationConstants.INVALID_MESSAGE));

			put(geneId, slotList.path(), row(slotList, 0, firstId(slotList)), row(slotList, 1, firstId(slotList))).
				then().
				statusCode(400).
				body(errors + ".'1'.id", is(ValidationConstants.DUPLICATE_MESSAGE));

			assertHoldsOnly(geneId, slotList.path(), firstId(slotList));
		}
	}

	@Test
	@Order(5)
	public void anotherGenesSlotRowIsRejected() {
		for (SlotList slotList : slotLists) {
			Long otherRowId = idOf(put(otherGeneId, slotList.path(), slotList.build().apply(1)).then().statusCode(200).extract().response(), slotList, 1);

			put(geneId, slotList.path(), row(slotList, 0, firstId(slotList)), row(slotList, 1, otherRowId)).
				then().
				statusCode(400).
				body("supplementalData.errorMap." + slotList.field() + ".'1'.id", is(ValidationConstants.INVALID_MESSAGE));

			assertHoldsOnly(geneId, slotList.path(), firstId(slotList));
			assertHoldsOnly(otherGeneId, slotList.path(), otherRowId);
		}
	}

	@Test
	@Order(6)
	public void emptyListClearsSlotRows() {
		for (SlotList slotList : slotLists) {
			put(geneId, slotList.path()).
				then().
				statusCode(200).
				body("entities", nullValue());

			RestAssured.given().
				when().
				get(endpoint(geneId, slotList.path())).
				then().
				statusCode(200).
				body("entities", nullValue());

			RestAssured.given().
				contentType("application/json").
				body("null").
				when().
				put(endpoint(geneId, slotList.path())).
				then().
				statusCode(400).
				body("errorMessages." + slotList.field(), is(ValidationConstants.REQUIRED_MESSAGE));
		}
	}

	@Test
	@Order(7)
	public void replaceCreatesNotes() {
		Response response = put(geneId, "relatednotes", createNote(commentType, FIRST_NOTE, false, false, evidence), createNote(commentType, SECOND_NOTE, false, false, null)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body("entities.freeText", containsInAnyOrder(FIRST_NOTE, SECOND_NOTE)).
			body(notePath(FIRST_NOTE) + ".references[0].curie", is(EVIDENCE)).
			body(notePath(FIRST_NOTE) + ".references[0].crossReferences[0].referencedCurie", is(EVIDENCE_XREF)).
			extract().response();
		firstNoteId = response.jsonPath().getLong(notePath(FIRST_NOTE) + ".id");

		RestAssured.given().
			when().
			get(endpoint(geneId, "relatednotes")).
			then().
			statusCode(200).
			body("entities", hasSize(2));
	}

	@Test
	@Order(8)
	public void noteErrorsAreKeyedByIndex() {
		put(geneId, "relatednotes", buildNote(firstNoteId, commentType, FIRST_NOTE), buildNote(null, nonGeneNoteType, SECOND_NOTE)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.relatedNotes.'1'.noteType", is(ValidationConstants.INVALID_MESSAGE));

		put(geneId, "relatednotes", buildNote(firstNoteId, commentType, FIRST_NOTE), buildNote(null, commentType, SECOND_NOTE), buildNote(null, commentType, SECOND_NOTE)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.relatedNotes.'2'.freeText", startsWith(ValidationConstants.DUPLICATE_MESSAGE + " ("));

		Long otherNoteId = put(otherGeneId, "relatednotes", buildNote(null, commentType, SECOND_NOTE)).
			then().
			statusCode(200).
			extract().jsonPath().getLong("entities[0].id");
		put(geneId, "relatednotes", buildNote(firstNoteId, commentType, FIRST_NOTE), buildNote(otherNoteId, commentType, SECOND_NOTE)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.relatedNotes.'1'.id", is(ValidationConstants.INVALID_MESSAGE));

		RestAssured.given().
			when().
			get(endpoint(geneId, "relatednotes")).
			then().
			statusCode(200).
			body("entities", hasSize(2));
		assertHoldsOnly(otherGeneId, "relatednotes", otherNoteId);
	}

	@Test
	@Order(9)
	public void emptyListClearsNotes() {
		put(geneId, "relatednotes").
			then().
			statusCode(200).
			body("entities", nullValue());

		RestAssured.given().
			when().
			get(endpoint(geneId, "relatednotes")).
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	@Test
	@Order(10)
	public void replaceAddsAndRemovesCrossReferences() {
		Response response = put(geneId, "crossreferences", buildCrossReference("GENESUBXR:0001"), buildCrossReference("GENESUBXR:0002")).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body("entities.referencedCurie", containsInAnyOrder("GENESUBXR:0001", "GENESUBXR:0002")).
			body("entities[0].resourceDescriptorPage.name", is("default")).
			extract().response();
		Long keptId = response.jsonPath().getLong("entities.find { it.referencedCurie == 'GENESUBXR:0001' }.id");

		CrossReference kept = buildCrossReference("GENESUBXR:0001");
		kept.setId(keptId);
		put(geneId, "crossreferences", kept).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(keptId.intValue()));

		// The list is the gene's own, so the gene as a whole carries it.
		RestAssured.given().
			contentType("application/json").
			body("{\"primaryExternalId\": \"" + GENE + "\"}").
			when().
			post("/api/gene/find?limit=1&page=0").
			then().
			statusCode(200).
			body("results[0].crossReferences", hasSize(1)).
			body("results[0].crossReferences[0].referencedCurie", is("GENESUBXR:0001"));
	}

	@Test
	@Order(11)
	public void unknownGeneIsRejected() {
		for (String path : List.of("synonyms", "secondaryids", "relatednotes", "crossreferences")) {
			RestAssured.given().
				when().
				get(endpoint(-1L, path)).
				then().
				statusCode(400).
				body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));

			put(-1L, path).
				then().
				statusCode(400).
				body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));
		}
	}

	@Test
	@Order(12)
	public void crossReferenceMatchingTheGcrpCrossReferenceIsRejected() {
		VocabularyTerm symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolNameType);
		symbol.setDisplayText(GCRP_GENE);
		symbol.setFormatText(GCRP_GENE);
		SOTerm geneType = getSoTerm("SO:0001217");

		Gene gene = new Gene();
		gene.setPrimaryExternalId(GCRP_GENE);
		gene.setTaxon(getNCBITaxonTerm("NCBITaxon:6239"));
		gene.setGeneSymbol(symbol);
		gene.setGeneType(geneType);
		gene.setGcrpCrossReference(buildCrossReference("GENESUBXR:GCRP0001"));
		Long gcrpGeneId = RestAssured.given().
			contentType("application/json").
			body(gene).
			when().
			post("/api/gene").
			then().
			statusCode(200).
			extract().jsonPath().getLong("entity.id");

		// The GCRP cross reference is held on its own, so the gene's cross references may not repeat it.
		put(gcrpGeneId, "crossreferences", buildCrossReference("GENESUBXR:0003"), buildCrossReference("GENESUBXR:GCRP0001")).
			then().
			statusCode(400).
			body("supplementalData.errorMap.crossReferences.'1'.referencedCurie", startsWith(ValidationConstants.DUPLICATE_MESSAGE));

		RestAssured.given().
			when().
			get(endpoint(gcrpGeneId, "crossreferences")).
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	private String endpoint(Long id, String path) {
		return "/api/gene/" + id + "/" + path;
	}

	private Response put(Long id, String path, Object... rows) {
		return RestAssured.given().
			contentType("application/json").
			body(rows).
			when().
			put(endpoint(id, path));
	}

	private SlotAnnotation row(SlotList slotList, int variant, Long id) {
		SlotAnnotation row = slotList.build().apply(variant);
		row.setId(id);
		return row;
	}

	private Note buildNote(Long id, VocabularyTerm noteType, String text) {
		Note note = createNote(noteType, text, false, false, null);
		note.setId(id);
		return note;
	}

	private CrossReference buildCrossReference(String referencedCurie) {
		CrossReference crossReference = new CrossReference();
		crossReference.setReferencedCurie(referencedCurie);
		crossReference.setDisplayName(referencedCurie);
		crossReference.setResourceDescriptorPage(crossReferencePage);
		return crossReference;
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

	private String notePath(String text) {
		return "entities.find { it.freeText == '" + text + "' }";
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

	private void assertHoldsOnly(Long id, String path, Long rowId) {
		RestAssured.given().
			when().
			get(endpoint(id, path)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(rowId.intValue()));
	}
}
