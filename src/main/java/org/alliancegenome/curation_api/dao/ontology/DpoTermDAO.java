package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DPOTerm;

@ApplicationScoped
public class DpoTermDAO extends BaseSQLDAO<DPOTerm> {

	protected DpoTermDAO() {
		super(DPOTerm.class);
	}

}
