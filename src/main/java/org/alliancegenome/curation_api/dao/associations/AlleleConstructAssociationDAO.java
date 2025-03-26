package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleConstructAssociationDAO extends BaseSQLDAO<AlleleConstructAssociation> {

	protected AlleleConstructAssociationDAO() {
		super(AlleleConstructAssociation.class);
	}

}
