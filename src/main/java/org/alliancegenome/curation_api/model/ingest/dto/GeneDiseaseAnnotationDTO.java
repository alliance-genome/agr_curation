package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Setter
@Getter
public class GeneDiseaseAnnotationDTO extends DiseaseAnnotationDTO {
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("gene_curie")
	private String geneCurie;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("sgd_strain_background_curie")
	private String sgdStrainBackgroundCurie;
	
}
