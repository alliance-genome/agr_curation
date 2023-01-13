package org.alliancegenome.curation_api.services;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class ResourceDescriptorService extends BaseEntityCrudService<ResourceDescriptor, ResourceDescriptorDAO> {

	@Inject
	ResourceDescriptorDAO resourceDescriptorDAO;
	
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

}
