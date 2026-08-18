package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolFullNameSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolFullNameSlotAnnotationValidator extends NameSlotAnnotationValidator<TransgenicToolFullNameSlotAnnotation> {

	@Inject TransgenicToolFullNameSlotAnnotationDAO transgenicToolFullNameDAO;
	@Inject TransgenicToolDAO transgenicToolDAO;

	public ObjectResponse<TransgenicToolFullNameSlotAnnotation> validateTransgenicToolFullNameSlotAnnotation(TransgenicToolFullNameSlotAnnotation uiEntity) {
		TransgenicToolFullNameSlotAnnotation fullName = validateTransgenicToolFullNameSlotAnnotation(uiEntity, false, false);
		response.setEntity(fullName);
		return response;
	}

	public TransgenicToolFullNameSlotAnnotation validateTransgenicToolFullNameSlotAnnotation(TransgenicToolFullNameSlotAnnotation uiEntity, Boolean throwError, Boolean validateTransgenicTool) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update TransgenicToolFullNameSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		TransgenicToolFullNameSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = transgenicToolFullNameDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find TransgenicToolFullNameSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new TransgenicToolFullNameSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (TransgenicToolFullNameSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.FULL_NAME_TYPE_TERM_SET, uiEntity.getNameType(), dbEntity.getNameType());
		dbEntity.setNameType(nameType);

		if (validateTransgenicTool) {
			TransgenicTool singleTransgenicTool = validateRequiredEntity(transgenicToolDAO, "singleTransgenicTool", uiEntity.getSingleTransgenicTool(), dbEntity.getSingleTransgenicTool());
			dbEntity.setSingleTransgenicTool(singleTransgenicTool);
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
