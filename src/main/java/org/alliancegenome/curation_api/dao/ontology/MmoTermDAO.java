package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MmoTermDAO extends BaseSQLDAO<MMOTerm> {

	protected MmoTermDAO() {
		super(MMOTerm.class);
	}

}
