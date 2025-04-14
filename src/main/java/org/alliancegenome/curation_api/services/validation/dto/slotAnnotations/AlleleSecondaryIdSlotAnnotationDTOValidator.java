package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleSecondaryIdSlotAnnotationDTOValidator extends SecondaryIdSlotAnnotationDTOValidator<AlleleSecondaryIdSlotAnnotation, SecondaryIdSlotAnnotationDTO> {

	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> validateAlleleSecondaryIdSlotAnnotationDTO(AlleleSecondaryIdSlotAnnotation annotation, SecondaryIdSlotAnnotationDTO dto) {
		response = new ObjectResponse<AlleleSecondaryIdSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AlleleSecondaryIdSlotAnnotation();
		}

		annotation = (AlleleSecondaryIdSlotAnnotation) validateSecondaryIdSlotAnnotationDTO(annotation, dto);
		
		response.setEntity(annotation);

		return response;
	}
}