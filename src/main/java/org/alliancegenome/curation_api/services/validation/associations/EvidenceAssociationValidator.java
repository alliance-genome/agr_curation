package org.alliancegenome.curation_api.services.validation.associations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.EvidenceAssociation;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.alliancegenome.curation_api.services.helpers.notes.NoteIdentityHelper;
import org.alliancegenome.curation_api.services.validation.AuditedObjectValidator;
import org.alliancegenome.curation_api.services.validation.ConditionRelationValidator;
import org.alliancegenome.curation_api.services.validation.DataProviderValidator;
import org.alliancegenome.curation_api.services.validation.NoteValidator;
import org.alliancegenome.curation_api.services.validation.ReferenceValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;

public class EvidenceAssociationValidator extends AuditedObjectValidator<EvidenceAssociation> {
	
	@Inject
	InformationContentEntityService informationContentEntityService;
	
	public List<InformationContentEntity> validateEvidence(EvidenceAssociation uiEntity, EvidenceAssociation dbEntity) {
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
			if (evidenceEntity.getObsolete() && (CollectionUtils.isEmpty(dbEntity.getEvidence()) || !dbEntity.getEvidence().contains(evidenceEntity))) {
				response.addErrorMessage(field, ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			validatedEntities.add(evidenceEntity);
		}

		return validatedEntities;
	}

	public EvidenceAssociation validateEvidenceAssociationFields(EvidenceAssociation uiEntity, EvidenceAssociation dbEntity) {
		Boolean newEntity = false;
		if (dbEntity.getId() == null)
			newEntity = true;
		dbEntity = validateAuditedObjectFields(uiEntity, dbEntity, newEntity);

		List<InformationContentEntity> evidence = validateEvidence(uiEntity, dbEntity);
		dbEntity.setEvidence(evidence);

		return dbEntity;
	}
}
