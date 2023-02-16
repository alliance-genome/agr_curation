package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.SearchResponse;

@ApplicationScoped
public class ResourceDescriptorPageDAO extends BaseSQLDAO<ResourceDescriptorPage> {

	protected ResourceDescriptorPageDAO() {
		super(ResourceDescriptorPage.class);
	}
	
	public ResourceDescriptorPage getPageForResourceDescriptor(String resourceDescriptorPrefix, String pageName) {

		Map<String, Object> params = new HashMap<>();
		params.put("name", pageName);
		params.put("resourceDescriptor.prefix", resourceDescriptorPrefix);

		SearchResponse<ResourceDescriptorPage> resp = findByParams(null, params);
		return resp.getSingleResult();

	}

}