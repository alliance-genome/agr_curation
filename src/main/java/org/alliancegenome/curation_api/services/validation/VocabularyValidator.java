package org.alliancegenome.curation_api.services.validation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class VocabularyValidator extends AuditedObjectValidator<Vocabulary>{

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
		
		return uiEntity.getName();
	}
}
