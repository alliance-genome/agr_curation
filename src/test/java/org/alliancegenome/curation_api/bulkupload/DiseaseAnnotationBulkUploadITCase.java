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

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectResponse;
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
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("04 - Disease annotation bulk upload")
@Order(4)
public class DiseaseAnnotationBulkUploadITCase {
	
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

	@Test
	@Order(1)
	public void geneDiseaseAnnotationBulkUploadCheckFields() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		loadRequiredEntities();
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
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
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/02_all_fields_allele_annotation.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
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
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/03_all_fields_agm_annotation.json"));
		
		// upload file
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
	
		
		// check entity count and fields correctly read
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
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
	public void diseaseAnnotationBulkUploadNoModEntityId() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/04_no_mod_id.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		// Creating new WB DA, deprecating previous
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].uniqueId", is("DATEST:Gene0001|DATEST:Disease0001|" + requiredReference));
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":true}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001"));
	}
	
	@Test
	@Order(5)
	public void diseaseAnnotationBulkUploadNoSubject() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/05_no_subject.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(6)
	public void diseaseAnnotationBulkUploadNoObject() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/06_no_object.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(7)
	public void diseaseAnnotationBulkUploadNoDataProvider() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/07_no_data_provider.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}	
	
	@Test
	@Order(8)
	public void diseaseAnnotationBulkUploadNoNegated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/08_no_negated.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0008")).
			body("results[0].negated", is(false)); 
	}
	
	@Test
	@Order(9)
	public void diseaseAnnotationBulkUploadNoPredicate() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/09_no_predicate.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(10)
	public void diseaseAnnotationBulkUploadNoGeneticSex() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/10_no_genetic_sex.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0010"));
	}
	
	@Test
	@Order(11)
	public void diseaseAnnotationBulkUploadNoUpdatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/11_no_updated_by.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0011"));
	}
	
	@Test
	@Order(12)
	public void diseaseAnnotationBulkUploadNoDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/12_no_date_updated.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0012")); 
	}
	
	@Test
	@Order(13)
	public void diseaseAnnotationBulkUploadNoCreatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/13_no_created_by.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0013"));
	}
	
	@Test
	@Order(14)
	public void diseaseAnnotationBulkUploadNoDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/14_no_creation_date.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0014")); 
	}
	
	@Test
	@Order(15)
	public void diseaseAnnotationBulkUploadNoEvidenceCodes() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/15_no_evidence_codes.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(16)
	public void diseaseAnnotationBulkUploadNoDiseaseGeneticModifier() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/16_no_disease_genetic_modifier.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(17)
	public void diseaseAnnotationBulkUploadNoDiseaseGeneticModifierRelation() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/17_no_disease_genetic_modifier_relation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(18)
	public void diseaseAnnotationBulkUploadNoWith() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/18_no_with.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0018")); 
	}
	
	@Test
	@Order(19)
	public void diseaseAnnotationBulkUploadNoSingleReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/19_no_single_reference.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(20)
	public void diseaseAnnotationBulkUploadNoRelatedNotes() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/20_no_related_notes.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0020")); 
	}
	
	@Test
	@Order(21)
	public void diseaseAnnotationBulkUploadNoSgdStrainBackground() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/21_no_sgd_strain_background.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0021")); 
	}
	
	@Test
	@Order(22)
	public void diseaseAnnotationBulkUploadNoConditionRelations() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/22_no_condition_relations.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0022")); 
	}
	
	@Test
	@Order(23)
	public void diseaseAnnotationBulkUploadNoConditionRelationsType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/23_no_condition_relations_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(24)
	public void diseaseAnnotationBulkUploadNoConditionRelationExperimentalConditions() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/24_no_condition_relation_experimental_conditions.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); //
	}
	
	@Test
	@Order(25)
	public void diseaseAnnotationBulkUploadNoConditionClass() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/25_no_condition_class.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(26)
	public void diseaseAnnotationMismatchedNoteReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/26_mismatched_note_reference.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(27)
	public void diseaseAnnotationBulkUploadNoConditionId() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/27_no_condition_id.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0027")); 
	}
	
	@Test
	@Order(28)
	public void diseaseAnnotationBulkUploadNoConditionQuantity() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/28_no_condition_quantity.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0028")); 
	}
	
	@Test
	@Order(29)
	public void diseaseAnnotationBulkUploadNoConditionGeneOntology() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/29_no_condition_gene_ontology.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0029"));
	}
	
	@Test
	@Order(30)
	public void diseaseAnnotationBulkUploadNoConditionAnatomy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/30_no_condition_anatomy.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0030")); 
	}
	
	@Test
	@Order(31)
	public void diseaseAnnotationBulkUploadNoConditionTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/31_no_condition_taxon.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0031")); 
	}
	
	@Test
	@Order(32)
	public void diseaseAnnotationBulkUploadNoConditionChemical() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/32_no_condition_chemical.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0032")); 
	}
	
	@Test
	@Order(33)
	public void diseaseAnnotationBulkUploadInvalidSubject() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/33_invalid_subject.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(34)
	public void diseaseAnnotationBulkUploadInvalidObject() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/34_invalid_object.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(35)
	public void diseaseAnnotationBulkUploadInvalidGenePredicate() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/35_invalid_gene_predicate.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(36)
	public void diseaseAnnotationBulkUploadInvalidAllelePredicate() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/36_invalid_allele_predicate.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":true}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0002"));
	}
	
	@Test
	@Order(37)
	public void diseaseAnnotationBulkUploadInvalidAgmPredicate() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/37_invalid_agm_predicate.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":true}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0003"));
	}
	
	@Test
	@Order(38)
	public void diseaseAnnotationBulkUploadInvalidGeneticSex() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/38_invalid_genetic_sex.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(39)
	public void diseaseAnnotationBulkUploadInvalidEvidenceCode() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/39_invalid_evidence_code.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(40)
	public void diseaseAnnotationBulkUploadInvalidGeneticModifier() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/40_invalid_genetic_modifier.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(41)
	public void diseaseAnnotationBulkUploadInvalidGeneticModifierRelation() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/41_invalid_genetic_modifier_relation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(42)
	public void diseaseAnnotationBulkUploadInvalidSingleReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/42_invalid_single_reference.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(43)
	public void diseaseAnnotationBulkUploadInvalidConditionClass() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/43_invalid_condition_class.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(44)
	public void diseaseAnnotationBulkUploadInvalidConditionId() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/44_invalid_condition_id.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(45)
	public void diseaseAnnotationBulkUploadInvalidConditionGeneOntology() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/45_invalid_condition_gene_ontology.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(46)
	public void diseaseAnnotationBulkUploadInvalidConditionAnatomy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/46_invalid_condition_anatomy.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(47)
	public void diseaseAnnotationBulkUploadInvalidConditionTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/47_invalid_condition_taxon.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(48)
	public void diseaseAnnotationBulkUploadInvalidConditionChemical() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/48_invalid_condition_chemical.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(49)
	public void diseaseAnnotationBulkUploadNoAnnotationType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/49_no_annotation_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0049")); 
	}
	
	@Test
	@Order(50)
	public void diseaseAnnotationBulkUploadNoDiseaseQualifiers() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/50_no_disease_qualifiers.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0050")); 
	}
	
	@Test
	@Order(51)
	public void diseaseAnnotationBulkUploadNoRelatedNoteFreeText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/51_no_related_note_free_text.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(52)
	public void diseaseAnnotationBulkUploadNoRelatedNoteType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/52_no_related_note_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(53)
	public void diseaseAnnotationBulkUploadNoRelatedNoteInternal() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/53_no_related_note_internal.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0053")).
			body("results[0].relatedNotes[0].internal", is(true));	// should be set to true be default
	}
	
	@Test
	@Order(54)
	public void diseaseAnnotationBulkUploadNoRelatedNoteReferences() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/54_no_related_note_references.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0054")); 
	}
	
	@Test
	@Order(55)
	public void diseaseAnnotationBulkUploadInvalidSgdStrainBackground() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/55_invalid_sgd_strain_background.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(56)
	public void diseaseAnnotationBulkUploadInvalidAnnotationType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/56_invalid_annotation_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(57)
	public void diseaseAnnotationBulkUploadInvalidDiseaseQualifier() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/57_invalid_disease_qualifier.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(58)
	public void diseaseAnnotationBulkUploadInvalidRelatedNoteType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/58_invalid_related_note_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(59)
	public void diseaseAnnotationBulkUploadInvalidRelatedNoteReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/59_invalid_related_note_reference.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(60)
	public void diseaseAnnotationBulkUploadNoSecondaryDataProvider() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/60_no_secondary_data_provider.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0060"));
	}
	
	@Test
	@Order(61)
	public void diseaseAnnotationBulkUploadNoDiseaseGeneticModifierAndRelation() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/61_no_disease_genetic_modifier_and_relation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0061")); 
	}
	
	@Test
	@Order(62)
	public void diseaseAnnotationBulkUploadInvalidDateCreated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/62_invalid_creation_date.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(63)
	public void diseaseAnnotationBulkUploadInvalidDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/63_invalid_date_updated.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(64)
	public void diseaseAnnotationBulkUploadNoConditionFreeText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/64_no_condition_free_text.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0064")); 
	}
	
	@Test
	@Order(65)
	public void diseaseAnnotationBulkUploadNonSlimConditionClass() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/65_non_slim_condition_class.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(66)
	public void diseaseAnnotationBulkUploadNoInternal() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/66_no_internal.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0066")).
			body("results[0].internal", is(false)); // should default to false 
	}
	
	@Test
	@Order(67)
	public void diseaseAnnotationBulkUploadNoConditionRelationsInternal() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/67_no_condition_relations_internal.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0067")).
			body("results[0].conditionRelations[0].internal", is(false)); //should default to false
	}
	
	@Test
	@Order(68)
	public void diseaseAnnotationBulkUploadNoObsolete() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/68_no_obsolete.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0068")).
			body("results[0].obsolete", is(false)); // should default to false 
	}
	
	@Test
	@Order(69)
	public void diseaseAnnotationBulkUploadNoConditionRelationsObsolete() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/69_no_condition_relations_obsolete.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0069")).
			body("results[0].conditionRelations[0].obsolete", is(false)); //should default to false
	}
	
	@Test
	@Order(70)
	public void diseaseAnnotationBulkUploadNoRelatedNotesObsolete() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/70_no_related_notes_obsolete.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0070")).
			body("results[0].relatedNotes[0].obsolete", is(false)); //should default to false
	}
	
	@Test
	@Order(71)
	public void diseaseAnnotationBulkUploadNoConditionInternal() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/71_no_condition_internal.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0071")).
			body("results[0].conditionRelations[0].conditions[0].internal", is(false)); //should default to false
	}
	
	@Test
	@Order(72)
	public void diseaseAnnotationBulkUploadNoConditionObsolete() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/72_no_condition_obsolete.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Annot0072")).
			body("results[0].conditionRelations[0].conditions[0].obsolete", is(false)); //should default to false
	}
	
	@Test
	@Order(73)
	public void diseaseAnnotationBulkUploadAlleleAnnotationNoInferredGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/73_no_inferred_gene_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0073"));
	}
	
	@Test
	@Order(74)
	public void diseaseAnnotationBulkUploadAlleleAnnotationNoAssertedGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/74_no_asserted_gene_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0074"));
	}
	
	@Test
	@Order(75)
	public void diseaseAnnotationBulkUploadAlleleAnnotationInvalidInferredGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/75_invalid_inferred_gene_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(76)
	public void diseaseAnnotationBulkUploadAlleleAnnotationInvalidAssertedGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/76_invalid_asserted_gene_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(77)
	public void diseaseAnnotationBulkUploadAgmAnnotationNoInferredGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/77_no_inferred_gene_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0077"));
	}
	
	@Test
	@Order(78)
	public void diseaseAnnotationBulkUploadAgmAnnotationNoAssertedGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/78_no_asserted_gene_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0078"));
	}
	
	@Test
	@Order(79)
	public void diseaseAnnotationBulkUploadAgmAnnotationNoInferredAllele() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/79_no_inferred_allele_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0079"));
	}
	
	@Test
	@Order(80)
	public void diseaseAnnotationBulkUploadAgmAnnotationNoAssertedAllele() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/80_no_asserted_allele_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0080"));
	}
	
	@Test
	@Order(81)
	public void diseaseAnnotationBulkUploadAgmAnnotationInvalidInferredGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/81_invalid_inferred_gene_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(82)
	public void diseaseAnnotationBulkUploadAgmAnnotationInvalidAssertedGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/82_invalid_asserted_gene_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(83)
	public void diseaseAnnotationBulkUploadAgmAnnotationInvalidInferredAllele() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/83_invalid_inferred_allele_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(84)
	public void diseaseAnnotationBulkUploadAgmAnnotationInvalidAssertedAllele() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/84_invalid_asserted_allele_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(85)
	public void diseaseAnnotationBulkUploadMissingEvidenceCodes() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/85_empty_evidence_codes.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(86)
	public void diseaseAnnotationBulkUploadEmptySecondaryDataProvider() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/86_empty_secondary_data_provider.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0086"));
	}
	
	@Test
	@Order(87)
	public void diseaseAnnotationBulkUploadAgmAnnotationEmptyInferredGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/87_empty_inferred_gene_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0087"));
	}
	
	@Test
	@Order(88)
	public void diseaseAnnotationBulkUploadEmptySgdStrainBackground() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/88_empty_sgd_strain_background.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0088"));
	}
	
	@Test
	@Order(89)
	public void diseaseAnnotationBulkUploadEmptyDataProvider() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/89_empty_data_provider.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(90)
	public void diseaseAnnotationBulkUploadEmptyConditionFreeText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/90_empty_condition_free_text.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0090"));
	}
	
	@Test
	@Order(91)
	public void diseaseAnnotationBulkUploadEmptyDateUpdated() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/91_empty_date_updated.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0091"));
	}
	
	@Test
	@Order(92)
	public void diseaseAnnotationBulkUploadEmptyConditionQuantity() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/92_empty_condition_quantity.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0092"));
	}
	
	@Test
	@Order(93)
	public void diseaseAnnotationBulkUploadEmptyObject() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/93_empty_object.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(94)
	public void diseaseAnnotationBulkUploadEmptyAnnotationType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/94_empty_annotation_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0094"));
	}
	
	@Test
	@Order(95)
	public void diseaseAnnotationBulkUploadEmptyModEntityId() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/95_empty_mod_id.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].uniqueId", is("DATEST:Gene0001|DATEST:Disease0001|" + requiredReference));
	}
	
	@Test
	@Order(96)
	public void diseaseAnnotationBulkUploadEmptyRelatedNoteReferences() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/96_empty_related_note_references.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0096"));
	}
	
	@Test
	@Order(97)
	public void diseaseAnnotationBulkUploadEmptyCreationDate() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/97_empty_creation_date.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0097"));
	}
	
	@Test
	@Order(98)
	public void diseaseAnnotationBulkUploadEmptyDiseaseGeneticModifierAndRelation() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/98_empty_disease_genetic_modifier_and_relation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0098"));
	}
	
	@Test
	@Order(99)
	public void diseaseAnnotationBulkUploadEmptyDiseaseGeneticModifierRelation() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/99_empty_disease_genetic_modifier_relation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(100)
	public void diseaseAnnotationBulkUploadEmptyRelatedNotes() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/100_empty_related_notes.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0100"));
	}
	
	@Test
	@Order(101)
	public void diseaseAnnotationBulkUploadEmptyRelatedNoteFreeText() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/101_empty_related_note_free_text.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(102)
	public void diseaseAnnotationBulkUploadAgmAnnotationEmptyInferredAllele() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/102_empty_inferred_allele_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0102"));
	}
	
	@Test
	@Order(103)
	public void diseaseAnnotationBulkUploadEmptyDiseaseQualifiers() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/103_empty_disease_qualifiers.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0103"));
	}
	
	@Test
	@Order(104)
	public void diseaseAnnotationBulkUploadAgmAnnotationEmptyAssertedAllele() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/104_empty_asserted_allele_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0104"));
	}
	
	@Test
	@Order(105)
	public void diseaseAnnotationBulkUploadEmptyGeneticSex() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/105_empty_genetic_sex.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0105"));
	}
	
	@Test
	@Order(106)
	public void diseaseAnnotationBulkUploadEmptyConditionRelations() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/106_empty_condition_relations.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0106"));
	}
	
	@Test
	@Order(107)
	public void diseaseAnnotationBulkUploadAlleleAnnotationEmptyInferredGene() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/107_empty_inferred_gene_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0107"));
	}
	
	@Test
	@Order(108)
	public void diseaseAnnotationBulkUploadEmptyCreatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/108_empty_created_by.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0108"));
	}
	
	@Test
	@Order(109)
	public void diseaseAnnotationBulkUploadAlleleAnnotationEmptyAssertedGenes() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/109_empty_asserted_genes_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0109"));
	}
	
	@Test
	@Order(110)
	public void diseaseAnnotationBulkUploadEmptyConditionRelationsType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/110_empty_condition_relations_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(111)
	public void diseaseAnnotationBulkUploadEmptyConditionTaxon() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/111_empty_condition_taxon.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0111"));
	}
	
	@Test
	@Order(112)
	public void diseaseAnnotationBulkUploadEmptySubject() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/112_empty_subject.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(113)
	public void diseaseAnnotationBulkUploadEmptySingleReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/113_empty_single_reference.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(114)
	public void diseaseAnnotationBulkUploadEmptyConditionGeneOntology() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/114_empty_condition_gene_ontology.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0114"));
	}
	
	@Test
	@Order(115)
	public void diseaseAnnotationBulkUploadEmptyWith() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/115_empty_with.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0115"));
	}
	
	@Test
	@Order(116)
	public void diseaseAnnotationBulkUploadEmptyConditionRelationExperimentalConditions() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/116_empty_condition_relation_experimental_conditions.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(117)
	public void diseaseAnnotationBulkUploadEmptyConditionId() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/117_empty_condition_id.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0117"));
	}
	
	@Test
	@Order(118)
	public void diseaseAnnotationBulkUploadEmptyConditionAnatomy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/118_empty_condition_anatomy.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0118"));
	}
	
	@Test
	@Order(119)
	public void diseaseAnnotationBulkUploadEmptyUpdatedBy() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/119_empty_updated_by.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0119"));
	}
	
	@Test
	@Order(120)
	public void diseaseAnnotationBulkUploadEmptyRelatedNoteType() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/120_empty_related_note_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(121)
	public void diseaseAnnotationBulkUploadEmptyConditionClass() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/121_empty_condition_class.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(122)
	public void diseaseAnnotationBulkUploadEmptyConditionChemical() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/122_empty_condition_chemical.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0122"));
	}
	
	@Test
	@Order(123)
	public void diseaseAnnotationBulkUploadAgmAnnotationEmptyAssertedGenes() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/123_empty_asserted_genes_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0123"));
	}
	
	@Test
	@Order(124)
	public void diseaseAnnotationBulkUploadEmptyDiseaseGeneticModifier() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/124_empty_disease_genetic_modifier.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(125)
	public void diseaseAnnotationBulkUploadEmptyPredicate() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/125_empty_predicate.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(126)
	public void diseaseAnnotationBulkUploadNoConditionRelationHandle() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/126_no_condition_relation_handle.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0126"));
	}
	
	@Test
	@Order(127)
	public void diseaseAnnotationBulkUploadNoConditionRelationReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/127_no_condition_relation_reference.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(128)
	public void diseaseAnnotationBulkUploadNoConditionRelationHandleOrReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/128_no_condition_relation_handle_or_reference.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results[0].modEntityId", is("DATEST:Annot0128"));
	}
	
	@Test
	@Order(129)
	public void diseaseAnnotationBulkUploadEmptyConditionRelationReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/129_empty_condition_relation_reference.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(130)
	public void diseaseAnnotationBulkUploadMismatchedConditionRelationReference() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/130_mismatched_condition_relation_reference.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0));
	}
	
	@Test
	@Order(131)
	public void diseaseAnnotationBulkUploadUpdateNoGeneticSex() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/131_update_no_genetic_sex.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("geneticSex")));
	}
	
	@Test
	@Order(132)
	public void diseaseAnnotationBulkUploadUpdateNoUpdatedBy() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/132_update_no_updated_by.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("updatedBy")));
	}
	
	@Test
	@Order(133)
	public void diseaseAnnotationBulkUploadUpdateNoDateUpdated() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/133_update_no_date_updated.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("dateUpdated")));
	}
	
	@Test
	@Order(134)
	public void diseaseAnnotationBulkUploadUpdateNoCreatedBy() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/134_update_no_created_by.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("createdBy")));
	}
	
	@Test
	@Order(135)
	public void diseaseAnnotationBulkUploadUpdateNoCreationDate() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/135_update_no_creation_date.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("dateCreated")));
	}
	
	@Test
	@Order(136)
	public void diseaseAnnotationBulkUploadUpdateNoWith() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/136_update_no_with.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("with")));
	}
	
	@Test
	@Order(137)
	public void diseaseAnnotationBulkUploadUpdateNoRelatedNotes() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/137_update_no_related_notes.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("relatedNotes")));
	}
	
	@Test
	@Order(138)
	public void diseaseAnnotationBulkUploadUpdateNoSgdStrainBackground() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/138_update_no_sgd_strain_background.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("sgdStrainBackground")));
	}
	
	@Test
	@Order(139)
	public void diseaseAnnotationBulkUploadUpdateNoConditionRelations() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/139_update_no_condition_relations.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("conditionRelations")));
	}
	
	@Test
	@Order(140)
	public void diseaseAnnotationBulkUploadUpdateNoConditionId() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/140_update_no_condition_id.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0].conditionRelations[0].conditions[0]", not(hasKey("conditionId")));
	}
	
	@Test
	@Order(141)
	public void diseaseAnnotationBulkUploadUpdateNoConditionQuantity() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/141_update_no_condition_quantity.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0].conditionRelations[0].conditions[0]", not(hasKey("conditionQuantity")));
	}
	
	@Test
	@Order(142)
	public void diseaseAnnotationBulkUploadUpdateNoConditionGeneOntology() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/142_update_no_condition_gene_ontology.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0].conditionRelations[0].conditions[0]", not(hasKey("conditionGeneOntology")));
	}
	
	@Test
	@Order(143)
	public void diseaseAnnotationBulkUploadUpdateNoConditionAnatomy() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/143_update_no_condition_anatomy.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0].conditionRelations[0].conditions[0]", not(hasKey("conditionAnatomy")));
	}
	
	@Test
	@Order(144)
	public void diseaseAnnotationBulkUploadUpdateNoConditionTaxon() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/144_update_no_condition_taxon.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0].conditionRelations[0].conditions[0]", not(hasKey("conditionTaxon")));
	}
	
	@Test
	@Order(145)
	public void diseaseAnnotationBulkUploadUpdateNoConditionChemical() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/145_update_no_condition_chemical.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0].conditionRelations[0].conditions[0]", not(hasKey("conditionChemical")));
	}
	
	@Test
	@Order(146)
	public void diseaseAnnotationBulkUploadUpdateNoAnnotationType() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/146_update_no_annotation_type.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("annotationType")));
	}
	
	@Test
	@Order(147)
	public void diseaseAnnotationBulkUploadUpdateNoDiseaseQualifiers() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/147_update_no_disease_qualifiers.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("diseaseQualifiers")));
	}
	
	@Test
	@Order(148)
	public void diseaseAnnotationBulkUploadUpdateNoDiseaseGeneticModifierAndRelation() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/148_update_no_disease_genetic_modifier_and_relation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("diseaseGeneticModifier"))).
			body("results[0]", not(hasKey("diseaseGeneticModifierRelation")));
	}
	
	@Test
	@Order(149)
	public void diseaseAnnotationBulkUploadUpdateNoInferredGeneAlleleAnnotation() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/02_all_fields_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/149_update_no_inferred_gene_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0002")).
			body("results[0]", not(hasKey("inferredGene")));
	}
	
	@Test
	@Order(150)
	public void diseaseAnnotationBulkUploadUpdateNoAssertedGeneAlleleAnnotation() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/02_all_fields_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/150_update_no_asserted_gene_allele_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/allele-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/allele-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0002")).
			body("results[0]", not(hasKey("assertedGene")));
	}
	
	@Test
	@Order(151)
	public void diseaseAnnotationBulkUploadUpdateNoInferredGeneAGMAnnotation() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/03_all_fields_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/151_update_no_inferred_gene_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0003")).
			body("results[0]", not(hasKey("inferredGene")));
	}
	
	@Test
	@Order(152)
	public void diseaseAnnotationBulkUploadUpdateNoAssertedGeneAGMAnnotation() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/03_all_fields_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/152_update_no_asserted_gene_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0003")).
			body("results[0]", not(hasKey("assertedGene")));
	}
	
	@Test
	@Order(153)
	public void diseaseAnnotationBulkUploadUpdateNoInferredAlleleAGMAnnotation() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/03_all_fields_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/153_update_no_inferred_allele_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0003")).
			body("results[0]", not(hasKey("inferredAllele")));
	}
	
	@Test
	@Order(154)
	public void diseaseAnnotationBulkUploadUpdateNoAssertedAlleleAGMAnnotation() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/03_all_fields_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/154_update_no_asserted_allele_agm_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/agm-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/agm-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)).
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0003")).
			body("results[0]", not(hasKey("assertedAllele")));
	}
	
	@Test
	@Order(155)
	public void diseaseAnnotationBulkUploadUpdateNoSecondaryDataProvider() throws Exception {
		String original_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/01_all_fields_gene_annotation.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(original_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
				
		String updated_content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/155_update_no_secondary_data_provider.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(updated_content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(1)). // Replace 1 WB gene disease annotation with 1
			body("results", hasSize(1)).
			body("results[0].modEntityId", is("DATEST:Annot0001")).
			body("results[0]", not(hasKey("secondaryDataProvider")));
	}
	
	@Test
	@Order(156)
	public void diseaseAnnotationBulkUploadInvalidDataProvider() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/156_invalid_data_provider.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	@Test
	@Order(157)
	public void diseaseAnnotationBulkUploadInvalidSecondaryDataProvider() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/04_disease_annotation/157_invalid_secondary_data_provider.json"));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post("/api/gene-disease-annotation/bulk/wbAnnotationFile").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			header("Content-Type", "application/json").
			body("{\"obsolete\":false}").
			post("/api/gene-disease-annotation/find?limit=10&page=0").
			then().
			statusCode(200).
			body("totalResults", is(0)); 
	}
	
	private void loadRequiredEntities() throws Exception {
		loadDOTerm();
		loadECOTerm();
		loadGOTerm();
		loadExpCondTerm();
		loadZecoTerm(requiredZecoTerm, OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		loadZecoTerm(requiredNonSlimZecoTerm, null);
		loadChemicalTerm();
		loadAnatomyTerm();
		loadGenes();
		loadAllele();
		loadAGM(requiredAgm, "NCBITaxon:6239");
		loadAGM(requiredSgdBackgroundStrain, "NCBITaxon:559292");
		loadReference();
		loadOrganization("TEST");
		loadOrganization("TEST2");
		loadOrganization("OBSOLETE");
		
		Vocabulary noteTypeVocabulary = createVocabulary(VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
		Vocabulary diseaseRelationVocabulary = createVocabulary(VocabularyConstants.DISEASE_RELATION_VOCABULARY);
		Vocabulary geneticSexVocabulary = createVocabulary(VocabularyConstants.GENETIC_SEX_VOCABULARY);
		Vocabulary diseaseGeneticModifierRelationVocabulary = createVocabulary(VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY);
		Vocabulary diseaseQualifierVocabulary = createVocabulary(VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY);
		Vocabulary annotationTypeVocabulary = createVocabulary(VocabularyConstants.ANNOTATION_TYPE_VOCABULARY);
		Vocabulary conditionRelationTypeVocabulary = createVocabulary(VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
		createVocabularyTerm(noteTypeVocabulary, requiredNoteType);
		VocabularyTerm alleleAndGeneDiseaseRelationVocabularyTerm = createVocabularyTerm(diseaseRelationVocabulary, requiredAlleleAndGeneDiseaseRelation);
		VocabularyTerm agmDiseaseRelationVocabularyTerm = createVocabularyTerm(diseaseRelationVocabulary, requiredAgmDiseaseRelation);
		VocabularyTerm geneDiseaseVocabularyTerm = createVocabularyTerm(diseaseRelationVocabulary, "is_marker_for");
		createVocabularyTerm(diseaseQualifierVocabulary, requiredDiseaseQualifier);
		createVocabularyTerm(geneticSexVocabulary, requiredGeneticSex);
		createVocabularyTerm(diseaseGeneticModifierRelationVocabulary, requiredDiseaseGeneticModifierRelation);
		createVocabularyTerm(annotationTypeVocabulary, requiredAnnotationType);
		createVocabularyTerm(conditionRelationTypeVocabulary, requiredConditionRelationType);
		createVocabularyTermSet(VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY_TERM_SET, diseaseRelationVocabulary, List.of(agmDiseaseRelationVocabularyTerm));
		createVocabularyTermSet(VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET, diseaseRelationVocabulary, List.of(alleleAndGeneDiseaseRelationVocabularyTerm));
		createVocabularyTermSet(VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, diseaseRelationVocabulary, List.of(geneDiseaseVocabularyTerm, alleleAndGeneDiseaseRelationVocabularyTerm));
	}
	
	private void loadOrganization(String abbreviation) throws Exception {
		Organization organization = new Organization();
		organization.setUniqueId(abbreviation);
		organization.setAbbreviation(abbreviation);
		organization.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(organization).
			when().
			put("/api/organization").
			then().
			statusCode(200);
	}
	
	private void loadDOTerm() throws Exception {
		DOTerm doTerm = new DOTerm();
		doTerm.setCurie(requiredDoTerm);
		doTerm.setName("Test DOTerm");
		doTerm.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(doTerm).
			when().
			put("/api/doterm").
			then().
			statusCode(200);
	}
	
	private void loadReference() throws Exception {
			
		CrossReference xref = new CrossReference();
		xref.setCurie(requiredReferenceXref);
		
		ObjectResponse<CrossReference> response = 
			RestAssured.given().
				contentType("application/json").
				body(xref).
				when().
				put("/api/cross-reference").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefCrossReference());
		
		Reference reference = new Reference();
		reference.setCurie(requiredReference);
		reference.setCrossReferences(List.of(response.getEntity()));
		reference.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(reference).
			when().
			put("/api/reference").
			then().
			statusCode(200);
	}


	private void loadECOTerm() throws Exception {
		ECOTerm ecoTerm = new ECOTerm();
		ecoTerm.setCurie(requiredEcoTerm);
		ecoTerm.setName("Test ECOTerm");
		ecoTerm.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(ecoTerm).
			when().
			put("/api/ecoterm").
			then().
			statusCode(200);
	}


	private void loadGOTerm() throws Exception {
		GOTerm goTerm = new GOTerm();
		goTerm.setCurie(requiredGoTerm);
		goTerm.setName("Test GOTerm");
		goTerm.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(goTerm).
			when().
			put("/api/goterm").
			then().
			statusCode(200);
	}

	private void loadAnatomyTerm() throws Exception {
		AnatomicalTerm anatomicalTerm = new AnatomicalTerm();
		anatomicalTerm.setCurie(requiredAnatomicalTerm);
		anatomicalTerm.setName("Test AnatomicalTerm");
		anatomicalTerm.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(anatomicalTerm).
			when().
			put("/api/anatomicalterm").
			then().
			statusCode(200);
	}

	private void loadChemicalTerm() throws Exception {
		ChemicalTerm chemicalTerm = new ChemicalTerm();
		chemicalTerm.setCurie(requiredChemicalTerm);
		chemicalTerm.setName("Test ChemicalTerm");
		chemicalTerm.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(chemicalTerm).
			when().
			put("/api/chemicalterm").
			then().
			statusCode(200);
	}
	
	private void loadZecoTerm(String name, String subset) throws Exception {
		ZECOTerm zecoTerm = new ZECOTerm();
		zecoTerm.setCurie(name);
		zecoTerm.setName("Test ExperimentalConditionOntologyTerm");
		zecoTerm.setObsolete(false);
		List<String> subsets = new ArrayList<String>();
		if (subset != null) {
			subsets.add(subset);
			zecoTerm.setSubsets(subsets);
		}
			
		RestAssured.given().
			contentType("application/json").
			body(zecoTerm).
			when().
			post("/api/zecoterm").
			then().
			statusCode(200);
	}
	
	private void loadExpCondTerm() throws Exception {
		ExperimentalConditionOntologyTerm ecTerm = new ExperimentalConditionOntologyTerm();
		ecTerm.setCurie(requiredExpCondTerm);
		ecTerm.setName("Test ExperimentalConditionOntologyTerm");
		ecTerm.setObsolete(false);

		RestAssured.given().
			contentType("application/json").
			body(ecTerm).
			when().
			post("/api/experimentalconditionontologyterm").
			then().
			statusCode(200);
	}
	
	private void loadGenes() throws Exception {
		for (String geneCurie : requiredGenes) {
			Gene gene = new Gene();
			gene.setCurie(geneCurie);
			gene.setTaxon(getTaxon("NCBITaxon:6239"));

			RestAssured.given().
					contentType("application/json").
					body(gene).
					when().
					post("/api/gene").
					then().
					statusCode(200);
		}
	}

	private void loadAGM(String agmCurie, String taxon) throws Exception {
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setCurie(agmCurie);
		agm.setTaxon(getTaxon(taxon));
		agm.setName("Test AGM");

		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(200);
	}

	private void loadAllele() throws Exception {
		Allele allele = new Allele();
		allele.setCurie(requiredAllele);
		allele.setTaxon(getTaxon("NCBITaxon:6239"));
		allele.setName("DA Test Allele");
		allele.setSymbol("BuData");
		allele.setInternal(false);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(200);
	}
	
	private Vocabulary createVocabulary(String name) {
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.setName(name);
		vocabulary.setInternal(false);
		
		ObjectResponse<Vocabulary> response = 
			RestAssured.given().
				contentType("application/json").
				body(vocabulary).
				when().
				post("/api/vocabulary").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabulary());
		
		vocabulary = response.getEntity();
		
		return vocabulary;
	}
	
	private VocabularyTerm createVocabularyTerm(Vocabulary vocabulary, String name) {
		VocabularyTerm vocabularyTerm = new VocabularyTerm();
		vocabularyTerm.setName(name);
		vocabularyTerm.setVocabulary(vocabulary);
		vocabularyTerm.setInternal(false);
		
		ObjectResponse<VocabularyTerm> response =
			RestAssured.given().
				contentType("application/json").
				body(vocabularyTerm).
				when().
				post("/api/vocabularyterm").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabularyTerm());
		
		return response.getEntity();
	}
	
	private void createVocabularyTermSet(String name, Vocabulary vocabulary, List<VocabularyTerm> terms) {
		VocabularyTermSet vocabularyTermSet = new VocabularyTermSet();
		vocabularyTermSet.setName(name);
		vocabularyTermSet.setVocabularyTermSetVocabulary(vocabulary);
		vocabularyTermSet.setInternal(false);
		vocabularyTermSet.setMemberTerms(terms);
		
		RestAssured.given().
				contentType("application/json").
				body(vocabularyTermSet).
				when().
				post("/api/vocabularytermset").
				then().
				statusCode(200);
	}
	
	private NCBITaxonTerm getTaxon(String taxonCurie) {
		ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
			when().
			get("/api/ncbitaxonterm/" + taxonCurie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefTaxon());
			
		return response.getEntity();
	}
	
	private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRefTaxon() {
		return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
	}
	
	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}
	
	private TypeRef<ObjectResponse<VocabularyTerm>> getObjectResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectResponse <VocabularyTerm>>() { };
	}
	
	private TypeRef<ObjectResponse<CrossReference>> getObjectResponseTypeRefCrossReference() {
		return new TypeRef<ObjectResponse <CrossReference>>() { };
	}
}
