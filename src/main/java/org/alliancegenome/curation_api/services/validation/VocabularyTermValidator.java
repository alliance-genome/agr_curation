package org.alliancegenome.curation_api.services.validation;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.validation.base.AuditedObjectValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class VocabularyTermValidator extends AuditedObjectValidator<VocabularyTerm> {

	@Inject VocabularyTermService vocabularyTermService;
	@Inject VocabularyDAO vocabularyDAO;

	private String errorMessage;

	public VocabularyTerm validateVocabularyTermUpdate(VocabularyTerm uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update VocabularyTerm: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No VocabularyTerm ID provided");
			throw new ApiErrorException(response);
		}
		VocabularyTerm dbEntity = vocabularyTermService.getById(id).getEntity();
		if (dbEntity == null) {
			addMessageResponse("Could not find VocabularyTerm with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = (VocabularyTerm) validateAuditedObjectFields(uiEntity, dbEntity, false);

		return validateVocabularyTerm(uiEntity, dbEntity);
	}

	public VocabularyTerm validateVocabularyTermCreate(VocabularyTerm uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create VocabularyTerm: [" + uiEntity.getName() + "]";

		VocabularyTerm dbEntity = new VocabularyTerm();

		dbEntity = (VocabularyTerm) validateAuditedObjectFields(uiEntity, dbEntity, true);

		return validateVocabularyTerm(uiEntity, dbEntity);
	}

	public VocabularyTerm validateVocabularyTerm(VocabularyTerm uiEntity, VocabularyTerm dbEntity) {

		String name = validateName(uiEntity);
		dbEntity.setName(name);

		dbEntity.setAbbreviation(handleStringField(uiEntity.getAbbreviation()));
		dbEntity.setDefinition(handleStringField(uiEntity.getDefinition()));

		Vocabulary vocabulary = validateVocabulary(uiEntity, dbEntity);
		dbEntity.setVocabulary(vocabulary);

		if (CollectionUtils.isNotEmpty(uiEntity.getSynonyms())) {
			dbEntity.setSynonyms(uiEntity.getSynonyms());
		} else {
			dbEntity.setSynonyms(null);
		}

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	public String validateName(VocabularyTerm uiEntity) {
		String field = "name";
		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		return uiEntity.getName();
	}

	public Vocabulary validateVocabulary(VocabularyTerm uiEntity, VocabularyTerm dbEntity) {
		String field = "vocabulary";
		if (uiEntity.getVocabulary() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		SearchResponse<Vocabulary> vocabularyResponse = vocabularyDAO.findByField("name", uiEntity.getVocabulary().getName());
		if (vocabularyResponse == null || vocabularyResponse.getSingleResult() == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		Vocabulary vocabulary = vocabularyResponse.getSingleResult();
		if (vocabulary.getObsolete() && (dbEntity.getVocabulary() == null || !vocabulary.getName().equals(dbEntity.getVocabulary().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return vocabulary;
	}

}
