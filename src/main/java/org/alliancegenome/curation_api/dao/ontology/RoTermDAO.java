package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ROTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoTermDAO extends BaseSQLDAO<ROTerm> {

	protected RoTermDAO() {
		super(ROTerm.class);
	}

}
