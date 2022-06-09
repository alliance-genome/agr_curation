package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBlsTerm;

@ApplicationScoped
public class WblsTermDAO extends BaseSQLDAO<WBlsTerm> {

	protected WblsTermDAO() {
		super(WBlsTerm.class);
	}

}
