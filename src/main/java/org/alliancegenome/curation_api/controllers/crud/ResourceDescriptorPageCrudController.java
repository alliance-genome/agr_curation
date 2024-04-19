package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.interfaces.crud.ResourceDescriptorPageCrudInterface;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ResourceDescriptorPageCrudController extends BaseEntityCrudController<ResourceDescriptorPageService, ResourceDescriptorPage, ResourceDescriptorPageDAO> implements ResourceDescriptorPageCrudInterface {

	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;

	@Override
	@PostConstruct
	protected void init() {
		setService(resourceDescriptorPageService);
	}

	@Override
	public ObjectResponse<ResourceDescriptorPage> getById(Long id) {
		return resourceDescriptorPageService.getById(id);
	}

}
