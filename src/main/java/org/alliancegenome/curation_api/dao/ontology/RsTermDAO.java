package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.RSTerm;

@ApplicationScoped
public class RsTermDAO extends BaseSQLDAO<RSTerm> {

	protected RsTermDAO() {
		super(RSTerm.class);
	}

}
