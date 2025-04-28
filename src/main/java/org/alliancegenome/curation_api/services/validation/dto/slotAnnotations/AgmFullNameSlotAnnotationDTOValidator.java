package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AgmFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<AgmFullNameSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<AgmFullNameSlotAnnotation> validateAgmFullNameSlotAnnotationDTO(AgmFullNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<AgmFullNameSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AgmFullNameSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.FULL_NAME_TYPE_TERM_SET);
		
		response.setEntity(annotation);
		return response;
	}
}