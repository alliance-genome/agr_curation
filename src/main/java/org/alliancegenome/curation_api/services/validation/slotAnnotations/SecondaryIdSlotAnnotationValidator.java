package org.alliancegenome.curation_api.services.validation.slotAnnotations;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.apache.commons.lang3.StringUtils;

public class SecondaryIdSlotAnnotationValidator<E extends SecondaryIdSlotAnnotation> extends SlotAnnotationValidator<E> {

	public E validateSecondaryIdSlotAnnotationFields(E uiEntity, E dbEntity, Boolean newEntity) {

		dbEntity = validateSlotAnnotationFields(uiEntity, dbEntity, newEntity);

		if (StringUtils.isBlank(uiEntity.getSecondaryId())) {
			addMessageResponse("secondaryId", ValidationConstants.REQUIRED_MESSAGE);
			dbEntity.setSecondaryId(null);
		} else {
			dbEntity.setSecondaryId(uiEntity.getSecondaryId());
		}

		return dbEntity;
	}
}
