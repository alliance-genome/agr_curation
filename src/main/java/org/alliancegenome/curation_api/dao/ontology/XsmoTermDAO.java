package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XSMOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class XsmoTermDAO extends BaseSQLDAO<XSMOTerm> {

	protected XsmoTermDAO() {
		super(XSMOTerm.class);
	}

}
