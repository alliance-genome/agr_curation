package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.model.entities.BioSampleAge;
import org.alliancegenome.curation_api.model.entities.TemporalContext;
import org.alliancegenome.curation_api.model.ingest.dto.BioSampleAgeDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6715: direct-submission validator for BioSampleAge, a cascade-owned sub-object (no
 * independent CRUD) currently only reachable from HTPExpressionDatasetSampleAnnotationDTO.
 */
@RequestScoped
public class BioSampleAgeDTOValidator extends AuditedObjectDTOValidator<BioSampleAge, BioSampleAgeDTO> {

	@Inject TemporalContextDTOValidator temporalContextDtoValidator;

	public ObjectResponse<BioSampleAge> validateBioSampleAgeDTO(BioSampleAgeDTO dto, BioSampleAge bioSampleAge) {
		response = new ObjectResponse<>();

		if (bioSampleAge == null) {
			bioSampleAge = new BioSampleAge();
		}

		bioSampleAge = validateAuditedObjectDTO(bioSampleAge, dto);

		if (dto.getStageDto() != null) {
			ObjectResponse<TemporalContext> stageResponse = temporalContextDtoValidator.validateTemporalContextDTO(dto.getStageDto(), bioSampleAge.getStage());
			if (stageResponse.hasErrors()) {
				response.addErrorMessage("stage_dto", stageResponse.errorMessagesString());
			} else {
				bioSampleAge.setStage(stageResponse.getEntity());
			}
		} else {
			bioSampleAge.setStage(null);
		}

		bioSampleAge.setAge(handleStringField(dto.getAge()));
		bioSampleAge.setWhenExpressedStageName(handleStringField(dto.getWhenExpressedStageName()));

		response.setEntity(bioSampleAge);
		return response;
	}

}
