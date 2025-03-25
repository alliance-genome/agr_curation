package org.alliancegenome.curation_api.dao.associations;

import jakarta.enterprise.context.ApplicationScoped;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.AgmAgmAssociation;

@ApplicationScoped
public class AgmAgmAssociationDAO extends BaseSQLDAO<AgmAgmAssociation> {

	protected AgmAgmAssociationDAO() {
		super(AgmAgmAssociation.class);
	}

}
