package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.PhenotypeTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleInheritanceModeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleInheritanceModeSlotAnnotationValidator extends SlotAnnotationValidator<AlleleInheritanceModeSlotAnnotation> {

	@Inject
	AlleleInheritanceModeSlotAnnotationDAO alleleInheritanceModeDAO;
	@Inject
	PhenotypeTermDAO phenotypeTermDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;

	public ObjectResponse<AlleleInheritanceModeSlotAnnotation> validateAlleleInheritanceModeSlotAnnotation(AlleleInheritanceModeSlotAnnotation uiEntity) {
		AlleleInheritanceModeSlotAnnotation mutationType = validateAlleleInheritanceModeSlotAnnotation(uiEntity, false, false);
		response.setEntity(mutationType);
		return response;
	}

	public AlleleInheritanceModeSlotAnnotation validateAlleleInheritanceModeSlotAnnotation(AlleleInheritanceModeSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleInheritanceModeSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleInheritanceModeSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleInheritanceModeDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleInheritanceModeSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleInheritanceModeSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleInheritanceModeSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		VocabularyTerm inheritanceMode = validateInheritanceMode(uiEntity, dbEntity);
		dbEntity.setInheritanceMode(inheritanceMode);
		
		PhenotypeTerm phenotypeTerm = validatePhenotypeTerm(uiEntity, dbEntity);
		dbEntity.setPhenotypeTerm(phenotypeTerm);
		
		String phenotypeStatement = handleStringField(uiEntity.getPhenotypeStatement());
		dbEntity.setPhenotypeStatement(phenotypeStatement);

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

	private VocabularyTerm validateInheritanceMode(AlleleInheritanceModeSlotAnnotation uiEntity, AlleleInheritanceModeSlotAnnotation dbEntity) {
		String field = "inheritanceMode";

		if (uiEntity.getInheritanceMode() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm inheritanceMode = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.ALLELE_INHERITANCE_MODE_VOCABULARY, uiEntity.getInheritanceMode().getName());
		if (inheritanceMode == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (inheritanceMode.getObsolete() && (dbEntity.getInheritanceMode() == null || !inheritanceMode.getName().equals(dbEntity.getInheritanceMode().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return inheritanceMode;
	}

	public PhenotypeTerm validatePhenotypeTerm(AlleleInheritanceModeSlotAnnotation uiEntity, AlleleInheritanceModeSlotAnnotation dbEntity) {
		String field = "phenotypeTerm";
		if (uiEntity.getPhenotypeTerm() == null)
			return null;
		
		PhenotypeTerm phenotypeTerm = phenotypeTermDAO.find(uiEntity.getPhenotypeTerm().getCurie());
		if (phenotypeTerm == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		} else if (phenotypeTerm.getObsolete() && (dbEntity.getPhenotypeTerm() == null || !phenotypeTerm.getCurie().equals(dbEntity.getPhenotypeTerm().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return phenotypeTerm;
	}
}
