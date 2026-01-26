package org.alliancegenome.curation_api.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.view.CurationView;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class APIResponse {

	@JsonView({ CurationView.FieldsOnly.class }) private String errorMessage;
	@JsonView({ CurationView.FieldsOnly.class }) private String warningMessage;

	@JsonView({ CurationView.FieldsOnly.class }) private Map<String, String> errorMessages;
	@JsonView({ CurationView.FieldsOnly.class }) private Map<String, String> warningMessages;

	@org.eclipse.microprofile.graphql.Ignore
	@JsonView({ CurationView.FieldsOnly.class }) private Map<String, Object> supplementalData = new HashMap<>();

	@JsonView({ CurationView.FieldsOnly.class }) private String requestDuration;

	
	public void addWarningMessage(String fieldName, String warningMessage) {
		if (warningMessages == null) {
			warningMessages = new HashMap<>();
		}
		warningMessages.put(fieldName, warningMessage);
	}
	
	public void addErrorMessage(String fieldName, String errorMessage) {
		if (errorMessages == null) {
			errorMessages = new HashMap<>();
		}
		errorMessages.put(fieldName, errorMessage);
	}
	
	public void addWarningMessages(Map<String, String> newWarningMessages) {
		if (newWarningMessages != null) {
			if (warningMessages == null) {
				warningMessages = new HashMap<>();
			}
			warningMessages.putAll(newWarningMessages);
		}
	}
	
	public void addErrorMessages(Map<String, String> newErrorMessages) {
		if (newErrorMessages != null) {
			if (errorMessages == null) {
				errorMessages = new HashMap<>();
			}
			errorMessages.putAll(newErrorMessages);
		}
	}
	
	
	public void addWarningMessages(String fieldName, Object fieldWarningMessages) {
		SupplementalDataHelper.addFieldWarningMessages(supplementalData, fieldName, fieldWarningMessages);
	}

	public void addErrorMessages(String fieldName, Object fieldErrorMessages) {
		SupplementalDataHelper.addFieldErrorMessages(supplementalData, fieldName, fieldErrorMessages);
	}

	public void addWarningMessages(String fieldName, Integer rowIndex, Object fieldWarningMessages) {
		SupplementalDataHelper.addRowFieldWarningMessages(supplementalData, fieldName, rowIndex, fieldWarningMessages);
	}
	
	public void addErrorMessages(String fieldName, Integer rowIndex, Object fieldErrorMessages) {
		SupplementalDataHelper.addRowFieldErrorMessages(supplementalData, fieldName, rowIndex, fieldErrorMessages);
	}

	public boolean hasWarnings() {
		return StringUtils.isNotEmpty(warningMessage) || MapUtils.isNotEmpty(warningMessages);
	}
	
	public boolean hasErrors() {
		return StringUtils.isNotEmpty(errorMessage) || MapUtils.isNotEmpty(errorMessages);
	}

	public String warningMessagesString() {
		if (warningMessages == null) {
			return null;
		}
		return warningMessages.entrySet().stream().map(m -> m.getKey() + " - " + m.getValue()).sorted().collect(Collectors.joining(" | "));
	}
	
	public String errorMessagesString() {
		if (errorMessages == null) {
			return null;
		}
		return errorMessages.entrySet().stream().map(m -> m.getKey() + " - " + m.getValue()).sorted().collect(Collectors.joining(" | "));
	}

	public List<String> warningMessagesList() {
		if (warningMessages == null) {
			return null;
		}
		return warningMessages.entrySet().stream().map(m -> m.getKey() + " - " + m.getValue()).sorted().collect(Collectors.toList());
	}
	
	public List<String> errorMessagesList() {
		if (errorMessages == null) {
			return null;
		}
		return errorMessages.entrySet().stream().map(m -> m.getKey() + " - " + m.getValue()).sorted().collect(Collectors.toList());
	}

	public void convertWarningMessagesToMap() {
		if (warningMessages == null) {
			return;
		}

		for (Map.Entry<String, String> reportedError : warningMessages.entrySet()) {
			SupplementalDataHelper.addFieldWarningMessages(supplementalData, reportedError.getKey(), reportedError.getValue());
		}
	}
	
	public void convertErrorMessagesToMap() {
		if (errorMessages == null) {
			return;
		}

		for (Map.Entry<String, String> reportedError : errorMessages.entrySet()) {
			SupplementalDataHelper.addFieldErrorMessages(supplementalData, reportedError.getKey(), reportedError.getValue());
		}
	}

	public void convertMapToWarningMessages(String fieldName) {
		String consolidatedMessages = SupplementalDataHelper.convertMapToFieldWarningMessages(supplementalData, fieldName);
		if (consolidatedMessages != null) {
			addWarningMessage(fieldName, consolidatedMessages);
		}
	}
	
	public void convertMapToErrorMessages(String fieldName) {
		String consolidatedMessages = SupplementalDataHelper.convertMapToFieldErrorMessages(supplementalData, fieldName);
		if (consolidatedMessages != null) {
			addErrorMessage(fieldName, consolidatedMessages);
		}
	}

}
