package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Species;

@ApplicationScoped
public class SpeciesDAO extends BaseSQLDAO<Species> {

	protected SpeciesDAO() {
		super(Species.class);
	}
}
