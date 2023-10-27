package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationUniqueIdHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class DiseaseAnnotationValidator extends AnnotationValidator {

	@Inject
	EcoTermDAO ecoTermDAO;
	@Inject
	DoTermDAO doTermDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	BiologicalEntityDAO biologicalEntityDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject
	DataProviderService dataProviderService;
	@Inject
	DataProviderValidator dataProviderValidator;

	public DOTerm validateObject(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "object";
		if (ObjectUtils.isEmpty(uiEntity.getObject()) || StringUtils.isEmpty(uiEntity.getObject().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		DOTerm diseaseTerm = doTermDAO.find(uiEntity.getObject().getCurie());
		if (diseaseTerm == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		} else if (diseaseTerm.getObsolete() && (dbEntity.getObject() == null || !diseaseTerm.getCurie().equals(dbEntity.getObject().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return diseaseTerm;
	}

	public List<VocabularyTerm> validateDiseaseQualifiers(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "diseaseQualifiers";
		if (CollectionUtils.isEmpty(uiEntity.getDiseaseQualifiers())) {
			return null;
		}
		List<VocabularyTerm> validDiseaseQualifiers = new ArrayList<>();
		for (VocabularyTerm dq : uiEntity.getDiseaseQualifiers()) {
			VocabularyTerm qualifier = vocabularyTermService.getTermInVocabulary(VocabularyConstants.DISEASE_QUALIFIER_VOCABULARY, dq.getName()).getEntity();
			if (qualifier == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (qualifier.getObsolete() && (CollectionUtils.isEmpty(dbEntity.getDiseaseQualifiers()) || !dbEntity.getDiseaseQualifiers().contains(qualifier))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}

			validDiseaseQualifiers.add(qualifier);

		}
		return validDiseaseQualifiers;
	}

	public List<ECOTerm> validateEvidenceCodes(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "evidenceCodes";
		if (CollectionUtils.isEmpty(uiEntity.getEvidenceCodes())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		List<ECOTerm> validEvidenceCodes = new ArrayList<>();
		for (ECOTerm ec : uiEntity.getEvidenceCodes()) {
			ECOTerm evidenceCode = ecoTermDAO.find(ec.getCurie());
			if (evidenceCode == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (evidenceCode.getObsolete() && (CollectionUtils.isEmpty(dbEntity.getEvidenceCodes()) || !dbEntity.getEvidenceCodes().contains(evidenceCode))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			if (!evidenceCode.getSubsets().contains(OntologyConstants.AGR_ECO_TERM_SUBSET)) {
				addMessageResponse(field, ValidationConstants.UNSUPPORTED_MESSAGE);
				return null;
			}
			validEvidenceCodes.add(evidenceCode);
		}
		return validEvidenceCodes;
	}

	public List<Gene> validateWith(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getWith()))
			return null;

		List<Gene> validWithGenes = new ArrayList<Gene>();
		List<String> previousCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getWith()))
			previousCuries = dbEntity.getWith().stream().map(Gene::getCurie).collect(Collectors.toList());
		for (Gene wg : uiEntity.getWith()) {
			Gene withGene = geneDAO.find(wg.getCurie());
			if (withGene == null || !withGene.getCurie().startsWith("HGNC:")) {
				addMessageResponse("with", ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (withGene.getObsolete() && !previousCuries.contains(withGene.getCurie())) {
				addMessageResponse("with", ValidationConstants.OBSOLETE_MESSAGE);
			} else {
				validWithGenes.add(withGene);
			}
		}

		return validWithGenes;
	}

	
	public DataProvider validateSecondaryDataProvider(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "secondaryDataProvider";
		
		if (uiEntity.getSecondaryDataProvider() == null) {
			if (dbEntity.getId() == null) {
				uiEntity.setSecondaryDataProvider(dataProviderService.createAllianceDataProvider());
				if (uiEntity.getSecondaryDataProvider() == null)
					return null;
			} else {
				return null;
			}
		}
		
		DataProvider uiDataProvider = uiEntity.getSecondaryDataProvider();
		DataProvider dbDataProvider = dbEntity.getSecondaryDataProvider();
		
		ObjectResponse<DataProvider> dpResponse = dataProviderValidator.validateDataProvider(uiDataProvider, dbDataProvider, false);
		if (dpResponse.hasErrors()) {
			addMessageResponse(field, dpResponse.errorMessagesString());
			return null;
		}
		
		DataProvider validatedDataProvider = dpResponse.getEntity();
		if (validatedDataProvider.getObsolete() && (dbDataProvider == null || !validatedDataProvider.getId().equals(dbDataProvider.getId()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return validatedDataProvider;
	}

	public List<BiologicalEntity> validateDiseaseGeneticModifiers(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getDiseaseGeneticModifiers())) {
			return null;
		}

		if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
			addMessageResponse("diseaseGeneticModifiers", ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifierRelation");
			return null;
		}

		List<BiologicalEntity> validModifiers = new ArrayList<>();
		List<String> previousCuries = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dbEntity.getDiseaseGeneticModifiers()))
			previousCuries = dbEntity.getDiseaseGeneticModifiers().stream().map(BiologicalEntity::getCurie).collect(Collectors.toList());
		for (BiologicalEntity modifier : uiEntity.getDiseaseGeneticModifiers()) {
			BiologicalEntity diseaseGeneticModifier = biologicalEntityDAO.find(modifier.getCurie());
		
			if (diseaseGeneticModifier == null) {
				addMessageResponse("diseaseGeneticModifiers", ValidationConstants.INVALID_MESSAGE);
				return null;
			}

			if (diseaseGeneticModifier.getObsolete() && !previousCuries.contains(diseaseGeneticModifier.getCurie())) {
				addMessageResponse("diseaseGeneticModifiers", ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			validModifiers.add(diseaseGeneticModifier);
		}

		return validModifiers;
	}

	public VocabularyTerm validateDiseaseGeneticModifierRelation(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "diseaseGeneticModifierRelation";
		if (uiEntity.getDiseaseGeneticModifierRelation() == null) {
			return null;
		}

		if (CollectionUtils.isEmpty(uiEntity.getDiseaseGeneticModifiers())) {
			addMessageResponse(field, ValidationConstants.DEPENDENCY_MESSAGE_PREFIX + "diseaseGeneticModifiers");
			return null;
		}

		VocabularyTerm dgmRelation = vocabularyTermService.getTermInVocabulary(VocabularyConstants.DISEASE_GENETIC_MODIFIER_RELATION_VOCABULARY, uiEntity.getDiseaseGeneticModifierRelation().getName()).getEntity();

		if (dgmRelation == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (dgmRelation.getObsolete() && (dbEntity.getDiseaseGeneticModifierRelation() == null || !dgmRelation.getName().equals(dbEntity.getDiseaseGeneticModifierRelation().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return dgmRelation;
	}

	public VocabularyTerm validateGeneticSex(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "geneticSex";
		if (uiEntity.getGeneticSex() == null) {
			return null;
		}

		VocabularyTerm geneticSex = vocabularyTermService.getTermInVocabulary(VocabularyConstants.GENETIC_SEX_VOCABULARY, uiEntity.getGeneticSex().getName()).getEntity();

		if (geneticSex == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (geneticSex.getObsolete() && (dbEntity.getGeneticSex() == null || !geneticSex.getName().equals(dbEntity.getGeneticSex().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return geneticSex;
	}

	public VocabularyTerm validateAnnotationType(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		String field = "annotationType";
		if (uiEntity.getAnnotationType() == null) {
			return null;
		}

		VocabularyTerm annotationType = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ANNOTATION_TYPE_VOCABULARY, uiEntity.getAnnotationType().getName()).getEntity();

		if (annotationType == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (annotationType.getObsolete() && (dbEntity.getAnnotationType() == null || !annotationType.getName().equals(dbEntity.getAnnotationType().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return annotationType;
	}

	public String validateUniqueId(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		
		if (dbEntity.getDataProvider() == null)
			return null;
		
		String uniqueId = DiseaseAnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(uiEntity);

		if (dbEntity.getUniqueId() == null || !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<DiseaseAnnotation> response = diseaseAnnotationDAO.findByField("uniqueId", uniqueId);
			if (response != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
				return null;
			}
		}

		return uniqueId;
	}

	public DiseaseAnnotation validateCommonDiseaseAnnotationFields(DiseaseAnnotation uiEntity, DiseaseAnnotation dbEntity) {
		
		DOTerm term = validateObject(uiEntity, dbEntity);
		dbEntity.setObject(term);

		List<ECOTerm> terms = validateEvidenceCodes(uiEntity, dbEntity);
		dbEntity.setEvidenceCodes(terms);

		List<Gene> genes = validateWith(uiEntity, dbEntity);
		dbEntity.setWith(genes);

		Boolean negated = uiEntity.getNegated() != null && uiEntity.getNegated();
		dbEntity.setNegated(negated);

		VocabularyTerm annotationType = validateAnnotationType(uiEntity, dbEntity);
		dbEntity.setAnnotationType(annotationType);

		VocabularyTerm geneticSex = validateGeneticSex(uiEntity, dbEntity);
		dbEntity.setGeneticSex(geneticSex);

		DataProvider secondaryDataProvider = validateSecondaryDataProvider(uiEntity, dbEntity);
		dbEntity.setSecondaryDataProvider(secondaryDataProvider);

		List<BiologicalEntity> diseaseGeneticModifiers = validateDiseaseGeneticModifiers(uiEntity, dbEntity);
		VocabularyTerm dgmRelation = validateDiseaseGeneticModifierRelation(uiEntity, dbEntity);
		dbEntity.setDiseaseGeneticModifiers(diseaseGeneticModifiers);
		dbEntity.setDiseaseGeneticModifierRelation(dgmRelation);

		List<VocabularyTerm> diseaseQualifiers = validateDiseaseQualifiers(uiEntity, dbEntity);
		dbEntity.setDiseaseQualifiers(diseaseQualifiers);

		dbEntity = (DiseaseAnnotation) validateCommonAnnotationFields(uiEntity, dbEntity, VocabularyConstants.DISEASE_ANNOTATION_NOTE_TYPES_VOCABULARY_TERM_SET);
		
		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		return dbEntity;
	}
}
