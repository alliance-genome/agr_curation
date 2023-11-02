package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBPhenotypeTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WbPhenotypeTermDAO extends BaseSQLDAO<WBPhenotypeTerm> {

	protected WbPhenotypeTermDAO() {
		super(WBPhenotypeTerm.class);
	}

}
