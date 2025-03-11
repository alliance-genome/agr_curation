package org.alliancegenome.curation_api.dao.associations.alleleAssociations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleConstructAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlleleConstructAssociationDAO extends BaseSQLDAO<AlleleConstructAssociation> {

	protected AlleleConstructAssociationDAO() {
		super(AlleleConstructAssociation.class);
	}

}
