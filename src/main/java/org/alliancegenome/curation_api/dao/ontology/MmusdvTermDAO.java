package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MMUSDVTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MmusdvTermDAO extends BaseSQLDAO<MMUSDVTerm> {

	protected MmusdvTermDAO() {
		super(MMUSDVTerm.class);
	}

}
