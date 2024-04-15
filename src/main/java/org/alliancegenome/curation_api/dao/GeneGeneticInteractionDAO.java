package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneGeneticInteractionDAO extends BaseSQLDAO<GeneGeneticInteraction> {

	protected GeneGeneticInteractionDAO() {
		super(GeneGeneticInteraction.class);
	}

}
