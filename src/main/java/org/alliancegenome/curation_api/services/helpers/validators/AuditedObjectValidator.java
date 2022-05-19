package org.alliancegenome.curation_api.services.helpers.validators;

import java.time.OffsetDateTime;

import javax.inject.Inject;

import org.alliancegenome.curation_api.auth.AuthenticatedUser;
import org.alliancegenome.curation_api.base.entity.AuditedObject;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.*;

public class AuditedObjectValidator<E extends AuditedObject> {

    @Inject
    @AuthenticatedUser
    protected LoggedInPerson authenticatedPerson;
    
    @Inject
    PersonService personService;
    
    @Inject
    LoggedInPersonService loggedInPersonService;
    
    protected String invalidMessage = "Not a valid entry";
    protected String obsoleteMessage = "Obsolete term specified";
    protected String requiredMessage = "Required field is empty";
    protected String dependencyMessagePrefix = "Invalid without value for ";
    
    protected ObjectResponse<E> response;
    
    public E validateAuditedObjectFields(E uiEntity, E dbEntity) {
        Boolean internal = validateInternal(uiEntity);
        if (internal != null) dbEntity.setInternal(internal);
        
        if (uiEntity.getDateCreated() != null)
            dbEntity.setDateCreated(uiEntity.getDateCreated());
        
        if (uiEntity.getCreatedBy() != null) {
            Person createdBy = personService.fetchByUniqueIdOrCreate(uiEntity.getCreatedBy().getUniqueId());
            dbEntity.setCreatedBy(createdBy);
        }

        LoggedInPerson modifiedBy = loggedInPersonService.findLoggedInPersonByOktaEmail(authenticatedPerson.getOktaEmail());
        dbEntity.setModifiedBy(modifiedBy);
        
        dbEntity.setDateUpdated(OffsetDateTime.now());
        
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
