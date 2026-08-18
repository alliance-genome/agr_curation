package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.TransgenicToolDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.ingest.dto.TransgenicToolDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.TransgenicToolUseSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.helpers.TransgenicToolUniqueIdHelper;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolSynonymSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.TransgenicToolUseSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class TransgenicToolDTOValidator extends ReagentDTOValidator<TransgenicTool, TransgenicToolDTO> {

	@Inject
	TransgenicToolSymbolSlotAnnotationDTOValidator transgenicToolSymbolDtoValidator;
	@Inject
	TransgenicToolFullNameSlotAnnotationDTOValidator transgenicToolFullNameDtoValidator;
	@Inject
	TransgenicToolSynonymSlotAnnotationDTOValidator transgenicToolSynonymDtoValidator;
	@Inject
	TransgenicToolDAO transgenicToolDAO;
	@Inject
	TransgenicToolUseSlotAnnotationDTOValidator transgenicToolUseDtoValidator;
	@Inject
	SlotAnnotationIdentityHelper identityHelper;
	@Inject
	ReferenceService referenceService;
	@Inject
	CrossReferenceDTOValidator crossReferenceDtoValidator;
	@Inject
	CrossReferenceService crossReferenceService;

	@Transactional
	public ObjectResponse<TransgenicTool> validateTransgenicToolDTO(TransgenicToolDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {

		response = new ObjectResponse<TransgenicTool>();

		TransgenicTool transgenicTool = new TransgenicTool();

		String uniqueId = TransgenicToolUniqueIdHelper.getTransgenicToolUniqueId(dto);
		String transgenicToolId = UniqueIdentifierHelper.setSubmittedObjectIdentifiers(dto, transgenicTool, uniqueId);
		String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);

		boolean existing = false;

		SearchResponse<TransgenicTool> resp = transgenicToolDAO.findByField(identifyingField, transgenicToolId);
		if (resp != null) {
			TransgenicTool dbTransgenicTool = resp.getSingleResult();
			if (dbTransgenicTool != null) {
				transgenicTool = dbTransgenicTool;
				existing = true;
			}
		}

		transgenicTool.setUniqueId(uniqueId);
		UniqueIdentifierHelper.setObsoleteAndInternal(dto, transgenicTool);

		transgenicTool = validateReagentDTO(transgenicTool, dto, VocabularyConstants.TRANSGENIC_TOOL_NOTE_TYPES_VOCABULARY_TERM_SET);

		List<Reference> refs = validateOptionalEntities("reference_curies", dto.getReferenceCuries(), referenceService::retrieveFromDbOrLiteratureService);

		transgenicTool.setReferences(refs);

		transgenicTool.setCrossReferences(validateCrossReferences(dto, transgenicTool));

		TransgenicToolSymbolSlotAnnotation symbol = validateTransgenicToolSymbol(transgenicTool, dto);
		transgenicTool.setTransgenicToolSymbol(symbol);

		TransgenicToolFullNameSlotAnnotation fullName = validateTransgenicToolFullName(transgenicTool, dto);
		transgenicTool.setTransgenicToolFullName(fullName);

		List<TransgenicToolSynonymSlotAnnotation> synonyms = validateTransgenicToolSynonyms(transgenicTool, dto);
		if (transgenicTool.getTransgenicToolSynonyms() != null) {
			transgenicTool.getTransgenicToolSynonyms().clear();
		}
		if (synonyms != null) {
			if (transgenicTool.getTransgenicToolSynonyms() == null) {
				transgenicTool.setTransgenicToolSynonyms(new ArrayList<>());
			}
			transgenicTool.getTransgenicToolSynonyms().addAll(synonyms);
		}

		List<TransgenicToolUseSlotAnnotation> uses = validateTransgenicToolUses(transgenicTool, dto);
		if (transgenicTool.getTransgenicToolUses() != null) {
			transgenicTool.getTransgenicToolUses().clear();
		}
		if (uses != null) {
			if (transgenicTool.getTransgenicToolUses() == null) {
				transgenicTool.setTransgenicToolUses(new ArrayList<>());
			}
			transgenicTool.getTransgenicToolUses().addAll(uses);
		}

		response.convertWarningMessagesToMap();
		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		if (!existing) {
			transgenicTool = transgenicToolDAO.persist(transgenicTool);
		}

		response.setEntity(transgenicTool);

		return response;
	}

	private List<CrossReference> validateCrossReferences(TransgenicToolDTO dto, TransgenicTool transgenicTool) {
		List<CrossReference> validatedXrefs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getCrossReferenceDtos())) {
			for (CrossReferenceDTO xrefDto : dto.getCrossReferenceDtos()) {
				ObjectResponse<CrossReference> xrefResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(xrefDto, null);
				if (xrefResponse.hasErrors()) {
					response.addErrorMessage("cross_reference_dtos", xrefResponse.errorMessagesString());
					break;
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		return crossReferenceService.getUpdatedXrefList(validatedXrefs, transgenicTool.getCrossReferences());
	}

	private TransgenicToolSymbolSlotAnnotation validateTransgenicToolSymbol(TransgenicTool transgenicTool, TransgenicToolDTO dto) {
		String field = "transgenic_tool_symbol_dto";

		if (dto.getTransgenicToolSymbolDto() == null) {
			response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<TransgenicToolSymbolSlotAnnotation> symbolResponse = transgenicToolSymbolDtoValidator.validateTransgenicToolSymbolSlotAnnotationDTO(transgenicTool.getTransgenicToolSymbol(), dto.getTransgenicToolSymbolDto());
		if (symbolResponse.hasErrors()) {
			response.addErrorMessage(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		if (symbolResponse.hasWarnings()) {
			response.addWarningMessage(field, symbolResponse.warningMessagesString());
			response.addWarningMessages(field, symbolResponse.getWarningMessages());
		}

		TransgenicToolSymbolSlotAnnotation symbol = symbolResponse.getEntity();
		symbol.setSingleTransgenicTool(transgenicTool);

		return symbol;
	}

	private TransgenicToolFullNameSlotAnnotation validateTransgenicToolFullName(TransgenicTool transgenicTool, TransgenicToolDTO dto) {
		if (dto.getTransgenicToolFullNameDto() == null) {
			return null;
		}

		String field = "transgenic_tool_full_name_dto";

		ObjectResponse<TransgenicToolFullNameSlotAnnotation> nameResponse = transgenicToolFullNameDtoValidator.validateTransgenicToolFullNameSlotAnnotationDTO(transgenicTool.getTransgenicToolFullName(), dto.getTransgenicToolFullNameDto());
		if (nameResponse.hasErrors()) {
			response.addErrorMessage(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		if (nameResponse.hasWarnings()) {
			response.addWarningMessage(field, nameResponse.warningMessagesString());
			response.addWarningMessages(field, nameResponse.getWarningMessages());
		}

		TransgenicToolFullNameSlotAnnotation fullName = nameResponse.getEntity();
		fullName.setSingleTransgenicTool(transgenicTool);

		return fullName;
	}

	private List<TransgenicToolSynonymSlotAnnotation> validateTransgenicToolSynonyms(TransgenicTool transgenicTool, TransgenicToolDTO dto) {
		String field = "transgenic_tool_synonym_dtos";

		Map<String, TransgenicToolSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(transgenicTool.getTransgenicToolSynonyms())) {
			for (TransgenicToolSynonymSlotAnnotation existingSynonym : transgenicTool.getTransgenicToolSynonyms()) {
				existingSynonyms.put(SlotAnnotationIdentityHelper.nameSlotAnnotationIdentity(existingSynonym), existingSynonym);
			}
		}

		List<TransgenicToolSynonymSlotAnnotation> validatedSynonyms = new ArrayList<TransgenicToolSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getTransgenicToolSynonymDtos())) {
			for (int ix = 0; ix < dto.getTransgenicToolSynonymDtos().size(); ix++) {
				NameSlotAnnotationDTO synDto = dto.getTransgenicToolSynonymDtos().get(ix);
				TransgenicToolSynonymSlotAnnotation syn = existingSynonyms.remove(identityHelper.nameSlotAnnotationDtoIdentity(synDto));
				ObjectResponse<TransgenicToolSynonymSlotAnnotation> synResponse = transgenicToolSynonymDtoValidator.validateTransgenicToolSynonymSlotAnnotationDTO(syn, synDto);
				if (synResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
				} else {
					syn = synResponse.getEntity();
					syn.setSingleTransgenicTool(transgenicTool);
					validatedSynonyms.add(syn);
				}
				if (synResponse.hasWarnings()) {
					response.addWarningMessages(field, ix, synResponse.getWarningMessages());
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		response.convertMapToWarningMessages(field);

		if (CollectionUtils.isEmpty(validatedSynonyms)) {
			return null;
		}

		return validatedSynonyms;
	}

	private List<TransgenicToolUseSlotAnnotation> validateTransgenicToolUses(TransgenicTool transgenicTool, TransgenicToolDTO dto) {
		String field = "transgenic_tool_use_dtos";

		Map<String, TransgenicToolUseSlotAnnotation> existingUses = new HashMap<>();
		if (CollectionUtils.isNotEmpty(transgenicTool.getTransgenicToolUses())) {
			for (TransgenicToolUseSlotAnnotation existingUse : transgenicTool.getTransgenicToolUses()) {
				existingUses.put(SlotAnnotationIdentityHelper.transgenicToolUseIdentity(existingUse), existingUse);
			}
		}

		List<TransgenicToolUseSlotAnnotation> validatedUses = new ArrayList<TransgenicToolUseSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getTransgenicToolUseDtos())) {
			for (int ix = 0; ix < dto.getTransgenicToolUseDtos().size(); ix++) {
				TransgenicToolUseSlotAnnotationDTO useDto = dto.getTransgenicToolUseDtos().get(ix);
				TransgenicToolUseSlotAnnotation use = existingUses.remove(identityHelper.transgenicToolUseDtoIdentity(useDto));
				ObjectResponse<TransgenicToolUseSlotAnnotation> useResponse = transgenicToolUseDtoValidator.validateTransgenicToolUseSlotAnnotationDTO(use, useDto);
				if (useResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, useResponse.getErrorMessages());
				} else {
					use = useResponse.getEntity();
					use.setSingleTransgenicTool(transgenicTool);
					validatedUses.add(use);
				}
				if (useResponse.hasWarnings()) {
					response.addWarningMessages(field, ix, useResponse.getWarningMessages());
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		response.convertMapToWarningMessages(field);

		if (CollectionUtils.isEmpty(validatedUses)) {
			return null;
		}

		return validatedUses;
	}
}
