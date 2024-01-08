package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Organization;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrganizationDAO extends BaseSQLDAO<Organization> {

	protected OrganizationDAO() {
		super(Organization.class);
	}

}
