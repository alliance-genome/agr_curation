package org.alliancegenome.curation_api.exceptions;

public class ObjectValidationException extends ObjectUpdateException {

    public ObjectValidationException(Object updateObject, String message) {
        super(updateObject, message);
    }

}
