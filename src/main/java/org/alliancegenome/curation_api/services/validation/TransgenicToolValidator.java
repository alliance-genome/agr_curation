package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.TransgenicToolUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolSynonymSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolUseSlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class TransgenicToolValidator extends ReagentValidator {

	@Inject TransgenicToolDAO transgenicToolDAO;
	@Inject TransgenicToolUseSlotAnnotationValidator transgenicToolUseValidator;
	@Inject ReferenceValidator referenceValidator;
	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject TransgenicToolSymbolSlotAnnotationValidator transgenicToolSymbolValidator;
	@Inject TransgenicToolFullNameSlotAnnotationValidator transgenicToolFullNameValidator;
	@Inject TransgenicToolSynonymSlotAnnotationValidator transgenicToolSynonymValidator;

	private String errorMessage;

	public TransgenicTool validateTransgenicToolUpdate(TransgenicTool uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update TransgenicTool: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No TransgenicTool ID provided");
			throw new ApiErrorException(response);
		}
		TransgenicTool dbEntity = transgenicToolDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find TransgenicTool with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if TransgenicTool ID has not been
			// found
		}

		return validateTransgenicTool(uiEntity, dbEntity);
	}

	public TransgenicTool validateTransgenicToolCreate(TransgenicTool uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Cound not create TransgenicTool";

		TransgenicTool dbEntity = new TransgenicTool();

		return validateTransgenicTool(uiEntity, dbEntity);
	}

	public TransgenicTool validateTransgenicTool(TransgenicTool uiEntity, TransgenicTool dbEntity) {

		List<String> previousReferenceCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getReferences())) {
			previousReferenceCuries = dbEntity.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
		}
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

		dbEntity = (TransgenicTool) validateCommonReagentFields(uiEntity, dbEntity, VocabularyConstants.TRANSGENIC_TOOL_NOTE_TYPES_VOCABULARY_TERM_SET);

		TransgenicToolSymbolSlotAnnotation symbol = validateTransgenicToolSymbol(uiEntity, dbEntity);
		TransgenicToolFullNameSlotAnnotation fullName = validateTransgenicToolFullName(uiEntity, dbEntity);
		List<TransgenicToolSynonymSlotAnnotation> synonyms = validateTransgenicToolSynonyms(uiEntity, dbEntity);
		List<TransgenicToolUseSlotAnnotation> uses = validateTransgenicToolUses(uiEntity, dbEntity);
		List<CrossReference> crossReferences = validateCrossReferences(uiEntity, dbEntity);

		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		dbEntity.setCrossReferences(crossReferences);

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		dbEntity = transgenicToolDAO.persist(dbEntity);

		if (symbol != null) {
			symbol.setSingleTransgenicTool(dbEntity);
		}
		dbEntity.setTransgenicToolSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleTransgenicTool(dbEntity);
		}
		dbEntity.setTransgenicToolFullName(fullName);

		if (dbEntity.getTransgenicToolSynonyms() != null) {
			dbEntity.getTransgenicToolSynonyms().clear();
		}
		if (synonyms != null) {
			if (dbEntity.getTransgenicToolSynonyms() == null) {
				dbEntity.setTransgenicToolSynonyms(new ArrayList<>());
			}
			dbEntity.getTransgenicToolSynonyms().addAll(synonyms);
		}

		if (dbEntity.getTransgenicToolUses() != null) {
			dbEntity.getTransgenicToolUses().clear();
		}
		if (uses != null) {
			if (dbEntity.getTransgenicToolUses() == null) {
				dbEntity.setTransgenicToolUses(new ArrayList<>());
			}
			dbEntity.getTransgenicToolUses().addAll(uses);
		}

		return dbEntity;
	}

	private Reference validateReference(Reference uiEntity, List<String> previousCuries) {
		ObjectResponse<Reference> singleRefResponse = referenceValidator.validateReference(uiEntity);
		if (singleRefResponse.getEntity() == null) {
			addMessageResponse("references", singleRefResponse.errorMessagesString());
			return null;
		}

		if (singleRefResponse.getEntity().getObsolete() && (CollectionUtils.isEmpty(previousCuries) || !previousCuries.contains(singleRefResponse.getEntity().getCurie()))) {
			addMessageResponse("references", "curie - " + ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return singleRefResponse.getEntity();
	}

	private List<CrossReference> validateCrossReferences(TransgenicTool uiEntity, TransgenicTool dbEntity) {
		String field = "crossReferences";

		List<CrossReference> validatedXrefs = new ArrayList<CrossReference>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getCrossReferences())) {
			for (int ix = 0; ix < uiEntity.getCrossReferences().size(); ix++) {
				CrossReference xref = uiEntity.getCrossReferences().get(ix);
				ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(xref, false);
				if (xrefResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, xrefResponse.getErrorMessages());
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedXrefs)) {
			return null;
		}

		return validatedXrefs;
	}

	public String validateUniqueId(TransgenicTool uiEntity, TransgenicTool dbEntity) {

		if (dbEntity.getDataProvider() == null) {
			return null;
		}

		String uniqueId = TransgenicToolUniqueIdHelper.getTransgenicToolUniqueId(uiEntity);

		if (dbEntity.getUniqueId() == null || !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<TransgenicTool> response = transgenicToolDAO.findByField("uniqueId", uniqueId);
			if (response != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
				return null;
			}
		}

		return uniqueId;
	}

	private TransgenicToolSymbolSlotAnnotation validateTransgenicToolSymbol(TransgenicTool uiEntity, TransgenicTool dbEntity) {
		String field = "transgenicToolSymbol";

		if (uiEntity.getTransgenicToolSymbol() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<TransgenicToolSymbolSlotAnnotation> symbolResponse = transgenicToolSymbolValidator.validateTransgenicToolSymbolSlotAnnotation(uiEntity.getTransgenicToolSymbol());
		if (symbolResponse.getEntity() == null) {
			addMessageResponse(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private TransgenicToolFullNameSlotAnnotation validateTransgenicToolFullName(TransgenicTool uiEntity, TransgenicTool dbEntity) {
		if (uiEntity.getTransgenicToolFullName() == null) {
			return null;
		}

		String field = "transgenicToolFullName";

		ObjectResponse<TransgenicToolFullNameSlotAnnotation> nameResponse = transgenicToolFullNameValidator.validateTransgenicToolFullNameSlotAnnotation(uiEntity.getTransgenicToolFullName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<TransgenicToolSynonymSlotAnnotation> validateTransgenicToolSynonyms(TransgenicTool uiEntity, TransgenicTool dbEntity) {
		String field = "transgenicToolSynonyms";

		List<TransgenicToolSynonymSlotAnnotation> validatedSynonyms = new ArrayList<TransgenicToolSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getTransgenicToolSynonyms())) {
			for (int ix = 0; ix < uiEntity.getTransgenicToolSynonyms().size(); ix++) {
				TransgenicToolSynonymSlotAnnotation syn = uiEntity.getTransgenicToolSynonyms().get(ix);
				ObjectResponse<TransgenicToolSynonymSlotAnnotation> synResponse = transgenicToolSynonymValidator.validateTransgenicToolSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
					allValid = false;
				} else {
					syn = synResponse.getEntity();
					syn.setSingleTransgenicTool(dbEntity);
					validatedSynonyms.add(syn);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSynonyms)) {
			return null;
		}

		return validatedSynonyms;
	}

	private List<TransgenicToolUseSlotAnnotation> validateTransgenicToolUses(TransgenicTool uiEntity, TransgenicTool dbEntity) {
		String field = "transgenicToolUses";

		List<TransgenicToolUseSlotAnnotation> validatedUses = new ArrayList<TransgenicToolUseSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getTransgenicToolUses())) {
			for (int ix = 0; ix < uiEntity.getTransgenicToolUses().size(); ix++) {
				TransgenicToolUseSlotAnnotation use = uiEntity.getTransgenicToolUses().get(ix);
				ObjectResponse<TransgenicToolUseSlotAnnotation> useResponse = transgenicToolUseValidator.validateTransgenicToolUseSlotAnnotation(use);
				if (useResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, useResponse.getErrorMessages());
					allValid = false;
				} else {
					use = useResponse.getEntity();
					use.setSingleTransgenicTool(dbEntity);
					validatedUses.add(use);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedUses)) {
			return null;
		}

		return validatedUses;
	}

}
