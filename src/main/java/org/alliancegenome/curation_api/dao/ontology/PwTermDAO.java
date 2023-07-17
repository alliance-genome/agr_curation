package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.PWTerm;

@ApplicationScoped
public class PwTermDAO extends BaseSQLDAO<PWTerm> {

	protected PwTermDAO() {
		super(PWTerm.class);
	}

}
