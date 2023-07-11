package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ResourceDescriptorPageDAO extends BaseSQLDAO<ResourceDescriptorPage> {

	protected ResourceDescriptorPageDAO() {
		super(ResourceDescriptorPage.class);
	}

}