package org.alliancegenome.curation_api.controllers.crud;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.OrganizationDAO;
import org.alliancegenome.curation_api.interfaces.crud.OrganizationCrudInterface;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.services.OrganizationService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class OrganizationCrudController extends BaseEntityCrudController<OrganizationService, Organization, OrganizationDAO> implements OrganizationCrudInterface {

	@Inject
	OrganizationService organizationService;

	@Override
	@PostConstruct
	protected void init() {
		setService(organizationService);
	}

}
