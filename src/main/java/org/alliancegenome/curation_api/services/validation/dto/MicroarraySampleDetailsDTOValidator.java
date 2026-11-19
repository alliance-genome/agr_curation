package org.alliancegenome.curation_api.services.validation.dto;

import org.alliancegenome.curation_api.model.entities.MicroarraySampleDetails;
import org.alliancegenome.curation_api.model.ingest.dto.MicroarraySampleDetailsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;

import jakarta.enterprise.context.RequestScoped;

/**
 * SCRUM-6715: direct-submission validator for MicroarraySampleDetails, a cascade-owned sub-object
 * (no independent CRUD) currently only reachable from HTPExpressionDatasetSampleAnnotationDTO.
 */
@RequestScoped
public class MicroarraySampleDetailsDTOValidator extends AuditedObjectDTOValidator<MicroarraySampleDetails, MicroarraySampleDetailsDTO> {

	public ObjectResponse<MicroarraySampleDetails> validateMicroarraySampleDetailsDTO(MicroarraySampleDetailsDTO dto, MicroarraySampleDetails microarraySampleDetails) {
		response = new ObjectResponse<>();

		if (microarraySampleDetails == null) {
			microarraySampleDetails = new MicroarraySampleDetails();
		}

		microarraySampleDetails = validateAuditedObjectDTO(microarraySampleDetails, dto);

		microarraySampleDetails.setChannelId(handleStringField(dto.getChannelId()));
		microarraySampleDetails.setChannelNumber(dto.getChannelNumber());

		response.setEntity(microarraySampleDetails);
		return response;
	}

}
