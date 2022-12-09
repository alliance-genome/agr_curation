package org.alliancegenome.curation_api.exceptions;

import org.alliancegenome.curation_api.config.RestDefaultObjectMapper;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Data
public class ObjectUpdateException extends Exception {

	private ObjectUpdateExceptionData data;

	public ObjectUpdateException(Object updateObject, String message) {
		data = new ObjectUpdateExceptionData(updateObject, message, null);
	}

	public ObjectUpdateException(Object updateObject, String message, StackTraceElement[] stackTraceElements) {
		data = new ObjectUpdateExceptionData(updateObject, message, stackTraceElements);
	}

	@Data
	@NoArgsConstructor
	public static class ObjectUpdateExceptionData {

		private static ObjectMapper mapper = new RestDefaultObjectMapper().getMapper();

		@JsonView({ View.FieldsOnly.class })
		private String jsonObject = null;
		@JsonView({ View.FieldsOnly.class })
		private String message = null;
		@JsonView({ View.FieldsOnly.class })
		private StackTraceElement[] stackTraceElements = null;

		public ObjectUpdateExceptionData(Object updateObject, String message, StackTraceElement[] stackTraceElements) {
			try {
				this.message = message;
				this.stackTraceElements = stackTraceElements;
				this.jsonObject = mapper.writeValueAsString(updateObject);
			} catch (JsonProcessingException e) {
				this.message = e.getMessage();
				this.jsonObject = "{}";
			}
		}

	}
}