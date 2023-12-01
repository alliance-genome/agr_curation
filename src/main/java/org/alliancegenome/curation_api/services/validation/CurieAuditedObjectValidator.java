package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.base.CurieObject;
import org.apache.commons.lang3.StringUtils;

public class CurieAuditedObjectValidator extends AuditedObjectValidator<CurieObject> {

	public String validateCurie(CurieObject uiEntity) {
		String curie = uiEntity.getCurie();
		if (StringUtils.isBlank(curie)) {
			addMessageResponse("curie", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		return curie;
	}

}
