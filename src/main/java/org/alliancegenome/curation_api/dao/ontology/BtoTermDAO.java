package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.BTOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BtoTermDAO extends BaseSQLDAO<BTOTerm> {

	protected BtoTermDAO() {
		super(BTOTerm.class);
	}

}
