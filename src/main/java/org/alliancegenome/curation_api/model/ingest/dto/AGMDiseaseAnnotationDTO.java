package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Setter
@Getter
public class AGMDiseaseAnnotationDTO extends DiseaseAnnotationDTO {
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("inferred_gene")
	private String inferredGene;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("inferred_allele")
	private String inferredAllele;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("asserted_gene")
	private String assertedGene;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("asserted_allele")
	private String assertedAllele;
	
}
