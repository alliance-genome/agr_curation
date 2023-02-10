package org.alliancegenome.curation_api.services;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorDTO;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.ResourceDescriptorDTOValidator;
import org.apache.commons.collections4.ListUtils;

@RequestScoped
public class ResourceDescriptorService extends BaseEntityCrudService<ResourceDescriptor, ResourceDescriptorDAO> {

	@Inject
	ResourceDescriptorDAO resourceDescriptorDAO;
	@Inject
	ResourceDescriptorDTOValidator resourceDescriptorDtoValidator;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(resourceDescriptorDAO);
	}
	
	public List<String> getAllNames() {
		List<String> names = resourceDescriptorDAO.findAllNames();
		names.removeIf(Objects::isNull);
		return names;
	}

	@Transactional
	public ResourceDescriptor upsert(ResourceDescriptorDTO dto) throws ObjectUpdateException {
		ResourceDescriptor rd = resourceDescriptorDtoValidator.validateResourceDescriptorDTO(dto);
		
		if (rd == null)
			return null;
		
		return resourceDescriptorDAO.persist(rd);
	}

	@Transactional
	public void removeNonUpdatedResourceDescriptors(List<String> rdNamesBefore, List<String> rdNamesAfter) {
		List<String> rdNamesToRemove = ListUtils.subtract(rdNamesBefore, rdNamesAfter);
		for (String rdName : rdNamesToRemove) {
			SearchResponse<ResourceDescriptor> rdResponse = resourceDescriptorDAO.findByField("name", rdName);
			if (rdResponse != null && rdResponse.getSingleResult() != null) {
				resourceDescriptorDAO.remove(rdResponse.getSingleResult().getId());
			}
		}
		
	}

}
