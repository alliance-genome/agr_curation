package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.HPTerm;

@ApplicationScoped
public class HpTermDAO extends BaseSQLDAO<HPTerm> {

	protected HpTermDAO() {
		super(HPTerm.class);
	}

}
