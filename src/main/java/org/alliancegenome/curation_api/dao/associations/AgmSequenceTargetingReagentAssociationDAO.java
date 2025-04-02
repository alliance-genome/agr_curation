package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.AgmSequenceTargetingReagentAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgmSequenceTargetingReagentAssociationDAO extends BaseSQLDAO<AgmSequenceTargetingReagentAssociation> {

	protected AgmSequenceTargetingReagentAssociationDAO() {
		super(AgmSequenceTargetingReagentAssociation.class);
	}

}
