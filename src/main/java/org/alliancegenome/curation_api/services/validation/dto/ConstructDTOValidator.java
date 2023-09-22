package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ConstructDAO;
import org.alliancegenome.curation_api.dao.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.ConstructDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.constructs.ConstructUniqueIdHelper;
import org.alliancegenome.curation_api.services.helpers.slotAnnotations.SlotAnnotationIdentityHelper;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations.ConstructComponentSlotAnnotationDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class ConstructDTOValidator extends ReagentDTOValidator {

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
		
		List<Reference> references = null;
		List<String> refCuries = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getReferenceCuries())) {
			references = new ArrayList<>();
			for (String publicationId : dto.getReferenceCuries()) {
				Reference reference = referenceService.retrieveFromDbOrLiteratureService(publicationId);
				if (reference == null) {
					constructResponse.addErrorMessage("reference_curies", ValidationConstants.INVALID_MESSAGE + " (" + publicationId + ")");
				} else {
					references.add(reference);
					refCuries.add(reference.getCurie());
				}
			}
		} 
		
		String constructId;
		String identifyingField;
		String uniqueId = ConstructUniqueIdHelper.getConstructUniqueId(dto, refCuries);
		
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
		construct.setReferences(references);
		
		ObjectResponse<Construct> reagentResponse = validateReagentDTO(construct, dto);
		constructResponse.addErrorMessages(reagentResponse.getErrorMessages());
		construct = reagentResponse.getEntity();

		if (StringUtils.isBlank(dto.getName())) {
			constructResponse.addErrorMessage("name", ValidationConstants.REQUIRED_MESSAGE);;
		} else {
			construct.setName(dto.getName());
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

		if (constructResponse.hasErrors()) {
			log.info("ERROR: " + constructResponse.errorMessagesString());
			throw new ObjectValidationException(dto, constructResponse.errorMessagesString());
		}
		construct = constructDAO.persist(construct);

		// Attach construct and persist SlotAnnotation objects

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
