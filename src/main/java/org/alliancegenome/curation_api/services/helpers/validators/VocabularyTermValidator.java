package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.VocabularyDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.*;

@RequestScoped
public class VocabularyTermValidator {

    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    @Inject
    VocabularyDAO vocabularyDAO;
    
    protected String invalidMessage = "Not a valid entry";
    protected String obsoleteMessage = "Obsolete term specified";
    protected String requiredMessage = "Required field is empty";
    
    protected ObjectResponse<VocabularyTerm> response;
    
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
        
        String name = validateName(uiEntity);
        dbEntity.setName(name);
        
        if (!StringUtils.isBlank(uiEntity.getAbbreviation()))
            dbEntity.setAbbreviation(uiEntity.getAbbreviation());
            
        if (!StringUtils.isBlank(uiEntity.getDefinition()))
            dbEntity.setDefinition(uiEntity.getDefinition());
        
        if (uiEntity.getObsolete() == null) {
            dbEntity.setObsolete(false);
        } else {
            dbEntity.setObsolete(uiEntity.getObsolete());
        }
        
        Vocabulary vocabulary = validateVocabulary(uiEntity);
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
    
    public Vocabulary validateVocabulary(VocabularyTerm uiEntity) {
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
        
        return vocabulary;
    }
    
    protected void addMessageResponse(String message) {
        response.setErrorMessage(message);
    }
    
    protected void addMessageResponse(String fieldName, String message) {
        response.addErrorMessage(fieldName, message);
    }
}
