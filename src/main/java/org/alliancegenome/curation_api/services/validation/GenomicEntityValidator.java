package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.apache.commons.collections.CollectionUtils;

public class GenomicEntityValidator extends BiologicalEntityValidator {

	@Inject
	CrossReferenceValidator crossReferenceValidator;
	@Inject
	CrossReferenceService crossReferenceService;

	public List<CrossReference> validateCrossReferences(GenomicEntity uiEntity, GenomicEntity dbEntity) {
		String field = "crossReferences";

		List<CrossReference> validatedXrefs = new ArrayList<CrossReference>();
		if (CollectionUtils.isNotEmpty(uiEntity.getCrossReferences())) {
			for (CrossReference newXref : uiEntity.getCrossReferences()) {
				ObjectResponse<CrossReference> xrefResponse = crossReferenceValidator.validateCrossReference(newXref, false);
				if (xrefResponse.getEntity() == null) {
					addMessageResponse(field, xrefResponse.errorMessagesString());
					return null;
				}
				validatedXrefs.add(xrefResponse.getEntity());
			}
		}
		
		validatedXrefs = crossReferenceService.getMergedXrefList(validatedXrefs, dbEntity.getCrossReferences());

		if (CollectionUtils.isEmpty(validatedXrefs))
			return null;

		return validatedXrefs;
	}

}
