package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBBTTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WbbtTermDAO extends BaseSQLDAO<WBBTTerm> {

	protected WbbtTermDAO() {
		super(WBBTTerm.class);
	}

}
