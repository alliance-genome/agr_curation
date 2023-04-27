package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SlotAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.alliancegenome.curation_api.services.validation.dto.base.BaseDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class SlotAnnotationDTOValidator extends BaseDTOValidator {

	@Inject
	InformationContentEntityService informationContentEntityService;

	public <E extends SlotAnnotation, D extends SlotAnnotationDTO> ObjectResponse<E> validateSlotAnnotationDTO(E annotation, D dto) {
		ObjectResponse<E> saResponse = validateAuditedObjectDTO(annotation, dto);
		annotation = saResponse.getEntity();

		List<InformationContentEntity> evidence = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getEvidenceCuries())) {
			for (String evidenceCurie : dto.getEvidenceCuries()) {
				InformationContentEntity evidenceEntity = informationContentEntityService.retrieveFromDbOrLiteratureService(evidenceCurie);
				if (evidenceEntity == null) {
					saResponse.addErrorMessage("evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + evidenceCurie + ")");
					break;
				}
				evidence.add(evidenceEntity);
			}
			annotation.setEvidence(evidence);
		} else {
			annotation.setEvidence(null);
		}

		saResponse.setEntity(annotation);
		return saResponse;
	}
}
