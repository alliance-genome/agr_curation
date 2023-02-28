package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.WBPhenotypeTerm;

@ApplicationScoped
public class WbphenotypeTermDAO extends BaseSQLDAO<WBPhenotypeTerm> {

	protected WbphenotypeTermDAO() {
		super(WBPhenotypeTerm.class);
	}

}
