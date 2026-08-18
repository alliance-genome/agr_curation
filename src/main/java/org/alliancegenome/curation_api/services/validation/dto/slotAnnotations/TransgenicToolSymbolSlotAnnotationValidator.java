package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolSymbolSlotAnnotationValidator extends NameSlotAnnotationValidator<TransgenicToolSymbolSlotAnnotation> {

	@Inject TransgenicToolSymbolSlotAnnotationDAO transgenicToolSymbolDAO;
	@Inject TransgenicToolDAO transgenicToolDAO;

	public ObjectResponse<TransgenicToolSymbolSlotAnnotation> validateTransgenicToolSymbolSlotAnnotation(TransgenicToolSymbolSlotAnnotation uiEntity) {
		TransgenicToolSymbolSlotAnnotation symbol = validateTransgenicToolSymbolSlotAnnotation(uiEntity, false, false);
		response.setEntity(symbol);
		return response;
	}

	public TransgenicToolSymbolSlotAnnotation validateTransgenicToolSymbolSlotAnnotation(TransgenicToolSymbolSlotAnnotation uiEntity, Boolean throwError, Boolean validateTransgenicTool) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update TransgenicToolSymbolSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		TransgenicToolSymbolSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = transgenicToolSymbolDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find TransgenicToolSymbolSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new TransgenicToolSymbolSlotAnnotation();
			newEntity = true;
		}
		dbEntity = (TransgenicToolSymbolSlotAnnotation) validateNameSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm nameType = validateRequiredTermInVocabularyTermSet("nameType", VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET, uiEntity.getNameType(), dbEntity.getNameType());
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
