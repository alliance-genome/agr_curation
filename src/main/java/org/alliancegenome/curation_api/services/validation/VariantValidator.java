package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.ontology.SoTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VariantValidator extends GenomicEntityValidator<Variant> {

	@Inject VariantDAO variantDAO;
	@Inject NoteValidator noteValidator;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject SoTermService soTermService;

	private String errorMessage;

	public Variant validateVariantUpdate(Variant uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Variant: [" + uiEntity.getIdentifier() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Variant ID provided");
			throw new ApiErrorException(response);
		}

		Variant dbEntity = variantDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (Variant) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateVariant(uiEntity, dbEntity);
	}

	public Variant validateVariantCreate(Variant uiEntity) {
		response = new ObjectResponse<>();
		errorMessage = "Could not create Variant";

		Variant dbEntity = new Variant();

		dbEntity = (Variant) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateVariant(uiEntity, dbEntity);
	}

	public Variant validateVariant(Variant uiEntity, Variant dbEntity) {

		dbEntity = (Variant) validateGenomicEntityFields(uiEntity, dbEntity);

		SOTerm variantType = validateVariantType(uiEntity, dbEntity);
		dbEntity.setVariantType(variantType);

		VocabularyTerm variantStatus = validateVariantStatus(uiEntity, dbEntity);
		dbEntity.setVariantStatus(variantStatus);

		SOTerm sourceGeneralConsequence = validateSourceGeneralConsequence(uiEntity, dbEntity);
		dbEntity.setSourceGeneralConsequence(sourceGeneralConsequence);

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
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		dbEntity = variantDAO.persist(dbEntity);

		return dbEntity;
	}

	public SOTerm validateVariantType(Variant uiEntity, Variant dbEntity) {
		String field = "variantType";
		if (ObjectUtils.isEmpty(uiEntity.getVariantType())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		SOTerm variantType = null;
		if (StringUtils.isNotBlank(uiEntity.getVariantType().getCurie())) {
			variantType = soTermService.findByCurie(uiEntity.getVariantType().getCurie());
			if (variantType == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (variantType.getObsolete() && (dbEntity.getVariantType() == null || !variantType.getId().equals(dbEntity.getVariantType().getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return variantType;
	}

	private VocabularyTerm validateVariantStatus(Variant uiEntity, Variant dbEntity) {
		String field = "variantStatus";

		if (uiEntity.getVariantStatus() == null) {
			return null;
		}

		VocabularyTerm variantStatus = vocabularyTermService.getTermInVocabulary(VocabularyConstants.VARIANT_STATUS_VOCABULARY, uiEntity.getVariantStatus().getName()).getEntity();
		if (variantStatus == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (variantStatus.getObsolete() && (dbEntity.getVariantStatus() == null || !variantStatus.getName().equals(dbEntity.getVariantStatus().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return variantStatus;
	}

	public SOTerm validateSourceGeneralConsequence(Variant uiEntity, Variant dbEntity) {
		String field = "sourceGeneralConsequence";
		if (ObjectUtils.isEmpty(uiEntity.getSourceGeneralConsequence())) {
			return null;
		}

		SOTerm sourceGeneralConsequence = null;
		if (StringUtils.isNotBlank(uiEntity.getSourceGeneralConsequence().getCurie())) {
			sourceGeneralConsequence = soTermService.findByCurie(uiEntity.getSourceGeneralConsequence().getCurie());
			if (sourceGeneralConsequence == null) {
				addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			} else if (sourceGeneralConsequence.getObsolete() && (dbEntity.getSourceGeneralConsequence() == null || !sourceGeneralConsequence.getId().equals(dbEntity.getSourceGeneralConsequence().getId()))) {
				addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
		}
		return sourceGeneralConsequence;
	}

	public List<Note> validateRelatedNotes(Variant uiEntity, Variant dbEntity) {
		String field = "relatedNotes";

		List<Note> validatedNotes = new ArrayList<Note>();
		Set<String> validatedNoteIdentities = new HashSet<>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getRelatedNotes())) {
			for (int ix = 0; ix < uiEntity.getRelatedNotes().size(); ix++) {
				Note note = uiEntity.getRelatedNotes().get(ix);
				ObjectResponse<Note> noteResponse = noteValidator.validateNote(note, VocabularyConstants.VARIANT_NOTE_TYPES_VOCABULARY_TERM_SET);
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
