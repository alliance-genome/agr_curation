package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import lombok.Data;

@Data
public class GenomicEntityDTO extends BiologicalEntityDTO {

	private String name;
	
	private List<SynonymDTO> synonyms;
	
}
