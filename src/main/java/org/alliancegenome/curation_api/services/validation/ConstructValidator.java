package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleMutationTypeSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.constructs.ConstructUniqueIdHelper;
import org.alliancegenome.curation_api.services.validation.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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

		String name = validateName(uiEntity);
		dbEntity.setName(name);
		
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
		
		List<ConstructComponentSlotAnnotation> components = validateConstructComponents(uiEntity, dbEntity);
		
		String uniqueId = validateUniqueId(uiEntity, dbEntity);
		dbEntity.setUniqueId(uniqueId);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}
		
		dbEntity = constructDAO.persist(dbEntity);
		
		if (components != null) {
			for (ConstructComponentSlotAnnotation cc : components) {
				cc.setSingleConstruct(dbEntity);
				constructComponentDAO.persist(cc);
			}
		}

		return dbEntity;
	}
	
	public String validateName(Construct uiEntity) {
		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse("name", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		
		return uiEntity.getName();
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
		if (CollectionUtils.isNotEmpty(dbEntity.getConstructComponents()))
			removeUnusedConstructComponents(uiEntity, dbEntity);
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
