package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EcoTermDAO extends BaseSQLDAO<ECOTerm> {

	protected EcoTermDAO() {
		super(ECOTerm.class);
	}

}