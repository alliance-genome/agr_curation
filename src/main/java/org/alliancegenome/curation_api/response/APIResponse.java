package org.alliancegenome.curation_api.response;

import java.util.*;

import org.alliancegenome.curation_api.view.View;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class APIResponse {

    @JsonView({View.FieldsOnly.class})
    private String errorMessage;
    
    @JsonView({View.FieldsOnly.class})
    private Map<String, String> errorMessages;

    @JsonView({View.FieldsOnly.class})
    private String requestDuration;

    public void addErrorMessage(String fieldName, String errorMessage){
        if(errorMessages== null)
            errorMessages = new HashMap<>(3);
        errorMessages.put(fieldName, errorMessage);
    }

    public boolean hasErrors() {
        return StringUtils.isNotEmpty(errorMessage) || MapUtils.isNotEmpty(errorMessages);
    }

}
