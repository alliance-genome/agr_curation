package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ResourceDescriptorValidator extends AuditedObjectValidator<ResourceDescriptor> {

	@Inject ResourceDescriptorDAO resourceDescriptorDAO;

	private String errorMessage;

	public ResourceDescriptor validateResourceDescriptorUpdate(ResourceDescriptor uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Resource Descriptor: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Resource Descriptor ID provided");
			throw new ApiErrorException(response);
		}
		ResourceDescriptor dbEntity = resourceDescriptorDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Resource Descriptor with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = (ResourceDescriptor) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateResourceDescriptor(uiEntity, dbEntity);
	}

	private ResourceDescriptor validateResourceDescriptor(ResourceDescriptor uiEntity, ResourceDescriptor dbEntity) {
		if (StringUtils.isBlank(uiEntity.getPrefix())) {
			addMessageResponse("prefix", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setPrefix(uiEntity.getPrefix());
		}

		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setName(uiEntity.getName());
		}

		dbEntity.setSynonyms(uiEntity.getSynonyms());

		if (StringUtils.isBlank(uiEntity.getIdPattern())) {
			addMessageResponse("idPattern", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setIdPattern(uiEntity.getIdPattern());
		}

		dbEntity.setIdExample(uiEntity.getIdExample());

		if (StringUtils.isBlank(uiEntity.getDefaultUrlTemplate())) {
			addMessageResponse("defaultUrlTemplate", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setDefaultUrlTemplate(uiEntity.getDefaultUrlTemplate());
		}

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}
}
