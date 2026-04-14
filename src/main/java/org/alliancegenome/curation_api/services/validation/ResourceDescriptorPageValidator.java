package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ResourceDescriptorPageValidator extends AuditedObjectValidator<ResourceDescriptorPage> {

	@Inject ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	@Inject ResourceDescriptorDAO resourceDescriptorDAO;

	private String errorMessage;

	public ResourceDescriptorPage validateResourceDescriptorPageUpdate(ResourceDescriptorPage uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Resource Descriptor Page: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Resource Descriptor Page ID provided");
			throw new ApiErrorException(response);
		}
		ResourceDescriptorPage dbEntity = resourceDescriptorPageDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Resource Descriptor Page with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = (ResourceDescriptorPage) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateResourceDescriptorPage(uiEntity, dbEntity);
	}

	private ResourceDescriptorPage validateResourceDescriptorPage(ResourceDescriptorPage uiEntity, ResourceDescriptorPage dbEntity) {
		if (uiEntity.getResourceDescriptor() == null || uiEntity.getResourceDescriptor().getId() == null) {
			addMessageResponse("resourceDescriptor", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ResourceDescriptor resourceDescriptor = resourceDescriptorDAO.find(uiEntity.getResourceDescriptor().getId());
			if (resourceDescriptor == null) {
				addMessageResponse("resourceDescriptor", ValidationConstants.INVALID_MESSAGE);
			} else {
				dbEntity.setResourceDescriptor(resourceDescriptor);
			}
		}

		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse("name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setName(uiEntity.getName());
		}

		if (StringUtils.isBlank(uiEntity.getUrlTemplate())) {
			addMessageResponse("urlTemplate", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			dbEntity.setUrlTemplate(uiEntity.getUrlTemplate());
		}
		dbEntity.setPageDescription(uiEntity.getPageDescription());

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}
}
