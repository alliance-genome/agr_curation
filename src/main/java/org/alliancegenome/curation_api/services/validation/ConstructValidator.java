package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.constructs.ConstructUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class ConstructValidator extends ReagentValidator {

	@Inject
	ConstructDAO constructDAO;
	@Inject
	ConstructComponentSlotAnnotationDAO constructComponentDAO;
	@Inject
	ConstructComponentSlotAnnotationValidator constructComponentValidator;
	@Inject
	ReferenceValidator referenceValidator;
	@Inject
	ConstructSymbolSlotAnnotationDAO constructSymbolDAO;
	@Inject
	ConstructFullNameSlotAnnotationDAO constructFullNameDAO;
	@Inject
	ConstructSynonymSlotAnnotationDAO constructSynonymDAO;
	@Inject
	ConstructSymbolSlotAnnotationValidator constructSymbolValidator;
	@Inject
	ConstructFullNameSlotAnnotationValidator constructFullNameValidator;
	@Inject
	ConstructSynonymSlotAnnotationValidator constructSynonymValidator;
	
	private String errorMessage;

	public Construct validateConstructUpdate(Construct uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Construct: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Construct ID provided");
			throw new ApiErrorException(response);
		}
		Construct dbEntity = constructDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Construct with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Construct ID has not been
			// found
		}

		return validateConstruct(uiEntity, dbEntity);
	}

	public Construct validateConstructCreate(Construct uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Cound not create Construct";

		Construct dbEntity = new Construct();

		return validateConstruct(uiEntity, dbEntity);
	}

	public Construct validateConstruct(Construct uiEntity, Construct dbEntity) {

		List<String> previousReferenceCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getReferences()))
			previousReferenceCuries = dbEntity.getReferences().stream().map(Reference::getCurie).collect(Collectors.toList());
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
		
		dbEntity = (Construct) validateCommonReagentFields(uiEntity, dbEntity);
		
		removeUnusedSlotAnnotations(uiEntity, dbEntity);
		
		ConstructSymbolSlotAnnotation symbol = validateConstructSymbol(uiEntity, dbEntity);
		ConstructFullNameSlotAnnotation fullName = validateConstructFullName(uiEntity, dbEntity);
		List<ConstructSynonymSlotAnnotation> synonyms = validateConstructSynonyms(uiEntity, dbEntity);
		
		List<ConstructComponentSlotAnnotation> components = validateConstructComponents(uiEntity, dbEntity);
		
		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}
		
		dbEntity = constructDAO.persist(dbEntity);
		
		if (symbol != null) {
			symbol.setSingleConstruct(dbEntity);
			constructSymbolDAO.persist(symbol);
		}
		dbEntity.setConstructSymbol(symbol);

		if (fullName != null) {
			fullName.setSingleConstruct(dbEntity);
			constructFullNameDAO.persist(fullName);
		}
		dbEntity.setConstructFullName(fullName);

		if (synonyms != null) {
			for (ConstructSynonymSlotAnnotation syn : synonyms) {
				syn.setSingleConstruct(dbEntity);
				constructSynonymDAO.persist(syn);
			}
		}
		dbEntity.setConstructSynonyms(synonyms);
		
		if (components != null) {
			for (ConstructComponentSlotAnnotation cc : components) {
				cc.setSingleConstruct(dbEntity);
				constructComponentDAO.persist(cc);
			}
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

	public String validateUniqueId(Construct uiEntity, Construct dbEntity) {
		
		if (dbEntity.getDataProvider() == null)
			return null;
		
		String uniqueId = ConstructUniqueIdHelper.getConstructUniqueId(uiEntity);

		if (dbEntity.getUniqueId() == null || !uniqueId.equals(dbEntity.getUniqueId())) {
			SearchResponse<Construct> response = constructDAO.findByField("uniqueId", uniqueId);
			if (response != null) {
				addMessageResponse("uniqueId", ValidationConstants.NON_UNIQUE_MESSAGE);
				return null;
			}
		}

		return uniqueId;
	}
	
	private ConstructSymbolSlotAnnotation validateConstructSymbol(Construct uiEntity, Construct dbEntity) {
		String field = "constructSymbol";

		if (uiEntity.getConstructSymbol() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<ConstructSymbolSlotAnnotation> symbolResponse = constructSymbolValidator.validateConstructSymbolSlotAnnotation(uiEntity.getConstructSymbol());
		if (symbolResponse.getEntity() == null) {
			addMessageResponse(field, symbolResponse.errorMessagesString());
			return null;
		}

		return symbolResponse.getEntity();
	}

	private ConstructFullNameSlotAnnotation validateConstructFullName(Construct uiEntity, Construct dbEntity) {
		if (uiEntity.getConstructFullName() == null)
			return null;

		String field = "constructFullName";

		ObjectResponse<ConstructFullNameSlotAnnotation> nameResponse = constructFullNameValidator.validateConstructFullNameSlotAnnotation(uiEntity.getConstructFullName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<ConstructSynonymSlotAnnotation> validateConstructSynonyms(Construct uiEntity, Construct dbEntity) {
		String field = "constructSynonyms";

		List<ConstructSynonymSlotAnnotation> validatedSynonyms = new ArrayList<ConstructSynonymSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructSynonyms())) {
			for (ConstructSynonymSlotAnnotation syn : uiEntity.getConstructSynonyms()) {
				ObjectResponse<ConstructSynonymSlotAnnotation> synResponse = constructSynonymValidator.validateConstructSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					addMessageResponse(field, synResponse.errorMessagesString());
					return null;
				}
				syn = synResponse.getEntity();
				validatedSynonyms.add(syn);
			}
		}

		if (CollectionUtils.isEmpty(validatedSynonyms))
			return null;

		return validatedSynonyms;
	}
	
	private List<ConstructComponentSlotAnnotation> validateConstructComponents(Construct uiEntity, Construct dbEntity) {
		String field = "constructComponents";

		List<ConstructComponentSlotAnnotation> validatedComponents = new ArrayList<ConstructComponentSlotAnnotation>();
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructComponents())) {
			for (ConstructComponentSlotAnnotation cc : uiEntity.getConstructComponents()) {
				ObjectResponse<ConstructComponentSlotAnnotation> ccResponse = constructComponentValidator.validateConstructComponentSlotAnnotation(cc);
				if (ccResponse.getEntity() == null) {
					addMessageResponse(field, ccResponse.errorMessagesString());
					return null;
				}
				cc = ccResponse.getEntity();
				validatedComponents.add(cc);
			}
		}

		if (CollectionUtils.isEmpty(validatedComponents))
			return null;

		return validatedComponents;
	}
	
	private void removeUnusedSlotAnnotations(Construct uiEntity, Construct dbEntity) {
		if (dbEntity.getConstructSymbol() != null)
			removeUnusedConstructSymbol(uiEntity, dbEntity);

		if (dbEntity.getConstructFullName() != null)
			removeUnusedConstructFullName(uiEntity, dbEntity);

		if (CollectionUtils.isNotEmpty(dbEntity.getConstructSynonyms()))
			removeUnusedConstructSynonyms(uiEntity, dbEntity);
		
		if (CollectionUtils.isNotEmpty(dbEntity.getConstructComponents()))
			removeUnusedConstructComponents(uiEntity, dbEntity);
	}
	
	private void removeUnusedConstructSymbol(Construct uiEntity, Construct dbEntity) {
		Long reusedId = uiEntity.getConstructSymbol() == null ? null : uiEntity.getConstructSymbol().getId();
		ConstructSymbolSlotAnnotation previousSymbol = dbEntity.getConstructSymbol();

		if (previousSymbol != null && (reusedId == null || !previousSymbol.getId().equals(reusedId))) {
			previousSymbol.setSingleConstruct(null);
			constructSymbolDAO.remove(previousSymbol.getId());
		}
	}
	
	private void removeUnusedConstructFullName(Construct uiEntity, Construct dbEntity) {
		Long reusedId = uiEntity.getConstructFullName() == null ? null : uiEntity.getConstructFullName().getId();
		ConstructFullNameSlotAnnotation previousFullName = dbEntity.getConstructFullName();

		if (previousFullName != null && (reusedId == null || !previousFullName.getId().equals(reusedId))) {
			previousFullName.setSingleConstruct(null);
			constructFullNameDAO.remove(previousFullName.getId());
		}
	}

	private void removeUnusedConstructSynonyms(Construct uiEntity, Construct dbEntity) {
		List<Long> reusedIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructSynonyms()))
			reusedIds = uiEntity.getConstructSynonyms().stream().map(ConstructSynonymSlotAnnotation::getId).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(dbEntity.getConstructSynonyms())) {
			for (ConstructSynonymSlotAnnotation previousSynonym : dbEntity.getConstructSynonyms()) {
				if (!reusedIds.contains(previousSynonym.getId())) {
					previousSynonym.setSingleConstruct(null);
					constructSynonymDAO.remove(previousSynonym.getId());
				}
			}
		}
	}
	
	private void removeUnusedConstructComponents(Construct uiEntity, Construct dbEntity) {
		List<Long> reusedIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructComponents()))
			reusedIds = uiEntity.getConstructComponents().stream().map(ConstructComponentSlotAnnotation::getId).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(dbEntity.getConstructComponents())) {
			for (ConstructComponentSlotAnnotation previousComponent : dbEntity.getConstructComponents()) {
				if (!reusedIds.contains(previousComponent.getId())) {
					previousComponent.setSingleConstruct(null);
					constructComponentDAO.remove(previousComponent.getId());
				}
			}
		}
	}
}
