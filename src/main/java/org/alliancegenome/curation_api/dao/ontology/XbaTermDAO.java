package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBATerm;

@ApplicationScoped
public class XbaTermDAO extends BaseSQLDAO<XBATerm> {

	protected XbaTermDAO() {
		super(XBATerm.class);
	}

}
