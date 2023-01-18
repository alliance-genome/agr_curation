package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorDTO;
import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorPageDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class ResourceDescriptorDTOValidator extends BaseDTOValidator {
	
	@Inject
	ResourceDescriptorDAO resourceDescriptorDAO;
	@Inject
	ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	@Inject
	ResourceDescriptorPageDTOValidator resourceDescriptorPageDtoValidator;

	public ResourceDescriptor validateResourceDescriptorDTO(ResourceDescriptorDTO dto) throws ObjectValidationException {
		ObjectResponse<ResourceDescriptor> rdResponse = new ObjectResponse<ResourceDescriptor>();
		
		ResourceDescriptor rd = null;
		if (StringUtils.isBlank(dto.getDbPrefix())) {
			rdResponse.addErrorMessage("db_prefix", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			SearchResponse<ResourceDescriptor> rdSearchResponse = resourceDescriptorDAO.findByField("prefix", dto.getDbPrefix());
			if (rdSearchResponse == null || rdSearchResponse.getSingleResult() == null) {
				rd = new ResourceDescriptor();
				rd.setPrefix(dto.getDbPrefix());
			} else {
				rd = rdSearchResponse.getSingleResult();
			}
		}
		

		if (StringUtils.isBlank(dto.getName()))
			rdResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);
		rd.setName(dto.getName());

		
		if (CollectionUtils.isNotEmpty(dto.getAliases())) {
			rd.setSynonyms(dto.getAliases());
		} else {
			rd.setSynonyms(null);
		}
		
		String idPattern = null;
		if (StringUtils.isNotBlank(dto.getGidPattern()))
			idPattern = dto.getGidPattern();
		rd.setIdPattern(idPattern);
		
		String idExample = null;
		if (StringUtils.isNotBlank(dto.getExampleGid())) {
			idExample = dto.getExampleGid();
		} else if (StringUtils.isNotBlank(dto.getExampleId())) {
			idExample = dto.getExampleId();
		}
		rd.setIdExample(idExample);
		
		String defaultUrlTemplate = null;
		if (StringUtils.isNotBlank(dto.getDefaultUrl()))
			defaultUrlTemplate = dto.getDefaultUrl();
		rd.setDefaultUrlTemplate(defaultUrlTemplate);
		
		List<ResourceDescriptorPage> rdPages = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getPages())) {
			for (ResourceDescriptorPageDTO rdPageDTO : dto.getPages()) {
				ObjectResponse<ResourceDescriptorPage> rdPageResponse = resourceDescriptorPageDtoValidator.validateResourceDescriptorPageDTO(rdPageDTO);
				if (rdPageResponse.hasErrors()) {
					rdResponse.addErrorMessage("pages", rdPageResponse.errorMessagesString());
					break;
				}
				rdPages.add(rdPageResponse.getEntity());
			}
		} else {
			rd.setResourcePages(null);
		}
		
		rdResponse.setEntity(rd);

		if (rdResponse.hasErrors()) {
			throw new ObjectValidationException(dto, rdResponse.errorMessagesString());
		}

		rd = resourceDescriptorDAO.persist(rd);

		// Attach rd and persist ResourceDescriptorPage objects

		if (CollectionUtils.isNotEmpty(rdPages)) {
			for (ResourceDescriptorPage rdPage : rdPages) {
				rdPage.setResourceDescriptor(rd);
				resourceDescriptorPageDAO.persist(rdPage);
			}
		}
		
		return rd;
	}

	private List<String> parseAliases(String aliasString) {
		if (StringUtils.isBlank(aliasString))
			return null;
		
		ArrayList<String> synonyms = new ArrayList<>(Arrays.asList(aliasString.split(",[ ]*")));
		return synonyms;
	}
}
