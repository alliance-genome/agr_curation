package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;

@ApplicationScoped
public class ResourceDescriptorPageDAO extends BaseSQLDAO<ResourceDescriptorPage> {

	protected ResourceDescriptorPageDAO() {
		super(ResourceDescriptorPage.class);
	}

}