package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ATPTerm;

@ApplicationScoped
public class AtpTermDAO extends BaseSQLDAO<ATPTerm> {

	protected AtpTermDAO() {
		super(ATPTerm.class);
	}

}
