package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.AgmAgmAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgmAgmAssociationDAO extends BaseSQLDAO<AgmAgmAssociation> {

	protected AgmAgmAssociationDAO() {
		super(AgmAgmAssociation.class);
	}

}
