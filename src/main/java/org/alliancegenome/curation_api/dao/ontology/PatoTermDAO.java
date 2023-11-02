package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.PATOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PatoTermDAO extends BaseSQLDAO<PATOTerm> {

	protected PatoTermDAO() {
		super(PATOTerm.class);
	}

}