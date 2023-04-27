package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.alleleSlotAnnotations;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.alleleSlotAnnotations.AlleleGermlineTransmissionStatusSlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.NameSlotAnnotationDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleGermlineTransmissionStatusSlotAnnotationDTOValidator extends NameSlotAnnotationDTOValidator {

	@Inject
	VocabularyTermService vocabularyTermService;

	public ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> validateAlleleGermlineTransmissionStatusSlotAnnotationDTO(AlleleGermlineTransmissionStatusSlotAnnotation annotation, AlleleGermlineTransmissionStatusSlotAnnotationDTO dto) {
		ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> agtsResponse = new ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation>();

		if (annotation == null)
			annotation = new AlleleGermlineTransmissionStatusSlotAnnotation();

		ObjectResponse<AlleleGermlineTransmissionStatusSlotAnnotation> saResponse = validateSlotAnnotationDTO(annotation, dto);
		annotation = saResponse.getEntity();
		agtsResponse.addErrorMessages(saResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getGermlineTransmissionStatusName())) {
			VocabularyTerm gts = vocabularyTermService.getTermInVocabulary(VocabularyConstants.GERMLINE_TRANSMISSION_STATUS_VOCABULARY, dto.getGermlineTransmissionStatusName()).getEntity();
			if (gts == null)
				agtsResponse.addErrorMessage("germline_transmission_status_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGermlineTransmissionStatusName() + ")");
			annotation.setGermlineTransmissionStatus(gts);
		} else {
			agtsResponse.addErrorMessage("germline_transmission_status_name", ValidationConstants.REQUIRED_MESSAGE);
		}

		agtsResponse.setEntity(annotation);

		return agtsResponse;
	}
}
