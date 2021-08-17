package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReferenceDAO extends BaseSQLDAO<Reference> {

	protected ReferenceDAO() {
		super(Reference.class);
	}

}
