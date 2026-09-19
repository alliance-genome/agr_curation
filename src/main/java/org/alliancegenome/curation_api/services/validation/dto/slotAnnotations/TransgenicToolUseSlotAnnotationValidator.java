package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.dao.ontology.FbcvTermDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.TransgenicToolUseSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535: mirrors AlleleMutationTypeSlotAnnotationValidator, the closest existing validator for
 * a slot annotation whose payload is a required list of ontology terms.
 */
@RequestScoped
public class TransgenicToolUseSlotAnnotationValidator extends SlotAnnotationValidator<TransgenicToolUseSlotAnnotation> {

	@Inject TransgenicToolUseSlotAnnotationDAO lTransgenicToolUseDAO;
	@Inject TransgenicToolDAO lTransgenicToolDAO;
	@Inject FbcvTermDAO fbcvTermDAO;

	public ObjectResponse<TransgenicToolUseSlotAnnotation> validateTransgenicToolUseSlotAnnotation(TransgenicToolUseSlotAnnotation uiEntity) {
		TransgenicToolUseSlotAnnotation use = validateTransgenicToolUseSlotAnnotation(uiEntity, false, false);
		response.setEntity(use);
		return response;
	}

	public TransgenicToolUseSlotAnnotation validateTransgenicToolUseSlotAnnotation(TransgenicToolUseSlotAnnotation uiEntity, Boolean throwError, Boolean validateTransgenicTool) {

		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update TransgenicToolUseSlotAnnotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		TransgenicToolUseSlotAnnotation dbEntity = null;
		Boolean newEntity;
		if (id != null) {
			dbEntity = lTransgenicToolUseDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find TransgenicToolUseSlotAnnotation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new TransgenicToolUseSlotAnnotation();
			newEntity = true;
		}

		dbEntity = (TransgenicToolUseSlotAnnotation) validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (validateTransgenicTool) {
			TransgenicTool singleTransgenicTool = validateRequiredEntity(lTransgenicToolDAO, "singleTransgenicTool", uiEntity.getSingleTransgenicTool(), dbEntity.getSingleTransgenicTool());
			dbEntity.setSingleTransgenicTool(singleTransgenicTool);
		}

		List<FBCVTerm> uses = validateRequiredEntities(fbcvTermDAO, "uses", uiEntity.getUses(), dbEntity.getUses());
		dbEntity.setUses(uses);

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
