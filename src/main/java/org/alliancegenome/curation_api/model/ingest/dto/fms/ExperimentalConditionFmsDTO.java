package org.alliancegenome.curation_api.model.ingest.dto.fms;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExperimentalConditionFmsDTO extends BaseDTO {

	private String conditionClassId;
	
	private String conditionStatement;
	
	private String conditionId;
	
	private String conditionQuantity;
	
	private String anatomicalOntologyId;
	
	private String geneOntologyId;
	
	@JsonProperty("NCBITaxonId")
	private String ncbiTaxonId;
	
	private String chemicalOntologyId;

}
