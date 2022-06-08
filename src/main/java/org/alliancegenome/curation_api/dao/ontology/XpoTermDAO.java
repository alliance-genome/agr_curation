package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XPOTerm;

@ApplicationScoped
public class XpoTermDAO extends BaseSQLDAO<XPOTerm> {

	protected XpoTermDAO() {
		super(XPOTerm.class);
	}

}
