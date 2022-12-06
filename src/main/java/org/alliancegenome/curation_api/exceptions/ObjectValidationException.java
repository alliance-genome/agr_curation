package org.alliancegenome.curation_api.exceptions;

public class ObjectValidationException extends ObjectUpdateException {

	public ObjectValidationException(Object updateObject, String message) {
		this(updateObject, message, null);
	}

	public ObjectValidationException(Object updateObject, String message, StackTraceElement[] stackTraceElements) {
		super(updateObject, message, stackTraceElements);
	}

}
