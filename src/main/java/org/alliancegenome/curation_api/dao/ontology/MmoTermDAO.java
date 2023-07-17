package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;

@ApplicationScoped
public class MmoTermDAO extends BaseSQLDAO<MMOTerm> {

	protected MmoTermDAO() {
		super(MMOTerm.class);
	}

}
