package org.alliancegenome.curation_api.dao.ontology;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MiTermDAO extends BaseSQLDAO<MITerm> {

	protected MiTermDAO() {
		super(MITerm.class);
	}

}
