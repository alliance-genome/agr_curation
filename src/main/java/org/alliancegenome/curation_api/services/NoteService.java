package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.NoteValidator;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class NoteService extends BaseCrudService<Note, NoteDAO> {

	@Inject
	NoteDAO noteDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	ReferenceDAO referenceDAO;
	@Inject
	ReferenceService referenceService;
	@Inject
	NoteValidator noteValidator;
	@Inject
	PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(noteDAO);
	}
	
	@Override
	@Transactional
	public ObjectResponse<Note> update(Note uiEntity) {
		Note dbEntity = noteValidator.validateNote(uiEntity, null, true);
		return new ObjectResponse<Note>(noteDAO.persist(dbEntity));
	}
	
	public ObjectResponse<Note> validate(Note uiEntity) {
		Note note = noteValidator.validateNote(uiEntity, null, true);
		return new ObjectResponse<Note>(note);
	}
	
	public Note validateNoteDTO(NoteDTO dto, String note_type_vocabulary) throws ObjectValidationException {
		Note note = new Note();
		if (dto.getFreeText() == null || dto.getNoteType() == null || dto.getInternal() == null) {
			throw new ObjectValidationException(dto, "Note missing required fields");
		}
		note.setFreeText(dto.getFreeText());
		note.setInternal(dto.getInternal());
		note.setObsolete(dto.getObsolete());
		
		VocabularyTerm noteType = vocabularyTermDAO.getTermInVocabulary(dto.getNoteType(), note_type_vocabulary);
		if (noteType == null) {
			throw new ObjectValidationException(dto, "Note type '" + dto.getNoteType() + "' not found in vocabulary '" + note_type_vocabulary + "'");
		}
		note.setNoteType(noteType);
		
		List<Reference> noteReferences = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getReferences())) {
			for (String publicationId : dto.getReferences()) {
				Reference reference = referenceDAO.find(publicationId);
				if (reference == null || reference.getObsolete()) {
					reference = referenceService.retrieveFromLiteratureService(publicationId);
					if (reference == null) {
						throw new ObjectValidationException(dto, "Invalid reference attached to note: " + publicationId);
					}
					referenceDAO.persist(reference);
				}
				noteReferences.add(reference);
			}
			note.setReferences(noteReferences);
		}
		
		if (dto.getCreatedBy()!= null) {
			Person createdBy = personService.fetchByUniqueIdOrCreate(dto.getCreatedBy());
			note.setCreatedBy(createdBy);
		}
		if (dto.getUpdatedBy() != null) {
			Person updatedBy = personService.fetchByUniqueIdOrCreate(dto.getUpdatedBy());
			note.setUpdatedBy(updatedBy);
		}
		

		if (dto.getDateUpdated() != null) {
			OffsetDateTime dateLastModified;
			try {
				dateLastModified = OffsetDateTime.parse(dto.getDateUpdated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_updated - skipping");
			}
			note.setDateUpdated(dateLastModified);
		}

		if (dto.getDateCreated() != null) {
			OffsetDateTime creationDate;
			try {
				creationDate = OffsetDateTime.parse(dto.getDateCreated());
			} catch (DateTimeParseException e) {
				throw new ObjectValidationException(dto, "Could not parse date_created - skipping");
			}
			note.setDateCreated(creationDate);
		}
		
		return note;
	}
	
}
