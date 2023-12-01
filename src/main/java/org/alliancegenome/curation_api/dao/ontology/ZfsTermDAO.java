package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZFSTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ZfsTermDAO extends BaseSQLDAO<ZFSTerm> {

	protected ZfsTermDAO() {
		super(ZFSTerm.class);
	}

}
