package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Species;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SpeciesDAO extends BaseSQLDAO<Species> {

	protected SpeciesDAO() {
		super(Species.class);
	}
}
