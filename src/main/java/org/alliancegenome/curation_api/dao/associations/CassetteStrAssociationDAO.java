package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.CassetteStrAssociation;

import jakarta.enterprise.context.ApplicationScoped;

/** SCRUM-6535. */
@ApplicationScoped
public class CassetteStrAssociationDAO extends BaseSQLDAO<CassetteStrAssociation> {

	protected CassetteStrAssociationDAO() {
		super(CassetteStrAssociation.class);
	}

}
