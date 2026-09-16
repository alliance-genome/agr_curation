package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;

import jakarta.enterprise.context.ApplicationScoped;

/** SCRUM-6535. */
@ApplicationScoped
public class ConstructCassetteAssociationDAO extends BaseSQLDAO<ConstructCassetteAssociation> {

	protected ConstructCassetteAssociationDAO() {
		super(ConstructCassetteAssociation.class);
	}

}
