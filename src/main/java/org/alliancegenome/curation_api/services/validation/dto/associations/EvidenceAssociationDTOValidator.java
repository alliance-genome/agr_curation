package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.associations.EvidenceAssociationDTO;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class EvidenceAssociationDTOValidator<E extends EvidenceAssociation, D extends EvidenceAssociationDTO> extends AuditedObjectDTOValidator<E, D> {

	@Inject
	ReferenceService referenceService;

	public E validateEvidenceAssociationDTO(E association, D dto) {
		association = validateAuditedObjectDTO(association, dto);
		
		if (CollectionUtils.isNotEmpty(dto.getEvidenceCuries())) {
			List<Reference> evidence = new ArrayList<>();
			for (String evidenceCurie : dto.getEvidenceCuries()) {
				Reference evidenceEntity = referenceService.retrieveFromDbOrLiteratureService(evidenceCurie);
				if (evidenceEntity == null) {
					response.addErrorMessage("evidence_curies", ValidationConstants.INVALID_MESSAGE + " (" + evidenceCurie + ")");
					break;
				}
				evidence.add(evidenceEntity);
			}
			association.setEvidence(evidence);
		} else {
			association.setEvidence(null);
		}

		return association;
	}
}
