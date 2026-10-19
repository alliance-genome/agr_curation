package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.dao.ResourceDescriptorDAO;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.ResourceDescriptorValidator;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ResourceDescriptorService extends BaseEntityCrudService<ResourceDescriptor, ResourceDescriptorDAO> {

	@Inject ResourceDescriptorDAO resourceDescriptorDAO;
	@Inject ResourceDescriptorValidator resourceDescriptorValidator;

	Date prefixRequest;
	HashMap<String, ResourceDescriptor> prefixCacheMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(resourceDescriptorDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<ResourceDescriptor> update(ResourceDescriptor uiEntity) {
		ResourceDescriptor dbEntity = resourceDescriptorValidator.validateResourceDescriptorUpdate(uiEntity);
		return new ObjectResponse<>(resourceDescriptorDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<ResourceDescriptor> create(ResourceDescriptor uiEntity) {
		ResourceDescriptor dbEntity = resourceDescriptorValidator.validateResourceDescriptorCreate(uiEntity);
		return new ObjectResponse<>(resourceDescriptorDAO.persist(dbEntity));
	}

	public ObjectResponse<ResourceDescriptor> getByPrefixOrSynonym(String prefix) {

		List<String> lookupFields = List.of("prefix", "synonyms");

		ResourceDescriptor rd = null;
		if (prefixRequest != null) {
			if (prefixCacheMap.containsKey(prefix)) {
				rd = prefixCacheMap.get(prefix);
			} else {
				Log.debug("RD not cached, caching rd: (" + prefix + ")");
				rd = findByAlternativeFields(lookupFields, prefix);
				if (rd != null) {
					prefixCacheMap.put(prefix, rd);
				}
			}
		} else {
			rd = findByAlternativeFields(lookupFields, prefix);
			prefixRequest = new Date();
		}

		ObjectResponse<ResourceDescriptor> response = new ObjectResponse<>();
		response.setEntity(rd);
		return response;
	}

}
