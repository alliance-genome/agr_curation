package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AGMDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;
	
	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	
	@Inject
	AlleleDAO alleleDAO;
	
	public AGMDiseaseAnnotation validateAnnotation(AGMDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update AGM Disease Annotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No AGM Disease Annotation ID provided");
			throw new ApiErrorException(response);
		}
		AGMDiseaseAnnotation dbEntity = agmDiseaseAnnotationDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find AGM Disease Annotation with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Disease Annotation ID has not been found
		}		

		AffectedGenomicModel subject = validateSubject(uiEntity, dbEntity);
		dbEntity.setSubject(subject);
		
		Gene inferredGene = validateInferredGene(uiEntity);
		dbEntity.setInferredGene(inferredGene);
		
		Gene assertedGene = validateAssertedGene(uiEntity);
		dbEntity.setAssertedGene(assertedGene);
		
		Allele inferredAllele = validateInferredAllele(uiEntity);
		dbEntity.setInferredAllele(inferredAllele);
		
		Allele assertedAllele = validateAssertedAllele(uiEntity);
		dbEntity.setAssertedAllele(assertedAllele);
		
		VocabularyTerm relation = validateDiseaseRelation(uiEntity);
		dbEntity.setDiseaseRelation(relation);

		dbEntity = (AGMDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);

		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	private AffectedGenomicModel validateSubject(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {
		if (ObjectUtils.isEmpty(uiEntity.getSubject()) || StringUtils.isBlank(uiEntity.getSubject().getCurie())) {
			addMessageResponse("subject", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		AffectedGenomicModel subjectEntity = affectedGenomicModelDAO.find(uiEntity.getSubject().getCurie());
		if (subjectEntity == null) {
			addMessageResponse("subject", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		return subjectEntity;

	}

	private Gene validateInferredGene(AGMDiseaseAnnotation uiEntity) {
		if (uiEntity.getInferredGene() == null)
			return null;
		
		Gene inferredGene = geneDAO.find(uiEntity.getInferredGene().getCurie());
		if (inferredGene == null) {
			addMessageResponse("inferredGene", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		return inferredGene;
	}

	private Gene validateAssertedGene(AGMDiseaseAnnotation uiEntity) {
		if (uiEntity.getAssertedGene() == null)
			return null;
		
		Gene assertedGene = geneDAO.find(uiEntity.getAssertedGene().getCurie());
		if (assertedGene == null) {
			addMessageResponse("assertedGene", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		return assertedGene;
	}

	private Allele validateInferredAllele(AGMDiseaseAnnotation uiEntity) {
		if (uiEntity.getInferredAllele() == null)
			return null;
		
		Allele inferredAllele = alleleDAO.find(uiEntity.getInferredAllele().getCurie());
		if (inferredAllele == null) {
			addMessageResponse("inferredAllele", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		return inferredAllele;
	}
	
	private Allele validateAssertedAllele(AGMDiseaseAnnotation uiEntity) {
		if (uiEntity.getAssertedAllele() == null)
			return null;
		
		Allele assertedAllele = alleleDAO.find(uiEntity.getAssertedAllele().getCurie());
		if (assertedAllele == null) {
			addMessageResponse("assertedAllele", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		return assertedAllele;
	}
	
	private VocabularyTerm validateDiseaseRelation(AGMDiseaseAnnotation uiEntity) {
		String field = "diseaseRelation";
		if (uiEntity.getDiseaseRelation() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		
		VocabularyTerm relation = vocabularyTermDAO.getTermInVocabulary(uiEntity.getDiseaseRelation().getName(), VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY);

		if(relation == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		return relation;
	}
}
