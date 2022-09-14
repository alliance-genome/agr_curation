package org.alliancegenome.curation_api.services.helpers.validators;

import java.util.*;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class NoteValidator extends AuditedObjectValidator<Note> {

	@Inject
	NoteDAO noteDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	ReferenceService referenceService;
	@Inject
	ReferenceValidator referenceValidator;

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
		Boolean newEntity;
		if (id != null) {
			dbEntity = noteDAO.find(id);
			newEntity = false;
			if (dbEntity == null) {
				addMessageResponse("Could not find Note with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new Note();
			newEntity = true;
		}
		dbEntity = (Note) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		VocabularyTerm noteType = validateNoteType(uiEntity, dbEntity, noteVocabularyName);
		dbEntity.setNoteType(noteType);

		String freeText = validateFreeText(uiEntity);
		dbEntity.setFreeText(freeText);

		List<String> previousReferenceCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getReferences()))
			previousReferenceCuries = dbEntity.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(uiEntity.getReferences())) {
			List<Reference> references = new ArrayList<Reference>();
			for (Reference uiReference : uiEntity.getReferences()) {
				Reference reference = validateReference(uiReference, previousReferenceCuries);
				if (reference != null) {
					references.add(reference);
				}
			}
			dbEntity.setReferences(references);
		} else {
			dbEntity.setReferences(null);
		}
		
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
	
	private Reference validateReference (Reference uiEntity, List<String> previousCuries) {
		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity);
		if (singleRefResponse.getEntity() == null) {
			Map<String, String> errors = singleRefResponse.getErrorMessages();
			for (String refField : errors.keySet()) {
				addMessageResponse("references", refField + " - " + errors.get(refField));
			}
			return null;
		}
		
		if (singleRefResponse.getEntity().getObsolete() && !previousCuries.contains(singleRefResponse.getEntity().getCurie())) {
			addMessageResponse("references", "curie - " + ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return singleRefResponse.getEntity();
	}

	public VocabularyTerm validateNoteType(Note uiEntity, Note dbEntity, String noteVocabularyName) {
		String field = "noteType";
		if (uiEntity.getNoteType() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm noteType;
		if (noteVocabularyName == null) {
			SearchResponse<VocabularyTerm> vtSearchResponse = vocabularyTermDAO.findByField("name", uiEntity.getNoteType().getName());
			if (vtSearchResponse == null || vtSearchResponse.getSingleResult() == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			noteType = vtSearchResponse.getSingleResult();
		} else {
			noteType = vocabularyTermDAO.getTermInVocabulary(uiEntity.getNoteType().getName(), noteVocabularyName);
			if (noteType == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
		}

		if (noteType.getObsolete() && (dbEntity.getNoteType() == null || !noteType.getName().equals(dbEntity.getNoteType().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return noteType;
	}

	public String validateFreeText(Note uiEntity) {
		String field = "freeText";
		if (StringUtils.isBlank(uiEntity.getFreeText())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		return uiEntity.getFreeText();
	}
}
