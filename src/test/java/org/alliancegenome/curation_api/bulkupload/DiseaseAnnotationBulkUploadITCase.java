package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
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
	
	private String geneDiseaseAnnotation = "DATEST:Annot0001";
	private String alleleDiseaseAnnotation = "DATEST:Annot0002";
	private String agmDiseaseAnnotation = "DATEST:Annot0003";
	private String gene = "DATEST:Gene0001";
	private String gene2 = "DATEST:Gene0002";
	private String withGene = "HGNC:0001";
	private String withGene2 = "HGNC:0002";
	private String doTerm = "DATEST:Disease0001";
	private String doTerm2 = "DATEST:Disease0002";
	private String ecoTerm = "DATEST:Evidence0001";
	private String ecoTerm2 = "DATEST:Evidence0002";
	private String goTerm = "DATEST:GOTerm0001";
	private String goTerm2 = "DATEST:GOTerm0002";
	private String anatomyTerm = "DATEST:AnatomyTerm0001";
	private String anatomyTerm2 = "DATEST:AnatomyTerm0002";
	private String chemicalTerm = "DATEST:ChemicalTerm0001";
	private String chemicalTerm2 = "DATEST:ChemicalTerm0002";
	private String allele = "DATEST:Allele0001";
	private String allele2 = "DATEST:Allele0002";
	private String agm = "DATEST:AGM0001";
	private String agm2 = "DATEST:AGM0002";
	private String sgdBackgroundStrain = "SGD:AGM0001";
	private String sgdBackgroundStrain2 = "SGD:AGM0002";
	private String zecoTerm = "DATEST:ExpCondTerm0001";
	private String zecoTerm2 = "DATEST:ExpCondTerm0003";
	private String nonSlimZecoTerm = "DATEST:NSExpCondTerm0001";
	private String expCondTerm = "DATEST:ExpCondTerm0002";
	private String alleleAndGeneDiseaseRelation = "is_implicated_in";
	private String geneDiseaseRelation = "is_marker_for";
	private String agmDiseaseRelation = "is_model_of";
	private String agmDiseaseRelation2 = "is_exacerbated_model_of";
	private String geneticSex = "male";
	private String geneticSex2 = "female";
	private String diseaseGeneticModifierRelation = "ameliorated_by";
	private String diseaseGeneticModifierRelation2 = "exacerbated_by";
	private String diseaseQualifier = "susceptibility";
	private String diseaseQualifier2 = "severity";
	private String annotationType = "manually_curated";
	private String annotationType2 = "computational";
	private String noteType = "disease_summary";
	private String noteType2 = "disease_note";
	private String conditionRelationType = "exacerbated_by";
	private String conditionRelationType2 = "induced_by";
	private String reference = "AGRKB:000000002";
	private String referenceXref = "PMID:25920554";
	private String reference2 = "AGRKB:000000021";
	private String dataProvider = "TEST";
	private String dataProvider2 = "TEST2";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}
	
	private final String geneDaBulkPostEndpoint = "/api/gene-disease-annotation/bulk/WB/annotationFile";
	private final String geneDaGetEndpoint = "/api/gene-disease-annotation/findBy/";
	private final String alleleDaBulkPostEndpoint = "/api/allele-disease-annotation/bulk/WB/annotationFile";
	private final String alleleDaGetEndpoint = "/api/allele-disease-annotation/findBy/";
	private final String agmDaBulkPostEndpoint = "/api/agm-disease-annotation/bulk/WB/annotationFile";
	private final String agmDaGetEndpoint = "/api/agm-disease-annotation/findBy/";
	private final String daTestFilePath = "src/test/resources/bulk/04_disease_annotation/";
	
	private void loadRequiredEntities() throws Exception {
		loadDOTerm(doTerm, "Test DOTerm");
		loadDOTerm(doTerm2, "Test DOTerm 2");
		loadECOTerm(ecoTerm, "Test ECOTerm");
		loadECOTerm(ecoTerm2, "Test ECOTerm 2");
		loadGOTerm(goTerm, "Test GOTerm");
		loadGOTerm(goTerm2, "Test GOTerm 2");
		loadExperimentalConditionTerm(expCondTerm, "Test ExperimentalConditionOntologyTerm");
		loadZecoTerm(zecoTerm, "Test ExperimentalConditionOntologyTerm", OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		loadZecoTerm(zecoTerm2, "Test ExperimentalConditionOntologyTerm 2", OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		loadZecoTerm(nonSlimZecoTerm, "Test ExperimentalConditionOntologyTerm", null);
		loadChemicalTerm(chemicalTerm, "Test ChemicalTerm");
		loadChemicalTerm(chemicalTerm2, "Test ChemicalTerm 2");
		loadAnatomyTerm(anatomyTerm, "Test AnatomicalTerm");
		loadAnatomyTerm(anatomyTerm2, "Test AnatomicalTerm 2");
		Vocabulary nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		loadGenes(List.of(gene, gene2, withGene, withGene2), "NCBITaxon:6239", symbolTerm);
		loadAllele(allele, "TestAllele", "NCBITaxon:6239", symbolTerm);
		loadAllele(allele2, "TestAllele2", "NCBITaxon:6239", symbolTerm);
		loadAffectedGenomicModel(agm, "Test AGM", "NCBITaxon:6239", "fish");
		loadAffectedGenomicModel(agm2, "Test AGM2", "NCBITaxon:6239", "genotype");
		loadAffectedGenomicModel(sgdBackgroundStrain, "Test SGD AGM", "NCBITaxon:559292", "strain");
		loadAffectedGenomicModel(sgdBackgroundStrain2, "Test SGD AGM2", "NCBITaxon:559292", "strain");
		loadReference(reference, referenceXref);
		loadOrganization(dataProvider);
		loadOrganization(dataProvider2);
		loadOrganization("OBSOLETE");
		ResourceDescriptor rd = createResourceDescriptor("TEST");
		createResourceDescriptorPage("homepage", "http://test.org", rd);
		
		Vocabulary noteTypeVocabulary = createVocabulary(VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY, false);
		Vocabulary diseaseRelationVocabulary = createVocabulary(VocabularyConstants.DISEASE_RELATION_VOCABULARY, false);
		Vocabulary geneticSexVocabulary = createVocabulary(VocabularyConstants.GENETIC_SEX_VOCABULARY, false);
		Vocabulary diseaseGeneticModifierRelationVocabulary = createVocabulary(VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY, false);
		Vocabulary diseaseQualifierVocabulary = createVocabulary(VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY, false);
		Vocabulary annotationTypeVocabulary = createVocabulary(VocabularyConstants.ANNOTATION_TYPE_VOCABULARY, false);
		Vocabulary conditionRelationTypeVocabulary = createVocabulary(VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY, false);
		createVocabularyTerm(noteTypeVocabulary, noteType, false);
		createVocabularyTerm(noteTypeVocabulary, noteType2, false);
		VocabularyTerm alleleAndGeneDiseaseRelationVocabularyTerm = createVocabularyTerm(diseaseRelationVocabulary, alleleAndGeneDiseaseRelation, false);
		VocabularyTerm agmDiseaseRelationVocabularyTerm = createVocabularyTerm(diseaseRelationVocabulary, agmDiseaseRelation, false);
		VocabularyTerm agmDiseaseRelationVocabularyTerm2 = createVocabularyTerm(diseaseRelationVocabulary, agmDiseaseRelation2, false);
		VocabularyTerm geneDiseaseVocabularyTerm = createVocabularyTerm(diseaseRelationVocabulary, geneDiseaseRelation, false);
		createVocabularyTerm(diseaseQualifierVocabulary, diseaseQualifier, false);
		createVocabularyTerm(diseaseQualifierVocabulary, diseaseQualifier2, false);
		createVocabularyTerm(geneticSexVocabulary, geneticSex, false);
		createVocabularyTerm(geneticSexVocabulary, geneticSex2, false);
		createVocabularyTerm(diseaseGeneticModifierRelationVocabulary, diseaseGeneticModifierRelation, false);
		createVocabularyTerm(diseaseGeneticModifierRelationVocabulary, diseaseGeneticModifierRelation2, false);
		createVocabularyTerm(annotationTypeVocabulary, annotationType, false);
		createVocabularyTerm(annotationTypeVocabulary, annotationType2, false);
		createVocabularyTerm(conditionRelationTypeVocabulary, conditionRelationType, false);
		createVocabularyTerm(conditionRelationTypeVocabulary, conditionRelationType2, false);
		createVocabularyTermSet(VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY_TERM_SET, diseaseRelationVocabulary, List.of(agmDiseaseRelationVocabularyTerm, agmDiseaseRelationVocabularyTerm2));
		createVocabularyTermSet(VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET, diseaseRelationVocabulary, List.of(alleleAndGeneDiseaseRelationVocabularyTerm));
		createVocabularyTermSet(VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, diseaseRelationVocabulary, List.of(geneDiseaseVocabularyTerm, alleleAndGeneDiseaseRelationVocabularyTerm));
	}

	@Test
	@Order(1)
	public void geneDiseaseAnnotationBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "AF_01_all_fields_gene_annotation.json");
		
		RestAssured.given().
			when().
			get(geneDaGetEndpoint + geneDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(geneDiseaseAnnotation)).
			body("entity.negated", is(true)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.uniqueId", is(geneDiseaseAnnotation)).
			body("entity.subject.curie", is(gene)).
			body("entity.object.curie", is(doTerm)).
			body("entity.diseaseRelation.name", is(alleleAndGeneDiseaseRelation)).
			body("entity.geneticSex.name", is(geneticSex)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].internal", is(true)).
			body("entity.conditionRelations[0].obsolete", is(true)).
			body("entity.conditionRelations[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].handle", is("test_handle")).
			body("entity.conditionRelations[0].singleReference.curie", is(reference)).
			body("entity.conditionRelations[0].conditionRelationType.name", is(conditionRelationType)).
			body("entity.conditionRelations[0].conditions", hasSize(1)).
			body("entity.conditionRelations[0].uniqueId", is(String.join("|", List.of(conditionRelationType, "test_handle", reference , zecoTerm, expCondTerm, anatomyTerm, chemicalTerm, goTerm, "NCBITaxon:6239|Some amount|Free text")))).
			body("entity.conditionRelations[0].conditions[0].internal", is(true)).
			body("entity.conditionRelations[0].conditions[0].obsolete", is(true)).
			body("entity.conditionRelations[0].conditions[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].conditions[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].conditions[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionId.curie", is(expCondTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionQuantity", is("Some amount")).
			body("entity.conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239")).
			body("entity.conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionFreeText", is("Free text")).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm:Test GOTerm:Test ChemicalTerm:Caenorhabditis elegans:Some amount:Free text")).
			body("entity.diseaseGeneticModifier.curie", is(gene2)).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation)).
			body("entity.with", hasSize(1)).
			body("entity.with[0].curie", is(withGene)).
			body("entity.singleReference.curie", is(reference)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].obsolete", is(true)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].freeText", is("Test note")).
			body("entity.relatedNotes[0].noteType.name", is(noteType)).
			body("entity.relatedNotes[0].references[0].curie", is(reference)).
			body("entity.annotationType.name", is(annotationType)).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier)).
			body("entity.sgdStrainBackground.curie", is(sgdBackgroundStrain)).
			body("entity.evidenceCodes", hasSize(1)).
			body("entity.evidenceCodes[0].curie", is(ecoTerm)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider)).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider2)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage")).
			body("entity.secondaryDataProvider.crossReference.referencedCurie", is("TEST:0002")).
			body("entity.secondaryDataProvider.crossReference.displayName", is("TEST:0002")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(2)
	public void alleleDiseaseAnnotationBulkUploadCheckFields() throws Exception {
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "AF_02_all_fields_allele_annotation.json");
		
		RestAssured.given().
			when().
			get(alleleDaGetEndpoint + alleleDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(alleleDiseaseAnnotation)).
			body("entity.negated", is(true)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.uniqueId", is(alleleDiseaseAnnotation)).
			body("entity.subject.curie", is(allele)).
			body("entity.object.curie", is(doTerm)).
			body("entity.diseaseRelation.name", is(alleleAndGeneDiseaseRelation)).
			body("entity.geneticSex.name", is(geneticSex)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].internal", is(true)).
			body("entity.conditionRelations[0].obsolete", is(true)).
			body("entity.conditionRelations[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].handle", is("test_handle")).
			body("entity.conditionRelations[0].singleReference.curie", is(reference)).
			body("entity.conditionRelations[0].conditionRelationType.name", is(conditionRelationType)).
			body("entity.conditionRelations[0].conditions", hasSize(1)).
			body("entity.conditionRelations[0].uniqueId", is(String.join("|", List.of(conditionRelationType, "test_handle", reference , zecoTerm, expCondTerm, anatomyTerm, chemicalTerm, goTerm, "NCBITaxon:6239|Some amount|Free text")))).
			body("entity.conditionRelations[0].conditions[0].internal", is(true)).
			body("entity.conditionRelations[0].conditions[0].obsolete", is(true)).
			body("entity.conditionRelations[0].conditions[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].conditions[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].conditions[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionId.curie", is(expCondTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionQuantity", is("Some amount")).
			body("entity.conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239")).
			body("entity.conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionFreeText", is("Free text")).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm:Test GOTerm:Test ChemicalTerm:Caenorhabditis elegans:Some amount:Free text")).
			body("entity.diseaseGeneticModifier.curie", is(gene2)).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation)).
			body("entity.with", hasSize(1)).
			body("entity.with[0].curie", is(withGene)).
			body("entity.singleReference.curie", is(reference)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].obsolete", is(true)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].freeText", is("Test note")).
			body("entity.relatedNotes[0].noteType.name", is(noteType)).
			body("entity.relatedNotes[0].references[0].curie", is(reference)).
			body("entity.annotationType.name", is(annotationType)).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier)).
			body("entity.evidenceCodes", hasSize(1)).
			body("entity.evidenceCodes[0].curie", is(ecoTerm)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider)).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider2)).
			body("entity.inferredGene.curie", is(gene)).
			body("entity.assertedGenes[0].curie", is(gene2)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage")).
			body("entity.secondaryDataProvider.crossReference.referencedCurie", is("TEST:0002")).
			body("entity.secondaryDataProvider.crossReference.displayName", is("TEST:0002")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(3)
	public void agmDiseaseAnnotationBulkUploadCheckFields() throws Exception {
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "AF_03_all_fields_agm_annotation.json");
		
		RestAssured.given().
			when().
			get(agmDaGetEndpoint + agmDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(agmDiseaseAnnotation)).
			body("entity.negated", is(true)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.uniqueId", is(agmDiseaseAnnotation)).
			body("entity.subject.curie", is(agm)).
			body("entity.object.curie", is(doTerm)).
			body("entity.diseaseRelation.name", is(agmDiseaseRelation)).
			body("entity.geneticSex.name", is(geneticSex)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].internal", is(true)).
			body("entity.conditionRelations[0].obsolete", is(true)).
			body("entity.conditionRelations[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].handle", is("test_handle")).
			body("entity.conditionRelations[0].singleReference.curie", is(reference)).
			body("entity.conditionRelations[0].conditionRelationType.name", is(conditionRelationType)).
			body("entity.conditionRelations[0].conditions", hasSize(1)).
			body("entity.conditionRelations[0].uniqueId", is(String.join("|", List.of(conditionRelationType, "test_handle", reference , zecoTerm, expCondTerm, anatomyTerm, chemicalTerm, goTerm, "NCBITaxon:6239|Some amount|Free text")))).
			body("entity.conditionRelations[0].conditions[0].internal", is(true)).
			body("entity.conditionRelations[0].conditions[0].obsolete", is(true)).
			body("entity.conditionRelations[0].conditions[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].conditions[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].conditions[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionId.curie", is(expCondTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionQuantity", is("Some amount")).
			body("entity.conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:6239")).
			body("entity.conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionFreeText", is("Free text")).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm:Test GOTerm:Test ChemicalTerm:Caenorhabditis elegans:Some amount:Free text")).
			body("entity.diseaseGeneticModifier.curie", is(gene2)).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation)).
			body("entity.with", hasSize(1)).
			body("entity.with[0].curie", is(withGene)).
			body("entity.singleReference.curie", is(reference)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.relatedNotes[0].obsolete", is(true)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].freeText", is("Test note")).
			body("entity.relatedNotes[0].noteType.name", is(noteType)).
			body("entity.relatedNotes[0].references[0].curie", is(reference)).
			body("entity.annotationType.name", is(annotationType)).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier)).
			body("entity.evidenceCodes", hasSize(1)).
			body("entity.evidenceCodes[0].curie", is(ecoTerm)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider)).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider2)).
			body("entity.inferredGene.curie", is(gene)).
			body("entity.assertedGenes[0].curie", is(gene2)).
			body("entity.inferredAllele.curie", is(allele)).
			body("entity.assertedAllele.curie", is(allele2)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage")).
			body("entity.secondaryDataProvider.crossReference.referencedCurie", is("TEST:0002")).
			body("entity.secondaryDataProvider.crossReference.displayName", is("TEST:0002")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}

	@Test
	@Order(4)
	public void geneDiseaseAnnotationBulkUploadUpdateCheckFields() throws Exception {
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "UD_01_update_all_except_default_fields_gene_annotation.json");
		
		RestAssured.given().
			when().
			get(geneDaGetEndpoint + geneDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(geneDiseaseAnnotation)).
			body("entity.negated", is(false)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.uniqueId", is(geneDiseaseAnnotation)).
			body("entity.subject.curie", is(gene2)).
			body("entity.object.curie", is(doTerm2)).
			body("entity.diseaseRelation.name", is(geneDiseaseRelation)).
			body("entity.geneticSex.name", is(geneticSex2)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].internal", is(false)).
			body("entity.conditionRelations[0].obsolete", is(false)).
			body("entity.conditionRelations[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].handle", is("test_handle_2")).
			body("entity.conditionRelations[0].singleReference.curie", is(reference2)).
			body("entity.conditionRelations[0].conditionRelationType.name", is(conditionRelationType2)).
			body("entity.conditionRelations[0].conditions", hasSize(1)).
			body("entity.conditionRelations[0].uniqueId", is(String.join("|", List.of(conditionRelationType2, "test_handle_2", reference2 , zecoTerm2, zecoTerm, anatomyTerm2, chemicalTerm2, goTerm2, "NCBITaxon:9606|Some amount 2|Free text 2")))).
			body("entity.conditionRelations[0].conditions[0].internal", is(false)).
			body("entity.conditionRelations[0].conditions[0].obsolete", is(false)).
			body("entity.conditionRelations[0].conditions[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].conditions[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].conditions[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionId.curie", is(zecoTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionQuantity", is("Some amount 2")).
			body("entity.conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:9606")).
			body("entity.conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionFreeText", is("Free text 2")).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm 2:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm 2:Test GOTerm 2:Test ChemicalTerm 2:Homo sapiens:Some amount 2:Free text 2")).
			body("entity.diseaseGeneticModifier.curie", is(gene)).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation2)).
			body("entity.with", hasSize(1)).
			body("entity.with[0].curie", is(withGene2)).
			body("entity.singleReference.curie", is(reference2)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.relatedNotes[0].obsolete", is(false)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].freeText", is("Test note 2")).
			body("entity.relatedNotes[0].noteType.name", is(noteType2)).
			body("entity.relatedNotes[0].references[0].curie", is(reference2)).
			body("entity.annotationType.name", is(annotationType2)).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier2)).
			body("entity.sgdStrainBackground.curie", is(sgdBackgroundStrain2)).
			body("entity.evidenceCodes", hasSize(1)).
			body("entity.evidenceCodes[0].curie", is(ecoTerm2)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2)).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0002")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0002")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage")).
			body("entity.secondaryDataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.secondaryDataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(5)
	public void alleleDiseaseAnnotationBulkUploadUpdateCheckFields() throws Exception {
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "UD_02_update_all_except_default_fields_allele_annotation.json");
		
		RestAssured.given().
			when().
			get(alleleDaGetEndpoint + alleleDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(alleleDiseaseAnnotation)).
			body("entity.negated", is(false)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.uniqueId", is(alleleDiseaseAnnotation)).
			body("entity.subject.curie", is(allele2)).
			body("entity.object.curie", is(doTerm2)).
			body("entity.diseaseRelation.name", is(alleleAndGeneDiseaseRelation)).
			body("entity.geneticSex.name", is(geneticSex2)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].internal", is(false)).
			body("entity.conditionRelations[0].obsolete", is(false)).
			body("entity.conditionRelations[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].handle", is("test_handle_2")).
			body("entity.conditionRelations[0].singleReference.curie", is(reference2)).
			body("entity.conditionRelations[0].conditionRelationType.name", is(conditionRelationType2)).
			body("entity.conditionRelations[0].conditions", hasSize(1)).
			body("entity.conditionRelations[0].uniqueId", is(String.join("|", List.of(conditionRelationType2, "test_handle_2", reference2 , zecoTerm2, zecoTerm, anatomyTerm2, chemicalTerm2, goTerm2, "NCBITaxon:9606|Some amount 2|Free text 2")))).
			body("entity.conditionRelations[0].conditions[0].internal", is(false)).
			body("entity.conditionRelations[0].conditions[0].obsolete", is(false)).
			body("entity.conditionRelations[0].conditions[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].conditions[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].conditions[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionId.curie", is(zecoTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionQuantity", is("Some amount 2")).
			body("entity.conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:9606")).
			body("entity.conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionFreeText", is("Free text 2")).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm 2:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm 2:Test GOTerm 2:Test ChemicalTerm 2:Homo sapiens:Some amount 2:Free text 2")).
			body("entity.diseaseGeneticModifier.curie", is(gene)).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation2)).
			body("entity.with", hasSize(1)).
			body("entity.with[0].curie", is(withGene2)).
			body("entity.singleReference.curie", is(reference2)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.relatedNotes[0].obsolete", is(false)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].freeText", is("Test note 2")).
			body("entity.relatedNotes[0].noteType.name", is(noteType2)).
			body("entity.relatedNotes[0].references[0].curie", is(reference2)).
			body("entity.annotationType.name", is(annotationType2)).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier2)).
			body("entity.evidenceCodes", hasSize(1)).
			body("entity.evidenceCodes[0].curie", is(ecoTerm2)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2)).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider)).
			body("entity.inferredGene.curie", is(gene2)).
			body("entity.assertedGenes[0].curie", is(gene)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0002")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0002")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage")).
			body("entity.secondaryDataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.secondaryDataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(6)
	public void agmDiseaseAnnotationBulkUploadUpdateCheckFields() throws Exception {
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "UD_03_update_all_except_default_fields_agm_annotation.json");
		
		RestAssured.given().
			when().
			get(agmDaGetEndpoint + agmDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(agmDiseaseAnnotation)).
			body("entity.negated", is(false)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.uniqueId", is(agmDiseaseAnnotation)).
			body("entity.subject.curie", is(agm2)).
			body("entity.object.curie", is(doTerm2)).
			body("entity.diseaseRelation.name", is(agmDiseaseRelation2)).
			body("entity.geneticSex.name", is(geneticSex2)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].internal", is(false)).
			body("entity.conditionRelations[0].obsolete", is(false)).
			body("entity.conditionRelations[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].handle", is("test_handle_2")).
			body("entity.conditionRelations[0].singleReference.curie", is(reference2)).
			body("entity.conditionRelations[0].conditionRelationType.name", is(conditionRelationType2)).
			body("entity.conditionRelations[0].conditions", hasSize(1)).
			body("entity.conditionRelations[0].uniqueId", is(String.join("|", List.of(conditionRelationType2, "test_handle_2", reference2 , zecoTerm2, zecoTerm, anatomyTerm2, chemicalTerm2, goTerm2, "NCBITaxon:9606|Some amount 2|Free text 2")))).
			body("entity.conditionRelations[0].conditions[0].internal", is(false)).
			body("entity.conditionRelations[0].conditions[0].obsolete", is(false)).
			body("entity.conditionRelations[0].conditions[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.conditionRelations[0].conditions[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.conditionRelations[0].conditions[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.conditionRelations[0].conditions[0].conditionClass.curie", is(zecoTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionId.curie", is(zecoTerm)).
			body("entity.conditionRelations[0].conditions[0].conditionQuantity", is("Some amount 2")).
			body("entity.conditionRelations[0].conditions[0].conditionAnatomy.curie", is(anatomyTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionGeneOntology.curie", is(goTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionTaxon.curie", is("NCBITaxon:9606")).
			body("entity.conditionRelations[0].conditions[0].conditionChemical.curie", is(chemicalTerm2)).
			body("entity.conditionRelations[0].conditions[0].conditionFreeText", is("Free text 2")).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is("Test ExperimentalConditionOntologyTerm 2:Test ExperimentalConditionOntologyTerm:Test AnatomicalTerm 2:Test GOTerm 2:Test ChemicalTerm 2:Homo sapiens:Some amount 2:Free text 2")).
			body("entity.diseaseGeneticModifier.curie", is(gene)).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation2)).
			body("entity.with", hasSize(1)).
			body("entity.with[0].curie", is(withGene2)).
			body("entity.singleReference.curie", is(reference2)).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.relatedNotes[0].obsolete", is(false)).
			body("entity.relatedNotes[0].updatedBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNotes[0].createdBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNotes[0].dateUpdated", is(OffsetDateTime.parse("2022-03-20T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].dateCreated", is(OffsetDateTime.parse("2022-03-19T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNotes[0].freeText", is("Test note 2")).
			body("entity.relatedNotes[0].noteType.name", is(noteType2)).
			body("entity.relatedNotes[0].references[0].curie", is(reference2)).
			body("entity.annotationType.name", is(annotationType2)).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier2)).
			body("entity.evidenceCodes", hasSize(1)).
			body("entity.evidenceCodes[0].curie", is(ecoTerm2)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2)).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider)).
			body("entity.inferredGene.curie", is(gene2)).
			body("entity.assertedGenes[0].curie", is(gene)).
			body("entity.inferredAllele.curie", is(allele2)).
			body("entity.assertedAllele.curie", is(allele)).
			body("entity.dataProvider.crossReference.referencedCurie", is("TEST:0002")).
			body("entity.dataProvider.crossReference.displayName", is("TEST:0002")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage")).
			body("entity.secondaryDataProvider.crossReference.referencedCurie", is("TEST:0001")).
			body("entity.secondaryDataProvider.crossReference.displayName", is("TEST:0001")).
			body("entity.dataProvider.crossReference.resourceDescriptorPage.name", is("homepage"));
	}
	
	@Test
	@Order(7)
	public void diseaseAnnotationBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_01_no_subject.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "MR_01_no_subject.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "MR_01_no_subject.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_02_no_object.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_03_no_disease_relation_gene_annotation.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "MR_04_no_disease_relation_allele_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "MR_05_no_disease_relation_agm_annotation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_06_no_evidence_codes.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_07_no_single_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_08_no_data_provider.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_09_no_condition_relations_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_10_no_condition_relation_experimental_conditions.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_11_no_condition_class.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_12_no_related_note_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_13_no_related_note_free_text.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_14_no_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_15_no_secondary_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_16_no_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_17_no_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MR_18_no_data_provider_cross_reference_prefix.json");
	}
	
	@Test
	@Order(8)
	public void diseaseAnnotationBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "ER_01_empty_subject.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_02_empty_object.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_03_empty_disease_relation_gene_annotation.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "ER_04_empty_disease_relation_allele_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "ER_05_empty_disease_relation_agm_annotation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_06_empty_evidence_codes.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_07_empty_single_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_08_empty_data_provider.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_09_empty_condition_relations_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_10_empty_condition_relation_experimental_conditions.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_11_empty_condition_class.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_12_empty_related_note_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_13_empty_related_note_free_text.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_14_empty_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_15_empty_secondary_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_16_empty_data_provider_cross_reference_referenced_curie.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_17_empty_data_provider_cross_reference_display_name.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ER_18_empty_data_provider_cross_reference_prefix.json");
	}
	
	@Test
	@Order(9)
	public void diseaseAnnotationBulkUploadMissingDependentFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MD_01_no_disease_genetic_modifier.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MD_02_no_disease_genetic_modifier_relation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MD_03_no_disease_genetic_modifier_or_relation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MD_04_no_condition_relation_reference.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MD_05_no_condition_relation_handle_or_reference.json");
	}
	
	@Test
	@Order(10)
	public void diseaseAnnotationBulkUploadEmptyDependentFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ED_01_empty_disease_genetic_modifier.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ED_02_empty_disease_genetic_modifier_relation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ED_03_empty_disease_genetic_modifier_and_relation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ED_04_empty_condition_relation_reference.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "ED_05_empty_condition_relation_handle_and_reference.json");
	}
	
	@Test
	@Order(11)
	public void diseaseAnnotationBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_01_invalid_date_created.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_02_invalid_date_updated.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_03_invalid_subject.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_04_invalid_object.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_05_invalid_disease_relation_gene_annotation.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "IV_06_invalid_disease_relation_allele_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "IV_07_invalid_disease_relation_agm_annotation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_08_invalid_evidence_code.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_09_invalid_single_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_10_invalid_genetic_sex.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_11_invalid_sgd_strain_background.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_12_invalid_with.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_13_invalid_disease_qualifier.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_14_invalid_genetic_modifier.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_15_invalid_genetic_modifier_relation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_16_invalid_annotation_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_17_invalid_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_18_invalid_secondary_data_provider_source_organization_abbreviation.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "IV_19_invalid_inferred_gene_allele_annotation.json");
		checkFailedBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "IV_20_invalid_asserted_gene_allele_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "IV_21_invalid_inferred_gene_agm_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "IV_22_invalid_asserted_gene_agm_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "IV_23_invalid_inferred_allele_agm_annotation.json");
		checkFailedBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "IV_24_invalid_asserted_allele_agm_annotation.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_25_invalid_condition_relation_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_26_invalid_condition_relation_single_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_27_invalid_condition_class.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_28_non_slim_condition_class.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_29_invalid_condition_id.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_30_invalid_condition_gene_ontology.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_31_invalid_condition_anatomy.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_32_invalid_condition_taxon.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_33_invalid_condition_chemical.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_34_invalid_related_note_type.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_35_invalid_related_note_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_36_invalid_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_37_invalid_secondary_data_provider_cross_reference_page_area.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_38_invalid_data_provider_cross_reference_prefix.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "IV_39_invalid_secondary_data_provider_cross_reference_prefix.json");
	}
	
	@Test
	@Order(12)
	public void diseaseAnnotationReferenceMismatches() throws Exception {
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MM_01_mismatched_note_reference.json");
		checkFailedBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MM_02_mismatched_condition_relation_reference.json");
	}
	
	@Test
	@Order(13)
	public void geneDiseaseAnnotationUpdateMissingNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "AF_01_all_fields_gene_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "UM_01_update_no_non_required_fields_level_1_gene_annotation.json");
		
		RestAssured.given().
			when().
			get(geneDaGetEndpoint + geneDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(geneDiseaseAnnotation)).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("geneticSex"))).
			body("entity", not(hasKey("secondaryDataProvider"))).
			body("entity", not(hasKey("conditionRelations"))).
			body("entity", not(hasKey("diseaseGeneticModifier"))).
			body("entity", not(hasKey("diseaseGeneticModifierRelation"))).
			body("entity", not(hasKey("with"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("sgdStrainBackground"))).
			body("entity", not(hasKey("annotationType"))).
			body("entity", not(hasKey("diseaseQualifiers")));
	}
	
	@Test
	@Order(14)
	public void alleleDiseaseAnnotationUpdateMissingNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "AF_02_all_fields_allele_annotation.json");
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "UM_02_update_no_non_required_fields_level_1_allele_annotation.json");
		
		RestAssured.given().
			when().
			get(alleleDaGetEndpoint + alleleDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(alleleDiseaseAnnotation)).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("geneticSex"))).
			body("entity", not(hasKey("secondaryDataProvider"))).
			body("entity", not(hasKey("conditionRelations"))).
			body("entity", not(hasKey("diseaseGeneticModifier"))).
			body("entity", not(hasKey("diseaseGeneticModifierRelation"))).
			body("entity", not(hasKey("with"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("annotationType"))).
			body("entity", not(hasKey("diseaseQualifiers"))).
			body("entity", not(hasKey("inferredGene"))).
			body("entity", not(hasKey("assertedGenes")));
	}
	
	@Test
	@Order(15)
	public void agmDiseaseAnnotationUpdateMissingNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "AF_03_all_fields_agm_annotation.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "UM_03_update_no_non_required_fields_level_1_agm_annotation.json");
		
		RestAssured.given().
			when().
			get(agmDaGetEndpoint + agmDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(agmDiseaseAnnotation)).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("geneticSex"))).
			body("entity", not(hasKey("secondaryDataProvider"))).
			body("entity", not(hasKey("conditionRelations"))).
			body("entity", not(hasKey("diseaseGeneticModifier"))).
			body("entity", not(hasKey("diseaseGeneticModifierRelation"))).
			body("entity", not(hasKey("with"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("annotationType"))).
			body("entity", not(hasKey("diseaseQualifiers"))).
			body("entity", not(hasKey("inferredGene"))).
			body("entity", not(hasKey("assertedGenes"))).
			body("entity", not(hasKey("inferredAllele"))).
			body("entity", not(hasKey("assertedAllele")));
	}
	
	@Test
	@Order(16)
	public void diseaseAnnotationUpdateMissingNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "AF_01_all_fields_gene_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "UM_04_update_no_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(geneDaGetEndpoint + geneDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(geneDiseaseAnnotation)).
			body("entity.conditionRelations[0]", not(hasKey("singleReference"))).
			body("entity.conditionRelations[0]", not(hasKey("handle"))).
			body("entity.conditionRelations[0]", not(hasKey("createdBy"))).
			body("entity.conditionRelations[0]", not(hasKey("updatedBy"))).
			body("entity.conditionRelations[0]", not(hasKey("dateCreated"))).
			body("entity.conditionRelations[0]", not(hasKey("dateUpdated"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionId"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionQuantity"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionGeneOntology"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionAnatomy"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionTaxon"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionChemical"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionFreeText"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("createdBy"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("updatedBy"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("dateCreated"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("dateUpdated"))).
			body("entity.relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.relatedNotes[0]", not(hasKey("dateUpdated"))).
			body("entity.relatedNotes[0]", not(hasKey("evidence"))).
			body("entity.dataProvider", not(hasKey("crossReference"))).
			body("entity.secondaryDataProvider", not(hasKey("crossReference")));
	}
	
	@Test
	@Order(17)
	public void geneDiseaseAnnotationUpdateEmptyNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "AF_01_all_fields_gene_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "UE_01_update_empty_non_required_fields_level_1_gene_annotation.json");
		
		RestAssured.given().
			when().
			get(geneDaGetEndpoint + geneDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(geneDiseaseAnnotation)).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("geneticSex"))).
			body("entity", not(hasKey("secondaryDataProvider"))).
			body("entity", not(hasKey("conditionRelations"))).
			body("entity", not(hasKey("diseaseGeneticModifier"))).
			body("entity", not(hasKey("diseaseGeneticModifierRelation"))).
			body("entity", not(hasKey("with"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("sgdStrainBackground"))).
			body("entity", not(hasKey("annotationType"))).
			body("entity", not(hasKey("diseaseQualifiers")));
	}
	
	@Test
	@Order(18)
	public void alleleDiseaseAnnotationUpdateEmptyNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "AF_02_all_fields_allele_annotation.json");
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "UE_02_update_empty_non_required_fields_level_1_allele_annotation.json");
		
		RestAssured.given().
			when().
			get(alleleDaGetEndpoint + alleleDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(alleleDiseaseAnnotation)).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("geneticSex"))).
			body("entity", not(hasKey("secondaryDataProvider"))).
			body("entity", not(hasKey("conditionRelations"))).
			body("entity", not(hasKey("diseaseGeneticModifier"))).
			body("entity", not(hasKey("diseaseGeneticModifierRelation"))).
			body("entity", not(hasKey("with"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("annotationType"))).
			body("entity", not(hasKey("diseaseQualifiers"))).
			body("entity", not(hasKey("inferredGene"))).
			body("entity", not(hasKey("assertedGenes")));
	}
	
	@Test
	@Order(19)
	public void agmDiseaseAnnotationUpdateEmptyNonRequiredFieldsLevel1() throws Exception {
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "AF_03_all_fields_agm_annotation.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "UE_03_update_empty_non_required_fields_level_1_agm_annotation.json");
		
		RestAssured.given().
			when().
			get(agmDaGetEndpoint + agmDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(agmDiseaseAnnotation)).
			body("entity", not(hasKey("createdBy"))).
			body("entity", not(hasKey("updatedBy"))).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("dateUpdated"))).
			body("entity", not(hasKey("geneticSex"))).
			body("entity", not(hasKey("secondaryDataProvider"))).
			body("entity", not(hasKey("conditionRelations"))).
			body("entity", not(hasKey("diseaseGeneticModifier"))).
			body("entity", not(hasKey("diseaseGeneticModifierRelation"))).
			body("entity", not(hasKey("with"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("annotationType"))).
			body("entity", not(hasKey("diseaseQualifiers"))).
			body("entity", not(hasKey("inferredGene"))).
			body("entity", not(hasKey("assertedGenes"))).
			body("entity", not(hasKey("inferredAllele"))).
			body("entity", not(hasKey("assertedAllele")));
	}
	
	@Test
	@Order(20)
	public void diseaseAnnotationUpdateEmptyNonRequiredFieldsLevel2() throws Exception {
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "AF_01_all_fields_gene_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "UE_04_update_empty_non_required_fields_level_2.json");
		
		RestAssured.given().
			when().
			get(geneDaGetEndpoint + geneDiseaseAnnotation).
			then().
			statusCode(200).
			body("entity.modEntityId", is(geneDiseaseAnnotation)).
			body("entity.conditionRelations[0]", not(hasKey("singleReference"))).
			body("entity.conditionRelations[0]", not(hasKey("handle"))).
			body("entity.conditionRelations[0]", not(hasKey("createdBy"))).
			body("entity.conditionRelations[0]", not(hasKey("updatedBy"))).
			body("entity.conditionRelations[0]", not(hasKey("dateCreated"))).
			body("entity.conditionRelations[0]", not(hasKey("dateUpdated"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionId"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionQuantity"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionGeneOntology"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionAnatomy"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionTaxon"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionChemical"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("conditionFreeText"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("createdBy"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("updatedBy"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("dateCreated"))).
			body("entity.conditionRelations[0].conditions[0]", not(hasKey("dateUpdated"))).
			body("entity.relatedNotes[0]", not(hasKey("createdBy"))).
			body("entity.relatedNotes[0]", not(hasKey("updatedBy"))).
			body("entity.relatedNotes[0]", not(hasKey("dateCreated"))).
			body("entity.relatedNotes[0]", not(hasKey("dateUpdated"))).
			body("entity.relatedNotes[0]", not(hasKey("evidence")));
	}
	
	@Test
	@Order(21)
	public void diseaseAnnotationBulkUploadMissingNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MN_01_no_non_required_fields_level_1_gene_annotation.json");
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "MN_02_no_non_required_fields_level_1_allele_annotation.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "MN_03_no_non_required_fields_level_1_agm_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "MN_04_no_non_required_fields_level_2.json");
	}
	
	@Test
	@Order(22)
	public void diseaseAnnotationBulkUploadEmptyNonRequiredFields() throws Exception {
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "EN_01_empty_non_required_fields_level_1_gene_annotation.json");
		checkSuccessfulBulkLoad(alleleDaBulkPostEndpoint, daTestFilePath + "EN_02_empty_non_required_fields_level_1_allele_annotation.json");
		checkSuccessfulBulkLoad(agmDaBulkPostEndpoint, daTestFilePath + "EN_03_empty_non_required_fields_level_1_agm_annotation.json");
		checkSuccessfulBulkLoad(geneDaBulkPostEndpoint, daTestFilePath + "EN_04_empty_non_required_fields_level_2.json");
	}
}
