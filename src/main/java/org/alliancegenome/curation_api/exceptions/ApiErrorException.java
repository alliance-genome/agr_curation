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
		super();
	}

	public ApiErrorException(ObjectResponse<?> error) {
		this(error, null);
	}

	public ApiErrorException(ObjectResponse<?> error, Throwable cause) {
		super(error == null ? null : StringUtils.defaultIfBlank(error.getErrorMessage(), error.errorMessagesString()), cause);
		this.objectResponse = error;
	}

	public ObjectResponse<?> getObjectResponse() {
		return objectResponse;
	}
}