package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBATerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class XbaTermDAO extends BaseSQLDAO<XBATerm> {

	protected XbaTermDAO() {
		super(XBATerm.class);
	}

}
