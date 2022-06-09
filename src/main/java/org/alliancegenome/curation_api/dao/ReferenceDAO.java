package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Reference;

@ApplicationScoped
public class ReferenceDAO extends BaseSQLDAO<Reference> {

	protected ReferenceDAO() {
		super(Reference.class);
	}

}
