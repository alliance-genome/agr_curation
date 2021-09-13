package org.alliancegenome.curation_api.model.ingest.json.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class ConditionRelationDTO extends BaseDTO {

    private String conditionRelationType ;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ExperimentalConditionDTO> conditions;
}
