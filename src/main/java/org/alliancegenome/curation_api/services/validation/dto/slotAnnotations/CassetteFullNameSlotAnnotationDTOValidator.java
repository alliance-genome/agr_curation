package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CassetteFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator<CassetteFullNameSlotAnnotation, NameSlotAnnotationDTO> {

	public ObjectResponse<CassetteFullNameSlotAnnotation> validateCassetteFullNameSlotAnnotationDTO(CassetteFullNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		response = new ObjectResponse<CassetteFullNameSlotAnnotation>();

		if (annotation == null) {
			annotation = new CassetteFullNameSlotAnnotation();
		}

		annotation = validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.FULL_NAME_TYPE_TERM_SET);

		response.setEntity(annotation);
		return response;
	}
}
