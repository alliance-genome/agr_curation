package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Construct;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConstructDAO extends BaseSQLDAO<Construct> {
	
	@Inject
	NoteDAO noteDAO;

	protected ConstructDAO() {
		super(Construct.class);
	}

}
