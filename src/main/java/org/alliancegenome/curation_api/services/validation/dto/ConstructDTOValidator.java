package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.constructs.ConstructUniqueIdHelper;
import org.alliancegenome.curation_api.services.helpers.slotAnnotations.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationDTOValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ConstructDTOValidator extends ReagentDTOValidator {

	@Inject
	ConstructSymbolSlotAnnotationDAO constructSymbolDAO;
	@Inject
	ConstructFullNameSlotAnnotationDAO constructFullNameDAO;
	@Inject
	ConstructSynonymSlotAnnotationDAO constructSynonymDAO;
	@Inject
	ConstructSymbolSlotAnnotationDTOValidator constructSymbolDtoValidator;
	@Inject
	ConstructFullNameSlotAnnotationDTOValidator constructFullNameDtoValidator;
	@Inject
	ConstructSynonymSlotAnnotationDTOValidator constructSynonymDtoValidator;
	@Inject
	ConstructDAO constructDAO;
	@Inject
	ConstructComponentSlotAnnotationDAO constructComponentDAO;
	@Inject
	ConstructComponentSlotAnnotationDTOValidator constructComponentDtoValidator;
	@Inject
	SlotAnnotationIdentityHelper identityHelper;

	private ObjectResponse<Construct> constructResponse = new ObjectResponse<Construct>();
	
	@Transactional
	public Construct validateConstructDTO(ConstructDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		Construct construct = new Construct();
		String constructId;
		String identifyingField;
		String uniqueId = ConstructUniqueIdHelper.getConstructUniqueId(dto);
		
		if (StringUtils.isNotBlank(dto.getModEntityId())) {
			constructId = dto.getModEntityId();
			construct.setModEntityId(constructId);
			identifyingField = "modEntityId";
		} else if (StringUtils.isNotBlank(dto.getModInternalId())) {
			constructId = dto.getModInternalId();
			construct.setModInternalId(constructId);
			identifyingField = "modInternalId";
		} else {
			constructId = uniqueId;
			identifyingField = "uniqueId";
		}

		SearchResponse<Construct> constructList = constructDAO.findByField(identifyingField, constructId);
		if (constructList != null && constructList.getResults().size() > 0) {
			construct = constructList.getResults().get(0);
		}
		construct.setUniqueId(uniqueId);
		
		ObjectResponse<Construct> reagentResponse = validateReagentDTO(construct, dto);
		constructResponse.addErrorMessages(reagentResponse.getErrorMessages());
		construct = reagentResponse.getEntity();

		if (CollectionUtils.isNotEmpty(dto.getReferenceCuries())) {
			List<Reference> references = new ArrayList<>();
			for (String publicationId : dto.getReferenceCuries()) {
				Reference reference = referenceService.retrieveFromDbOrLiteratureService(publicationId);
				if (reference == null) {
					constructResponse.addErrorMessage("reference_curies", ValidationConstants.INVALID_MESSAGE + " (" + publicationId + ")");
				} else {
					references.add(reference);
				}
			}
			construct.setReferences(references);
		} else {
			construct.setReferences(null);
		}
		
		ConstructSymbolSlotAnnotation symbol = validateConstructSymbol(construct, dto);
		ConstructFullNameSlotAnnotation fullName = validateConstructFullName(construct, dto);
		List<ConstructSynonymSlotAnnotation> synonyms = validateConstructSynonyms(construct, dto);
		List<ConstructComponentSlotAnnotation> components = validateConstructComponents(construct, dto);
		
		constructResponse.convertErrorMessagesToMap();

		if (constructResponse.hasErrors())
			throw new ObjectValidationException(dto, constructResponse.errorMessagesString());

		construct = constructDAO.persist(construct);

		// Attach construct and persist SlotAnnotation objects

		if (symbol != null) {
			symbol.setSingleConstruct(construct);
			constructSymbolDAO.persist(symbol);
		}
		construct.setConstructSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleConstruct(construct);
			constructFullNameDAO.persist(fullName);
		}
		construct.setConstructFullName(fullName);

		if (construct.getConstructSynonyms() != null)
			construct.getConstructSynonyms().clear();
		if (synonyms != null) {
			for (ConstructSynonymSlotAnnotation syn : synonyms) {
				syn.setSingleConstruct(construct);
				constructSynonymDAO.persist(syn);
			}
			if (construct.getConstructSynonyms() == null)
				construct.setConstructSynonyms(new ArrayList<>());
			construct.getConstructSynonyms().addAll(synonyms);
		}
		
		if (construct.getConstructComponents() != null)
			construct.getConstructComponents().clear();
		if (components != null) {
			for (ConstructComponentSlotAnnotation comp : components) {
				comp.setSingleConstruct(construct);
				constructComponentDAO.persist(comp);
			}
			if (construct.getConstructComponents() == null)
				construct.setConstructComponents(new ArrayList<>());
			construct.getConstructComponents().addAll(components);
		}

		return construct;
	}
	
	private ConstructSymbolSlotAnnotation validateConstructSymbol(Construct construct, ConstructDTO dto) {
		String field = "construct_symbol_dto";

		if (dto.getConstructSymbolDto() == null) {
			constructResponse.addErrorMessage(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<ConstructSymbolSlotAnnotation> symbolResponse = constructSymbolDtoValidator.validateConstructSymbolSlotAnnotationDTO(construct.getConstructSymbol(), dto.getConstructSymbolDto());
		if (symbolResponse.hasErrors()) {
			constructResponse.addErrorMessage(field, symbolResponse.errorMessagesString());
			constructResponse.addErrorMessages(field, symbolResponse.getErrorMessages());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private ConstructFullNameSlotAnnotation validateConstructFullName(Construct construct, ConstructDTO dto) {
		if (dto.getConstructFullNameDto() == null)
			return null;

		String field = "construct_full_name_dto";

		ObjectResponse<ConstructFullNameSlotAnnotation> nameResponse = constructFullNameDtoValidator.validateConstructFullNameSlotAnnotationDTO(construct.getConstructFullName(), dto.getConstructFullNameDto());
		if (nameResponse.hasErrors()) {
			constructResponse.addErrorMessage(field, nameResponse.errorMessagesString());
			constructResponse.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<ConstructSynonymSlotAnnotation> validateConstructSynonyms(Construct construct, ConstructDTO dto) {
		String field = "construct_synonym_dtos";
		
		Map<String, ConstructSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(construct.getConstructSynonyms())) {
			for (ConstructSynonymSlotAnnotation existingSynonym : construct.getConstructSynonyms()) {
				existingSynonyms.put(SlotAnnotationIdentityHelper.nameSlotAnnotationIdentity(existingSynonym), existingSynonym);
			}
		}

		List<ConstructSynonymSlotAnnotation> validatedSynonyms = new ArrayList<ConstructSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getConstructSynonymDtos())) {
			for (int ix = 0; ix < dto.getConstructSynonymDtos().size(); ix++) {
				NameSlotAnnotationDTO synDto = dto.getConstructSynonymDtos().get(ix);
				ConstructSynonymSlotAnnotation syn = existingSynonyms.remove(identityHelper.nameSlotAnnotationDtoIdentity(synDto));
				ObjectResponse<ConstructSynonymSlotAnnotation> synResponse = constructSynonymDtoValidator.validateConstructSynonymSlotAnnotationDTO(syn, synDto);
				if (synResponse.hasErrors()) {
					allValid = false;
					constructResponse.addErrorMessages(field, ix, synResponse.getErrorMessages());
				} else {
					validatedSynonyms.add(synResponse.getEntity());
				}
			}
		}
		
		if (!allValid) {
			constructResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedSynonyms))
			return null;

		return validatedSynonyms;
	}
	
	private List<ConstructComponentSlotAnnotation> validateConstructComponents(Construct construct, ConstructDTO dto) {
		String field = "construct_component_dtos";
		
		Map<String, ConstructComponentSlotAnnotation> existingComponents = new HashMap<>();
		if (CollectionUtils.isNotEmpty(construct.getConstructComponents())) {
			for (ConstructComponentSlotAnnotation existingComponent : construct.getConstructComponents()) {
				existingComponents.put(SlotAnnotationIdentityHelper.constructComponentIdentity(existingComponent), existingComponent);
			}
		}

		List<ConstructComponentSlotAnnotation> validatedComponents = new ArrayList<ConstructComponentSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(dto.getConstructComponentDtos())) {
			for (int ix = 0; ix < dto.getConstructComponentDtos().size(); ix++) {
				ConstructComponentSlotAnnotationDTO compDto = dto.getConstructComponentDtos().get(ix);
				ConstructComponentSlotAnnotation comp = existingComponents.remove(identityHelper.constructComponentDtoIdentity(compDto));
				ObjectResponse<ConstructComponentSlotAnnotation> compResponse = constructComponentDtoValidator.validateConstructComponentSlotAnnotationDTO(comp, compDto);
				if (compResponse.hasErrors()) {
					allValid = false;
					constructResponse.addErrorMessages(field, ix, compResponse.getErrorMessages());
				} else {
					validatedComponents.add(compResponse.getEntity());
				}
			}
		}
		
		if (!allValid) {
			constructResponse.convertMapToErrorMessages(field);
			return null;
		}
		
		if (CollectionUtils.isEmpty(validatedComponents))
			return null;

		return validatedComponents;
	}
}
