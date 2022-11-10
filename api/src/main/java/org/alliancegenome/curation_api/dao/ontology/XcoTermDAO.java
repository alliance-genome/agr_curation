package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XCOTerm;

@ApplicationScoped
public class XcoTermDAO extends BaseSQLDAO<XCOTerm> {

	protected XcoTermDAO() {
		super(XCOTerm.class);
	}

}
