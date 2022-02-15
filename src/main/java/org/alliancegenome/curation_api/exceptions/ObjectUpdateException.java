package org.alliancegenome.curation_api.exceptions;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class ObjectUpdateException extends Exception {

    @Inject ObjectMapper mapper;
    private String message = null;
    private String jsonObject = null;
    
    public ObjectUpdateException(Object updateObject, String message) {
        try {
            this.message = message;
            log.warn(message);
            jsonObject = mapper.writeValueAsString(updateObject);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            this.message = e.getMessage();
            jsonObject = "{}";
        }
    }

}