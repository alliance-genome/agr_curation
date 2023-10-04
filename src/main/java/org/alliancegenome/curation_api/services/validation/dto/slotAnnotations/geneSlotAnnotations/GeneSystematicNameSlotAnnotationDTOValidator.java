package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.geneSlotAnnotations;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSystematicNameSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;

@RequestScoped
public class GeneSystematicNameSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	public ObjectResponse<GeneSystematicNameSlotAnnotation> validateGeneSystematicNameSlotAnnotationDTO(GeneSystematicNameSlotAnnotation annotation, NameSlotAnnotationDTO dto) {
		if (annotation == null)
			annotation = new GeneSystematicNameSlotAnnotation();

		return validateNameSlotAnnotationDTO(annotation, dto, VocabularyConstants.SYSTEMATIC_NAME_TYPE_TERM_SET);
	}
}
