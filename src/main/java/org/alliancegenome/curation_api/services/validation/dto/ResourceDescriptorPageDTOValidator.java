package org.alliancegenome.curation_api.services.validation.dto;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorPageDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class ResourceDescriptorPageDTOValidator extends BaseDTOValidator {

	public ObjectResponse<ResourceDescriptorPage> validateResourceDescriptorPageDTO(ResourceDescriptorPageDTO dto) {
		ResourceDescriptorPage rdPage = new ResourceDescriptorPage();
		ObjectResponse<ResourceDescriptorPage> rdPageResponse = new ObjectResponse<ResourceDescriptorPage>();

		if (StringUtils.isBlank(dto.getName()))
			rdPageResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		rdPage.setName(dto.getName());

		if (StringUtils.isBlank(dto.getUrl()))
			rdPageResponse.addErrorMessage("url", ValidationConstants.REQUIRED_MESSAGE);
		rdPage.setUrlTemplate(dto.getUrl());
			
		String pageDescription = null;
		if (StringUtils.isNotBlank(dto.getDescription()))
			pageDescription = dto.getDescription();
		rdPage.setPageDescription(pageDescription);
		
		rdPageResponse.setEntity(rdPage);

		return rdPageResponse;
	}
}
