package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MMUSDVTerm;

@ApplicationScoped
public class MmusdvTermDAO extends BaseSQLDAO<MMUSDVTerm> {

	protected MmusdvTermDAO() {
		super(MMUSDVTerm.class);
	}

}
