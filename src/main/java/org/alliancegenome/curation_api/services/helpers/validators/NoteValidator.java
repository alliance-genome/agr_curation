package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class NoteValidator extends AuditedObjectValidator<Note> {

	@Inject
	NoteDAO noteDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;

	public ObjectResponse<Note> validateNote(Note uiEntity, String noteVocabularyName) {
		Note note = validateNote(uiEntity, noteVocabularyName, false);
		response.setEntity(note);
		return response;
	}

	public Note validateNote(Note uiEntity, String noteVocabularyName, Boolean throwError) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update Note: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		Note dbEntity = null;
		if (id != null) {
			dbEntity = noteDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("Could not find Note with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		}else {
			dbEntity = new Note();
		}
		dbEntity = (Note) validateAuditedObjectFields(uiEntity, dbEntity);

		VocabularyTerm noteType = validateNoteType(uiEntity, dbEntity, noteVocabularyName);
		dbEntity.setNoteType(noteType);

		String freeText = validateFreeText(uiEntity);
		dbEntity.setFreeText(freeText);

		// TODO: add validation for references
		if (CollectionUtils.isNotEmpty(uiEntity.getReferences()))
			dbEntity.setReferences(uiEntity.getReferences());

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorTitle);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

	public VocabularyTerm validateNoteType(Note uiEntity, Note dbEntity, String noteVocabularyName) {
		String field = "noteType";
		if (uiEntity.getNoteType() == null ) {
			addMessageResponse(field, requiredMessage);
			return null;
		}

		VocabularyTerm noteType;
		if (noteVocabularyName == null) {
			SearchResponse<VocabularyTerm> vtSearchResponse = vocabularyTermDAO.findByField("name", uiEntity.getNoteType().getName());
			if (vtSearchResponse == null || vtSearchResponse.getSingleResult() == null) {
				addMessageResponse(field, invalidMessage);
				return null;
			}
			noteType = vtSearchResponse.getSingleResult();
		} else {
			noteType = vocabularyTermDAO.getTermInVocabulary(uiEntity.getNoteType().getName(), noteVocabularyName);
			if (noteType == null) {
				addMessageResponse(field, invalidMessage);
				return null;
			}
		}

		if (noteType.getObsolete() && !noteType.getName().equals(dbEntity.getNoteType().getName())) {
			addMessageResponse(field, obsoleteMessage);
			return null;
		}
		return noteType;
	}

	public String validateFreeText(Note uiEntity) {
		String field = "freeText";
		if (!StringUtils.isNotBlank(uiEntity.getFreeText())) {
			addMessageResponse(field, requiredMessage);
		}
		return uiEntity.getFreeText();
	}
}
