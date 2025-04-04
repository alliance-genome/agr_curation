package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class GeneFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<GeneFullNameSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<GeneFullNameSlotAnnotation> validateGeneFullNameSlotAnnotationDTO(GeneFullNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<GeneFullNameSlotAnnotation>();
		
		if (annotation == null) {
			annotation = new GeneFullNameSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.FULL_NAME_TYPE_TERM_SET);
		
		response.setEntity(annotation);
		return response;
	}
}