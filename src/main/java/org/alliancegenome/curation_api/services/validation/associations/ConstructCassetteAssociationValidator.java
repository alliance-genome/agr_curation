package org.alliancegenome.curation_api.services.validation.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.dao.associations.ConstructCassetteAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.ConstructCassetteAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/** SCRUM-6535: Construct Cassette Association. Mirrors ConstructGenomicEntityAssociationValidator. */
@RequestScoped
public class ConstructCassetteAssociationValidator extends EvidenceAssociationValidator<ConstructCassetteAssociation> {

	@Inject ConstructDAO constructDAO;
	@Inject CassetteDAO cassetteDAO;
	@Inject ConstructCassetteAssociationDAO lConstructCassetteAssociationDAO;
	@Inject NoteValidator noteValidator;

	private String errorMessage;

	public ObjectResponse<ConstructCassetteAssociation> validateConstructCassetteAssociation(ConstructCassetteAssociation uiEntity) {
		ConstructCassetteAssociation association = validateConstructCassetteAssociation(uiEntity, false, false);
		response.setEntity(association);
		return response;
	}

	public ConstructCassetteAssociation validateConstructCassetteAssociation(ConstructCassetteAssociation uiEntity, Boolean throwError, Boolean validateConstruct) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create/update Construct Cassette Association: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		ConstructCassetteAssociation dbEntity = null;
		if (id != null) {
			dbEntity = lConstructCassetteAssociationDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("Could not find ConstructCassetteAssociation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new ConstructCassetteAssociation();
		}

		dbEntity = validateEvidenceAssociationFields(uiEntity, dbEntity);

		if (validateConstruct) {
			Construct subject = validateRequiredEntity(constructDAO, "constructAssociationSubject", uiEntity.getConstructAssociationSubject(), dbEntity.getConstructAssociationSubject());
			dbEntity.setConstructAssociationSubject(subject);
		}

		Cassette object = validateRequiredEntity(cassetteDAO, "constructCassetteAssociationObject", uiEntity.getConstructCassetteAssociationObject(), dbEntity.getConstructCassetteAssociationObject());
		dbEntity.setConstructCassetteAssociationObject(object);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.CONSTRUCT_CASSETTE_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
		dbEntity.setRelation(relation);

		List<Note> relatedNotes = validateRelatedNotes(uiEntity, dbEntity);
		if (dbEntity.getRelatedNotes() != null) {
			dbEntity.getRelatedNotes().clear();
		}
		if (relatedNotes != null) {
			if (dbEntity.getRelatedNotes() == null) {
				dbEntity.setRelatedNotes(new ArrayList<>());
			}
			dbEntity.getRelatedNotes().addAll(relatedNotes);
		}

		if (response.hasErrors()) {
			if (throwError) {
				response.setErrorMessage(errorMessage);
				throw new ApiErrorException(response);
			} else {
				return null;
			}
		}

		return dbEntity;
	}

	public List<Note> validateRelatedNotes(ConstructCassetteAssociation uiEntity, ConstructCassetteAssociation dbEntity) {
		String field = "relatedNotes";

		List<Note> validatedNotes = new ArrayList<Note>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
			for (int ix = 0; ix < uiEntity.getRelatedNotes().size(); ix++) {
				Note note = uiEntity.getRelatedNotes().get(ix);
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.CONSTRUCT_CASSETTE_ASSOCIATION_NOTE_TYPES_VOCABULARY_TERM_SET);
				if (noteResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, noteResponse.getErrorMessages());
				} else {
					note = noteResponse.getEntity();

					String noteIdentity = NoteIdentityHelper.noteIdentity(note);
					if (validatedNoteIdentities.contains(noteIdentity)) {
						allValid = false;
						Map<String, String> duplicateError = new HashMap<>();
						duplicateError.put("freeText", ValidationConstants.DUPLICATE_MESSAGE + " (" + noteIdentity + ")");
						response.addErrorMessages(field, ix, duplicateError);
					} else {
						validatedNoteIdentities.add(noteIdentity);
						validatedNotes.add(note);
					}
				}
			}
		}
		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedNotes)) {
			return null;
		}

		return validatedNotes;
	}
}
