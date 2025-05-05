package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AgmSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<AgmSynonymSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<AgmSynonymSlotAnnotation> validateAgmSynonymSlotAnnotationDTO(AgmSynonymSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<AgmSynonymSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new AgmSynonymSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.NAME_TYPE_VOCABULARY);
		
		response.setEntity(annotation);
		return response;
	}
}