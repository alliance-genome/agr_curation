package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MaTermDAO extends BaseSQLDAO<MATerm> {

	protected MaTermDAO() {
		super(MATerm.class);
	}

}
