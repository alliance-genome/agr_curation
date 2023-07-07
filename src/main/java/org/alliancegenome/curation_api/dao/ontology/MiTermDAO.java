package org.alliancegenome.curation_api.dao.ontology;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;

@ApplicationScoped
public class MiTermDAO extends BaseSQLDAO<MITerm> {

	protected MiTermDAO() {
		super(MITerm.class);
	}

}
