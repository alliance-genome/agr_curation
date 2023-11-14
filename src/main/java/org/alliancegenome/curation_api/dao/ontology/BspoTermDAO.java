package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.BSPOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BspoTermDAO extends BaseSQLDAO<BSPOTerm> {

	protected BspoTermDAO() {
		super(BSPOTerm.class);
	}

}