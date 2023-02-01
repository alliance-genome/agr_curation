package org.alliancegenome.curation_api.crud.controllers;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(10)
public class DiseaseAnnotationITCase extends BaseITCase {

	private final String GENE_DISEASE_ANNOTATION = "GeneDisease:0001";
	private final String ALLELE_DISEASE_ANNOTATION = "AlleleDisease:0001";
	private final String AGM_DISEASE_ANNOTATION = "AgmDisease:0001";
	
	private DOTerm doTerm;
	private DOTerm doTerm2;
	private DOTerm obsoleteDoTerm;
	private ECOTerm ecoTerm;
	private ECOTerm ecoTerm2;
	private ECOTerm obsoleteEcoTerm;
	private Gene gene;
	private Gene gene2;
	private Gene obsoleteGene;
	private Gene withGene;
	private Gene withGene2;
	private Allele allele;
	private Allele allele2;
	private Allele obsoleteAllele;
	private AffectedGenomicModel agm;
	private AffectedGenomicModel agm2;
	private AffectedGenomicModel obsoleteAgm;
	private AffectedGenomicModel nonSgdAgm;
	private Vocabulary diseaseRelationVocabulary;
	private Vocabulary geneticSexVocabulary;
	private Vocabulary diseaseGeneticModifierRelationVocabulary;
	private Vocabulary diseaseQualifierVocabulary;
	private Vocabulary annotationTypeVocabulary;
	private Vocabulary noteTypeVocabulary;
	private Vocabulary conditionRelationTypeVocabulary;
	private VocabularyTerm geneDiseaseRelation;
	private VocabularyTerm alleleAndGeneDiseaseRelation;
	private VocabularyTerm agmDiseaseRelation;
	private VocabularyTerm agmDiseaseRelation2;
	private VocabularyTerm obsoleteAlleleDiseaseRelation;
	private VocabularyTerm obsoleteGeneDiseaseRelation;
	private VocabularyTerm obsoleteAgmDiseaseRelation;
	private VocabularyTerm geneticSex;
	private VocabularyTerm geneticSex2;
	private VocabularyTerm obsoleteGeneticSex;
	private VocabularyTerm diseaseGeneticModifierRelation;
	private VocabularyTerm diseaseGeneticModifierRelation2;
	private VocabularyTerm obsoleteDiseaseGeneticModifierRelation;
	private VocabularyTerm noteType;
	private VocabularyTerm noteType2;
	private VocabularyTerm obsoleteNoteType;
	private VocabularyTerm conditionRelationType;
	private VocabularyTerm conditionRelationType2;
	private VocabularyTerm obsoleteConditionRelationType;
	private VocabularyTerm diseaseQualifier;
	private VocabularyTerm diseaseQualifier2;
	private VocabularyTerm obsoleteDiseaseQualifier;
	private VocabularyTerm annotationType;
	private VocabularyTerm annotationType2;
	private VocabularyTerm obsoleteAnnotationType;
	private Person person;
	private OffsetDateTime datetime;
	private OffsetDateTime datetime2;
	private Note relatedNote;
	private ExperimentalCondition experimentalCondition;
	private ExperimentalCondition experimentalCondition2;
	private ConditionRelation conditionRelation;
	private Reference reference;
	private Reference reference2;
	private Reference obsoleteReference;
	private DataProvider dataProvider;
	private DataProvider dataProvider2;
	private DataProvider obsoleteDataProvider;
	private Vocabulary nameTypeVocabulary;
	private VocabularyTerm symbolNameType;
	private DOTerm nonPersistedDoTerm;
	private ECOTerm nonPersistedEcoTerm;
	private Organization nonPersistedOrganization;
	private Allele nonPersistedAllele;
	private Gene nonPersistedGene;
	private AffectedGenomicModel nonPersistedAgm;
	private Reference nonPersistedReference;
	private ExperimentalCondition nonPersistedCondition;
	
	
	private void loadRequiredEntities() {
		reference = createReference("AGRKB:100000005", false);
		reference2 = createReference("AGRKB:100000006", false);
		obsoleteReference = createReference("AGRKB:100000009", true);
		doTerm = createDoTerm("DOID:da0001", false);
		doTerm2 = createDoTerm("DOID:da0002", false);
		obsoleteDoTerm = createDoTerm("DOID:da0003", true);
		ecoTerm = createEcoTerm("ECO:da0001", "Test evidence code", false);
		ecoTerm2 = createEcoTerm("ECO:da0002", "Test evidence code2", false);
		obsoleteEcoTerm = createEcoTerm("ECO:da0003", "Test obsolete evidence code", true);
		nameTypeVocabulary = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		symbolNameType = getVocabularyTerm(nameTypeVocabulary, "nomenclature_symbol");
		gene = createGene("GENE:da0001", "NCBITaxon:9606", false, symbolNameType);
		gene2 = createGene("GENE:da0002", "NCBITaxon:9606", false, symbolNameType);
		obsoleteGene = createGene("HGNC:da0003", "NCBITaxon:9606", true, symbolNameType);
		withGene = createGene("HGNC:1", "NCBITaxon:9606", false, symbolNameType);
		withGene2 = createGene("HGNC:2", "NCBITaxon:9606", false, symbolNameType);
		allele = createAllele("ALLELE:da0001", "NCBITaxon:9606", false, symbolNameType);
		allele2 = createAllele("ALLELE:da0002", "NCBITaxon:9606", false, symbolNameType);
		obsoleteAllele = createAllele("ALLELE:da0003", "NCBITaxon:9606", true, symbolNameType);
		datetime = OffsetDateTime.parse("2022-03-09T22:10:12+00:00");
		datetime2 = OffsetDateTime.parse("2022-04-10T22:10:11+00:00");
		agm = createAffectedGenomicModel("SGD:da0001", "NCBITaxon:559292", "strain", "TestAGM", false);
		agm2 = createAffectedGenomicModel("SGD:da0002", "NCBITaxon:559292", "strain", "TestAGM2", false);
		obsoleteAgm = createAffectedGenomicModel("SGD:da0003", "NCBITaxon:559292", "strain", "TestAGM3", true);
		nonSgdAgm = createAffectedGenomicModel("WB:da0004", "NCBITaxon:6239", "genotype", "TestAGM4", false);
		experimentalCondition = createExperimentalCondition("Statement", "ZECO:da001", "Test");
		experimentalCondition2 = createExperimentalCondition("Statement2", "ZECO:da002", "Test2");
		diseaseRelationVocabulary = getVocabulary(VocabularyConstants.DISEASE_RELATION_VOCABULARY);
		noteTypeVocabulary = getVocabulary(VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY);
		geneticSexVocabulary = getVocabulary(VocabularyConstants.GENETIC_SEX_VOCABULARY);
		conditionRelationTypeVocabulary = getVocabulary(VocabularyConstants.CONDITION_RELATION_TYPE_VOCABULARY);
		diseaseGeneticModifierRelationVocabulary = getVocabulary(VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY);
		diseaseQualifierVocabulary = getVocabulary(VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY);
		annotationTypeVocabulary = getVocabulary(VocabularyConstants.ANNOTATION_TYPE_VOCABULARY);
		geneDiseaseRelation = getVocabularyTerm(diseaseRelationVocabulary, "is_marker_for");
		obsoleteGeneDiseaseRelation = addObsoleteVocabularyTermToSet(VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, "obsolete_gene_relation", diseaseRelationVocabulary);
		obsoleteAlleleDiseaseRelation = addObsoleteVocabularyTermToSet(VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET, "obsolete_allele_relation", diseaseRelationVocabulary);
		obsoleteAgmDiseaseRelation = addObsoleteVocabularyTermToSet(VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY_TERM_SET, "obsolete_agm_relation", diseaseRelationVocabulary);
		alleleAndGeneDiseaseRelation = getVocabularyTerm(diseaseRelationVocabulary, "is_implicated_in");
		agmDiseaseRelation = getVocabularyTerm(diseaseRelationVocabulary, "is_model_of");
		agmDiseaseRelation2 = getVocabularyTerm(diseaseRelationVocabulary, "is_exacerbated_model_of");
		diseaseQualifier = getVocabularyTerm(diseaseQualifierVocabulary, "severity");
		diseaseQualifier2 = createVocabularyTerm(diseaseQualifierVocabulary, "onset", false);
		obsoleteDiseaseQualifier = createVocabularyTerm(diseaseQualifierVocabulary, "obsolete_qualifier", true);
		geneticSex = createVocabularyTerm(geneticSexVocabulary,"hermaphrodite", false);
		geneticSex2 = getVocabularyTerm(geneticSexVocabulary,"female");
		obsoleteGeneticSex = createVocabularyTerm(geneticSexVocabulary, "obsolete_sex", true);
		diseaseGeneticModifierRelation = getVocabularyTerm(diseaseGeneticModifierRelationVocabulary, "ameliorated_by");
		diseaseGeneticModifierRelation2 = getVocabularyTerm(diseaseGeneticModifierRelationVocabulary, "exacerbated_by");
		obsoleteDiseaseGeneticModifierRelation = createVocabularyTerm(diseaseGeneticModifierRelationVocabulary, "obsolete_modifier_relation", true);
		annotationType = getVocabularyTerm(annotationTypeVocabulary,"computational");
		annotationType2 = getVocabularyTerm(annotationTypeVocabulary,"manually_curated");
		obsoleteAnnotationType = createVocabularyTerm(annotationTypeVocabulary, "obsolete_annotation_type", true);
		person = createPerson("TEST:Person0001");
		noteType = getVocabularyTerm(noteTypeVocabulary, "disease_note");
		noteType2 = getVocabularyTerm(noteTypeVocabulary, "disease_summary");
		obsoleteNoteType = createVocabularyTerm(noteTypeVocabulary, "obsolete_type", true);
		relatedNote = createNote(noteType, "Test text", false, null);
		conditionRelationType = createVocabularyTerm(conditionRelationTypeVocabulary, "has_condition", false);
		conditionRelationType2 = getVocabularyTerm(conditionRelationTypeVocabulary, "induced_by");
		obsoleteConditionRelationType = createVocabularyTerm(conditionRelationTypeVocabulary, "obsolete_relation_type", true);
		conditionRelation = createConditionRelation("test_handle", reference, conditionRelationType, List.of(experimentalCondition));
		dataProvider = createDataProvider("TEST", false);
		dataProvider2 = createDataProvider("TEST2", false);
		obsoleteDataProvider = createDataProvider("ODP", true);
		
		nonPersistedDoTerm = new DOTerm();
		nonPersistedDoTerm.setCurie("DO:Invalid");
		nonPersistedEcoTerm = new ECOTerm();
		nonPersistedEcoTerm.setCurie("ECO:Invalid");
		nonPersistedOrganization = new Organization();
		nonPersistedOrganization.setAbbreviation("INV");
		nonPersistedAllele = new Allele();
		nonPersistedAllele.setCurie("ALLELE:Invalid");
		nonPersistedGene = new Gene();
		nonPersistedGene.setCurie("GENE:Invalid");
		nonPersistedAgm = new AffectedGenomicModel();
		nonPersistedAgm.setCurie("AGM:Invalid");
		nonPersistedReference = new Reference();
		nonPersistedReference.setCurie("AGRKB:Invalid");
		nonPersistedCondition = new ExperimentalCondition();
		nonPersistedCondition.setUniqueId("Invalid");
	}
	
