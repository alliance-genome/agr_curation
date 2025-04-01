package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.inject.Inject;

public class AnnotationValidator extends AuditedObjectValidator<Annotation> {

	@Inject InformationContentEntityValidator informationContentEntityValidator;
	@Inject ConditionRelationValidator conditionRelationValidator;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject AnnotationDAO annotationDAO;
	
	public List<ConditionRelation> validateConditionRelations(Annotation uiEntity, Annotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getConditionRelations())) {
			return null;
		}

		List<ConditionRelation> validatedConditionRelations = new ArrayList<>();
		List<Long> previousConditionRelationIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dbEntity.getConditionRelations())) {
			previousConditionRelationIds = dbEntity.getConditionRelations().stream().map(ConditionRelation::getId).collect(Collectors.toList());
		}

		for (ConditionRelation conditionRelation : uiEntity.getConditionRelations()) {
			if (uiEntity.getEvidenceItem()!= null && !StringUtils.isBlank(uiEntity.getEvidenceItem().getCurie()) && conditionRelation.getSingleReference() != null && !StringUtils.isBlank(conditionRelation.getSingleReference().getCurie())
				&& !conditionRelation.getSingleReference().getCurie().equals(uiEntity.getEvidenceItem().getCurie())) {
				addMessageResponse("conditionRelations", "singleReference - " + ValidationConstants.INVALID_MESSAGE);
			}

			if (conditionRelation.getObsolete() && !previousConditionRelationIds.contains(conditionRelation.getId())) {
				addMessageResponse("conditionRelations", ValidationConstants.OBSOLETE_MESSAGE);
			}

			ObjectResponse<ConditionRelation> crResponse = conditionRelationValidator.validateConditionRelation(conditionRelation);
			conditionRelation = crResponse.getEntity();
			if (conditionRelation == null) {
				addMessageResponse("conditionRelations", crResponse.errorMessagesString());
				return null;
			}

			// reuse existing condition relation
			SearchResponse<ConditionRelation> crSearchResponse = conditionRelationDAO.findByField("uniqueId", conditionRelation.getUniqueId());
			if (crSearchResponse != null && crSearchResponse.getSingleResult() != null) {
				conditionRelation.setId(crSearchResponse.getSingleResult().getId());
				conditionRelation = conditionRelationDAO.merge(conditionRelation);
			} else if (conditionRelation.getId() == null) {
				conditionRelation = conditionRelationDAO.persist(crResponse.getEntity());
			}
			validatedConditionRelations.add(crResponse.getEntity());
		}
		return validatedConditionRelations;
	}

	public InformationContentEntity validateEvidenceItem(Annotation uiEntity, Annotation dbEntity) {
		String field = "evidenceItem";
		if (uiEntity.getEvidenceItem() == null || StringUtils.isBlank(uiEntity.getEvidenceItem().getCurie())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		ObjectResponse<InformationContentEntity> evidenceItemResponse = informationContentEntityValidator.validateInformationContentEntity(uiEntity.getEvidenceItem());
		if (evidenceItemResponse.getEntity() == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (evidenceItemResponse.getEntity().getObsolete() && (dbEntity.getEvidenceItem() == null || !evidenceItemResponse.getEntity().getCurie().equals(dbEntity.getEvidenceItem().getCurie()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return evidenceItemResponse.getEntity();
	}

	public Annotation validateCommonAnnotationFields(Annotation uiEntity, Annotation dbEntity, String noteTypeSet) {
		Boolean newEntity = false;
		if (dbEntity.getId() == null) {
			newEntity = true;
		}
		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, newEntity);
		
		String primaryExternalId = handleStringField(uiEntity.getPrimaryExternalId());
		dbEntity.setPrimaryExternalId(primaryExternalId);

		String modInternalId = handleStringField(uiEntity.getModInternalId());
		dbEntity.setModInternalId(modInternalId);

		InformationContentEntity evidenceItem = validateEvidenceItem(uiEntity, dbEntity);
		dbEntity.setEvidenceItem(evidenceItem);

		Organization dataProvider = validateDataProvider(uiEntity.getDataProvider(), dbEntity.getDataProvider(), newEntity);
		dbEntity.setDataProvider(dataProvider);
		
		CrossReference dataProviderCrossReference = validateDataProviderCrossReference(uiEntity.getDataProviderCrossReference(), dbEntity.getDataProviderCrossReference());
		dbEntity.setDataProviderCrossReference(dataProviderCrossReference);
		
		List<ConditionRelation> conditionRelations = validateConditionRelations(uiEntity, dbEntity);
		dbEntity.setConditionRelations(conditionRelations);

		String evidenceItemCurie = dbEntity.getEvidenceItem() == null ? null : dbEntity.getEvidenceItem().getCurie();
		List<Note> relatedNotes = validateRelatedNotes(uiEntity.getRelatedNotes(), noteTypeSet, evidenceItemCurie);
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
}
