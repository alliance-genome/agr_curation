package org.alliancegenome.curation_api.services.validation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermSetDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class VocabularyTermSetValidator extends AuditedObjectValidator<VocabularyTermSet>{

	@Inject VocabularyTermSetDAO vocabularyTermSetDAO;
	@Inject VocabularyDAO vocabularyDAO;
	
	private String errorMessage;
	
	public VocabularyTermSet validateVocabularyTermSetUpdate(VocabularyTermSet uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update VocabularyTermSet: [" + uiEntity.getId() + "]";
		
		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No VocabularyTermSet ID provided");
			throw new ApiErrorException(response);
		}
		VocabularyTermSet dbEntity = vocabularyTermSetDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Vocabulary with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}

		dbEntity = (VocabularyTermSet) validateAuditedObjectFields(uiEntity, dbEntity, false);
		
		return validateVocabularyTermSet(uiEntity, dbEntity);
	}
	
	public VocabularyTermSet validateVocabularyTermSetCreate(VocabularyTermSet uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not create Vocabulary: [" + uiEntity.getName() + "]";
		
		VocabularyTermSet dbEntity = new VocabularyTermSet();

		dbEntity = (VocabularyTermSet) validateAuditedObjectFields(uiEntity, dbEntity, true);
		
		return validateVocabularyTermSet(uiEntity, dbEntity);
	}
	
	public VocabularyTermSet validateVocabularyTermSet(VocabularyTermSet uiEntity, VocabularyTermSet dbEntity) {
		
		String name = validateName(uiEntity);
		dbEntity.setName(name);
		
		Vocabulary vocabularyTermSetVocabulary = validateVocabularyTermSetVocabulary(uiEntity, dbEntity);
		dbEntity.setVocabularyTermSetVocabulary(vocabularyTermSetVocabulary);
		
		dbEntity.setVocabularyTermSetDescription(handleStringField(uiEntity.getVocabularyTermSetDescription()));
		
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
	
	public String validateName(VocabularyTermSet uiEntity) {
		String field = "name";
		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		
		return uiEntity.getName();
	}
	
	public Vocabulary validateVocabularyTermSetVocabulary(VocabularyTermSet uiEntity, VocabularyTermSet dbEntity) {
		String field = "vocabularyTermSetVocabulary";
		if (uiEntity.getVocabularyTermSetVocabulary() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}
		SearchResponse<Vocabulary> vocabularyResponse = vocabularyDAO.findByField("name", uiEntity.getVocabularyTermSetVocabulary().getName());
		if (vocabularyResponse == null || vocabularyResponse.getSingleResult() == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}
		
		Vocabulary vocabulary = vocabularyResponse.getSingleResult();
		if (vocabulary.getObsolete() && (dbEntity.getVocabularyTermSetVocabulary() == null || !vocabulary.getName().equals(dbEntity.getVocabularyTermSetVocabulary().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		
		return vocabulary;
	}
}
