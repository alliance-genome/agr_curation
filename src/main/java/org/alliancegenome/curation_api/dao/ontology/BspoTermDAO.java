package org.alliancegenome.curation_api.dao.ontology;

import jakarta.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.BSPOTerm;

@ApplicationScoped
public class BspoTermDAO extends BaseSQLDAO<BSPOTerm> {

	protected BspoTermDAO() {
		super(BSPOTerm.class);
	}

}