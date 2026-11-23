package org.alliancegenome.curation_api.services.validation.base;

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
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.ReferenceValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class SubmittedObjectValidator<E extends SubmittedObject> extends AuditedObjectValidator<SubmittedObject> {

	@Inject ReferenceValidator referenceValidator;

	public E validateSubmittedObjectFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet) {
		return validateSubmittedObjectFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, true);
	}

	public E validateSubmittedObjectFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier) {
		return validateSubmittedObjectFields(uiEntity, dbEntity, noteTypeVocabularyTermSet, requireModIdentifier, true);
	}

	/**
	 * @param manageRelatedNotes whether the calling endpoint's JSON view carries relatedNotes. An endpoint whose view
	 *        omits them leaves the stored ones as they are; they are written through a related notes sub-resource of
	 *        the entity instead.
	 */
	public E validateSubmittedObjectFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet, boolean requireModIdentifier, boolean manageRelatedNotes) {
		Boolean newEntity = false;
		if (dbEntity.getId() == null) {
			newEntity = true;
		}
		
		dbEntity = (E) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);
		
		String curie = handleStringField(uiEntity.getCurie());
		dbEntity.setCurie(curie);

		String primaryExternalId = handleStringField(uiEntity.getPrimaryExternalId());
		dbEntity.setPrimaryExternalId(primaryExternalId);

		String modInternalId = validateModInternalId(uiEntity, requireModIdentifier);
		dbEntity.setModInternalId(modInternalId);

		Organization dataProvider = validateDataProvider(uiEntity.getDataProvider(), dbEntity.getDataProvider(), newEntity);
		dbEntity.setDataProvider(dataProvider);
		
		CrossReference dataProviderXref = validateDataProviderCrossReference(uiEntity.getDataProviderCrossReference(), dbEntity.getDataProviderCrossReference());
		dbEntity.setDataProviderCrossReference(dataProviderXref);
		
		if (manageRelatedNotes) {
			List<Note> relatedNotes = validateRelatedNotes(uiEntity.getRelatedNotes(), noteTypeVocabularyTermSet);
			if (dbEntity.getRelatedNotes() != null) {
				dbEntity.getRelatedNotes().clear();
			}
			if (relatedNotes != null) {
				if (dbEntity.getRelatedNotes() == null) {
					dbEntity.setRelatedNotes(new ArrayList<>());
				}
				dbEntity.getRelatedNotes().addAll(relatedNotes);
			}
		}
		
		return dbEntity;
	}

	public String validateCurie(E uiEntity) {
		String curie = handleStringField(uiEntity.getCurie());
		if (curie != null && !curie.startsWith("AGRKB:")) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		return curie;
	}

	public String validateModInternalId(E uiEntity) {
		return validateModInternalId(uiEntity, true);
	}

	public String validateModInternalId(E uiEntity, boolean isRequired) {
		String modInternalId = uiEntity.getModInternalId();
		if (StringUtils.isBlank(modInternalId)) {
			if (isRequired && StringUtils.isBlank(uiEntity.getPrimaryExternalId())) {
				addMessageResponse("modInternalId", ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + " primaryExternalId");
			}
			return null;
		}
		return modInternalId;
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
	protected <O extends E, S extends SlotAnnotation> List<S> validateSlotAnnotationReplacement(O dbEntity, List<S> incomingAnnotations, List<S> heldAnnotations, String field, String requiredField,
			String label, Function<S, ObjectResponse<S>> slotAnnotationValidator, BiConsumer<S, O> assignOwner) {
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
			ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, noteTypeVocabularyTermSet);
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

	/**
	 * Resolves the references an entity's references sub-resource is to hold, in submitted order, with the same
	 * rules as references saved through the entity itself, and no reference named twice. Two curies can name the
	 * same reference, so duplicates are found by the reference they resolve to. Duplicates are reported once every
	 * entry resolves.
	 *
	 * @param ownerId the id of the entity the references belong to
	 * @param incomingReferences the submitted references
	 * @param heldReferences the references the entity holds now
	 * @return the resolved references, one per submitted entry
	 * @throws ApiErrorException when the list is missing or any entry cannot be resolved or is a duplicate, with
	 *         each entry's errors keyed by its index
	 */
	public List<Reference> validateReferenceReplacement(Long ownerId, List<Reference> incomingReferences, List<Reference> heldReferences) {
		response = new ObjectResponse<>();
		String field = "references";
		String errorTitle = "Could not update References for: [" + ownerId + "]";

		if (incomingReferences == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		List<Reference> resolvedReferences = resolveReferences(incomingReferences, heldReferences, field);
		boolean allValid = resolvedReferences != null;
		if (allValid) {
			Set<Long> seenIds = new HashSet<>();
			for (int ix = 0; ix < resolvedReferences.size(); ix++) {
				if (!seenIds.add(resolvedReferences.get(ix).getId())) {
					allValid = false;
					response.addErrorMessages(field, ix, Map.of("curie", ValidationConstants.DUPLICATE_MESSAGE));
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return resolvedReferences;
	}

	/**
	 * Resolves each submitted reference to the stored one it names, by its curie or one of its cross reference
	 * curies. An entry must name a reference that exists, and a reference the entity does not already hold must
	 * not be obsolete. A cross reference curie does not resolve to an obsolete reference, so an obsolete reference
	 * the entity already holds is kept only when named by its own curie.
	 *
	 * @param incomingReferences the submitted references
	 * @param heldReferences the references the entity holds now
	 * @param field the field each entry's errors are recorded under, keyed by its index
	 * @return the resolved references in submitted order, or null when any entry is rejected
	 */
	protected List<Reference> resolveReferences(List<Reference> incomingReferences, List<Reference> heldReferences, String field) {
		Set<Long> heldIds = new HashSet<>();
		if (heldReferences != null) {
			heldReferences.forEach(reference -> heldIds.add(reference.getId()));
		}

		List<Reference> resolvedReferences = new ArrayList<>();
		boolean allValid = true;
		for (int ix = 0; ix < incomingReferences.size(); ix++) {
			Reference incomingReference = incomingReferences.get(ix);
			if (incomingReference == null || StringUtils.isBlank(incomingReference.getCurie())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("curie", ValidationConstants.REQUIRED_MESSAGE));
				continue;
			}

			ObjectResponse<Reference> referenceResponse = referenceValidator.validateReference(incomingReference);
			Reference reference = referenceResponse.getEntity();
			if (reference == null) {
				allValid = false;
				response.addErrorMessages(field, ix, referenceResponse.getErrorMessages());
			} else if (Boolean.TRUE.equals(reference.getObsolete()) && !heldIds.contains(reference.getId())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("curie", ValidationConstants.OBSOLETE_MESSAGE));
			} else {
				resolvedReferences.add(reference);
			}
		}

		return allValid ? resolvedReferences : null;
	}
}
