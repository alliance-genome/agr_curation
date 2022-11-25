package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

@RequestScoped
public class OrganizationService extends BaseEntityCrudService<Organization, OrganizationDAO> {

	@Inject OrganizationDAO organizationDAO;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(organizationDAO);
	}
	
}
