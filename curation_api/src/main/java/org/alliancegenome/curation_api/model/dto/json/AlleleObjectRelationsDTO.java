package org.alliancegenome.curation_api.model.dto.json;

import java.util.Map;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class AlleleObjectRelationsDTO extends BaseDTO {

	private Map<String, String> objectRelation;
}
