package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.PWTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PwTermDAO extends BaseSQLDAO<PWTerm> {

	protected PwTermDAO() {
		super(PWTerm.class);
	}

}
