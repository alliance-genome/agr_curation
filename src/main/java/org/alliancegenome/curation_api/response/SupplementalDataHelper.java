package org.alliancegenome.curation_api.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.quarkus.logging.Log;

public class SupplementalDataHelper {

	public static final String ERROR_MAP = "errorMap";
	
	public static void addFieldErrorMessages(Map<String, Object> supplementalData, String fieldName, Object fieldErrorMessages) {
		Map<String, Object> errorMap = getErrorMap(supplementalData);
		if(!errorMap.containsKey(fieldName)) {
			errorMap.put(fieldName, fieldErrorMessages);
		} else {
			Log.debug("Error Map already contains errors for field: " + fieldName);
			Log.debug(errorMap.get(fieldName));
		}
	}

	public static void addRowFieldErrorMessages(Map<String, Object> supplementalData, String fieldName, Integer rowIndex, Object fieldErrorMessages) {
		Map<String, Object> fieldErrorMap = getFieldErrorMap(supplementalData, fieldName);
		fieldErrorMap.put(Integer.toString(rowIndex), fieldErrorMessages);
	}
	
	private static Map<String, Object> getFieldErrorMap(Map<String, Object> supplementalData, String fieldName) {
		Map<String, Object> errorMap = getErrorMap(supplementalData);
		
		Map<String, Object> fieldErrorMap = (Map<String, Object>) errorMap.get(fieldName);
		if (fieldErrorMap == null) {
			fieldErrorMap = new LinkedHashMap<>();
			errorMap.put(fieldName, fieldErrorMap);
		}
		return (Map<String, Object>)errorMap.get(fieldName);
	}

	private static Map<String, Object> getErrorMap(Map<String, Object> supplementalData) {
		if(!supplementalData.containsKey(ERROR_MAP)) {
			supplementalData.put(ERROR_MAP, new LinkedHashMap<>());
		}
		return (Map<String, Object>)supplementalData.get(ERROR_MAP);
	}

	public static String convertMapToFieldErrorMessages(Map<String, Object> supplementalData, String fieldName) {
		if(!supplementalData.containsKey(ERROR_MAP)) return null;
		Map<String, Object> errorMap = (Map<String, Object>) supplementalData.get(ERROR_MAP);
		Map<String, Object> fieldErrorMap = (Map<String, Object>) errorMap.get(fieldName);
		if (fieldErrorMap == null)
			return null;
		
		Map<String, Set<String>> consolidatedErrors = new LinkedHashMap<>();
		for (Map.Entry<String, Object> fieldRowError : fieldErrorMap.entrySet()) {
			Map<String, String> subfieldErrors = (Map<String, String>) fieldRowError.getValue();
			for (Map.Entry<String, String> subfieldError : subfieldErrors.entrySet()) {
				Set<String> uniqueSubfieldErrors = consolidatedErrors.get(subfieldError.getKey());
				if (uniqueSubfieldErrors == null)
					uniqueSubfieldErrors = new HashSet<>();
				uniqueSubfieldErrors.add(subfieldError.getValue());
				consolidatedErrors.put(subfieldError.getKey(), uniqueSubfieldErrors);
			}
		}
		
		List<String> consolidatedMessages = new ArrayList<>();
		for (Map.Entry<String, Set<String>> consolidatedError : consolidatedErrors.entrySet()) {
			consolidatedMessages.add(consolidatedError.getKey() + " - " + consolidatedError.getValue().stream().sorted().collect(Collectors.joining("/")));
		}
		Collections.sort(consolidatedMessages);
		return String.join(" | ", consolidatedMessages);

	}

}
