package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class TransgenicToolSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<TransgenicToolSynonymSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<TransgenicToolSynonymSlotAnnotation> validateTransgenicToolSynonymSlotAnnotationDTO(TransgenicToolSynonymSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<TransgenicToolSynonymSlotAnnotation>();

		if (annotation == null) {
			annotation = new TransgenicToolSynonymSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.NAME_TYPE_VOCABULARY);

		response.setEntity(annotation);
		return response;
	}
}
