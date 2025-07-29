package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class NoteService extends BaseEntityCrudService<Note, NoteDAO> {

	@Inject NoteDAO noteDAO;
	@Inject NoteValidator noteValidator;
	@Inject VocabularyTermService vocabularyTermService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(noteDAO);
	}

	@Transactional
	public ObjectResponse<Note> upsert(Note uiEntity) {
		Note dbEntity = noteValidator.validateNote(uiEntity, null, true);
		if (dbEntity == null) {
			return null;
		}
		return new ObjectResponse<Note>(noteDAO.persist(dbEntity));
	}

	public ObjectResponse<Note> validate(Note uiEntity) {
		Note note = noteValidator.validateNote(uiEntity, null, true);
		return new ObjectResponse<Note>(note);
	}

	@Transactional
	public Note createDeprecationNote(String entityIdentifier, String requestSource, List<String> deprecationReasons) {
		Note deprecationNote = new Note();
		deprecationNote.setNoteType(vocabularyTermService.getTermInVocabulary(VocabularyConstants.NOTE_TYPE_VOCABULARY, VocabularyConstants.DEPRECATION_REASON_TERM).getEntity());
		
		String noteText = "Deletion of " + entityIdentifier + " was requested by " + requestSource;
		if (requestSource.contains("bulk load")) {
			noteText = entityIdentifier + " was not present in " + requestSource;
		}
		noteText += ".  It was deprecated instead of deleted due to the following foreign key restraints: " + StringUtils.join(deprecationReasons, " | ");
		deprecationNote.setFreeText(noteText);
		
		return noteDAO.persist(deprecationNote);
	}
}
