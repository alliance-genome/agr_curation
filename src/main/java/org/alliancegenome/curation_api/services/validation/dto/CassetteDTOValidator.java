package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.CassetteDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.CassetteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.CassetteComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.CassetteUniqueIdHelper;
import org.alliancegenome.curation_api.services.helpers.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.helpers.UniqueIdentifierHelper;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteComponentSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.CassetteSynonymSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CassetteDTOValidator extends ReagentDTOValidator<Cassette, CassetteDTO> {

	@Inject
	CassetteSymbolSlotAnnotationDTOValidator cassetteSymbolDtoValidator;
	@Inject
	CassetteFullNameSlotAnnotationDTOValidator cassetteFullNameDtoValidator;
	@Inject
	CassetteSynonymSlotAnnotationDTOValidator cassetteSynonymDtoValidator;
	@Inject
	CassetteDAO cassetteDAO;
	@Inject
	CassetteComponentSlotAnnotationDTOValidator cassetteComponentDtoValidator;
	@Inject
	SlotAnnotationIdentityHelper identityHelper;
	@Inject
	ReferenceService referenceService;

	@Transactional
	public ObjectResponse<Cassette> validateCassetteDTO(CassetteDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {

		response = new ObjectResponse<Cassette>();

		Cassette cassette = new Cassette();

		String uniqueId = CassetteUniqueIdHelper.getCassetteUniqueId(dto);
		String cassetteId = UniqueIdentifierHelper.setSubmittedObjectIdentifiers(dto, cassette, uniqueId);
		String identifyingField = UniqueIdentifierHelper.getIdentifyingField(dto);

		boolean existing = false;

		SearchResponse<Cassette> resp = cassetteDAO.findByField(identifyingField, cassetteId);
		if (resp != null) {
			Cassette dbCassette = resp.getSingleResult();
			if (dbCassette != null) {
				cassette = dbCassette;
				existing = true;
			}
		}

		cassette.setUniqueId(uniqueId);
		UniqueIdentifierHelper.setObsoleteAndInternal(dto, cassette);

		cassette = validateReagentDTO(cassette, dto, VocabularyConstants.CASSETTE_NOTE_TYPES_VOCABULARY_TERM_SET);

		List<Reference> refs = validateOptionalEntities("reference_curies", dto.getReferenceCuries(), referenceService::retrieveFromDbOrLiteratureService);

		cassette.setReferences(refs);

		CassetteSymbolSlotAnnotation symbol = validateCassetteSymbol(cassette, dto);
		cassette.setCassetteSymbol(symbol);

		CassetteFullNameSlotAnnotation fullName = validateCassetteFullName(cassette, dto);
		cassette.setCassetteFullName(fullName);

		List<CassetteSynonymSlotAnnotation> synonyms = validateCassetteSynonyms(cassette, dto);
		if (cassette.getCassetteSynonyms() != null) {
			cassette.getCassetteSynonyms().clear();
		}
		if (synonyms != null) {
			if (cassette.getCassetteSynonyms() == null) {
				cassette.setCassetteSynonyms(new ArrayList<>());
			}
			cassette.getCassetteSynonyms().addAll(synonyms);
		}

		List<CassetteComponentSlotAnnotation> components = validateCassetteComponents(cassette, dto);
		if (cassette.getCassetteComponents() != null) {
			cassette.getCassetteComponents().clear();
		}
		if (components != null) {
			if (cassette.getCassetteComponents() == null) {
				cassette.setCassetteComponents(new ArrayList<>());
			}
			cassette.getCassetteComponents().addAll(components);
		}

		response.convertWarningMessagesToMap();
		response.convertErrorMessagesToMap();

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		if (!existing) {
			cassette = cassetteDAO.persist(cassette);
		}

		response.setEntity(cassette);

		return response;
	}

	private CassetteSymbolSlotAnnotation validateCassetteSymbol(Cassette cassette, CassetteDTO dto) {
		String field = "cassette_symbol_dto";

		if (dto.getCassetteSymbolDto() == null) {
			response.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<CassetteSymbolSlotAnnotation> symbolResponse = cassetteSymbolDtoValidator.validateCassetteSymbolSlotAnnotationDTO(cassette.getCassetteSymbol(), dto.getCassetteSymbolDto());
		if (symbolResponse.hasErrors()) {
			response.addErrorMessage(field, symbolResponse.errorMessagesString());
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		if (symbolResponse.hasWarnings()) {
			response.addWarningMessage(field, symbolResponse.warningMessagesString());
			response.addWarningMessages(field, symbolResponse.getWarningMessages());
		}

		CassetteSymbolSlotAnnotation symbol = symbolResponse.getEntity();
		symbol.setSingleCassette(cassette);

		return symbol;
	}

	private CassetteFullNameSlotAnnotation validateCassetteFullName(Cassette cassette, CassetteDTO dto) {
		if (dto.getCassetteFullNameDto() == null) {
			return null;
		}

		String field = "cassette_full_name_dto";

		ObjectResponse<CassetteFullNameSlotAnnotation> nameResponse = cassetteFullNameDtoValidator.validateCassetteFullNameSlotAnnotationDTO(cassette.getCassetteFullName(), dto.getCassetteFullNameDto());
		if (nameResponse.hasErrors()) {
			response.addErrorMessage(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		if (nameResponse.hasWarnings()) {
			response.addWarningMessage(field, nameResponse.warningMessagesString());
			response.addWarningMessages(field, nameResponse.getWarningMessages());
		}

		CassetteFullNameSlotAnnotation fullName = nameResponse.getEntity();
		fullName.setSingleCassette(cassette);

		return fullName;
	}

	private List<CassetteSynonymSlotAnnotation> validateCassetteSynonyms(Cassette cassette, CassetteDTO dto) {
		String field = "cassette_synonym_dtos";

		Map<String, CassetteSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(cassette.getCassetteSynonyms())) {
			for (CassetteSynonymSlotAnnotation existingSynonym : cassette.getCassetteSynonyms()) {
				existingSynonyms.put(SlotAnnotationIdentityHelper.nameSlotAnnotationIdentity(existingSynonym), existingSynonym);
			}
		}

		List<CassetteSynonymSlotAnnotation> validatedSynonyms = new ArrayList<CassetteSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getCassetteSynonymDtos())) {
			for (int ix = 0; ix < dto.getCassetteSynonymDtos().size(); ix++) {
				NameSlotAnnotationDTO synDto = dto.getCassetteSynonymDtos().get(ix);
				CassetteSynonymSlotAnnotation syn = existingSynonyms.remove(identityHelper.nameSlotAnnotationDtoIdentity(synDto));
				ObjectResponse<CassetteSynonymSlotAnnotation> synResponse = cassetteSynonymDtoValidator.validateCassetteSynonymSlotAnnotationDTO(syn, synDto);
				if (synResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
				} else {
					syn = synResponse.getEntity();
					syn.setSingleCassette(cassette);
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

	private List<CassetteComponentSlotAnnotation> validateCassetteComponents(Cassette cassette, CassetteDTO dto) {
		String field = "cassette_component_dtos";

		Map<String, CassetteComponentSlotAnnotation> existingComponents = new HashMap<>();
		if (CollectionUtils.isNotEmpty(cassette.getCassetteComponents())) {
			for (CassetteComponentSlotAnnotation existingComponent : cassette.getCassetteComponents()) {
				existingComponents.put(SlotAnnotationIdentityHelper.cassetteComponentIdentity(existingComponent), existingComponent);
			}
		}

		List<CassetteComponentSlotAnnotation> validatedComponents = new ArrayList<CassetteComponentSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getCassetteComponentDtos())) {
			for (int ix = 0; ix < dto.getCassetteComponentDtos().size(); ix++) {
				CassetteComponentSlotAnnotationDTO compDto = dto.getCassetteComponentDtos().get(ix);
				CassetteComponentSlotAnnotation comp = existingComponents.remove(identityHelper.cassetteComponentDtoIdentity(compDto));
				ObjectResponse<CassetteComponentSlotAnnotation> compResponse = cassetteComponentDtoValidator.validateCassetteComponentSlotAnnotationDTO(comp, compDto);
				if (compResponse.hasErrors()) {
					allValid = false;
					response.addErrorMessages(field, ix, compResponse.getErrorMessages());
				} else {
					comp = compResponse.getEntity();
					comp.setSingleCassette(cassette);
					validatedComponents.add(comp);
				}
				if (compResponse.hasWarnings()) {
					response.addWarningMessages(field, ix, compResponse.getWarningMessages());
				}
			}
		}

		if (!allValid) {
			response.convertMapToErrorMessages(field);
			return null;
		}

		response.convertMapToWarningMessages(field);

		if (CollectionUtils.isEmpty(validatedComponents)) {
			return null;
		}

		return validatedComponents;
	}
}
