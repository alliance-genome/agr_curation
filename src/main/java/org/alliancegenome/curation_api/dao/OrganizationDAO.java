package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Organization;

@ApplicationScoped
public class OrganizationDAO extends BaseSQLDAO<Organization> {

	protected OrganizationDAO() {
		super(Organization.class);
	}

}
