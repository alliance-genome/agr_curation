package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XPOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class XpoTermDAO extends BaseSQLDAO<XPOTerm> {

	protected XpoTermDAO() {
		super(XPOTerm.class);
	}

}
