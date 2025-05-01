package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.AgmSecondaryIdSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmSecondaryIdSlotAnnotationValidator extends SecondaryIdSlotAnnotationValidator<AgmSecondaryIdSlotAnnotation> {
	
	@Inject AgmSecondaryIdSlotAnnotationDAO agmSecondaryIdDAO;
	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

	public ObjectResponse<AgmSecondaryIdSlotAnnotation> validateAgmSecondaryIdSlotAnnotation(AgmSecondaryIdSlotAnnotation uiEntity) {
		AgmSecondaryIdSlotAnnotation secondaryId = validateAgmSecondaryIdSlotAnnotation(uiEntity, false, false);
		response.setEntity(secondaryId);
		return response;
	}

	public AgmSecondaryIdSlotAnnotation validateAgmSecondaryIdSlotAnnotation(AgmSecondaryIdSlotAnnotation uiEntity, Boolean throwError, Boolean validateAgm) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AgmSecondaryIdSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AgmSecondaryIdSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = agmSecondaryIdDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AgmSecondaryIdSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AgmSecondaryIdSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (AgmSecondaryIdSlotAnnotation) validateSecondaryIdSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateAgm) {
			AffectedGenomicModel singleAgm = validateRequiredEntity(affectedGenomicModelDAO, "singleAgm", uiEntity.getSingleAgm(), dbEntity.getSingleAgm());
			dbEntity.setSingleAgm(singleAgm);
		}

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

}
