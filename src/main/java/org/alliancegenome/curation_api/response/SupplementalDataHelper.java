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

	private SupplementalDataHelper() {
		// Hidden from view, as it is a utility class
	}

	public static final String ERROR_MAP = "errorMap";
	public static final String WARNING_MAP = "warnMap";

	public static void addFieldWarningMessages(Map<String, Object> supplementalData, String fieldName, Object fieldWarningMessages) {
		Map<String, Object> warnMap = getWarnMap(supplementalData);
		if (!warnMap.containsKey(fieldName)) {
			warnMap.put(fieldName, fieldWarningMessages);
		} else {
			Log.debug("Warn Map already contains warnings for field: " + fieldName);
			Log.debug(warnMap.get(fieldName));
		}
	}
	
	public static void addFieldErrorMessages(Map<String, Object> supplementalData, String fieldName, Object fieldErrorMessages) {
		Map<String, Object> errorMap = getErrorMap(supplementalData);
		if (!errorMap.containsKey(fieldName)) {
			errorMap.put(fieldName, fieldErrorMessages);
		} else {
			Log.debug("Error Map already contains errors for field: " + fieldName);
			Log.debug(errorMap.get(fieldName));
		}
	}

	public static void addRowFieldWarningMessages(Map<String, Object> supplementalData, String fieldName, Integer rowIndex, Object fieldWarningMessages) {
		Map<String, Object> fieldWarnMap = getFieldWarnMap(supplementalData, fieldName);
		fieldWarnMap.put(Integer.toString(rowIndex), fieldWarningMessages);
	}
	
	public static void addRowFieldErrorMessages(Map<String, Object> supplementalData, String fieldName, Integer rowIndex, Object fieldErrorMessages) {
		Map<String, Object> fieldErrorMap = getFieldErrorMap(supplementalData, fieldName);
		fieldErrorMap.put(Integer.toString(rowIndex), fieldErrorMessages);
	}

	private static Map<String, Object> getFieldWarnMap(Map<String, Object> supplementalData, String fieldName) {
		Map<String, Object> warnMap = getWarnMap(supplementalData);

		Map<String, Object> fieldWarnMap = (Map<String, Object>) warnMap.get(fieldName);
		if (fieldWarnMap == null) {
			fieldWarnMap = new LinkedHashMap<>();
			warnMap.put(fieldName, fieldWarnMap);
		}
		return (Map<String, Object>) warnMap.get(fieldName);
	}
	
	private static Map<String, Object> getFieldErrorMap(Map<String, Object> supplementalData, String fieldName) {
		Map<String, Object> errorMap = getErrorMap(supplementalData);

		Map<String, Object> fieldErrorMap = (Map<String, Object>) errorMap.get(fieldName);
		if (fieldErrorMap == null) {
			fieldErrorMap = new LinkedHashMap<>();
			errorMap.put(fieldName, fieldErrorMap);
		}
		return (Map<String, Object>) errorMap.get(fieldName);
	}

	private static Map<String, Object> getWarnMap(Map<String, Object> supplementalData) {
		if (!supplementalData.containsKey(WARNING_MAP)) {
			supplementalData.put(WARNING_MAP, new LinkedHashMap<>());
		}
		return (Map<String, Object>) supplementalData.get(WARNING_MAP);
	}
	
	private static Map<String, Object> getErrorMap(Map<String, Object> supplementalData) {
		if (!supplementalData.containsKey(ERROR_MAP)) {
			supplementalData.put(ERROR_MAP, new LinkedHashMap<>());
		}
		return (Map<String, Object>) supplementalData.get(ERROR_MAP);
	}

	public static String convertMapToFieldWarningMessages(Map<String, Object> supplementalData, String fieldName) {
		if (!supplementalData.containsKey(WARNING_MAP)) {
			return null;
		}
		Map<String, Object> warnMap = (Map<String, Object>) supplementalData.get(WARNING_MAP);
		Map<String, Object> fieldWarnMap = (Map<String, Object>) warnMap.get(fieldName);
		if (fieldWarnMap == null) {
			return null;
		}

		Map<String, Set<String>> consolidatedWarnings = new LinkedHashMap<>();
		for (Map.Entry<String, Object> fieldRowWarning : fieldWarnMap.entrySet()) {
			Map<String, String> subfieldWarnings = (Map<String, String>) fieldRowWarning.getValue();
			for (Map.Entry<String, String> subfieldWarning : subfieldWarnings.entrySet()) {
				Set<String> uniqueSubfieldWarnings = consolidatedWarnings.get(subfieldWarning.getKey());
				if (uniqueSubfieldWarnings == null) {
					uniqueSubfieldWarnings = new HashSet<>();
				}
				uniqueSubfieldWarnings.add(subfieldWarning.getValue());
				consolidatedWarnings.put(subfieldWarning.getKey(), uniqueSubfieldWarnings);
			}
		}

		List<String> consolidatedMessages = new ArrayList<>();
		for (Map.Entry<String, Set<String>> consolidatedWarning : consolidatedWarnings.entrySet()) {
			consolidatedMessages.add(consolidatedWarning.getKey() + " - " + consolidatedWarning.getValue().stream().sorted().collect(Collectors.joining("/")));
		}
		Collections.sort(consolidatedMessages);
		return String.join(" | ", consolidatedMessages);

	}
	
	public static String convertMapToFieldErrorMessages(Map<String, Object> supplementalData, String fieldName) {
		if (!supplementalData.containsKey(ERROR_MAP)) {
			return null;
		}
		Map<String, Object> errorMap = (Map<String, Object>) supplementalData.get(ERROR_MAP);
		Map<String, Object> fieldErrorMap = (Map<String, Object>) errorMap.get(fieldName);
		if (fieldErrorMap == null) {
			return null;
		}

		Map<String, Set<String>> consolidatedErrors = new LinkedHashMap<>();
		for (Map.Entry<String, Object> fieldRowError : fieldErrorMap.entrySet()) {
			Map<String, String> subfieldErrors = (Map<String, String>) fieldRowError.getValue();
			for (Map.Entry<String, String> subfieldError : subfieldErrors.entrySet()) {
				Set<String> uniqueSubfieldErrors = consolidatedErrors.get(subfieldError.getKey());
				if (uniqueSubfieldErrors == null) {
					uniqueSubfieldErrors = new HashSet<>();
				}
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
