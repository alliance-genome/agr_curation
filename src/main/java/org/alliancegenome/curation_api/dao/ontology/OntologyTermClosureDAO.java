package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OntologyTermClosureDAO extends BaseSQLDAO<OntologyTermClosure> {
	
	protected OntologyTermClosureDAO() {
		super(OntologyTermClosure.class);
	}
	
}