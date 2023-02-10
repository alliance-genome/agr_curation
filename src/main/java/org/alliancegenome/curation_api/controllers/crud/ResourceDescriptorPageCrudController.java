package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.interfaces.crud.ResourceDescriptorPageCrudInterface;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;

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
	public ObjectResponse<ResourceDescriptorPage> get(Long id) {
		return resourceDescriptorPageService.get(id);
	}

}
