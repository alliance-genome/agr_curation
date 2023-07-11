package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MODTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ModTermDAO extends BaseSQLDAO<MODTerm> {

	protected ModTermDAO() {
		super(MODTerm.class);
	}

}
