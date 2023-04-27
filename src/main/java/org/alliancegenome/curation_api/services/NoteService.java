package org.alliancegenome.curation_api.services;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.NoteValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

@RequestScoped
public class NoteService extends BaseEntityCrudService<Note, NoteDAO> {

	@Inject
	NoteDAO noteDAO;
	@Inject
	NoteValidator noteValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(noteDAO);
	}

	@Transactional
	public ObjectResponse<Note> upsert(Note uiEntity) {
		Note dbEntity = noteValidator.validateNote(uiEntity, null, true);
		if (dbEntity == null)
			return null;
		return new ObjectResponse<Note>(noteDAO.persist(dbEntity));
	}

	public ObjectResponse<Note> validate(Note uiEntity) {
		Note note = noteValidator.validateNote(uiEntity, null, true);
		return new ObjectResponse<Note>(note);
	}

}
