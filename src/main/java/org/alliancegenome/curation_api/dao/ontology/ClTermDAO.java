package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CLTerm;

@ApplicationScoped
public class ClTermDAO extends BaseSQLDAO<CLTerm> {

	protected ClTermDAO() {
		super(CLTerm.class);
	}

}