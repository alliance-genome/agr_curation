package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OntologyTermDAO extends BaseSQLDAO<OntologyTerm> {

	protected OntologyTermDAO() {
		super(OntologyTerm.class);
	}

}