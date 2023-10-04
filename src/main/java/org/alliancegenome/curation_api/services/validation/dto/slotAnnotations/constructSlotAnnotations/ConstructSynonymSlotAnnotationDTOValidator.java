package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.constructSlotAnnotations;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations.ConstructSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

@RequestScoped
public class ConstructSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	public ObjectResponse<ConstructSynonymSlotAnnotation> validateConstructSynonymSlotAnnotationDTO(ConstructSynonymSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		if (annotation == null)
			annotation = new ConstructSynonymSlotAnnotation();

		return validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.NAME_TYPE_VOCABULARY);
	}
}
