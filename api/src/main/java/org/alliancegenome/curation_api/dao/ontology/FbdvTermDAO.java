package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.FBDVTerm;

@ApplicationScoped
public class FbdvTermDAO extends BaseSQLDAO<FBDVTerm> {

	protected FbdvTermDAO() {
		super(FBDVTerm.class);
	}

}
