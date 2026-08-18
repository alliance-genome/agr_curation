package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolSynonymSlotAnnotationValidator extends NameSlotAnnotationValidator<TransgenicToolSynonymSlotAnnotation> {

	@Inject TransgenicToolSynonymSlotAnnotationDAO transgenicToolSynonymDAO;
	@Inject TransgenicToolDAO transgenicToolDAO;

	public ObjectResponse<TransgenicToolSynonymSlotAnnotation> validateTransgenicToolSynonymSlotAnnotation(TransgenicToolSynonymSlotAnnotation uiEntity) {
		TransgenicToolSynonymSlotAnnotation synonym = validateTransgenicToolSynonymSlotAnnotation(uiEntity, false, false);
		response.setEntity(synonym);
		return response;
	}

	public TransgenicToolSynonymSlotAnnotation validateTransgenicToolSynonymSlotAnnotation(TransgenicToolSynonymSlotAnnotation uiEntity, Boolean throwError, Boolean validateTransgenicTool) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update TransgenicToolSynonymSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		TransgenicToolSynonymSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = transgenicToolSynonymDAO.find(id);
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

		VocabularyTerm nameType = validateRequiredTermInVocabulary("nameType", VocabularyConstants.NAME_TYPE_VOCABULARY, uiEntity.getNameType(), dbEntity.getNameType());
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
