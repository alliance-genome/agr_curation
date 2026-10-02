package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.CassetteTransgenicToolAssociation;

import jakarta.enterprise.context.ApplicationScoped;

/** SCRUM-6535. */
@ApplicationScoped
public class CassetteTransgenicToolAssociationDAO extends BaseSQLDAO<CassetteTransgenicToolAssociation> {

	protected CassetteTransgenicToolAssociationDAO() {
		super(CassetteTransgenicToolAssociation.class);
	}

}
