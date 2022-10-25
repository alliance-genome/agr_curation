package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(10)
public class DiseaseAnnotationITCase {

	private final String GENE_DISEASE_ANNOTATION = "GeneDisease:0001";
	private final String GENE_DISEASE_ANNOTATION2 = "GeneDisease:0002";
	private final String GENE_DISEASE_ANNOTATION3 = "GeneDisease:0003";
	private final String ALLELE_DISEASE_ANNOTATION = "AlleleDisease:0001";
	private final String ALLELE_DISEASE_ANNOTATION2 = "AlleleDisease:0002";
	private final String AGM_DISEASE_ANNOTATION = "AgmDisease:0001";
	private final String AGM_DISEASE_ANNOTATION2 = "AgmDisease:0002";
	
	private DOTerm testDoTerm;
	private DOTerm testDoTerm2;
	private DOTerm testObsoleteDoTerm;
	private List<ECOTerm> testEcoTerms;
	private List<ECOTerm> testEcoTerms2;
	private List<ECOTerm> testObsoleteEcoTerms;
	private Gene testGene;
	private Gene testGene2;
	private Gene testObsoleteGene;
	private List<Gene> testWithGenes;
	private Allele testAllele;
	private Allele testAllele2;
	private Allele testObsoleteAllele;
	private AffectedGenomicModel testAgm;
	private AffectedGenomicModel testAgm2;
	private BiologicalEntity testBiologicalEntity;
	private Vocabulary geneDiseaseRelationVocabulary;
	private Vocabulary alleleDiseaseRelationVocabulary;
	private Vocabulary agmDiseaseRelationVocabulary;
	private Vocabulary geneticSexVocabulary;
	private Vocabulary diseaseGeneticModifierRelationVocabulary;
	private Vocabulary diseaseQualifierVocabulary;
	private Vocabulary annotationTypeVocabulary;
	private Vocabulary noteTypeVocabulary;
	private Vocabulary conditionRelationTypeVocabulary;
	private VocabularyTerm geneDiseaseRelation;
	private VocabularyTerm geneDiseaseRelation2;
	private VocabularyTerm alleleDiseaseRelation;
	private VocabularyTerm agmDiseaseRelation;
	private VocabularyTerm geneticSex;
	private VocabularyTerm diseaseGeneticModifierRelation;
	private VocabularyTerm noteType;
	private VocabularyTerm obsoleteNoteType;
	private VocabularyTerm conditionRelationType;
	private VocabularyTerm obsoleteConditionRelationType;
	private List<VocabularyTerm> diseaseQualifiers;
	private VocabularyTerm annotationType;
	private Person testPerson;
	private OffsetDateTime testDate;
	private List<Note> relatedNotes;
	private ExperimentalCondition experimentalCondition;
	private ConditionRelation conditionRelation;
	private Reference testReference;
	private Reference testReference2;
	
