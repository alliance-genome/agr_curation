package org.alliancegenome.curation_api.base;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.model.entities.ontology.ChemicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ExperimentalConditionOntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZFATerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

public class BaseITCase {
	
	private static Pattern keyPattern = Pattern.compile("^(.+)\\.([^\\.]+)$");

	public VocabularyTerm addObsoleteVocabularyTermToSet(String setName, String termName, Vocabulary vocabulary) {
		VocabularyTermSet set = getVocabularyTermSet(setName);
		VocabularyTerm term = createVocabularyTerm(vocabulary, termName, false);
		
		List<VocabularyTerm> setTerms = set.getMemberTerms();
		setTerms.add(term);
		set.setMemberTerms(setTerms);
		
		RestAssured.given().
			contentType("application/json").
			body(set).
			when().
			put("/api/vocabularytermset").
			then().
			statusCode(200);
		
		term.setObsolete(true);
		
		ObjectResponse<VocabularyTerm> response = RestAssured.given().
			contentType("application/json").
			body(term).
			when().
			put("/api/vocabularyterm").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefVocabularyTerm());
		
		return response.getEntity();
	}
	
	public void checkFailedBulkLoad(String endpoint, String filePath) throws Exception {
		String content = Files.readString(Path.of(filePath));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post(endpoint).
			then().
			statusCode(200).
			body("history.totalRecords", is(1)).
			body("history.failedRecords", is(1)).
			body("history.completedRecords", is(0));
	}
	
	public void checkSuccessfulBulkLoad(String endpoint, String filePath) throws Exception {
		String content = Files.readString(Path.of(filePath));
		
		RestAssured.given().
			contentType("application/json").
			body(content).
			when().
			post(endpoint).
			then().
			statusCode(200).
			body("history.totalRecords", is(1)).
			body("history.failedRecords", is(0)).
			body("history.completedRecords", is(1));
	}
	
	public AffectedGenomicModel createAffectedGenomicModel(String curie, String taxonCurie, String subtypeName, String name, Boolean obsolete) {
		Vocabulary subtypeVocabulary = getVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY);
		VocabularyTerm subtype = getVocabularyTerm(subtypeVocabulary, subtypeName);
		
		AffectedGenomicModel model = new AffectedGenomicModel();
		model.setCurie(curie);
		model.setTaxon(getNCBITaxonTerm(taxonCurie));
		model.setSubtype(subtype);
		model.setName(name);
		model.setObsolete(obsolete);

		RestAssured.given().
				contentType("application/json").
				body(model).
				when().
				post("/api/agm").
				then().
				statusCode(200);
		return model;
	}

	public Allele createAllele(String curie, String taxonCurie, Boolean obsolete, VocabularyTerm symbolNameTerm) {
		Allele allele = new Allele();
		allele.setCurie(curie);
		allele.setTaxon(getNCBITaxonTerm(taxonCurie));
		allele.setObsolete(obsolete);
		allele.setInternal(false);
		
		AlleleSymbolSlotAnnotation symbol = new AlleleSymbolSlotAnnotation();
		symbol.setNameType(symbolNameTerm);
		symbol.setDisplayText(curie);
		symbol.setFormatText(curie);
		
		allele.setAlleleSymbol(symbol);

		RestAssured.given().
				contentType("application/json").
				body(allele).
				when().
				post("/api/allele").
				then().
				statusCode(200);
		return allele;
	}
	
	public BiologicalEntity createBiologicalEntity(String curie, String taxonCurie) {
		BiologicalEntity bioEntity = new BiologicalEntity();
		bioEntity.setCurie(curie);
		bioEntity.setTaxon(getNCBITaxonTerm(taxonCurie));
		
		RestAssured.given().
				contentType("application/json").
				body(bioEntity).
				when().
				post("/api/biologicalentity").
				then().
				statusCode(200);
		
		return bioEntity;
	}

	public CHEBITerm createChebiTerm(String curie, String name, Boolean obsolete) {
		CHEBITerm chebiTerm = new CHEBITerm();
		chebiTerm.setCurie(curie);
		chebiTerm.setObsolete(obsolete);
		chebiTerm.setName(name);
		chebiTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		RestAssured.given().
				contentType("application/json").
				body(chebiTerm).
				when().
				post("/api/chebiterm").
				then().
				statusCode(200);
		return chebiTerm;
	}
	
	public ConditionRelation createConditionRelation(String handle, Reference reference, VocabularyTerm relationType, List<ExperimentalCondition> conditions) {
		ConditionRelation conditionRelation = new ConditionRelation();
		conditionRelation.setHandle(handle);
		conditionRelation.setSingleReference(reference);
		conditionRelation.setConditionRelationType(relationType);
		conditionRelation.setConditions(conditions);

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

	public DataProvider createDataProvider(String organizationAbbreviation, Boolean obsolete) {
		DataProvider dataProvider = new DataProvider();
		Organization sourceOrganization = getOrganization(organizationAbbreviation);
		if (sourceOrganization == null)
			sourceOrganization = createOrganization(organizationAbbreviation, organizationAbbreviation, false);
		dataProvider.setSourceOrganization(sourceOrganization);
		dataProvider.setObsolete(obsolete);
		
		ObjectResponse<DataProvider> response = RestAssured.given().
			contentType("application/json").
			body(dataProvider).
			when().
			post("/api/dataprovider").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefDataProvider());
		
		return response.getEntity();
	}
	
	public DOTerm createDoTerm(String curie, Boolean obsolete) {
		DOTerm doTerm = new DOTerm();
		doTerm.setCurie(curie);
		doTerm.setObsolete(obsolete);
		doTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		RestAssured.given().
				contentType("application/json").
				body(doTerm).
				when().
				post("/api/doterm").
				then().
				statusCode(200);
		return doTerm;
	}

	public ECOTerm createEcoTerm(String curie, String name, Boolean obsolete, Boolean inAgrSubset) {
		ECOTerm ecoTerm = new ECOTerm();
		ecoTerm.setCurie(curie);
		ecoTerm.setName(name);
		ecoTerm.setObsolete(obsolete);
		ecoTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
		if (inAgrSubset)
			ecoTerm.setSubsets(List.of(OntologyConstants.AGR_ECO_TERM_SUBSET));
		
		RestAssured.given().
				contentType("application/json").
				body(ecoTerm).
				when().
				post("/api/ecoterm").
				then().
				statusCode(200);
		return ecoTerm;
	}

	public ExperimentalCondition createExperimentalCondition(String uniqueId, String conditionClassCurie, String conditionClassName) {
		ExperimentalCondition condition = new ExperimentalCondition();
		condition.setConditionClass(createZecoTerm(conditionClassCurie, conditionClassName, false, OntologyConstants.ZECO_AGR_SLIM_SUBSET));
		condition.setUniqueId(uniqueId);

		ObjectResponse<ExperimentalCondition> response = given().
			contentType("application/json").
			body(condition).
			when().
			post("/api/experimental-condition").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefExperimentalCondition());

		return response.getEntity();
	}

	public Gene createGene(String curie, String taxonCurie, Boolean obsolete, VocabularyTerm symbolNameTerm) {
		Gene gene = new Gene();
		gene.setCurie(curie);
		gene.setTaxon(getNCBITaxonTerm(taxonCurie));
		gene.setObsolete(obsolete);
		
		GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
		symbol.setNameType(symbolNameTerm);
		symbol.setDisplayText(curie);
		symbol.setFormatText(curie);
		
		gene.setGeneSymbol(symbol);

		RestAssured.given().
				contentType("application/json").
				body(gene).
				when().
				post("/api/gene").
				then().
				statusCode(200);
		return gene;
	}

	public GOTerm createGoTerm(String curie, String name, Boolean obsolete) {
		GOTerm goTerm = new GOTerm();
		goTerm.setCurie(curie);
		goTerm.setObsolete(obsolete);
		goTerm.setName(name);
		goTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		RestAssured.given().
				contentType("application/json").
				body(goTerm).
				when().
				post("/api/goterm").
				then().
				statusCode(200);
		return goTerm;
	}
	
	public MPTerm createMpTerm(String curie, Boolean obsolete) {
		MPTerm mpTerm = new MPTerm();
		mpTerm.setCurie(curie);
		mpTerm.setObsolete(obsolete);
		mpTerm.setName("Test MPTerm");
		mpTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		RestAssured.given().
				contentType("application/json").
				body(mpTerm).
				when().
				post("/api/mpterm").
				then().
				statusCode(200);
		return mpTerm;
	}
	
	public NCBITaxonTerm createNCBITaxonTerm(String curie, String name, Boolean obsolete) {
		NCBITaxonTerm term = new NCBITaxonTerm();
		term.setCurie(curie);
		term.setName(name);
		term.setObsolete(obsolete);
		
		ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
				contentType("application/json").
				body(term).
				when().
				post("/api/ncbitaxonterm").
				then().
				statusCode(200).extract().
				body().as(getObjectResponseTypeRefNCBITaxonTerm());
		
		return response.getEntity();
	}

	public Note createNote(VocabularyTerm vocabularyTerm, String text, Boolean internal, Reference reference) {
		Note note = new Note();
		note.setNoteType(vocabularyTerm);
		note.setFreeText(text);
		note.setInternal(internal);
		if (reference != null) {
			List<Reference> references = new ArrayList<Reference>();
			references.add(reference);
			note.setReferences(references);
		}

		return note;
	}
	
	public Organization createOrganization(String uniqueId, String abbreviation, Boolean obsolete) {
		Organization organization = new Organization();
		organization.setAbbreviation(abbreviation);
		organization.setUniqueId(uniqueId);
		organization.setObsolete(obsolete);
		
		ObjectResponse<Organization> response = RestAssured.given().
				contentType("application/json").
				body(organization).
				when().
				post("/api/organization").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefOrganization());
		
		return response.getEntity();
	}
	
	public Person createPerson(String uniqueId) {
		Person person = new Person();
		person.setUniqueId(uniqueId);
		
		ObjectResponse<Person> response = RestAssured.given().
				contentType("application/json").
				body(person).
				when().
				post("/api/person").
				then().
				statusCode(200).extract().
				body().as(getObjectResponseTypeRefLoggedInPerson());
		
		person = response.getEntity();
		return (Person) person;
	}
	
	public Reference createReference(String curie, Boolean obsolete) {
		Reference reference = new Reference();
		reference.setCurie(curie);
		reference.setObsolete(obsolete);
		
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
	
	public ResourceDescriptor createResourceDescriptor(String prefix) {
		ResourceDescriptor rd = new ResourceDescriptor();
		rd.setPrefix(prefix);
		
		ObjectResponse<ResourceDescriptor> response = RestAssured.given().
			contentType("application/json").
			body(rd).
			when().
			post("/api/resourcedescriptor").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefResourceDescriptor());
			
		return response.getEntity();
	}
	
	public ResourceDescriptorPage createResourceDescriptorPage(String name, String urlTemplate, ResourceDescriptor rd) {
		ResourceDescriptorPage rdPage = new ResourceDescriptorPage();
		rdPage.setResourceDescriptor(rd);
		rdPage.setUrlTemplate(urlTemplate);
		rdPage.setName(name);
		
		ObjectResponse<ResourceDescriptorPage> response = RestAssured.given().
			contentType("application/json").
			body(rdPage).
			when().
			post("/api/resourcedescriptorpage").
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefResourceDescriptorPage());
			
		return response.getEntity();
	}
	
	public SOTerm createSoTerm(String curie, Boolean obsolete) {
		SOTerm term = new SOTerm();
		term.setCurie(curie);
		term.setObsolete(obsolete);
		term.setSecondaryIdentifiers(List.of(curie + "secondary"));
		
		ObjectResponse<SOTerm> response = RestAssured.given().
				contentType("application/json").
				body(term).
				when().
				post("/api/soterm").
				then().
				statusCode(200).extract().
				body().as(getObjectResponseTypeRefSOTerm());
		
		return response.getEntity();
	}
	
	public Vocabulary createVocabulary(String name, Boolean obsolete) {
		Vocabulary vocabulary = new Vocabulary();
		vocabulary.setName(name);
		vocabulary.setInternal(false);
		vocabulary.setObsolete(obsolete);
		
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
	
	public VocabularyTerm createVocabularyTerm(Vocabulary vocabulary, String name, Boolean obsolete) {
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
		
		return response.getEntity();
	}
	
	public void createVocabularyTermSet(String name, Vocabulary vocabulary, List<VocabularyTerm> terms) {
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

	public ZECOTerm createZecoTerm(String curie, String name, Boolean obsolete, String subset) {
		ZECOTerm zecoTerm = new ZECOTerm();
		zecoTerm.setCurie(curie);
		zecoTerm.setName(name);
		zecoTerm.setObsolete(obsolete);
		List<String> subsets = new ArrayList<String>();
		if (subset != null) {
			subsets.add(subset);
			zecoTerm.setSubsets(subsets);
		}
		zecoTerm.setObsolete(obsolete);
		zecoTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		given().
			contentType("application/json").
			body(zecoTerm).
			when().
			post("/api/zecoterm").
			then().
			statusCode(200);
		return zecoTerm;
	}
	
	public ZFATerm createZfaTerm(String curie, Boolean obsolete) {
		ZFATerm zfaTerm = new ZFATerm();
		zfaTerm.setCurie(curie);
		zfaTerm.setObsolete(obsolete);
		zfaTerm.setName("Test ZFATerm");
		zfaTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		RestAssured.given().
				contentType("application/json").
				body(zfaTerm).
				when().
				post("/api/zfaterm").
				then().
				statusCode(200);
		return zfaTerm;
	}

	public AffectedGenomicModel getAffectedGenomicModel(String curie) {
		ObjectResponse<AffectedGenomicModel> res = RestAssured.given().
				when().
				get("/api/agm/" + curie).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAffectedGenomicModel());

		return res.getEntity();
	}

	public AGMDiseaseAnnotation getAgmDiseaseAnnotation(String uniqueId) {
		ObjectResponse<AGMDiseaseAnnotation> res = RestAssured.given().
				when().
				get("/api/agm-disease-annotation/findBy/" + uniqueId).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAGMDiseaseAnnotation());

		return res.getEntity();
	}

	public Allele getAllele(String curie) {
		ObjectResponse<Allele> res = RestAssured.given().
				when().
				get("/api/allele/" + curie).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAllele());

		return res.getEntity();
	}

	public AlleleDiseaseAnnotation getAlleleDiseaseAnnotation(String uniqueId) {
		ObjectResponse<AlleleDiseaseAnnotation> res = RestAssured.given().
				when().
				get("/api/allele-disease-annotation/findBy/" + uniqueId).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefAlleleDiseaseAnnotation());

		return res.getEntity();
	}

	public ConditionRelation getConditionRelation(Long id) {
		ObjectResponse<ConditionRelation> response =
				given().
					when().
					get("/api/condition-relation/" + id).
					then().
					statusCode(200).
					extract().body().as(getObjectResponseTypeRefConditionRelation());

			return response.getEntity();
	}

	public ExperimentalCondition getExperimentalCondition(String conditionSummary) {
		ObjectResponse<ExperimentalCondition> res = RestAssured.given().
				when().
				get("/api/experimental-condition/findBy/" + conditionSummary).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefExperimentalCondition());
		
		return res.getEntity();
	}

	public Gene getGene(String curie) {
		ObjectResponse<Gene> res = RestAssured.given().
				when().
				get("/api/gene/" + curie).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefGene());

		return res.getEntity();
	}
	
	public GeneDiseaseAnnotation getGeneDiseaseAnnotation(String uniqueId) {
		ObjectResponse<GeneDiseaseAnnotation> res = RestAssured.given().
				when().
				get("/api/gene-disease-annotation/findBy/" + uniqueId).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefGeneDiseaseAnnotation());

		return res.getEntity();
	}
	
	public MPTerm getMpTerm(String curie) {
		ObjectResponse<MPTerm> response = RestAssured.given().
			when().
			get("/api/mpterm/" + curie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefMPTerm());
			
		return response.getEntity();
	}
	
	public Note getNote(Long id) {
		
		ObjectResponse<Note> response =
				RestAssured.given().
					when().
					get("/api/note/" + id).
					then().
					statusCode(200).
					extract().body().as(getObjectResponseTypeRefNote());
		
		Note note = response.getEntity();
		
		return note;
	}
	
	private TypeRef<ObjectListResponse<VocabularyTerm>> getObjectListResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectListResponse <VocabularyTerm>>() { };
	}
	
	private TypeRef<ObjectResponse<AffectedGenomicModel>> getObjectResponseTypeRefAffectedGenomicModel() {
		return new TypeRef<ObjectResponse <AffectedGenomicModel>>() { };
	}

	private TypeRef<ObjectResponse<AGMDiseaseAnnotation>> getObjectResponseTypeRefAGMDiseaseAnnotation() {
		return new TypeRef<ObjectResponse <AGMDiseaseAnnotation>>() {
		};
	}
	
	private TypeRef<ObjectResponse<Allele>> getObjectResponseTypeRefAllele() {
		return new TypeRef<ObjectResponse <Allele>>() { };
	}

	private TypeRef<ObjectResponse<AlleleDiseaseAnnotation>> getObjectResponseTypeRefAlleleDiseaseAnnotation() {
		return new TypeRef<ObjectResponse <AlleleDiseaseAnnotation>>() {
		};
	}

	public TypeRef<ObjectResponse<ConditionRelation>> getObjectResponseTypeRefConditionRelation() {
		return new TypeRef<ObjectResponse<ConditionRelation>>() {
		};
	}
	
	private TypeRef<ObjectResponse<CrossReference>> getObjectResponseTypeRefCrossReference() {
		return new TypeRef<ObjectResponse <CrossReference>>() { };
	}

	private TypeRef<ObjectResponse<DataProvider>> getObjectResponseTypeRefDataProvider() {
		return new TypeRef<ObjectResponse<DataProvider>>() {
		};
	}

	private TypeRef<ObjectResponse<ExperimentalCondition>> getObjectResponseTypeRefExperimentalCondition() {
		return new TypeRef<ObjectResponse<ExperimentalCondition>>() {
		};
	}

	private TypeRef<ObjectResponse<Gene>> getObjectResponseTypeRefGene() {
		return new TypeRef<ObjectResponse <Gene>>() { };
	}

	private TypeRef<ObjectResponse<GeneDiseaseAnnotation>> getObjectResponseTypeRefGeneDiseaseAnnotation() {
		return new TypeRef<ObjectResponse <GeneDiseaseAnnotation>>() {
		};
	}

	private TypeRef<ObjectResponse<Person>> getObjectResponseTypeRefLoggedInPerson() {
		return new TypeRef<ObjectResponse <Person>>() {
		};
	}

	private TypeRef<ObjectResponse<MPTerm>> getObjectResponseTypeRefMPTerm() {
		return new TypeRef<ObjectResponse <MPTerm>>() {
		};
	}

	private TypeRef<ObjectResponse<NCBITaxonTerm>> getObjectResponseTypeRefNCBITaxonTerm() {
		return new TypeRef<ObjectResponse <NCBITaxonTerm>>() { };
	}
	
	public TypeRef<ObjectResponse<Note>> getObjectResponseTypeRefNote() {
		return new TypeRef<ObjectResponse <Note>>() { };
	}

	private TypeRef<ObjectResponse<Organization>> getObjectResponseTypeRefOrganization() {
		return new TypeRef<ObjectResponse <Organization>>() {
		};
	}

	private TypeRef<ObjectResponse<Reference>> getObjectResponseTypeRefReference() {
		return new TypeRef<ObjectResponse <Reference>>() {
		};
	}

	private TypeRef<ObjectResponse<ResourceDescriptor>> getObjectResponseTypeRefResourceDescriptor() {
		return new TypeRef<ObjectResponse <ResourceDescriptor>>() {
		};
	}

	private TypeRef<ObjectResponse<ResourceDescriptorPage>> getObjectResponseTypeRefResourceDescriptorPage() {
		return new TypeRef<ObjectResponse <ResourceDescriptorPage>>() {
		};
	}

	private TypeRef<ObjectResponse<SOTerm>> getObjectResponseTypeRefSOTerm() {
		return new TypeRef<ObjectResponse <SOTerm>>() {
		};
	}
	
	private TypeRef<ObjectResponse<Vocabulary>> getObjectResponseTypeRefVocabulary() {
		return new TypeRef<ObjectResponse <Vocabulary>>() { };
	}
	
	private TypeRef<ObjectResponse<VocabularyTerm>> getObjectResponseTypeRefVocabularyTerm() {
		return new TypeRef<ObjectResponse <VocabularyTerm>>() { };
	}

	private TypeRef<ObjectResponse<ZECOTerm>> getObjectResponseTypeRefZecoTerm() {
		return new TypeRef<>() {
		};
	}
	
	public NCBITaxonTerm getNCBITaxonTerm(String curie) {
		ObjectResponse<NCBITaxonTerm> response = RestAssured.given().
			when().
			get("/api/ncbitaxonterm/" + curie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefNCBITaxonTerm());
			
		return response.getEntity();
	}
	
	public Organization getOrganization(String abbreviation) {
		
		SearchResponse<Organization> response =
				RestAssured.given().
					contentType("application/json").
					body("{\"abbreviation\": \"" + abbreviation + "\" }").
					when().
					post("/api/organization/find").
					then().
					statusCode(200).
					extract().body().as(getSearchResponseTypeRefOrganization());
		
		return response.getSingleResult();
	}

	private TypeRef<SearchResponse<Organization>> getSearchResponseTypeRefOrganization() {
		return new TypeRef<SearchResponse <Organization>>() {
		};
	}
	
	private TypeRef<SearchResponse<VocabularyTermSet>> getSearchResponseTypeRefVocabularyTermSet() {
		return new TypeRef<SearchResponse <VocabularyTermSet>>() { };
	}
	
	public SOTerm getSoTerm(String curie) {
		ObjectResponse<SOTerm> response = RestAssured.given().
			when().
			get("/api/soterm/" + curie).
			then().
			statusCode(200).
			extract().body().as(getObjectResponseTypeRefSOTerm());
			
		return response.getEntity();
	}

	public Vocabulary getVocabulary(String name) {
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
	
	public VocabularyTerm getVocabularyTerm(Vocabulary vocabulary, String name) {
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

	public VocabularyTermSet getVocabularyTermSet(String name) {
		
		SearchResponse<VocabularyTermSet> response =
				RestAssured.given().
					contentType("application/json").
					body("{\"name\": \"" + name + "\" }").
					when().
					post("/api/vocabularytermset/find").
					then().
					statusCode(200).
					extract().body().as(getSearchResponseTypeRefVocabularyTermSet());
		
		return response.getSingleResult();
	}

	public ZECOTerm getZecoTerm(String curie) {
		ObjectResponse<ZECOTerm> response =
			given().
				when().
				get("/api/zecoterm/" + curie).
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefZecoTerm());

		return response.getEntity();
	}

	public void loadAffectedGenomicModel(String curie, String name, String taxonCurie, String subtypeName, DataProvider dataProvider) throws Exception {
		Vocabulary subtypeVocabulary = getVocabulary(VocabularyConstants.AGM_SUBTYPE_VOCABULARY);
		VocabularyTerm subtype = getVocabularyTerm(subtypeVocabulary, subtypeName);
		
		AffectedGenomicModel agm = new AffectedGenomicModel();
		agm.setCurie(curie);
		agm.setTaxon(getNCBITaxonTerm(taxonCurie));
		agm.setName(name);
		agm.setSubtype(subtype);
		agm.setDataProvider(dataProvider);		
		RestAssured.given().
			contentType("application/json").
			body(agm).
			when().
			post("/api/agm").
			then().
			statusCode(200);
	}

	public void loadAllele(String curie, String symbol, String taxonCurie, VocabularyTerm symbolNameTerm, DataProvider dataProvider) throws Exception {
		Allele allele = new Allele();
		allele.setCurie(curie);
		allele.setTaxon(getNCBITaxonTerm(taxonCurie));
		allele.setInternal(false);
		allele.setDataProvider(dataProvider);
		
		AlleleSymbolSlotAnnotation alleleSymbol = new AlleleSymbolSlotAnnotation();
		alleleSymbol.setNameType(symbolNameTerm);
		alleleSymbol.setDisplayText(symbol);
		alleleSymbol.setFormatText(symbol);
		
		allele.setAlleleSymbol(alleleSymbol);

		RestAssured.given().
			contentType("application/json").
			body(allele).
			when().
			post("/api/allele").
			then().
			statusCode(200);
	}

	public void loadAnatomyTerm(String curie, String name) throws Exception {
		AnatomicalTerm anatomicalTerm = new AnatomicalTerm();
		anatomicalTerm.setCurie(curie);
		anatomicalTerm.setName(name);
		anatomicalTerm.setObsolete(false);
		anatomicalTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
		
		RestAssured.given().
			contentType("application/json").
			body(anatomicalTerm).
			when().
			put("/api/anatomicalterm").
			then().
			statusCode(200);
	}

	public void loadChemicalTerm(String curie, String name) throws Exception {
		ChemicalTerm chemicalTerm = new ChemicalTerm();
		chemicalTerm.setCurie(curie);
		chemicalTerm.setName(name);
		chemicalTerm.setObsolete(false);
		chemicalTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
		
		RestAssured.given().
			contentType("application/json").
			body(chemicalTerm).
			when().
			put("/api/chemicalterm").
			then().
			statusCode(200);
	}
	
	public void loadDOTerm(String curie, String name) throws Exception {
		DOTerm doTerm = new DOTerm();
		doTerm.setCurie(curie);
		doTerm.setName(name);
		doTerm.setObsolete(false);
		doTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
		
		RestAssured.given().
			contentType("application/json").
			body(doTerm).
			when().
			put("/api/doterm").
			then().
			statusCode(200);
	}

	public void loadExperimentalConditionTerm(String curie, String name) throws Exception {
		ExperimentalConditionOntologyTerm ecTerm = new ExperimentalConditionOntologyTerm();
		ecTerm.setCurie(curie);
		ecTerm.setName(name);
		ecTerm.setObsolete(false);
		ecTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));

		RestAssured.given().
			contentType("application/json").
			body(ecTerm).
			when().
			post("/api/experimentalconditionontologyterm").
			then().
			statusCode(200);
	}
	
	public void loadGene(String curie, String taxonCurie, VocabularyTerm symbolNameTerm, DataProvider dataProvider) {
			Gene gene = new Gene();
			gene.setCurie(curie);
			gene.setTaxon(getNCBITaxonTerm(taxonCurie));
			gene.setDataProvider(dataProvider);
			
			GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
			symbol.setNameType(symbolNameTerm);
			symbol.setDisplayText(curie);
			symbol.setFormatText(curie);
			
			gene.setGeneSymbol(symbol);

			RestAssured.given().
					contentType("application/json").
					body(gene).
					when().
					post("/api/gene").
					then().
					statusCode(200);
	}
	
	public void loadGenes(List<String> curies, String taxonCurie, VocabularyTerm symbolNameTerm, DataProvider dataProvider) throws Exception {
		for (String curie : curies) {
			loadGene(curie, taxonCurie, symbolNameTerm, dataProvider);
		}
	}

	public void loadGOTerm(String curie, String name) throws Exception {
		GOTerm goTerm = new GOTerm();
		goTerm.setCurie(curie);
		goTerm.setName(name);
		goTerm.setObsolete(false);
		goTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
		
		RestAssured.given().
			contentType("application/json").
			body(goTerm).
			when().
			put("/api/goterm").
			then().
			statusCode(200);
	}
	
	public void loadMPTerm(String curie, String name) throws Exception {
		MPTerm mpTerm = new MPTerm();
		mpTerm.setCurie(curie);
		mpTerm.setName(name);
		mpTerm.setObsolete(false);
		mpTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
		
		RestAssured.given().
			contentType("application/json").
			body(mpTerm).
			when().
			put("/api/mpterm").
			then().
			statusCode(200);
	}
	
	public void loadOrganization(String abbreviation) throws Exception {
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
	
	public void loadReference(String curie, String xrefCurie) throws Exception {
			
		CrossReference xref = new CrossReference();
		xref.setReferencedCurie(xrefCurie);
		xref.setDisplayName(xrefCurie);
		
		ObjectResponse<CrossReference> response = 
			RestAssured.given().
				contentType("application/json").
				body(xref).
				when().
				post("/api/cross-reference").
				then().
				statusCode(200).
				extract().body().as(getObjectResponseTypeRefCrossReference());
		
		Reference reference = new Reference();
		reference.setCurie(curie);
		reference.setCrossReferences(List.of(response.getEntity()));
		reference.setObsolete(false);
		
		RestAssured.given().
			contentType("application/json").
			body(reference).
			when().
			post("/api/reference").
			then().
			statusCode(200);
	}
	
	public void loadSOTerm(String curie, String name) throws Exception {
		SOTerm soTerm = new SOTerm();
		soTerm.setCurie(curie);
		soTerm.setName(name);
		soTerm.setObsolete(false);
		soTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
		
		RestAssured.given().
			contentType("application/json").
			body(soTerm).
			when().
			put("/api/soterm").
			then().
			statusCode(200);
	}
	
	public void loadZecoTerm(String curie, String name, String subset) throws Exception {
		ZECOTerm zecoTerm = new ZECOTerm();
		zecoTerm.setCurie(curie);
		zecoTerm.setName(name);
		zecoTerm.setObsolete(false);
		List<String> subsets = new ArrayList<String>();
		if (subset != null) {
			subsets.add(subset);
			zecoTerm.setSubsets(subsets);
		}
		zecoTerm.setSecondaryIdentifiers(List.of(curie + "secondary"));
			
		RestAssured.given().
			contentType("application/json").
			body(zecoTerm).
			when().
			post("/api/zecoterm").
			then().
			statusCode(200);
	}
}
