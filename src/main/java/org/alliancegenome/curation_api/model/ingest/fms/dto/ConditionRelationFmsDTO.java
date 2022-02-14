package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class ConditionRelationFmsDTO extends BaseDTO {

    private String conditionRelationType ;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ExperimentalConditionFmsDTO> conditions;
}
