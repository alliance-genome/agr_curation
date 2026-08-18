package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class TransgenicToolFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<TransgenicToolFullNameSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<TransgenicToolFullNameSlotAnnotation> validateTransgenicToolFullNameSlotAnnotationDTO(TransgenicToolFullNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<TransgenicToolFullNameSlotAnnotation>();

		if (annotation == null) {
			annotation = new TransgenicToolFullNameSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.FULL_NAME_TYPE_TERM_SET);

		response.setEntity(annotation);
		return response;
	}
}
