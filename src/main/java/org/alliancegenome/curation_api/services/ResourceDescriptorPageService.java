package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ResourceDescriptorPageService extends BaseEntityCrudService<ResourceDescriptorPage, ResourceDescriptorPageDAO> {

	@Inject
	ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	@Inject
	ResourceDescriptorService resourceDescriptorService;
	
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
		
		if (page != null && page.getResourceDescriptor() != null && page.getResourceDescriptor().getSynonyms() != null)
			page.getResourceDescriptor().getSynonyms().size();
				
		return page;

	}
	
	
	private ResourceDescriptorPage getPageForResourceDescriptorFromDB(String resourceDescriptorPrefix, String pageName) {
		
		ObjectResponse<ResourceDescriptor> rdResponse = resourceDescriptorService.getByPrefixOrSynonym(resourceDescriptorPrefix);
		if (rdResponse == null || rdResponse.getEntity() == null)
			return null;
		
		for (ResourceDescriptorPage page : rdResponse.getEntity().getResourcePages()) {
			if (Objects.equals(page.getName(), pageName))
				return page;
		}
		
		return null;
	}
	
}
