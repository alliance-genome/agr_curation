package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeneMolecularInteractionDAO extends BaseSQLDAO<GeneMolecularInteraction> {

	protected GeneMolecularInteractionDAO() {
		super(GeneMolecularInteraction.class);
	}

}
