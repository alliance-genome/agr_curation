package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MPATHTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MpathTermDAO extends BaseSQLDAO<MPATHTerm> {

	protected MpathTermDAO() {
		super(MPATHTerm.class);
	}

}
