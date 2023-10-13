package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Construct;

@ApplicationScoped
public class ConstructDAO extends BaseSQLDAO<Construct> {
	
	@Inject
	NoteDAO noteDAO;

	protected ConstructDAO() {
		super(Construct.class);
	}

}
