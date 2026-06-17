package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import org.alliancegenome.curation_api.dao.ResourceDescriptorPageDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.ResourceDescriptorPageValidator;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ResourceDescriptorPageService extends BaseEntityCrudService<ResourceDescriptorPage, ResourceDescriptorPageDAO> {

	@Inject ResourceDescriptorPageDAO resourceDescriptorPageDAO;
	@Inject ResourceDescriptorService resourceDescriptorService;
	@Inject ResourceDescriptorPageValidator resourceDescriptorPageValidator;

	HashMap<String, Date> resourceRequestMap = new HashMap<>();
	HashMap<String, HashMap<String, ResourceDescriptorPage>> resourcePageCacheMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(resourceDescriptorPageDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<ResourceDescriptorPage> update(ResourceDescriptorPage uiEntity) {
		ResourceDescriptorPage dbEntity = resourceDescriptorPageValidator.validateResourceDescriptorPageUpdate(uiEntity);
		return new ObjectResponse<>(resourceDescriptorPageDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<ResourceDescriptorPage> create(ResourceDescriptorPage uiEntity) {
		ResourceDescriptorPage dbEntity = resourceDescriptorPageValidator.validateResourceDescriptorPageCreate(uiEntity);
		return new ObjectResponse<>(resourceDescriptorPageDAO.persist(dbEntity));
	}

	/**
	 * Resolve the ResourceDescriptorPage for a reference-style curie ({@code PREFIX:localId}):
	 * tries {@code (prefix, "reference")} first, then falls back to {@code (prefix, "default")}.
	 * Returns null for malformed curies (null, missing colon, empty prefix) and for prefixes
	 * with no matching resource descriptor. Use this anywhere a CrossReference is constructed
	 * from a curie and we want the persisted resourceDescriptorPage link.
	 */
	public ResourceDescriptorPage resolvePageForReferenceCurie(String curie) {
		if (curie == null) {
			return null;
		}
		int colon = curie.indexOf(':');
		if (colon <= 0) {
			// no colon, or empty prefix (curie starts with ':')
			return null;
		}
		String prefix = curie.substring(0, colon);
		ResourceDescriptorPage page = getPageForResourceDescriptor(prefix, "reference");
		if (page == null) {
			page = getPageForResourceDescriptor(prefix, "default");
		}
		return page;
	}

	public ResourceDescriptorPage getPageForResourceDescriptor(String resourceDescriptorPrefix, String pageName) {

		ResourceDescriptorPage page = null;

		if (resourceRequestMap.get(resourceDescriptorPrefix) != null) {

			HashMap<String, ResourceDescriptorPage> pageMap = resourcePageCacheMap.get(resourceDescriptorPrefix);

			if (pageMap == null) {
				Log.debug("Vocab not cached, caching vocab: " + resourceDescriptorPrefix);
				pageMap = new HashMap<>();
				resourcePageCacheMap.put(resourceDescriptorPrefix, pageMap);
			}

			if (pageMap.containsKey(pageName)) {
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

		if (page != null && page.getResourceDescriptor() != null && page.getResourceDescriptor().getSynonyms() != null) {
			page.getResourceDescriptor().getSynonyms().size();
		}

		return page;

	}

	private ResourceDescriptorPage getPageForResourceDescriptorFromDB(String resourceDescriptorPrefix, String pageName) {

		ObjectResponse<ResourceDescriptor> rdResponse = resourceDescriptorService.getByPrefixOrSynonym(resourceDescriptorPrefix);
		if (rdResponse == null || rdResponse.getEntity() == null || CollectionUtils.isEmpty(rdResponse.getEntity().getResourcePages())) {
			return null;
		}

		for (ResourceDescriptorPage page : rdResponse.getEntity().getResourcePages()) {
			if (Objects.equals(page.getName(), pageName)) {
				return page;
			}
		}

		return null;
	}

}
