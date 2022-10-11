package org.alliancegenome.curation_api.services.validation.dto;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import javax.inject.Inject;

import javax.enterprise.context.RequestScoped;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AuditedObjectDTOValidator <E extends AuditedObject, D extends AuditedObjectDTO> {

	@Inject PersonService personService;
	
	public ObjectResponse<E> validateAuditedObjectDTO (E entity, D dto) {
		
		ObjectResponse<E> response = new ObjectResponse<E>();
		
		if (StringUtils.isNotBlank(dto.getCreatedBy())) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
			entity.setCreatedBy(createdBy);
		}
		if (StringUtils.isNotBlank(dto.getUpdatedBy())) {
			Person updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedBy());
			entity.setUpdatedBy(updatedBy);
		}
		
		Boolean internal = false;
		if (dto.getInternal() != null)
			internal = dto.getInternal();
		entity.setInternal(internal);
		
		Boolean obsolete = false;
		if (dto.getObsolete() != null)
			obsolete = dto.getObsolete();
		entity.setObsolete(obsolete);

		if (StringUtils.isNotBlank(dto.getDateUpdated())) {
			OffsetDateTime dateUpdated;
			try {
				dateUpdated = OffsetDateTime.parse(dto.getDateUpdated());
				entity.setDateUpdated(dateUpdated);
			} catch (DateTimeParseException e) {
				response.addErrorMessage("dateUpdated", ValidationConstants.INVALID_MESSAGE);
			}
		}

		if (StringUtils.isNotBlank(dto.getDateCreated())) {
			OffsetDateTime creationDate;
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
				entity.setDateCreated(creationDate);
			} catch (DateTimeParseException e) {
				response.addErrorMessage("dateCreated", ValidationConstants.INVALID_MESSAGE);
			}
		}
	
		response.setEntity(entity);
		
		return response;
	}
}
