package org.alliancegenome.curation_api.services.validation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class GeneDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

	@Inject
	GeneDAO geneDAO;
	@Inject
	AffectedGenomicModelDAO agmDAO;
	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	
	private String errorMessage;
	
	public GeneDiseaseAnnotation validateAnnotationUpdate(GeneDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Gene Disease Annotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Gene Disease Annotation ID provided");
			throw new ApiErrorException(response);
		}
		GeneDiseaseAnnotation dbEntity = geneDiseaseAnnotationDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Gene Disease Annotation with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Disease Annotation ID has not been found
		}		
		
		return validateAnnotation(uiEntity, dbEntity);
	}
	
	public GeneDiseaseAnnotation validateAnnotationCreate(GeneDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Cound not create Gene Disease Annotation";;
		
		GeneDiseaseAnnotation dbEntity = new GeneDiseaseAnnotation();
		
		return validateAnnotation(uiEntity, dbEntity);
	}
	
	public GeneDiseaseAnnotation validateAnnotation(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {

		Gene subject = validateSubject(uiEntity, dbEntity);
		dbEntity.setSubject(subject);

		VocabularyTerm relation = validateDiseaseRelation(uiEntity, dbEntity);
		dbEntity.setDiseaseRelation(relation);

		AffectedGenomicModel sgdStrainBackground = validateSgdStrainBackground(uiEntity, dbEntity);
		dbEntity.setSgdStrainBackground(sgdStrainBackground);
		
		dbEntity = (GeneDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}
	
	private Gene validateSubject(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {
		if (ObjectUtils.isEmpty(uiEntity.getSubject()) || StringUtils.isBlank(uiEntity.getSubject().getCurie())) {
			addMessageResponse("subject", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		
		Gene subjectEntity = geneDAO.find(uiEntity.getSubject().getCurie());
		if (subjectEntity == null) {
			addMessageResponse("subject", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		if (subjectEntity.getObsolete() && (dbEntity.getSubject() == null || !subjectEntity.getCurie().equals(dbEntity.getSubject().getCurie()))) {
			addMessageResponse("subject", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return subjectEntity;

	}
	
	private VocabularyTerm validateDiseaseRelation(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {
		String field = "diseaseRelation";
		if (uiEntity.getDiseaseRelation() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		
		VocabularyTerm relation = vocabularyTermDAO.getTermInVocabularyTermSet(VocabularyConstants.GENE_DISEASE_RELATION_VOCABULARY_TERM_SET, uiEntity.getDiseaseRelation().getName());

		if(relation == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		if (relation.getObsolete() && (dbEntity.getDiseaseRelation() == null || !relation.getName().equals(dbEntity.getDiseaseRelation().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return relation;
	}
	
	private AffectedGenomicModel validateSgdStrainBackground(GeneDiseaseAnnotation uiEntity, GeneDiseaseAnnotation dbEntity) {
		if (uiEntity.getSgdStrainBackground() == null)
			return null;
		
		AffectedGenomicModel sgdStrainBackground = agmDAO.find(uiEntity.getSgdStrainBackground().getCurie());
		if (sgdStrainBackground == null || !sgdStrainBackground.getTaxon().getName().startsWith("Saccharomyces cerevisiae")) {
			addMessageResponse("sgdStrainBackground", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		if (sgdStrainBackground.getObsolete() && (dbEntity.getSgdStrainBackground() == null || !sgdStrainBackground.getCurie().equals(dbEntity.getSgdStrainBackground().getCurie()))) {
			addMessageResponse("sgdStrainBackground", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return sgdStrainBackground;
	}
}
