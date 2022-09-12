package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZFATerm;

@ApplicationScoped
public class ZfaTermDAO extends BaseSQLDAO<ZFATerm> {

	protected ZfaTermDAO() {
		super(ZFATerm.class);
	}

}
