package org.alliancegenome.curation_api.services.helpers.validators;

import java.time.OffsetDateTime;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.*;

@RequestScoped
public class ReferenceValidator extends AuditedObjectValidator<Reference> {

	@Inject
	ReferenceService referenceService;
	@Inject
	ReferenceDAO referenceDAO;

	@Inject
	LoggedInPersonService loggedInPersonService;

	public ObjectResponse<Reference> validateReference(Reference uiEntity) {
		Reference reference = validateReference(uiEntity, false);
		response.setEntity(reference);
		return response;
	}
	
	public Reference validateReference(Reference uiEntity, Boolean throwError) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update Reference: [" + uiEntity.getCurie() + "]";

		if (uiEntity.getCurie() == null) {
			addMessageResponse("No reference submitted");
			throw new ApiErrorException(response);
		}
		
		Reference dbEntity = referenceDAO.find(uiEntity.getCurie());
		
		if (dbEntity == null || dbEntity.getObsolete()) {
			dbEntity = referenceService.retrieveFromLiteratureService(uiEntity.getCurie());
			if (dbEntity == null) {
				addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
				return null;
			}
		}
		
		Boolean internal = validateInternal(uiEntity);
		dbEntity.setInternal(internal);
		
		dbEntity.setDateCreated(uiEntity.getDateCreated());
		
		if (uiEntity.getCreatedBy() != null) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(uiEntity.getCreatedBy().getUniqueId());
			dbEntity.setCreatedBy(createdBy);
		}

		LoggedInPerson updatedBy = loggedInPersonService.findLoggedInPersonByOktaEmail(authenticatedPerson.getOktaEmail());
		dbEntity.setUpdatedBy(updatedBy);
		
		dbEntity.setDateUpdated(OffsetDateTime.now());
		
		dbEntity = (Reference) validateAuditedObjectFields(uiEntity, dbEntity);
		
		if (dbEntity.getObsolete() && !uiEntity.getCurie().equals(dbEntity.getCurie())) {
			addMessageResponse("curie", ValidationConstants.OBSOLETE_MESSAGE);
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
