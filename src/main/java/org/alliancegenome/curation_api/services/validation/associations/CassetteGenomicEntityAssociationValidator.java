package org.alliancegenome.curation_api.services.validation.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.dao.GenomicEntityDAO;
import org.alliancegenome.curation_api.dao.associations.CassetteGenomicEntityAssociationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.CassetteGenomicEntityAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CassetteGenomicEntityAssociationValidator extends EvidenceAssociationValidator<CassetteGenomicEntityAssociation> {

	@Inject
	CassetteDAO cassetteDAO;
	@Inject
	GenomicEntityDAO genomicEntityDAO;
	@Inject
	CassetteGenomicEntityAssociationDAO cassetteGenomicEntityAssociationDAO;
	@Inject
	NoteValidator noteValidator;

	private String errorMessage;

	public ObjectResponse<CassetteGenomicEntityAssociation> validateCassetteGenomicEntityAssociation(CassetteGenomicEntityAssociation uiEntity) {
		CassetteGenomicEntityAssociation geAssociation = validateCassetteGenomicEntityAssociation(uiEntity, false, false);
		response.setEntity(geAssociation);
		return response;
	}

	public CassetteGenomicEntityAssociation validateCassetteGenomicEntityAssociation(CassetteGenomicEntityAssociation uiEntity, Boolean throwError, Boolean validateCassette) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create/update Cassette GenomicEntity Association: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		CassetteGenomicEntityAssociation dbEntity = null;
		if (id != null) {
			dbEntity = cassetteGenomicEntityAssociationDAO.find(id);
			if (dbEntity == null) {
				addMessageResponse("Could not find CassetteGenomicEntityAssociation with ID: [" + id + "]");
				throw new ApiErrorException(response);
			}
		} else {
			dbEntity = new CassetteGenomicEntityAssociation();
		}

		dbEntity = validateEvidenceAssociationFields(uiEntity, dbEntity);

		if (validateCassette) {
			Cassette subject = validateRequiredEntity(cassetteDAO, "cassetteAssociationSubject", uiEntity.getCassetteAssociationSubject(), dbEntity.getCassetteAssociationSubject());
			dbEntity.setCassetteAssociationSubject(subject);
		}

		GenomicEntity object = validateRequiredEntity(genomicEntityDAO, "cassetteGenomicEntityAssociationObject", uiEntity.getCassetteGenomicEntityAssociationObject(), dbEntity.getCassetteGenomicEntityAssociationObject());
		dbEntity.setCassetteGenomicEntityAssociationObject(object);

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation", VocabularyConstants.CASSETTE_GENOMIC_ENTITY_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation(), dbEntity.getRelation());
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

	public List<Note> validateRelatedNotes(CassetteGenomicEntityAssociation uiEntity, CassetteGenomicEntityAssociation dbEntity) {
		String field = "relatedNotes";

		List<Note> validatedNotes = new ArrayList<Note>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
			for (int ix = 0; ix < uiEntity.getRelatedNotes().size(); ix++) {
				Note note = uiEntity.getRelatedNotes().get(ix);
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.CASSETTE_COMPONENT_NOTE_TYPES_VOCABULARY_TERM_SET);
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
