package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Setter
@Getter
public class AlleleDiseaseAnnotationDTO extends DiseaseAnnotationDTO {
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("inferred_gene")
	private String inferredGene;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("asserted_gene")
	private String assertedGene;
	
}
