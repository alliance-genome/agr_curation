package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZFSTerm;

@ApplicationScoped
public class ZfsTermDAO extends BaseSQLDAO<ZFSTerm> {

	protected ZfsTermDAO() {
		super(ZFSTerm.class);
	}

}
