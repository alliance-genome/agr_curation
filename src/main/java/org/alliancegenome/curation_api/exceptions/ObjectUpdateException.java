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

    private static ObjectMapper mapper = new RestDefaultObjectMapper().getMapper();
    
    @JsonView({View.FieldsOnly.class})
    private String message = null;
    
    @JsonView({View.FieldsOnly.class})
    private String jsonObject = null;
    
    public ObjectUpdateException(Object updateObject, String message) {
        try {
            this.message = message;
            //log.warn(message);
            jsonObject = mapper.writeValueAsString(updateObject);
        } catch (JsonProcessingException e) {
            //log.error(e.getMessage());
            this.message = e.getMessage();
            jsonObject = "{}";
        }
    }

}