package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;

@ApplicationScoped
public class OntologyTermDAO extends BaseSQLDAO<OntologyTerm> {

	protected OntologyTermDAO() {
		super(OntologyTerm.class);
	}
	
}