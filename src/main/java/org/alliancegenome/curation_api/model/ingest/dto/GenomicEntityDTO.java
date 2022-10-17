package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class GenomicEntityDTO extends BiologicalEntityDTO {

	@JsonView({View.FieldsOnly.class})
	private String name;
	
	@JsonView({View.FieldsAndLists.class})
	private List<SynonymDTO> synonyms;
	
}
