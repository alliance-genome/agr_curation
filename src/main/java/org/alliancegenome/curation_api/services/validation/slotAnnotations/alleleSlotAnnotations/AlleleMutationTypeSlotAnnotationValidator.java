package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;


@RequestScoped
public class AlleleMutationTypeSlotAnnotationValidator extends SlotAnnotationValidator<AlleleMutationTypeSlotAnnotation> {

	@Inject AlleleMutationTypeSlotAnnotationDAO alleleMutationTypeDAO;
	@Inject SoTermDAO soTermDAO;
	@Inject AlleleDAO alleleDAO;


	public ObjectResponse<AlleleMutationTypeSlotAnnotation> validateAlleleMutationTypeSlotAnnotation(AlleleMutationTypeSlotAnnotation uiEntity) {
		AlleleMutationTypeSlotAnnotation mutationType = validateAlleleMutationTypeSlotAnnotation(uiEntity, false, false);
		response.setEntity(mutationType);
		return response;
	}

	public AlleleMutationTypeSlotAnnotation validateAlleleMutationTypeSlotAnnotation(AlleleMutationTypeSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleMutationTypeSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleMutationTypeSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleMutationTypeDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleMutationTypeSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleMutationTypeSlotAnnotation();
			newEntity = true;
		}
		
		dbEntity = (AlleleMutationTypeSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);
		
		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}
		
		List<SOTerm> mutationTypes = validateMutationTypes(uiEntity, dbEntity);
		dbEntity.setMutationTypes(mutationTypes);

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
	
	private List<SOTerm> validateMutationTypes (AlleleMutationTypeSlotAnnotation uiEntity, AlleleMutationTypeSlotAnnotation dbEntity) {
		String field = "mutationTypes";
		if (CollectionUtils.isEmpty(uiEntity.getMutationTypes())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		List<SOTerm> validMutationTypes = new ArrayList<>();
		for (SOTerm mt : uiEntity.getMutationTypes()) {
			SOTerm mutationType = soTermDAO.find(mt.getCurie());
			if (mutationType == null ) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (mutationType.getObsolete() &&
					(CollectionUtils.isEmpty(dbEntity.getMutationTypes()) || !dbEntity.getMutationTypes().contains(mutationType))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}

			validMutationTypes.add(mutationType);

		}
		return validMutationTypes;
	}
	
}
