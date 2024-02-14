package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.associations.constructAssociations.ConstructGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.constructs.ConstructUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.associations.constructAssociations.ConstructGenomicEntityAssociationValidator;
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
	ConstructComponentSlotAnnotationValidator constructComponentValidator;
	@Inject
	ReferenceValidator referenceValidator;
	@Inject
	ConstructSymbolSlotAnnotationValidator constructSymbolValidator;
	@Inject
	ConstructFullNameSlotAnnotationValidator constructFullNameValidator;
	@Inject
	ConstructSynonymSlotAnnotationValidator constructSynonymValidator;
	@Inject
	ConstructGenomicEntityAssociationValidator constructGenomicEntityAssociationValidator;
	
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
		
		ConstructSymbolSlotAnnotation symbol = validateConstructSymbol(uiEntity, dbEntity);
		ConstructFullNameSlotAnnotation fullName = validateConstructFullName(uiEntity, dbEntity);
		List<ConstructSynonymSlotAnnotation> synonyms = validateConstructSynonyms(uiEntity, dbEntity);
		List<ConstructComponentSlotAnnotation> components = validateConstructComponents(uiEntity, dbEntity);
		
		List<ConstructGenomicEntityAssociation> geAssociations = validateConstructGenomicEntityAssociations(uiEntity, dbEntity);
		
		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		response.convertErrorMessagesToMap();
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}
		
		dbEntity = constructDAO.persist(dbEntity);
		
		if (symbol != null)
			symbol.setSingleConstruct(dbEntity);
		dbEntity.setConstructSymbol(symbol);

		if (fullName != null)
			fullName.setSingleConstruct(dbEntity);
		dbEntity.setConstructFullName(fullName);

		if (dbEntity.getConstructSynonyms() != null)
			dbEntity.getConstructSynonyms().clear();
		if (synonyms != null) {
			if (dbEntity.getConstructSynonyms() == null)
				dbEntity.setConstructSynonyms(new ArrayList<>());
			dbEntity.getConstructSynonyms().addAll(synonyms);
		}
		
		if (dbEntity.getConstructComponents() != null)
			dbEntity.getConstructComponents().clear();
		if (components != null) {
			if (dbEntity.getConstructComponents() == null)
				dbEntity.setConstructComponents(new ArrayList<>());
			dbEntity.getConstructComponents().addAll(components);
		}
		
		if (dbEntity.getConstructGenomicEntityAssociations() != null)
			dbEntity.getConstructGenomicEntityAssociations().clear();
		if (geAssociations != null) {
			if (dbEntity.getConstructGenomicEntityAssociations() == null)
				dbEntity.setConstructGenomicEntityAssociations(new ArrayList<>());
			dbEntity.getConstructGenomicEntityAssociations().addAll(geAssociations);
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
			response.addErrorMessages(field, symbolResponse.getErrorMessages());
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
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<ConstructSynonymSlotAnnotation> validateConstructSynonyms(Construct uiEntity, Construct dbEntity) {
		String field = "constructSynonyms";

		List<ConstructSynonymSlotAnnotation> validatedSynonyms = new ArrayList<ConstructSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructSynonyms())) {
			for (int ix = 0; ix < uiEntity.getConstructSynonyms().size(); ix++) { 
				ConstructSynonymSlotAnnotation syn = uiEntity.getConstructSynonyms().get(ix);
				ObjectResponse<ConstructSynonymSlotAnnotation> synResponse = constructSynonymValidator.validateConstructSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
					allValid = false;
				} else {
					syn = synResponse.getEntity();
					syn.setSingleConstruct(dbEntity);
					validatedSynonyms.add(syn);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSynonyms))
			return null;

		return validatedSynonyms;
	}
	
	private List<ConstructComponentSlotAnnotation> validateConstructComponents(Construct uiEntity, Construct dbEntity) {
		String field = "constructComponents";

		List<ConstructComponentSlotAnnotation> validatedComponents = new ArrayList<ConstructComponentSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructComponents())) {
			for (int ix = 0; ix < uiEntity.getConstructComponents().size(); ix++) { 
				ConstructComponentSlotAnnotation comp = uiEntity.getConstructComponents().get(ix);
				ObjectResponse<ConstructComponentSlotAnnotation> synResponse = constructComponentValidator.validateConstructComponentSlotAnnotation(comp);
				if (synResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
					allValid = false;
				} else {
					comp = synResponse.getEntity();
					comp.setSingleConstruct(dbEntity);
					validatedComponents.add(comp);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedComponents))
			return null;

		return validatedComponents;
	}
	
	private List<ConstructGenomicEntityAssociation> validateConstructGenomicEntityAssociations(Construct uiEntity, Construct dbEntity) {
		String field = "constructGenomicEntityAssociations";

		List<ConstructGenomicEntityAssociation> validatedAssociations = new ArrayList<ConstructGenomicEntityAssociation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getConstructGenomicEntityAssociations())) {
			for (int ix = 0; ix < uiEntity.getConstructGenomicEntityAssociations().size(); ix++) {
				ConstructGenomicEntityAssociation gea = uiEntity.getConstructGenomicEntityAssociations().get(ix);
				ObjectResponse<ConstructGenomicEntityAssociation> geaResponse = constructGenomicEntityAssociationValidator.validateConstructGenomicEntityAssociation(gea);
				if (geaResponse.getEntity() == null) {
					allValid = false;
					response.addErrorMessages(field, ix, geaResponse.getErrorMessages());
				} else {
					gea = geaResponse.getEntity();
					gea.setSubjectReagent(dbEntity);
					validatedAssociations.add(gea);
				}
			}
		}
		
		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedAssociations))
			return null;

		return validatedAssociations;
	}
	
}
