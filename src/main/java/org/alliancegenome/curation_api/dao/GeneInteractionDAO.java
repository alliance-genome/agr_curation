package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneInteraction;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneInteractionDAO extends BaseSQLDAO<GeneInteraction> {

	protected GeneInteractionDAO() {
		super(GeneInteraction.class);
	}

}
