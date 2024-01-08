package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class SecondaryIdSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator {

	public <E extends SecondaryIdSlotAnnotation> ObjectResponse<E> validateSecondaryIdSlotAnnotationDTO(E annotation, SecondaryIdSlotAnnotationDTO dto) {
		ObjectResponse<E> sidResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = sidResponse.getEntity();

		if (StringUtils.isBlank(dto.getSecondaryId())) {
			sidResponse.addErrorMessage("secondary_id", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			annotation.setSecondaryId(dto.getSecondaryId());
		}

		sidResponse.setEntity(annotation);

		return sidResponse;
	}
}
