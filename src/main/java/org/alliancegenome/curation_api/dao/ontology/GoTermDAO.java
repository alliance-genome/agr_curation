package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;

@ApplicationScoped
public class GoTermDAO extends BaseSQLDAO<GOTerm> {

	protected GoTermDAO() {
		super(GOTerm.class);
	}

}
