package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.AgmFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmFullNameSlotAnnotationValidator extends NameSlotAnnotationValidator<AgmFullNameSlotAnnotation> {

	@Inject AgmFullNameSlotAnnotationDAO agmFullNameDAO;
	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

	public ObjectResponse<AgmFullNameSlotAnnotation> validateAgmFullNameSlotAnnotation(AgmFullNameSlotAnnotation uiEntity) {
		AgmFullNameSlotAnnotation fullName = validateAgmFullNameSlotAnnotation(uiEntity, false, false);
		response.setEntity(fullName);
		return response;
	}

	public AgmFullNameSlotAnnotation validateAgmFullNameSlotAnnotation(AgmFullNameSlotAnnotation uiEntity, Boolean throwError, Boolean validateAgm) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update AgmFullNameSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		AgmFullNameSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = agmFullNameDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find AgmFullNameSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new AgmFullNameSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (AgmFullNameSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.FULL_NAME_TYPE_TERM_SET, uiEntity.getNameType(), dbEntity.getNameType());
		dbEntity.setNameType(nameType);

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
