package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;

import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class OrganizationService extends BaseEntityCrudService<Organization, OrganizationDAO> {

	@Inject
	OrganizationDAO organizationDAO;

	Date orgRequest = null;
	HashMap<String, Organization> orgCacheMap = new HashMap<>();
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(organizationDAO);
	}

	public ObjectResponse<Organization> get(String orgId) {
		
		Organization org = null;
		
		if(orgRequest != null) {
			if(orgCacheMap.containsKey(orgId)) {
				org = orgCacheMap.get(orgId);
			} else {
				Log.debug("Org not cached, caching org: (" + orgId + ")");
				org = organizationDAO.find(Long.parseLong(orgId));
				orgCacheMap.put(orgId, org);
			}
		} else {
			org = organizationDAO.find(Long.parseLong(orgId));
			orgRequest = new Date();
		}

		ObjectResponse<Organization> response = new ObjectResponse<>();
		response.setEntity(org);
		return response;
	}
	
	public ObjectResponse<Organization> getByAbbr(String abbr) {
		
		Organization org = null;
		SearchResponse<Organization> orgResponse = null;
		
		if(orgRequest != null) {
			if(orgCacheMap.containsKey(abbr)) {
				org = orgCacheMap.get(abbr);
			} else {
				Log.debug("Org not cached, caching org: (" + abbr + ")");
				orgResponse = organizationDAO.findByField("abbreviation", abbr);
				if (orgResponse != null)
					org = orgResponse.getSingleResult();
				orgCacheMap.put(abbr, org);
			}
		} else {
			orgResponse = organizationDAO.findByField("abbreviation", abbr);
			if (orgResponse != null)
				org = orgResponse.getSingleResult();
			orgRequest = new Date();
		}

		ObjectResponse<Organization> response = new ObjectResponse<>();
		response.setEntity(org);
		return response;
	}
	
}
