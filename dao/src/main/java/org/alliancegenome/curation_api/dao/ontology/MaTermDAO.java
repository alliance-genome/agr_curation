package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;

@ApplicationScoped
public class MaTermDAO extends BaseSQLDAO<MATerm> {

	protected MaTermDAO() {
		super(MATerm.class);
	}

}
