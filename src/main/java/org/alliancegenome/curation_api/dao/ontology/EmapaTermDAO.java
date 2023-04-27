package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.EMAPATerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmapaTermDAO extends BaseSQLDAO<EMAPATerm> {

	protected EmapaTermDAO() {
		super(EMAPATerm.class);
	}

}
