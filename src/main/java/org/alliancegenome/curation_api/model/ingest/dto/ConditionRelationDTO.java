package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ConditionRelationDTO extends AuditedObjectDTO {

	@JsonProperty("condition_relation_type_name")
	private String conditionRelationTypeName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("condition_dtos")
	private List<ExperimentalConditionDTO> conditionDtos;

	private String handle;
	
	@JsonProperty("reference_curie")
	private String referenceCurie;

}
