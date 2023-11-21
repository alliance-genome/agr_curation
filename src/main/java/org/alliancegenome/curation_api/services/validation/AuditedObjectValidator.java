package org.alliancegenome.curation_api.services.validation;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class AuditedObjectValidator<E extends AuditedObject> {

	@Inject
	@AuthenticatedUser
	protected Person authenticatedPerson;

	@Inject
	PersonService personService;

	public ObjectResponse<E> response;

	public E validateAuditedObjectFields(E uiEntity, E dbEntity, Boolean newEntity) {
		Boolean internal = uiEntity.getInternal() == null ? false : uiEntity.getInternal();
		dbEntity.setInternal(internal);

		Boolean obsolete = uiEntity.getObsolete() == null ? false : uiEntity.getObsolete();
		dbEntity.setObsolete(obsolete);

		if (newEntity && uiEntity.getDateCreated() == null) {
			dbEntity.setDateCreated(OffsetDateTime.now());
		} else {
			dbEntity.setDateCreated(uiEntity.getDateCreated());
		}

		if (uiEntity.getCreatedBy() != null) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(uiEntity.getCreatedBy().getUniqueId());
			dbEntity.setCreatedBy(createdBy);
		} else if (newEntity) {
			Person createdBy = personService.findPersonByOktaEmail(authenticatedPerson.getOktaEmail());
			dbEntity.setCreatedBy(createdBy);
		}

		Person updatedBy = personService.findPersonByOktaEmail(authenticatedPerson.getOktaEmail());
		dbEntity.setUpdatedBy(updatedBy);

		dbEntity.setDateUpdated(OffsetDateTime.now());

		return dbEntity;
	}

	public String handleStringField(String string) {
		if (!StringUtils.isBlank(string)) {
			return string;
		}
		return null;
	}

	public List<Object> handleListField(List<Object> list) {
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
	
	public void convertMapToErrorMessages(String fieldName) {
		response.convertMapToErrorMessages(fieldName);
	}

}
