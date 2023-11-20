package org.alliancegenome.curation_api.response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.view.View;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class APIResponse {

	@JsonView({ View.FieldsOnly.class })
	private String errorMessage;

	@JsonView({ View.FieldsOnly.class })
	private Map<String, String> errorMessages;
	
	@JsonView({ View.FieldsOnly.class })
	private Map<String, Object> supplementalData;

	@JsonView({ View.FieldsOnly.class })
	private String requestDuration;

	public void addErrorMessage(String fieldName, String errorMessage) {
		if (errorMessages == null)
			errorMessages = new HashMap<>(3);
		errorMessages.put(fieldName, errorMessage);
	}

	public void addErrorMessages(Map<String, String> newErrorMessages) {
		if (newErrorMessages != null) {
			if (errorMessages == null)
				errorMessages = new HashMap<>();
			errorMessages.putAll(newErrorMessages);
		}
	}
	

	public void addErrorMessagesToSupplementalData(String fieldName, Object fieldErrorMessages) {
		addErrorMessagesToSupplementalData(fieldName, null, fieldErrorMessages);
	}
	
	public void addErrorMessagesToSupplementalData(String fieldName, Integer rowIndex, Object fieldErrorMessages) {
		if (supplementalData == null)
			supplementalData = new LinkedHashMap<>();
		Map<String, Object> errorMap = (Map<String, Object>) supplementalData.get("errorMap");
		if(errorMap == null) {
			errorMap = new LinkedHashMap<>();
			supplementalData.put("errorMap", errorMap);
		}
		Map<String, Object> fieldErrorMap = (Map<String, Object>) errorMap.get(fieldName);
		if (fieldErrorMap == null) {
			fieldErrorMap = new LinkedHashMap<>();
			errorMap.put(fieldName, fieldErrorMap);
		}
		if (rowIndex == null) {
			errorMap.put(fieldName, fieldErrorMessages);
		} else {
			fieldErrorMap.put(Integer.toString(rowIndex), fieldErrorMessages);
		}
	}

	public boolean hasErrors() {
		return StringUtils.isNotEmpty(errorMessage) || MapUtils.isNotEmpty(errorMessages);
	}

	public String errorMessagesString() {
		if (errorMessages == null)
			return null;

		return errorMessages.entrySet().stream().map(m -> m.getKey() + " - " + m.getValue()).sorted().collect(Collectors.joining(" | "));
	}

}
