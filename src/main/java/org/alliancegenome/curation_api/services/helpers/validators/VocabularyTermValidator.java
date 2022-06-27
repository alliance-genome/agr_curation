package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class VocabularyTermValidator extends AuditedObjectValidator<VocabularyTerm>{

	@Inject
	VocabularyTermDAO vocabularyTermDAO;
	@Inject
	VocabularyDAO vocabularyDAO;
	
	public VocabularyTerm validateVocabularyTerm(VocabularyTerm uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		String errorTitle = "Could not update VocabularyTerm: [" + uiEntity.getId() + "]";
		
		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No VocabularyTerm ID provided");
			throw new ApiErrorException(response);
		}
		VocabularyTerm dbEntity = vocabularyTermDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find VocabularyTerm with ID: [" + id + "]");
			throw new ApiErrorException(response);
		}
		
		dbEntity = (VocabularyTerm) validateAuditedObjectFields(uiEntity, dbEntity);
		
		String name = validateName(uiEntity);
		dbEntity.setName(name);
		
		dbEntity.setAbbreviation(handleStringField(uiEntity.getAbbreviation()));
		dbEntity.setDefinition(handleStringField(uiEntity.getDefinition()));
		
		Vocabulary vocabulary = validateVocabulary(uiEntity, dbEntity);
		dbEntity.setVocabulary(vocabulary);
		
		if (CollectionUtils.isNotEmpty(uiEntity.getTextSynonyms()))
			dbEntity.setTextSynonyms(uiEntity.getTextSynonyms());
		
		if (response.hasErrors()) {
			response.setErrorMessage(errorTitle);
			throw new ApiErrorException(response);
		}
		
		return dbEntity;
	}
	
	public String validateName(VocabularyTerm uiEntity) {
		String field = "name";
		if (StringUtils.isBlank(uiEntity.getName())) {
			addMessageResponse(field, requiredMessage);
			return null;
		}
		
		return uiEntity.getName();
	}
	
	public Vocabulary validateVocabulary(VocabularyTerm uiEntity, VocabularyTerm dbEntity) {
		String field = "vocabulary";
		if (uiEntity.getVocabulary() == null) {
			addMessageResponse(field, requiredMessage);
			return null;
		}
		SearchResponse<Vocabulary> vocabularyResponse = vocabularyDAO.findByField("name", uiEntity.getVocabulary().getName());
		if (vocabularyResponse == null || vocabularyResponse.getSingleResult() == null) {
			addMessageResponse(field, invalidMessage);
			return null;
		}
		
		Vocabulary vocabulary = vocabularyResponse.getSingleResult();
		if (vocabulary.getObsolete() && !vocabulary.getName().equals(dbEntity.getVocabulary().getName())) {
			addMessageResponse(field, obsoleteMessage);
			return null;
		}
		
		return vocabulary;
	}

}
