package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class BiologicalEntityDTO extends AuditedObjectDTO {

	@JsonView({View.FieldsOnly.class})
	private String curie;
	
	@JsonProperty("taxon_curie")
	private String taxonCurie;
	
}
