package org.alliancegenome.curation_api.model.dto.json;

import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;

import java.util.List;

@Data
public class ConditionDTO extends BaseDTO {

	private String conditionStatement ;
	private String conditionClassId ;
	private String chemicalOntologyId ;
	private String anatomicalOntologyId ;
	private String ncbitaxonId ;
}
