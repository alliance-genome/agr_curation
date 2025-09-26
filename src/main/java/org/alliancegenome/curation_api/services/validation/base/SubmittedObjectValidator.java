package org.alliancegenome.curation_api.services.validation.base;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.apache.commons.lang3.StringUtils;

public class SubmittedObjectValidator<E extends SubmittedObject> extends AuditedObjectValidator<SubmittedObject> {

	public E validateSubmittedObjectFields(E uiEntity, E dbEntity, String noteTypeVocabularyTermSet) {
		Boolean newEntity = false;
		if (dbEntity.getId() == null) {
			newEntity = true;
		}
		
		dbEntity = (E) validateAuditedObjectFields(uiEntity, dbEntity, newEntity);
		
		String curie = handleStringField(uiEntity.getCurie());
		dbEntity.setCurie(curie);

		String primaryExternalId = handleStringField(uiEntity.getPrimaryExternalId());
		dbEntity.setPrimaryExternalId(primaryExternalId);

		String modInternalId = validateModInternalId(uiEntity);
		dbEntity.setModInternalId(modInternalId);

		Organization dataProvider = validateDataProvider(uiEntity.getDataProvider(), dbEntity.getDataProvider(), newEntity);
		dbEntity.setDataProvider(dataProvider);
		
		CrossReference dataProviderXref = validateDataProviderCrossReference(uiEntity.getDataProviderCrossReference(), dbEntity.getDataProviderCrossReference());
		dbEntity.setDataProviderCrossReference(dataProviderXref);
		
		List<Note> relatedNotes = validateRelatedNotes(uiEntity.getRelatedNotes(), noteTypeVocabularyTermSet);
		if (dbEntity.getRelatedNotes() != null) {
			dbEntity.getRelatedNotes().clear();
		}
		if (relatedNotes != null) {
			if (dbEntity.getRelatedNotes() == null) {
				dbEntity.setRelatedNotes(new ArrayList<>());
			}
			dbEntity.getRelatedNotes().addAll(relatedNotes);
		}
		
		return dbEntity;
	}

	public String validateCurie(E uiEntity) {
		String curie = handleStringField(uiEntity.getCurie());
		if (curie != null && !curie.startsWith("AGRKB:")) {
			addMessageResponse("curie", ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		return curie;
	}

	public String validateModInternalId(E uiEntity) {
		String modInternalId = uiEntity.getModInternalId();
		if (StringUtils.isBlank(modInternalId)) {
			if (StringUtils.isBlank(uiEntity.getPrimaryExternalId())) {
				addMessageResponse("modInternalId", ValidationConstants.REQUIRED_UNLESS_OTHER_FIELD_POPULATED_MESSAGE + "primaryExternalId");
			}
			return null;
		}
		return modInternalId;
	}
}
