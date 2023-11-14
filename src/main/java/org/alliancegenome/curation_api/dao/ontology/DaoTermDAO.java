package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DAOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DaoTermDAO extends BaseSQLDAO<DAOTerm> {

	protected DaoTermDAO() {
		super(DAOTerm.class);
	}

}
