package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.CassetteAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CassetteAssociationDAO extends BaseSQLDAO<CassetteAssociation> {

	protected CassetteAssociationDAO() {
		super(CassetteAssociation.class);
	}

}
