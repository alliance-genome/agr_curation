package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;

import jakarta.enterprise.context.ApplicationScoped;

/** SCRUM-6543. */
@ApplicationScoped
public class TransgenicToolTransgenicToolAssociationDAO extends BaseSQLDAO<TransgenicToolTransgenicToolAssociation> {

	protected TransgenicToolTransgenicToolAssociationDAO() {
		super(TransgenicToolTransgenicToolAssociation.class);
	}

}
