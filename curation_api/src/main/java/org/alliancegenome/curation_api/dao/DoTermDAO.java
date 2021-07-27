package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

public class DoTermDAO extends BaseSQLDAO<DOTerm> {

	protected DoTermDAO() {
		super(DOTerm.class);
	}

}
