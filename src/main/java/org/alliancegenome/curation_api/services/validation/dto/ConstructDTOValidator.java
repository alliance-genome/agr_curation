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

	@Transactional
	public Construct validateConstructDTO(ConstructDTO dto, BackendBulkDataProvider dataProvider) throws ObjectValidationException {

		ObjectResponse<Construct> constructResponse = new ObjectResponse<Construct>();

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
		
		ConstructSymbolSlotAnnotation symbol = construct.getConstructSymbol();
		if (dto.getConstructSymbolDto() == null) {
			constructResponse.addErrorMessage("construct_symbol_dto", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<ConstructSymbolSlotAnnotation> symbolResponse = constructSymbolDtoValidator.validateConstructSymbolSlotAnnotationDTO(symbol, dto.getConstructSymbolDto());
			if (symbolResponse.hasErrors()) {
				constructResponse.addErrorMessage("construct_symbol_dto", symbolResponse.errorMessagesString());
			} else {
				symbol = symbolResponse.getEntity();
			}
		}

		ConstructFullNameSlotAnnotation fullName = construct.getConstructFullName();
		if (construct.getConstructFullName() != null && dto.getConstructFullNameDto() == null) {
			fullName.setSingleConstruct(null);
			constructFullNameDAO.remove(fullName.getId());
		}

		if (dto.getConstructFullNameDto() != null) {
			ObjectResponse<ConstructFullNameSlotAnnotation> fullNameResponse = constructFullNameDtoValidator.validateConstructFullNameSlotAnnotationDTO(fullName, dto.getConstructFullNameDto());
			if (fullNameResponse.hasErrors()) {
				constructResponse.addErrorMessage("construct_full_name_dto", fullNameResponse.errorMessagesString());
			} else {
				fullName = fullNameResponse.getEntity();
			}
		} else {
			fullName = null;
		}

		Map<String, ConstructSynonymSlotAnnotation> existingSynonyms = new HashMap<>();
		if (CollectionUtils.isNotEmpty(construct.getConstructSynonyms())) {
			for (ConstructSynonymSlotAnnotation existingSynonym : construct.getConstructSynonyms()) {
				existingSynonyms.put(SlotAnnotationIdentityHelper.nameSlotAnnotationIdentity(existingSynonym), existingSynonym);
			}
		}
		
		List<ConstructSynonymSlotAnnotation> synonyms = new ArrayList<>();
		List<Long> synonymIdsToKeep = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getConstructSynonymDtos())) {
			for (NameSlotAnnotationDTO synonymDTO : dto.getConstructSynonymDtos()) {
				ObjectResponse<ConstructSynonymSlotAnnotation> synonymResponse = constructSynonymDtoValidator.validateConstructSynonymSlotAnnotationDTO(existingSynonyms.get(identityHelper.nameSlotAnnotationDtoIdentity(synonymDTO)), synonymDTO);
				if (synonymResponse.hasErrors()) {
					constructResponse.addErrorMessage("construct_synonym_dtos", synonymResponse.errorMessagesString());
				} else {
					ConstructSynonymSlotAnnotation synonym = synonymResponse.getEntity();
					synonyms.add(synonym);
					if (synonym.getId() != null)
						synonymIdsToKeep.add(synonym.getId());
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(construct.getConstructSynonyms())) {
			for (ConstructSynonymSlotAnnotation syn : construct.getConstructSynonyms()) {
				if (!synonymIdsToKeep.contains(syn.getId())) {
					syn.setSingleConstruct(null);
					constructSynonymDAO.remove(syn.getId());
				}
			}
		}
		
		Map<String, ConstructComponentSlotAnnotation> existingComponentIds = new HashMap<>();
		if (CollectionUtils.isNotEmpty(construct.getConstructComponents())) {
			for (ConstructComponentSlotAnnotation existingComponent : construct.getConstructComponents()) {
				existingComponentIds.put(SlotAnnotationIdentityHelper.constructComponentIdentity(existingComponent), existingComponent);
			}
		}
		
		List<ConstructComponentSlotAnnotation> components = new ArrayList<>();
		List<Long> componentIdsToKeep = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getConstructComponentDtos())) {
			for (ConstructComponentSlotAnnotationDTO componentDTO : dto.getConstructComponentDtos()) {
				ObjectResponse<ConstructComponentSlotAnnotation> componentResponse = constructComponentDtoValidator.validateConstructComponentSlotAnnotationDTO(existingComponentIds.get(identityHelper.constructComponentDtoIdentity(componentDTO)), componentDTO);
				if (componentResponse.hasErrors()) {
					constructResponse.addErrorMessage("construct_component_dtos", componentResponse.errorMessagesString());
				} else {
					ConstructComponentSlotAnnotation component = componentResponse.getEntity();
					components.add(component);
					if (component.getId() != null)
						componentIdsToKeep.add(component.getId());
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(construct.getConstructComponents())) {
			for (ConstructComponentSlotAnnotation cc : construct.getConstructComponents()) {
				if (!componentIdsToKeep.contains(cc.getId())) {
					cc.setSingleConstruct(null);
					constructComponentDAO.remove(cc.getId());
				}
			}
		}

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

		if (CollectionUtils.isNotEmpty(synonyms)) {
			for (ConstructSynonymSlotAnnotation syn : synonyms) {
				syn.setSingleConstruct(construct);
				constructSynonymDAO.persist(syn);
			}
		}
		construct.setConstructSynonyms(synonyms);
		
		if (CollectionUtils.isNotEmpty(components)) {
			for (ConstructComponentSlotAnnotation cc : components) {
				cc.setSingleConstruct(construct);
				constructComponentDAO.persist(cc);
			}
		}
		construct.setConstructComponents(components);

		return construct;
	}
}
