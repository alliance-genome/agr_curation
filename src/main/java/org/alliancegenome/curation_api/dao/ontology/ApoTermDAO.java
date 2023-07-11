package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.APOTerm;

@ApplicationScoped
public class ApoTermDAO extends BaseSQLDAO<APOTerm> {

	protected ApoTermDAO() {
		super(APOTerm.class);
	}

}
