package org.alliancegenome.curation_api.services.validation.associations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class EvidenceAssociationValidator<E extends EvidenceAssociation> extends AuditedObjectValidator<E> {

	@Inject ReferenceService referenceService;

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

	public E validateEvidenceAssociationFields(E uiEntity, E dbEntity) {
		Boolean newEntity = false;
		if (dbEntity.getId() == null) {
			newEntity = true;
		}
		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		List<Reference> evidence = validateEvidence(uiEntity, dbEntity);
		dbEntity.setEvidence(evidence);

		return dbEntity;
	}
}
