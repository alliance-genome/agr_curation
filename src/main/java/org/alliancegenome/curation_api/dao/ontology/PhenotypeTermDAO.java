package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PhenotypeTermDAO extends BaseSQLDAO<PhenotypeTerm> {

	protected PhenotypeTermDAO() {
		super(PhenotypeTerm.class);
	}

}