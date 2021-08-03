package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.*;

public class MpTermDAO extends BaseSQLDAO<MPTerm> {

	protected MpTermDAO() {
		super(MPTerm.class);
	}

}
