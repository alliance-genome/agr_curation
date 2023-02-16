package org.alliancegenome.curation_api.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.response.SearchResponse;

@ApplicationScoped
public class ResourceDescriptorDAO extends BaseSQLDAO<ResourceDescriptor> {

	protected ResourceDescriptorDAO() {
		super(ResourceDescriptor.class);
	}
	
	public List<String> findAllNames() {
		SearchResponse<ResourceDescriptor> response = findAll(null);
		List<ResourceDescriptor> resourceDescriptors = response.getResults();
		List<String> resourceDescriptorNames = resourceDescriptors.stream().map(ResourceDescriptor::getName).collect(Collectors.toList());
		
		return resourceDescriptorNames;
	}
	
}