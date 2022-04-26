package org.alliancegenome.curation_api.services.helpers.validators;

import java.time.OffsetDateTime;
import java.util.*;

import javax.inject.Inject;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.base.entity.BaseEntity;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.*;

public class AuditedObjectValidator<E extends BaseEntity> {

    @Inject
    @AuthenticatedUser
    protected Person authenticatedPerson;
    
    protected String invalidMessage = "Not a valid entry";
    protected String obsoleteMessage = "Obsolete term specified";
    protected String requiredMessage = "Required field is empty";
    protected String dependencyMessagePrefix = "Invalid without value for ";
    
    protected ObjectResponse<E> response;
    
    public E validateAuditedObjectFields(E uiEntity, E dbEntity) {
        Boolean internal = validateInternal(uiEntity);
        if (internal != null) dbEntity.setInternal(internal);
        
        if (uiEntity.getCreationDate() != null)
            dbEntity.setCreationDate(uiEntity.getCreationDate());
        
        if (uiEntity.getCreatedBy() != null)
            dbEntity.setCreatedBy(uiEntity.getCreatedBy());

        dbEntity.setDateLastModified(OffsetDateTime.now());
        dbEntity.setModifiedBy(authenticatedPerson.getFirstName() + " " + authenticatedPerson.getLastName());
        
        return dbEntity;
    }
    
    public Boolean validateInternal(E uiEntity) {
        if (uiEntity.getInternal() == null) {
            addMessageResponse("internal", requiredMessage);
            return null;
        }
        return uiEntity.getInternal();
    }
    
    protected void addMessageResponse(String message) {
        response.setErrorMessage(message);
    }
    
    protected void addMessageResponse(String fieldName, String message) {
        response.addErrorMessage(fieldName, message);
    }
}