	private void createRequiredObjects() {
		testEcoTerms = new ArrayList<ECOTerm>();
		testEcoTerms2 = new ArrayList<ECOTerm>();
		testObsoleteEcoTerms = new ArrayList<ECOTerm>();
		testWithGenes = new ArrayList<Gene>();
		diseaseQualifiers = new ArrayList<VocabularyTerm>();
		relatedNotes = new ArrayList<Note>();
		
		testReference = createReference("PMID:14978094");
		testReference2 = createReference("PMID:14978095");
		testDoTerm = createDiseaseTerm("DOID:da0001", false);
		testDoTerm2 = createDiseaseTerm("DOID:da0002", false);
		testObsoleteDoTerm = createDiseaseTerm("DOID:da0003", true);
		testEcoTerms.add(createEcoTerm("ECO:da0001", "Test evidence code", false));
		testEcoTerms2.add(createEcoTerm("ECO:da0002", "Test evidence code2", false));
		testObsoleteEcoTerms.add(createEcoTerm("ECO:da0003", "Test obsolete evidence code", true));
		testGene = createGene("GENE:da0001", "NCBITaxon:9606", false);
		testGene2 = createGene("GENE:da0002", "NCBITaxon:9606", false);
		testObsoleteGene = createGene("HGNC:da0003", "NCBITaxon:9606", true);
		testWithGenes.add(createGene("HGNC:1", "NCBITaxon:9606", false));
		testAllele = createAllele("ALLELE:da0001", "NCBITaxon:9606", false);
		testAllele2 = createAllele("ALLELE:da0002", "NCBITaxon:9606", false);
		testObsoleteAllele = createAllele("ALLELE:da0003", "NCBITaxon:9606", true);
		testAgm = createModel("MODEL:da0001", "NCBITaxon:9606", "TestAGM");
		testAgm2 = createModel("SGD:da0002", "NCBITaxon:559292", "TestAGM2");
		testBiologicalEntity = createBiologicalEntity("BE:da0001", "NCBITaxon:9606");
		experimentalCondition = createExperimentalCondition("ZECO:da001", "Statement");
		geneDiseaseRelationVocabulary = getVocabulary(VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY);
		alleleDiseaseRelationVocabulary = getVocabulary(VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY);
		agmDiseaseRelationVocabulary = getVocabulary(VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY);
		noteTypeVocabulary = getVocabulary(VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
		geneticSexVocabulary = getVocabulary(VocabularyConstants.GENETIC_SEX_VOCABULARY);
		conditionRelationTypeVocabulary = getVocabulary(VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
		diseaseGeneticModifierRelationVocabulary = getVocabulary(VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY);
		diseaseQualifierVocabulary = getVocabulary(VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY);
		annotationTypeVocabulary = getVocabulary(VocabularyConstants.ANNOTATION_TYPE_VOCABULARY);
		geneDiseaseRelation = getVocabularyTerm(geneDiseaseRelationVocabulary, "is_implicated_in");
		geneDiseaseRelation2 = createVocabularyTerm(geneDiseaseRelationVocabulary, "is_marker_for", false);
		alleleDiseaseRelation = getVocabularyTerm(alleleDiseaseRelationVocabulary, "is_implicated_in");
		agmDiseaseRelation = getVocabularyTerm(agmDiseaseRelationVocabulary, "is_model_of");
		diseaseQualifiers.add(createVocabularyTerm(diseaseQualifierVocabulary, "severity", false));
		geneticSex = createVocabularyTerm(geneticSexVocabulary,"hermaphrodite", false);
		diseaseGeneticModifierRelation = createVocabularyTerm(diseaseGeneticModifierRelationVocabulary, "ameliorated_by", false);
		annotationType = createVocabularyTerm(annotationTypeVocabulary,"computational", false);
		testPerson = createPerson("TEST:Person0001");
		testDate = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		noteType = createVocabularyTerm(noteTypeVocabulary, "disease_note", false);
		obsoleteNoteType = createVocabularyTerm(noteTypeVocabulary, "obsolete_type", true);
		relatedNotes.add(createNote(noteType, "Test text", false, null));
		relatedNotes.add(createNote(noteType, "Test text 2", false, null));
		conditionRelationType = createVocabularyTerm(conditionRelationTypeVocabulary, "relation_type", false);
		obsoleteConditionRelationType = createVocabularyTerm(conditionRelationTypeVocabulary, "obsolete_relation_type", true);
		conditionRelation = createConditionRelation(conditionRelationType, experimentalCondition);
	}

	@Test
	@Order(1)
	public void createGeneDiseaseAnnotation() {
		createRequiredObjects();
		
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		diseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION);
		diseaseAnnotation.setNegated(false);
		diseaseAnnotation.setObject(testDoTerm);
		diseaseAnnotation.setDataProvider("TEST");
		diseaseAnnotation.setSubject(testGene);
		diseaseAnnotation.setEvidenceCodes(testEcoTerms);
		diseaseAnnotation.setCreatedBy(testPerson);
		diseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
				contentType("application/json").
				body(diseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(200);
		RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.uniqueId", is(GENE_DISEASE_ANNOTATION)).
				body("entity.modEntityId", is(GENE_DISEASE_ANNOTATION)).
				body("entity.subject.curie", is("GENE:da0001")).
				body("entity.object.curie", is("DOID:da0001")).
				body("entity.diseaseRelation.name", is("is_implicated_in")).
				body("entity.negated", is(false)).
				body("entity.dataProvider", is("TEST")).
				body("entity.internal", is(false)).
				body("entity.obsolete", is(false)).
				body("entity.singleReference.curie", is(testReference.getCurie())).
				body("entity.evidenceCodes[0].curie", is("ECO:da0001")).
				body("entity.createdBy.uniqueId", is("TEST:Person0001")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(2)
	public void createAlleleDiseaseAnnotation() {

		AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		diseaseAnnotation.setModEntityId(ALLELE_DISEASE_ANNOTATION);
		diseaseAnnotation.setNegated(false);
		diseaseAnnotation.setObject(testDoTerm);
		diseaseAnnotation.setDataProvider("TEST");
		diseaseAnnotation.setSubject(testAllele);
		diseaseAnnotation.setEvidenceCodes(testEcoTerms);
		diseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
				contentType("application/json").
				body(diseaseAnnotation).
				when().
				post("/api/allele-disease-annotation").
				then().
				statusCode(200);
		RestAssured.given().
				when().
				get("/api/allele-disease-annotation/findBy/" + ALLELE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.uniqueId", is(ALLELE_DISEASE_ANNOTATION)).
				body("entity.modEntityId", is(ALLELE_DISEASE_ANNOTATION)).
				body("entity.subject.curie", is("ALLELE:da0001")).
				body("entity.object.curie", is("DOID:da0001")).
				body("entity.diseaseRelation.name", is("is_implicated_in")).
				body("entity.negated", is(false)).
				body("entity.dataProvider", is("TEST")).
				body("entity.internal", is(false)).
				body("entity.obsolete", is(false)).
				body("entity.evidenceCodes[0].curie", is("ECO:da0001")).
				body("entity.singleReference.curie", is(testReference.getCurie())).
				body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(3)
	public void createAgmDiseaseAnnotation() {

		AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		diseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION);
		diseaseAnnotation.setNegated(false);
		diseaseAnnotation.setObject(testDoTerm);
		diseaseAnnotation.setDataProvider("TEST");
		diseaseAnnotation.setSubject(testAgm);
		diseaseAnnotation.setEvidenceCodes(testEcoTerms);
		diseaseAnnotation.setCreatedBy(testPerson);
		diseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
				contentType("application/json").
				body(diseaseAnnotation).
				when().
				post("/api/agm-disease-annotation").
				then().
				statusCode(200);
		RestAssured.given().
				when().
				get("/api/agm-disease-annotation/findBy/" + AGM_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.uniqueId", is(AGM_DISEASE_ANNOTATION)).
				body("entity.modEntityId", is(AGM_DISEASE_ANNOTATION)).
				body("entity.subject.curie", is("MODEL:da0001")).
				body("entity.object.curie", is("DOID:da0001")).
				body("entity.diseaseRelation.name", is("is_model_of")).
				body("entity.negated", is(false)).
				body("entity.dataProvider", is("TEST")).
				body("entity.internal", is(false)).
				body("entity.obsolete", is(false)).
				body("entity.evidenceCodes[0].curie", is("ECO:da0001")).
				body("entity.singleReference.curie", is(testReference.getCurie())).
				body("entity.createdBy.uniqueId", is("TEST:Person0001")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(4)
	public void editGeneDiseaseAnnotation() {
		
		List<ConditionRelation> conditionRelations= new ArrayList<>();
		conditionRelations.add(conditionRelation);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation2);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setConditionRelations(conditionRelations);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200);

		RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.uniqueId", is(GENE_DISEASE_ANNOTATION)).
				body("entity.subject.curie", is("GENE:da0002")).
				body("entity.object.curie", is("DOID:da0002")).
				body("entity.negated", is(true)).
				body("entity.evidenceCodes[0].curie", is("ECO:da0002")).
				body("entity.diseaseRelation.name", is("is_marker_for")).
				body("entity.dataProvider", is("TEST2")).
				body("entity.secondaryDataProvider", is("TEST3")).
				body("entity.geneticSex.name", is("hermaphrodite")).
				body("entity.diseaseGeneticModifierRelation.name", is("ameliorated_by")).
				body("entity.diseaseGeneticModifier.curie", is("BE:da0001")).
				body("entity.annotationType.name", is("computational")).
				body("entity.diseaseQualifiers[0].name", is("severity")).
				body("entity.with[0].curie", is("HGNC:1")).
				body("entity.sgdStrainBackground.curie", is("SGD:da0002")).
				body("entity.relatedNotes", hasSize(2)).
				body("entity.relatedNotes[0].freeText", is("Test text")).
				body("entity.relatedNotes[1].freeText", is("Test text 2")).
				body("entity.conditionRelations[0].conditionRelationType.name", is("relation_type")).
				body("entity.internal", is(true)).
				body("entity.obsolete", is(true)).
				body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
				body("entity.createdBy.uniqueId", is("TEST:Person0001")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}

	@Test
	@Order(5)
	public void editAlleleDiseaseAnnotation() {
		
		AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAllele2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/allele-disease-annotation").
				then().
				statusCode(200);

		RestAssured.given().
				when().
				get("/api/allele-disease-annotation/findBy/" + ALLELE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.uniqueId", is(ALLELE_DISEASE_ANNOTATION)).
				body("entity.subject.curie", is("ALLELE:da0002")).
				body("entity.object.curie", is("DOID:da0002")).
				body("entity.negated", is(true)).
				body("entity.evidenceCodes[0].curie", is("ECO:da0002")).
				body("entity.diseaseRelation.name", is("is_implicated_in")).
				body("entity.dataProvider", is("TEST2")).
				body("entity.secondaryDataProvider", is("TEST3")).
				body("entity.geneticSex.name", is("hermaphrodite")).
				body("entity.diseaseGeneticModifier.curie", is("BE:da0001")).
				body("entity.diseaseGeneticModifierRelation.name", is("ameliorated_by")).
				body("entity.annotationType.name", is("computational")).
				body("entity.diseaseQualifiers[0].name", is("severity")).
				body("entity.with[0].curie", is("HGNC:1")).
				body("entity.inferredGene.curie", is(testGene.getCurie())).
				body("entity.assertedGenes[0].curie", is(testGene2.getCurie())).
				body("entity.internal", is(true)).
				body("entity.obsolete", is(true)).
				body("entity.createdBy.uniqueId", is("TEST:Person0001")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString()));

	}
	
	@Test
	@Order(6)
	public void editAgmDiseaseAnnotation() {
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		editedDiseaseAnnotation.setInferredAllele(testAllele);
		editedDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(200);

		RestAssured.given().
				when().
				get("/api/agm-disease-annotation/findBy/" + AGM_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.uniqueId", is(AGM_DISEASE_ANNOTATION)).
				body("entity.subject.curie", is("SGD:da0002")).
				body("entity.object.curie", is("DOID:da0002")).
				body("entity.negated", is(true)).
				body("entity.evidenceCodes[0].curie", is("ECO:da0002")).
				body("entity.diseaseRelation.name", is("is_model_of")).
				body("entity.dataProvider", is("TEST2")).
				body("entity.secondaryDataProvider", is("TEST3")).
				body("entity.geneticSex.name", is("hermaphrodite")).
				body("entity.diseaseGeneticModifier.curie", is("BE:da0001")).
				body("entity.diseaseGeneticModifierRelation.name", is("ameliorated_by")).
				body("entity.annotationType.name", is("computational")).
				body("entity.diseaseQualifiers[0].name", is("severity")).
				body("entity.with[0].curie", is("HGNC:1")).
				body("entity.inferredGene.curie", is(testGene.getCurie())).
				body("entity.assertedGenes[0].curie", is(testGene2.getCurie())).
				body("entity.inferredAllele.curie", is(testAllele.getCurie())).
				body("entity.assertedAllele.curie", is(testAllele2.getCurie())).
				body("entity.internal", is(true)).
				body("entity.obsolete", is(true)).
				body("entity.createdBy.uniqueId", is("TEST:Person0001")).
				body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
				body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString()));

	}

	@Test
	@Order(7)
	public void editWithObsoleteEcoTerm() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testObsoleteEcoTerms);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
			contentType("application/json").
			body(editedDiseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.evidenceCodes", is(ValidationConstants.OBSOLETE_MESSAGE));
	}

	@Test
	@Order(8)
	public void editWithObsoleteDoTerm() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testObsoleteDoTerm);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
			contentType("application/json").
			body(editedDiseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.object", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void editWithMissingSubject() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(null);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.subject", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(10)
	public void editWithMissingObject() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(null);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.object", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void editWithMissingDiseaseRelation() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(null);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseRelation", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	// TODO: re-enable test response once field can be added in UI
	// @Test
	@Order(12)
	public void editWithMissingDataProvider() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider(null);
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(13)
	public void editWithInvalidSubject() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(nonPersistedGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.subject", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(14)
	public void editWithInvalidObject() {
		
		DOTerm nonPersistedDoTerm = new DOTerm();
		nonPersistedDoTerm.setCurie("NPDO:0001");
		nonPersistedDoTerm.setObsolete(false);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(nonPersistedDoTerm);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.object", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(15)
	public void editWithInvalidDiseaseRelation() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider(null);
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseRelation", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(16)
	public void editWithInvalidEvidenceCode() {
		
		ECOTerm nonPersistedEcoTerm = new ECOTerm();
		nonPersistedEcoTerm.setCurie("NPECO:0001");
		nonPersistedEcoTerm.setObsolete(false);
		
		List<ECOTerm> ecoTerms = new ArrayList<>();
		ecoTerms.add(nonPersistedEcoTerm);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(ecoTerms);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.evidenceCodes", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(17)
	public void editWithInvalidDiseaseGeneticModifier() {
		
		BiologicalEntity nonPersistedBiologicalEntity = new BiologicalEntity();
		nonPersistedBiologicalEntity.setCurie("NPBE:0001");
		nonPersistedBiologicalEntity.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(nonPersistedBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(18)
	public void editWithInvalidWithGene() {
		
		List<Gene> withGenes = new ArrayList<>();
		withGenes.add(testGene2);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(withGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.with", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(19)
	public void editWithInvalidSgdStrainBackground() {
		
		AffectedGenomicModel nonPersistedModel = new AffectedGenomicModel();
		nonPersistedModel.setCurie("NPModel:0001");
		nonPersistedModel.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(nonPersistedModel);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.sgdStrainBackground", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	// TODO: re-enable test once field can be added in UI
	// @Test
	@Order(20)
	public void editWithMissingCreatedBy() {
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(null);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
			contentType("application/json").
			body(editedDiseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.createdBy", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(21)
	public void editAttachedNote() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		List<Note> editedNotes = new ArrayList<Note>();
		for (Note note : editedDiseaseAnnotation.getRelatedNotes()) {
			note.setFreeText("Edited note");
			editedNotes.add(note);
		}
		editedDiseaseAnnotation.setRelatedNotes(editedNotes);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.relatedNotes[0].freeText", is("Edited note"));
	}
	
	@Test
	@Order(22)
	public void editWithObsoleteNoteType() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		List<Note> editedNotes = new ArrayList<Note>();
		for (Note note : editedDiseaseAnnotation.getRelatedNotes()) {
			note.setNoteType(obsoleteNoteType);
			editedNotes.add(note);
		}
		editedDiseaseAnnotation.setRelatedNotes(editedNotes);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.relatedNotes", is("noteType - " + ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(23)
	public void editWithObsoleteConditionRelationType() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		List<ConditionRelation> editedConditionRelations = new ArrayList<ConditionRelation>();
		for (ConditionRelation conditionRelation : editedDiseaseAnnotation.getConditionRelations()) {
			conditionRelation.setConditionRelationType(obsoleteConditionRelationType);
			editedConditionRelations.add(conditionRelation);
		}
		editedDiseaseAnnotation.setConditionRelations(editedConditionRelations);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionRelations", is("conditionRelationType - " + ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(24)
	public void editWithInvalidSingleReference() {
		
		Reference invalidReference = new Reference();
		invalidReference.setCurie("Invalid");
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setSingleReference(invalidReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(2))).
				body("errorMessages.singleReference", is("curie - " + ValidationConstants.INVALID_MESSAGE)).
				body("errorMessages.conditionRelations", is("singleReference - " + ValidationConstants.INVALID_MESSAGE));
	}
	

	
	@Test
	@Order(25)
	public void deleteRelatedNote() {
		
		Long deletedNoteId = relatedNotes.get(0).getId();
		relatedNotes.remove(0);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.relatedNotes", hasSize(1)).
				body("entity.relatedNotes[0].freeText", is("Test text 2"));
		
		RestAssured.given().
				when().
				get("/api/note/" + deletedNoteId).
				then().
				statusCode(200).
				body("", is(Collections.emptyMap()));
		
		Long nextDeletedNoteId = relatedNotes.get(0).getId();
		relatedNotes.remove(0);
		editedDiseaseAnnotation.setRelatedNotes(null);
		
		RestAssured.given().
			contentType("application/json").
			body(editedDiseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(200);

		RestAssured.given().
			when().
			get("/api/note/" + nextDeletedNoteId).
			then().
			statusCode(200).
			body("", is(Collections.emptyMap()));
	}
	
	
	@Test
	@Order(26)
	public void addRelatedNote() {
		
		Note note = new Note();
		note.setNoteType(noteType);
		note.setFreeText("Test text 3");
		note.setInternal(false);
		relatedNotes.add(note);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.relatedNotes", hasSize(1)).
				body("entity.relatedNotes[0].freeText", is("Test text 3"));
		
	}
	
	@Test
	@Order(27)
	public void updateConditionRelationWithNonMatchingReference() {
		
		conditionRelation.setHandle("handle");
		conditionRelation.setSingleReference(testReference2);
		List<ConditionRelation> conditionRelations= new ArrayList<>();
		conditionRelations.add(conditionRelation);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setConditionRelations(conditionRelations);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionRelations", is("singleReference - " + ValidationConstants.INVALID_MESSAGE));
		
	}
	
	@Test
	@Order(28)
	public void deleteConditionRelation() {
		
		ExperimentalCondition newEc = createExperimentalCondition("ZECO:da002", "Another_statement");
		ConditionRelation newCr = createConditionRelation(conditionRelationType, newEc);
		newCr.setHandle("first_cr");
		newCr.setSingleReference(testReference);
		conditionRelation.setHandle("second_cr");
		conditionRelation.setSingleReference(testReference);
		List<ConditionRelation> conditionRelations= new ArrayList<>();
		conditionRelations.add(newCr);
		conditionRelations.add(conditionRelation);
		
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setConditionRelations(conditionRelations);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200);
		
		RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.uniqueId", is(GENE_DISEASE_ANNOTATION)).
				body("entity.conditionRelations", hasSize(2)).
				body("entity.conditionRelations[0].handle", is("first_cr")).
				body("entity.conditionRelations[1].handle", is("second_cr"));
		
		conditionRelations.remove(0);
		editedDiseaseAnnotation.setConditionRelations(conditionRelations);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200);

		RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				body("entity.uniqueId", is(GENE_DISEASE_ANNOTATION)).
				body("entity.conditionRelations", hasSize(1)).
				body("entity.conditionRelations[0].handle", is("second_cr"));
		
		// Check CR itself has not been deleted
		RestAssured.given().
			when().
			get("/api/condition-relation/" + newCr.getId()).
			then().
			statusCode(200).
			body("entity.handle", is("first_cr"));
	}
	
	@Test
	@Order(29)
	public void editAlleleDiseaseAnnotationWithInvalidInferredGene() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAllele2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(nonPersistedGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/allele-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredGene", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(30)
	public void editAlleleDiseaseAnnotationWithInvalidAssertedGene() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAllele2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(nonPersistedGene));
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/allele-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedGenes", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(31)
	public void editAgmDiseaseAnnotationWithInvalidInferredGene() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(nonPersistedGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		editedDiseaseAnnotation.setInferredAllele(testAllele);
		editedDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredGene", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(32)
	public void editAgmDiseaseAnnotationWithInvalidAssertedGene() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(nonPersistedGene));
		editedDiseaseAnnotation.setInferredAllele(testAllele);
		editedDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedGenes", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(33)
	public void editAgmDiseaseAnnotationWithInvalidInferredAllele() {
		
		Allele nonPersistedAllele = new Allele();
		nonPersistedAllele.setCurie("NPAllele:0001");
		nonPersistedAllele.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		editedDiseaseAnnotation.setInferredAllele(nonPersistedAllele);
		editedDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredAllele", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(34)
	public void editAgmDiseaseAnnotationWithInvalidAssertedAllele() {
		
		Allele nonPersistedAllele = new Allele();
		nonPersistedAllele.setCurie("NPAllele:0001");
		nonPersistedAllele.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		editedDiseaseAnnotation.setInferredAllele(testAllele);
		editedDiseaseAnnotation.setAssertedAllele(nonPersistedAllele);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedAllele", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(35)
	public void editAlleleDiseaseAnnotationWithObsoleteInferredGene() {
		
		AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAllele2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testObsoleteGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/allele-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredGene", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(36)
	public void editAlleleDiseaseAnnotationWithObsoleteAssertedGene() {
		
		AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAllele2);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testObsoleteGene));
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/allele-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedGenes", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(37)
	public void editAgmDiseaseAnnotationWithObsoleteInferredGene() {
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testObsoleteGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		editedDiseaseAnnotation.setInferredAllele(testAllele);
		editedDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredGene", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(38)
	public void editAgmDiseaseAnnotationWithObsoleteAssertedGene() {
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testObsoleteGene));
		editedDiseaseAnnotation.setInferredAllele(testAllele);
		editedDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedGenes", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(39)
	public void editAgmDiseaseAnnotationWithObsoleteInferredAllele() {
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		editedDiseaseAnnotation.setInferredAllele(testObsoleteAllele);
		editedDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredAllele", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(40)
	public void editAgmDiseaseAnnotationWithObsoleteAssertedAllele() {
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		editedDiseaseAnnotation.setInferredAllele(testAllele);
		editedDiseaseAnnotation.setAssertedAllele(testObsoleteAllele);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedAllele", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(41)
	public void editWithObsoleteSubject() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testObsoleteGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.subject", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(42)
	public void editWithInvalidGeneticSex() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(annotationType);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.geneticSex", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(43)
	public void editWithInvalidAnnotationType() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(geneticSex);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.annotationType", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(44)
	public void editWithInvalidDiseaseGeneticModifierRelation() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(geneticSex);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(45)
	public void editWithObsoleteWithGene() {
		
		List<Gene> withGenes = new ArrayList<Gene>();
		withGenes.add(testObsoleteGene);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(withGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.with", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(46)
	public void createWithObsoleteEcoTerm() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testObsoleteEcoTerms);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
			contentType("application/json").
			body(newDiseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.evidenceCodes", is(ValidationConstants.OBSOLETE_MESSAGE));
	}

	@Test
	@Order(47)
	public void createWithObsoleteDoTerm() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testObsoleteDoTerm);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
			contentType("application/json").
			body(newDiseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.object", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(48)
	public void createWithMissingSubject() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.subject", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(49)
	public void createWithMissingObject() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.object", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(50)
	public void createWithMissingDiseaseRelation() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseRelation", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	// TODO: enable test once field can be added in UI
	// @Test
	@Order(51)
	public void createWithMissingDataProvider() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION3);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(52)
	public void createWithInvalidSubject() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(nonPersistedGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.subject", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(53)
	public void createWithInvalidObject() {
		
		DOTerm nonPersistedDoTerm = new DOTerm();
		nonPersistedDoTerm.setCurie("NPDO:0001");
		nonPersistedDoTerm.setObsolete(false);
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(nonPersistedDoTerm);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.object", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(54)
	public void createWithInvalidDiseaseRelation() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider(null);
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseRelation", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(55)
	public void createWithInvalidEvidenceCode() {
		
		ECOTerm nonPersistedEcoTerm = new ECOTerm();
		nonPersistedEcoTerm.setCurie("NPECO:0001");
		nonPersistedEcoTerm.setObsolete(false);
		
		List<ECOTerm> ecoTerms = new ArrayList<>();
		ecoTerms.add(nonPersistedEcoTerm);
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(ecoTerms);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.evidenceCodes", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(56)
	public void createWithInvalidDiseaseGeneticModifier() {
		
		BiologicalEntity nonPersistedBiologicalEntity = new BiologicalEntity();
		nonPersistedBiologicalEntity.setCurie("NPBE:0001");
		nonPersistedBiologicalEntity.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(nonPersistedBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(57)
	public void createWithInvalidWithGene() {
		
		List<Gene> withGenes = new ArrayList<>();
		withGenes.add(testGene2);
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(withGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.with", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(58)
	public void createWithInvalidSgdStrainBackground() {
		
		AffectedGenomicModel nonPersistedModel = new AffectedGenomicModel();
		nonPersistedModel.setCurie("NPModel:0001");
		nonPersistedModel.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(nonPersistedModel);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.sgdStrainBackground", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	// TODO: enable once field can be autopopulated on create
	// @Test
	@Order(59)
	public void createWithMissingCreatedBy() {
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION3);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(null);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
			contentType("application/json").
			body(newDiseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(200);
		
		RestAssured.given().
			when().
			get("/api/allele-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION3).
			then().
			statusCode(200).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org"));
	}
	
	@Test
	@Order(60)
	public void createWithObsoleteNoteType() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		List<Note> newNotes = new ArrayList<Note>();
		Note newNote = new Note();
		newNote.setFreeText("test text");
		newNote.setNoteType(obsoleteNoteType);
		newNotes.add(newNote);
		
		newDiseaseAnnotation.setRelatedNotes(newNotes);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.relatedNotes", is("noteType - " + ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(61)
	public void createWithObsoleteConditionRelationType() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		List<ConditionRelation> newConditionRelations = new ArrayList<ConditionRelation>();
		List<ExperimentalCondition> conditions = List.of(experimentalCondition);
		ConditionRelation newConditionRelation = new ConditionRelation();
		newConditionRelation.setConditionRelationType(obsoleteConditionRelationType);
		newConditionRelation.setConditions(conditions);
		newConditionRelations.add(newConditionRelation);
		newDiseaseAnnotation.setConditionRelations(newConditionRelations);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.conditionRelations", is("conditionRelationType - " + ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(62)
	public void createWithInvalidSingleReference() {
		
		Reference invalidReference = new Reference();
		invalidReference.setCurie("Invalid");
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setSingleReference(invalidReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.singleReference", is("curie - " + ValidationConstants.INVALID_MESSAGE));
	}	
	
	@Test
	@Order(63)
	public void createAlleleDiseaseAnnotationWithInvalidInferredGene() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AlleleDiseaseAnnotation newDiseaseAnnotation = new AlleleDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(ALLELE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAllele2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(nonPersistedGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/allele-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredGene", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(64)
	public void createAlleleDiseaseAnnotationWithInvalidAssertedGene() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AlleleDiseaseAnnotation newDiseaseAnnotation = new AlleleDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(ALLELE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAllele2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(nonPersistedGene));
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/allele-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedGenes", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(65)
	public void createAgmDiseaseAnnotationWithInvalidInferredGene() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AGMDiseaseAnnotation newDiseaseAnnotation = new AGMDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAgm);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(nonPersistedGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		newDiseaseAnnotation.setInferredAllele(testAllele);
		newDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredGene", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(66)
	public void createAgmDiseaseAnnotationWithInvalidAssertedGene() {
		
		Gene nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("NPGene:0001");
		nonPersistedGene.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AGMDiseaseAnnotation newDiseaseAnnotation = new AGMDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAgm);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(nonPersistedGene));
		newDiseaseAnnotation.setInferredAllele(testAllele);
		newDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedGenes", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(67)
	public void createAgmDiseaseAnnotationWithInvalidInferredAllele() {
		
		Allele nonPersistedAllele = new Allele();
		nonPersistedAllele.setCurie("NPAllele:0001");
		nonPersistedAllele.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AGMDiseaseAnnotation newDiseaseAnnotation = new AGMDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAgm);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		newDiseaseAnnotation.setInferredAllele(nonPersistedAllele);
		newDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredAllele", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(68)
	public void createAgmDiseaseAnnotationWithInvalidAssertedAllele() {
		
		Allele nonPersistedAllele = new Allele();
		nonPersistedAllele.setCurie("NPAllele:0001");
		nonPersistedAllele.setTaxon(getTaxonFromCurie("NCBITaxon:9606"));
		
		AGMDiseaseAnnotation newDiseaseAnnotation = new AGMDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAgm);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		newDiseaseAnnotation.setInferredAllele(testAllele);
		newDiseaseAnnotation.setAssertedAllele(nonPersistedAllele);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedAllele", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(69)
	public void createAlleleDiseaseAnnotationWithObsoleteInferredGene() {
		
		AlleleDiseaseAnnotation newDiseaseAnnotation = new AlleleDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(ALLELE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAllele2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testObsoleteGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/allele-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredGene", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(70)
	public void createAlleleDiseaseAnnotationWithObsoleteAssertedGene() {
		
		AlleleDiseaseAnnotation newDiseaseAnnotation = new AlleleDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(ALLELE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAllele2);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testObsoleteGene));
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/allele-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedGenes", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(71)
	public void createAgmDiseaseAnnotationWithObsoleteInferredGene() {
		
		AGMDiseaseAnnotation newDiseaseAnnotation = new AGMDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAgm);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testObsoleteGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		newDiseaseAnnotation.setInferredAllele(testAllele);
		newDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredGene", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(72)
	public void createAgmDiseaseAnnotationWithObsoleteAssertedGene() {
		
		AGMDiseaseAnnotation newDiseaseAnnotation = new AGMDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAgm);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testObsoleteGene));
		newDiseaseAnnotation.setInferredAllele(testAllele);
		newDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedGenes", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(73)
	public void createAgmDiseaseAnnotationWithObsoleteInferredAllele() {
		
		AGMDiseaseAnnotation newDiseaseAnnotation = new AGMDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAgm);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		newDiseaseAnnotation.setInferredAllele(testObsoleteAllele);
		newDiseaseAnnotation.setAssertedAllele(testAllele2);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.inferredAllele", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(74)
	public void createAgmDiseaseAnnotationWithObsoleteAssertedAllele() {
		
		AGMDiseaseAnnotation newDiseaseAnnotation = new AGMDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testAgm);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setInferredGene(testGene);
		newDiseaseAnnotation.setAssertedGenes(List.of(testGene2));
		newDiseaseAnnotation.setInferredAllele(testAllele);
		newDiseaseAnnotation.setAssertedAllele(testObsoleteAllele);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/agm-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.assertedAllele", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(75)
	public void createWithObsoleteSubject() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testObsoleteGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.subject", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(76)
	public void createWithInvalidGeneticSex() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(annotationType);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.geneticSex", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(77)
	public void createWithInvalidAnnotationType() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(geneticSex);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.annotationType", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(78)
	public void createWithInvalidDiseaseGeneticModifierRelation() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(geneticSex);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(79)
	public void createWithObsoleteWithGene() {
		
		List<Gene> withGenes = new ArrayList<Gene>();
		withGenes.add(testObsoleteGene);
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(withGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.with", is(ValidationConstants.OBSOLETE_MESSAGE));
	}
	
	@Test
	@Order(80)
	public void createDuplicateDiseaseAnnotation() {
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.uniqueId", is(ValidationConstants.NON_UNIQUE_MESSAGE));
	}
	
	@Test
	@Order(81)
	public void createDiseaseAnnotationWithModifierWithoutRelation() {
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation"));
	}
	
	@Test
	@Order(82)
	public void createDiseaseAnnotationWithModiferRelationWithoutModifier() {
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setInternal(true);
		newDiseaseAnnotation.setObsolete(true);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifier"));
	}
	
	@Test
	@Order(83)
	public void editWithModifierWithoutRelation() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(null);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation"));
	}
	
	@Test
	@Order(84)
	public void editWithModifierRelationWithoutModifier() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(null);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setInternal(true);
		editedDiseaseAnnotation.setObsolete(true);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifier"));
	}
	
	@Test
	@Order(85)
	public void editWithMissingSingleReference() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.singleReference", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(86)
	public void createWithMissingSingleReference() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(null);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.singleReference", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(87)
	public void editWithMissingEvidenceCodes() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(null);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.evidenceCodes", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(88)
	public void createWithMissingEvidenceCodes() {
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(null);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(relatedNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.evidenceCodes", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(89)
	public void editWithMatchingNoteReference() {
		
		List<Note> mismatchedRefNotes = new ArrayList<Note>();
		Note mismatchRefNote = createNote(noteType, "Test text", false, testReference);
		mismatchedRefNotes.add(mismatchRefNote);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(mismatchedRefNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200);
	}
	
	@Test
	@Order(90)
	public void createWithMatchingNoteReference() {
		
		List<Note> mismatchedRefNotes = new ArrayList<Note>();
		Note mismatchRefNote = createNote(noteType, "Test text", false, testReference);
		mismatchedRefNotes.add(mismatchRefNote);
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION2);
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(mismatchedRefNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(200);
	}
	
	@Test
	@Order(91)
	public void editWithMismatchedNoteReference() {
		
		List<Note> mismatchedRefNotes = new ArrayList<Note>();
		Note mismatchRefNote = createNote(noteType, "Test text", false, testReference2);
		mismatchedRefNotes.add(mismatchRefNote);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(mismatchedRefNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.relatedNotes", is("references - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(92)
	public void createWithMismatchedNoteReference() {
		
		List<Note> mismatchedRefNotes = new ArrayList<Note>();
		Note mismatchRefNote = createNote(noteType, "Test text", false, testReference2);
		mismatchedRefNotes.add(mismatchRefNote);
		
		GeneDiseaseAnnotation newDiseaseAnnotation = new GeneDiseaseAnnotation();
		newDiseaseAnnotation.setModEntityId("TEST:MismatchRef");
		newDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		newDiseaseAnnotation.setNegated(true);
		newDiseaseAnnotation.setObject(testDoTerm2);
		newDiseaseAnnotation.setDataProvider("TEST2");
		newDiseaseAnnotation.setSubject(testGene);
		newDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		newDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		newDiseaseAnnotation.setGeneticSex(geneticSex);
		newDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		newDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		newDiseaseAnnotation.setAnnotationType(annotationType);
		newDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		newDiseaseAnnotation.setWith(testWithGenes);
		newDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		newDiseaseAnnotation.setCreatedBy(testPerson);
		newDiseaseAnnotation.setDateCreated(testDate);
		newDiseaseAnnotation.setRelatedNotes(mismatchedRefNotes);
		newDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(newDiseaseAnnotation).
				when().
				post("/api/gene-disease-annotation").
				then().
				statusCode(400).
				body("errorMessages", is(aMapWithSize(1))).
				body("errorMessages.relatedNotes", is("references - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(93)
	public void editWithNullGeneticSex() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().	
				statusCode(200).
				body("entity", hasKey("geneticSex"));
		
		editedDiseaseAnnotation.setGeneticSex(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("geneticSex")));
	}
	
	@Test
	@Order(94)
	public void editWithNullWith() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		

		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("with"));
		
		editedDiseaseAnnotation.setWith(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("with")));
	}
	
	@Test
	@Order(95)
	public void editWithNullRelatedNotes() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("relatedNotes"));
		
		editedDiseaseAnnotation.setRelatedNotes(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("relatedNotes")));
	}
	
	@Test
	@Order(96)
	public void editWithNullSgdStrainBackground() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("sgdStrainBackground"));
		
		editedDiseaseAnnotation.setSgdStrainBackground(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("sgdStrainBackground")));
	}
	
	@Test
	@Order(97)
	public void editWithNullConditionRelations() {
		
		List<ConditionRelation> conditionRelations= new ArrayList<>();
		conditionRelations.add(conditionRelation);
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setConditionRelations(conditionRelations);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("conditionRelations"));
		
		editedDiseaseAnnotation.setConditionRelations(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("conditionRelations")));
	}
	
	@Test
	@Order(98)
	public void editWithNullAnnotationType() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);

		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("annotationType"));
		
		editedDiseaseAnnotation.setAnnotationType(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("annotationType")));
	}
	
	@Test
	@Order(99)
	public void editWithNullDiseaseQualifiers() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("diseaseQualifiers"));
		
		editedDiseaseAnnotation.setDiseaseQualifiers(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("diseaseQualifiers")));
	}
	
	@Test
	@Order(100)
	public void editWithNullDiseaseGeneticModifierAndRelation() {
		
		GeneDiseaseAnnotation editedDiseaseAnnotation = getGeneDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testGene);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(null);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(null);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setSgdStrainBackground(testAgm2);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/gene-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("diseaseGeneticModifier"))).
				body("entity", not(hasKey("diseaseGeneticModifierRelation")));
	}
	
	@Test
	@Order(101)
	public void editAlleleAnnotationWithNullInferredGene() {
		
		AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAllele);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);

		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/allele-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("inferredGene"));
		
		editedDiseaseAnnotation.setInferredGene(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/allele-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("inferredGene")));
	}
	
	@Test
	@Order(102)
	public void editAlleleAnnotationWithNullAssertedGene() {
		
		AlleleDiseaseAnnotation editedDiseaseAnnotation = getAlleleDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(alleleDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAllele);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene));

		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/allele-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("assertedGenes"));
		
		editedDiseaseAnnotation.setAssertedGenes(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/allele-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("assertedGenes")));
	}
	
