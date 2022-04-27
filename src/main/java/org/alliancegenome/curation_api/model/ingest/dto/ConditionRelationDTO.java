package org.alliancegenome.curation_api.model.ingest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

import java.util.List;

import org.alliancegenome.curation_api.view.View;

@Data
public class ConditionRelationDTO {

    @JsonProperty("condition_relation_type")
    private String conditionRelationType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ExperimentalConditionDTO> conditions;

    private String handle;
    @JsonProperty("single_reference")
    private String singleReference;

    @JsonView({View.FieldsOnly.class})
    private Boolean internal = false;
}
