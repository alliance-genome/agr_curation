package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import javax.enterprise.context.RequestScoped;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleSecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.SlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleSecondaryIdSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	public ObjectResponse<AlleleSecondaryIdSlotAnnotation> validateAlleleSecondaryIdSlotAnnotationDTO(AlleleSecondaryIdSlotAnnotationDTO dto) {
		ObjectResponse<AlleleSecondaryIdSlotAnnotation> asidResponse = new ObjectResponse<AlleleSecondaryIdSlotAnnotation>();

		AlleleSecondaryIdSlotAnnotation annotation = new AlleleSecondaryIdSlotAnnotation();

		if (StringUtils.isBlank(dto.getSecondaryId())) {
			asidResponse.addErrorMessage("secondary_id", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			annotation.setSecondaryId(dto.getSecondaryId());
		}

		ObjectResponse<AlleleSecondaryIdSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		asidResponse.addErrorMessages(saResponse.getErrorMessages());

		asidResponse.setEntity(annotation);

		return asidResponse;
	}
}
