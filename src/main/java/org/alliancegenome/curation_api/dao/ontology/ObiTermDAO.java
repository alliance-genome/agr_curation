package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OBITerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ObiTermDAO extends BaseSQLDAO<OBITerm> {

	protected ObiTermDAO() {
		super(OBITerm.class);
	}

}
