package org.alliancegenome.curation_api.model.dto.json;

import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;

import java.util.List;

@Data
public class ConditionRelationDTO extends BaseDTO {

	private String conditionRelationType ;
	private List<ConditionDTO> conditions;
}
