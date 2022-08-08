package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Note;

@ApplicationScoped
public class NoteDAO extends BaseSQLDAO<Note> {

	protected NoteDAO() {
		super(Note.class);
	}

}
