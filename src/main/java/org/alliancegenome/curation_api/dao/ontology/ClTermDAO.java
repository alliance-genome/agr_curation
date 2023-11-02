package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CLTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClTermDAO extends BaseSQLDAO<CLTerm> {

	protected ClTermDAO() {
		super(CLTerm.class);
	}

}