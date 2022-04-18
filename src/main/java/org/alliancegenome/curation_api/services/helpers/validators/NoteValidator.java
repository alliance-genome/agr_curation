package org.alliancegenome.curation_api.services.helpers.validators;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.NoteDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.*;

@RequestScoped
public class NoteValidator {

    @Inject
    NoteDAO noteDAO;
    @Inject
    VocabularyTermDAO vocabularyTermDAO;
    
    
    
    protected String invalidMessage = "Not a valid entry";
    protected String obsoleteMessage = "Obsolete term specified";
    protected String requiredMessage = "Required field is empty";
    
    protected ObjectResponse<Note> response;
    
    public Note validateNote(Note uiEntity, String noteVocabularyName, Boolean throwError) {
        response = new ObjectResponse<>(uiEntity);
        String errorTitle = "Could not update Note: [" + uiEntity.getId() + "]";
        
        Long id = uiEntity.getId();
        if (id == null) {
            addMessageResponse("No Note ID provided");
            throw new ApiErrorException(response);
        }
        Note dbEntity = noteDAO.find(id);
        if (dbEntity == null) {
            addMessageResponse("Could not find Note with ID: [" + id + "]");
            throw new ApiErrorException(response);
        }
        
    VocabularyTerm noteType = validateNoteType(uiEntity, dbEntity, noteVocabularyName);
        dbEntity.setNoteType(noteType);
        
        Boolean internal = validateInternal(uiEntity);
        dbEntity.setInternal(internal);
        
        String freeText = validateFreeText(uiEntity);
        dbEntity.setFreeText(freeText);
        
        // TODO: add validation for references
        if (CollectionUtils.isNotEmpty(uiEntity.getReferences()))
            dbEntity.setReferences(uiEntity.getReferences());
        
        if (response.hasErrors()) {
            if (throwError) {
                response.setErrorMessage(errorTitle);
                throw new ApiErrorException(response);
            } else {
                return null;
            }
        }
        
        return dbEntity;
    }
    
    public VocabularyTerm validateNoteType(Note uiEntity, Note dbEntity, String noteVocabularyName) {
        String field = "noteType";
        if (uiEntity.getNoteType() == null ) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        
        VocabularyTerm noteType;
        if (noteVocabularyName == null) {
            SearchResponse<VocabularyTerm> vtSearchResponse = vocabularyTermDAO.findByField("name", uiEntity.getNoteType().getName());
            if (vtSearchResponse == null || vtSearchResponse.getSingleResult() == null) {
                addMessageResponse(field, invalidMessage);
                return null;
            } 
            noteType = vtSearchResponse.getSingleResult();
        } else {
            noteType = vocabularyTermDAO.getTermInVocabulary(uiEntity.getNoteType().getName(), noteVocabularyName);
            if (noteType == null) {
                addMessageResponse(field, invalidMessage);
                return null;
            }
        }
        
        if (noteType.getObsolete() && !noteType.getName().equals(dbEntity.getNoteType().getName())) {
            addMessageResponse(field, obsoleteMessage);
            return null;
        }
        return noteType;
    }
    
    public Boolean validateInternal(Note uiEntity) {
        String field = "internal";
        if (uiEntity.getInternal() == null) {
            addMessageResponse(field, requiredMessage);
            return null;
        }
        return uiEntity.getInternal();
    }
    
    public String validateFreeText(Note uiEntity) {
        String field = "freeText";
        if (!StringUtils.isNotBlank(uiEntity.getFreeText())) {
            addMessageResponse(field, requiredMessage);
        }
        return uiEntity.getFreeText();
    }
    
    protected void addMessageResponse(String message) {
        response.setErrorMessage(message);
    }
    
    protected void addMessageResponse(String fieldName, String message) {
        response.addErrorMessage(fieldName, message);
    }
}
