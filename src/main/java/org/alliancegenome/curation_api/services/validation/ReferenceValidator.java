package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ReferenceValidator extends AuditedObjectValidator<Reference> {

	@Inject ReferenceService referenceService;

	public ObjectResponse<Reference> validateReference(Reference uiEntity) {
		Reference reference = validateReference(uiEntity, false);
		response.setEntity(reference);
		return response;
	}

	/**
	 * Resolves the stored reference a submitted one names by its curie, or by one of its cross reference curies,
	 * from the database or the literature service. The submitted entry only names the reference: none of its
	 * other fields are read, and the stored reference is returned as it is.
	 *
	 * @param uiEntity the submitted reference
	 * @param throwError not consulted: a reference that cannot be resolved is returned as null
	 * @return the stored reference, or null when none matches
	 */
	public Reference validateReference(Reference uiEntity, Boolean throwError) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update Reference: [" + uiEntity.getCurie() + "]";

		if (uiEntity.getCurie() == null) {
			addMessageResponse("No reference submitted");
			throw new ApiErrorException(response);
		}

		Reference dbEntity = referenceService.retrieveFromDbOrLiteratureService(uiEntity.getCurie());
		if (dbEntity == null) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			return null;
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
