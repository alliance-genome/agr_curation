package org.alliancegenome.curation_api.exceptions;

import org.alliancegenome.curation_api.response.ObjectResponse;
import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiErrorException extends RuntimeException {

	public static final String INTERRUPTED_MESSAGE = "Thread isInterrupted";

	private ObjectResponse<?> objectResponse;

	public ApiErrorException(String message) {
		super(message);
	}

	public ApiErrorException(ObjectResponse<?> error) {
		this.objectResponse = error;
	}

	public ObjectResponse<?> getObjectResponse() {
		return objectResponse;
	}

	/**
	 * SCRUM-6874: the reason for the error is held in the response, so it is read from there. Callers that only see
	 * the exception, such as the bulk load cleanup recording why a deletion failed, otherwise get null. The response
	 * is read when the message is asked for, as it may be filled in after the exception is created.
	 */
	@Override
	public String getMessage() {
		String message = super.getMessage();
		if (StringUtils.isNotBlank(message) || objectResponse == null) {
			return message;
		}
		String errorMessage = objectResponse.getErrorMessage();
		String fieldErrors = objectResponse.errorMessagesString();
		if (StringUtils.isNotBlank(errorMessage) && StringUtils.isNotBlank(fieldErrors)) {
			return errorMessage + ": " + fieldErrors;
		}
		return StringUtils.isNotBlank(errorMessage) ? errorMessage : StringUtils.trimToNull(fieldErrors);
	}
}
