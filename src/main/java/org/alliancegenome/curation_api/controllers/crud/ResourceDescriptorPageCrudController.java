package org.alliancegenome.curation_api.controllers.crud;

import java.util.HashMap;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.interfaces.crud.ResourceDescriptorPageCrudInterface;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
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

	@Override
	public ObjectResponse<ResourceDescriptorPage> create(ResourceDescriptorPage entity) {
		return resourceDescriptorPageService.create(entity);
	}

	@Override
	public ObjectResponse<ResourceDescriptorPage> update(ResourceDescriptorPage entity) {
		return resourceDescriptorPageService.update(entity);
	}

	@Override
	public SearchResponse<ResourceDescriptorPage> findAllForPublic(Integer page, Integer limit, HashMap<String, Object> params) {
		return find(page, limit, params);
	}

}
