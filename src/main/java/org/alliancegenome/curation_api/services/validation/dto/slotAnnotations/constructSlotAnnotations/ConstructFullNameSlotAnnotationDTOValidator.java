package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ConstructFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	public ObjectResponse<ConstructFullNameSlotAnnotation> validateConstructFullNameSlotAnnotationDTO(ConstructFullNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		if (annotation == null)
			annotation = new ConstructFullNameSlotAnnotation();

		return validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.FULL_NAME_TYPE_TERM_SET);
	}
}
