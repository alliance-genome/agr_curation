package org.alliancegenome.curation_api.base.dto;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuditedObjectDTO {
    
    @JsonView({View.FieldsOnly.class})
    private Boolean internal = false;
    
    @JsonView({View.FieldsOnly.class})
    private Boolean obsolete = false;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("created_by")
    private String createdBy;

    @JsonView({View.FieldsOnly.class})
    @JsonProperty("modified_by")
    private String modifiedBy;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("date_created")
    private String dateCreated;
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("date_updated")
    private String dateUpdated;
    
}
