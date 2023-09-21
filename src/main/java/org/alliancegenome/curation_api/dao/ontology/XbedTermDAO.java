package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.XBEDTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class XbedTermDAO extends BaseSQLDAO<XBEDTerm> {

	protected XbedTermDAO() {
		super(XBEDTerm.class);
	}

}
