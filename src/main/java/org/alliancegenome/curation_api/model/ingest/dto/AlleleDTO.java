package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class AlleleDTO extends GenomicEntityDTO {

	@JsonView({View.FieldsOnly.class})
	private String symbol;
	
	@JsonView({View.FieldsAndLists.class})
	private List<String> references;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("inheritance_mode")
	private String inheritanceMode;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("in_collection")
	private String inCollection;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("sequencing_status")
	private String sequencingStatus;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("is_extinct")
	private Boolean isExtinct = false;
	
	@JsonView({View.FieldsAndLists.class})
	private List<SynonymDTO> synonyms;
}
