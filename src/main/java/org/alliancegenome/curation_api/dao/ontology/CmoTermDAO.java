package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.CMOTerm;

@ApplicationScoped
public class CmoTermDAO extends BaseSQLDAO<CMOTerm> {

	protected CmoTermDAO() {
		super(CMOTerm.class);
	}

}