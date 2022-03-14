package org.alliancegenome.curation_api.exceptions;

import org.alliancegenome.curation_api.config.RestDefaultObjectMapper;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.*;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Data
public class ObjectUpdateException extends Exception {

    private ObjectUpdateExceptionData data;

    public ObjectUpdateException(Object updateObject, String message) {
        data = new ObjectUpdateExceptionData(updateObject, message);
    }
    
    @Data
    @NoArgsConstructor
    public static class ObjectUpdateExceptionData {
        
        private static ObjectMapper mapper = new RestDefaultObjectMapper().getMapper();
        
        @JsonView({View.FieldsOnly.class})
        private String jsonObject = null;
        @JsonView({View.FieldsOnly.class})
        private String message = null;
        
        public ObjectUpdateExceptionData(Object updateObject, String message) {
            try {
                this.message = message;
                this.jsonObject = mapper.writeValueAsString(updateObject);
            } catch (JsonProcessingException e) {
                this.message = e.getMessage();
                this.jsonObject = "{}";
            }
        }
        
    }
}