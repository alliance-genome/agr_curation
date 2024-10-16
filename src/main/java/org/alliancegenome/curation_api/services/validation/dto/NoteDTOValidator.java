package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class NoteDTOValidator extends BaseDTOValidator {

	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;
	@Inject PersonService personService;
	@Inject VocabularyTermService vocabularyTermService;

	public ObjectResponse<Note> validateNoteDTO(NoteDTO dto, String noteTypeVocabularyTermSet) {
		Note note = new Note();
		ObjectResponse<Note> noteResponse = validateAuditedObjectDTO(note, dto);

		note = noteResponse.getEntity();

		if (StringUtils.isBlank(dto.getFreeText())) {
			noteResponse.addErrorMessage("freeText", ValidationConstants.REQUIRED_MESSAGE);
		}
		note.setFreeText(dto.getFreeText());

		if (StringUtils.isBlank(dto.getNoteTypeName())) {
			noteResponse.addErrorMessage("note_type_name", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			VocabularyTerm noteType = vocabularyTermService.getTermInVocabularyTermSet(noteTypeVocabularyTermSet, dto.getNoteTypeName()).getEntity();
			if (noteType == null) {
				noteResponse.addErrorMessage("note_type_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getNoteTypeName() + ")");
			}
			note.setNoteType(noteType);
		}

		if (CollectionUtils.isNotEmpty(dto.getEvidenceCuries())) {
			List<Reference> noteReferences = new ArrayList<>();
			for (String publicationId : dto.getEvidenceCuries()) {
				Reference reference = referenceService.retrieveFromDbOrLiteratureService(publicationId);
				if (reference == null) {
					noteResponse.addErrorMessage("evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + publicationId + ")");
					break;
				}
				noteReferences.add(reference);
			}
			note.setReferences(noteReferences);
		} else {
			note.setReferences(null);
		}

		if (note.getInternal() == null) {
			note.setInternal(true);
		}

		noteResponse.setEntity(note);

		return noteResponse;
	}

}
