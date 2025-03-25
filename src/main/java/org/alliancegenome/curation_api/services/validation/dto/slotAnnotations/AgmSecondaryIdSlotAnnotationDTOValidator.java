package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AgmSecondaryIdSlotAnnotationDTOValidator extends SecondaryIdSlotAnnotationDTOValidator<AgmSecondaryIdSlotAnnotation, SecondaryIdSlotAnnotationDTO> {

	public ObjectResponse<AgmSecondaryIdSlotAnnotation> validateAgmSecondaryIdSlotAnnotationDTO(AgmSecondaryIdSlotAnnotation annotation, SecondaryIdSlotAnnotationDTO dto) {
		response = new ObjectResponse<AgmSecondaryIdSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AgmSecondaryIdSlotAnnotation();
		}

		annotation = (AgmSecondaryIdSlotAnnotation) validateSecondaryIdSlotAnnotationDTO(annotation, dto);
		
		response.setEntity(annotation);
		return response;
	}
}