package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class InformationContentEntityValidator extends AuditedObjectValidator<InformationContentEntity> {

	@Inject InformationContentEntityService informationContentEntityService;

	public ObjectResponse<InformationContentEntity> validateInformationContentEntity(InformationContentEntity uiEntity) {
		InformationContentEntity ice = validateInformationContentEntity(uiEntity, false);
		response.setEntity(ice);
		return response;
	}

	public InformationContentEntity validateInformationContentEntity(InformationContentEntity uiEntity, Boolean throwError) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update InformationContentEntity: [" + uiEntity.getCurie() + "]";

		if (uiEntity.getCurie() == null) {
			addMessageResponse("No InformationContentEntity curie submitted");
			throw new ApiErrorException(response);
		}

		InformationContentEntity dbEntity = informationContentEntityService.retrieveFromDbOrLiteratureService(uiEntity.getCurie());
		if (dbEntity == null) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, false);

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
