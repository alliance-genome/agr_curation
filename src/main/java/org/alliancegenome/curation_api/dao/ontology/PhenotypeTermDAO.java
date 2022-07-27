package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;

@ApplicationScoped
public class PhenotypeTermDAO extends BaseSQLDAO<PhenotypeTerm> {

	protected PhenotypeTermDAO() {
		super(PhenotypeTerm.class);
	}
	
}