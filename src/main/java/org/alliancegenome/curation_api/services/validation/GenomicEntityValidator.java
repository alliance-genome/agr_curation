package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.helpers.NoteIdentityHelper;

import jakarta.inject.Inject;

public class GenomicEntityValidator<E extends GenomicEntity> extends BiologicalEntityValidator<E> {

	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject CrossReferenceService crossReferenceService;
	@Inject NoteValidator relatedNoteValidator;

	public E validateGenomicEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet) {
		return validateGenomicEntityFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, true);
	}

	public E validateGenomicEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier) {
		return validateGenomicEntityFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier, true);
	}

	public E validateGenomicEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier, boolean manageCrossReferences) {
		return validateGenomicEntityFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier, manageCrossReferences, true);
	}

	/**
	 * @param manageCrossReferences whether the calling endpoint's JSON view carries crossReferences. An endpoint whose
	 *        view omits the field receives an empty list whatever the client sent, so applying it clears the stored rows
	 *        and orphanRemoval deletes them; such an endpoint passes false and leaves the list untouched, and cross
	 *        references for that entity are written through its cross-references sub-resource. An endpoint whose view
	 *        does carry the field passes true, both to honour the payload and because clearing the collection
	 *        initialises it - a lazy collection left uninitialised while in view throws once the transaction closes.
	 * @param manageRelatedNotes whether the calling endpoint's JSON view carries relatedNotes; see
	 *        SubmittedObjectValidator.validateSubmittedObjectFields.
	 */
	public E validateGenomicEntityFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier, boolean manageCrossReferences, boolean manageRelatedNotes) {

		dbEntity = validateBiologicalEntityFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier, manageRelatedNotes);

		if (manageCrossReferences) {
			List<CrossReference> xrefs = validateCrossReferences(uiEntity, dbEntity);
			if (dbEntity.getCrossReferences() != null) {
				dbEntity.getCrossReferences().clear();
			}
			if (xrefs != null) {
				if (dbEntity.getCrossReferences() == null) {
					dbEntity.setCrossReferences(new ArrayList<>());
				}
				dbEntity.getCrossReferences().addAll(xrefs);
			}
		}

		return dbEntity;
	}

	public List<CrossReference> validateCrossReferences(E uiEntity, E dbEntity) {
		return crossReferenceValidator.validateCrossReferences(uiEntity.getCrossReferences(), "crossReferences", response);
	}

	/**
	 * Validates the slot annotations one of an entity's slot annotation sub-resources is to hold, in submitted
	 * order, and assigns each to the entity. Every entry must be present; an entry with an id updates that
	 * annotation, which must be one the entity holds and no other entry names; an entry without one is a new
	 * annotation.
	 *
	 * @param dbEntity the entity the annotations belong to
	 * @param incomingAnnotations the submitted annotations
	 * @param heldAnnotations the annotations of this kind the entity holds
	 * @param field the field the errors are recorded under, each entry's keyed by its index
	 * @param requiredField the entry field a missing entry is reported against
	 * @param label the list's name, for the error title
	 * @param slotAnnotationValidator validates one entry
	 * @param assignOwner sets an annotation's entity
	 * @return the validated annotations, one per submitted entry
	 * @throws ApiErrorException when the list is missing or any entry is rejected, with each entry's errors keyed
	 *         by its index
	 */
	protected <S extends SlotAnnotation> List<S> validateSlotAnnotationReplacement(E dbEntity, List<S> incomingAnnotations, List<S> heldAnnotations, String field, String requiredField,
			String label, Function<S, ObjectResponse<S>> slotAnnotationValidator, BiConsumer<S, E> assignOwner) {
		response = new ObjectResponse<>();
		String errorTitle = "Could not update " + label + " for: [" + dbEntity.getId() + "]";

		if (incomingAnnotations == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		Set<Long> heldIds = new HashSet<>();
		if (heldAnnotations != null) {
			heldAnnotations.forEach(annotation -> heldIds.add(annotation.getId()));
		}

		List<S> validatedAnnotations = new ArrayList<>();
		boolean allValid = true;
		Set<Long> seenIds = new HashSet<>();
		for (int ix = 0; ix < incomingAnnotations.size(); ix++) {
			S annotation = incomingAnnotations.get(ix);
			if (annotation == null) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of(requiredField, ValidationConstants.REQUIRED_MESSAGE));
				continue;
			}
			if (annotation.getId() != null && !heldIds.contains(annotation.getId())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("id", ValidationConstants.INVALID_MESSAGE));
				continue;
			}
			if (annotation.getId() != null && !seenIds.add(annotation.getId())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("id", ValidationConstants.DUPLICATE_MESSAGE));
				continue;
			}
			ObjectResponse<S> annotationResponse = slotAnnotationValidator.apply(annotation);
			if (annotationResponse.getEntity() == null) {
				allValid = false;
				response.addErrorMessages(field, ix, annotationResponse.getErrorMessages());
			} else {
				S validatedAnnotation = annotationResponse.getEntity();
				assignOwner.accept(validatedAnnotation, dbEntity);
				validatedAnnotations.add(validatedAnnotation);
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return validatedAnnotations;
	}

	/**
	 * Validates the notes an entity's related notes sub-resource is to hold, in submitted order, with the same
	 * rules as notes saved through the entity itself: a note type from the entity's note types set, free text,
	 * and no two notes alike. Every entry must be present; an entry with an id updates that note, which must be
	 * one the entity holds and no other entry names; an entry without one is a new note. Every entry's errors
	 * are reported together.
	 *
	 * @param dbEntity the entity the notes belong to
	 * @param incomingNotes the submitted notes
	 * @param noteTypeVocabularyTermSet the vocabulary term set the notes' types come from
	 * @return the validated notes, one per submitted entry
	 * @throws ApiErrorException when the list is missing or any entry is rejected, with each entry's errors keyed
	 *         by its index
	 */
	protected List<Note> validateRelatedNoteReplacement(E dbEntity, List<Note> incomingNotes, String noteTypeVocabularyTermSet) {
		response = new ObjectResponse<>();
		String field = "relatedNotes";
		String errorTitle = "Could not update Related Notes for: [" + dbEntity.getId() + "]";

		if (incomingNotes == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		Set<Long> heldIds = new HashSet<>();
		if (dbEntity.getRelatedNotes() != null) {
			dbEntity.getRelatedNotes().forEach(note -> heldIds.add(note.getId()));
		}

		List<Note> validatedNotes = new ArrayList<>();
		boolean allValid = true;
		Set<Long> seenIds = new HashSet<>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		for (int ix = 0; ix < incomingNotes.size(); ix++) {
			Note note = incomingNotes.get(ix);
			if (note == null) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("freeText", ValidationConstants.REQUIRED_MESSAGE));
				continue;
			}
			if (note.getId() != null && !heldIds.contains(note.getId())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("id", ValidationConstants.INVALID_MESSAGE));
				continue;
			}
			if (note.getId() != null && !seenIds.add(note.getId())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("id", ValidationConstants.DUPLICATE_MESSAGE));
				continue;
			}
			ObjectResponse<Note> noteResponse = relatedNoteValidator.validateNote(note, noteTypeVocabularyTermSet);
			if (noteResponse.getEntity() == null) {
				allValid = false;
				response.addErrorMessages(field, ix, noteResponse.getErrorMessages());
				continue;
			}
			Note validatedNote = noteResponse.getEntity();
			String noteIdentity = NoteIdentityHelper.noteIdentity(validatedNote);
			if (!validatedNoteIdentities.add(noteIdentity)) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("freeText", ValidationConstants.DUPLICATE_MESSAGE + " (" + noteIdentity + ")"));
				continue;
			}
			validatedNotes.add(validatedNote);
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return validatedNotes;
	}

}
