package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ROTerm;

@ApplicationScoped
public class RoTermDAO extends BaseSQLDAO<ROTerm> {

	protected RoTermDAO() {
		super(ROTerm.class);
	}

}
