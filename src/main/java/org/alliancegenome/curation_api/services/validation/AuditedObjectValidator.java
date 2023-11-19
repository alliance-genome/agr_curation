package org.alliancegenome.curation_api.services.validation;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	public void constructErrorMessagesFromSupplementalData(String fieldName) {
		if (response.getSupplementalData() == null)
			return;
		Map<String, Object> errorMap = (Map<String, Object>) response.getSupplementalData().get("listErrorMap"); 
		if (errorMap == null)
			return;
		Map<String, Object> fieldErrorMap = (Map<String, Object>) errorMap.get(fieldName);
		if (fieldErrorMap == null)
			return;
		Map<String, Set<String>> consolidatedErrors = new LinkedHashMap<>();
		for (Map.Entry<String, Object> fieldRowError : fieldErrorMap.entrySet()) {
			Map<String, String> subfieldErrors = (Map<String, String>) fieldRowError.getValue();
			for (Map.Entry<String, String> subfieldError : subfieldErrors.entrySet()) {
				Set<String> uniqueSubfieldErrors = consolidatedErrors.get(subfieldError.getKey());
				if (uniqueSubfieldErrors == null)
					uniqueSubfieldErrors = new HashSet<>();
				uniqueSubfieldErrors.add(subfieldError.getValue());
				consolidatedErrors.put(subfieldError.getKey(), uniqueSubfieldErrors);
			}
		}
		
		List<String> consolidatedMessages = new ArrayList<>();
		for (Map.Entry<String, Set<String>> consolidatedError : consolidatedErrors.entrySet()) {
			consolidatedMessages.add(consolidatedError.getKey() + " - " + consolidatedError.getValue().stream().sorted().collect(Collectors.joining("/")));
		}
		Collections.sort(consolidatedMessages);
		addMessageResponse(fieldName, String.join("|", consolidatedMessages));
	}
}
