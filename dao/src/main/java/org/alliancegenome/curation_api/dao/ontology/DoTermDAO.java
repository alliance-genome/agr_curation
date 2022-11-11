package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

@ApplicationScoped
public class DoTermDAO extends BaseSQLDAO<DOTerm> {

	protected DoTermDAO() {
		super(DOTerm.class);
	}

}
