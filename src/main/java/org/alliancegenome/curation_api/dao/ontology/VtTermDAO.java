package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.VTTerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VtTermDAO extends BaseSQLDAO<VTTerm> {

	protected VtTermDAO() {
		super(VTTerm.class);
	}

}
