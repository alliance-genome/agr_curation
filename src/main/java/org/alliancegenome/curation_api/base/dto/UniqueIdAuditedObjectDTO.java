package org.alliancegenome.curation_api.base.dto;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UniqueIdAuditedObjectDTO extends AuditedObjectDTO {
    
    @JsonView({View.FieldsOnly.class})
    @JsonProperty("unique_id")
    private String uniqueId;
    
}
