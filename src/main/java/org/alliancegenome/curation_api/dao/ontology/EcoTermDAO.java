package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;

@ApplicationScoped
public class EcoTermDAO extends BaseSQLDAO<ECOTerm> {
	
	protected EcoTermDAO() {
		super(ECOTerm.class);
	}
	
}