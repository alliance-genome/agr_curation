package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CMOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CmoTermDAO extends BaseSQLDAO<CMOTerm> {

	protected CmoTermDAO() {
		super(CMOTerm.class);
	}

}