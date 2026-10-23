package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CurieMintService;
import org.alliancegenome.curation_api.services.helpers.TransgenicToolUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.associations.TransgenicToolTransgenicToolAssociationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolSynonymSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolUseSlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535: mirrors ConstructValidator.
 *
 * transgenicToolSymbol is optional here, unlike constructSymbol, matching LinkML: the symbol slot
 * carries no required flag on TransgenicTool.
 */
@RequestScoped
public class TransgenicToolValidator extends ReagentValidator {

	@Inject TransgenicToolDAO transgenicToolDAO;
	@Inject CurieMintService curieMintService;
	@Inject ReferenceValidator referenceValidator;
	@Inject CrossReferenceValidator crossReferenceValidator;
	@Inject TransgenicToolSymbolSlotAnnotationValidator transgenicToolSymbolValidator;
	@Inject TransgenicToolFullNameSlotAnnotationValidator transgenicToolFullNameValidator;
	@Inject TransgenicToolSynonymSlotAnnotationValidator transgenicToolSynonymValidator;
	@Inject TransgenicToolUseSlotAnnotationValidator transgenicToolUseValidator;
	@Inject TransgenicToolTransgenicToolAssociationValidator transgenicToolTransgenicToolAssociationValidator;

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
		}

		return validateTransgenicTool(uiEntity, dbEntity);
	}

	public TransgenicTool validateTransgenicToolCreate(TransgenicTool uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create TransgenicTool";

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

		dbEntity = (TransgenicTool) validateCommonReagentFields(uiEntity, dbEntity, VocabularyConstants.CASSETTE_AND_TRANSGENIC_TOOL_NOTE_TYPES_VOCABULARY_TERM_SET);

		dbEntity.setCrossReferences(validateCrossReferences(uiEntity, dbEntity));

		TransgenicToolSymbolSlotAnnotation symbol = validateTransgenicToolSymbol(uiEntity);
		TransgenicToolFullNameSlotAnnotation fullName = validateTransgenicToolFullName(uiEntity);
		List<TransgenicToolSynonymSlotAnnotation> synonyms = validateTransgenicToolSynonyms(uiEntity, dbEntity);
		List<TransgenicToolUseSlotAnnotation> uses = validateTransgenicToolUses(uiEntity, dbEntity);

		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		// SCRUM-6693: mint an AGRKB curie for a NEW transgenic tool that has none, before persist so the insert
		// writes it. A curator-supplied curie is left alone. The getId() == null guard is load-bearing, as
		// for constructs: this serves create and update, and validateCommonReagentFields ->
		// SubmittedObjectValidator copies the payload's curie unconditionally, so an update omitting curie
		// nulls it and an unguarded mint would then silently replace the transgenic tool's AGRKB id.
		if (dbEntity.getId() == null) {
			curieMintService.mintCurieIfAbsent(dbEntity);
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

	/**
	 * SCRUM-6655: the associations the tool is the subject of, as the detail page submits them to replace the stored
	 * ones. An entry with an id updates that association, one without creates one, and the stored ones the list
	 * omits are removed with the tool's list (orphan removal); the other tool holds no list of them. An entry's id
	 * must be one of the tool's associations and named by no other entry, and no two entries may name the same
	 * relation and tool.
	 *
	 * @param dbEntity the tool the associations belong to
	 * @param incomingAssociations the submitted associations
	 * @return the validated associations, in submitted order, each with the tool as its subject
	 * @throws ApiErrorException listing each rejected entry's errors under its index
	 */
	public List<TransgenicToolTransgenicToolAssociation> validateTransgenicToolAssociationReplacement(TransgenicTool dbEntity, List<TransgenicToolTransgenicToolAssociation> incomingAssociations) {
		response = new ObjectResponse<>();
		String field = "transgenicToolTransgenicToolAssociations";
		String errorTitle = "Could not update Transgenic Tool Associations for: [" + dbEntity.getId() + "]";

		if (incomingAssociations == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		Set<Long> heldIds = new HashSet<>();
		if (dbEntity.getTransgenicToolTransgenicToolAssociations() != null) {
			dbEntity.getTransgenicToolTransgenicToolAssociations().forEach(association -> heldIds.add(association.getId()));
		}

		List<TransgenicToolTransgenicToolAssociation> validatedAssociations = new ArrayList<>();
		Boolean allValid = true;
		Set<Long> seenIds = new HashSet<>();
		Set<String> seenIdentities = new HashSet<>();
		for (int ix = 0; ix < incomingAssociations.size(); ix++) {
			TransgenicToolTransgenicToolAssociation association = incomingAssociations.get(ix);
			if (association == null) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("transgenicToolTransgenicToolAssociationObject", ValidationConstants.REQUIRED_MESSAGE));
				continue;
			}
			if (association.getId() != null && !heldIds.contains(association.getId())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("id", ValidationConstants.INVALID_MESSAGE));
				continue;
			}
			if (association.getId() != null && !seenIds.add(association.getId())) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("id", ValidationConstants.DUPLICATE_MESSAGE));
				continue;
			}
			ObjectResponse<TransgenicToolTransgenicToolAssociation> associationResponse = transgenicToolTransgenicToolAssociationValidator.validateTransgenicToolTransgenicToolAssociation(association);
			if (associationResponse.getEntity() == null) {
				allValid = false;
				response.addErrorMessages(field, ix, associationResponse.getErrorMessages());
				continue;
			}
			association = associationResponse.getEntity();
			String identity = association.getRelation().getName() + "|" + association.getTransgenicToolTransgenicToolAssociationObject().getId();
			if (!seenIdentities.add(identity)) {
				allValid = false;
				response.addErrorMessages(field, ix, Map.of("transgenicToolTransgenicToolAssociationObject", ValidationConstants.DUPLICATE_MESSAGE));
				continue;
			}
			association.setTransgenicToolAssociationSubject(dbEntity);
			validatedAssociations.add(association);
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}

		return validatedAssociations;
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

	private TransgenicToolSymbolSlotAnnotation validateTransgenicToolSymbol(TransgenicTool uiEntity) {
		if (uiEntity.getTransgenicToolSymbol() == null) {
			return null;
		}

		String field = "transgenicToolSymbol";

		ObjectResponse<TransgenicToolSymbolSlotAnnotation> symbolResponse = transgenicToolSymbolValidator.validateTransgenicToolSymbolSlotAnnotation(uiEntity.getTransgenicToolSymbol());
		if (symbolResponse.getEntity() == null) {
			addMessageResponse(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private TransgenicToolFullNameSlotAnnotation validateTransgenicToolFullName(TransgenicTool uiEntity) {
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

	/** SCRUM-6535: mirrors AntibodyValidator.validateCrossReferences, for the curation UI path. */
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
}
