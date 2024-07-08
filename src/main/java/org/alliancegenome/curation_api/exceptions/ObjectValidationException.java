package org.alliancegenome.curation_api.exceptions;

import java.util.Collection;

public class ObjectValidationException extends ObjectUpdateException {

	public ObjectValidationException(Object updateObject, String message) {
		super(updateObject, message, null);
	}

	public ObjectValidationException(Object updateObject, String message, StackTraceElement[] stackTraceElements) {
		super(updateObject, message, stackTraceElements);
	}
	
	public ObjectValidationException(Object updateObject, Collection<String> messages) {
		super(updateObject, messages);
	}

}
