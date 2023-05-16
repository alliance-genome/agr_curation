package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SecondaryIdSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleSecondaryIdSlotAnnotationDTOValidator extends SecondaryIdSlotAnnotationDTOValidator {

	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> validateAlleleSecondaryIdSlotAnnotationDTO(AlleleSecondaryIdSlotAnnotation annotation, SecondaryIdSlotAnnotationDTO dto) {
		ObjectResponse<AlleleSecondaryIdSlotAnnotation> asidResponse = new ObjectResponse<AlleleSecondaryIdSlotAnnotation>();

		if (annotation == null)
			annotation = new AlleleSecondaryIdSlotAnnotation();

		ObjectResponse<AlleleSecondaryIdSlotAnnotation> saResponse = validateSecondaryIdSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		asidResponse.addErrorMessages(saResponse.getErrorMessages());

		asidResponse.setEntity(annotation);

		return asidResponse;
	}
}
