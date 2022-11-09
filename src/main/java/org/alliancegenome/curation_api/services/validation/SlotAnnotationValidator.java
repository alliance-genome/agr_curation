package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.SlotAnnotation;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.apache.commons.collections.CollectionUtils;

public class SlotAnnotationValidator extends AuditedObjectValidator<SlotAnnotation> {

	@Inject InformationContentEntityService informationContentEntityService;
	
	public List<InformationContentEntity> validateEvidence(SlotAnnotation uiEntity, SlotAnnotation dbEntity) {
		String field = "evidence";
		
		if (CollectionUtils.isEmpty(uiEntity.getEvidence()))
			return null;
		
		List<InformationContentEntity> validatedEntities = new ArrayList<>();
		for (InformationContentEntity evidenceEntity : uiEntity.getEvidence()) {
			evidenceEntity = informationContentEntityService.retrieveFromDbOrLiteratureService(evidenceEntity.getCurie());
			if (evidenceEntity == null) {
				response.addErrorMessage(field, ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (evidenceEntity.getObsolete() &&
					(CollectionUtils.isEmpty(dbEntity.getEvidence()) || !dbEntity.getEvidence().contains(evidenceEntity))) {
				response.addErrorMessage(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			validatedEntities.add(evidenceEntity);
		}
		
		return validatedEntities;
	}

}
