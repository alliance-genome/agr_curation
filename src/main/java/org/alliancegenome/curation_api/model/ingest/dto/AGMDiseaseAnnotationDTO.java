package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AGMDiseaseAnnotationDTO extends DiseaseAnnotationDTO {
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("inferred_gene")
	private String inferredGene;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("inferred_allele")
	private String inferredAllele;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("asserted_genes")
	private List<String> assertedGenes;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("asserted_allele")
	private String assertedAllele;
	
}
