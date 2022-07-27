package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.UniqueIdAuditedObjectDTO;

import com.fasterxml.jackson.annotation.*;

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
