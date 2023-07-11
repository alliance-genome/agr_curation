package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.VTTerm;

@ApplicationScoped
public class VtTermDAO extends BaseSQLDAO<VTTerm> {

	protected VtTermDAO() {
		super(VTTerm.class);
	}

}