	@Test
	@Order(1)
	public void createGeneDiseaseAnnotation() {
		loadRequiredEntities();
		
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(alleleAndGeneDiseaseRelation);
		diseaseAnnotation.setModEntityId(GENE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDateCreated(datetime);
		diseaseAnnotation.setNegated(false);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setDataProvider(dataProvider);
		diseaseAnnotation.setSecondaryDataProvider(dataProvider2);
		diseaseAnnotation.setSubject(gene);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setConditionRelations(List.of(conditionRelation));
		diseaseAnnotation.setRelatedNotes(List.of(relatedNote));
		diseaseAnnotation.setSgdStrainBackground(agm);
		diseaseAnnotation.setWith(List.of(withGene));
		diseaseAnnotation.setAnnotationType(annotationType);
		diseaseAnnotation.setDiseaseQualifiers(List.of(diseaseQualifier));
		diseaseAnnotation.setGeneticSex(geneticSex);
		diseaseAnnotation.setDiseaseGeneticModifier(agm2);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);

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
			body("entity.subject.curie", is(gene.getCurie())).
			body("entity.object.curie", is(doTerm.getCurie())).
			body("entity.diseaseRelation.name", is(alleleAndGeneDiseaseRelation.getName())).
			body("entity.negated", is(false)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.singleReference.curie", is(reference.getCurie())).
			body("entity.evidenceCodes[0].curie", is(ecoTerm.getCurie())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dateCreated", is(datetime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneticSex.name", is(geneticSex.getName())).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation.getName())).
			body("entity.diseaseGeneticModifier.curie", is(agm2.getCurie())).
			body("entity.annotationType.name", is(annotationType.getName())).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier.getName())).
			body("entity.with[0].curie", is(withGene.getCurie())).
			body("entity.sgdStrainBackground.curie", is(agm.getCurie())).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(relatedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(relatedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].handle", is(conditionRelation.getHandle())).
			body("entity.conditionRelations[0].singleReference.curie", is(conditionRelation.getSingleReference().getCurie())).
			body("entity.conditionRelations[0].conditionRelationType.name", is(conditionRelation.getConditionRelationType().getName())).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is(conditionRelation.getConditions().get(0).getConditionSummary())).
			body("entity.conditionRelations[0].internal", is(false)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation())).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation()));
	}
	
	@Test
	@Order(2)
	public void createAlleleDiseaseAnnotation() {
		AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(alleleAndGeneDiseaseRelation);
		diseaseAnnotation.setModEntityId(ALLELE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDateCreated(datetime);
		diseaseAnnotation.setNegated(false);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setDataProvider(dataProvider);
		diseaseAnnotation.setSecondaryDataProvider(dataProvider2);
		diseaseAnnotation.setSubject(allele);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setConditionRelations(List.of(conditionRelation));
		diseaseAnnotation.setRelatedNotes(List.of(relatedNote));
		diseaseAnnotation.setWith(List.of(withGene));
		diseaseAnnotation.setAnnotationType(annotationType);
		diseaseAnnotation.setDiseaseQualifiers(List.of(diseaseQualifier));
		diseaseAnnotation.setGeneticSex(geneticSex);
		diseaseAnnotation.setDiseaseGeneticModifier(agm2);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		diseaseAnnotation.setInferredGene(gene);
		diseaseAnnotation.setAssertedGenes(List.of(gene2));

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
			body("entity.subject.curie", is(allele.getCurie())).
			body("entity.object.curie", is(doTerm.getCurie())).
			body("entity.diseaseRelation.name", is(alleleAndGeneDiseaseRelation.getName())).
			body("entity.negated", is(false)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.singleReference.curie", is(reference.getCurie())).
			body("entity.evidenceCodes[0].curie", is(ecoTerm.getCurie())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dateCreated", is(datetime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneticSex.name", is(geneticSex.getName())).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation.getName())).
			body("entity.diseaseGeneticModifier.curie", is(agm2.getCurie())).
			body("entity.annotationType.name", is(annotationType.getName())).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier.getName())).
			body("entity.with[0].curie", is(withGene.getCurie())).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(relatedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(relatedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].handle", is(conditionRelation.getHandle())).
			body("entity.conditionRelations[0].singleReference.curie", is(conditionRelation.getSingleReference().getCurie())).
			body("entity.conditionRelations[0].conditionRelationType.name", is(conditionRelation.getConditionRelationType().getName())).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is(conditionRelation.getConditions().get(0).getConditionSummary())).
			body("entity.conditionRelations[0].internal", is(false)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation())).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation())).
			body("entity.inferredGene.curie", is(gene.getCurie())).
			body("entity.assertedGenes", hasSize(1)).
			body("entity.assertedGenes[0].curie", is(gene2.getCurie()));
	}
	
	@Test
	@Order(3)
	public void createAgmDiseaseAnnotation() {
		AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		diseaseAnnotation.setModEntityId(AGM_DISEASE_ANNOTATION);
		diseaseAnnotation.setDateCreated(datetime);
		diseaseAnnotation.setNegated(false);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setDataProvider(dataProvider);
		diseaseAnnotation.setSecondaryDataProvider(dataProvider2);
		diseaseAnnotation.setSubject(agm);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setConditionRelations(List.of(conditionRelation));
		diseaseAnnotation.setRelatedNotes(List.of(relatedNote));
		diseaseAnnotation.setWith(List.of(withGene));
		diseaseAnnotation.setAnnotationType(annotationType);
		diseaseAnnotation.setDiseaseQualifiers(List.of(diseaseQualifier));
		diseaseAnnotation.setGeneticSex(geneticSex);
		diseaseAnnotation.setDiseaseGeneticModifier(agm2);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		diseaseAnnotation.setInferredGene(gene);
		diseaseAnnotation.setAssertedGenes(List.of(gene2));
		diseaseAnnotation.setInferredAllele(allele);
		diseaseAnnotation.setAssertedAllele(allele2);

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
			body("entity.subject.curie", is(agm.getCurie())).
			body("entity.object.curie", is(doTerm.getCurie())).
			body("entity.diseaseRelation.name", is(agmDiseaseRelation.getName())).
			body("entity.negated", is(false)).
			body("entity.internal", is(false)).
			body("entity.obsolete", is(false)).
			body("entity.singleReference.curie", is(reference.getCurie())).
			body("entity.evidenceCodes[0].curie", is(ecoTerm.getCurie())).
			body("entity.createdBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dateCreated", is(datetime.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneticSex.name", is(geneticSex.getName())).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation.getName())).
			body("entity.diseaseGeneticModifier.curie", is(agm2.getCurie())).
			body("entity.annotationType.name", is(annotationType.getName())).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier.getName())).
			body("entity.with[0].curie", is(withGene.getCurie())).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(relatedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(relatedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(false)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].handle", is(conditionRelation.getHandle())).
			body("entity.conditionRelations[0].singleReference.curie", is(conditionRelation.getSingleReference().getCurie())).
			body("entity.conditionRelations[0].conditionRelationType.name", is(conditionRelation.getConditionRelationType().getName())).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is(conditionRelation.getConditions().get(0).getConditionSummary())).
			body("entity.conditionRelations[0].internal", is(false)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation())).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation())).
			body("entity.inferredGene.curie", is(gene.getCurie())).
			body("entity.assertedGenes", hasSize(1)).
			body("entity.assertedGenes[0].curie", is(gene2.getCurie())).
			body("entity.inferredAllele.curie", is(allele.getCurie())).
			body("entity.assertedAllele.curie", is(allele2.getCurie()));
	}
	
	@Test
	@Order(4)
	public void editGeneDiseaseAnnotation() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		diseaseAnnotation.setDateCreated(datetime2);
		diseaseAnnotation.setObsolete(true);
		diseaseAnnotation.setInternal(true);
		diseaseAnnotation.setCreatedBy(person);
		diseaseAnnotation.setNegated(true);
		diseaseAnnotation.setObject(doTerm2);
		diseaseAnnotation.setDataProvider(dataProvider2);
		diseaseAnnotation.setSecondaryDataProvider(dataProvider);
		diseaseAnnotation.setSubject(gene2);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm2));
		diseaseAnnotation.setSingleReference(reference2);
		diseaseAnnotation.setSgdStrainBackground(agm2);
		diseaseAnnotation.setWith(List.of(withGene2));
		diseaseAnnotation.setAnnotationType(annotationType2);
		diseaseAnnotation.setDiseaseQualifiers(List.of(diseaseQualifier2));
		diseaseAnnotation.setGeneticSex(geneticSex2);
		diseaseAnnotation.setDiseaseGeneticModifier(agm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation2);
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(noteType2);
		editedNote.setFreeText("Edited text");
		editedNote.setInternal(true);
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(conditionRelationType2);
		editedRelation.setHandle("test_handle_2");
		editedRelation.setSingleReference(reference2);
		editedRelation.setConditions(List.of(experimentalCondition2));
		editedRelation.setInternal(true);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
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
			body("entity.modEntityId", is(GENE_DISEASE_ANNOTATION)).
			body("entity.subject.curie", is(gene2.getCurie())).
			body("entity.object.curie", is(doTerm2.getCurie())).
			body("entity.diseaseRelation.name", is(geneDiseaseRelation.getName())).
			body("entity.negated", is(true)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.singleReference.curie", is(reference2.getCurie())).
			body("entity.evidenceCodes[0].curie", is(ecoTerm2.getCurie())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dateCreated", is(datetime2.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneticSex.name", is(geneticSex2.getName())).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation2.getName())).
			body("entity.diseaseGeneticModifier.curie", is(agm.getCurie())).
			body("entity.annotationType.name", is(annotationType2.getName())).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier2.getName())).
			body("entity.with[0].curie", is(withGene2.getCurie())).
			body("entity.sgdStrainBackground.curie", is(agm2.getCurie())).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(editedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(editedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].handle", is(editedRelation.getHandle())).
			body("entity.conditionRelations[0].singleReference.curie", is(editedRelation.getSingleReference().getCurie())).
			body("entity.conditionRelations[0].conditionRelationType.name", is(editedRelation.getConditionRelationType().getName())).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is(editedRelation.getConditions().get(0).getConditionSummary())).
			body("entity.conditionRelations[0].internal", is(true)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation())).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation()));
	}
	
	@Test
	@Order(5)
	public void editAlleleDiseaseAnnotation() {
		AlleleDiseaseAnnotation diseaseAnnotation = getAlleleDiseaseAnnotation(ALLELE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDateCreated(datetime2);
		diseaseAnnotation.setObsolete(true);
		diseaseAnnotation.setInternal(true);
		diseaseAnnotation.setCreatedBy(person);
		diseaseAnnotation.setNegated(true);
		diseaseAnnotation.setObject(doTerm2);
		diseaseAnnotation.setDataProvider(dataProvider2);
		diseaseAnnotation.setSecondaryDataProvider(dataProvider);
		diseaseAnnotation.setSubject(allele2);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm2));
		diseaseAnnotation.setSingleReference(reference2);
		diseaseAnnotation.setWith(List.of(withGene2));
		diseaseAnnotation.setAnnotationType(annotationType2);
		diseaseAnnotation.setDiseaseQualifiers(List.of(diseaseQualifier2));
		diseaseAnnotation.setGeneticSex(geneticSex2);
		diseaseAnnotation.setDiseaseGeneticModifier(agm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation2);
		diseaseAnnotation.setInferredGene(gene2);
		diseaseAnnotation.setAssertedGenes(List.of(gene));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(noteType2);
		editedNote.setFreeText("Edited text");
		editedNote.setInternal(true);
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(conditionRelationType2);
		editedRelation.setHandle("test_handle_2");
		editedRelation.setSingleReference(reference2);
		editedRelation.setConditions(List.of(experimentalCondition2));
		editedRelation.setInternal(true);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
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
			body("entity.modEntityId", is(ALLELE_DISEASE_ANNOTATION)).
			body("entity.subject.curie", is(allele2.getCurie())).
			body("entity.object.curie", is(doTerm2.getCurie())).
			body("entity.diseaseRelation.name", is(alleleAndGeneDiseaseRelation.getName())).
			body("entity.negated", is(true)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.singleReference.curie", is(reference2.getCurie())).
			body("entity.evidenceCodes[0].curie", is(ecoTerm2.getCurie())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dateCreated", is(datetime2.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneticSex.name", is(geneticSex2.getName())).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation2.getName())).
			body("entity.diseaseGeneticModifier.curie", is(agm.getCurie())).
			body("entity.annotationType.name", is(annotationType2.getName())).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier2.getName())).
			body("entity.with[0].curie", is(withGene2.getCurie())).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(editedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(editedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].handle", is(editedRelation.getHandle())).
			body("entity.conditionRelations[0].singleReference.curie", is(editedRelation.getSingleReference().getCurie())).
			body("entity.conditionRelations[0].conditionRelationType.name", is(editedRelation.getConditionRelationType().getName())).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is(editedRelation.getConditions().get(0).getConditionSummary())).
			body("entity.conditionRelations[0].internal", is(true)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation())).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation())).
			body("entity.inferredGene.curie", is(gene2.getCurie())).
			body("entity.assertedGenes", hasSize(1)).
			body("entity.assertedGenes[0].curie", is(gene.getCurie()));
	}
	
	@Test
	@Order(6)
	public void editAgmDiseaseAnnotation() {
		AGMDiseaseAnnotation diseaseAnnotation = getAgmDiseaseAnnotation(AGM_DISEASE_ANNOTATION);
		diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation2);
		diseaseAnnotation.setDateCreated(datetime2);
		diseaseAnnotation.setObsolete(true);
		diseaseAnnotation.setInternal(true);
		diseaseAnnotation.setCreatedBy(person);
		diseaseAnnotation.setNegated(true);
		diseaseAnnotation.setObject(doTerm2);
		diseaseAnnotation.setDataProvider(dataProvider2);
		diseaseAnnotation.setSecondaryDataProvider(dataProvider);
		diseaseAnnotation.setSubject(agm2);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm2));
		diseaseAnnotation.setSingleReference(reference2);
		diseaseAnnotation.setWith(List.of(withGene2));
		diseaseAnnotation.setAnnotationType(annotationType2);
		diseaseAnnotation.setDiseaseQualifiers(List.of(diseaseQualifier2));
		diseaseAnnotation.setGeneticSex(geneticSex2);
		diseaseAnnotation.setDiseaseGeneticModifier(agm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation2);
		diseaseAnnotation.setInferredGene(gene2);
		diseaseAnnotation.setAssertedGenes(List.of(gene));
		diseaseAnnotation.setInferredAllele(allele2);
		diseaseAnnotation.setAssertedAllele(allele);
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(noteType2);
		editedNote.setFreeText("Edited text");
		editedNote.setInternal(true);
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(conditionRelationType2);
		editedRelation.setHandle("test_handle_2");
		editedRelation.setSingleReference(reference2);
		editedRelation.setConditions(List.of(experimentalCondition2));
		editedRelation.setInternal(true);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
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
			body("entity.modEntityId", is(AGM_DISEASE_ANNOTATION)).
			body("entity.subject.curie", is(agm2.getCurie())).
			body("entity.object.curie", is(doTerm2.getCurie())).
			body("entity.diseaseRelation.name", is(agmDiseaseRelation2.getName())).
			body("entity.negated", is(true)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.singleReference.curie", is(reference2.getCurie())).
			body("entity.evidenceCodes[0].curie", is(ecoTerm2.getCurie())).
			body("entity.createdBy.uniqueId", is(person.getUniqueId())).
			body("entity.updatedBy.uniqueId", is("Local|Dev User|test@alliancegenome.org")).
			body("entity.dateCreated", is(datetime2.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.geneticSex.name", is(geneticSex2.getName())).
			body("entity.diseaseGeneticModifierRelation.name", is(diseaseGeneticModifierRelation2.getName())).
			body("entity.diseaseGeneticModifier.curie", is(agm.getCurie())).
			body("entity.annotationType.name", is(annotationType2.getName())).
			body("entity.diseaseQualifiers[0].name", is(diseaseQualifier2.getName())).
			body("entity.with[0].curie", is(withGene2.getCurie())).
			body("entity.relatedNotes", hasSize(1)).
			body("entity.relatedNotes[0].noteType.name", is(editedNote.getNoteType().getName())).
			body("entity.relatedNotes[0].freeText", is(editedNote.getFreeText())).
			body("entity.relatedNotes[0].internal", is(true)).
			body("entity.conditionRelations", hasSize(1)).
			body("entity.conditionRelations[0].handle", is(editedRelation.getHandle())).
			body("entity.conditionRelations[0].singleReference.curie", is(editedRelation.getSingleReference().getCurie())).
			body("entity.conditionRelations[0].conditionRelationType.name", is(editedRelation.getConditionRelationType().getName())).
			body("entity.conditionRelations[0].conditions[0].conditionSummary", is(editedRelation.getConditions().get(0).getConditionSummary())).
			body("entity.conditionRelations[0].internal", is(true)).
			body("entity.dataProvider.sourceOrganization.abbreviation", is(dataProvider2.getSourceOrganization().getAbbreviation())).
			body("entity.secondaryDataProvider.sourceOrganization.abbreviation", is(dataProvider.getSourceOrganization().getAbbreviation())).
			body("entity.inferredGene.curie", is(gene2.getCurie())).
			body("entity.assertedGenes", hasSize(1)).
			body("entity.assertedGenes[0].curie", is(gene.getCurie())).
			body("entity.inferredAllele.curie", is(allele2.getCurie())).
			body("entity.assertedAllele.curie", is(allele.getCurie()));
	}
	
	@Test
	@Order(7)
	public void createGeneDiseaseAnnotationWithMissingRequiredFieldsLevel1() {
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.subject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(8)
	public void createAlleleDiseaseAnnotationWithMissingRequiredFieldsLevel1() {
		AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.subject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(9)
	public void createAgmDiseaseAnnotationWithMissingRequiredFieldsLevel1() {
		AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.subject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(10)
	public void editGeneDiseaseAnnotationWithMissingRequiredFieldsLevel1() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		diseaseAnnotation.setSubject(null);
		diseaseAnnotation.setObject(null);
		diseaseAnnotation.setDiseaseRelation(null);
		diseaseAnnotation.setEvidenceCodes(null);
		diseaseAnnotation.setSingleReference(null);
		diseaseAnnotation.setDataProvider(null);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.subject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(11)
	public void editAlleleDiseaseAnnotationWithMissingRequiredFieldsLevel1() {
		AlleleDiseaseAnnotation diseaseAnnotation = getAlleleDiseaseAnnotation(ALLELE_DISEASE_ANNOTATION);
		diseaseAnnotation.setSubject(null);
		diseaseAnnotation.setObject(null);
		diseaseAnnotation.setDiseaseRelation(null);
		diseaseAnnotation.setEvidenceCodes(null);
		diseaseAnnotation.setSingleReference(null);
		diseaseAnnotation.setDataProvider(null);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.subject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(12)
	public void editAgmDiseaseAnnotationWithMissingRequiredFieldsLevel1() {
		AGMDiseaseAnnotation diseaseAnnotation = getAgmDiseaseAnnotation(AGM_DISEASE_ANNOTATION);
		diseaseAnnotation.setSubject(null);
		diseaseAnnotation.setObject(null);
		diseaseAnnotation.setDiseaseRelation(null);
		diseaseAnnotation.setEvidenceCodes(null);
		diseaseAnnotation.setSingleReference(null);
		diseaseAnnotation.setDataProvider(null);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(6))).
			body("errorMessages.subject", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(13)
	public void createGeneDiseaseAnnotationWithMissingRequiredFieldsLevel2() {
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setModEntityId("GeneDisease:0013");
		diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setSubject(gene);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		
		DataProvider newDataProvider = new DataProvider();
		diseaseAnnotation.setDataProvider(newDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(newDataProvider);
		ConditionRelation newRelation = new ConditionRelation();
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		Note newNote = new Note();
		diseaseAnnotation.setRelatedNotes(List.of(newNote));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.REQUIRED_MESSAGE,
					"conditions - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(14)
	public void createAlleleDiseaseAnnotationWithMissingRequiredFieldsLevel2() {
		AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();
		diseaseAnnotation.setModEntityId("AlleleDisease:0014");
		diseaseAnnotation.setDiseaseRelation(alleleAndGeneDiseaseRelation);
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setSubject(allele);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		
		DataProvider newDataProvider = new DataProvider();
		diseaseAnnotation.setDataProvider(newDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(newDataProvider);
		ConditionRelation newRelation = new ConditionRelation();
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		Note newNote = new Note();
		diseaseAnnotation.setRelatedNotes(List.of(newNote));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.REQUIRED_MESSAGE,
					"conditions - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(15)
	public void createAgmDiseaseAnnotationWithMissingRequiredFieldsLevel2() {
		AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();
		diseaseAnnotation.setModEntityId("AGMDisease:0016");
		diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setSubject(agm);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		
		DataProvider newDataProvider = new DataProvider();
		diseaseAnnotation.setDataProvider(newDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(newDataProvider);
		ConditionRelation newRelation = new ConditionRelation();
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		Note newNote = new Note();
		diseaseAnnotation.setRelatedNotes(List.of(newNote));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.REQUIRED_MESSAGE,
					"conditions - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(16)
	public void editGeneDiseaseAnnotationWithMissingRequiredFieldsLevel2() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(null);
		editedRelation.setConditions(null);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(null);
		editedNote.setFreeText(null);
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));
		
		DataProvider editedDataProvider = diseaseAnnotation.getDataProvider();
		editedDataProvider.setSourceOrganization(null);
		diseaseAnnotation.setDataProvider(editedDataProvider);
		DataProvider editedSecondaryDataProvider = diseaseAnnotation.getSecondaryDataProvider();
		editedSecondaryDataProvider.setSourceOrganization(null);
		diseaseAnnotation.setSecondaryDataProvider(editedSecondaryDataProvider);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.REQUIRED_MESSAGE,
					"conditions - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(17)
	public void editAlleleDiseaseAnnotationWithMissingRequiredFieldsLevel2() {
		AlleleDiseaseAnnotation diseaseAnnotation = getAlleleDiseaseAnnotation(ALLELE_DISEASE_ANNOTATION);
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(null);
		editedRelation.setConditions(null);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(null);
		editedNote.setFreeText(null);
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));
		
		DataProvider editedDataProvider = diseaseAnnotation.getDataProvider();
		editedDataProvider.setSourceOrganization(null);
		diseaseAnnotation.setDataProvider(editedDataProvider);
		DataProvider editedSecondaryDataProvider = diseaseAnnotation.getSecondaryDataProvider();
		editedSecondaryDataProvider.setSourceOrganization(null);
		diseaseAnnotation.setSecondaryDataProvider(editedSecondaryDataProvider);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.REQUIRED_MESSAGE,
					"conditions - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(18)
	public void editAgmDiseaseAnnotationWithMissingRequiredFieldsLevel2() {
		AGMDiseaseAnnotation diseaseAnnotation = getAgmDiseaseAnnotation(AGM_DISEASE_ANNOTATION);
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(null);
		editedRelation.setConditions(null);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(null);
		editedNote.setFreeText(null);
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));
		
		DataProvider editedDataProvider = diseaseAnnotation.getDataProvider();
		editedDataProvider.setSourceOrganization(null);
		diseaseAnnotation.setDataProvider(editedDataProvider);
		DataProvider editedSecondaryDataProvider = diseaseAnnotation.getSecondaryDataProvider();
		editedSecondaryDataProvider.setSourceOrganization(null);
		diseaseAnnotation.setSecondaryDataProvider(editedSecondaryDataProvider);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(4))).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.REQUIRED_MESSAGE,
					"conditions - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"freeText - " + ValidationConstants.REQUIRED_MESSAGE,
					"noteType - " + ValidationConstants.REQUIRED_MESSAGE)))).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(19)
	public void createGeneDiseaseAnnotationWithEmptyRequiredFieldsLevel2() {
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setModEntityId("GeneDisease:0019");
		diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setSubject(gene);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setDataProvider(dataProvider);
		
		Note newNote = new Note();
		newNote.setNoteType(noteType);
		newNote.setFreeText("");
		diseaseAnnotation.setRelatedNotes(List.of(newNote));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(20)
	public void createAlleleDiseaseAnnotationWithEmptyRequiredFieldsLevel2() {
		AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();
		diseaseAnnotation.setModEntityId("AlleleDisease:0020");
		diseaseAnnotation.setDiseaseRelation(alleleAndGeneDiseaseRelation);
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setSubject(allele);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setDataProvider(dataProvider);
		
		Note newNote = new Note();
		newNote.setNoteType(noteType);
		newNote.setFreeText("");
		diseaseAnnotation.setRelatedNotes(List.of(newNote));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(21)
	public void createAgmDiseaseAnnotationWithEmptyRequiredFieldsLevel2() {
		AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();
		diseaseAnnotation.setModEntityId("AgmDisease:0021");
		diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setSubject(agm);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setDataProvider(dataProvider);
		
		Note newNote = new Note();
		newNote.setNoteType(noteType);
		newNote.setFreeText("");
		diseaseAnnotation.setRelatedNotes(List.of(newNote));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(22)
	public void editGeneDiseaseAnnotationWithEmptyRequiredFieldsLevel2() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setFreeText("");
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(23)
	public void editAlleleDiseaseAnnotationWithEmptyRequiredFieldsLevel2() {
		AlleleDiseaseAnnotation diseaseAnnotation = getAlleleDiseaseAnnotation(ALLELE_DISEASE_ANNOTATION);
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setFreeText("");
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(24)
	public void editAgmDiseaseAnnotationWithEmptyRequiredFieldsLevel2() {
		AGMDiseaseAnnotation diseaseAnnotation = getAgmDiseaseAnnotation(AGM_DISEASE_ANNOTATION);
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setFreeText("");
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.relatedNotes", is("freeText - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(25)
	public void createGeneDiseaseAnnotationWithInvalidFields() {
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		diseaseAnnotation.setObject(nonPersistedDoTerm);
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		diseaseAnnotation.setDataProvider(invalidDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(invalidDataProvider);
		diseaseAnnotation.setSubject(nonPersistedGene);
		diseaseAnnotation.setEvidenceCodes(List.of(nonPersistedEcoTerm));
		diseaseAnnotation.setSingleReference(nonPersistedReference);
		diseaseAnnotation.setSgdStrainBackground(nonSgdAgm);
		diseaseAnnotation.setWith(List.of(gene));
		diseaseAnnotation.setAnnotationType(diseaseQualifier);
		diseaseAnnotation.setDiseaseQualifiers(List.of(geneticSex));
		diseaseAnnotation.setGeneticSex(annotationType);
		diseaseAnnotation.setDiseaseGeneticModifier(nonPersistedAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(agmDiseaseRelation);
		
		ConditionRelation newRelation = new ConditionRelation();
		newRelation.setConditionRelationType(geneticSex);
		newRelation.setSingleReference(nonPersistedReference);
		newRelation.setConditions(List.of(nonPersistedCondition));
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		
		Note newNote = new Note();
		newNote.setNoteType(annotationType);
		newNote.setReferences(List.of(nonPersistedReference));
		newNote.setFreeText("Invalid");
		diseaseAnnotation.setRelatedNotes(List.of(newNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(16))).
			body("errorMessages.subject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.sgdStrainBackground", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.INVALID_MESSAGE,
					"conditions - " + ValidationConstants.INVALID_MESSAGE,
					"singleReference - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(26)
	public void createAlleleDiseaseAnnotationWithInvalidFields() {
		AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		diseaseAnnotation.setObject(nonPersistedDoTerm);
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		diseaseAnnotation.setDataProvider(invalidDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(invalidDataProvider);
		diseaseAnnotation.setSubject(nonPersistedAllele);
		diseaseAnnotation.setEvidenceCodes(List.of(nonPersistedEcoTerm));
		diseaseAnnotation.setSingleReference(nonPersistedReference);
		diseaseAnnotation.setWith(List.of(gene));
		diseaseAnnotation.setAnnotationType(diseaseQualifier);
		diseaseAnnotation.setDiseaseQualifiers(List.of(geneticSex));
		diseaseAnnotation.setGeneticSex(annotationType);
		diseaseAnnotation.setDiseaseGeneticModifier(nonPersistedAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(agmDiseaseRelation);
		diseaseAnnotation.setInferredGene(nonPersistedGene);
		diseaseAnnotation.setAssertedGenes(List.of(nonPersistedGene));
		
		ConditionRelation newRelation = new ConditionRelation();
		newRelation.setConditionRelationType(geneticSex);
		newRelation.setSingleReference(nonPersistedReference);
		newRelation.setConditions(List.of(nonPersistedCondition));
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		
		Note newNote = new Note();
		newNote.setNoteType(annotationType);
		newNote.setReferences(List.of(nonPersistedReference));
		newNote.setFreeText("Invalid");
		diseaseAnnotation.setRelatedNotes(List.of(newNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(17))).
			body("errorMessages.subject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inferredGene", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.assertedGenes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.INVALID_MESSAGE,
					"conditions - " + ValidationConstants.INVALID_MESSAGE,
					"singleReference - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(27)
	public void createAgmDiseaseAnnotationWithInvalidFields() {
		AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		diseaseAnnotation.setObject(nonPersistedDoTerm);
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		diseaseAnnotation.setDataProvider(invalidDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(invalidDataProvider);
		diseaseAnnotation.setSubject(nonPersistedAgm);
		diseaseAnnotation.setEvidenceCodes(List.of(nonPersistedEcoTerm));
		diseaseAnnotation.setSingleReference(nonPersistedReference);
		diseaseAnnotation.setWith(List.of(gene));
		diseaseAnnotation.setAnnotationType(diseaseQualifier);
		diseaseAnnotation.setDiseaseQualifiers(List.of(geneticSex));
		diseaseAnnotation.setGeneticSex(annotationType);
		diseaseAnnotation.setDiseaseGeneticModifier(nonPersistedAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(agmDiseaseRelation);
		diseaseAnnotation.setInferredGene(nonPersistedGene);
		diseaseAnnotation.setAssertedGenes(List.of(nonPersistedGene));
		diseaseAnnotation.setInferredAllele(nonPersistedAllele);
		diseaseAnnotation.setAssertedAllele(nonPersistedAllele);
		
		ConditionRelation newRelation = new ConditionRelation();
		newRelation.setConditionRelationType(geneticSex);
		newRelation.setSingleReference(nonPersistedReference);
		newRelation.setConditions(List.of(nonPersistedCondition));
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		
		Note newNote = new Note();
		newNote.setNoteType(annotationType);
		newNote.setReferences(List.of(nonPersistedReference));
		newNote.setFreeText("Invalid");
		diseaseAnnotation.setRelatedNotes(List.of(newNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(19))).
			body("errorMessages.subject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inferredGene", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.assertedGenes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inferredAllele", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.assertedAllele", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.INVALID_MESSAGE,
					"conditions - " + ValidationConstants.INVALID_MESSAGE,
					"singleReference - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(28)
	public void editGeneDiseaseAnnotationWithInvalidFields() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		diseaseAnnotation.setObject(nonPersistedDoTerm);
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		diseaseAnnotation.setDataProvider(invalidDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(invalidDataProvider);
		diseaseAnnotation.setSubject(nonPersistedGene);
		diseaseAnnotation.setEvidenceCodes(List.of(nonPersistedEcoTerm));
		diseaseAnnotation.setSingleReference(nonPersistedReference);
		diseaseAnnotation.setSgdStrainBackground(nonSgdAgm);
		diseaseAnnotation.setWith(List.of(gene));
		diseaseAnnotation.setAnnotationType(diseaseQualifier);
		diseaseAnnotation.setDiseaseQualifiers(List.of(geneticSex));
		diseaseAnnotation.setGeneticSex(annotationType);
		diseaseAnnotation.setDiseaseGeneticModifier(nonPersistedAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(agmDiseaseRelation);
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(geneticSex);
		editedRelation.setSingleReference(nonPersistedReference);
		editedRelation.setConditions(List.of(nonPersistedCondition));
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(annotationType);
		editedNote.setReferences(List.of(nonPersistedReference));
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(16))).
			body("errorMessages.subject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.sgdStrainBackground", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.INVALID_MESSAGE,
					"conditions - " + ValidationConstants.INVALID_MESSAGE,
					"singleReference - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(29)
	public void editAlleleDiseaseAnnotationWithInvalidFields() {
		AlleleDiseaseAnnotation diseaseAnnotation = getAlleleDiseaseAnnotation(ALLELE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDiseaseRelation(agmDiseaseRelation);
		diseaseAnnotation.setObject(nonPersistedDoTerm);
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		diseaseAnnotation.setDataProvider(invalidDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(invalidDataProvider);
		diseaseAnnotation.setSubject(nonPersistedAllele);
		diseaseAnnotation.setEvidenceCodes(List.of(nonPersistedEcoTerm));
		diseaseAnnotation.setSingleReference(nonPersistedReference);
		diseaseAnnotation.setWith(List.of(gene));
		diseaseAnnotation.setAnnotationType(diseaseQualifier);
		diseaseAnnotation.setDiseaseQualifiers(List.of(geneticSex));
		diseaseAnnotation.setGeneticSex(annotationType);
		diseaseAnnotation.setDiseaseGeneticModifier(nonPersistedAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(agmDiseaseRelation);
		diseaseAnnotation.setInferredGene(nonPersistedGene);
		diseaseAnnotation.setAssertedGenes(List.of(nonPersistedGene));
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(geneticSex);
		editedRelation.setSingleReference(nonPersistedReference);
		editedRelation.setConditions(List.of(nonPersistedCondition));
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(annotationType);
		editedNote.setReferences(List.of(nonPersistedReference));
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(17))).
			body("errorMessages.subject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inferredGene", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.assertedGenes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.INVALID_MESSAGE,
					"conditions - " + ValidationConstants.INVALID_MESSAGE,
					"singleReference - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(30)
	public void editAgmDiseaseAnnotationWithInvalidFields() {
		AGMDiseaseAnnotation diseaseAnnotation = getAgmDiseaseAnnotation(AGM_DISEASE_ANNOTATION);
		diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		diseaseAnnotation.setObject(nonPersistedDoTerm);
		DataProvider invalidDataProvider = new DataProvider();
		invalidDataProvider.setSourceOrganization(nonPersistedOrganization);
		diseaseAnnotation.setDataProvider(invalidDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(invalidDataProvider);
		diseaseAnnotation.setSubject(nonPersistedAgm);
		diseaseAnnotation.setEvidenceCodes(List.of(nonPersistedEcoTerm));
		diseaseAnnotation.setSingleReference(nonPersistedReference);
		diseaseAnnotation.setWith(List.of(gene));
		diseaseAnnotation.setAnnotationType(diseaseQualifier);
		diseaseAnnotation.setDiseaseQualifiers(List.of(geneticSex));
		diseaseAnnotation.setGeneticSex(annotationType);
		diseaseAnnotation.setDiseaseGeneticModifier(nonPersistedAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(agmDiseaseRelation);
		diseaseAnnotation.setInferredGene(nonPersistedGene);
		diseaseAnnotation.setAssertedGenes(List.of(nonPersistedGene));
		diseaseAnnotation.setInferredAllele(nonPersistedAllele);
		diseaseAnnotation.setAssertedAllele(nonPersistedAllele);
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(geneticSex);
		editedRelation.setSingleReference(nonPersistedReference);
		editedRelation.setConditions(List.of(nonPersistedCondition));
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(annotationType);
		editedNote.setReferences(List.of(nonPersistedReference));
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(19))).
			body("errorMessages.subject", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.dataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is("sourceOrganization - " + ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inferredGene", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.assertedGenes", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.inferredAllele", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.assertedAllele", is(ValidationConstants.INVALID_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.INVALID_MESSAGE,
					"conditions - " + ValidationConstants.INVALID_MESSAGE,
					"singleReference - " + ValidationConstants.INVALID_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.INVALID_MESSAGE,
					"references - " + ValidationConstants.INVALID_MESSAGE))));
	}
	
	@Test
	@Order(31)
	public void createGeneDiseaseAnnotationWithObsoleteFields() {
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(obsoleteGeneDiseaseRelation);
		diseaseAnnotation.setObject(obsoleteDoTerm);
		diseaseAnnotation.setDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSubject(obsoleteGene);
		diseaseAnnotation.setEvidenceCodes(List.of(obsoleteEcoTerm));
		diseaseAnnotation.setSingleReference(obsoleteReference);
		diseaseAnnotation.setSgdStrainBackground(obsoleteAgm);
		diseaseAnnotation.setWith(List.of(obsoleteGene));
		diseaseAnnotation.setAnnotationType(obsoleteAnnotationType);
		diseaseAnnotation.setDiseaseQualifiers(List.of(obsoleteDiseaseQualifier));
		diseaseAnnotation.setGeneticSex(obsoleteGeneticSex);
		diseaseAnnotation.setDiseaseGeneticModifier(obsoleteAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(obsoleteDiseaseGeneticModifierRelation);
		
		ConditionRelation newRelation = new ConditionRelation();
		newRelation.setConditionRelationType(obsoleteConditionRelationType);
		newRelation.setSingleReference(obsoleteReference);
		newRelation.setConditions(List.of(experimentalCondition));
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		
		Note newNote = new Note();
		newNote.setNoteType(obsoleteNoteType);
		newNote.setReferences(List.of(obsoleteReference));
		newNote.setFreeText("Obsolete");
		diseaseAnnotation.setRelatedNotes(List.of(newNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(16))).
			body("errorMessages.subject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.sgdStrainBackground", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"singleReference - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(32)
	public void createAlleleDiseaseAnnotationWithObsoleteFields() {
		AlleleDiseaseAnnotation diseaseAnnotation = new AlleleDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(obsoleteAlleleDiseaseRelation);
		diseaseAnnotation.setObject(obsoleteDoTerm);
		diseaseAnnotation.setDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSubject(obsoleteAllele);
		diseaseAnnotation.setEvidenceCodes(List.of(obsoleteEcoTerm));
		diseaseAnnotation.setSingleReference(obsoleteReference);
		diseaseAnnotation.setWith(List.of(obsoleteGene));
		diseaseAnnotation.setAnnotationType(obsoleteAnnotationType);
		diseaseAnnotation.setDiseaseQualifiers(List.of(obsoleteDiseaseQualifier));
		diseaseAnnotation.setGeneticSex(obsoleteGeneticSex);
		diseaseAnnotation.setDiseaseGeneticModifier(obsoleteAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(obsoleteDiseaseGeneticModifierRelation);
		diseaseAnnotation.setInferredGene(obsoleteGene);
		diseaseAnnotation.setAssertedGenes(List.of(obsoleteGene));
		
		ConditionRelation newRelation = new ConditionRelation();
		newRelation.setConditionRelationType(obsoleteConditionRelationType);
		newRelation.setSingleReference(obsoleteReference);
		newRelation.setConditions(List.of(experimentalCondition));
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		
		Note newNote = new Note();
		newNote.setNoteType(obsoleteNoteType);
		newNote.setReferences(List.of(obsoleteReference));
		newNote.setFreeText("Obsolete");
		diseaseAnnotation.setRelatedNotes(List.of(newNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(17))).
			body("errorMessages.subject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.inferredGene", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.assertedGenes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"singleReference - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(33)
	public void createAgmDiseaseAnnotationWithObsoleteFields() {
		AGMDiseaseAnnotation diseaseAnnotation = new AGMDiseaseAnnotation();
		diseaseAnnotation.setDiseaseRelation(obsoleteAgmDiseaseRelation);
		diseaseAnnotation.setObject(obsoleteDoTerm);
		diseaseAnnotation.setDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSubject(obsoleteAgm);
		diseaseAnnotation.setEvidenceCodes(List.of(obsoleteEcoTerm));
		diseaseAnnotation.setSingleReference(obsoleteReference);
		diseaseAnnotation.setWith(List.of(obsoleteGene));
		diseaseAnnotation.setAnnotationType(obsoleteAnnotationType);
		diseaseAnnotation.setDiseaseQualifiers(List.of(obsoleteDiseaseQualifier));
		diseaseAnnotation.setGeneticSex(obsoleteGeneticSex);
		diseaseAnnotation.setDiseaseGeneticModifier(obsoleteAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(obsoleteDiseaseGeneticModifierRelation);
		diseaseAnnotation.setInferredGene(obsoleteGene);
		diseaseAnnotation.setAssertedGenes(List.of(obsoleteGene));
		diseaseAnnotation.setInferredAllele(obsoleteAllele);
		diseaseAnnotation.setAssertedAllele(obsoleteAllele);
		
		ConditionRelation newRelation = new ConditionRelation();
		newRelation.setConditionRelationType(obsoleteConditionRelationType);
		newRelation.setSingleReference(obsoleteReference);
		newRelation.setConditions(List.of(experimentalCondition));
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		
		Note newNote = new Note();
		newNote.setNoteType(obsoleteNoteType);
		newNote.setReferences(List.of(obsoleteReference));
		newNote.setFreeText("Obsolete");
		diseaseAnnotation.setRelatedNotes(List.of(newNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(19))).
			body("errorMessages.subject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.inferredGene", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.assertedGenes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.inferredAllele", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.assertedAllele", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"singleReference - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(34)
	public void editGeneDiseaseAnnotationWithObsoleteFields() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDiseaseRelation(obsoleteGeneDiseaseRelation);
		diseaseAnnotation.setObject(obsoleteDoTerm);
		diseaseAnnotation.setDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSubject(obsoleteGene);
		diseaseAnnotation.setEvidenceCodes(List.of(obsoleteEcoTerm));
		diseaseAnnotation.setSingleReference(obsoleteReference);
		diseaseAnnotation.setSgdStrainBackground(obsoleteAgm);
		diseaseAnnotation.setWith(List.of(obsoleteGene));
		diseaseAnnotation.setAnnotationType(obsoleteAnnotationType);
		diseaseAnnotation.setDiseaseQualifiers(List.of(obsoleteDiseaseQualifier));
		diseaseAnnotation.setGeneticSex(obsoleteGeneticSex);
		diseaseAnnotation.setDiseaseGeneticModifier(obsoleteAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(obsoleteDiseaseGeneticModifierRelation);
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(obsoleteConditionRelationType);
		editedRelation.setSingleReference(obsoleteReference);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(obsoleteNoteType);
		editedNote.setReferences(List.of(obsoleteReference));
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(16))).
			body("errorMessages.subject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.sgdStrainBackground", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"singleReference - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(35)
	public void editAlleleDiseaseAnnotationWithObsoleteFields() {
		AlleleDiseaseAnnotation diseaseAnnotation = getAlleleDiseaseAnnotation(ALLELE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDiseaseRelation(obsoleteAlleleDiseaseRelation);
		diseaseAnnotation.setObject(obsoleteDoTerm);
		diseaseAnnotation.setDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSubject(obsoleteAllele);
		diseaseAnnotation.setEvidenceCodes(List.of(obsoleteEcoTerm));
		diseaseAnnotation.setSingleReference(obsoleteReference);
		diseaseAnnotation.setWith(List.of(obsoleteGene));
		diseaseAnnotation.setAnnotationType(obsoleteAnnotationType);
		diseaseAnnotation.setDiseaseQualifiers(List.of(obsoleteDiseaseQualifier));
		diseaseAnnotation.setGeneticSex(obsoleteGeneticSex);
		diseaseAnnotation.setDiseaseGeneticModifier(obsoleteAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(obsoleteDiseaseGeneticModifierRelation);
		diseaseAnnotation.setInferredGene(obsoleteGene);
		diseaseAnnotation.setAssertedGenes(List.of(obsoleteGene));
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(obsoleteConditionRelationType);
		editedRelation.setSingleReference(obsoleteReference);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(obsoleteNoteType);
		editedNote.setReferences(List.of(obsoleteReference));
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/allele-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(17))).
			body("errorMessages.subject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.inferredGene", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.assertedGenes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"singleReference - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(36)
	public void editAgmDiseaseAnnotationWithObsoleteFields() {
		AGMDiseaseAnnotation diseaseAnnotation = getAgmDiseaseAnnotation(AGM_DISEASE_ANNOTATION);
		diseaseAnnotation.setDiseaseRelation(obsoleteAgmDiseaseRelation);
		diseaseAnnotation.setObject(obsoleteDoTerm);
		diseaseAnnotation.setDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSecondaryDataProvider(obsoleteDataProvider);
		diseaseAnnotation.setSubject(obsoleteAgm);
		diseaseAnnotation.setEvidenceCodes(List.of(obsoleteEcoTerm));
		diseaseAnnotation.setSingleReference(obsoleteReference);
		diseaseAnnotation.setWith(List.of(obsoleteGene));
		diseaseAnnotation.setAnnotationType(obsoleteAnnotationType);
		diseaseAnnotation.setDiseaseQualifiers(List.of(obsoleteDiseaseQualifier));
		diseaseAnnotation.setGeneticSex(obsoleteGeneticSex);
		diseaseAnnotation.setDiseaseGeneticModifier(obsoleteAgm);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(obsoleteDiseaseGeneticModifierRelation);
		diseaseAnnotation.setInferredGene(obsoleteGene);
		diseaseAnnotation.setAssertedGenes(List.of(obsoleteGene));
		diseaseAnnotation.setInferredAllele(obsoleteAllele);
		diseaseAnnotation.setAssertedAllele(obsoleteAllele);
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setConditionRelationType(obsoleteConditionRelationType);
		editedRelation.setSingleReference(obsoleteReference);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		Note editedNote = diseaseAnnotation.getRelatedNotes().get(0);
		editedNote.setNoteType(obsoleteNoteType);
		editedNote.setReferences(List.of(obsoleteReference));
		diseaseAnnotation.setRelatedNotes(List.of(editedNote));

		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/agm-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(19))).
			body("errorMessages.subject", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.object", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.dataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.secondaryDataProvider", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.evidenceCodes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.singleReference", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.with", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.annotationType", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseQualifiers", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.geneticSex", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.inferredGene", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.assertedGenes", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.inferredAllele", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.assertedAllele", is(ValidationConstants.OBSOLETE_MESSAGE)).
			body("errorMessages.conditionRelations", is(String.join(" | ", List.of(
					"conditionRelationType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"singleReference - " + ValidationConstants.OBSOLETE_MESSAGE)))).
			body("errorMessages.relatedNotes", is(String.join(" | ", List.of(
					"noteType - " + ValidationConstants.OBSOLETE_MESSAGE,
					"references - " + ValidationConstants.OBSOLETE_MESSAGE))));
	}
	
	@Test
	@Order(37)
	public void createDiseaseAnnotationWithMissingDependentFields() {
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setSubject(gene);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setDataProvider(dataProvider);
		
		diseaseAnnotation.setDiseaseGeneticModifier(allele);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation"));
		
		diseaseAnnotation.setDiseaseGeneticModifier(null);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifier"));
	
	}
	
	@Test
	@Order(38)
	public void editDiseaseAnnotationWithMissingDependentFields() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(null);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.diseaseGeneticModifier", is(ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation"));
		
		diseaseAnnotation.setDiseaseGeneticModifierRelation(diseaseGeneticModifierRelation);
		diseaseAnnotation.setDiseaseGeneticModifier(null);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.diseaseGeneticModifierRelation", is(ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifier"));
	
	}
	
	@Test
	@Order(39)
	public void createDiseaseAnnotationConditionRelationWithHandleWithoutReference() {
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setSubject(gene);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setDataProvider(dataProvider);
		
		ConditionRelation newRelation = new ConditionRelation();
		newRelation.setHandle("Missing_reference");
		newRelation.setConditionRelationType(conditionRelationType);
		newRelation.setConditions(List.of(experimentalCondition));
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.conditionRelations", is("handle - " + ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "singleReference"));
	}
	
	@Test
	@Order(40)
	public void editDiseaseAnnotationConditionRelationWithHandleWithoutReference() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setSingleReference(null);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.conditionRelations", is("handle - " + ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "singleReference"));
	}
	
	@Test
	@Order(41)
	public void createDiseaseAnnotationWithConditionRelationReferenceMismatch() {
		GeneDiseaseAnnotation diseaseAnnotation = new GeneDiseaseAnnotation();
		diseaseAnnotation.setSubject(gene);
		diseaseAnnotation.setObject(doTerm);
		diseaseAnnotation.setDiseaseRelation(geneDiseaseRelation);
		diseaseAnnotation.setEvidenceCodes(List.of(ecoTerm));
		diseaseAnnotation.setSingleReference(reference);
		diseaseAnnotation.setDataProvider(dataProvider);
		
		ConditionRelation newRelation = new ConditionRelation();
		newRelation.setSingleReference(reference2);
		newRelation.setConditionRelationType(conditionRelationType);
		newRelation.setConditions(List.of(experimentalCondition));
		diseaseAnnotation.setConditionRelations(List.of(newRelation));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			post("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.conditionRelations", is("singleReference - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(42)
	public void editDiseaseAnnotationWithConditionRelationReferenceMismatch() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setSingleReference(reference);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.conditionRelations", is("singleReference - " + ValidationConstants.INVALID_MESSAGE));
	}
	
	@Test
	@Order(43)
	public void removeHandleFromDiseaseAnnotationConditionRelation() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		ConditionRelation editedRelation = diseaseAnnotation.getConditionRelations().get(0);
		editedRelation.setHandle(null);
		diseaseAnnotation.setConditionRelations(List.of(editedRelation));
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(400).
			body("errorMessages", is(aMapWithSize(1))).
			body("errorMessages.conditionRelations", is("handle - " + ValidationConstants.REQUIRED_MESSAGE));
	}
	
	@Test
	@Order(44)
	public void editGeneDiseaseAnnotationWithNullNonRequiredFieldsLevel() {
		GeneDiseaseAnnotation diseaseAnnotation = getGeneDiseaseAnnotation(GENE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDateCreated(null);
		diseaseAnnotation.setSecondaryDataProvider(null);
		diseaseAnnotation.setSgdStrainBackground(null);
		diseaseAnnotation.setWith(null);
		diseaseAnnotation.setAnnotationType(null);
		diseaseAnnotation.setDiseaseQualifiers(null);
		diseaseAnnotation.setGeneticSex(null);
		diseaseAnnotation.setDiseaseGeneticModifier(null);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(null);
		diseaseAnnotation.setConditionRelations(null);
		diseaseAnnotation.setRelatedNotes(null);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/gene-disease-annotation").
			then().
			statusCode(200);
	
		RestAssured.given().
			when().
			get("/api/gene-disease-annotation/findBy/" + GENE_DISEASE_ANNOTATION).
			then().
			statusCode(200).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("secondaryDataProvider"))).
			body("entity", not(hasKey("sgdStrainBackground"))).
			body("entity", not(hasKey("with"))).
			body("entity", not(hasKey("annotationType"))).
			body("entity", not(hasKey("diseaseQualifiers"))).
			body("entity", not(hasKey("geneticSex"))).
			body("entity", not(hasKey("diseaseGeneticModifier"))).
			body("entity", not(hasKey("diseaseGeneticModifierRelation"))).
			body("entity", not(hasKey("conditionRelations"))).
			body("entity", not(hasKey("relatedNotes")));	
	}
	
	@Test
	@Order(45)
	public void editAlleleDiseaseAnnotationWithNullNonRequiredFieldsLevel() {
		AlleleDiseaseAnnotation diseaseAnnotation = getAlleleDiseaseAnnotation(ALLELE_DISEASE_ANNOTATION);
		diseaseAnnotation.setDateCreated(null);
		diseaseAnnotation.setSecondaryDataProvider(null);
		diseaseAnnotation.setWith(null);
		diseaseAnnotation.setAnnotationType(null);
		diseaseAnnotation.setDiseaseQualifiers(null);
		diseaseAnnotation.setGeneticSex(null);
		diseaseAnnotation.setDiseaseGeneticModifier(null);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(null);
		diseaseAnnotation.setConditionRelations(null);
		diseaseAnnotation.setRelatedNotes(null);
		diseaseAnnotation.setInferredGene(null);
		diseaseAnnotation.setAssertedGenes(null);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/allele-disease-annotation").
			then().
			statusCode(200);
	
		RestAssured.given().
			when().
			get("/api/allele-disease-annotation/findBy/" + ALLELE_DISEASE_ANNOTATION).
			then().
			statusCode(200).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("secondaryDataProvider"))).
			body("entity", not(hasKey("with"))).
			body("entity", not(hasKey("annotationType"))).
			body("entity", not(hasKey("diseaseQualifiers"))).
			body("entity", not(hasKey("geneticSex"))).
			body("entity", not(hasKey("diseaseGeneticModifier"))).
			body("entity", not(hasKey("diseaseGeneticModifierRelation"))).
			body("entity", not(hasKey("conditionRelations"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("inferredGene"))).
			body("entity", not(hasKey("assertedGenes")));	
	}
	
	@Test
	@Order(46)
	public void editAgmDiseaseAnnotationWithNullNonRequiredFieldsLevel() {
		AGMDiseaseAnnotation diseaseAnnotation = getAgmDiseaseAnnotation(AGM_DISEASE_ANNOTATION);
		diseaseAnnotation.setDateCreated(null);
		diseaseAnnotation.setSecondaryDataProvider(null);
		diseaseAnnotation.setWith(null);
		diseaseAnnotation.setAnnotationType(null);
		diseaseAnnotation.setDiseaseQualifiers(null);
		diseaseAnnotation.setGeneticSex(null);
		diseaseAnnotation.setDiseaseGeneticModifier(null);
		diseaseAnnotation.setDiseaseGeneticModifierRelation(null);
		diseaseAnnotation.setConditionRelations(null);
		diseaseAnnotation.setRelatedNotes(null);
		diseaseAnnotation.setInferredGene(null);
		diseaseAnnotation.setAssertedGenes(null);
		diseaseAnnotation.setInferredAllele(null);
		diseaseAnnotation.setAssertedAllele(null);
		
		RestAssured.given().
			contentType("application/json").
			body(diseaseAnnotation).
			when().
			put("/api/agm-disease-annotation").
			then().
			statusCode(200);
	
		RestAssured.given().
			when().
			get("/api/agm-disease-annotation/findBy/" + AGM_DISEASE_ANNOTATION).
			then().
			statusCode(200).
			body("entity", not(hasKey("dateCreated"))).
			body("entity", not(hasKey("secondaryDataProvider"))).
			body("entity", not(hasKey("with"))).
			body("entity", not(hasKey("annotationType"))).
			body("entity", not(hasKey("diseaseQualifiers"))).
			body("entity", not(hasKey("geneticSex"))).
			body("entity", not(hasKey("diseaseGeneticModifier"))).
			body("entity", not(hasKey("diseaseGeneticModifierRelation"))).
			body("entity", not(hasKey("conditionRelations"))).
			body("entity", not(hasKey("relatedNotes"))).
			body("entity", not(hasKey("inferredGene"))).
			body("entity", not(hasKey("assertedGenes"))).
			body("entity", not(hasKey("inferredAllele"))).
			body("entity", not(hasKey("assertedAllele")));	
	}
}