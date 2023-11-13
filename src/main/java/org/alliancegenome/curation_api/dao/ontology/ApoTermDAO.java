package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.APOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApoTermDAO extends BaseSQLDAO<APOTerm> {

	protected ApoTermDAO() {
		super(APOTerm.class);
	}

}
