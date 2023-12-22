package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBSTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class XbsTermDAO extends BaseSQLDAO<XBSTerm> {

	protected XbsTermDAO() {
		super(XBSTerm.class);
	}

}
