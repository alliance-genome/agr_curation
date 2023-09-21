package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ResourceDescriptorPageService extends BaseEntityCrudService<ResourceDescriptorPage, ResourceDescriptorPageDAO> {

	@Inject
	ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	
	HashMap<String, Date> resourceRequestMap = new HashMap<>();
	HashMap<String, HashMap<String, ResourceDescriptorPage>> resourcePageCacheMap = new HashMap<>();
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(resourceDescriptorPageDAO);
	}

	public ResourceDescriptorPage getPageForResourceDescriptor(String resourceDescriptorPrefix, String pageName) {

		ResourceDescriptorPage page = null;
		
		if(resourceRequestMap.get(resourceDescriptorPrefix) != null) {
			
			HashMap<String, ResourceDescriptorPage> pageMap = resourcePageCacheMap.get(resourceDescriptorPrefix);
			
			if(pageMap == null) {
				Log.debug("Vocab not cached, caching vocab: " + resourceDescriptorPrefix);
				pageMap = new HashMap<>();
				resourcePageCacheMap.put(resourceDescriptorPrefix, pageMap);
			}

			if(pageMap.containsKey(pageName)) {
				page = pageMap.get(pageName);
			} else {
				Log.debug("page not cached, caching page: " + resourceDescriptorPrefix + "(" + pageName + ")");
				page = getPageForResourceDescriptorFromDB(resourceDescriptorPrefix, pageName);
				pageMap.put(pageName, page);
			}
			
		} else {
			page = getPageForResourceDescriptorFromDB(resourceDescriptorPrefix, pageName);
			resourceRequestMap.put(resourceDescriptorPrefix, new Date());
		}
		
		return page;

	}
	
	
	private ResourceDescriptorPage getPageForResourceDescriptorFromDB(String resourceDescriptorPrefix, String pageName) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", pageName);
		params.put("resourceDescriptor.prefix", resourceDescriptorPrefix);

		SearchResponse<ResourceDescriptorPage> resp = resourceDescriptorPageDAO.findByParams(params);
		return resp.getSingleResult();
	}
	
}
