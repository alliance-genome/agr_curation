package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ATPTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AtpTermDAO extends BaseSQLDAO<ATPTerm> {

	protected AtpTermDAO() {
		super(ATPTerm.class);
	}

}
