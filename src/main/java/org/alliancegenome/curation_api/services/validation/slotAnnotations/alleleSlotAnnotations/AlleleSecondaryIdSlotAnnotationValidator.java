package org.alliancegenome.curation_api.services.validation.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.SlotAnnotationValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleSecondaryIdSlotAnnotationValidator extends SlotAnnotationValidator<AlleleSecondaryIdSlotAnnotation> {

	@Inject
	AlleleSecondaryIdSlotAnnotationDAO alleleSecondaryIdDAO;

	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> validateAlleleSecondaryIdSlotAnnotation(AlleleSecondaryIdSlotAnnotation uiEntity) {
		AlleleSecondaryIdSlotAnnotation secondaryId = validateAlleleSecondaryIdSlotAnnotation(uiEntity, false, false);
		response.setEntity(secondaryId);
		return response;
	}

	public AlleleSecondaryIdSlotAnnotation validateAlleleSecondaryIdSlotAnnotation(AlleleSecondaryIdSlotAnnotation uiEntity, Boolean throwError, Boolean validateAllele) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AlleleSecondaryIdSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AlleleSecondaryIdSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = alleleSecondaryIdDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AlleleSecondaryIdSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AlleleSecondaryIdSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AlleleSecondaryIdSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAllele) {
			Allele singleAllele = validateSingleAllele(uiEntity.getSingleAllele(), dbEntity.getSingleAllele());
			dbEntity.setSingleAllele(singleAllele);
		}

		String secondaryId = validateSecondaryId(uiEntity, dbEntity);
		dbEntity.setSecondaryId(secondaryId);

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

	private String validateSecondaryId(AlleleSecondaryIdSlotAnnotation uiEntity, AlleleSecondaryIdSlotAnnotation dbEntity) {
		String field = "secondaryId";
		if (StringUtils.isBlank(uiEntity.getSecondaryId())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		return uiEntity.getSecondaryId();
	}

}
