package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSymbolSlotAnnotation;
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
@DisplayName("0316 - AlleleGeneAssociationSubResourceITCase")
@Order(316)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0316_AlleleGeneAssociationSubResourceITCase extends BaseITCase {

	private static final String ALLELE = "Allele:6628Aga0001";
	private static final String OTHER_ALLELE = "Allele:6628Aga0002";
	private static final String CREATED_ALLELE = "Allele:6628Aga0003";
	private static final String EVIDENCE = "AGRKB:6628000001";
	private static final String EVIDENCE_XREF = "PMID:6628000001";
	private static final String NOTE_REFERENCE = "AGRKB:6628000002";
	private static final String NOTE_REFERENCE_XREF = "PMID:6628000002";

	private Long alleleId;
	private Long otherAlleleId;
	private Gene gene;
	private Gene gene2;
	private Gene gene3;
	private VocabularyTerm isAlleleOf;
	private VocabularyTerm duplication;
	private VocabularyTerm contains;
	private VocabularyTerm noteType;
	private ECOTerm ecoTerm;
	private ECOTerm ecoTerm2;
	private Reference evidence;
	private Reference noteReference;

	private final OffsetDateTime dateCreated = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");

	private Long isAlleleOfId;
	private Long duplicationId;

	private void loadRequiredEntities() {
		VocabularyTerm symbolNameType = getVocabularyTerm(getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY), "nomenclature_symbol");
		Vocabulary relationVocabulary = getVocabulary(VocabularyConstants.ALLELE_RELATION_VOCABULARY);
		isAlleleOf = getVocabularyTerm(relationVocabulary, "is_allele_of");
		duplication = getVocabularyTerm(relationVocabulary, "duplication");
		contains = getVocabularyTerm(relationVocabulary, "contains");
		noteType = getVocabularyTerm(getVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY), "comment");
		ecoTerm = createEcoTerm("ECO:6628AGA01", "6628 evidence code", false, true);
		ecoTerm2 = createEcoTerm("ECO:6628AGA02", "6628 evidence code 2", false, true);
		evidence = createReference(EVIDENCE, EVIDENCE_XREF, false);
		noteReference = createReference(NOTE_REFERENCE, NOTE_REFERENCE_XREF, false);
		gene = createGene("TEST:6628Gene1", "NCBITaxon:6239", symbolNameType, false);
		gene2 = createGene("TEST:6628Gene2", "NCBITaxon:6239", symbolNameType, false);
		gene3 = createGene("TEST:6628Gene3", "NCBITaxon:6239", symbolNameType, false);
		alleleId = createAllele(ALLELE, "NCBITaxon:6239", symbolNameType, false).getId();
		otherAlleleId = createAllele(OTHER_ALLELE, "NCBITaxon:6239", symbolNameType, false).getId();
	}

	@Test
	@Order(1)
	public void replaceCreatesAssociations() {
		loadRequiredEntities();

		// createAllele leaves the allele with none, and NON_EMPTY drops the key rather than sending [].
		RestAssured.given().
			when().
			get(endpoint(alleleId)).
			then().
			statusCode(200).
			body("entities", nullValue());

		AlleleGeneAssociation annotated = buildAssociation(null, gene, isAlleleOf);
		annotated.setEvidenceCode(ecoTerm);
		annotated.setEvidence(List.of(evidence));
		annotated.setRelatedNote(createNote(noteType, "6628 note", false, false, noteReference));
		annotated.setDateCreated(dateCreated);

		// The PUT response is serialized after the write transaction closes, so it is asserted directly.
		Response response = put(alleleId, annotated, buildAssociation(null, gene2, duplication)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body("entities.alleleGeneAssociationObject.primaryExternalId", containsInAnyOrder(gene.getPrimaryExternalId(), gene2.getPrimaryExternalId())).
			extract().response();

		assertAnnotatedAssociation(response);
		isAlleleOfId = idOf(response, gene);
		duplicationId = idOf(response, gene2);

		Response getResponse = RestAssured.given().
			when().
			get(endpoint(alleleId)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			extract().response();
		assertAnnotatedAssociation(getResponse);
	}

	@Test
	@Order(2)
	public void replaceUpdatesAssociationInPlace() {
		AlleleGeneAssociation edited = buildAssociation(isAlleleOfId, gene, isAlleleOf);
		edited.setEvidenceCode(ecoTerm2);
		edited.setDateCreated(dateCreated);

		Response response = put(alleleId, edited, buildAssociation(duplicationId, gene2, duplication)).
			then().
			statusCode(200).
			body("entities", hasSize(2)).
			body(rowPath(gene) + ".id", is(isAlleleOfId.intValue())).
			body(rowPath(gene) + ".evidenceCode.curie", is(ecoTerm2.getCurie())).
			body(rowPath(gene) + ".evidence", nullValue()).
			body(rowPath(gene) + ".relatedNote", nullValue()).
			body(rowPath(gene2) + ".id", is(duplicationId.intValue())).
			extract().response();

		assertEquals(dateCreated.toInstant(), OffsetDateTime.parse(response.jsonPath().getString(rowPath(gene) + ".dateCreated")).toInstant());
	}

	@Test
	@Order(3)
	public void replaceDeletesOmittedAssociation() {
		put(alleleId, buildAssociation(isAlleleOfId, gene, isAlleleOf)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(isAlleleOfId.intValue()));

		RestAssured.given().
			when().
			get("/api/allelegeneassociation/" + duplicationId).
			then().
			statusCode(200).
			body("entity", nullValue());
	}

	@Test
	@Order(4)
	public void missingGeneAndRelationAreRejectedByIndex() {
		AlleleGeneAssociation blank = new AlleleGeneAssociation();

		put(alleleId, buildAssociation(isAlleleOfId, gene, isAlleleOf), blank, null).
			then().
			statusCode(400).
			body("errorMessages.alleleGeneAssociations", notNullValue()).
			body("supplementalData.errorMap.alleleGeneAssociations.'1'.alleleGeneAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("supplementalData.errorMap.alleleGeneAssociations.'1'.relation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("supplementalData.errorMap.alleleGeneAssociations.'2'.alleleGeneAssociationObject", is(ValidationConstants.REQUIRED_MESSAGE));

		assertHoldsOnly(alleleId, isAlleleOfId);
	}

	@Test
	@Order(5)
	public void secondIsAlleleOfIsRejected() {
		put(alleleId, buildAssociation(isAlleleOfId, gene, isAlleleOf), buildAssociation(null, gene2, isAlleleOf)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.alleleGeneAssociations.'1'.relation",
				is(ValidationConstants.DUPLICATE_RELATION_PREFIX + " " + VocabularyConstants.ALLELE_OF_VOCABULARY_TERM));

		assertHoldsOnly(alleleId, isAlleleOfId);
	}

	@Test
	@Order(6)
	public void duplicateAssociationIsRejected() {
		put(alleleId, buildAssociation(isAlleleOfId, gene, isAlleleOf), buildAssociation(null, gene3, duplication), buildAssociation(null, gene3, duplication)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.alleleGeneAssociations.'2'.relation",
				startsWith(ValidationConstants.DUPLICATE_MESSAGE + " ("));

		assertHoldsOnly(alleleId, isAlleleOfId);
	}

	@Test
	@Order(7)
	public void unknownIdIsRejectedByIndex() {
		put(alleleId, buildAssociation(isAlleleOfId, gene, isAlleleOf), buildAssociation(-1L, gene2, duplication)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.alleleGeneAssociations.'1'.id", is(ValidationConstants.INVALID_MESSAGE));

		assertHoldsOnly(alleleId, isAlleleOfId);
	}

	@Test
	@Order(8)
	public void heldIdNamedTwiceIsRejected() {
		put(alleleId, buildAssociation(isAlleleOfId, gene, isAlleleOf), buildAssociation(isAlleleOfId, gene2, duplication)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.alleleGeneAssociations.'1'.id", is(ValidationConstants.DUPLICATE_MESSAGE));

		assertHoldsOnly(alleleId, isAlleleOfId);
	}

	@Test
	@Order(9)
	public void anotherAllelesAssociationIsRejected() {
		Long otherAssociationId = idOf(
			put(otherAlleleId, buildAssociation(null, gene3, duplication)).then().statusCode(200).extract().response(),
			gene3);

		put(alleleId, buildAssociation(isAlleleOfId, gene, isAlleleOf), buildAssociation(otherAssociationId, gene3, duplication)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.alleleGeneAssociations.'1'.id", is(ValidationConstants.INVALID_MESSAGE));

		assertHoldsOnly(alleleId, isAlleleOfId);
		assertHoldsOnly(otherAlleleId, otherAssociationId);
	}

	@Test
	@Order(10)
	public void replaceRepointsAssociationToAnotherGene() {
		put(alleleId, buildAssociation(isAlleleOfId, gene2, isAlleleOf)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", is(isAlleleOfId.intValue())).
			body("entities[0].alleleGeneAssociationObject.primaryExternalId", is(gene2.getPrimaryExternalId()));
	}

	@Test
	@Order(11)
	public void replaceDeletesAndCreatesSameIdentity() {
		Long replacedId = isAlleleOfId;

		Response response = put(alleleId, buildAssociation(null, gene2, isAlleleOf)).
			then().
			statusCode(200).
			body("entities", hasSize(1)).
			body("entities[0].id", not(is(replacedId.intValue()))).
			extract().response();
		isAlleleOfId = idOf(response, gene2);

		RestAssured.given().
			when().
			get("/api/allelegeneassociation/" + replacedId).
			then().
			statusCode(200).
			body("entity", nullValue());
	}

	@Test
	@Order(12)
	public void emptyListDeletesAssociations() {
		RestAssured.given().
			contentType("application/json").
			body(new AlleleGeneAssociation[0]).
			when().
			put(endpoint(alleleId)).
			then().
			statusCode(200).
			body("entities", nullValue());

		RestAssured.given().
			when().
			get(endpoint(alleleId)).
			then().
			statusCode(200).
			body("entities", nullValue());

		RestAssured.given().
			when().
			get("/api/allelegeneassociation/" + isAlleleOfId).
			then().
			statusCode(200).
			body("entity", nullValue());
	}

	@Test
	@Order(13)
	public void missingBodyIsRejected() {
		RestAssured.given().
			contentType("application/json").
			body("null").
			when().
			put(endpoint(alleleId)).
			then().
			statusCode(400).
			body("errorMessages.alleleGeneAssociations", is(ValidationConstants.REQUIRED_MESSAGE));
	}

	@Test
	@Order(14)
	public void unknownAlleleIsRejected() {
		RestAssured.given().
			when().
			get(endpoint(-1L)).
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));

		put(-1L, buildAssociation(null, gene, isAlleleOf)).
			then().
			statusCode(400).
			body("errorMessages.id", is(ValidationConstants.INVALID_MESSAGE));
	}

	@Test
	@Order(15)
	public void relationOutsideTheAlleleGeneSetIsRejected() {
		// "contains" relates an allele to a construct, not a gene.
		put(alleleId, buildAssociation(null, gene, contains)).
			then().
			statusCode(400).
			body("supplementalData.errorMap.alleleGeneAssociations.'0'.relation", is(ValidationConstants.INVALID_MESSAGE));

		RestAssured.given().
			when().
			get(endpoint(alleleId)).
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	@Test
	@Order(16)
	public void alleleSavesIgnoreGeneAssociationsInTheirBody() {
		Long heldId = idOf(put(alleleId, buildAssociation(null, gene, isAlleleOf)).then().statusCode(200).extract().response(), gene);

		Allele allele = getAllele(ALLELE);
		allele.setAlleleGeneAssociations(List.of(buildAssociation(null, gene2, duplication)));
		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			put("/api/allele/updateDetail").
			then().
			statusCode(200);

		assertHoldsOnly(alleleId, heldId);

		// Built here rather than with createAllele, which sends no gene associations.
		Allele newAllele = new Allele();
		newAllele.setPrimaryExternalId(CREATED_ALLELE);
		newAllele.setTaxon(allele.getTaxon());
		newAllele.setAlleleSymbol(buildSymbol(allele));
		newAllele.setAlleleGeneAssociations(List.of(buildAssociation(null, gene2, duplication)));
		Long createdId = RestAssured.given().
			contentType("application/json").
			body(newAllele).
			when().
			post("/api/allele").
			then().
			statusCode(200).
			extract().jsonPath().getLong("entity.id");

		RestAssured.given().
			when().
			get(endpoint(createdId)).
			then().
			statusCode(200).
			body("entities", nullValue());
	}

	private AlleleSymbolSlotAnnotation buildSymbol(Allele source) {
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setNameType(source.getAlleleSymbol().getNameType());
		symbol.setDisplayText(CREATED_ALLELE);
		symbol.setFormatText(CREATED_ALLELE);
		return symbol;
	}

	private String endpoint(Long id) {
		return "/api/allele/" + id + "/gene-associations";
	}

	private Response put(Long id, AlleleGeneAssociation... associations) {
		return RestAssured.given().
			contentType("application/json").
			body(associations).
			when().
			put(endpoint(id));
	}

	private AlleleGeneAssociation buildAssociation(Long id, Gene object, VocabularyTerm relation) {
		AlleleGeneAssociation association = new AlleleGeneAssociation();
		association.setId(id);
		association.setAlleleGeneAssociationObject(object);
		association.setRelation(relation);
		return association;
	}

	private String rowPath(Gene object) {
		return "entities.find { it.alleleGeneAssociationObject.primaryExternalId == '" + object.getPrimaryExternalId() + "' }";
	}

	private Long idOf(Response response, Gene object) {
		return response.jsonPath().getLong(rowPath(object) + ".id");
	}

	private void assertAnnotatedAssociation(Response response) {
		String row = rowPath(gene);
		response.then().
			body(row + ".id", notNullValue()).
			body(row + ".relation.name", is(isAlleleOf.getName())).
			body(row + ".evidenceCode.curie", is(ecoTerm.getCurie())).
			body(row + ".evidence[0].curie", is(EVIDENCE)).
			body(row + ".evidence[0].crossReferences[0].referencedCurie", is(EVIDENCE_XREF)).
			body(row + ".relatedNote.freeText", is("6628 note")).
			body(row + ".relatedNote.references[0].crossReferences[0].referencedCurie", is(NOTE_REFERENCE_XREF)).
			body(row + ".alleleAssociationSubject", nullValue());
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
