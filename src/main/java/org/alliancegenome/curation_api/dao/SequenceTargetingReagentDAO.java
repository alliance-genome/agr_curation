package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SequenceTargetingReagentDAO extends BaseSQLDAO<SequenceTargetingReagent> {

	protected SequenceTargetingReagentDAO() {
		super(SequenceTargetingReagent.class);
	}

}
