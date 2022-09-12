package org.alliancegenome.curation_api.controllers.crud;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.controllers.base.BaseEntityCrudController;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.interfaces.crud.NoteCrudInterface;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.NoteService;

@RequestScoped
public class NoteCrudController extends BaseEntityCrudController<NoteService, Note, NoteDAO> implements NoteCrudInterface {

	@Inject NoteService noteService;
	
	@Override
	@PostConstruct
	protected void init() {
		setService(noteService);
	}
	
	@Override
	public ObjectResponse<Note> update(Note entity) {
		return noteService.upsert(entity);
	}
	
	@Override
	public ObjectResponse<Note> create(Note entity) {
		return noteService.upsert(entity);
	}

	public ObjectResponse<Note> validate(Note entity) {
		return noteService.validate(entity);
	}
}
