package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleSymbolSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	public ObjectResponse<AlleleSymbolSlotAnnotation> validateAlleleSymbolSlotAnnotationDTO(AlleleSymbolSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		if (annotation == null)
			annotation = new AlleleSymbolSlotAnnotation();

		return validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.SYMBOL_NAME_TYPE_TERM_SET);
	}
}
