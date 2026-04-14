package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SlotAnnotationDTO;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;

import jakarta.inject.Inject;

public class SlotAnnotationDTOValidator<E extends SlotAnnotation, D extends SlotAnnotationDTO> extends AuditedObjectDTOValidator<E, D> {

	@Inject InformationContentEntityService informationContentEntityService;

	public E validateSlotAnnotationDTO(E annotation, D dto) {
		annotation = validateAuditedObjectDTO(annotation, dto);

		List<InformationContentEntity> evidence = validateOptionalEntities("evidence_curies", dto.getEvidenceCuries(), informationContentEntityService::retrieveFromDbOrLiteratureService);

		annotation.setEvidence(evidence);

		return annotation;
	}
}
