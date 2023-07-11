package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OBITerm;

@ApplicationScoped
public class ObiTermDAO extends BaseSQLDAO<OBITerm> {

	protected ObiTermDAO() {
		super(OBITerm.class);
	}

}
