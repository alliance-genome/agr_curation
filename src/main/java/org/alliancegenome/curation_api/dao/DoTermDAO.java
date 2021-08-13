package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

@ApplicationScoped
public class DoTermDAO extends BaseSQLDAO<DOTerm> {

	protected DoTermDAO() {
		super(DOTerm.class);
	}

}
