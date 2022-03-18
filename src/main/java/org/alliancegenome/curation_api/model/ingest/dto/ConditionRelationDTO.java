package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

import lombok.Data;
import org.alliancegenome.curation_api.model.entities.Reference;

@Data
public class ConditionRelationDTO {
    
    @JsonProperty("condition_relation_type")
    private String conditionRelationType ;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ExperimentalConditionDTO> conditions;

    private String handle;
    @JsonProperty("single_reference")
    private Reference singleReference;

}
