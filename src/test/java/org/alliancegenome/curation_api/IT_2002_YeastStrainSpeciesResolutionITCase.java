package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.constants.YeastStrainTaxonConstants;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

/**
 * SCRUM-6152 - all 13 SGD models must be reachable under the single "Saccharomyces cerevisiae"
 * species facet on the public site, not just the 7 that carry the canonical NCBITaxon:559292.
 *
 * The strain-level taxa have no row of their own in `species`, so before the fix the species
 * join in the search DAOs yielded a null species and those models produced no facet bucket.
 * These tests cover the query-time fallback that resolves them to the canonical species row,
 * for both the model and the allele search documents.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("2002 - YeastStrainSpeciesResolutionITCase")
@Order(2002)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_2002_YeastStrainSpeciesResolutionITCase extends BaseITCase {

	private static final String SCE_FULL_NAME = "Saccharomyces cerevisiae";
	private static final String SCE_ABBREVIATION = "Sce";
	private static final String MMU_FULL_NAME = "Mus musculus";
	private static final String MMU_ABBREVIATION = "Mmu";

	private static final String MOUSE_TAXON = "NCBITaxon:10090";
	// A real taxon that is neither curated in `species` nor in the yeast strain list.
	private static final String UNCURATED_TAXON = "NCBITaxon:7955";

	private static final String MODEL_SUMMARY_ENDPOINT = "/api/model/document/summary/byids";
	private static final String ALLELE_SUMMARY_ENDPOINT = "/api/allele/document/summary/byids";

	private VocabularyTerm fullNameType;
	private VocabularyTerm symbolNameType;
	private VocabularyTerm strainSubtype;
	private Organization dataProvider;
	private ResourceDescriptorPage resourceDescriptorPage;

	private void loadRequiredEntities() {
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		fullNameType = getVocabularyTerm(nameTypeVocabulary, "full_name");
		symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");

		Vocabulary subtypeVocabulary = getVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY);
		strainSubtype = getVocabularyTerm(subtypeVocabulary, "strain");

		dataProvider = getOrganization("SGD");
		ResourceDescriptor resourceDescriptor = createResourceDescriptor("YEASTTEST");
		resourceDescriptorPage = createResourceDescriptorPage("homepage", "http://example.com/yeast/[%s]", resourceDescriptor);

		// The IT database ships with no `species` rows: the v0.29.0.2 seed is guarded on
		// ncbitaxonterm already being populated, which it is not at migration time. Create the
		// two species rows these tests resolve against.
		createSpecies(YeastStrainTaxonConstants.CANONICAL_SCE_TAXON, SCE_FULL_NAME, SCE_ABBREVIATION, "SGD", 70);
		createSpecies(MOUSE_TAXON, MMU_FULL_NAME, MMU_ABBREVIATION, "MGI", 40);
	}

	// All six strain-level taxa, plus the canonical one, must resolve to the single curated
	// S. cerevisiae species row - this is what puts all 13 SGD models in one facet bucket.
	@Test
	@Order(1)
	public void testYeastStrainAgmsResolveToCanonicalSpecies() {
		loadRequiredEntities();

		Long canonical = createYeastAgm("YEASTAGM:S288C", YeastStrainTaxonConstants.CANONICAL_SCE_TAXON, "S288C");
		Long rm11 = createYeastAgm("YEASTAGM:RM11", "NCBITaxon:285006", "RM11-1a");
		Long sk1 = createYeastAgm("YEASTAGM:SK1", "NCBITaxon:580239", "SK1");
		Long w303 = createYeastAgm("YEASTAGM:W303", "NCBITaxon:580240", "W303");
		Long sigma = createYeastAgm("YEASTAGM:SIGMA", "NCBITaxon:658763", "Sigma1278b");
		Long fl100 = createYeastAgm("YEASTAGM:FL100", "NCBITaxon:947036", "FL100");
		Long root = createYeastAgm("YEASTAGM:ROOT", "NCBITaxon:4932", "Other");

		List<Long> ids = List.of(canonical, rm11, sk1, w303, sigma, fl100, root);

		RestAssured.given().
			contentType("application/json").
			body(ids).
			when().
			post(MODEL_SUMMARY_ENDPOINT).
			then().
			statusCode(200).
			body("totalResults", is(ids.size())).
			body("results.species", hasItems(SCE_FULL_NAME)).
			// every document, strain-level or not, carries the canonical species...
			body("results.findAll { it.species != '" + SCE_FULL_NAME + "' }.size()", is(0)).
			// ...and the strain name survives in nameKey, per the ticket's display requirement.
			body("results.nameKey", hasItems(
				"S288C (" + SCE_ABBREVIATION + ")",
				"RM11-1a (" + SCE_ABBREVIATION + ")",
				"SK1 (" + SCE_ABBREVIATION + ")",
				"W303 (" + SCE_ABBREVIATION + ")",
				"Sigma1278b (" + SCE_ABBREVIATION + ")",
				"FL100 (" + SCE_ABBREVIATION + ")",
				"Other (" + SCE_ABBREVIATION + ")"));
	}

	// A taxon with its own curated species row must keep resolving directly - the fallback
	// join must not divert any non-yeast model.
	@Test
	@Order(2)
	public void testNonYeastAgmSpeciesUnchanged() {
		Long mouseAgm = createYeastAgm("YEASTAGM:MOUSE", MOUSE_TAXON, "MouseModel");

		RestAssured.given().
			contentType("application/json").
			body(List.of(mouseAgm)).
			when().
			post(MODEL_SUMMARY_ENDPOINT).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].species", is(MMU_FULL_NAME)).
			body("results[0].nameKey", is("MouseModel (" + MMU_ABBREVIATION + ")"));
	}

	// A taxon that is neither curated nor a listed yeast strain must still yield a null
	// species: the fallback is scoped to the hardcoded SGD strain list only.
	@Test
	@Order(3)
	public void testUncuratedTaxonStillResolvesToNoSpecies() {
		Long uncuratedAgm = createYeastAgm("YEASTAGM:UNCURATED", UNCURATED_TAXON, "UncuratedModel");

		RestAssured.given().
			contentType("application/json").
			body(List.of(uncuratedAgm)).
			when().
			post(MODEL_SUMMARY_ENDPOINT).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].species", is(nullValue())).
			// with no species abbreviation there is no suffix to compose
			body("results[0].nameKey", is("UncuratedModel"));
	}

	// The same fallback applies to the allele search documents, which is what pulls the SGD
	// alleles sitting on NCBITaxon:4932 into the yeast species facet.
	@Test
	@Order(4)
	public void testYeastStrainAlleleResolvesToCanonicalSpecies() {
		Long rootAllele = createYeastAllele("YEASTALLELE:ROOT", "NCBITaxon:4932", "yeastAllele1");
		Long canonicalAllele = createYeastAllele("YEASTALLELE:S288C", YeastStrainTaxonConstants.CANONICAL_SCE_TAXON, "yeastAllele2");

		RestAssured.given().
			contentType("application/json").
			body(List.of(rootAllele, canonicalAllele)).
			when().
			post(ALLELE_SUMMARY_ENDPOINT).
			then().
			statusCode(200).
			body("totalResults", is(2)).
			body("results.findAll { it.allele.taxon.species.fullName != '" + SCE_FULL_NAME + "' }.size()", is(0)).
			// the allele's own taxon is untouched - only the attached species resolves
			body("results.allele.taxon.curie", hasItems("NCBITaxon:4932", YeastStrainTaxonConstants.CANONICAL_SCE_TAXON));
	}

	private Species createSpecies(String taxonCurie, String fullName, String abbreviation, String displayName, Integer phylogeneticOrder) {
		Species species = new Species();
		species.setTaxon(getNCBITaxonTerm(taxonCurie));
		species.setFullName(fullName);
		species.setAbbreviation(abbreviation);
		species.setDisplayName(displayName);
		species.setPhylogeneticOrder(phylogeneticOrder);

		ObjectResponse<Species> response = RestAssured.given().
			contentType("application/json").
			body(species).
			when().
			post("/api/species").
			then().
			statusCode(200).
			extract().body().as(new TypeRef<ObjectResponse<Species>>() { });

		return response.getEntity();
	}

	private Long createYeastAgm(String primaryExternalId, String taxonCurie, String fullName) {
		NCBITaxonTerm taxon = getNCBITaxonTerm(taxonCurie);

		AgmFullNameSlotAnnotation agmFullName = new AgmFullNameSlotAnnotation();
		agmFullName.setDisplayText(fullName);
		agmFullName.setFormatText(fullName);
		agmFullName.setNameType(fullNameType);

		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setPrimaryExternalId(primaryExternalId);
		agm.setTaxon(taxon);
		agm.setSubtype(strainSubtype);
		agm.setAgmFullName(agmFullName);
		agm.setDataProvider(dataProvider);
		agm.setDataProviderCrossReference(buildCrossReference(primaryExternalId));
		agm.setInternal(false);
		agm.setObsolete(false);

		ObjectResponse<AffectedGenomicModel> response = RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(200).
			extract().body().as(new TypeRef<ObjectResponse<AffectedGenomicModel>>() { });

		return response.getEntity().getId();
	}

	private Long createYeastAllele(String primaryExternalId, String taxonCurie, String symbol) {
		AlleleSymbolSlotAnnotation alleleSymbol = new AlleleSymbolSlotAnnotation();
		alleleSymbol.setDisplayText(symbol);
		alleleSymbol.setFormatText(symbol);
		alleleSymbol.setNameType(symbolNameType);

		Allele allele = new Allele();
		allele.setPrimaryExternalId(primaryExternalId);
		allele.setTaxon(getNCBITaxonTerm(taxonCurie));
		allele.setAlleleSymbol(alleleSymbol);
		allele.setDataProvider(dataProvider);
		// the allele summary query inner-joins the data provider cross reference and its page,
		// so an allele without one would not be returned at all
		allele.setDataProviderCrossReference(buildCrossReference(primaryExternalId));
		allele.setInternal(false);
		allele.setObsolete(false);

		ObjectResponse<Allele> response = RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(200).
			extract().body().as(new TypeRef<ObjectResponse<Allele>>() { });

		return response.getEntity().getId();
	}

	private CrossReference buildCrossReference(String referencedCurie) {
		CrossReference crossReference = new CrossReference();
		crossReference.setReferencedCurie(referencedCurie);
		crossReference.setDisplayName(referencedCurie);
		crossReference.setResourceDescriptorPage(resourceDescriptorPage);
		return crossReference;
	}
}
