package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBbtTerm;

@ApplicationScoped
public class WbbtTermDAO extends BaseSQLDAO<WBbtTerm> {

	protected WbbtTermDAO() {
		super(WBbtTerm.class);
	}

}
