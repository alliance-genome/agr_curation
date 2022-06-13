package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.dto.UniqueIdAuditedObjectDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ConditionRelationDTO extends UniqueIdAuditedObjectDTO {

	@JsonProperty("condition_relation_type")
	private String conditionRelationType;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<ExperimentalConditionDTO> conditions;

	private String handle;
	
	@JsonProperty("single_reference")
	private String singleReference;

}
