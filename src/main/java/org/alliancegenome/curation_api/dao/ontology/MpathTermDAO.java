package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MPATHTerm;

@ApplicationScoped
public class MpathTermDAO extends BaseSQLDAO<MPATHTerm> {

	protected MpathTermDAO() {
		super(MPATHTerm.class);
	}

}
