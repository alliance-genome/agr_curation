package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.ontology.FBCVTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.TransgenicToolUseSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.TransgenicToolUseSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ontology.FbcvTermService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

/**
 * SCRUM-6535: validates the use(s) of a TransgenicTool.
 *
 * use_curies is required by LinkML, so an empty list is an error rather than simply leaving the
 * annotation with no uses - an annotation that names no use carries no information.
 */
@RequestScoped
public class TransgenicToolUseSlotAnnotationDTOValidator extends SlotAnnotationDTOValidator<TransgenicToolUseSlotAnnotation, TransgenicToolUseSlotAnnotationDTO> {

	@Inject FbcvTermService fbcvTermService;

	public ObjectResponse<TransgenicToolUseSlotAnnotation> validateTransgenicToolUseSlotAnnotationDTO(TransgenicToolUseSlotAnnotation annotation, TransgenicToolUseSlotAnnotationDTO dto) {
		response = new ObjectResponse<TransgenicToolUseSlotAnnotation>();

		if (annotation == null) {
			annotation = new TransgenicToolUseSlotAnnotation();
		}

		annotation = validateSlotAnnotationDTO(annotation, dto);

		if (CollectionUtils.isEmpty(dto.getUseCuries())) {
			response.addErrorMessage("use_curies", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			List<FBCVTerm> uses = validateRequiredOntologyTerms(fbcvTermService, "use_curies", dto.getUseCuries());
			annotation.setUses(uses);
		}

		response.setEntity(annotation);
		return response;
	}
}
