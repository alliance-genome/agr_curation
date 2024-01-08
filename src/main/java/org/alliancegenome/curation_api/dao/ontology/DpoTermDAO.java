package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DPOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DpoTermDAO extends BaseSQLDAO<DPOTerm> {

	protected DpoTermDAO() {
		super(DPOTerm.class);
	}

}
