package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBLSTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WblsTermDAO extends BaseSQLDAO<WBLSTerm> {

	protected WblsTermDAO() {
		super(WBLSTerm.class);
	}

}
