package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class VocabularyValidator extends AuditedObjectValidator<Vocabulary>{

	@Inject
	VocabularyDAO vocabularyDAO;
	
	public Vocabulary validateVocabulary(Vocabulary uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update Vocabulary: [" + uiEntity.getId() + "]";
		
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
		
		dbEntity = (Vocabulary) validateAuditedObjectFields(uiEntity, dbEntity);
		
		String name = validateName(uiEntity);
		dbEntity.setName(name);
		
		dbEntity.setVocabularyDescription(handleStringField(uiEntity.getVocabularyDescription()));
		
		if (CollectionUtils.isNotEmpty(uiEntity.getMemberTerms()))
			dbEntity.setMemberTerms(uiEntity.getMemberTerms());
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
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
