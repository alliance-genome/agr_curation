package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.helpers.validators.NoteValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class NoteService extends BaseEntityCrudService<Note, NoteDAO> {

	@Inject NoteDAO noteDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;
	@Inject NoteValidator noteValidator;
	@Inject PersonService personService;
	@Inject AuditedObjectService<Note, NoteDTO> auditedObjectService;
	
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
	
	public ObjectResponse<Note> validateNoteDTO(NoteDTO dto, String note_type_vocabulary) {
		Note note = new Note();
		ObjectResponse<Note> noteResponse = auditedObjectService.validateAuditedObjectDTO(note, dto);
		
		note = noteResponse.getEntity();
		
		if (StringUtils.isBlank(dto.getFreeText()))
			noteResponse.addErrorMessage("freeText", ValidationConstants.REQUIRED_MESSAGE);
		note.setFreeText(dto.getFreeText());
		
		
		if (StringUtils.isBlank(dto.getNoteType())) {
			noteResponse.addErrorMessage("noteType", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			VocabularyTerm noteType = vocabularyTermDAO.getTermInVocabulary(dto.getNoteType(), note_type_vocabulary);
			if (noteType == null)
				noteResponse.addErrorMessage("noteType", ValidationConstants.INVALID_MESSAGE);
			note.setNoteType(noteType);
		}
		
		List<Reference> noteReferences = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getReferences())) {
			for (String publicationId : dto.getReferences()) {
				Reference reference = referenceDAO.find(publicationId);
				if (reference == null || reference.getObsolete()) {
					reference = referenceService.retrieveFromLiteratureService(publicationId);
					if (reference == null) {
						noteResponse.addErrorMessage("references", ValidationConstants.INVALID_MESSAGE);
						break;
					}
					referenceDAO.persist(reference);
				}
				noteReferences.add(reference);
			}
		}
		note.setReferences(noteReferences);
		
		noteResponse.setEntity(note);
		
		return noteResponse;
	}
	
}
