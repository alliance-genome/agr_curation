package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.ExternalDataBaseEntityDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.ingest.dto.ExternalDataBaseEntityDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * SCRUM-6714: direct-submission validator for ExternalDataBaseEntity. Simpler than the FMS
 * equivalent (ExternalDataBaseEntityFmsDTOValidator) - cross references are revalidated fresh each
 * time via the shared CrossReferenceDTOValidator rather than merged/deduped against FMS's richer id
 * list shape.
 */
@RequestScoped
public class ExternalDataBaseEntityDTOValidator extends AuditedObjectDTOValidator<ExternalDataBaseEntity, ExternalDataBaseEntityDTO> {

	@Inject ExternalDataBaseEntityDAO externalDataBaseEntityDAO;
	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject CrossReferenceDTOValidator crossReferenceDtoValidator;

	@Transactional
	public ObjectResponse<ExternalDataBaseEntity> validateExternalDataBaseEntityDTO(ExternalDataBaseEntityDTO dto) {
		response = new ObjectResponse<>();

		if (StringUtils.isBlank(dto.getCurie())) {
			response.addErrorMessage("curie", ValidationConstants.REQUIRED_MESSAGE);
			return response;
		}

		ExternalDataBaseEntity entity = externalDataBaseEntityService.findByCurie(dto.getCurie());
		if (entity == null) {
			entity = new ExternalDataBaseEntity();
			entity.setCurie(dto.getCurie());
		}

		entity = validateAuditedObjectDTO(entity, dto);

		entity.setSecondaryIdentifiers(handleStringListField(dto.getSecondaryIdentifiers()));

		if (dto.getPreferredCrossReferenceDto() != null) {
			ObjectResponse<CrossReference> xrefResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(dto.getPreferredCrossReferenceDto(), entity.getPreferredCrossReference());
			if (xrefResponse.hasErrors()) {
				response.addErrorMessage("preferred_cross_reference_dto", xrefResponse.errorMessagesString());
			} else {
				entity.setPreferredCrossReference(xrefResponse.getEntity());
			}
		} else {
			entity.setPreferredCrossReference(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getCrossReferenceDtos())) {
			List<CrossReference> crossReferences = new ArrayList<>();
			for (int ix = 0; ix < dto.getCrossReferenceDtos().size(); ix++) {
				ObjectResponse<CrossReference> xrefResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(dto.getCrossReferenceDtos().get(ix), null);
				if (xrefResponse.hasErrors()) {
					response.addErrorMessage("cross_reference_dtos", xrefResponse.errorMessagesString());
				} else {
					crossReferences.add(xrefResponse.getEntity());
				}
			}
			entity.setCrossReferences(crossReferences);
		} else {
			entity.setCrossReferences(null);
		}

		if (response.hasErrors()) {
			return response;
		}

		response.setEntity(externalDataBaseEntityDAO.persist(entity));
		return response;
	}

}
