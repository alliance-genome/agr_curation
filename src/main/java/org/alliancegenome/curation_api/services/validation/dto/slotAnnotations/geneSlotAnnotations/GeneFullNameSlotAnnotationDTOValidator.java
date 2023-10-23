package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

@RequestScoped
public class GeneFullNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	public ObjectResponse<GeneFullNameSlotAnnotation> validateGeneFullNameSlotAnnotationDTO(GeneFullNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		if (annotation == null)
			annotation = new GeneFullNameSlotAnnotation();

		return validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.FULL_NAME_TYPE_TERM_SET);
	}
}
