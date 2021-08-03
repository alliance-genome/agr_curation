package org.alliancegenome.curation_api.model.dto.json;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class GeneDTO extends BaseDTO {
	private BasicGeneticEntityDTO basicGeneticEntity;
	private String name;
	private String symbol;
	private String geneSynopsis;
	private String geneSynopsisUrl;
	private String soTermId;
}
