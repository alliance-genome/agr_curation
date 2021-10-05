package org.alliancegenome.curation_api.response;

import java.util.*;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class APIResponse {

    @JsonView({View.FieldsOnly.class})
    private Integer statusCode;
    
    @JsonView({View.FieldsOnly.class})
    private String errorMessage = "";
    
    @JsonView({View.FieldsOnly.class})
    private Map<String, String> errorMessages;

    @JsonView({View.FieldsOnly.class})
    private String requestDuration;


}
