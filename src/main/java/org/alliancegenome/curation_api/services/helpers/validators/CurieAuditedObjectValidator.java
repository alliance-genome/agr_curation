package org.alliancegenome.curation_api.services.helpers.validators;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.base.CurieAuditedObject;
import org.apache.commons.lang3.StringUtils;

public class CurieAuditedObjectValidator extends AuditedObjectValidator<CurieAuditedObject> {
	
	public String validateCurie(CurieAuditedObject uiEntity) {
		String curie = uiEntity.getCurie();
		if (StringUtils.isBlank(curie)) {
			addMessageResponse("curie", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		return curie;
	}
	
}
