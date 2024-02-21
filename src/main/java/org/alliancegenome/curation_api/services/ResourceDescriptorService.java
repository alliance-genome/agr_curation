package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.ingest.dto.ResourceDescriptorDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.ResourceDescriptorDTOValidator;
import org.apache.commons.collections4.ListUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ResourceDescriptorService extends BaseEntityCrudService<ResourceDescriptor, ResourceDescriptorDAO> {

	@Inject
	ResourceDescriptorDAO resourceDescriptorDAO;
	@Inject
	ResourceDescriptorDTOValidator resourceDescriptorDtoValidator;
	
	Date prefixRequest = null;
	HashMap<String, ResourceDescriptor> prefixCacheMap = new HashMap<>();
	
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

	
	public ObjectResponse<ResourceDescriptor> getByPrefix(String prefix) {
		
		ResourceDescriptor rd = null;
		SearchResponse<ResourceDescriptor> rdResponse = null;
		if(prefixRequest != null) {
			if(prefixCacheMap.containsKey(prefix)) {
				rd = prefixCacheMap.get(prefix);
			} else {
				Log.debug("RD not cached, caching rd: (" + prefix + ")");
				rdResponse = resourceDescriptorDAO.findByField("prefix", prefix);
				if (rdResponse != null && rdResponse.getSingleResult() != null) {
					rd = rdResponse.getSingleResult();
					prefixCacheMap.put(prefix, rd);
				}
			}
		} else {
			rdResponse = resourceDescriptorDAO.findByField("prefix", prefix);
			if (rdResponse != null && rdResponse.getSingleResult() != null)
				rd = rdResponse.getSingleResult();
			prefixRequest = new Date();
		}

		ObjectResponse<ResourceDescriptor> response = new ObjectResponse<>();
		response.setEntity(rd);
		return response;
	}

}
