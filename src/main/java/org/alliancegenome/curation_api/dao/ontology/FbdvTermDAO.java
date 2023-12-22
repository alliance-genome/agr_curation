package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.FBDVTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FbdvTermDAO extends BaseSQLDAO<FBDVTerm> {

	protected FbdvTermDAO() {
		super(FBDVTerm.class);
	}

}
