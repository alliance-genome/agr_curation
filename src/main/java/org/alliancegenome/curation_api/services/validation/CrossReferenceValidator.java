package org.alliancegenome.curation_api.services.validation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.response.ObjectResponse;

@RequestScoped
public class CrossReferenceValidator extends AuditedObjectValidator<CrossReference> {

	@Inject
	CrossReferenceDAO crossReferenceDAO;

	public ObjectResponse<CrossReference> validateCrossReference(CrossReference uiEntity) {
		CrossReference crossReference = validateCrossReference(uiEntity, false);
		response.setEntity(crossReference);
		return response;
	}

	public CrossReference validateCrossReference(CrossReference uiEntity, Boolean throwError) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not create/update CrossReference: [" + uiEntity.getCurie() + "]";

		String curie = uiEntity.getCurie();
		CrossReference dbEntity = null;
		Boolean newEntity;
		if (curie != null) {
			dbEntity = crossReferenceDAO.find(curie);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("curie", ValidationConstants.REQUIRED_MESSAGE);
				if (throwError) {
					throw new ApiErrorException(response);
				} else {
					return null;
				}
			}
		} else {
			dbEntity = new CrossReference();
			newEntity = true;
		}

		dbEntity = (CrossReference) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			}
			return null;
		}

		return dbEntity;
	}
}