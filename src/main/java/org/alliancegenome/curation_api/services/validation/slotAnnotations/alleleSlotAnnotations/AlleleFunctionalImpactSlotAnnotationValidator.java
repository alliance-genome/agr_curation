package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.dao.ontology.PhenotypeTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleFunctionalImpactSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

@RequestScoped
public class AlleleFunctionalImpactSlotAnnotationValidator extends SlotAnnotationValidator<AlleleFunctionalImpactSlotAnnotation> {

	@Inject
	AlleleFunctionalImpactSlotAnnotationDAO alleleFunctionalImpactDAO;
	@Inject
	PhenotypeTermDAO phenotypeTermDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;

	public ObjectResponse<AlleleFunctionalImpactSlotAnnotation> validateAlleleFunctionalImpactSlotAnnotation(AlleleFunctionalImpactSlotAnnotation uiEntity) {
		AlleleFunctionalImpactSlotAnnotation mutationType = validateAlleleFunctionalImpactSlotAnnotation(uiEntity, false, false);
		response.setEntity(mutationType);
		return response;
	}

	public AlleleFunctionalImpactSlotAnnotation validateAlleleFunctionalImpactSlotAnnotation(AlleleFunctionalImpactSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleFunctionalImpactSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleFunctionalImpactSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleFunctionalImpactDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleFunctionalImpactSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleFunctionalImpactSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleFunctionalImpactSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		List<VocabularyTerm> functionalImpacts = validateFunctionalImpacts(uiEntity, dbEntity);
		dbEntity.setFunctionalImpacts(functionalImpacts);
		
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

	private List<VocabularyTerm> validateFunctionalImpacts(AlleleFunctionalImpactSlotAnnotation uiEntity, AlleleFunctionalImpactSlotAnnotation dbEntity) {
		String field = "functionalImpacts";
		if (CollectionUtils.isEmpty(uiEntity.getFunctionalImpacts())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		List<VocabularyTerm> validFunctionalImpacts = new ArrayList<>();
		for (VocabularyTerm fi : uiEntity.getFunctionalImpacts()) {
			VocabularyTerm functionalImpact = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.ALLELE_FUNCTIONAL_IMPACT_VOCABULARY, fi.getName());
			if (functionalImpact == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (functionalImpact.getObsolete() && (CollectionUtils.isEmpty(dbEntity.getFunctionalImpacts()) || !dbEntity.getFunctionalImpacts().contains(functionalImpact))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}

			validFunctionalImpacts.add(functionalImpact);

		}
		return validFunctionalImpacts;
	}

	public PhenotypeTerm validatePhenotypeTerm(AlleleFunctionalImpactSlotAnnotation uiEntity, AlleleFunctionalImpactSlotAnnotation dbEntity) {
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
