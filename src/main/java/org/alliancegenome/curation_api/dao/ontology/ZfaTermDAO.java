package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZFATerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ZfaTermDAO extends BaseSQLDAO<ZFATerm> {

	protected ZfaTermDAO() {
		super(ZFATerm.class);
	}

}
