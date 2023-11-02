package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Note;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NoteDAO extends BaseSQLDAO<Note> {

	protected NoteDAO() {
		super(Note.class);
	}

}
