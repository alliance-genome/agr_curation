package org.alliancegenome.curation_api.model.dto.json;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class BasicGeneticEntityDTO extends BaseDTO {
	private String primaryId;
	private String taxonId;
	private List<String> synonyms;
	private List<String> secondaryIds;
	private List<CrossReferenceDTO> crossReferences;
	private List<GenomeLocationsDTO> genomeLocations;
	
}
