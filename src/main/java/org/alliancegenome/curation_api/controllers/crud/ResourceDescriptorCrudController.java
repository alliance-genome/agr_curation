package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.interfaces.crud.ResourceDescriptorCrudInterface;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ResourceDescriptorService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ResourceDescriptorCrudController extends BaseEntityCrudController<ResourceDescriptorService, ResourceDescriptor, ResourceDescriptorDAO> implements ResourceDescriptorCrudInterface {

	@Inject
	ResourceDescriptorService resourceDescriptorService;

	@Override
	@PostConstruct
	protected void init() {
		setService(resourceDescriptorService);
	}

	@Override
	public ObjectResponse<ResourceDescriptor> get(Long id) {
		return resourceDescriptorService.get(id);
	}

}
