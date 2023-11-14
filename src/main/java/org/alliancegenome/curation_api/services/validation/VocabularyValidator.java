package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VocabularyValidator extends AuditedObjectValidator<Vocabulary> {

	@Inject
	VocabularyDAO vocabularyDAO;

	private String errorMessage;

	public Vocabulary validateVocabularyUpdate(Vocabulary uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Vocabulary: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Vocabulary ID provided");
			throw new ApiErrorException(response);
		}
		Vocabulary dbEntity = vocabularyDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Vocabulary with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = (Vocabulary) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateVocabulary(uiEntity, dbEntity);
	}

	public Vocabulary validateVocabularyCreate(Vocabulary uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create Vocabulary: [" + uiEntity.getName() + "]";

		Vocabulary dbEntity = new Vocabulary();

		dbEntity = (Vocabulary) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateVocabulary(uiEntity, dbEntity);
	}

	public Vocabulary validateVocabulary(Vocabulary uiEntity, Vocabulary dbEntity) {

		String name = validateName(uiEntity);
		dbEntity.setName(name);
		
		String label = validateVocabularyLabel(uiEntity, dbEntity);
		dbEntity.setVocabularyLabel(label);

		dbEntity.setVocabularyDescription(handleStringField(uiEntity.getVocabularyDescription()));

		if (CollectionUtils.isNotEmpty(uiEntity.getMemberTerms())) {
			dbEntity.setMemberTerms(uiEntity.getMemberTerms());
		} else {
			dbEntity.setMemberTerms(null);
		}

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	public String validateName(Vocabulary uiEntity) {
		String field = "name";
		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		if (!isUniqueValue(uiEntity.getName(), field, uiEntity.getId())) {
			addMessageResponse(field, ValidationConstants.NON_UNIQUE_MESSAGE);
			return null;
		}

		return uiEntity.getName();
	}

	public String validateVocabularyLabel(Vocabulary uiEntity, Vocabulary dbEntity) {
		String field = "vocabularyLabel";
		if (StringUtils.isBlank(uiEntity.getVocabularyLabel())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		if (StringUtils.isNotBlank(dbEntity.getVocabularyLabel()) && 
				!StringUtils.equals(uiEntity.getVocabularyLabel(), dbEntity.getVocabularyLabel())) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		if (!isUniqueValue(uiEntity.getVocabularyLabel(), field, uiEntity.getId())) {
			addMessageResponse(field, ValidationConstants.NON_UNIQUE_MESSAGE);
			return null;
		}

		return uiEntity.getVocabularyLabel();
	}
	
	private Boolean isUniqueValue(String uiEntityValue, String field, Long uiEntityId) {
		SearchResponse<Vocabulary> response = vocabularyDAO.findByField(field, uiEntityValue);
		if (response == null || response.getSingleResult() == null)
			return true;
		if (uiEntityId == null)
			return false;
		if (uiEntityId.equals(response.getSingleResult().getId()))
			return true;
		return false;
	}
}
