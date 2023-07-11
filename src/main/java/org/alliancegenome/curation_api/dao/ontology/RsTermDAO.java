package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.RSTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RsTermDAO extends BaseSQLDAO<RSTerm> {

	protected RsTermDAO() {
		super(RSTerm.class);
	}

}
