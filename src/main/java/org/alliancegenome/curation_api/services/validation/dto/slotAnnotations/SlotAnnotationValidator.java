package org.alliancegenome.curation_api.services.validation.dto.slotAnnotations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class SlotAnnotationValidator<E extends SlotAnnotation> extends AuditedObjectValidator<E> {

	@Inject ReferenceService referenceService;

	public E validateSlotAnnotationFields(E uiEntity, E dbEntity, Boolean newObject) {
		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, newObject);

		List<Reference> evidence = validateEvidence(uiEntity, dbEntity);
		dbEntity.setEvidence(evidence);

		return dbEntity;
	}

	public List<Reference> validateEvidence(E uiEntity, E dbEntity) {
		String field = "evidence";

		if (CollectionUtils.isEmpty(uiEntity.getEvidence())) {
			return null;
		}

		List<Reference> validatedEntities = new ArrayList<>();
		for (Reference evidenceEntity : uiEntity.getEvidence()) {
			evidenceEntity = referenceService.retrieveFromDbOrLiteratureService(evidenceEntity.getCurie());
			if (evidenceEntity == null) {
				response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (evidenceEntity.getObsolete() && (CollectionUtils.isEmpty(dbEntity.getEvidence()) || !dbEntity.getEvidence().contains(evidenceEntity))) {
				response.addErrorMessage(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			validatedEntities.add(evidenceEntity);
		}

		return validatedEntities;
	}

}
