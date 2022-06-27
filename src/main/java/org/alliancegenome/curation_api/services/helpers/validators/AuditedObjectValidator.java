package org.alliancegenome.curation_api.services.helpers.validators;

import java.time.OffsetDateTime;
import java.util.List;

import javax.inject.Inject;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.base.entity.AuditedObject;
import org.alliancegenome.curation_api.model.entities.LoggedInPerson;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.LoggedInPersonService;
import org.alliancegenome.curation_api.services.PersonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class AuditedObjectValidator<E extends AuditedObject> {

	@Inject
	@AuthenticatedUser
	protected LoggedInPerson authenticatedPerson;
	
	@Inject
	PersonService personService;
	
	@Inject
	LoggedInPersonService loggedInPersonService;
	
	public String invalidMessage = "Not a valid entry";
	public String obsoleteMessage = "Obsolete term specified";
	public String requiredMessage = "Required field is empty";
	public String dependencyMessagePrefix = "Invalid without value for ";
	
	public ObjectResponse<E> response;
	
	public E validateAuditedObjectFields(E uiEntity, E dbEntity) {
		Boolean internal = validateInternal(uiEntity);
		if (internal != null) dbEntity.setInternal(internal);
		
		if (uiEntity.getObsolete() != null)
			dbEntity.setObsolete(uiEntity.getObsolete());
		
		if (uiEntity.getDateCreated() != null)
			dbEntity.setDateCreated(uiEntity.getDateCreated());
		
		if (uiEntity.getCreatedBy() != null) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(uiEntity.getCreatedBy().getUniqueId());
			dbEntity.setCreatedBy(createdBy);
		}

		LoggedInPerson modifiedBy = loggedInPersonService.findLoggedInPersonByOktaEmail(authenticatedPerson.getOktaEmail());
		dbEntity.setModifiedBy(modifiedBy);
		
		dbEntity.setDateUpdated(OffsetDateTime.now());
		
		return dbEntity;
	}
	
	public Boolean validateInternal(E uiEntity) {
		if (uiEntity.getInternal() == null) {
			addMessageResponse("internal", requiredMessage);
			return null;
		}
		return uiEntity.getInternal();
	}
	
	public String handleStringField (String string) {
		if (!StringUtils.isBlank(string)) {
			return string;
		}
		return null;
	}
	
	public List<Object> handleListField (List<Object> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			return list;
		}
		return null;
	}
	
	public void addMessageResponse(String message) {
		response.setErrorMessage(message);
	}
	
	public void addMessageResponse(String fieldName, String message) {
		response.addErrorMessage(fieldName, message);
	}
}
