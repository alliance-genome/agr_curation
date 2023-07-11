package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ZecoTermDAO extends BaseSQLDAO<ZECOTerm> {

	protected ZecoTermDAO() {
		super(ZECOTerm.class);
	}

}
