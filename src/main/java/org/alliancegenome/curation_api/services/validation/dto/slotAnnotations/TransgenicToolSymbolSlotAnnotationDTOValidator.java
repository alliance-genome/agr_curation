package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class TransgenicToolSymbolSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<TransgenicToolSymbolSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<TransgenicToolSymbolSlotAnnotation> validateTransgenicToolSymbolSlotAnnotationDTO(TransgenicToolSymbolSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<TransgenicToolSymbolSlotAnnotation>();

		if (annotation == null) {
			annotation = new TransgenicToolSymbolSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET);

		response.setEntity(annotation);
		return response;
	}
}
