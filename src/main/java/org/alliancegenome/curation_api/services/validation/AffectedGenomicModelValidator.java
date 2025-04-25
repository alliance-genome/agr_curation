package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmFullNameSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AgmFullNameSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AgmSecondaryIdSlotAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.slotAnnotations.AgmSynonymSlotAnnotationValidator;
import org.apache.commons.collections4.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AffectedGenomicModelValidator extends GenomicEntityValidator<AffectedGenomicModel> {

	@Inject AffectedGenomicModelDAO affectedGenomicModelDAO;
	@Inject AgmFullNameSlotAnnotationValidator agmFullNameValidator;
	@Inject AgmSynonymSlotAnnotationValidator agmSynonymValidator;
	@Inject AgmSecondaryIdSlotAnnotationValidator agmSecondaryIdValidator;
	
	private String errorMessage;

	public AffectedGenomicModel validateAffectedGenomicModelUpdate(AffectedGenomicModel uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update AGM: [" + uiEntity.getIdentifier() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No AGM ID provided");
			throw new ApiErrorException(response);
		}

		AffectedGenomicModel dbEntity = affectedGenomicModelDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("id", ValidationConstants.INVALID_MESSAGE);
			throw new ApiErrorException(response);
		}

		dbEntity = (AffectedGenomicModel) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateAffectedGenomicModel(uiEntity, dbEntity);
	}

	public AffectedGenomicModel validateAffectedGenomicModelCreate(AffectedGenomicModel uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create AGM";

		AffectedGenomicModel dbEntity = new AffectedGenomicModel();

		dbEntity = (AffectedGenomicModel) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateAffectedGenomicModel(uiEntity, dbEntity);
	}

	private AffectedGenomicModel validateAffectedGenomicModel(AffectedGenomicModel uiEntity, AffectedGenomicModel dbEntity) {

		dbEntity = validateGenomicEntityFields(uiEntity, dbEntity);

		AgmFullNameSlotAnnotation fullName = validateAgmFullName(uiEntity, dbEntity);
		List<AgmSynonymSlotAnnotation> synonyms = validateAgmSynonyms(uiEntity, dbEntity);
		List<AgmSecondaryIdSlotAnnotation> secondaryIds = validateAgmSecondaryIds(uiEntity, dbEntity);

		VocabularyTerm subtype = validateRequiredTermInVocabulary("subtype", VocabularyConstants.AGM_SUBTYPE_VOCABULARY, uiEntity.getSubtype(), dbEntity.getSubtype());
		dbEntity.setSubtype(subtype);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}
		
		dbEntity = affectedGenomicModelDAO.persist(dbEntity);
		
		if (fullName != null) {
			fullName.setSingleAgm(dbEntity);
		}
		dbEntity.setAgmFullName(fullName);

		if (dbEntity.getAgmSynonyms() != null) {
			dbEntity.getAgmSynonyms().clear();
		}
		if (synonyms != null) {
			if (dbEntity.getAgmSynonyms() == null) {
				dbEntity.setAgmSynonyms(new ArrayList<>());
			}
			dbEntity.getAgmSynonyms().addAll(synonyms);
		}

		if (dbEntity.getAgmSecondaryIds() != null) {
			dbEntity.getAgmSecondaryIds().clear();
		}
		if (secondaryIds != null) {
			if (dbEntity.getAgmSecondaryIds() == null) {
				dbEntity.setAgmSecondaryIds(new ArrayList<>());
			}
			dbEntity.getAgmSecondaryIds().addAll(secondaryIds);
		}
		
		return dbEntity;
	}

	private AgmFullNameSlotAnnotation validateAgmFullName(AffectedGenomicModel uiEntity, AffectedGenomicModel dbEntity) {
		if (uiEntity.getAgmFullName() == null) {
			return null;
		}

		String field = "agmFullName";

		ObjectResponse<AgmFullNameSlotAnnotation> nameResponse = agmFullNameValidator.validateAgmFullNameSlotAnnotation(uiEntity.getAgmFullName());
		if (nameResponse.getEntity() == null) {
			addMessageResponse(field, nameResponse.errorMessagesString());
			response.addErrorMessages(field, nameResponse.getErrorMessages());
			return null;
		}

		return nameResponse.getEntity();
	}

	private List<AgmSynonymSlotAnnotation> validateAgmSynonyms(AffectedGenomicModel uiEntity, AffectedGenomicModel dbEntity) {
		String field = "agmSynonyms";

		List<AgmSynonymSlotAnnotation> validatedSynonyms = new ArrayList<AgmSynonymSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getAgmSynonyms())) {
			for (int ix = 0; ix < uiEntity.getAgmSynonyms().size(); ix++) {
				AgmSynonymSlotAnnotation syn = uiEntity.getAgmSynonyms().get(ix);
				ObjectResponse<AgmSynonymSlotAnnotation> synResponse = agmSynonymValidator.validateAgmSynonymSlotAnnotation(syn);
				if (synResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, synResponse.getErrorMessages());
					allValid = false;
				} else {
					syn = synResponse.getEntity();
					syn.setSingleAgm(dbEntity);
					validatedSynonyms.add(syn);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSynonyms)) {
			return null;
		}

		return validatedSynonyms;
	}

	private List<AgmSecondaryIdSlotAnnotation> validateAgmSecondaryIds(AffectedGenomicModel uiEntity, AffectedGenomicModel dbEntity) {
		String field = "agmSecondaryIds";

		List<AgmSecondaryIdSlotAnnotation> validatedSecondaryIds = new ArrayList<AgmSecondaryIdSlotAnnotation>();
		Boolean allValid = true;
		if (CollectionUtils.isNotEmpty(uiEntity.getAgmSecondaryIds())) {
			for (int ix = 0; ix < uiEntity.getAgmSecondaryIds().size(); ix++) {
				AgmSecondaryIdSlotAnnotation sid = uiEntity.getAgmSecondaryIds().get(ix);
				ObjectResponse<AgmSecondaryIdSlotAnnotation> sidResponse = agmSecondaryIdValidator.validateAgmSecondaryIdSlotAnnotation(sid);
				if (sidResponse.getEntity() == null) {
					response.addErrorMessages(field, ix, sidResponse.getErrorMessages());
					allValid = false;
				} else {
					sid = sidResponse.getEntity();
					sid.setSingleAgm(dbEntity);
					validatedSecondaryIds.add(sid);
				}
			}
		}

		if (!allValid) {
			convertMapToErrorMessages(field);
			return null;
		}

		if (CollectionUtils.isEmpty(validatedSecondaryIds)) {
			return null;
		}

		return validatedSecondaryIds;
	}

}
