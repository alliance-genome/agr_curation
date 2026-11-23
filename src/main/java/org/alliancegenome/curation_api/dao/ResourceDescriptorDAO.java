package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ResourceDescriptorDAO extends BaseSQLDAO<ResourceDescriptor> {

	protected ResourceDescriptorDAO() {
		super(ResourceDescriptor.class);
	}

}