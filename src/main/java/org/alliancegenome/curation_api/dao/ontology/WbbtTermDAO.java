package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBBTTerm;

@ApplicationScoped
public class WbbtTermDAO extends BaseSQLDAO<WBBTTerm> {

	protected WbbtTermDAO() {
		super(WBBTTerm.class);
	}

}
