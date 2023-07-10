package org.alliancegenome.curation_api.services.validation.slotAnnotations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class NameSlotAnnotationValidator<E extends NameSlotAnnotation> extends SlotAnnotationValidator<E> {

	@Inject
	VocabularyTermService vocabularyTermService;

	public E validateNameSlotAnnotationFields(E uiEntity, E dbEntity, Boolean newEntity) {

		dbEntity = validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (StringUtils.isBlank(uiEntity.getDisplayText())) {
			addMessageResponse("displayText", ValidationConstants.REQUIRED_MESSAGE);
			dbEntity.setDisplayText(null);
		} else {
			dbEntity.setDisplayText(uiEntity.getDisplayText());
		}

		if (StringUtils.isBlank(uiEntity.getFormatText())) {
			addMessageResponse("formatText", ValidationConstants.REQUIRED_MESSAGE);
			dbEntity.setFormatText(null);
		} else {
			dbEntity.setFormatText(uiEntity.getFormatText());
		}

		if (StringUtils.isBlank(uiEntity.getSynonymUrl())) {
			dbEntity.setSynonymUrl(null);
		} else {
			dbEntity.setSynonymUrl(uiEntity.getSynonymUrl());
		}

		VocabularyTerm synonymScope = validateSynonymScope(uiEntity, dbEntity);
		dbEntity.setSynonymScope(synonymScope);

		return dbEntity;
	}

	private VocabularyTerm validateSynonymScope(E uiEntity, E dbEntity) {
		String field = "synonymScope";
		if (uiEntity.getSynonymScope() == null)
			return null;

		VocabularyTerm synonymScope = vocabularyTermService.getTermInVocabulary(VocabularyConstants.SYNONYM_SCOPE_VOCABULARY, uiEntity.getSynonymScope().getName()).getEntity();
		if (synonymScope == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (synonymScope.getObsolete() && (dbEntity.getSynonymScope() == null || !synonymScope.getName().equals(dbEntity.getSynonymScope().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return synonymScope;
	}

	private VocabularyTerm validateNameType(VocabularyTerm nameType, VocabularyTerm dbTerm) {

		if (nameType.getObsolete() && (dbTerm == null || !nameType.getName().equals(dbTerm.getName()))) {
			addMessageResponse("nameType", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return nameType;
	}

	public VocabularyTerm validateSymbolNameType(VocabularyTerm uiTerm, VocabularyTerm dbTerm) {
		String field = "nameType";
		if (uiTerm == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm nameType = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET, uiTerm.getName()).getEntity();
		if (nameType == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		return validateNameType(nameType, dbTerm);
	}

	public VocabularyTerm validateFullNameType(VocabularyTerm uiTerm, VocabularyTerm dbTerm) {
		String field = "nameType";
		if (uiTerm == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm nameType = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.FULL_NAME_TYPE_TERM_SET, uiTerm.getName()).getEntity();
		if (nameType == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		return validateNameType(nameType, dbTerm);
	}

	public VocabularyTerm validateSystematicNameType(VocabularyTerm uiTerm, VocabularyTerm dbTerm) {
		String field = "nameType";
		if (uiTerm == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm nameType = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.SYSTEMATIC_NAME_TYPE_TERM_SET, uiTerm.getName()).getEntity();
		if (nameType == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		return validateNameType(nameType, dbTerm);
	}

	public VocabularyTerm validateSynonymNameType(VocabularyTerm uiTerm, VocabularyTerm dbTerm) {
		String field = "nameType";
		if (uiTerm == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm nameType = vocabularyTermService.getTermInVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY, uiTerm.getName()).getEntity();
		if (nameType == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		return validateNameType(nameType, dbTerm);
	}

}
