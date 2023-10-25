package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleSynonymSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	public ObjectResponse<AlleleSynonymSlotAnnotation> validateAlleleSynonymSlotAnnotationDTO(AlleleSynonymSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		if (annotation == null)
			annotation = new AlleleSynonymSlotAnnotation();

		return validateNameSlotAnnotationDTO(annotation, dto,VocabularyConstants.NAME_TYPE_VOCABULARY);
	}
}
