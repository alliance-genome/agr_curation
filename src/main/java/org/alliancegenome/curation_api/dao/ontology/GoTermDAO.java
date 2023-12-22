package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GoTermDAO extends BaseSQLDAO<GOTerm> {

	protected GoTermDAO() {
		super(GOTerm.class);
	}

}
