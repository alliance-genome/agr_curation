package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class NoteDTOValidator extends BaseDTOValidator {

	@Inject ReferenceDAO referenceDAO;
	@Inject ReferenceService referenceService;
	@Inject PersonService personService;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	
	public ObjectResponse<Note> validateNoteDTO(NoteDTO dto, String note_type_vocabulary) {
		Note note = new Note();
		ObjectResponse<Note> noteResponse = validateAuditedObjectDTO(note, dto);
		
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
					reference = referenceDAO.persist(reference);
				}
				noteReferences.add(reference);
			}
		}
		note.setReferences(noteReferences);
		
		noteResponse.setEntity(note);
		
		return noteResponse;
	}

}
