package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535. */
@RequestScoped
public class TransgenicToolSynonymSlotAnnotationValidator extends NameSlotAnnotationValidator<TransgenicToolSynonymSlotAnnotation> {

	@Inject TransgenicToolSynonymSlotAnnotationDAO lTransgenicToolSynonymDAO;
	@Inject TransgenicToolDAO lTransgenicToolDAO;

	public ObjectResponse<TransgenicToolSynonymSlotAnnotation> validateTransgenicToolSynonymSlotAnnotation(TransgenicToolSynonymSlotAnnotation uiEntity) {
		TransgenicToolSynonymSlotAnnotation annotation = validateTransgenicToolSynonymSlotAnnotation(uiEntity, false, false);
		response.setEntity(annotation);
		return response;
	}

	public TransgenicToolSynonymSlotAnnotation validateTransgenicToolSynonymSlotAnnotation(TransgenicToolSynonymSlotAnnotation uiEntity, Boolean throwError, Boolean validateTransgenicTool) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update TransgenicToolSynonymSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		TransgenicToolSynonymSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = lTransgenicToolSynonymDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find TransgenicToolSynonymSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new TransgenicToolSynonymSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (TransgenicToolSynonymSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);


		if (validateTransgenicTool) {
			TransgenicTool singleTransgenicTool = validateRequiredEntity(lTransgenicToolDAO, "singleTransgenicTool", uiEntity.getSingleTransgenicTool(), dbEntity.getSingleTransgenicTool());
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
