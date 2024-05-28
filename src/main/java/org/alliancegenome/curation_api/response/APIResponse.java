package org.alliancegenome.curation_api.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.view.View;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class APIResponse {

	@JsonView({ View.FieldsOnly.class }) private String errorMessage;

	@JsonView({ View.FieldsOnly.class }) private Map<String, String> errorMessages;

	@org.eclipse.microprofile.graphql.Ignore
	@JsonView({ View.FieldsOnly.class }) private Map<String, Object> supplementalData = new HashMap<>();

	@JsonView({ View.FieldsOnly.class }) private String requestDuration;

	public void addErrorMessage(String fieldName, String errorMessage) {
		if (errorMessages == null) {
			errorMessages = new HashMap<>(3);
		}
		errorMessages.put(fieldName, errorMessage);
	}

	public void addErrorMessages(Map<String, String> newErrorMessages) {
		if (newErrorMessages != null) {
			if (errorMessages == null) {
				errorMessages = new HashMap<>();
			}
			errorMessages.putAll(newErrorMessages);
		}
	}

	public void addErrorMessages(String fieldName, Object fieldErrorMessages) {
		SupplementalDataHelper.addFieldErrorMessages(supplementalData, fieldName, fieldErrorMessages);
	}

	public void addErrorMessages(String fieldName, Integer rowIndex, Object fieldErrorMessages) {
		SupplementalDataHelper.addRowFieldErrorMessages(supplementalData, fieldName, rowIndex, fieldErrorMessages);
	}

	public boolean hasErrors() {
		return StringUtils.isNotEmpty(errorMessage) || MapUtils.isNotEmpty(errorMessages);
	}

	public String errorMessagesString() {
		if (errorMessages == null) {
			return null;
		}
		return errorMessages.entrySet().stream().map(m -> m.getKey() + " - " + m.getValue()).sorted().collect(Collectors.joining(" | "));
	}

	public void convertErrorMessagesToMap() {
		if (errorMessages == null) {
			return;
		}

		for (Map.Entry<String, String> reportedError : errorMessages.entrySet()) {
			SupplementalDataHelper.addFieldErrorMessages(supplementalData, reportedError.getKey(), reportedError.getValue());
		}
	}

	public void convertMapToErrorMessages(String fieldName) {
		String consolidatedMessages = SupplementalDataHelper.convertMapToFieldErrorMessages(supplementalData, fieldName);
		if (consolidatedMessages != null) {
			addErrorMessage(fieldName, consolidatedMessages);
		}
	}

}
