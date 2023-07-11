package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XCOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class XcoTermDAO extends BaseSQLDAO<XCOTerm> {

	protected XcoTermDAO() {
		super(XCOTerm.class);
	}

}
