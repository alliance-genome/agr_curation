package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.VariantDAO;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VariantValidator extends GenomicEntityValidator {

	@Inject
	VariantDAO variantDAO;
	@Inject
	NoteValidator noteValidator;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	SoTermDAO soTermDAO;

	private String errorMessage;

	public Variant validateVariantUpdate(Variant uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Variant: [" + uiEntity.getCurie() + "]";

		String curie = validateCurie(uiEntity);
		if (curie == null) {
			throw new ApiErrorException(response);
		}

		Variant dbEntity = variantDAO.find(curie);
		if (dbEntity == null) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (Variant) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateVariant(uiEntity, dbEntity);
	}

	public Variant validateVariantCreate(Variant uiEntity) {
		response = new ObjectResponse<>();
		errorMessage = "Could not create Variant: [" + uiEntity.getCurie() + "]";

		Variant dbEntity = new Variant();
		String curie = validateCurie(uiEntity);
		dbEntity.setCurie(curie);

		dbEntity = (Variant) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateVariant(uiEntity, dbEntity);
	}

	public Variant validateVariant(Variant uiEntity, Variant dbEntity) {

		NCBITaxonTerm taxon = validateTaxon(uiEntity, dbEntity);
		dbEntity.setTaxon(taxon);
		
		DataProvider dataProvider = validateDataProvider(uiEntity, dbEntity);
		dbEntity.setDataProvider(dataProvider);
		
		List<Long> currentXrefIds;
		if (dbEntity.getCrossReferences() == null) {
			currentXrefIds = new ArrayList<>();
		} else {
			currentXrefIds = dbEntity.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
		}
		
		List<CrossReference> crossReferences = validateCrossReferences(uiEntity, dbEntity);
		dbEntity.setCrossReferences(crossReferences);
		List<Long> mergedIds = crossReferences == null ? new ArrayList<>() :
			crossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
		for (Long currentXrefId : currentXrefIds) {
			if (!mergedIds.contains(currentXrefId)) {
				crossReferenceDAO.remove(currentXrefId);
			}
		}
		
		SOTerm variantType = validateVariantType(uiEntity, dbEntity);
		dbEntity.setVariantType(variantType);

		VocabularyTerm variantStatus = validateVariantStatus(uiEntity, dbEntity);
		dbEntity.setVariantStatus(variantStatus);
		
		SOTerm sourceGeneralConsequence = validateSourceGeneralConsequence(uiEntity, dbEntity);
		dbEntity.setSourceGeneralConsequence(sourceGeneralConsequence);
		
		List<Note> relatedNotes = validateRelatedNotes(uiEntity, dbEntity);
		if (dbEntity.getRelatedNotes() != null)
			dbEntity.getRelatedNotes().clear();
		if (relatedNotes != null) {
			if (dbEntity.getRelatedNotes() == null)
				dbEntity.setRelatedNotes(new ArrayList<>());
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
		if (ObjectUtils.isEmpty(uiEntity.getVariantType()) || StringUtils.isEmpty(uiEntity.getVariantType().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		SOTerm variantType = soTermDAO.find(uiEntity.getVariantType().getCurie());
		if (variantType == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		} else if (variantType.getObsolete() && (dbEntity.getVariantType() == null || !variantType.getCurie().equals(dbEntity.getVariantType().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return variantType;
	}

	private VocabularyTerm validateVariantStatus(Variant uiEntity, Variant dbEntity) {
		String field = "variantStatus";

		if (uiEntity.getVariantStatus() == null)
			return null;

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
		if (ObjectUtils.isEmpty(uiEntity.getSourceGeneralConsequence()) || StringUtils.isEmpty(uiEntity.getSourceGeneralConsequence().getCurie()))
			return null;
		
		SOTerm sourceGeneralConsequence = soTermDAO.find(uiEntity.getSourceGeneralConsequence().getCurie());
		if (sourceGeneralConsequence == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		} else if (sourceGeneralConsequence.getObsolete() && (dbEntity.getSourceGeneralConsequence() == null || !sourceGeneralConsequence.getCurie().equals(dbEntity.getSourceGeneralConsequence().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
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
		
		if (CollectionUtils.isEmpty(validatedNotes))
			return null;

		return validatedNotes;
	}

}