	@Test
	@Order(103)
	public void editAGMAnnotationWithNullInferredGene() {
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredGene(testGene);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("inferredGene"));
		
		editedDiseaseAnnotation.setInferredGene(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("inferredGene")));
	}
	
	@Test
	@Order(104)
	public void editAGMAnnotationWithNullAssertedGenes() {
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setAssertedGenes(List.of(testGene));
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("assertedGenes"));
		
		editedDiseaseAnnotation.setAssertedGenes(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("assertedGenes")));
	}
	
	@Test
	@Order(105)
	public void editAGMAnnotationWithNullInferredAllele() {
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setInferredAllele(testAllele);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("inferredAllele"));
		
		editedDiseaseAnnotation.setInferredAllele(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("inferredAllele")));
	}
	
	@Test
	@Order(106)
	public void editAGMAnnotationWithNullAssertedAllele() {
		
		AGMDiseaseAnnotation editedDiseaseAnnotation = getAgmDiseaseAnnotation();
		editedDiseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		editedDiseaseAnnotation.setNegated(true);
		editedDiseaseAnnotation.setObject(testDoTerm2);
		editedDiseaseAnnotation.setDataProvider("TEST2");
		editedDiseaseAnnotation.setSubject(testAgm);
		editedDiseaseAnnotation.setEvidenceCodes(testEcoTerms2);
		editedDiseaseAnnotation.setSecondaryDataProvider("TEST3");
		editedDiseaseAnnotation.setGeneticSex(geneticSex);
		editedDiseaseAnnotation.setDiseaseGeneticModifier(testBiologicalEntity);
		editedDiseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		editedDiseaseAnnotation.setAnnotationType(annotationType);
		editedDiseaseAnnotation.setDiseaseQualifiers(diseaseQualifiers);
		editedDiseaseAnnotation.setWith(testWithGenes);
		editedDiseaseAnnotation.setCreatedBy(testPerson);
		editedDiseaseAnnotation.setDateCreated(testDate);
		editedDiseaseAnnotation.setRelatedNotes(relatedNotes);
		editedDiseaseAnnotation.setSingleReference(testReference);
		editedDiseaseAnnotation.setAssertedAllele(testAllele);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(200).
				body("entity", hasKey("assertedAllele"));
		
		editedDiseaseAnnotation.setAssertedAllele(null);
		
		RestAssured.given().
				contentType("application/json").
				body(editedDiseaseAnnotation).
				when().
				put("/api/agm-disease-annotation").
				then().
				statusCode(200).
				body("entity", not(hasKey("assertedAllele")));
	}
	
	private GeneDiseaseAnnotation getGeneDiseaseAnnotation() {
		ObjectResponse<GeneDiseaseAnnotation> res = RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRef());

		return res.getEntity();
	}

	private AlleleDiseaseAnnotation getAlleleDiseaseAnnotation() {
		ObjectResponse<AlleleDiseaseAnnotation> res = RestAssured.given().
				when().
				get("/api/allele-disease-annotation/findBy/" + ALLELE_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				extract().body().as(new TypeRef<>() {
		});

		return res.getEntity();
	}

	private AGMDiseaseAnnotation getAgmDiseaseAnnotation() {
		ObjectResponse<AGMDiseaseAnnotation> res = RestAssured.given().
				when().
				get("/api/agm-disease-annotation/findBy/" + AGM_DISEASE_ANNOTATION).
				then().
				statusCode(200).
				extract().body().as(new TypeRef<>() {
		});

		return res.getEntity();
	}
	
	private BiologicalEntity createBiologicalEntity(String curie, String taxon) {
		BiologicalEntity bioEntity = new BiologicalEntity();
		bioEntity.setCurie(curie);
		bioEntity.setTaxon(getTaxonFromCurie(taxon));
		
		RestAssured.given().
				contentType("application/json").
				body(bioEntity).
				when().
				post("/api/biologicalentity").
				then().
				statusCode(200);
		
		return bioEntity;
	}


	private DOTerm createDiseaseTerm(String curie, Boolean obsolete) {
		DOTerm doTerm = new DOTerm();
		doTerm.setCurie(curie);
		doTerm.setObsolete(obsolete);

		RestAssured.given().
				contentType("application/json").
				body(doTerm).
				when().
				post("/api/doterm").
				then().
				statusCode(200);
		return doTerm;
	}


	private ECOTerm createEcoTerm(String curie, String name, Boolean obsolete) {
		ECOTerm ecoTerm = new ECOTerm();
		ecoTerm.setCurie(curie);
		ecoTerm.setName(name);
		ecoTerm.setObsolete(obsolete);

		RestAssured.given().
				contentType("application/json").
				body(ecoTerm).
				when().
				post("/api/ecoterm").
				then().
				statusCode(200);
		return ecoTerm;
	}


	private Gene createGene(String curie, String taxon, Boolean obsolete) {
		Gene gene = new Gene();
		gene.setCurie(curie);
		gene.setTaxon(getTaxonFromCurie(taxon));
		gene.setObsolete(obsolete);

		RestAssured.given().
				contentType("application/json").
				body(gene).
				when().
				post("/api/gene").
				then().
				statusCode(200);
		return gene;
	}

	private Allele createAllele(String curie, String taxon, Boolean obsolete) {
		Allele allele = new Allele();
		allele.setCurie(curie);
		allele.setTaxon(getTaxonFromCurie(taxon));
		allele.setObsolete(obsolete);
		allele.setName("DA CRUD Test Allele");
		allele.setSymbol("DATA");
		allele.setInternal(false);

		RestAssured.given().
				contentType("application/json").
				body(allele).
				when().
				post("/api/allele").
				then().
				statusCode(200);
		return allele;
	}
	
	private Person createPerson(String uniqueId) {
		LoggedInPerson person = new LoggedInPerson();
		person.setUniqueId(uniqueId);
		
		ObjectResponse<LoggedInPerson> response = RestAssured.given().
				contentType("application/json").
				body(person).
				when().
				post("/api/loggedinperson").
				then().
				statusCode(200).extract().
				body().as(getObjectResponseTypeRefLoggedInPerson());
		
		person = response.getEntity();
		return (Person) person;
	}

	private AffectedGenomicModel createModel(String curie, String taxon, String name) {
		AffectedGenomicModel model = new AffectedGenomicModel();
		model.setCurie(curie);
		model.setTaxon(getTaxonFromCurie(taxon));
		model.setName(name);

		RestAssured.given().
				contentType("application/json").
				body(model).
				when().
				post("/api/agm").
				then().
				statusCode(200);
		return model;
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
	
	private Reference createReference(String curie) {
		Reference reference = new Reference();
		reference.setCurie(curie);
		
		ObjectResponse<Reference> response = RestAssured.given().
			contentType("application/json").
			body(reference).
			when().
			post("/api/reference").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefReference());
			
		return response.getEntity();
	}

	private Vocabulary getVocabulary(String name) {
		ObjectResponse<Vocabulary> response = 
			RestAssured.given().
				when().
				get("/api/vocabulary/findBy/" + name).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefVocabulary());
		
		Vocabulary vocabulary = response.getEntity();
		
		return vocabulary;
	}

	private VocabularyTerm createVocabularyTerm(Vocabulary vocabulary, String name, Boolean obsolete) {
		VocabularyTerm vocabularyTerm = new VocabularyTerm();
		vocabularyTerm.setName(name);
		vocabularyTerm.setVocabulary(vocabulary);
		vocabularyTerm.setObsolete(obsolete);
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
		
		vocabularyTerm = response.getEntity();
		
		return vocabularyTerm;
	}
	
	private VocabularyTerm getVocabularyTerm(Vocabulary vocabulary, String name) {
		ObjectListResponse<VocabularyTerm> response = 
			RestAssured.given().
				when().
				get("/api/vocabulary/" + vocabulary.getId() + "/terms").
				then().
				statusCode(200).
				extract().body().as(getObjectListResponseTypeRefVocabularyTerm());
		
		List<VocabularyTerm> vocabularyTerms = response.getEntities();
		for (VocabularyTerm vocabularyTerm : vocabularyTerms) {
			if (vocabularyTerm.getName().equals(name)) {
				return vocabularyTerm;
			}
		}
		
		return null;
	}
	
	private NCBITaxonTerm getTaxonFromCurie(String taxonCurie) {
		ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
			when().
			get("/api/ncbitaxonterm/" + taxonCurie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefTaxon());
		
		return response.getEntity();
	}

	private Note createNote(VocabularyTerm vocabularyTerm, String text, Boolean internal, Reference reference) {
		Note note = new Note();
		note.setNoteType(vocabularyTerm);
		note.setFreeText(text);
		note.setInternal(internal);
		if (reference != null) {
			List<Reference> references = new ArrayList<Reference>();
			references.add(reference);
			note.setReferences(references);
		}

		ObjectResponse<Note> response = RestAssured.given().
			contentType("application/json").
			body(note).
			when().
			post("/api/note").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefNote());
		
		return response.getEntity();
	}
	
	private ExperimentalCondition createExperimentalCondition(String conditionClass, String statement) {
		ExperimentalCondition condition = new ExperimentalCondition();
		condition.setConditionClass(createZecoTerm(conditionClass));
		condition.setUniqueId(statement);
		ObjectResponse<ExperimentalCondition> response = RestAssured.given().
			contentType("application/json").
			body(condition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefExperimentalCondition());
		
		return response.getEntity();
	}
	
	private ZECOTerm createZecoTerm(String curie) {
		ZECOTerm zecoTerm = new ZECOTerm();
		zecoTerm.setCurie(curie);
		zecoTerm.setName("Test");
		zecoTerm.setObsolete(false);
		List<String> subsets = new ArrayList<String>();
		subsets.add(OntologyConstants.ZECO_AGR_SLIM_SUBSET);
		zecoTerm.setSubsets(subsets);

		RestAssured.given().
				contentType("application/json").
				body(zecoTerm).
				when().
				post("/api/zecoterm").
				then().
				statusCode(200);
		return zecoTerm;
	}
	
	private ConditionRelation createConditionRelation(VocabularyTerm conditionRelationType, ExperimentalCondition condition) {
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setConditionRelationType(conditionRelationType);
		List<ExperimentalCondition> conditions = new ArrayList<>();
		conditions.add(condition);
		conditionRelation.addExperimentCondition(condition);
		conditionRelation.setConditions(conditions);
		conditionRelation.setSingleReference(testReference);
		
		ObjectResponse<ConditionRelation> response = RestAssured.given().
			contentType("application/json").
			body(conditionRelation).
			when().
			post("/api/condition-relation").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefConditionRelation());
	
		return response.getEntity();
	}
	
	private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRefTaxon() {
		return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
	}

	private TypeRef<ObjectResponse<LoggedInPerson>> getObjectResponseTypeRefLoggedInPerson() {
		return new TypeRef<ObjectResponse <LoggedInPerson>>() { };
	}

	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}
	
	private TypeRef<ObjectResponse<VocabularyTerm>> getObjectResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectResponse <VocabularyTerm>>() { };
	}
	
	private TypeRef<ObjectResponse<Note>> getObjectResponseTypeRefNote() {
		return new TypeRef<ObjectResponse <Note>>() { };
	}
	
	private TypeRef<ObjectListResponse<VocabularyTerm>> getObjectListResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectListResponse <VocabularyTerm>>() { };
	}
	
	private TypeRef<ObjectResponse<ExperimentalCondition>> getObjectResponseTypeRefExperimentalCondition() {
		return new TypeRef<ObjectResponse <ExperimentalCondition>>() { };
	}
	
	private TypeRef<ObjectResponse<ConditionRelation>> getObjectResponseTypeRefConditionRelation() {
		return new TypeRef<ObjectResponse <ConditionRelation>>() { };
	}

	private TypeRef<ObjectResponse<Reference>> getObjectResponseTypeRefReference() {
		return new TypeRef<ObjectResponse <Reference>>() {
		};
	}

	private TypeRef<ObjectResponse<GeneDiseaseAnnotation>> getObjectResponseTypeRef() {
		return new TypeRef<ObjectResponse <GeneDiseaseAnnotation>>() {
		};
	}
}
