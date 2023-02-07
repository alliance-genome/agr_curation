package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class ResourceDescriptorPageService extends BaseEntityCrudService<ResourceDescriptorPage, ResourceDescriptorPageDAO> {

	@Inject
	ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(resourceDescriptorPageDAO);
	}
}
