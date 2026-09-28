package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ChromosomeAccession;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChromosomeAccessionDAO extends BaseSQLDAO<ChromosomeAccession> {

	protected ChromosomeAccessionDAO() {
		super(ChromosomeAccession.class);
	}

}
