package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SoTermDAO extends BaseSQLDAO<SOTerm> {

	protected SoTermDAO() {
		super(SOTerm.class);
	}

}
