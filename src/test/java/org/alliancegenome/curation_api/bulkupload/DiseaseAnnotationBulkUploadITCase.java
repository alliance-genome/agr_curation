package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("04 - Disease annotation bulk upload")
@Order(4)
public class DiseaseAnnotationBulkUploadITCase extends BaseITCase {
	
	private ArrayList<String> requiredGenes = new ArrayList<String>(Arrays.asList( "DATEST:Gene0001", "DATEST:Gene0002", "HGNC:0001"));
	private String requiredDoTerm = "DATEST:Disease0001";
	private String requiredEcoTerm = "DATEST:Evidence0001";
	private String requiredGoTerm = "DATEST:GOTerm0001";
	private String requiredAnatomicalTerm = "DATEST:AnatomyTerm0001";
	private String requiredChemicalTerm = "DATEST:ChemicalTerm0001";
	private String requiredAllele = "DATEST:Allele0001";
	private String requiredAgm = "DATEST:AGM0001";
	private String requiredSgdBackgroundStrain = "SGD:AGM0001";
	private String requiredZecoTerm = "DATEST:ExpCondTerm0001";
	private String requiredNonSlimZecoTerm = "DATEST:NSExpCondTerm0001";
	private String requiredExpCondTerm = "DATEST:ExpCondTerm0002";
	private String requiredAlleleAndGeneDiseaseRelation = "is_implicated_in";
	private String requiredAgmDiseaseRelation = "is_model_of";
	private String requiredGeneticSex = "male";
	private String requiredDiseaseGeneticModifierRelation = "ameliorated_by";
	private String requiredDiseaseQualifier = "susceptibility";
	private String requiredAnnotationType = "manually_curated";
	private String requiredNoteType = "disease_summary";
	private String requiredConditionRelationType = "exacerbated_by";
	private String requiredReference = "AGRKB:000000002";
	private String requiredReferenceXref = "PMID:25920554";

	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}
	
	private final String geneDaBulkPostEndpoint = "/api/gene-disease-annotation/bulk/wbAnnotationFile";
	private final String geneDaFindEndpoint = "/api/gene-disease-annotation/find?limit=10&page=0";
	private final String alleleDaBulkPostEndpoint = "/api/allele-disease-annotation/bulk/wbAnnotationFile";
	private final String alleleDaFindEndpoint = "/api/allele-disease-annotation/find?limit=10&page=0";
	private final String agmDaBulkPostEndpoint = "/api/agm-disease-annotation/bulk/wbAnnotationFile";
	private final String agmDaFindEndpoint = "/api/agm-disease-annotation/find?limit=10&page=0";
	private final String daTestFilePath = "src/test/resources/bulk/04_disease_annotation/";
	
	private void loadRequiredEntities() throws Exception {
		loadDOTerm(requiredDoTerm, "Test DOTerm");
		loadECOTerm(requiredEcoTerm, "Test ECOTerm");
		loadGOTerm(requiredGoTerm, "Test GOTerm");
		loadExperimentalConditionTerm(requiredExpCondTerm, "Test ExperimentalConditionOntologyTerm");
		loadZecoTerm(requiredZecoTerm, "Test ExperimentalConditionOntologyTerm", OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		loadZecoTerm(requiredNonSlimZecoTerm, "Test ExperimentalConditionOntologyTerm", null);
		loadChemicalTerm(requiredChemicalTerm, "Test ChemicalTerm");
		loadAnatomyTerm(requiredAnatomicalTerm, "Test AnatomicalTerm");
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		loadGenes(requiredGenes, "NCBITaxon:6239", symbolTerm);
		loadAllele(requiredAllele, requiredAllele, "NCBITaxon:6239", symbolTerm);
		loadAffectedGenomicModel(requiredAgm, "Test AGM", "NCBITaxon:6239");
		loadAffectedGenomicModel(requiredSgdBackgroundStrain, "Test AGM", "NCBITaxon:559292");
		loadReference(requiredReference, requiredReferenceXref);
		loadOrganization("TEST");
		loadOrganization("TEST2");
		loadOrganization("OBSOLETE");
		
		Vocabulary noteTypeVocabulary = createVocabulary(VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY, false);
		Vocabulary diseaseRelationVocabulary = createVocabulary(VocabularyConstants.DISEASE_RELATION_VOCABULARY, false);
		Vocabulary geneticSexVocabulary = createVocabulary(VocabularyConstants.GENETIC_SEX_VOCABULARY, false);
		Vocabulary diseaseGeneticModifierRelationVocabulary = createVocabulary(VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY, false);
		Vocabulary diseaseQualifierVocabulary = createVocabulary(VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY, false);
		Vocabulary annotationTypeVocabulary = createVocabulary(VocabularyConstants.ANNOTATION_TYPE_VOCABULARY, false);
		Vocabulary conditionRelationTypeVocabulary = createVocabulary(VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY, false);
		createVocabularyTerm(noteTypeVocabulary, requiredNoteType, false);
		VocabularyTerm alleleAndGeneDiseaseRelationVocabularyTerm = createVocabularyTerm(diseaseRelationVocabulary, requiredAlleleAndGeneDiseaseRelation, false);
		VocabularyTerm agmDiseaseRelationVocabularyTerm = createVocabularyTerm(diseaseRelationVocabulary, requiredAgmDiseaseRelation, false);
		VocabularyTerm agmDiseaseRelationVocabularyTerm2 = createVocabularyTerm(diseaseRelationVocabulary, "is_exacerbated_model_of", false);
		VocabularyTerm geneDiseaseVocabularyTerm = createVocabularyTerm(diseaseRelationVocabulary, "is_marker_for", false);
		createVocabularyTerm(diseaseQualifierVocabulary, requiredDiseaseQualifier, false);
		createVocabularyTerm(geneticSexVocabulary, requiredGeneticSex, false);
		createVocabularyTerm(diseaseGeneticModifierRelationVocabulary, requiredDiseaseGeneticModifierRelation, false);
		createVocabularyTerm(annotationTypeVocabulary, requiredAnnotationType, false);
		createVocabularyTerm(conditionRelationTypeVocabulary, requiredConditionRelationType, false);
		createVocabularyTermSet(VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY_TERM_SET, diseaseRelationVocabulary, List.of(agmDiseaseRelationVocabularyTerm, agmDiseaseRelationVocabularyTerm2));
		createVocabularyTermSet(VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET, diseaseRelationVocabulary, List.of(alleleAndGeneDiseaseRelationVocabularyTerm));
		createVocabularyTermSet(VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, diseaseRelationVocabulary, List.of(geneDiseaseVocabularyTerm, alleleAndGeneDiseaseRelationVocabularyTerm));
	}

	@Test
	@Order(1)
	public void geneDiseaseAnnotationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "001_all_fields_gene_annotation.json");
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(geneDaFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0].uniqueId", is("DATEST:Annot0001")).
			body("results[0].subject.curie", is("DATEST:Gene0001")).
			body("results[0].object.curie", is("DATEST:Disease0001")).
			body("results[0].diseaseRelation.name", is("is_implicated_in")).
			body("results[0].geneticSex.name", is("male")).
			body("results[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("results[0].dateUpdated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("results[0].conditionRelations", hasSize(1)).
			body("results[0].conditionRelations[0].handle", is("test_handle")).
			body("results[0].conditionRelations[0].singleReference.curie", is(requiredReference)).
			body("results[0].conditionRelations[0].conditionRelationType.name", is("exacerbated_by")).
			body("results[0].conditionRelations[0].internal", is(false)).
			body("results[0].conditionRelations[0].obsolete", is(false)).
			body("results[0].conditionRelations[0].uniqueId", is("exacerbated_by|test_handle|" + requiredReference + "|DATEST:ExpCondTerm0001|DATEST:ExpCondTerm0002|DATEST:AnatomyTerm0001|DATEST:ChemicalTerm0001|DATEST:GOTerm0001|NCBITaxon:6239|Some amount|Free text")).
			body("results[0].conditionRelations[0].conditions", hasSize(1)).
			body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is("DATEST:ExpCondTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is("DATEST:ExpCondTerm0002")).
			body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount")).
			body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("DATEST:AnatomyTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("DATEST:GOTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239")).
			body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is("DATEST:ChemicalTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionFreeText", is("Free text")).
			body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm:Test GOTerm:Test ChemicalTerm:Caenorhabditis elegans:Some amount:Free text")).
			body("results[0].conditionRelations[0].conditions[0].internal", is(false)).
			body("results[0].conditionRelations[0].conditions[0].obsolete", is(false)).
			body("results[0].negated", is(true)).
			body("results[0].internal", is(false)).
			body("results[0].obsolete", is(false)).
			body("results[0].diseaseGeneticModifier.curie", is("DATEST:Gene0002")).
			body("results[0].diseaseGeneticModifierRelation.name", is("ameliorated_by")).
			body("results[0].with", hasSize(1)).
			body("results[0].with[0].curie", is("HGNC:0001")).
			body("results[0].singleReference.curie", is(requiredReference)).
			body("results[0].relatedNotes[0].freeText", is("Test note")).
			body("results[0].relatedNotes[0].noteType.name", is("disease_summary")).
			body("results[0].relatedNotes[0].internal", is(false)).
			body("results[0].relatedNotes[0].obsolete", is(false)).
			body("results[0].relatedNotes[0].references[0].curie", is(requiredReference)).
			body("results[0].annotationType.name", is("manually_curated")).
			body("results[0].diseaseQualifiers[0].name", is("susceptibility")).
			body("results[0].sgdStrainBackground.curie", is("SGD:AGM0001")).
			body("results[0].evidenceCodes", hasSize(1)).
			body("results[0].evidenceCodes[0].curie", is("DATEST:Evidence0001")).
			body("results[0].dataProvider.abbreviation", is("TEST")).
			body("results[0].secondaryDataProvider.abbreviation", is("TEST2"));
	}
	
	@Test
	@Order(2)
	public void alleleDiseaseAnnotationBulkUploadCheckFields() throws Exception {
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "002_all_fields_allele_annotation.json");
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(alleleDaFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0002")).
			body("results[0].uniqueId", is("DATEST:Annot0002")).
			body("results[0].subject.curie", is("DATEST:Allele0001")).
			body("results[0].object.curie", is("DATEST:Disease0001")).
			body("results[0].diseaseRelation.name", is("is_implicated_in")).
			body("results[0].geneticSex.name", is("male")).
			body("results[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("results[0].dateUpdated".toString(), is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("results[0].dateCreated".toString(), is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].conditionRelations", hasSize(1)).
			body("results[0].conditionRelations[0].handle", is("test_handle")).
			body("results[0].conditionRelations[0].singleReference.curie", is(requiredReference)).
			body("results[0].conditionRelations[0].conditionRelationType.name", is("exacerbated_by")).
			body("results[0].conditionRelations[0].internal", is(false)).
			body("results[0].conditionRelations[0].obsolete", is(false)).
			body("results[0].conditionRelations[0].uniqueId", is("exacerbated_by|test_handle|" + requiredReference + "|DATEST:ExpCondTerm0001|DATEST:ExpCondTerm0002|DATEST:AnatomyTerm0001|DATEST:ChemicalTerm0001|DATEST:GOTerm0001|NCBITaxon:6239|Some amount|Free text")).
			body("results[0].conditionRelations[0].conditions", hasSize(1)).
			body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is("DATEST:ExpCondTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is("DATEST:ExpCondTerm0002")).
			body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount")).
			body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("DATEST:AnatomyTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("DATEST:GOTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239")).
			body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is("DATEST:ChemicalTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionFreeText", is("Free text")).
			body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm:Test GOTerm:Test ChemicalTerm:Caenorhabditis elegans:Some amount:Free text")).
			body("results[0].conditionRelations[0].conditions[0].internal", is(false)).
			body("results[0].conditionRelations[0].conditions[0].obsolete", is(false)).
			body("results[0].negated", is(true)).
			body("results[0].internal", is(false)).
			body("results[0].obsolete", is(false)).
			body("results[0].diseaseGeneticModifier.curie", is("DATEST:Gene0002")).
			body("results[0].diseaseGeneticModifierRelation.name", is("ameliorated_by")).
			body("results[0].with", hasSize(1)).
			body("results[0].with[0].curie", is("HGNC:0001")).
			body("results[0].singleReference.curie", is(requiredReference)).
			body("results[0].relatedNotes[0].freeText", is("Test note")).
			body("results[0].relatedNotes[0].noteType.name", is("disease_summary")).
			body("results[0].relatedNotes[0].internal", is(false)).
			body("results[0].relatedNotes[0].obsolete", is(false)).
			body("results[0].relatedNotes[0].references[0].curie", is(requiredReference)).
			body("results[0].annotationType.name", is("manually_curated")).
			body("results[0].diseaseQualifiers[0].name", is("susceptibility")).
			body("results[0].evidenceCodes", hasSize(1)).
			body("results[0].evidenceCodes[0].curie", is("DATEST:Evidence0001")).
			body("results[0].inferredGene.curie", is("DATEST:Gene0001")).
			body("results[0].assertedGenes[0].curie", is("DATEST:Gene0001")).
			body("results[0].dataProvider.abbreviation", is("TEST")).
			body("results[0].secondaryDataProvider.abbreviation", is("TEST2"));
	}
	
	@Test
	@Order(3)
	public void agmDiseaseAnnotationBulkUploadCheckFields() throws Exception {
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "003_all_fields_agm_annotation.json");
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post(agmDaFindEndpoint).
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0003")).
			body("results[0].uniqueId", is("DATEST:Annot0003")).
			body("results[0].subject.curie", is("DATEST:AGM0001")).
			body("results[0].object.curie", is("DATEST:Disease0001")).
			body("results[0].diseaseRelation.name", is("is_model_of")).
			body("results[0].geneticSex.name", is("male")).
			body("results[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("results[0].dateUpdated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("results[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("results[0].conditionRelations", hasSize(1)).
			body("results[0].conditionRelations[0].handle", is("test_handle")).
			body("results[0].conditionRelations[0].singleReference.curie", is(requiredReference)).
			body("results[0].conditionRelations[0].conditionRelationType.name", is("exacerbated_by")).
			body("results[0].conditionRelations[0].internal", is(false)).
			body("results[0].conditionRelations[0].obsolete", is(false)).
			body("results[0].conditionRelations[0].uniqueId", is("exacerbated_by|test_handle|" + requiredReference + "|DATEST:ExpCondTerm0001|DATEST:ExpCondTerm0002|DATEST:AnatomyTerm0001|DATEST:ChemicalTerm0001|DATEST:GOTerm0001|NCBITaxon:6239|Some amount|Free text")).
			body("results[0].conditionRelations[0].conditions", hasSize(1)).
			body("results[0].conditionRelations[0].conditions[0].conditionClass.curie", is("DATEST:ExpCondTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionId.curie", is("DATEST:ExpCondTerm0002")).
			body("results[0].conditionRelations[0].conditions[0].conditionQuantity", is("Some amount")).
			body("results[0].conditionRelations[0].conditions[0].conditionAnatomy.curie", is("DATEST:AnatomyTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionGeneOntology.curie", is("DATEST:GOTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239")).
			body("results[0].conditionRelations[0].conditions[0].conditionChemical.curie", is("DATEST:ChemicalTerm0001")).
			body("results[0].conditionRelations[0].conditions[0].conditionFreeText", is("Free text")).
			body("results[0].conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm:Test GOTerm:Test ChemicalTerm:Caenorhabditis elegans:Some amount:Free text")).
			body("results[0].conditionRelations[0].conditions[0].internal", is(false)).
			body("results[0].conditionRelations[0].conditions[0].obsolete", is(false)).
			body("results[0].negated", is(true)).
			body("results[0].internal", is(false)).
			body("results[0].obsolete", is(false)).
			body("results[0].diseaseGeneticModifier.curie", is("DATEST:Gene0002")).
			body("results[0].diseaseGeneticModifierRelation.name", is("ameliorated_by")).
			body("results[0].with", hasSize(1)).
			body("results[0].with[0].curie", is("HGNC:0001")).
			body("results[0].singleReference.curie", is(requiredReference)).
			body("results[0].relatedNotes[0].freeText", is("Test note")).
			body("results[0].relatedNotes[0].noteType.name", is("disease_summary")).
			body("results[0].relatedNotes[0].internal", is(false)).
			body("results[0].relatedNotes[0].obsolete", is(false)).
			body("results[0].relatedNotes[0].references[0].curie", is(requiredReference)).
			body("results[0].annotationType.name", is("manually_curated")).
			body("results[0].diseaseQualifiers[0].name", is("susceptibility")).
			body("results[0].evidenceCodes", hasSize(1)).
			body("results[0].evidenceCodes[0].curie", is("DATEST:Evidence0001")).
			body("results[0].inferredGene.curie", is("DATEST:Gene0001")).
			body("results[0].assertedGenes[0].curie", is("DATEST:Gene0001")).
			body("results[0].inferredAllele.curie", is("DATEST:Allele0001")).
			body("results[0].assertedAllele.curie", is("DATEST:Allele0001")).
			body("results[0].dataProvider.abbreviation", is("TEST")).
			body("results[0].secondaryDataProvider.abbreviation", is("TEST2"));
	}
	
	@Test
	@Order(4)
	public void diseaseAnnotationBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "005_no_subject.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "006_no_object.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "007_no_data_provider.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "009_no_predicate.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "015_no_evidence_codes.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "019_no_single_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "023_no_condition_relations_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "024_no_condition_relation_experimental_conditions.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "025_no_condition_class.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "051_no_related_note_free_text.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "052_no_related_note_type.json");
	}
	
	@Test
	@Order(5)
	public void diseaseAnnotationBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "004_no_mod_id.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "008_no_negated.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "010_no_genetic_sex.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "011_no_updated_by.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "012_no_date_updated.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "013_no_created_by.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "014_no_date_created.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "018_no_with.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "020_no_related_notes.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "021_no_sgd_strain_background.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "022_no_condition_relations.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "027_no_condition_id.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "028_no_condition_quantity.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "029_no_condition_gene_ontology.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "030_no_condition_anatomy.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "031_no_condition_taxon.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "032_no_condition_chemical.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "049_no_annotation_type.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "050_no_disease_qualifiers.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "053_no_related_note_internal.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "054_no_related_note_references.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "060_no_secondary_data_provider.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "064_no_condition_free_text.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "066_no_internal.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "067_no_condition_relations_internal.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "068_no_obsolete.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "069_no_condition_relations_obsolete.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "070_no_related_notes_obsolete.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "071_no_condition_internal.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "072_no_condition_obsolete.json");
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "073_no_inferred_gene_allele_annotation.json");
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "074_no_asserted_gene_allele_annotation.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "077_no_inferred_gene_agm_annotation.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "078_no_asserted_gene_agm_annotation.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "079_no_inferred_allele_agm_annotation.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "080_no_asserted_allele_agm_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "126_no_condition_relation_handle.json");
	}
	
	@Test
	@Order(6)
	public void diseaseAnnotationBulkUploadMissingDependentFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "016_no_disease_genetic_modifier.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "017_no_disease_genetic_modifier_relation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "061_no_disease_genetic_modifier_or_relation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "127_no_condition_relation_reference.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "128_no_condition_relation_handle_or_reference.json");
	}
	
	@Test
	@Order(7)
	public void diseaseAnnotationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "085_empty_evidence_codes.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "089_empty_data_provider.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "093_empty_object.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "101_empty_related_note_free_text.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "110_empty_condition_relations_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "112_empty_subject.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "113_empty_single_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "116_empty_condition_relation_experimental_conditions.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "120_empty_related_note_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "121_empty_condition_class.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "125_empty_predicate.json");
	}	
	
	@Test
	@Order(8)
	public void diseaseAnnotationBulkUploadEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "086_empty_secondary_data_provider.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "087_empty_inferred_gene_agm_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "088_empty_sgd_strain_background.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "090_empty_condition_free_text.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "091_empty_date_updated.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "092_empty_condition_quantity.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "094_empty_annotation_type.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "095_empty_mod_id.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "096_empty_related_note_references.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "097_empty_creation_date.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "100_empty_related_notes.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "102_empty_inferred_allele_agm_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "103_empty_disease_qualifiers.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "104_empty_asserted_allele_agm_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "105_empty_genetic_sex.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "106_empty_condition_relations.json");
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "107_empty_inferred_gene_allele_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "108_empty_created_by.json");
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "109_empty_asserted_genes_allele_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "111_empty_condition_taxon.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "114_empty_condition_gene_ontology.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "115_empty_with.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "117_empty_condition_id.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "118_empty_condition_anatomy.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "119_empty_updated_by.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "122_empty_condition_chemical.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "123_empty_asserted_genes_agm_annotation.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "158_empty_inferred_gene_agm_annotation.json");
	}
	
	@Test
	@Order(9)
	public void diseaseAnnotationBulkUploadEmptyDependentFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "124_empty_disease_genetic_modifier.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "099_empty_disease_genetic_modifier_relation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "098_empty_disease_genetic_modifier_and_relation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "129_empty_condition_relation_reference.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "159_empty_condition_relation_handle_and_reference.json");
	}
	
	@Test
	@Order(10)
	public void diseaseAnnotationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "033_invalid_subject.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "034_invalid_object.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "035_invalid_gene_predicate.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "036_invalid_allele_predicate.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "037_invalid_agm_predicate.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "038_invalid_genetic_sex.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "039_invalid_evidence_code.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "040_invalid_genetic_modifier.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "041_invalid_genetic_modifier_relation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "042_invalid_single_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "043_invalid_condition_class.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "044_invalid_condition_id.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "045_invalid_condition_gene_ontology.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "046_invalid_condition_anatomy.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "047_invalid_condition_taxon.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "048_invalid_condition_chemical.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "055_invalid_sgd_strain_background.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "056_invalid_annotation_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "057_invalid_disease_qualifier.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "058_invalid_related_note_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "059_invalid_related_note_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "062_invalid_date_created.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "063_invalid_date_updated.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "065_non_slim_condition_class.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "075_invalid_inferred_gene_allele_annotation.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "076_invalid_asserted_gene_allele_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "081_invalid_inferred_gene_agm_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "082_invalid_asserted_gene_agm_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "083_invalid_inferred_allele_agm_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "084_invalid_asserted_allele_agm_annotation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "156_invalid_data_provider.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "157_invalid_secondary_data_provider.json");
	}
	
	@Test
	@Order(11)
	public void diseaseAnnotationReferenceMismatches() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "026_mismatched_note_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "130_mismatched_condition_relation_reference.json");	
	}
	
	@Test
	@Order(12)
	public void diseaseAnnotationBulkUploadUpdateMissingNonRequiredFields() throws Exception {
		String geneDaOriginalFilePath = daTestFilePath + "001_all_fields_gene_annotation.json";
		String alleleDaOriginalFilePath = daTestFilePath + "002_all_fields_allele_annotation.json";
		String agmDaOriginalFilePath = daTestFilePath + "003_all_fields_agm_annotation.json";
		
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "131_update_no_genetic_sex.json", "geneticSex");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "132_update_no_updated_by.json", "updatedBy");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "133_update_no_date_updated.json", "dateUpdated");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "134_update_no_created_by.json", "createdBy");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "135_update_no_date_created.json", "dateCreated");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "136_update_no_with.json", "with");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "137_update_no_related_notes.json", "relatedNotes");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "138_update_no_sgd_strain_background.json", "sgdStrainBackground");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "139_update_no_condition_relations.json", "conditionRelations");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "140_update_no_condition_id.json", "conditionRelations[0].conditions[0].conditionId");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "141_update_no_condition_quantity.json", "conditionRelations[0].conditions[0].conditionQuantity");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "142_update_no_condition_gene_ontology.json", "conditionRelations[0].conditions[0].conditionGeneOntology");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "143_update_no_condition_anatomy.json", "conditionRelations[0].conditions[0].conditionAnatomy");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "144_update_no_condition_taxon.json", "conditionRelations[0].conditions[0].conditionTaxon");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "145_update_no_condition_chemical.json", "conditionRelations[0].conditions[0].conditionChemical");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "146_update_no_annotation_type.json", "annotationType");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "147_update_no_disease_qualifiers.json", "diseaseQualifiers");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "148_update_no_disease_genetic_modifier_and_relation.json", "diseaseGeneticModifier");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleDaBulkPostEndpoint, alleleDaFindEndpoint,
				alleleDaOriginalFilePath, daTestFilePath + "149_update_no_inferred_gene_allele_annotation.json", "inferredGene");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(alleleDaBulkPostEndpoint, alleleDaFindEndpoint,
				alleleDaOriginalFilePath, daTestFilePath + "150_update_no_asserted_gene_allele_annotation.json", "assertedGenes");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(agmDaBulkPostEndpoint, agmDaFindEndpoint,
				agmDaOriginalFilePath, daTestFilePath + "151_update_no_inferred_gene_agm_annotation.json", "inferredGene");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(agmDaBulkPostEndpoint, agmDaFindEndpoint,
				agmDaOriginalFilePath, daTestFilePath + "152_update_no_asserted_gene_agm_annotation.json", "assertedGenes");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(agmDaBulkPostEndpoint, agmDaFindEndpoint,
				agmDaOriginalFilePath, daTestFilePath + "153_update_no_inferred_allele_agm_annotation.json", "inferredAllele");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(agmDaBulkPostEndpoint, agmDaFindEndpoint,
				agmDaOriginalFilePath, daTestFilePath + "154_update_no_asserted_allele_agm_annotation.json", "assertedAllele");
		checkSuccessfulBulkLoadUpdateWithMissingNonRequiredField(geneDaBulkPostEndpoint, geneDaFindEndpoint,
				geneDaOriginalFilePath, daTestFilePath + "155_update_no_secondary_data_provider.json", "secondaryDataProvider");
	}
}
